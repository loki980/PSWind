import { defineConfig } from 'vite';

export default defineConfig({
  server: {
    host: '0.0.0.0',
    // Proxy /api to the local Cloudflare Worker during dev.
    // Start the worker with `cd ../worker && npx wrangler dev --port 8787`.
    proxy: {
      '/api': 'http://127.0.0.1:8787',
    },
  },
  build: {
    target: 'es2020',
  },
});
