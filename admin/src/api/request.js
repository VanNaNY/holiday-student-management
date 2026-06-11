import axios from 'axios'

// 统一 axios 实例：baseURL 指向后端 /api（开发由 vite 代理）
const request = axios.create({
  baseURL: '/api',
  timeout: 15000
})

request.interceptors.request.use((config) => {
  const token = localStorage.getItem('admin_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

request.interceptors.response.use(
  (resp) => {
    const body = resp.data
    if (body && typeof body.code !== 'undefined') {
      if (body.code === 200) return body.data
      return Promise.reject(new Error(body.msg || '请求失败'))
    }
    return body
  },
  (err) => Promise.reject(err)
)

export default request
