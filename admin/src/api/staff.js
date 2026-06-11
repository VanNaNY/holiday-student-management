import request from './request'

/** 教职工列表 */
export const listStaff = () => request.get('/staff')

/** 新增/编辑教职工 */
export const saveStaff = (data) => request.post('/staff', data)

/** 删除教职工 */
export const deleteStaff = (id) => request.delete(`/staff/${id}`)

/** 学院列表 */
export const listColleges = () => request.get('/org/colleges')

/** 班级列表（可按学院过滤） */
export const listClasses = (collegeId) =>
  request.get('/org/classes', { params: { collegeId } })

/** 更新班级（含设置辅导员 counselorId） */
export const updateClass = (id, data) => request.put(`/org/classes/${id}`, data)
