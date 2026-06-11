import { createRouter, createWebHistory } from 'vue-router'

// 路由骨架：登录 + 学生/辅导员两大角色分区，后续阶段逐页填充。
const routes = [
  { path: '/', redirect: '/home' },
  {
    path: '/home',
    name: 'Home',
    component: () => import('../views/Home.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { title: '登录' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.afterEach((to) => {
  if (to.meta?.title) document.title = to.meta.title
})

export default router
