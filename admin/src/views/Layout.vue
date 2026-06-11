<template>
  <el-container style="height: 100vh">
    <el-aside width="210px" class="aside">
      <div class="logo">假期管理后台</div>
      <el-menu :default-active="activeMenu" router background-color="#001529" text-color="#c0c4cc"
               active-text-color="#fff">
        <el-menu-item index="/dashboard">
          <el-icon><DataLine /></el-icon><span>控制台</span>
        </el-menu-item>
        <el-menu-item index="/batch">
          <el-icon><Calendar /></el-icon><span>批次管理</span>
        </el-menu-item>
        <el-menu-item index="/student">
          <el-icon><User /></el-icon><span>学生管理</span>
        </el-menu-item>
        <el-menu-item index="/staff">
          <el-icon><Avatar /></el-icon><span>教职工管理</span>
        </el-menu-item>
        <el-menu-item index="/stat">
          <el-icon><DataAnalysis /></el-icon><span>统计与导出</span>
        </el-menu-item>
        <el-menu-item index="/approval">
          <el-icon><Tickets /></el-icon><span>审批记录查询</span>
        </el-menu-item>
        <el-menu-item index="/checkin">
          <el-icon><Location /></el-icon><span>签到记录查询</span>
        </el-menu-item>
        <el-menu-item index="/config">
          <el-icon><Setting /></el-icon><span>系统配置</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <span class="page-title">{{ pageTitle }}</span>
        <el-dropdown @command="onCommand">
          <span class="user">
            {{ userStore.user?.name || '未登录' }}
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowDown } from '@element-plus/icons-vue'
import { useUserStore } from '../store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
const pageTitle = computed(() => route.meta?.title || '')

onMounted(() => {
  if (!userStore.user) userStore.fetchMe().catch(() => {})
})

function onCommand(cmd) {
  if (cmd === 'logout') {
    userStore.logout()
    router.push('/login')
  }
}
</script>

<style scoped>
.aside {
  background: #001529;
}
.logo {
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  text-align: center;
  line-height: 56px;
}
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #eee;
  background: #fff;
}
.page-title {
  font-size: 16px;
  font-weight: 600;
}
.user {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
  color: #303133;
}
</style>
