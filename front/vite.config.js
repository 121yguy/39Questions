import { fileURLToPath, URL } from 'node:url'

import {build, defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'
import legacy from "@vitejs/plugin-legacy"

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    legacy({
      targets: ["ie>=11"],
      additionalLegacyPolyfills: ["regenerator-runtime/runtime"],
    })
  ],
  base: '/',
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/,"")
      }
    }
  },
})
