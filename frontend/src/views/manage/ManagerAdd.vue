<template>
  <div class="page">
    <van-nav-bar title="留校责任人" left-arrow @click-left="$router.back()" />

    <van-cell-group inset title="新增责任人" class="block">
      <van-field v-model="form.name" label="姓名" placeholder="责任人姓名" />
      <van-field v-model="form.phone" label="电话" placeholder="联系电话" />
      <van-field label="是否在校">
        <template #input>
          <van-switch v-model="onCampus" />
        </template>
      </van-field>
      <div style="padding: 12px 16px">
        <van-button round block type="primary" :loading="loading" @click="onAdd">添加</van-button>
      </div>
    </van-cell-group>

    <van-cell-group inset title="已有责任人" class="block">
      <van-empty v-if="!list.length" description="暂无" image-size="50" />
      <van-cell v-for="m in list" :key="m.id" :title="m.name" :label="m.phone"
                :value="m.onCampus ? '在校' : '不在校'" />
    </van-cell-group>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { showSuccessToast, showToast } from 'vant'
import { listManagers, addManager } from '../../api/manage'

const form = reactive({ name: '', phone: '' })
const onCampus = ref(true)
const list = ref([])
const loading = ref(false)

async function load() {
  list.value = await listManagers()
}
async function onAdd() {
  if (!form.name) return showToast('请填写姓名')
  loading.value = true
  try {
    await addManager({ name: form.name, phone: form.phone, onCampus: onCampus.value ? 1 : 0 })
    showSuccessToast('已添加')
    form.name = ''
    form.phone = ''
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
