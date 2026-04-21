// PSWind API — Cloudflare Worker.
//
// Aggregates two free, no-key NOAA sources into a single JSON feed:
//
//   1. NOAA Aviation Weather METAR API — airport (ASOS/AWOS) observations
//      https://aviationweather.gov/api/data/metar?bbox=S,W,N,E&format=json&hours=1
//
//   2. NDBC latest_obs.txt — marine buoy observations (global, filtered here)
//      https://www.ndbc.noaa.gov/data/latest_obs/latest_obs.txt
//
// Both upstream endpoints are CORS-less plain-text/JSON, so a proxy is required
// from the browser. We also cache for ~5 min via the Cache API to smooth out
// bursts and be a good citizen.

const REGION_BBOX = {
  // [west, south, east, north]
  nw: [-125.5, 41.9, -116.4, 51.0],
  ca: [-124.5, 32.4, -114.1, 42.1],
  mx: [-117.5, 14.5, -86.6, 32.7],
  hi: [-160.3, 18.9, -154.7, 22.3],
  tx: [-106.7, 25.8, -93.5, 36.6],
  mt: [-116.1, 44.3, -104.0, 49.1],
  gl: [-92.9, 41.1, -76.2, 49.0],
  la: [-94.1, 28.9, -88.8, 33.0],
  ma: [-73.6, 41.1, -69.9, 42.9],
  ny: [-79.8, 40.5, -71.8, 45.1],
  nj: [-75.6, 38.4, -73.8, 41.4],
  cb: [-83.7, 36.5, -75.0, 39.8],
  nc: [-84.4, 33.7, -75.4, 36.6],
  gc: [-85.7, 30.3, -78.5, 35.3],
  fl: [-87.7, 24.3, -79.9, 31.1],
};

const KNOTS_TO_MPH = 1.15077945;
const MS_TO_MPH = 2.23693629;

const NDBC_NAMES = {
  '46088': 'New Dungeness',
  '46120': 'North Puget Sound',
  '46123': 'South Puget Sound',
  '46125': 'Hood Canal Entrance',
  'BMTW1': 'Blake Island',
  'CHYW1': 'Birch Point',
  'CPMW1': 'Birch Point S',
  'FRDW1': 'Friday Harbor',
  'PBFW1': 'Padilla Bay',
  'PTAW1': 'Port Angeles',
  'PTWW1': 'Point Wilson',
  'SISW1': 'Smith Island',
  'TCMW1': 'Tacoma Narrows',
  'WPOW1': 'West Point Seattle',
};

export default {
  async fetch(request, env, ctx) {
    const url = new URL(request.url);

    if (request.method === 'OPTIONS') return cors(new Response(null, { status: 204 }));
    if (request.method !== 'GET') return cors(new Response('Method not allowed', { status: 405 }));

    if (url.pathname === '/api/wind') return cors(await handleWind(url, env, ctx));
    if (url.pathname === '/api/tj') return cors(await handleTj(url));
    if (url.pathname === '/api/kml') return cors(await handleKml());
    if (url.pathname === '/api/health') return cors(new Response('ok'));
    return cors(new Response('Not found', { status: 404 }));
  },
};

async function handleWind(url, env, ctx) {
  const region = (url.searchParams.get('region') || 'nw').toLowerCase();
  const bbox = REGION_BBOX[region];
  if (!bbox) return jsonResponse({ error: 'unknown region' }, 400);

  const ttlSec = Number(env.DEFAULT_CACHE_TTL_SEC ?? 300);
  const cacheKey = new Request(`https://pswind-cache.invalid/wind?region=${region}`);
  const cache = caches.default;

  const cached = await cache.match(cacheKey);
  if (cached) return withCacheHeaders(cached.clone(), 'HIT', ttlSec);

  const [metarStations, buoyStations, wsdotStations] = await Promise.all([
    fetchMetar(bbox).catch((e) => (console.warn('metar', e), [])),
    fetchBuoys(bbox).catch((e) => (console.warn('buoys', e), [])),
    fetchWsdot(bbox, region).catch((e) => (console.warn('wsdot', e), [])),
  ]);

  const stations = [...metarStations, ...buoyStations, ...wsdotStations];
  const payload = { region, count: stations.length, stations };

  const res = new Response(JSON.stringify(payload), {
    status: 200,
    headers: {
      'content-type': 'application/json',
      'cache-control': `public, max-age=${ttlSec}`,
    },
  });

  ctx.waitUntil(cache.put(cacheKey, res.clone()));
  return withCacheHeaders(res, 'MISS', ttlSec);
}

