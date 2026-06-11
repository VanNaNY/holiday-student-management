import request from './request'

// ---------- 离校登记 ----------
export const submitLeave = (data) => request.post('/leave', data)
export const myLeave = (batchId) => request.get('/leave/my', { params: { batchId } })
export const leaveRecords = () => request.get('/leave/records')
export const updateLeaveStatus = (id, status) =>
  request.put(`/leave/${id}/status`, null, { params: { status } })

// ---------- 返校登记 ----------
export const submitReturn = (data) => request.post('/return', data)
export const myReturn = (batchId) => request.get('/return/my', { params: { batchId } })
export const returnRecords = () => request.get('/return/records')
export const updateReturnStatus = (id, status) =>
  request.put(`/return/${id}/status`, null, { params: { status } })

// ---------- 留校申请 ----------
export const submitStay = (data) => request.post('/stay', data)
export const withdrawStay = (id) => request.post(`/stay/${id}/withdraw`)
export const stayDetail = (id) => request.get(`/stay/${id}`)
export const stayRecords = (status) => request.get('/stay/records', { params: { status } })
export const stayCentralDorms = () => request.get('/stay/central-dorms')
export const stayManagers = () => request.get('/stay/managers')

// ---------- 文件上传 ----------
export const uploadFile = (file) => {
  const fd = new FormData()
  fd.append('file', file)
  return request.post('/file/upload', fd, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
