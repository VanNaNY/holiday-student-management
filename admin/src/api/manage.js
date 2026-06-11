import request from './request'

/** 留校责任人列表 */
export const listManagers = () => request.get('/manage/managers')

/** 添加留校责任人 */
export const addManager = (data) => request.post('/manage/managers', data)

/** 集中住宿地址列表 */
export const listCentralDorms = () => request.get('/manage/central-dorms')

/** 添加集中住宿地址 */
export const addCentralDorm = (data) => request.post('/manage/central-dorms', data)

/** 未登记学生分页（type=LEAVE/RETURN） */
export const unregistered = (params) => request.get('/manage/unregistered', { params })
