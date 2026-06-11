<template>
  <div class="page">
    <van-nav-bar title="审批记录" left-arrow @click-left="$router.back()" />
    <van-search v-model="keyword" placeholder="搜索学号/姓名" @search="reload" @clear="reload" />
    <van-tabs v-model:active="active" @change="reload">
      <van-tab title="全部" name="" />
      <van-tab title="通过" name="APPROVED" />
      <van-tab title="不通过" name="REJECTED" />
      <van-tab title="待审批" name="PENDING" />
    </van-tabs>

    <van-empty v-if="!loading && !rows.length" description="暂无记录" />
    <van-list v-model:loading="loading" :finished="finished" finished-text="没有更多了" @load="onLoad">
      <van-cell-group v-for="r in rows" :key="r.id" inset class="block" @click="$router.push(`/approval/${r.id}`)">
        <van-cell :title="`${r.studentName} · ${r.studentNo}`" is-link>
          <template #value>
            <van-tag :type="statusType(r.approvalStatus)">{{ statusText(r.approvalStatus) }}</van-tag>
          </template>
        </van-cell>
        <van-cell title="班级" :value="r.className || '-'" />
        <van-cell title="留校期间" :value="`${r.planStart || '-'} ~ ${r.planEnd || '-'}`" />
      </van-cell-group>
    </van-list>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { showToast } from 'vant'
import { approvalRecords } from '../../api/approval'
import { currentBatch } from '../../api/auth'

const rows = ref([])
const keyword = ref('')
const active = ref('')
const loading = ref(false)
const finished = ref(false)
const page = ref(1)
const size = 10
const batchId = ref(null)

const STATUS = { PENDING: '待审批', APPROVED: '已通过', REJECTED: '已驳回', WITHDRAWN: '已撤回' }
const statusText = (s) => STATUS[s] || s
const statusType = (s) =>
  ({ PENDING: 'warning', APPROVED: 'success', REJECTED: 'danger', WITHDRAWN: 'default' }[s] || 'default')

async function fetchPage() {
  const data = await approvalRecords({
    batchId: batchId.value || undefined,
    status: active.value || undefined,
    keyword: keyword.value || undefined,
    page: page.value,
    size
  })
  return data
}

async function onLoad() {
  try {
    const data = await fetchPage()
    rows.value.push(...data.records)
    if (rows.value.length >= data.total || data.records.length === 0) {
      finished.value = true
    } else {
      page.value += 1
    }
  } catch (e) {
    finished.value = true
    showToast(e.message)
  } finally {
    loading.value = false
  }
}
function reload() {
  rows.value = []
  page.value = 1
  finished.value = false
  loading.value = true
  onLoad()
}

onMounted(async () => {
  try {
    const b = await currentBatch()
    batchId.value = b?.id || null
  } catch { /* ignore */ }
  reload()
})
</script>

<style scoped>
.block {
  margin-top: 12px;
}
</style>
