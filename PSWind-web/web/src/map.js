import L from 'leaflet';
import { DEFAULT_REGION, regionForLatLon } from './regions.js';
import { loadRegion, ensureLayer } from './sensors.js';
import { loadKiteSpots } from './kites.js';

const PUGET_SOUND = [47.612, -122.358]; // default center (from NWKiteMap.java)

export function initMap(container) {
  const map = L.map(container, {
    center: PUGET_SOUND,
    zoom: 9,
    zoomControl: true,
    tap: true,
  });

  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; OpenStreetMap',
    maxZoom: 19,
  }).addTo(map);

  const statusEl = document.getElementById('map-status');
  ensureLayer(map);

  // First load: default region.
  let currentRegion = DEFAULT_REGION;
  loadRegion(map, currentRegion, { statusEl });

  // Periodic refresh.
  setInterval(() => {
    loadRegion(map, currentRegion, { statusEl, force: true });
  }, 5 * 60 * 1000);

  // Kite spots overlay — load once, then let the user toggle via layer control.
  loadKiteSpots(map).then((layer) => {
    if (!layer) return;
    L.control
      .layers(null, { 'Kite spots': layer }, { position: 'topright' })
      .addTo(map);
    layer.addTo(map); // visible by default
  });

  // Region auto-switch on significant pan.
  let lastRegionCheck = { lat: PUGET_SOUND[0], lon: PUGET_SOUND[1] };
  map.on('moveend', async () => {
    const c = map.getCenter();
    if (haversine(c.lat, c.lng, lastRegionCheck.lat, lastRegionCheck.lon) < 50) {
      return; // < 50 km pan, skip
    }
    lastRegionCheck = { lat: c.lat, lon: c.lng };
    const r = await regionForLatLon(c.lat, c.lng);
    if (r && r !== currentRegion) {
      currentRegion = r;
      loadRegion(map, currentRegion, { statusEl });
    }
  });

  // "My location" button (top-right, below layer control).
  const locBtn = L.control({ position: 'topright' });
  locBtn.onAdd = () => {
    const div = L.DomUtil.create('div', 'leaflet-bar');
    div.innerHTML =
      '<a href="#" title="My location" aria-label="My location" ' +
      'style="font:16px/26px system-ui;text-align:center;width:26px;height:26px;">📍</a>';
    L.DomEvent.on(div, 'click', (e) => {
      L.DomEvent.preventDefault(e);
      if (!navigator.geolocation) return;
      navigator.geolocation.getCurrentPosition(
        (pos) => {
          const { latitude, longitude } = pos.coords;
          map.setView([latitude, longitude], 11);
        },
        () => {},
        { enableHighAccuracy: false, timeout: 5000, maximumAge: 60000 },
      );
    });
    return div;
  };
  locBtn.addTo(map);

  return map;
}

function haversine(lat1, lon1, lat2, lon2) {
  const R = 6371;
  const dLat = toRad(lat2 - lat1);
  const dLon = toRad(lon2 - lon1);
  const a =
    Math.sin(dLat / 2) ** 2 +
    Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) * Math.sin(dLon / 2) ** 2;
  return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
}
const toRad = (d) => (d * Math.PI) / 180;
