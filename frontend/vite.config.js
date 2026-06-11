import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// 移动端 H5：开发时把 /api 代理到后端 8080
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
