<template>
  <div>
    <el-card shadow="never" class="card">
      <div class="toolbar">
        <span>批次：</span>
        <el-select v-model="batchId" style="width: 220px" @change="loadSummary">
          <el-option v-for="b in batches" :key="b.id" :label="b.name" :value="b.id" />
        </el-select>
        <el-button :icon="Refresh" @click="loadSummary">刷新</el-button>
        <span class="hint">留校签到按日汇总（已签 / 应签）</span>
      </div>

      <el-table :data="summary" v-loading="loading" border stripe>
        <el-table-column prop="date" label="日期" min-width="140" />
        <el-table-column label="签到情况" min-width="200">
          <template #default="{ row }">
            <el-progress
              :percentage="pct(row)"
              :status="row.signed >= row.total && row.total > 0 ? 'success' : ''"
              style="max-width: 240px"
            />
            <span class="ratio">{{ row.signed }} / {{ row.total }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openDetail(row.date)">查看明细</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 某日明细抽屉 -->
    <el-drawer v-model="drawer" :title="`签到明细 · ${curDate}`" size="520px">
      <el-radio-group v-model="signedFilter" @change="loadDetail" style="margin-bottom: 12px">
        <el-radio-button :value="''">全部</el-radio-button>
        <el-radio-button :value="'true'">已签到</el-radio-button>
        <el-radio-button :value="'false'">未签到</el-radio-button>
      </el-radio-group>

      <el-table :data="detailRows" v-loading="detailLoading" border stripe>
        <el-table-column prop="studentNo" label="学号" width="110" />
        <el-table-column prop="studentName" label="姓名" width="90" />
        <el-table-column prop="className" label="班级" min-width="120" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.signed ? 'success' : 'info'">
              {{ row.signed ? '已签' : '未签' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="电话" width="130">
          <template #default="{ row }">
            <el-link v-if="row.phone" type="primary" :href="`tel:${row.phone}`">{{ row.phone }}</el-link>
            <span v-else>-</span>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        class="pager"
        layout="total, prev, pager, next"
        :total="detailTotal"
        :page-size="detailPage.size"
        :current-page="detailPage.page"
        @current-change="onDetailPage"
      />
    </el-drawer>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { Refresh } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { listBatch } from '../api/batch'
import { checkinSummary, checkinDetail } from '../api/checkin'

const batches = ref([])
const batchId = ref(null)
const summary = ref([])
const loading = ref(false)

const drawer = ref(false)
const curDate = ref('')
const signedFilter = ref('')
const detailRows = ref([])
const detailTotal = ref(0)
const detailLoading = ref(false)
const detailPage = reactive({ page: 1, size: 20 })

const pct = (row) => (row.total > 0 ? Math.round((row.signed / row.total) * 100) : 0)

async function loadSummary() {
  if (!batchId.value) return
  loading.value = true
  try {
    summary.value = await checkinSummary(batchId.value)
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    loading.value = false
  }
}

function openDetail(date) {
  curDate.value = date
  signedFilter.value = ''
  detailPage.page = 1
  drawer.value = true
  loadDetail()
}

async function loadDetail() {
  detailLoading.value = true
  try {
    const params = { batchId: batchId.value, date: curDate.value, page: detailPage.page, size: detailPage.size }
    if (signedFilter.value !== '') params.signed = signedFilter.value
    const data = await checkinDetail(params)
    detailRows.value = data.records || []
    detailTotal.value = data.total || 0
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    detailLoading.value = false
  }
}
function onDetailPage(p) {
  detailPage.page = p
  loadDetail()
}

onMounted(async () => {
  try {
    batches.value = await listBatch()
    const active = batches.value.find((b) => b.status === 'ACTIVE') || batches.value[0]
    if (active) {
      batchId.value = active.id
      await loadSummary()
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
  margin-bottom: 12px;
}
.hint {
  color: #909399;
  font-size: 13px;
}
.ratio {
  margin-left: 8px;
  color: #606266;
}
.pager {
  margin-top: 12px;
  justify-content: flex-end;
}
</style>
