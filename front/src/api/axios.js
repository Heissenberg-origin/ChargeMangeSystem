// src/api/axios.js
// src/api/axios.js
import axios from 'axios'

// 使用 import.meta.env 代替 process.env
const http = axios.create({
  baseURL: '/abc',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
    Accept: 'application/json'
  }
})

// 请求拦截器
http.interceptors.request.use(
  config => {
    // 可以在这里添加token等
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
http.interceptors.response.use(
  response => {
    return response
  },
  error => {
    return Promise.reject(error)
  }
)

export default http