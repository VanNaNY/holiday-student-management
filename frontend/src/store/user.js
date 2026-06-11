import { defineStore } from 'pinia'
import { ref } from 'vue'
import * as authApi from '../api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(null)

  function setToken(t) {
    token.value = t
    if (t) localStorage.setItem('token', t)
    else localStorage.removeItem('token')
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

  async function switchRole(role) {
    const data = await authApi.switchRole(role)
    setToken(data.token)
    user.value = data.user
    return data.user
  }

  function logout() {
    setToken('')
    user.value = null
  }

  return { token, user, login, fetchMe, switchRole, logout, setToken }
})