// --- WSDOT road weather stations -------------------------------------------
// No API key required — open AJAX endpoint used by wsdot.com/travel/real-time
// Curated lists keep only waterfront/kite-relevant stations per region.

const CARDINAL = {
  N:0,NNE:22.5,NE:45,ENE:67.5,E:90,ESE:112.5,SE:135,SSE:157.5,
  S:180,SSW:202.5,SW:225,WSW:247.5,W:270,WNW:292.5,NW:315,NNW:337.5,
};

// Station IDs from wsdot.com/Travel/Real-time/Service/api/Weather/GetWeatherStationForecast
// Selected for proximity to water / kiting areas. Add IDs as needed.
const WSDOT_CURATED = {
  nw: [
    4022,   // Yarrow Point (Lake Washington / 520 bridge)
    4218,   // Everett waterfront
    1873,   // Everett Snohomish County Airport area
    3063,   // Hansville (Hood Canal entrance / Double Bluff)
    5993,   // Freeland (Whidbey Island south)
    3906,   // Port Townsend (Admiralty Inlet)
    4265,   // Port Townsend S
    4023,   // Sequim Bay
    1870,   // Whidbey Island NAS (Oak Harbor area)
    3952,   // Kingston
    470,    // Bremerton
  ],
};

async function fetchWsdot([w, s, e, n], region) {
  const stationIds = new Set(WSDOT_CURATED[region] ?? []);
  if (!stationIds.size) return [];

  const r = await fetch(
    'https://wsdot.com/Travel/Real-time/Service/api/Weather/GetWeatherStationForecast',
    { cf: { cacheTtl: 180, cacheEverything: true } },
  );
  if (!r.ok) throw new Error(`wsdot ${r.status}`);
  const arr = await r.json();

  return arr
    .filter((st) => stationIds.has(st.weatherStationId))
    .map((st) => {
      const mph = parseFloat(st.windSpeed);
      const dir = CARDINAL[st.windDirection] ?? null;
      if (!isFinite(mph) && dir == null) return null;
      return {
        id: `WSDOT-${st.weatherStationId}`,
        name: st.weatherStationDescription,
        lat: st.latitude,
        lon: st.longitude,
        wind_mph: isFinite(mph) ? mph : null,
        gust_mph: null,
        dir_deg: dir,
        t: st.weatherReportDateTime || null,
        source: 'wsdot',
      };
    })
    .filter(Boolean);
}

// --- TJ loop proxy ---------------------------------------------------------

async function handleTj(url) {
  const grid = url.searchParams.get('grid') || 'd2';
  const hour = url.searchParams.get('hour') || '1200';
  if (!/^[a-z0-9]+$/i.test(grid) || !/^\d{4}$/.test(hour)) {
    return new Response('Bad request', { status: 400 });
  }
  // Use HTTP to avoid wxtofly.net's invalid SSL cert
  const imgUrl = `http://wxtofly.net/RASP/PNW/FCST/tenmwind.curr.${hour}lst.${grid}.png`;
  try {
    const r = await fetch(imgUrl, { cf: { cacheTtl: 3600, cacheEverything: true } });
    if (!r.ok) return new Response('', { status: r.status });
    return new Response(r.body, {
      status: 200,
      headers: {
        'content-type': r.headers.get('content-type') || 'image/png',
        'cache-control': 'public, max-age=3600',
      },
    });
  } catch (e) {
    return new Response('upstream error', { status: 502 });
  }
}

// --- KML proxy -------------------------------------------------------------

const KML_URL =
  'https://maps.google.com/maps/ms?ie=UTF8&authuser=0&msa=0&output=kml' +
  '&msid=214913314730054445951.0000011206947d3332bf5';

async function handleKml() {
  try {
    const r = await fetch(KML_URL, { cf: { cacheTtl: 86400, cacheEverything: true } });
    if (!r.ok) return new Response('upstream error', { status: r.status });
    return new Response(r.body, {
      status: 200,
      headers: {
        'content-type': r.headers.get('content-type') || 'application/vnd.google-earth.kml+xml',
        'cache-control': 'public, max-age=86400',
      },
    });
  } catch (e) {
    return new Response('upstream error', { status: 502 });
  }
}

// --- Sources ---------------------------------------------------------------

