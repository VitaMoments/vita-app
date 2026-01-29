import { defineConfig, loadEnv } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), "VITE_");
  const target = env.VITE_API_BASE_URL || "http://localhost:8080";

  return {
    plugins: [react()],
    base: mode === "production" ? "/vita-app/" : "/",
    build: {
      outDir: "dist",
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
        "/api": {
          target,
          changeOrigin: true,
          secure: false,
        },
        "/uploads": {
          target,
          changeOrigin: true,
          secure: false,
        },
      },
    },
  };
});
