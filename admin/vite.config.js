import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// PC 后台：开发时把 /api 代理到后端 8080，端口 5174 与移动端错开
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5174,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
