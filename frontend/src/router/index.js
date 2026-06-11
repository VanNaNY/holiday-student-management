import { createRouter, createWebHistory } from 'vue-router'

// 移动端路由：登录 + 角色首页（学生/辅导员/副书记），后续阶段逐页填充。
const routes = [
  { path: '/', redirect: '/home' },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { title: '登录', public: true }
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('../views/Home.vue'),
    meta: { title: '首页' }
  }
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
