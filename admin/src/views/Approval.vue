<template>
  <div>
    <el-card shadow="never" class="card">
      <div class="toolbar">
        <el-select v-model="query.batchId" placeholder="批次" style="width: 180px" @change="reload">
          <el-option v-for="b in batches" :key="b.id" :label="b.name" :value="b.id" />
        </el-select>
        <el-select v-model="query.status" placeholder="全部状态" clearable style="width: 150px" @change="reload">
          <el-option label="待审批" value="PENDING" />
          <el-option label="已通过" value="APPROVED" />
          <el-option label="已驳回" value="REJECTED" />
          <el-option label="已撤回" value="WITHDRAWN" />
        </el-select>
        <el-input
          v-model="query.keyword"
          placeholder="姓名/学号"
          clearable
          style="width: 200px"
          @keyup.enter="reload"
        />
        <el-button type="primary" :icon="Search" @click="reload">查询</el-button>
      </div>

      <el-table :data="rows" v-loading="loading" border stripe>
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="studentName" label="姓名" width="100" />
        <el-table-column prop="className" label="班级" min-width="130" />
        <el-table-column label="留校时间" min-width="180">
          <template #default="{ row }">{{ row.planStart }} ~ {{ row.planEnd }}</template>
        </el-table-column>
        <el-table-column prop="campus" label="校区" width="80" />
        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="statusType(row.approvalStatus)">{{ statusText(row.approvalStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="当前节点" width="110">
          <template #default="{ row }">{{ nodeText(row.currentNode) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        class="pager"
        layout="total, prev, pager, next"
        :total="total"
        :page-size="query.size"
        :current-page="query.page"
        @current-change="onPage"
      />
    </el-card>

    <!-- 详情抽屉 -->
    <el-drawer v-model="drawer" title="留校审批详情" size="460px">
      <div v-if="detail" v-loading="detailLoading">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="学生">
            {{ detail.studentName }}（{{ detail.studentNo }}）
          </el-descriptions-item>
          <el-descriptions-item label="班级">{{ current?.className }}</el-descriptions-item>
          <el-descriptions-item label="电话">{{ current?.studentPhone }}</el-descriptions-item>
          <el-descriptions-item label="留校时间">
            {{ app.planStart }} ~ {{ app.planEnd }}
          </el-descriptions-item>
          <el-descriptions-item label="校区">{{ app.campus }}</el-descriptions-item>
          <el-descriptions-item label="事由">{{ app.reason }}</el-descriptions-item>
          <el-descriptions-item label="紧急联系人">
            {{ app.emergencyName }} {{ app.emergencyPhone }}
          </el-descriptions-item>
          <el-descriptions-item label="原宿舍">{{ app.originDorm }}</el-descriptions-item>
          <el-descriptions-item label="集中住宿">{{ detail.centralDormAddress }}</el-descriptions-item>
          <el-descriptions-item label="责任人">{{ detail.managerName }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="statusType(app.approvalStatus)">
              {{ statusText(app.approvalStatus) }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>

        <div v-if="(detail.attachments || []).length" class="block">
          <div class="block-title">附件</div>
          <el-image
            v-for="(a, i) in detail.attachments"
            :key="i"
            :src="a"
            :preview-src-list="detail.attachments"
            :initial-index="i"
            fit="cover"
            style="width: 88px; height: 88px; margin: 0 8px 8px 0; border-radius: 6px"
          />
        </div>

        <div class="block">
          <div class="block-title">审批流</div>
          <el-timeline>
            <el-timeline-item
              v-for="(r, i) in (detail.approvalRecords || [])"
              :key="i"
              :timestamp="fmt(r.createTime)"
              :type="actionType(r.action)"
            >
              {{ r.approverRole ? roleText(r.approverRole) : '' }}
              {{ actionText(r.action) }}
              <span v-if="r.comment">：{{ r.comment }}</span>
            </el-timeline-item>
          </el-timeline>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { listBatch } from '../api/batch'
import { approvalRecords, approvalDetail } from '../api/approval'

const ROLE = { COUNSELOR: '辅导员', SECRETARY: '副书记', STUDENT: '学生', ADMIN: '管理员' }
const roleText = (r) => ROLE[r] || r
const statusText = (s) =>
  ({ PENDING: '待审批', APPROVED: '已通过', REJECTED: '已驳回', WITHDRAWN: '已撤回' }[s] || s)
const statusType = (s) =>
  ({ PENDING: 'warning', APPROVED: 'success', REJECTED: 'danger', WITHDRAWN: 'info' }[s] || '')
const nodeText = (n) =>
  ({ COUNSELOR: '辅导员', SECRETARY: '副书记', DONE: '已结束' }[n] || n || '-')
const actionText = (a) =>
  ({ SUBMIT: '发起申请', APPROVE: '通过', REJECT: '驳回', WITHDRAW: '撤回' }[a] || a)
const actionType = (a) =>
  ({ SUBMIT: 'primary', APPROVE: 'success', REJECT: 'danger', WITHDRAW: 'info' }[a] || '')
const fmt = (s) => (s ? String(s).replace('T', ' ').slice(0, 16) : '')

const batches = ref([])
const rows = ref([])
const total = ref(0)
const loading = ref(false)
const query = reactive({ batchId: null, status: '', keyword: '', page: 1, size: 10 })

const drawer = ref(false)
const detail = ref(null)
const current = ref(null)
const detailLoading = ref(false)
// 申请明细（嵌套在 detail.application 内）
const app = computed(() => detail.value?.application || {})

async function load() {
  loading.value = true
  try {
    const data = await approvalRecords({ ...query })
    rows.value = data.records || []
    total.value = data.total || 0
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    loading.value = false
  }
}
function reload() {
  query.page = 1
  load()
}
function onPage(p) {
  query.page = p
  load()
}

async function openDetail(row) {
  drawer.value = true
  detail.value = null
  current.value = row
  detailLoading.value = true
  try {
    detail.value = await approvalDetail(row.id)
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    detailLoading.value = false
  }
}

onMounted(async () => {
  try {
    batches.value = await listBatch()
    const active = batches.value.find((b) => b.status === 'ACTIVE') || batches.value[0]
    if (active) query.batchId = active.id
    await load()
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
  gap: 10px;
  margin-bottom: 12px;
}
.pager {
  margin-top: 12px;
  justify-content: flex-end;
}
.block {
  margin-top: 16px;
}
.block-title {
  font-weight: 600;
  margin-bottom: 8px;
}
</style>
