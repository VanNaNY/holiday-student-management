import request from './request'

/** 待审批列表（按当前角色节点） */
export const pendingApprovals = (params) => request.get('/approval/pending', { params })

/** 审批记录 */
export const approvalRecords = (params) => request.get('/approval/records', { params })

/** 审批详情 */
export const approvalDetail = (id) => request.get(`/approval/${id}`)

/** 通过 */
export const approve = (id, comment) => request.post(`/approval/${id}/approve`, { comment })

/** 驳回 */
export const reject = (id, comment) => request.post(`/approval/${id}/reject`, { comment })

/** 批量审批 */
export const batchApproval = (ids, action, comment) =>
  request.post('/approval/batch', { ids, action, comment })
