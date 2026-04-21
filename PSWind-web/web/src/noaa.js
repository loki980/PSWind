// NOAA Coastal Waters Forecast (CWF) text product from Seattle WFO (SEW).
// Replaces the old nwwind.net scrape. The CWF covers all inland/coastal WA
// waters including Puget Sound.

export async function initForecast(container) {
  container.innerHTML = 'Loading forecast…';

  try {
    const listRes = await fetch('https://api.weather.gov/products?type=CWF&location=SEW', {
      headers: { Accept: 'application/ld+json' },
    });
    if (!listRes.ok) throw new Error(`HTTP ${listRes.status}`);
    const list = await listRes.json();

    const productId = list['@graph']?.[0]?.id;
    if (!productId) throw new Error('no CWF products found');

    const prodRes = await fetch(`https://api.weather.gov/products/${productId}`);
    if (!prodRes.ok) throw new Error(`HTTP ${prodRes.status}`);
    const prod = await prodRes.json();

    const text = prod.productText ?? '';
    container.innerHTML = formatCwf(text, prod.issuanceTime);
  } catch (e) {
    console.warn('forecast fetch failed', e);
    container.innerHTML =
      '<p class="forecast-error">Failed to load forecast. Check your connection and try again.</p>';
  }
}

function formatCwf(text, issuanceTime) {
  // Strip WMO header lines and blank leading lines
  const body = text.replace(/^[\s\S]*?(?=\.SYNOPSIS)/m, '').trim();

  const issued = issuanceTime
    ? `<div class="forecast-meta">Issued: ${new Date(issuanceTime).toLocaleString()}</div>`
    : '';

  // Split into sections on lines that look like zone IDs or period headers
  const lines = body.split('\n');
  let html = issued + '<div class="forecast-body">';
  for (const line of lines) {
    const trimmed = line.trim();
    if (!trimmed || trimmed === '$$') continue;
    if (/^\.\w.*\.\.\./i.test(trimmed)) {
      // Period header like ".TONIGHT..." or ".MONDAY..."
      html += `<h3 class="forecast-period">${escapeHtml(trimmed.replace(/^\./, '').replace(/\.\.\.$/, ''))}</h3>`;
    } else if (/^PZZ\d+/i.test(trimmed)) {
      // Zone header like "PZZ133-201015-"
      html += `<h2 class="forecast-zone">${escapeHtml(trimmed.split('-')[0])}</h2>`;
    } else if (/^[A-Z][\w\s]+-$/.test(trimmed)) {
      // Zone name line
      html += `<div class="forecast-zone-name">${escapeHtml(trimmed.replace(/-$/, ''))}</div>`;
    } else {
      html += `<p>${escapeHtml(trimmed)}</p>`;
    }
  }
  html += '</div>';
  return html;
}

function escapeHtml(s) {
  return String(s ?? '').replace(/[&<>"']/g, (c) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' })[c]);
}
