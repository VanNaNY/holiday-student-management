<template>
  <div class="page">
    <van-nav-bar title="留校申请" left-arrow @click-left="$router.back()" />
    <van-form @submit="onSubmit">
      <van-cell-group inset title="留校信息">
        <van-field v-model="form.planStart" label="开始日期" placeholder="YYYY-MM-DD" />
        <van-field v-model="form.planEnd" label="结束日期" placeholder="YYYY-MM-DD" />
        <van-field label="校区">
          <template #input>
            <van-radio-group v-model="form.campus" direction="horizontal">
              <van-radio name="A">A 校区</van-radio>
              <van-radio name="B">B 校区</van-radio>
            </van-radio-group>
          </template>
        </van-field>
        <van-field
          v-model="centralDormText"
          label="集中住宿"
          placeholder="请选择"
          readonly
          is-link
          @click="showDorm = true"
        />
        <van-field
          v-model="managerText"
          label="责任人"
          placeholder="请选择"
          readonly
          is-link
          @click="showManager = true"
        />
        <van-field v-model="form.originDorm" label="原宿舍" placeholder="原宿舍" />
        <van-field v-model="form.reason" label="留校事由" type="textarea" rows="2"
                   placeholder="请填写留校事由" :rules="[{ required: true, message: '请填写事由' }]" />
      </van-cell-group>

      <van-cell-group inset title="紧急联系人">
        <van-field v-model="form.emergencyName" label="姓名" placeholder="紧急联系人" />
        <van-field v-model="form.emergencyPhone" label="电话" placeholder="联系电话" />
      </van-cell-group>

      <van-cell-group inset title="附件（1~5 张，png/jpg）">
        <div style="padding: 12px 16px">
          <van-uploader
            v-model="fileList"
            :max-count="5"
            :after-read="afterRead"
            accept="image/png,image/jpeg"
          />
        </div>
      </van-cell-group>

      <div style="margin: 16px">
        <van-button round block type="primary" native-type="submit" :loading="loading">提交申请</van-button>
      </div>
    </van-form>

    <van-popup v-model:show="showDorm" position="bottom" round>
      <van-picker
        :columns="dormColumns"
        @confirm="onDorm"
        @cancel="showDorm = false"
      />
    </van-popup>
    <van-popup v-model:show="showManager" position="bottom" round>
      <van-picker
        :columns="managerColumns"
        @confirm="onManager"
        @cancel="showManager = false"
      />
    </van-popup>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { showSuccessToast, showToast } from 'vant'
import { currentBatch } from '../../api/auth'
import { submitStay, stayCentralDorms, stayManagers, uploadFile } from '../../api/registration'

const router = useRouter()
const loading = ref(false)
const batchId = ref(null)
const form = reactive({
  planStart: '', planEnd: '', campus: 'A', reason: '',
  emergencyName: '', emergencyPhone: '', originDorm: '',
  centralDormId: null, managerId: null, attachments: []
})

const fileList = ref([])
const dorms = ref([])
const managers = ref([])
const showDorm = ref(false)
const showManager = ref(false)
const centralDormText = ref('')
const managerText = ref('')

const dormColumns = computed(() => dorms.value.map((d) => ({ text: d.address, value: d.id })))
const managerColumns = computed(() =>
  managers.value.map((m) => ({ text: `${m.name}（${m.phone || '无电话'}）`, value: m.id }))
)

onMounted(async () => {
  try {
    const b = await currentBatch()
    if (!b) return showToast('暂无生效批次')
    batchId.value = b.id
    dorms.value = await stayCentralDorms()
    managers.value = await stayManagers()
  } catch (e) {
    showToast(e.message)
  }
})

function onDorm({ selectedOptions }) {
  form.centralDormId = selectedOptions[0].value
  centralDormText.value = selectedOptions[0].text
  showDorm.value = false
}
function onManager({ selectedOptions }) {
  form.managerId = selectedOptions[0].value
  managerText.value = selectedOptions[0].text
  showManager.value = false
}

async function afterRead(item) {
  const items = Array.isArray(item) ? item : [item]
  for (const it of items) {
    it.status = 'uploading'
    it.message = '上传中'
    try {
      const data = await uploadFile(it.file)
      it.url = data.url
      it.status = 'done'
      it.message = ''
      form.attachments.push(data.url)
    } catch (e) {
      it.status = 'failed'
      it.message = '上传失败'
      showToast(e.message)
    }
  }
}

async function onSubmit() {
  if (!batchId.value) return showToast('暂无生效批次')
  if (!form.reason) return showToast('请填写留校事由')
  loading.value = true
  try {
    await submitStay({ batchId: batchId.value, ...form })
    showSuccessToast('留校申请已提交')
    router.replace('/stay/records')
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
