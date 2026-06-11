import request from './request'

// ---------- 统计 ----------
export const statOverview = (batchId) => request.get('/stat/overview', { params: { batchId } })
export const exportStat = (batchId) =>
  request.get('/stat/export', { params: { batchId }, responseType: 'blob' })

// ---------- 未登记 / 已登记 ----------
export const unregistered = (params) => request.get('/manage/unregistered', { params })
export const leaveRegistered = (params) => request.get('/manage/leave-registered', { params })

// ---------- 帮助重置 ----------
export const resetLeave = (batchId, studentIds) =>
  request.post('/manage/reset-leave', { batchId, studentIds })
export const resetReturn = (batchId, studentIds) =>
  request.post('/manage/reset-return', { batchId, studentIds })
export const resetStay = (applicationIds) => request.post('/manage/reset-stay', { applicationIds })

// ---------- 修改离校时间 ----------
export const updateLeaveTime = (data) => request.put('/manage/leave-time', data)

// ---------- 责任人 / 集中住宿 ----------
export const listManagers = () => request.get('/manage/managers')
export const addManager = (data) => request.post('/manage/managers', data)
export const listCentralDorms = () => request.get('/manage/central-dorms')
export const addCentralDorm = (data) => request.post('/manage/central-dorms', data)
