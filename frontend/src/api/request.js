import axios from 'axios'

// 统一 axios 实例：baseURL 指向后端 /api（开发由 vite 代理）
const request = axios.create({
  baseURL: '/api',
  timeout: 15000
})

// 请求拦截：携带 JWT
request.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 响应拦截：拆出统一返回体 {code,msg,data}
request.interceptors.response.use(
  (resp) => {
    const body = resp.data
    if (body instanceof Blob) return body
    if (body && typeof body.code !== 'undefined') {
      if (body.code === 200) return body.data
      return Promise.reject(new Error(body.msg || '请求失败'))
    }
    return body
  },
  (err) => {
    if (err.response && err.response.status === 401) {
      localStorage.removeItem('token')
      if (location.pathname !== '/login') location.href = '/login'
    }
    const msg = err.response?.data?.msg || err.message || '网络错误'
    return Promise.reject(new Error(msg))
  }
)

export default request
