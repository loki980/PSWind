import L from 'leaflet';
import { windBarbSvg as windMarkerHtml, WIND_BARB_SIZE as WIND_MARKER_SIZE } from './wind-barb.js';

let layerGroup = null;
let currentRegion = null;
let inflight = null;

export function ensureLayer(map) {
  if (!layerGroup) {
    layerGroup = L.layerGroup().addTo(map);
  }
  return layerGroup;
}

/**
 * Fetch /api/wind?region=<region> and render markers on the map.
 */
export async function loadRegion(map, region, { statusEl } = {}) {
  if (!region || region === currentRegion) return;
  currentRegion = region;

  if (inflight) inflight.abort?.();
  const ctrl = new AbortController();
  inflight = ctrl;

  setStatus(statusEl, `Loading ${region.toUpperCase()} sensors…`);

  try {
    const r = await fetch(`/api/wind?region=${encodeURIComponent(region)}`, {
      signal: ctrl.signal,
    });
    if (!r.ok) throw new Error(`HTTP ${r.status}`);
    const data = await r.json();
    renderStations(map, data.stations ?? []);
    const n = data.stations?.length ?? 0;
    setStatus(statusEl, `${n} sensor${n === 1 ? '' : 's'}`, 1500);
  } catch (e) {
    if (e.name === 'AbortError') return;
    console.warn('sensor fetch failed', e);
    setStatus(statusEl, 'Sensor fetch failed. Retrying soon…', 3000);
  }
}

function renderStations(map, stations) {
  const group = ensureLayer(map);
  group.clearLayers();
  for (const s of stations) {
    if (typeof s.lat !== 'number' || typeof s.lon !== 'number') continue;
    const html = windMarkerHtml(s.wind_mph, s.dir_deg, s.t);
    const icon = L.divIcon({
      className: 'wind-marker',
      html,
      iconSize: [WIND_MARKER_SIZE, WIND_MARKER_SIZE],
      iconAnchor: [WIND_MARKER_SIZE / 2, WIND_MARKER_SIZE / 2],
    });
    L.marker([s.lat, s.lon], { icon })
      .addTo(group)
      .bindPopup(() => popupHtml(s));
  }
}

function popupHtml(s) {
  const age = s.t ? agoLabel(s.t) : '';
  const wind = s.wind_mph == null ? 'n/a' : `${Math.round(s.wind_mph)} mph`;
  const gust = s.gust_mph == null ? '' : ` · gust ${Math.round(s.gust_mph)}`;
  const dir = s.dir_deg == null ? '' : ` · ${cardinal(s.dir_deg)} (${Math.round(s.dir_deg)}°)`;
  return `
    <div class="popup-title">${escapeHtml(s.name || s.id)}</div>
    <div class="popup-wind">${wind}${gust}</div>
    <div class="popup-meta">${dir}${age ? ' · ' + age : ''}</div>
  `;
}

function cardinal(deg) {
  const dirs = ['N', 'NNE', 'NE', 'ENE', 'E', 'ESE', 'SE', 'SSE', 'S', 'SSW', 'SW', 'WSW', 'W', 'WNW', 'NW', 'NNW'];
  return dirs[Math.round(((deg % 360) / 22.5)) % 16];
}

function agoLabel(t) {
  const ms = Date.now() - new Date(t).getTime();
  if (!isFinite(ms)) return '';
  const min = Math.round(ms / 60000);
  if (min < 1) return 'just now';
  if (min < 60) return `${min} min ago`;
  const hr = Math.round(min / 60);
  return `${hr} hr ago`;
}

function escapeHtml(s) {
  return String(s).replace(/[&<>"']/g, (c) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' })[c]);
}

let statusTimer;
function setStatus(el, msg, hideAfterMs) {
  if (!el) return;
  el.textContent = msg;
  el.classList.add('visible');
  clearTimeout(statusTimer);
  if (hideAfterMs) {
    statusTimer = setTimeout(() => el.classList.remove('visible'), hideAfterMs);
  }
}
