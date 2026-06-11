<template>
  <div class="page">
    <van-nav-bar title="批次统计" left-arrow @click-left="$router.back()" />
    <div v-if="data">
      <van-cell-group inset class="block">
        <van-cell title="管辖学生总数" :value="`${data.totalStudents} 人`" />
      </van-cell-group>

      <van-cell-group inset title="离校登记" class="block">
        <van-grid :column-num="2" :border="false">
          <van-grid-item v-for="(v, k) in data.leave" :key="k">
            <div class="stat"><div class="num">{{ v }}</div><div class="label">{{ regLabel(k) }}</div></div>
          </van-grid-item>
        </van-grid>
      </van-cell-group>

      <van-cell-group inset title="留校申请" class="block">
        <van-grid :column-num="2" :border="false">
          <van-grid-item v-for="(v, k) in data.stay" :key="k">
            <div class="stat"><div class="num">{{ v }}</div><div class="label">{{ stayLabel(k) }}</div></div>
          </van-grid-item>
        </van-grid>
      </van-cell-group>

      <van-cell-group inset title="返校登记" class="block">
        <van-grid :column-num="2" :border="false">
          <van-grid-item v-for="(v, k) in data.ret" :key="k">
            <div class="stat"><div class="num">{{ v }}</div><div class="label">{{ regLabel(k) }}</div></div>
          </van-grid-item>
        </van-grid>
      </van-cell-group>

      <div style="margin: 16px">
        <van-button round block type="primary" icon="down" @click="onExport">导出 Excel</van-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { showToast } from 'vant'
import { currentBatch } from '../../api/auth'
import { statOverview, exportStat } from '../../api/manage'

const data = ref(null)
const batchId = ref(null)

const REG = { NOT_REG: '未登记', REGISTERED: '已登记', ON_THE_WAY: '途中', ARRIVED: '已到达' }
const STAY = { PENDING: '待审批', APPROVED: '已通过', REJECTED: '已驳回', WITHDRAWN: '已撤回' }
const regLabel = (k) => REG[k] || k
const stayLabel = (k) => STAY[k] || k

async function load() {
  const b = await currentBatch()
  if (!b) return showToast('暂无生效批次')
  batchId.value = b.id
  data.value = await statOverview(b.id)
}
async function onExport() {
  try {
    const blob = await exportStat(batchId.value)
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = '假期统计.xlsx'
    a.click()
    URL.revokeObjectURL(url)
  } catch (e) {
    showToast(e.message)
  }
}
onMounted(() => load().catch((e) => showToast(e.message)))
</script>

<style scoped>
.block {
  margin-top: 12px;
}
.stat {
  text-align: center;
}
.stat .num {
  font-size: 22px;
  font-weight: 600;
  color: #1989fa;
}
.stat .label {
  font-size: 12px;
  color: #969799;
}
</style>
