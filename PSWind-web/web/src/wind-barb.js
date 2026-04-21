const SIZE = 48;

const STALE_MS = 3 * 60 * 60 * 1000;

function arrowAsset(mph, isStale) {
  if (isStale) return 'marker_stale';
  if (mph >= 27) return 'marker_high_wind';
  if (mph >= 19) return 'marker_medium_wind';
  if (mph >= 13) return 'marker_light_wind';
  return 'marker_no_wind';
}

export function windBarbSvg(mph, dirDeg, timestamp) {
  const speed = Math.max(0, Math.min(99, Math.round(mph ?? 0)));
  const dir = dirDeg ?? 0;
  const isStale = timestamp ? (Date.now() - new Date(timestamp).getTime()) > STALE_MS : false;
  const arrow = arrowAsset(speed, isStale);

  return `
    <div style="position:relative;width:${SIZE}px;height:${SIZE}px;">
      <img src="/markers/${arrow}.png"
           style="position:absolute;inset:0;width:${SIZE}px;height:${SIZE}px;transform:rotate(${dir}deg);transform-origin:center;" />
      <img src="/markers/windspeed${speed}.png"
           style="position:absolute;inset:0;width:${SIZE}px;height:${SIZE}px;pointer-events:none;" />
    </div>`;
}

export { SIZE as WIND_BARB_SIZE };
