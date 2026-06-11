<template>
  <div class="page">
    <van-nav-bar title="留校签到汇总" left-arrow @click-left="$router.back()" />
    <van-empty v-if="!loading && !rows.length" description="暂无签到数据" />
    <van-cell-group inset class="block">
      <van-cell
        v-for="r in rows"
        :key="r.date"
        :title="r.date"
        is-link
        @click="$router.push(`/checkin/detail/${r.date}`)"
      >
        <template #value>
          <span :class="r.signed >= r.total && r.total > 0 ? 'full' : 'part'">
            {{ r.signed }} / {{ r.total }}
          </span>
        </template>
      </van-cell>
    </van-cell-group>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { showToast } from 'vant'
import { currentBatch } from '../../api/auth'
import { checkinSummary } from '../../api/checkin'

const rows = ref([])
const loading = ref(true)

async function load() {
  loading.value = true
  try {
    const b = await currentBatch()
    if (!b) return showToast('暂无生效批次')
    rows.value = await checkinSummary(b.id)
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
.full {
  color: #07c160;
  font-weight: 600;
}
.part {
  color: #ff976a;
  font-weight: 600;
}
</style>
