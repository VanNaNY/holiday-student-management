import request from './request'

/** 审批记录（可按批次/状态/关键词过滤） */
export const approvalRecords = (params) => request.get('/approval/records', { params })

/** 待审批列表 */
export const approvalPending = (params) => request.get('/approval/pending', { params })

/** 审批详情 */
export const approvalDetail = (id) => request.get(`/approval/${id}`)
