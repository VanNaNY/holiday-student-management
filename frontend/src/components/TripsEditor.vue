<template>
  <div>
    <van-cell-group inset :title="`行程信息（${list.length}/10 段）`">
      <div v-for="(t, i) in list" :key="i" class="trip">
        <van-cell :title="`第 ${i + 1} 段`">
          <template #value>
            <van-icon name="cross" @click="remove(i)" />
          </template>
        </van-cell>
        <van-field v-model="t.transport" label="交通方式" placeholder="火车/飞机/汽车/自驾" />
        <van-field v-model="t.transportInfo" label="车次/航班" placeholder="如 G123" />
        <van-field v-model="t.fromStation" label="出发站" placeholder="出发站点" />
        <van-field v-model="t.destStation" label="到达站" placeholder="到达站点" />
        <van-field
          v-model="t.departTime"
          label="出发时间"
          placeholder="YYYY-MM-DD HH:mm"
          readonly
          is-link
          @click="openPicker(i, 'departTime')"
        />
        <van-field
          v-model="t.arriveTime"
          label="到达时间"
          placeholder="YYYY-MM-DD HH:mm"
          readonly
          is-link
          @click="openPicker(i, 'arriveTime')"
        />
      </div>
      <div style="padding: 10px 16px">
        <van-button size="small" icon="plus" block plain type="primary" @click="add" :disabled="list.length >= 10">
          添加行程段
        </van-button>
      </div>
    </van-cell-group>

    <van-popup v-model:show="showPicker" position="bottom" round>
      <van-date-picker
        v-model="pickerValue"
        title="选择日期"
        :columns-type="['year', 'month', 'day']"
        @confirm="onPickDate"
        @cancel="showPicker = false"
      />
    </van-popup>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({ modelValue: { type: Array, default: () => [] } })
const emit = defineEmits(['update:modelValue'])

const list = ref(props.modelValue.length ? [...props.modelValue] : [])
const showPicker = ref(false)
const pickerValue = ref(['2026', '07', '10'])
let editing = { i: 0, field: 'departTime' }

function sync() {
  emit('update:modelValue', list.value)
}
function add() {
  if (list.value.length >= 10) return
  list.value.push({ transport: '', transportInfo: '', fromStation: '', destStation: '', departTime: '', arriveTime: '' })
  sync()
}
function remove(i) {
  list.value.splice(i, 1)
  sync()
}
function openPicker(i, field) {
  editing = { i, field }
  showPicker.value = true
}
function onPickDate({ selectedValues }) {
  const [y, m, d] = selectedValues
  // 简化为日期 + 默认 08:00；后端按 LocalDateTime 解析
  list.value[editing.i][editing.field] = `${y}-${m}-${d}T08:00:00`
  showPicker.value = false
  sync()
}
</script>

<style scoped>
.trip {
  border-bottom: 8px solid #f7f8fa;
}
</style>
