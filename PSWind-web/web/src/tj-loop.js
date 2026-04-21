// TJ loop: cycle through wxtofly.net RASP forecast images.
// URL shape: http://wxtofly.net/RASP/PNW/FCST/tenmwind.curr.{HHMM}lst.{grid}.png
//   grid = d2 → TJ Seattle
//   grid = w2 → TJ North Sound
// Hours 0500-2200 attempted; pre-dawn/post-dusk usually 404 and are skipped.
//
// Model updates 2x/day, so images change only twice a day; a short in-memory
// preload is enough to animate smoothly.

const LST_HOURS = [
  '0700','0800','0900','1000','1100','1200',
  '1300','1400','1500','1600','1700','1800','1900','2000',
];
const LABELS = [
  '7AM','8AM','9AM','10AM','11AM','12noon',
  '1PM','2PM','3PM','4PM','5PM','6PM','7PM','8PM',
];
const FRAME_MS = 700;

export function initTjLoop(root, grid) {
  root.innerHTML = `
    <div class="tj-loop-inner">
      <div class="tj-stage">
        <img class="tj-img" alt="RASP forecast" />
        <div class="tj-time"></div>
      </div>
      <div class="tj-controls">
        <button class="tj-play" type="button" aria-label="Play/pause">▶︎</button>
        <input class="tj-scrub" type="range" min="0" max="0" value="0" step="1" aria-label="Frame" />
        <span class="tj-counter"></span>
      </div>
    </div>
  `;

  const imgEl = root.querySelector('.tj-img');
  const timeEl = root.querySelector('.tj-time');
  const playBtn = root.querySelector('.tj-play');
  const scrub = root.querySelector('.tj-scrub');
  const counter = root.querySelector('.tj-counter');

  let frames = []; // [{src, label}]
  let idx = 0;
  let playing = false;
  let timer = null;

  (async () => {
    frames = await preloadFrames(grid);
    if (frames.length === 0) {
      timeEl.textContent = 'No forecast imagery available.';
      return;
    }
    scrub.max = String(frames.length - 1);
    setFrame(0);
    start();
  })();

  scrub.addEventListener('input', () => {
    stop();
    setFrame(Number(scrub.value));
  });
  playBtn.addEventListener('click', () => (playing ? stop() : start()));

  function setFrame(i) {
    idx = i;
    const f = frames[i];
    if (!f) return;
    imgEl.src = f.src;
    timeEl.textContent = f.label;
    scrub.value = String(i);
    counter.textContent = `${i + 1} / ${frames.length}`;
  }

  function start() {
    if (frames.length <= 1) return;
    playing = true;
    playBtn.textContent = '❚❚';
    timer = setInterval(() => {
      setFrame((idx + 1) % frames.length);
    }, FRAME_MS);
  }

  function stop() {
    playing = false;
    playBtn.textContent = '▶︎';
    clearInterval(timer);
    timer = null;
  }

  // Pause looping when the tab isn't visible to save battery.
  const observer = new IntersectionObserver(([entry]) => {
    if (!entry.isIntersecting) stop();
    else if (!playing && frames.length > 1) start();
  });
  observer.observe(root);
}

async function preloadFrames(grid) {
  const candidates = LST_HOURS.map((h, i) => ({
    src: `/api/tj?grid=${grid}&hour=${h}`,
    label: LABELS[i],
  }));
  const results = await Promise.allSettled(
    candidates.map((c) => loadImage(c.src).then(() => c)),
  );
  return results
    .filter((r) => r.status === 'fulfilled')
    .map((r) => r.value);
}

function loadImage(src) {
  return new Promise((resolve, reject) => {
    const img = new Image();
    img.onload = () => resolve(img);
    img.onerror = () => reject(new Error('404'));
    img.src = src;
  });
}
