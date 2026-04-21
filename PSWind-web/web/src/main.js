import 'leaflet/dist/leaflet.css';
import './styles.css';

import { initMap } from './map.js';
import { initForecast } from './noaa.js';
import { initTjLoop } from './tj-loop.js';

// --- Tab routing ---
const tabs = document.querySelectorAll('#tabs button');
const views = document.querySelectorAll('.view');

function showView(name) {
  tabs.forEach((t) => t.classList.toggle('active', t.dataset.view === name));
  views.forEach((v) => {
    const match = v.id === `view-${name}`;
    v.classList.toggle('active', match);
    v.hidden = !match;
  });
  // Leaflet needs a size recompute after its container becomes visible.
  if (name === 'map' && window.__pswindMap) {
    setTimeout(() => window.__pswindMap.invalidateSize(), 0);
  }
}

tabs.forEach((t) => t.addEventListener('click', () => showView(t.dataset.view)));

// --- Init modules ---
window.__pswindMap = initMap(document.getElementById('map'));

document
  .querySelectorAll('.tj-loop')
  .forEach((el) => initTjLoop(el, el.dataset.grid));

// Lazy-load forecast the first time its tab is opened.
let forecastLoaded = false;
document.querySelector('[data-view="forecast"]').addEventListener(
  'click',
  () => {
    if (!forecastLoaded) {
      forecastLoaded = true;
      initForecast(document.getElementById('forecast-content'));
    }
  },
  { once: false },
);

// --- Service worker (PWA) ---
if ('serviceWorker' in navigator && import.meta.env.PROD) {
  window.addEventListener('load', () => {
    navigator.serviceWorker.register('/sw.js').catch(() => {});
  });
}
