<template>
  <div>
    <el-row :gutter="16">
      <el-col :span="8">
        <el-card>
          <template #header>当前生效批次</template>
          <div v-if="batch">
            <p><b>{{ batch.name }}</b></p>
            <p>假期：{{ batch.holidayStart }} ~ {{ batch.holidayEnd }}</p>
            <el-tag type="success">{{ batch.status }}</el-tag>
          </div>
          <el-empty v-else description="暂无生效批次" :image-size="60" />
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>后端连通性</template>
          <p>应用：{{ ping.app }}</p>
          <p>状态：<el-tag :type="ping.status === 'UP' ? 'success' : 'danger'">{{ ping.status }}</el-tag></p>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>快捷入口</template>
          <el-button type="primary" link @click="$router.push('/batch')">批次管理</el-button>
          <el-divider direction="vertical" />
          <el-button type="primary" link @click="$router.push('/student')">学生管理</el-button>
        </el-card>
      </el-col>
    </el-row>
    <el-alert
      style="margin-top: 16px"
      title="Phase 1 已接入：登录鉴权、批次管理、学生导入。后续阶段补全统计/审批查询等。"
      type="info"
      :closable="false"
    />
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import request from '../api/request'
import { currentBatch } from '../api/batch'

const batch = ref(null)
const ping = reactive({ app: '-', status: '检测中…' })

onMounted(async () => {
  try {
    const data = await request.get('/ping')
    ping.app = data.app
    ping.status = data.status
  } catch {
    ping.status = '连接失败'
  }
  try {
    batch.value = await currentBatch()
  } catch { /* ignore */ }
})
</script>
