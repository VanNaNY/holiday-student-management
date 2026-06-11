<template>
  <div class="page">
    <van-nav-bar title="返校登记" left-arrow @click-left="$router.back()" />
    <van-form @submit="onSubmit">
      <van-cell-group inset title="出发地信息">
        <van-field v-model="form.departProvince" label="省" placeholder="省" />
        <van-field v-model="form.departCity" label="市" placeholder="市" />
        <van-field v-model="form.departDistrict" label="区/县" placeholder="区/县" />
        <van-field v-model="form.departAddress" label="详细地址" placeholder="详细地址" />
      </van-cell-group>

      <van-cell-group inset title="计划时间">
        <van-field v-model="form.planDepartTime" label="计划出发" placeholder="YYYY-MM-DDTHH:mm:ss" />
        <van-field v-model="form.planArriveTime" label="计划返校" placeholder="YYYY-MM-DDTHH:mm:ss" />
      </van-cell-group>

      <TripsEditor v-model="form.trips" />

      <div style="margin: 16px">
        <van-button round block type="primary" native-type="submit" :loading="loading">提交返校登记</van-button>
      </div>
    </van-form>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { showSuccessToast, showToast } from 'vant'
import TripsEditor from '../../components/TripsEditor.vue'
import { currentBatch } from '../../api/auth'
import { submitReturn, myReturn } from '../../api/registration'

const router = useRouter()
const loading = ref(false)
const batchId = ref(null)
const form = reactive({
  departProvince: '', departCity: '', departDistrict: '', departAddress: '',
  planDepartTime: '', planArriveTime: '', trips: []
})

onMounted(async () => {
  try {
    const b = await currentBatch()
    if (!b) return showToast('暂无生效批次')
    batchId.value = b.id
    const exist = await myReturn(b.id)
    if (exist?.registration) {
      Object.assign(form, {
        departProvince: exist.registration.departProvince || '',
        departCity: exist.registration.departCity || '',
        departDistrict: exist.registration.departDistrict || '',
        departAddress: exist.registration.departAddress || '',
        planDepartTime: exist.registration.planDepartTime || '',
        planArriveTime: exist.registration.planArriveTime || '',
        trips: (exist.trips || []).map((t) => ({
          transport: t.transport, transportInfo: t.transportInfo,
          fromStation: t.fromStation, destStation: t.destStation,
          departTime: t.departTime, arriveTime: t.arriveTime
        }))
      })
    }
  } catch (e) {
    showToast(e.message)
  }
})

async function onSubmit() {
  if (!batchId.value) return showToast('暂无生效批次')
  loading.value = true
  try {
    await submitReturn({ batchId: batchId.value, ...form })
    showSuccessToast('返校登记成功')
    router.back()
  } catch (e) {
    showToast(e.message)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.page {
  padding-bottom: 24px;
}
</style>
