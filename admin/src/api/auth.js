import request from './request'

/** 登录 */
export const login = (data) => request.post('/auth/login', data)

/** 当前用户 */
export const getMe = () => request.get('/auth/me')

/** 角色切换 */
export const switchRole = (role) => request.post('/auth/switch-role', { role })
