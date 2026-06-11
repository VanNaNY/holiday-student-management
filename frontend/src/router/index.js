import { createRouter, createWebHistory } from 'vue-router'

// 移动端路由：登录 + 角色首页 + 学生登记/记录页（Phase 2）。
const routes = [
  { path: '/', redirect: '/home' },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { title: '登录', public: true }
  },
  { path: '/home', name: 'Home', component: () => import('../views/Home.vue'), meta: { title: '首页' } },
  { path: '/more', name: 'More', component: () => import('../views/More.vue'), meta: { title: '更多功能' } },

  // 离校登记
  { path: '/leave', name: 'LeaveForm', component: () => import('../views/leave/LeaveForm.vue'), meta: { title: '离校登记' } },
  { path: '/leave/records', name: 'LeaveRecords', component: () => import('../views/leave/LeaveRecords.vue'), meta: { title: '离校登记记录' } },

  // 返校登记
  { path: '/return', name: 'ReturnForm', component: () => import('../views/ret/ReturnForm.vue'), meta: { title: '返校登记' } },
  { path: '/return/records', name: 'ReturnRecords', component: () => import('../views/ret/ReturnRecords.vue'), meta: { title: '返校登记记录' } },

  // 留校申请
  { path: '/stay', name: 'StayForm', component: () => import('../views/stay/StayForm.vue'), meta: { title: '留校申请' } },
  { path: '/stay/records', name: 'StayRecords', component: () => import('../views/stay/StayRecords.vue'), meta: { title: '留校申请记录' } },
  { path: '/stay/:id', name: 'StayDetail', component: () => import('../views/stay/StayDetail.vue'), meta: { title: '留校申请详情' } },

  // 留校审批（辅导员/副书记）
  { path: '/approval', name: 'ApprovalList', component: () => import('../views/approval/ApprovalList.vue'), meta: { title: '假期审批' } },
  { path: '/approval/records', name: 'ApprovalRecords', component: () => import('../views/approval/ApprovalRecords.vue'), meta: { title: '审批记录' } },
  { path: '/approval/:id', name: 'ApprovalDetail', component: () => import('../views/approval/ApprovalDetail.vue'), meta: { title: '留校审批详情' } },

  // 签到
  { path: '/checkin/stay', name: 'StayCheckin', component: () => import('../views/checkin/StayCheckin.vue'), meta: { title: '留校签到' } },
  { path: '/checkin/return', name: 'ReturnCheckin', component: () => import('../views/checkin/ReturnCheckin.vue'), meta: { title: '返校报到' } },
  { path: '/checkin/summary', name: 'CheckinSummary', component: () => import('../views/checkin/CheckinSummary.vue'), meta: { title: '留校签到汇总' } },
  { path: '/checkin/detail/:date', name: 'CheckinDetail', component: () => import('../views/checkin/CheckinDetail.vue'), meta: { title: '签到详情' } },

  // 管理与统计（教职工）
  { path: '/staff-more', name: 'StaffMore', component: () => import('../views/manage/StaffMore.vue'), meta: { title: '更多功能' } },
  { path: '/stat', name: 'BatchStat', component: () => import('../views/manage/BatchStat.vue'), meta: { title: '批次统计' } },
  { path: '/manage/unregistered', name: 'Unregistered', component: () => import('../views/manage/Unregistered.vue'), meta: { title: '假期未登记' } },
  { path: '/manage/help-reset', name: 'HelpReset', component: () => import('../views/manage/HelpReset.vue'), meta: { title: '帮助重置登记' } },
  { path: '/manage/managers', name: 'ManagerAdd', component: () => import('../views/manage/ManagerAdd.vue'), meta: { title: '留校责任人' } },
  { path: '/manage/central-dorms', name: 'CentralDormAdd', component: () => import('../views/manage/CentralDormAdd.vue'), meta: { title: '集中住宿地址' } }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  const token = localStorage.getItem('token')
  if (!to.meta.public && !token) {
    return { path: '/login' }
  }
  if (to.path === '/login' && token) {
    return { path: '/home' }
  }
  return true
})

router.afterEach((to) => {
  if (to.meta?.title) document.title = to.meta.title
})

export default router
