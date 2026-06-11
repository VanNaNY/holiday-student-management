import request from './request'

/** 签到规则（围栏+时段） */
export const checkinRule = (batchId, type, collegeId) =>
  request.get('/checkin/rule', { params: { batchId, type, collegeId } })

/** 留校签到 */
export const stayCheckin = (data) => request.post('/checkin/stay', data)

/** 返校报到 */
export const returnCheckin = (data) => request.post('/checkin/return', data)

/** 我的留校签到记录 */
export const myStayCheckins = (batchId) => request.get('/checkin/stay/my', { params: { batchId } })

/** 我的返校报到记录 */
export const myReturnCheckin = (batchId) => request.get('/checkin/return/my', { params: { batchId } })

/** 留校签到按日汇总 */
export const checkinSummary = (batchId) => request.get('/checkin/summary', { params: { batchId } })

/** 某日签到详情 */
export const checkinDetail = (params) => request.get('/checkin/detail', { params })
