<template>
  <div class="page">
    <van-nav-bar title="帮助重置登记" left-arrow @click-left="$router.back()" />
    <van-search v-model="keyword" placeholder="搜索学号/姓名" @search="reload" @clear="reload" />

    <van-empty v-if="!loading && !rows.length" description="暂无已登记学生" />
    <van-checkbox-group v-model="checked">
      <van-cell-group v-for="r in rows" :key="r.studentId" inset class="block">
        <van-cell>
          <template #title>
            <van-checkbox :name="r.studentId">{{ r.studentName }} · {{ r.studentNo }}</van-checkbox>
          </template>
        </van-cell>
        <van-cell title="班级" :value="r.className || '-'" />
        <van-cell title="操作">
          <template #value>
            <van-button size="mini" type="primary" plain @click="openTime(r)">改离校时间</van-button>
            <van-button size="mini" type="danger" plain style="margin-left: 6px" @click="resetOne(r)">
              重置
            </van-button>
          </template>
        </van-cell>
      </van-cell-group>
    </van-checkbox-group>

    <div style="height: 60px"></div>
    <van-action-bar v-if="rows.length">
      <van-action-bar-icon icon="passed" :text="allSel ? '取消全选' : '全选'" @click="toggleAll" />
      <van-action-bar-button type="danger" :text="`批量重置(${checked.length})`" :disabled="!checked.length"
                             @click="resetBatch" />
    </van-action-bar>

    <van-dialog v-model:show="showTime" title="修改离校时间" show-cancel-button @confirm="confirmTime">
      <van-field v-model="timeValue" label="离校时间" placeholder="YYYY-MM-DDTHH:mm:ss" />
    </van-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { showSuccessToast, showToast, showConfirmDialog } from 'vant'
import { currentBatch } from '../../api/auth'
import { leaveRegistered, resetLeave, updateLeaveTime } from '../../api/manage'

const rows = ref([])
const checked = ref([])
const keyword = ref('')
const loading = ref(true)
const batchId = ref(null)
const showTime = ref(false)
const timeValue = ref('')
const editing = ref(null)

const allSel = computed(() => rows.value.length > 0 && checked.value.length === rows.value.length)

async function load() {
  loading.value = true
  try {
    if (!batchId.value) {
      const b = await currentBatch()
      batchId.value = b?.id || null
    }
    const data = await leaveRegistered({ batchId: batchId.value, keyword: keyword.value || undefined, page: 1, size: 100 })
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
  checked.value = allSel.value ? [] : rows.value.map((r) => r.studentId)
}
async function resetOne(r) {
  await showConfirmDialog({ title: '提示', message: `重置「${r.studentName}」的离校登记？` })
  await doReset([r.studentId])
}
async function resetBatch() {
  await showConfirmDialog({ title: '批量重置', message: `重置所选 ${checked.value.length} 名学生的离校登记？` })
  await doReset(checked.value)
}
async function doReset(ids) {
  try {
    const res = await resetLeave(batchId.value, ids)
    showSuccessToast(`成功 ${res.success}`)
    load()
  } catch (e) {
    showToast(e.message)
  }
}
function openTime(r) {
  editing.value = r
  timeValue.value = ''
  showTime.value = true
}
async function confirmTime() {
  if (!timeValue.value) return
  try {
    await updateLeaveTime({ studentId: editing.value.studentId, batchId: batchId.value, planLeaveTime: timeValue.value })
    showSuccessToast('已修改')
  } catch (e) {
    showToast(e.message)
  }
}
onMounted(load)
</script>

<style scoped>
.block {
  margin-top: 12px;
}
</style>
