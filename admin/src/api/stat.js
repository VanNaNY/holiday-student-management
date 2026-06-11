import request from './request'

/** 批次统计概览（按当前角色范围；管理员=全部） */
export const statOverview = (batchId) =>
  request.get('/stat/overview', { params: { batchId } })

/** 导出统计 Excel（blob） */
export const exportStat = (batchId) =>
  request.get('/stat/export', { params: { batchId }, responseType: 'blob' })
