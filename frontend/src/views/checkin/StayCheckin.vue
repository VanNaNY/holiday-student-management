<template>
  <div class="page">
    <van-nav-bar title="留校签到" left-arrow @click-left="$router.back()" />

    <van-cell-group inset title="签到规则" class="block">
      <template v-if="rule">
        <van-cell title="签到范围" :value="`半径 ${rule.fenceRadius || '-'} 米`" />
        <van-cell title="时段" :value="`${fmt(rule.timeStart)} ~ ${fmt(rule.timeEnd)}`" />
        <van-cell title="围栏中心" :value="`${rule.fenceLat}, ${rule.fenceLng}`" />
      </template>
      <van-cell v-else title="签到规则" value="未配置" />
    </van-cell-group>

    <div class="map-placeholder">
      <van-icon name="location-o" size="32" />
      <p>地图展示需配置高德地图 Key</p>
      <p v-if="result" :class="result.success ? 'ok' : 'fail'">{{ result.message }}</p>
    </div>

    <div style="margin: 16px">
      <van-button round block type="primary" :loading="loading" @click="doCheckin(false)">
        定位并签到
      </van-button>
      <van-button round block plain type="primary" style="margin-top: 10px" :loading="loading"
                  @click="doCheckin(true)">
        模拟校内定位签到（演示）
      </van-button>
    </div>

    <van-cell-group inset title="我的签到记录" class="block">
      <van-empty v-if="!records.length" description="暂无签到记录" image-size="50" />
      <van-cell v-for="r in records" :key="r.id" :title="r.checkinDate"
                :value="r.status === 'DONE' ? '已签到' : '未签到'" :label="fmt(r.checkinTime)" />
    </van-cell-group>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { showSuccessToast, showToast } from 'vant'
import { currentBatch } from '../../api/auth'
import { checkinRule, stayCheckin, myStayCheckins } from '../../api/checkin'
import { getLocation } from '../../utils/geo'

const batchId = ref(null)
const rule = ref(null)
const records = ref([])
const loading = ref(false)
const result = ref(null)

const fmt = (s) => (s ? s.replace('T', ' ').slice(0, 16) : '-')

async function load() {
  const b = await currentBatch()
  if (!b) return showToast('暂无生效批次')
  batchId.value = b.id
  rule.value = await checkinRule(b.id, 'STAY')
  records.value = await myStayCheckins(b.id)
}

async function doCheckin(simulate) {
  if (!batchId.value) return showToast('暂无生效批次')
  loading.value = true
  result.value = null
  try {
    let loc
    if (simulate && rule.value?.fenceLat) {
      loc = { lat: Number(rule.value.fenceLat), lng: Number(rule.value.fenceLng) }
    } else {
      loc = await getLocation()
    }
    const res = await stayCheckin({ batchId: batchId.value, lat: loc.lat, lng: loc.lng })
    result.value = res
    if (res.success) {
      showSuccessToast('签到成功')
      records.value = await myStayCheckins(batchId.value)
    } else {
      showToast(res.message)
    }
  } catch (e) {
    showToast(e.message)
  } finally {
    loading.value = false
  }
}

onMounted(() => load().catch((e) => showToast(e.message)))
</script>

<style scoped>
.block {
  margin-top: 12px;
}
.map-placeholder {
  margin: 12px 16px;
  padding: 24px;
  background: #fff;
  border-radius: 8px;
  text-align: center;
  color: #969799;
}
.map-placeholder .ok {
  color: #07c160;
  font-weight: 600;
}
.map-placeholder .fail {
  color: #ee0a24;
  font-weight: 600;
}
</style>
