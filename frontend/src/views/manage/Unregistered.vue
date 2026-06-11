<template>
  <div class="page">
    <van-nav-bar title="假期未登记" left-arrow @click-left="$router.back()" />
    <van-tabs v-model:active="type" @change="reload">
      <van-tab title="离校未登记" name="LEAVE" />
      <van-tab title="返校未登记" name="RETURN" />
    </van-tabs>
    <van-search v-model="keyword" placeholder="搜索学号/姓名" @search="reload" @clear="reload" />

    <van-empty v-if="!loading && !rows.length" description="没有未登记的学生" />
    <van-list v-model:loading="loading" :finished="finished" finished-text="没有更多了" @load="onLoad">
      <van-cell-group v-for="r in rows" :key="r.studentId" inset class="block">
        <van-cell :title="`${r.studentName} · ${r.studentNo}`" :label="r.className || ''" />
        <van-cell v-if="r.phone" title="联系电话" :value="r.phone" is-link icon="phone-o"
                  @click="call(r.phone)" />
        <van-cell v-else title="联系电话" value="未登记手机号" />
      </van-cell-group>
    </van-list>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { showToast } from 'vant'
import { currentBatch } from '../../api/auth'
import { unregistered } from '../../api/manage'

const rows = ref([])
const keyword = ref('')
const type = ref('LEAVE')
const loading = ref(false)
const finished = ref(false)
const page = ref(1)
const size = 20
const batchId = ref(null)

function call(phone) {
  window.location.href = `tel:${phone}`
}

async function onLoad() {
  try {
    if (!batchId.value) {
      const b = await currentBatch()
      batchId.value = b?.id || null
    }
    const data = await unregistered({
      batchId: batchId.value,
      type: type.value,
      keyword: keyword.value || undefined,
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
