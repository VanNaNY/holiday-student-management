import request from './request'

/** 留校签到按日汇总（0/N） */
export const checkinSummary = (batchId) =>
  request.get('/checkin/summary', { params: { batchId } })

/** 某日签到详情（signed=true 已签 / false 未签 / 不传=全部） */
export const checkinDetail = (params) => request.get('/checkin/detail', { params })
