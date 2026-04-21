// Minimal service worker: cache the app shell so the UI opens offline.
// Sensor / forecast data always goes to the network (no stale wind!).

const SHELL = 'pswind-shell-v1';
const TILES = 'pswind-tiles-v1';
const SHELL_FILES = ['/', '/index.html', '/manifest.webmanifest'];

self.addEventListener('install', (e) => {
  e.waitUntil(caches.open(SHELL).then((c) => c.addAll(SHELL_FILES)));
  self.skipWaiting();
});

self.addEventListener('activate', (e) => {
  e.waitUntil(
    caches
      .keys()
      .then((keys) =>
        Promise.all(
          keys
            .filter((k) => k !== SHELL && k !== TILES)
            .map((k) => caches.delete(k)),
        ),
      ),
  );
  self.clients.claim();
});

self.addEventListener('fetch', (event) => {
  const req = event.request;
  if (req.method !== 'GET') return;
  const url = new URL(req.url);

  // Never cache live sensor / forecast / KML.
  if (
    url.pathname.startsWith('/api/') ||
    url.hostname === 'api.weather.gov' ||
    url.hostname === 'maps.google.com' ||
    url.hostname === 'wxtofly.net'
  ) {
    return; // default network behavior
  }

  // OSM tiles: cache-first, long-lived.
  if (url.hostname.endsWith('tile.openstreetmap.org')) {
    event.respondWith(cacheFirst(req, TILES, 7 * 24 * 3600));
    return;
  }

  // App shell: cache-first.
  if (url.origin === location.origin) {
    event.respondWith(cacheFirst(req, SHELL, 24 * 3600));
  }
});

async function cacheFirst(req, cacheName, maxAgeSec) {
  const cache = await caches.open(cacheName);
  const hit = await cache.match(req);
  if (hit) return hit;
  try {
    const res = await fetch(req);
    if (res.ok) cache.put(req, res.clone());
    return res;
  } catch (e) {
    return hit || Response.error();
  }
}
