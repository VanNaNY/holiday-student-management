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
