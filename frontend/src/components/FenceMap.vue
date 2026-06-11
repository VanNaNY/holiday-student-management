<template>
  <div class="fence-map">
    <div v-show="ready" ref="mapEl" class="map"></div>
    <div v-if="!ready" class="map-fallback">
      <van-icon name="location-o" size="32" />
      <p>{{ errorMsg || '地图加载中…' }}</p>
      <p v-if="errorMsg" class="tip">（地图不影响签到，可直接点下方按钮定位签到）</p>
    </div>
  </div>
</template>

<script setup>
import { onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { loadAMap } from '../utils/amap'

const props = defineProps({
  // 围栏中心 { lat, lng }
  center: { type: Object, default: null },
  // 围栏半径（米）
  radius: { type: Number, default: 0 },
  // 我的定位 { lat, lng }
  mine: { type: Object, default: null }
})

const mapEl = ref(null)
const ready = ref(false)
const errorMsg = ref('')

let map = null
let AMapRef = null
let mineMarker = null

async function init() {
  if (!props.center || props.center.lat == null) return
  try {
    const AMap = await loadAMap()
    AMapRef = AMap
    const center = [Number(props.center.lng), Number(props.center.lat)]
    map = new AMap.Map(mapEl.value, { zoom: 16, center, resizeEnable: true })
    map.add(new AMap.Marker({ position: center, title: '签到中心' }))
    if (props.radius) {
      map.add(new AMap.Circle({
        center,
        radius: Number(props.radius),
        strokeColor: '#1989fa',
        strokeWeight: 2,
        fillColor: '#1989fa',
        fillOpacity: 0.15
      }))
    }
    ready.value = true
    drawMine()
  } catch (e) {
    errorMsg.value = e.message
  }
}

function drawMine() {
  if (!map || !AMapRef || !props.mine || props.mine.lat == null) return
  const pos = [Number(props.mine.lng), Number(props.mine.lat)]
  if (mineMarker) {
    mineMarker.setPosition(pos)
  } else {
    mineMarker = new AMapRef.Marker({
      position: pos,
      title: '我的位置',
      icon: new AMapRef.Icon({
        size: new AMapRef.Size(25, 34),
        image: '//webapi.amap.com/theme/v1.3/markers/n/mark_r.png',
        imageSize: new AMapRef.Size(25, 34)
      })
    })
    map.add(mineMarker)
  }
  map.setFitView()
}

watch(() => props.mine, drawMine, { deep: true })
watch(() => props.center, init, { deep: true })

onMounted(init)
onBeforeUnmount(() => {
  if (map) {
    map.destroy()
    map = null
  }
})
</script>

<style scoped>
.fence-map {
  margin: 12px 16px;
}
.map {
  width: 100%;
  height: 220px;
  border-radius: 8px;
  overflow: hidden;
}
.map-fallback {
  padding: 24px;
  background: #fff;
  border-radius: 8px;
  text-align: center;
  color: #969799;
}
.map-fallback .tip {
  font-size: 12px;
  margin-top: 4px;
}
</style>
