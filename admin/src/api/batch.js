import request from './request'

/** 批次列表 */
export const listBatch = () => request.get('/batch')

/** 当前生效批次 */
export const currentBatch = () => request.get('/batch/current')

/** 新建批次 */
export const createBatch = (data) => request.post('/batch', data)

/** 更新批次 */
export const updateBatch = (id, data) => request.put(`/batch/${id}`, data)

/** 修改状态 */
export const changeBatchStatus = (id, status) =>
  request.put(`/batch/${id}/status`, null, { params: { status } })

/** 删除批次 */
export const deleteBatch = (id) => request.delete(`/batch/${id}`)
