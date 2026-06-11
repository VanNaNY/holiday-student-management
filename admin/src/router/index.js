import { createRouter, createWebHistory } from 'vue-router'

// 后台路由：登录 + 主框架布局（含批次/学生等子页）
const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { title: '后台登录', public: true }
  },
  {
    path: '/',
    component: () => import('../views/Layout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue'),
        meta: { title: '控制台' }
      },
      {
        path: 'batch',
        name: 'Batch',
        component: () => import('../views/Batch.vue'),
        meta: { title: '批次管理' }
      },
      {
        path: 'student',
        name: 'Student',
        component: () => import('../views/Student.vue'),
        meta: { title: '学生管理' }
      },
      {
        path: 'staff',
        name: 'Staff',
        component: () => import('../views/Staff.vue'),
        meta: { title: '教职工管理' }
      },
      {
        path: 'stat',
        name: 'Stat',
        component: () => import('../views/Stat.vue'),
        meta: { title: '统计与导出' }
      },
      {
        path: 'approval',
        name: 'Approval',
        component: () => import('../views/Approval.vue'),
        meta: { title: '审批记录查询' }
      },
      {
        path: 'checkin',
        name: 'Checkin',
        component: () => import('../views/Checkin.vue'),
        meta: { title: '签到记录查询' }
      },
      {
        path: 'config',
        name: 'Config',
        component: () => import('../views/Config.vue'),
        meta: { title: '系统配置' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 登录守卫
router.beforeEach((to) => {
  const token = localStorage.getItem('admin_token')
  if (!to.meta.public && !token) {
    return { path: '/login' }
  }
  if (to.path === '/login' && token) {
    return { path: '/dashboard' }
  }
  return true
})

router.afterEach((to) => {
  if (to.meta?.title) document.title = to.meta.title + ' · 假期管理后台'
})

export default router
