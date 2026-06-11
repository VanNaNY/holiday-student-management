<template>
  <div class="page">
    <van-nav-bar title="留校申请记录" left-arrow @click-left="$router.back()" />
    <van-tabs v-model:active="active" @change="load">
      <van-tab title="全部" name="" />
      <van-tab title="待审批" name="PENDING" />
      <van-tab title="通过" name="APPROVED" />
      <van-tab title="不通过" name="REJECTED" />
    </van-tabs>
    <van-empty v-if="!loading && !rows.length" description="暂无申请记录" />
    <van-cell-group v-for="r in rows" :key="r.id" inset class="block" @click="$router.push(`/stay/${r.id}`)">
      <van-cell title="留校期间" :value="`${r.planStart || '-'} ~ ${r.planEnd || '-'}`" is-link />
      <van-cell title="校区" :value="r.campus ? r.campus + ' 校区' : '-'" />
      <van-cell title="状态">
        <template #value><van-tag :type="statusType(r.approvalStatus)">{{ statusText(r.approvalStatus) }}</van-tag></template>
      </van-cell>
    </van-cell-group>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { showToast } from 'vant'
import { stayRecords } from '../../api/registration'

const rows = ref([])
const loading = ref(true)
const active = ref('')

const STATUS = { PENDING: '待审批', APPROVED: '已通过', REJECTED: '已驳回', WITHDRAWN: '已撤回' }
const statusText = (s) => STATUS[s] || s
const statusType = (s) =>
  ({ PENDING: 'warning', APPROVED: 'success', REJECTED: 'danger', WITHDRAWN: 'default' }[s] || 'default')

async function load() {
  loading.value = true
  try {
    rows.value = await stayRecords(active.value || undefined)
  } catch (e) {
    showToast(e.message)
  } finally {
    loading.value = false
  }
}
onMounted(load)
</script>

<style scoped>
.block {
  margin-top: 12px;
}
</style>
