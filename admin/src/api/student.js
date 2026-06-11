import request from './request'

/** 学生分页 */
export const pageStudent = (params) => request.get('/student', { params })

/** 新增/编辑学生 */
export const saveStudent = (data) => request.post('/student', data)

/** 删除学生 */
export const deleteStudent = (id) => request.delete(`/student/${id}`)

/** 学院列表 */
export const listColleges = () => request.get('/org/colleges')

/** 班级列表 */
export const listClasses = (collegeId) =>
  request.get('/org/classes', { params: { collegeId } })

/** 导入模板下载地址（需带 token，故用 blob 方式） */
export const downloadTemplate = () =>
  request.get('/student/import/template', { responseType: 'blob' })

/** Excel 导入 */
export const importStudents = (file) => {
  const fd = new FormData()
  fd.append('file', file)
  return request.post('/student/import', fd, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