async function fetchMetar([w, s, e, n]) {
  const u = new URL('https://aviationweather.gov/api/data/metar');
  u.searchParams.set('bbox', `${s},${w},${n},${e}`); // S,W,N,E
  u.searchParams.set('format', 'json');
  u.searchParams.set('hours', '1');

  const r = await fetch(u.toString(), {
    cf: { cacheTtl: 180, cacheEverything: true },
    headers: { 'user-agent': 'pswind (github.com/yourrepo)' },
  });
  if (!r.ok) throw new Error(`metar ${r.status}`);
  const arr = await r.json();
  if (!Array.isArray(arr)) return [];

  return arr
    .map((m) => {
      if (typeof m.lat !== 'number' || typeof m.lon !== 'number') return null;
      const wspdKt = toNum(m.wspd);
      const gstKt = toNum(m.wgst);
      const dir = parseMetarDir(m.wdir);
      if (wspdKt == null && gstKt == null && dir == null) return null;
      return {
        id: m.icaoId,
        name: m.name || m.icaoId,
        lat: m.lat,
        lon: m.lon,
        wind_mph: wspdKt != null ? +(wspdKt * KNOTS_TO_MPH).toFixed(1) : null,
        gust_mph: gstKt != null ? +(gstKt * KNOTS_TO_MPH).toFixed(1) : null,
        dir_deg: dir,
        t: m.reportTime || null,
        source: 'metar',
      };
    })
    .filter(Boolean);
}

async function fetchBuoys([w, s, e, n]) {
  const r = await fetch(
    'https://www.ndbc.noaa.gov/data/latest_obs/latest_obs.txt',
    {
      cf: { cacheTtl: 180, cacheEverything: true },
      headers: { 'user-agent': 'pswind (github.com/yourrepo)' },
    },
  );
  if (!r.ok) throw new Error(`ndbc ${r.status}`);
  const text = await r.text();

  const out = [];
  const lines = text.split('\n');
  for (const line of lines) {
    if (!line || line.startsWith('#')) continue;
    const cols = line.split(/\s+/);
    // Fields: STN LAT LON YYYY MM DD hh mm WDIR WSPD GST ...
    if (cols.length < 11) continue;
    const lat = parseFloat(cols[1]);
    const lon = parseFloat(cols[2]);
    if (!isFinite(lat) || !isFinite(lon)) continue;
    if (lat < s || lat > n || lon < w || lon > e) continue;

    const dir = toNum(cols[8]);
    const wspdMs = toNum(cols[9]);
    const gstMs = toNum(cols[10]);
    if (wspdMs == null && gstMs == null && dir == null) continue;

    const [yy, mo, dd, hh, mm] = cols.slice(3, 8).map(toNum);
    const t =
      [yy, mo, dd, hh, mm].every((v) => v != null)
        ? `${yy}-${pad(mo)}-${pad(dd)}T${pad(hh)}:${pad(mm)}:00Z`
        : null;

    out.push({
      id: `NDBC-${cols[0]}`,
      name: NDBC_NAMES[cols[0]] ?? `Buoy ${cols[0]}`,
      lat,
      lon,
      wind_mph: wspdMs != null ? +(wspdMs * MS_TO_MPH).toFixed(1) : null,
      gust_mph: gstMs != null ? +(gstMs * MS_TO_MPH).toFixed(1) : null,
      dir_deg: dir,
      t,
      source: 'ndbc',
    });
  }
  return out;
}

// --- Parsing helpers -------------------------------------------------------

function parseMetarDir(v) {
  if (v == null) return null;
  if (typeof v === 'number') return isFinite(v) ? v : null;
  if (v === 'VRB' || v === '') return null;
  const n = parseFloat(v);
  return isFinite(n) ? n : null;
}

function toNum(v) {
  if (v == null) return null;
  if (v === 'MM' || v === '' || v === 'VRB') return null;
  const n = typeof v === 'number' ? v : parseFloat(v);
  return isFinite(n) ? n : null;
}

const pad = (n) => String(n).padStart(2, '0');

// --- Response helpers ------------------------------------------------------

function jsonResponse(obj, status = 200) {
  return new Response(JSON.stringify(obj), {
    status,
    headers: { 'content-type': 'application/json' },
  });
}

function withCacheHeaders(res, status, ttlSec) {
  const out = new Response(res.body, res);
  out.headers.set('x-cache', status);
  out.headers.set('cache-control', `public, max-age=${ttlSec}`);
  return out;
}

function cors(res) {
  const out = new Response(res.body, res);
  out.headers.set('access-control-allow-origin', '*');
  out.headers.set('access-control-allow-methods', 'GET, OPTIONS');
  out.headers.set('access-control-max-age', '86400');
  return out;
}
