<template>
  <div class="page">
    <van-nav-bar title="假期审批" left-arrow @click-left="$router.back()">
      <template #right>
        <span @click="$router.push('/approval/records')">记录</span>
      </template>
    </van-nav-bar>

    <van-search v-model="keyword" placeholder="搜索学号/姓名" @search="reload" @clear="reload" />

    <van-empty v-if="!loading && !rows.length" description="暂无待审批申请" />

    <van-checkbox-group v-model="checked">
      <van-cell-group v-for="r in rows" :key="r.id" inset class="block">
        <van-cell>
          <template #title>
            <van-checkbox :name="r.id" @click.stop>
              {{ r.studentName }} · {{ r.studentNo }}
            </van-checkbox>
          </template>
          <template #right-icon>
            <van-tag type="warning">{{ nodeText(r.currentNode) }}</van-tag>
          </template>
        </van-cell>
        <van-cell title="班级" :value="r.className || '-'" @click="goDetail(r.id)" />
        <van-cell title="留校期间" :value="`${r.planStart || '-'} ~ ${r.planEnd || '-'}`" @click="goDetail(r.id)" />
        <van-cell title="事由" :label="r.reason" is-link @click="goDetail(r.id)" />
      </van-cell-group>
    </van-checkbox-group>

    <div style="height: 60px"></div>
    <van-action-bar v-if="rows.length">
      <van-action-bar-icon icon="passed" :text="allSelected ? '取消全选' : '全选'" @click="toggleAll" />
      <van-action-bar-button type="danger" :text="`驳回(${checked.length})`" :disabled="!checked.length"
                             @click="onBatch('REJECT')" />
      <van-action-bar-button type="success" :text="`通过(${checked.length})`" :disabled="!checked.length"
                             @click="onBatch('APPROVE')" />
    </van-action-bar>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { showSuccessToast, showToast, showConfirmDialog } from 'vant'
import { pendingApprovals, batchApproval } from '../../api/approval'
import { currentBatch } from '../../api/auth'

const router = useRouter()
const rows = ref([])
const checked = ref([])
const keyword = ref('')
const loading = ref(true)
const batchId = ref(null)

const NODE = { COUNSELOR: '辅导员待审', SECRETARY: '副书记待审', DONE: '已结束' }
const nodeText = (n) => NODE[n] || n

const allSelected = computed(() => rows.value.length > 0 && checked.value.length === rows.value.length)

async function load() {
  loading.value = true
  try {
    const data = await pendingApprovals({
      batchId: batchId.value || undefined,
      keyword: keyword.value || undefined,
      page: 1,
      size: 50
    })
    rows.value = data.records
    checked.value = []
  } catch (e) {
    showToast(e.message)
  } finally {
    loading.value = false
  }
}
function reload() {
  load()
}
function toggleAll() {
  checked.value = allSelected.value ? [] : rows.value.map((r) => r.id)
}
function goDetail(id) {
  router.push(`/approval/${id}`)
}
async function onBatch(action) {
  const word = action === 'APPROVE' ? '通过' : '驳回'
  await showConfirmDialog({ title: '批量审批', message: `确认${word}所选 ${checked.value.length} 条申请？` })
  try {
    const res = await batchApproval(checked.value, action)
    showSuccessToast(`成功 ${res.success}，失败 ${res.failed}`)
    load()
  } catch (e) {
    showToast(e.message)
  }
}

onMounted(async () => {
  try {
    const b = await currentBatch()
    batchId.value = b?.id || null
  } catch { /* ignore */ }
  load()
})
</script>

<style scoped>
.block {
  margin-top: 12px;
}
</style>
