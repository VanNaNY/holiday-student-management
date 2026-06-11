<template>
  <div class="page">
    <van-nav-bar title="集中住宿地址" left-arrow @click-left="$router.back()" />

    <van-cell-group inset title="新增地址" class="block">
      <van-field label="校区">
        <template #input>
          <van-radio-group v-model="form.campus" direction="horizontal">
            <van-radio name="A">A 校区</van-radio>
            <van-radio name="B">B 校区</van-radio>
          </van-radio-group>
        </template>
      </van-field>
      <van-field v-model="form.building" label="楼栋" placeholder="楼栋（选填）" />
      <van-field v-model="form.address" label="地址" placeholder="集中住宿地址" />
      <div style="padding: 12px 16px">
        <van-button round block type="primary" :loading="loading" @click="onAdd">添加</van-button>
      </div>
    </van-cell-group>

    <van-cell-group inset title="已有地址" class="block">
      <van-empty v-if="!list.length" description="暂无" image-size="50" />
      <van-cell v-for="d in list" :key="d.id" :title="d.address"
                :label="d.building" :value="d.campus ? d.campus + '区' : ''" />
    </van-cell-group>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { showSuccessToast, showToast } from 'vant'
import { listCentralDorms, addCentralDorm } from '../../api/manage'

const form = reactive({ campus: 'A', building: '', address: '' })
const list = ref([])
const loading = ref(false)

async function load() {
  list.value = await listCentralDorms()
}
async function onAdd() {
  if (!form.address) return showToast('请填写地址')
  loading.value = true
  try {
    await addCentralDorm({ ...form })
    showSuccessToast('已添加')
    form.building = ''
    form.address = ''
    load()
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
</style>
