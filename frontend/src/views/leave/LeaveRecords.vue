<template>
  <div class="page">
    <van-nav-bar title="离校登记记录" left-arrow @click-left="$router.back()" />
    <van-empty v-if="!loading && !rows.length" description="暂无离校登记记录" />
    <van-cell-group v-for="r in rows" :key="r.id" inset class="block">
      <van-cell title="目的地" :value="dest(r)" />
      <van-cell title="计划离校" :value="fmt(r.planLeaveTime)" />
      <van-cell title="状态">
        <template #value><van-tag :type="statusType(r.status)">{{ statusText(r.status) }}</van-tag></template>
      </van-cell>
      <van-cell v-if="r.status !== 'ARRIVED'">
        <template #title>
          <van-button size="small" type="primary" plain @click="mark(r, 'ON_THE_WAY')"
                      v-if="r.status === 'REGISTERED'">标记离校途中</van-button>
          <van-button size="small" type="success" plain style="margin-left: 8px"
                      @click="mark(r, 'ARRIVED')" v-if="r.status !== 'NOT_REG'">标记已到达</van-button>
        </template>
      </van-cell>
    </van-cell-group>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { showSuccessToast, showToast } from 'vant'
import { leaveRecords, updateLeaveStatus } from '../../api/registration'

const rows = ref([])
const loading = ref(true)

const STATUS = { NOT_REG: '未登记', REGISTERED: '已登记', ON_THE_WAY: '离校途中', ARRIVED: '已到目的地' }
const statusText = (s) => STATUS[s] || s
const statusType = (s) => ({ NOT_REG: 'default', REGISTERED: 'primary', ON_THE_WAY: 'warning', ARRIVED: 'success' }[s] || 'default')
const fmt = (s) => (s ? s.replace('T', ' ').slice(0, 16) : '-')
const dest = (r) => [r.destProvince, r.destCity, r.destDistrict, r.destAddress].filter(Boolean).join(' ') || '-'

async function load() {
  loading.value = true
  try {
    rows.value = await leaveRecords()
  } catch (e) {
    showToast(e.message)
  } finally {
    loading.value = false
  }
}
async function mark(r, status) {
  try {
    await updateLeaveStatus(r.id, status)
    showSuccessToast('已更新')
    load()
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
