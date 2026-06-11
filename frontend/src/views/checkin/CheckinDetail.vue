<template>
  <div class="page">
    <van-nav-bar :title="`签到详情 ${date}`" left-arrow @click-left="$router.back()" />
    <van-tabs v-model:active="active" @change="reload">
      <van-tab title="未签到" name="unsigned" />
      <van-tab title="已签到" name="signed" />
      <van-tab title="全部" name="all" />
    </van-tabs>

    <van-empty v-if="!loading && !rows.length" description="暂无数据" />
    <van-list v-model:loading="loading" :finished="finished" finished-text="没有更多了" @load="onLoad">
      <van-cell-group v-for="r in rows" :key="r.studentId" inset class="block">
        <van-cell :title="`${r.studentName} · ${r.studentNo}`">
          <template #value>
            <van-tag :type="r.signed ? 'success' : 'warning'">{{ r.signed ? '已签' : '未签' }}</van-tag>
          </template>
        </van-cell>
        <van-cell title="班级" :value="r.className || '-'" />
        <van-cell v-if="r.signed" title="签到时间" :value="fmt(r.checkinTime)" />
        <van-cell v-if="r.phone" title="联系电话" :value="r.phone" is-link
                  @click="call(r.phone)" />
      </van-cell-group>
    </van-list>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { showToast } from 'vant'
import { currentBatch } from '../../api/auth'
import { checkinDetail } from '../../api/checkin'

const route = useRoute()
const date = route.params.date
const active = ref('unsigned')
const rows = ref([])
const loading = ref(false)
const finished = ref(false)
const page = ref(1)
const size = 20
const batchId = ref(null)

const fmt = (s) => (s ? s.replace('T', ' ').slice(0, 16) : '-')
const signedParam = () => (active.value === 'all' ? undefined : active.value === 'signed')

function call(phone) {
  window.location.href = `tel:${phone}`
}

async function onLoad() {
  try {
    if (!batchId.value) {
      const b = await currentBatch()
      batchId.value = b?.id || null
    }
    const data = await checkinDetail({
      batchId: batchId.value,
      date,
      signed: signedParam(),
      page: page.value,
      size
    })
    rows.value.push(...data.records)
    if (rows.value.length >= data.total || data.records.length === 0) finished.value = true
    else page.value += 1
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
onMounted(reload)
</script>

<style scoped>
.block {
  margin-top: 12px;
}
</style>
