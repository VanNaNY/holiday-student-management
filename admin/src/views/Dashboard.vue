<template>
  <el-container style="height: 100vh">
    <el-aside width="210px" class="aside">
      <div class="logo">假期管理后台</div>
      <el-menu :default-active="'dashboard'" router>
        <el-menu-item index="dashboard">
          <el-icon><DataLine /></el-icon><span>控制台</span>
        </el-menu-item>
        <el-menu-item index="batch" disabled>
          <el-icon><Calendar /></el-icon><span>批次管理</span>
        </el-menu-item>
        <el-menu-item index="student" disabled>
          <el-icon><User /></el-icon><span>学生管理</span>
        </el-menu-item>
        <el-menu-item index="stat" disabled>
          <el-icon><PieChart /></el-icon><span>统计导出</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <span>控制台</span>
        <el-tag :type="status === 'UP' ? 'success' : 'danger'">后端：{{ status }}</el-tag>
      </el-header>
      <el-main>
        <el-card>
          <template #header>后端连通性自检</template>
          <p>应用：{{ app }}</p>
          <p>状态：{{ status }}</p>
          <el-alert
            title="PC 后台脚手架已就绪，批次/学生/统计等模块将在后续阶段填充。"
            type="info"
            :closable="false"
          />
        </el-card>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../api/request'

const status = ref('检测中…')
const app = ref('-')

onMounted(async () => {
  try {
    const data = await request.get('/ping')
    status.value = data.status
    app.value = data.app
  } catch (e) {
    status.value = '连接失败'
  }
})
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
}
</style>
