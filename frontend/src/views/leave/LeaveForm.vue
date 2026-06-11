<template>
  <div class="page">
    <van-nav-bar title="离校登记" left-arrow @click-left="$router.back()" />
    <van-form @submit="onSubmit">
      <van-cell-group inset title="目的地信息">
        <van-field v-model="form.destProvince" label="省" placeholder="省" />
        <van-field v-model="form.destCity" label="市" placeholder="市" />
        <van-field v-model="form.destDistrict" label="区/县" placeholder="区/县" />
        <van-field v-model="form.destAddress" label="详细地址" placeholder="详细地址" />
      </van-cell-group>

      <van-cell-group inset title="计划时间">
        <van-field v-model="form.planLeaveTime" label="计划离校" placeholder="YYYY-MM-DDTHH:mm:ss" />
        <van-field v-model="form.planArriveTime" label="计划到达" placeholder="YYYY-MM-DDTHH:mm:ss" />
      </van-cell-group>

      <van-cell-group inset title="紧急联系人">
        <van-field v-model="form.emergencyName" label="姓名" placeholder="紧急联系人" />
        <van-field v-model="form.emergencyPhone" label="电话" placeholder="联系电话" />
      </van-cell-group>

      <TripsEditor v-model="form.trips" />

      <van-cell-group inset>
        <van-field v-model="form.remark" label="备注" type="textarea" rows="2" placeholder="备注（选填）" />
      </van-cell-group>

      <div style="margin: 16px">
        <van-button round block type="primary" native-type="submit" :loading="loading">提交登记</van-button>
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
import { submitLeave, myLeave } from '../../api/registration'

const router = useRouter()
const loading = ref(false)
const batchId = ref(null)
const form = reactive({
  destProvince: '', destCity: '', destDistrict: '', destAddress: '',
  planLeaveTime: '', planArriveTime: '', emergencyName: '', emergencyPhone: '',
  remark: '', trips: []
})

onMounted(async () => {
  try {
    const b = await currentBatch()
    if (!b) return showToast('暂无生效批次')
    batchId.value = b.id
    const exist = await myLeave(b.id)
    if (exist?.registration) {
      Object.assign(form, {
        destProvince: exist.registration.destProvince || '',
        destCity: exist.registration.destCity || '',
        destDistrict: exist.registration.destDistrict || '',
        destAddress: exist.registration.destAddress || '',
        planLeaveTime: exist.registration.planLeaveTime || '',
        planArriveTime: exist.registration.planArriveTime || '',
        emergencyName: exist.registration.emergencyName || '',
        emergencyPhone: exist.registration.emergencyPhone || '',
        remark: exist.registration.remark || '',
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
    await submitLeave({ batchId: batchId.value, ...form })
    showSuccessToast('离校登记成功')
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
