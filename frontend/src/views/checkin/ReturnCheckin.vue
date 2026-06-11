<template>
  <div class="page">
    <van-nav-bar title="返校报到" left-arrow @click-left="$router.back()" />

    <van-cell-group inset title="报到规则" class="block">
      <template v-if="rule">
        <van-cell title="报到范围" :value="`半径 ${rule.fenceRadius || '-'} 米`" />
        <van-cell title="时段" :value="`${fmt(rule.timeStart)} ~ ${fmt(rule.timeEnd)}`" />
      </template>
      <van-cell v-else title="报到规则" value="未配置" />
    </van-cell-group>

    <van-cell-group inset class="block">
      <van-cell title="报到状态">
        <template #value>
          <van-tag :type="done ? 'success' : 'warning'">{{ done ? '已报到' : '未报到' }}</van-tag>
        </template>
      </van-cell>
      <van-cell v-if="done" title="报到时间" :value="fmt(my?.checkinTime)" />
    </van-cell-group>

    <div class="map-placeholder">
      <van-icon name="location-o" size="32" />
      <p>地图展示需配置高德地图 Key</p>
      <p v-if="result" :class="result.success ? 'ok' : 'fail'">{{ result.message }}</p>
    </div>

    <div style="margin: 16px">
      <van-button round block type="primary" :loading="loading" @click="doCheckin(false)">
        定位并报到
      </van-button>
      <van-button round block plain type="primary" style="margin-top: 10px" :loading="loading"
                  @click="doCheckin(true)">
        模拟校内定位报到（演示）
      </van-button>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { showSuccessToast, showToast } from 'vant'
import { currentBatch } from '../../api/auth'
import { checkinRule, returnCheckin, myReturnCheckin } from '../../api/checkin'
import { getLocation } from '../../utils/geo'

const batchId = ref(null)
const rule = ref(null)
const my = ref(null)
const loading = ref(false)
const result = ref(null)
const done = computed(() => my.value?.status === 'DONE')

const fmt = (s) => (s ? s.replace('T', ' ').slice(0, 16) : '-')

async function load() {
  const b = await currentBatch()
  if (!b) return showToast('暂无生效批次')
  batchId.value = b.id
  rule.value = await checkinRule(b.id, 'RETURN')
  my.value = await myReturnCheckin(b.id)
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
    const res = await returnCheckin({ batchId: batchId.value, lat: loc.lat, lng: loc.lng })
    result.value = res
    if (res.success) {
      showSuccessToast('报到成功')
      my.value = await myReturnCheckin(batchId.value)
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
