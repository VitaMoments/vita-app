import { defineConfig, loadEnv } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  root: '.',
  base: '/vita-app/',
  plugins: [react()],
  build: {
    outDir: 'dist',
    emptyOutDir: true,
  },
  server: {
      port: 5174,
      proxy: {
            '/api': {
              target: 'http://localhost:8080',
              changeOrigin: true,
              secure: false,
            },
            "/uploads": {
              target: "http://localhost:8080",
              changeOrigin: true,
              secure: false,
            },
          },
      },
});