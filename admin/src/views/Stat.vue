<template>
  <div>
    <el-card shadow="never" class="card">
      <div class="toolbar">
        <span>批次：</span>
        <el-select v-model="batchId" style="width: 220px" @change="load">
          <el-option v-for="b in batches" :key="b.id" :label="b.name" :value="b.id" />
        </el-select>
        <el-button type="primary" :icon="Download" :loading="exporting" @click="onExport">
          导出 Excel
        </el-button>
        <el-button :icon="Refresh" @click="load">刷新</el-button>
        <span v-if="data" class="total">范围内学生总数：<b>{{ data.totalStudents }}</b></span>
      </div>
    </el-card>

    <el-row :gutter="16" v-loading="loading">
      <el-col :span="8">
        <el-card shadow="never">
          <template #header>离校登记</template>
          <el-descriptions :column="1" border>
            <el-descriptions-item v-for="s in LEAVE_STATUS" :key="s.key" :label="s.text">
              <el-tag :type="s.type">{{ num(data?.leave, s.key) }}</el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <template #header>留校申请</template>
          <el-descriptions :column="1" border>
            <el-descriptions-item v-for="s in STAY_STATUS" :key="s.key" :label="s.text">
              <el-tag :type="s.type">{{ num(data?.stay, s.key) }}</el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <template #header>返校登记</template>
          <el-descriptions :column="1" border>
            <el-descriptions-item v-for="s in LEAVE_STATUS" :key="s.key" :label="s.text">
              <el-tag :type="s.type">{{ num(data?.ret, s.key) }}</el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { Download, Refresh } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { listBatch } from '../api/batch'
import { statOverview, exportStat } from '../api/stat'

const LEAVE_STATUS = [
  { key: 'NOT_REG', text: '未登记', type: 'info' },
  { key: 'REGISTERED', text: '已登记', type: 'success' },
  { key: 'ON_THE_WAY', text: '途中', type: 'warning' },
  { key: 'ARRIVED', text: '已到达', type: 'primary' }
]
const STAY_STATUS = [
  { key: 'PENDING', text: '待审批', type: 'warning' },
  { key: 'APPROVED', text: '已通过', type: 'success' },
  { key: 'REJECTED', text: '已驳回', type: 'danger' },
  { key: 'WITHDRAWN', text: '已撤回', type: 'info' }
]

const batches = ref([])
const batchId = ref(null)
const data = ref(null)
const loading = ref(false)
const exporting = ref(false)

const num = (map, key) => (map && map[key] != null ? map[key] : 0)

async function load() {
  if (!batchId.value) return
  loading.value = true
  try {
    data.value = await statOverview(batchId.value)
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    loading.value = false
  }
}

async function onExport() {
  if (!batchId.value) return ElMessage.warning('请先选择批次')
  exporting.value = true
  try {
    const blob = await exportStat(batchId.value)
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = '假期统计.xlsx'
    a.click()
    URL.revokeObjectURL(url)
  } catch (e) {
    ElMessage.error(e.message || '导出失败')
  } finally {
    exporting.value = false
  }
}

onMounted(async () => {
  try {
    batches.value = await listBatch()
    const active = batches.value.find((b) => b.status === 'ACTIVE') || batches.value[0]
    if (active) {
      batchId.value = active.id
      await load()
    }
  } catch (e) {
    ElMessage.error(e.message)
  }
})
</script>

<style scoped>
.card {
  margin-bottom: 16px;
}
.toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
}
.total {
  margin-left: auto;
  color: #606266;
}
</style>
