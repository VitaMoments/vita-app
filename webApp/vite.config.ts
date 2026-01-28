import { defineConfig, loadEnv } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  root: '.',
  base: '/',
  plugins: [react()],
  build: {
    outDir: 'dist',
    emptyOutDir: true,
    rollupOptions: {
      output: {
        manualChunks: {
          react: ["react", "react-dom", "react-router-dom"],
          tiptap: ["@tiptap/core", "@tiptap/react"],
          icons: ["react-icons"],
        },
      },
    },
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