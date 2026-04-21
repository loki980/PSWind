# PSWind Web

Static PWA revival of the original PSWind Android app. Pacific-Northwest-focused
live wind sensors, kite spots, NOAA marine forecast, and wxtofly RASP loops for
the Seattle & North Sound TJs.

```
PSWind-web/
├── web/      # Vite static site (Leaflet + vanilla JS)
└── worker/   # Cloudflare Worker: Synoptic proxy + cache
```

## Dev

```fish
# one-time
cd web && npm install
cd ../worker && npm install

# two terminals
cd worker && npm run dev           # http://127.0.0.1:8787
cd web    && npm run dev           # http://127.0.0.1:5173  (proxies /api → worker)
```

No API keys required — all data sources are public NOAA endpoints.

Tabs: **Map** (live wind + kite spots), **TJ Seattle**, **TJ N. Sound**, **Forecast**.

## Deploy

Both halves deploy to Cloudflare's free tier.

```fish
# Worker (API)
cd worker
wrangler deploy                          # publishes pswind-api.<account>.workers.dev

# Static site
cd ../web
npm run build
wrangler pages deploy dist --project-name pswind
```

In the Cloudflare dashboard for the Pages project, add a **Worker binding**
or a **route** so `https://<your-pages>/api/*` forwards to the `pswind-api`
Worker. Alternatively, point `wrangler.toml`'s `[[routes]]` block at a real
custom domain.

## Data sources

| Feature                 | Source                                       | Notes                                          |
|-------------------------|----------------------------------------------|------------------------------------------------|
| Wind — airports         | `aviationweather.gov/api/data/metar`         | No key, bbox query, ~5 min updates             |
| Wind — buoys            | `ndbc.noaa.gov/data/latest_obs/latest_obs.txt` | No key, global text file, filtered in Worker |
| Kite spots              | Google My Maps KML (original URL)            | Still live from the Android app                |
| Marine forecast         | `api.weather.gov` zones/forecast             | No key, CORS-friendly                          |
| TJ Seattle / N. Sound   | `wxtofly.net` RASP PNG loop                  | 2×/day refresh, 18 hourly frames               |
| Basemap                 | OpenStreetMap tiles                          | No key, attribution required                   |

## Open items

- App icons (see `web/public/icons/README.md`)
- Custom domain
- Additional free sensor sources if coverage is thin:
  - **aprs.fi** (CWOP personal stations, free API key) — best for non-airport/non-buoy spots
  - **api.weather.gov/stations/{id}/observations/latest** (per-station, cacheable)
- Historical graphs — need to pick a free timeseries source (NWS obs history is free)
- Optional region picker in the UI (currently auto-detects from map center)
