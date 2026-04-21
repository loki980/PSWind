import L from 'leaflet';
import JSZip from 'jszip';
import { kml as kmlToGeoJson } from '@tmcw/togeojson';

// The long-lived Google My Maps KML from the original Android app
// (see PSWind-android/src/com/lokico/PSWind/NWKiteMap.java).
const KML_URL = '/api/kml';

let kiteLayer = null;

export async function loadKiteSpots(map) {
  if (kiteLayer) return kiteLayer;

  try {
    const res = await fetch(KML_URL);
    if (!res.ok) throw new Error(`HTTP ${res.status}`);

    // Google's `output=kml` sometimes returns raw KML, sometimes a KMZ zip.
    // Detect by magic bytes and unzip if needed.
    const buf = await res.arrayBuffer();
    let xmlString;
    if (isZip(buf)) {
      const zip = await JSZip.loadAsync(buf);
      const kmlFile =
        zip.file(/doc\.kml$/i)?.[0] ??
        Object.values(zip.files).find((f) => /\.kml$/i.test(f.name));
      if (!kmlFile) throw new Error('KMZ has no .kml inside');
      xmlString = await kmlFile.async('string');
    } else {
      xmlString = new TextDecoder().decode(buf);
    }

    const doc = new DOMParser().parseFromString(xmlString, 'text/xml');
    const geo = kmlToGeoJson(doc);

    kiteLayer = L.geoJSON(geo, {
      pointToLayer: (feature, latlng) =>
        L.marker(latlng, {
          icon: L.divIcon({
            className: 'kite-spot',
            html: '<div class="kite-spot-dot"></div>',
            iconSize: [14, 14],
            iconAnchor: [7, 7],
          }),
        }),
      onEachFeature: (feature, layer) => {
        const name = feature.properties?.name ?? 'Spot';
        const raw = feature.properties?.description;
        // togeojson v7 wraps CDATA as { "@type": "html", value: "..." }
        const desc = typeof raw === 'string' ? raw
          : (raw?.['@type'] === 'html' ? raw.value ?? '' : '');
        layer.bindPopup(
          `<div class="popup-title">${escapeHtml(name)}</div>${
            desc ? `<div class="popup-meta">${desc}</div>` : ''
          }`,
        );
      },
    });

    return kiteLayer;
  } catch (e) {
    console.warn('kite-spot load failed', e);
    return null;
  }
}

function isZip(buf) {
  const view = new Uint8Array(buf, 0, 4);
  return view[0] === 0x50 && view[1] === 0x4b; // "PK"
}

function escapeHtml(s) {
  return String(s).replace(/[&<>"']/g, (c) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' })[c]);
}

function stripHtml(s) {
  // My Maps descriptions often contain raw HTML; let it render.
  // If this ends up being a problem (untrusted), swap to a sanitizer.
  return s;
}
