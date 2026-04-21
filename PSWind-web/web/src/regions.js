// State / province / country → Synoptic region code.
// Ported from Omnimap.java `createStateHashMap()`.
export const stateToRegion = {
  // Pacific Northwest
  'British Columbia': 'nw',
  Washington: 'nw',
  Oregon: 'nw',
  // California
  California: 'ca',
  // Mexico (country-level)
  Mexico: 'mx',
  // Hawaii
  Hawaii: 'hi',
  // Texas
  Texas: 'tx',
  // Montana
  Montana: 'mt',
  // Great Lakes
  Illinois: 'gl',
  Wisconsin: 'gl',
  Indiana: 'gl',
  Michigan: 'gl',
  Ohio: 'gl',
  Ontario: 'gl',
  Minnesota: 'gl',
  // Louisiana
  Louisiana: 'la',
  // Massachusetts
  Massachusetts: 'ma',
  'Rhode Island': 'ma',
  // New York
  'New York': 'ny',
  // New Jersey
  'New Jersey': 'nj',
  Delaware: 'nj',
  // Chesapeake Bay
  Maryland: 'cb',
  Virginia: 'cb',
  // North Carolina
  'North Carolina': 'nc',
  // Gulf Coast (SC/GA)
  'South Carolina': 'gc',
  Georgia: 'gc',
  // Florida
  Florida: 'fl',
};

// Approximate bounding boxes for each region: [west, south, east, north].
// Used by the Worker when querying Synoptic `stations/latest?bbox=...`.
// Bounds are generous so we don't miss edge sensors; they don't need to be tight.
export const regionBboxes = {
  nw: [-125.5, 41.9, -116.4, 51.0], // BC, WA, OR
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

export const DEFAULT_REGION = 'nw';

/**
 * Resolve a latitude/longitude to a region code by asking NOAA's points API,
 * which returns the U.S. state. For BC (Canada) we cheat and infer from the
 * bounding box — NOAA doesn't cover it.
 */
export async function regionForLatLon(lat, lon) {
  // Rough check: is this British Columbia? NOAA won't answer for Canada.
  if (lat >= 48.3 && lat <= 60 && lon >= -139 && lon <= -114) {
    return 'nw';
  }
  try {
    const r = await fetch(
      `https://api.weather.gov/points/${lat.toFixed(3)},${lon.toFixed(3)}`,
      { headers: { Accept: 'application/geo+json' } },
    );
    if (!r.ok) return null;
    const j = await r.json();
    const state = j?.properties?.relativeLocation?.properties?.state;
    // NOAA returns 2-letter state codes; expand a few we care about.
    const stateNames = {
      WA: 'Washington',
      OR: 'Oregon',
      CA: 'California',
      TX: 'Texas',
      HI: 'Hawaii',
      MT: 'Montana',
      IL: 'Illinois',
      WI: 'Wisconsin',
      IN: 'Indiana',
      MI: 'Michigan',
      OH: 'Ohio',
      MN: 'Minnesota',
      LA: 'Louisiana',
      MA: 'Massachusetts',
      RI: 'Rhode Island',
      NY: 'New York',
      NJ: 'New Jersey',
      DE: 'Delaware',
      MD: 'Maryland',
      VA: 'Virginia',
      NC: 'North Carolina',
      SC: 'South Carolina',
      GA: 'Georgia',
      FL: 'Florida',
    };
    return stateToRegion[stateNames[state] ?? state] ?? null;
  } catch {
    return null;
  }
}
