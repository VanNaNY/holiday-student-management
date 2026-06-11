import request from './request'

/** 登录 */
export const login = (data) => request.post('/auth/login', data)

/** 当前用户 */
export const getMe = () => request.get('/auth/me')

/** 角色切换（返回新 token） */
export const switchRole = (role) => request.post('/auth/switch-role', { role })

/** 当前生效批次 */
export const currentBatch = () => request.get('/batch/current')
