import { defineStore } from 'pinia'
import { ref } from 'vue'
import * as authApi from '../api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('admin_token') || '')
  const user = ref(null)

  function setToken(t) {
    token.value = t
    if (t) localStorage.setItem('admin_token', t)
    else localStorage.removeItem('admin_token')
  }

  async function login(payload) {
    const data = await authApi.login(payload)
    setToken(data.token)
    user.value = data.user
    return data.user
  }

  async function fetchMe() {
    user.value = await authApi.getMe()
    return user.value
  }

  function logout() {
    setToken('')
    user.value = null
  }

  return { token, user, login, fetchMe, logout, setToken }
})
