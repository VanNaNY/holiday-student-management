import { createRouter, createWebHistory } from 'vue-router'

// 后台路由骨架：登录 + 主框架（含批次/学生/统计等子页，后续阶段填充）
const routes = [
  { path: '/', redirect: '/dashboard' },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { title: '后台登录' }
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('../views/Dashboard.vue'),
    meta: { title: '控制台' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.afterEach((to) => {
  if (to.meta?.title) document.title = to.meta.title + ' · 假期管理后台'
})

export default router
