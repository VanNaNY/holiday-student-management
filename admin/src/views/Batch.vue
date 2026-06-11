<template>
  <div>
    <div class="toolbar">
      <el-button type="primary" :icon="Plus" @click="openCreate">新建批次</el-button>
      <el-button :icon="Refresh" @click="load">刷新</el-button>
    </div>

    <el-table :data="rows" v-loading="loading" border stripe>
      <el-table-column prop="name" label="批次名称" min-width="140" />
      <el-table-column label="假期起止" min-width="180">
        <template #default="{ row }">{{ row.holidayStart }} ~ {{ row.holidayEnd }}</template>
      </el-table-column>
      <el-table-column label="离校登记窗" min-width="200">
        <template #default="{ row }">{{ fmt(row.leaveOpenStart) }} ~ {{ fmt(row.leaveOpenEnd) }}</template>
      </el-table-column>
      <el-table-column label="状态" width="120">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
          <el-dropdown @command="(s) => changeStatus(row, s)" style="margin: 0 8px">
            <el-button size="small">状态<el-icon><ArrowDown /></el-icon></el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="DRAFT">草稿</el-dropdown-item>
                <el-dropdown-item command="ACTIVE">生效</el-dropdown-item>
                <el-dropdown-item command="CLOSED">关闭</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <el-button size="small" type="danger" @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialog" :title="form.id ? '编辑批次' : '新建批次'" width="640px">
      <el-form :model="form" label-width="120px">
        <el-form-item label="批次名称">
          <el-input v-model="form.name" placeholder="如 2026 暑假" />
        </el-form-item>
        <el-form-item label="假期起止">
          <el-date-picker v-model="form.holidayRange" type="daterange" value-format="YYYY-MM-DD"
                          range-separator="至" start-placeholder="开始" end-placeholder="结束" />
        </el-form-item>
        <el-form-item label="离校登记窗">
          <el-date-picker v-model="form.leaveRange" type="datetimerange" value-format="YYYY-MM-DDTHH:mm:ss"
                          range-separator="至" start-placeholder="开始" end-placeholder="结束" />
        </el-form-item>
        <el-form-item label="留校申请窗">
          <el-date-picker v-model="form.stayRange" type="datetimerange" value-format="YYYY-MM-DDTHH:mm:ss"
                          range-separator="至" start-placeholder="开始" end-placeholder="结束" />
        </el-form-item>
        <el-form-item label="返校登记窗">
          <el-date-picker v-model="form.returnRange" type="datetimerange" value-format="YYYY-MM-DDTHH:mm:ss"
                          range-separator="至" start-placeholder="开始" end-placeholder="结束" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status">
            <el-option label="草稿" value="DRAFT" />
            <el-option label="生效" value="ACTIVE" />
            <el-option label="关闭" value="CLOSED" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { Plus, Refresh, ArrowDown } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listBatch, createBatch, updateBatch, changeBatchStatus, deleteBatch } from '../api/batch'

const rows = ref([])
const loading = ref(false)
const dialog = ref(false)
const saving = ref(false)
const form = reactive({})

const fmt = (s) => (s ? s.replace('T', ' ').slice(0, 16) : '-')
const statusText = (s) => ({ DRAFT: '草稿', ACTIVE: '生效', CLOSED: '关闭' }[s] || s)
const statusType = (s) => ({ DRAFT: 'info', ACTIVE: 'success', CLOSED: 'warning' }[s] || '')

async function load() {
  loading.value = true
  try {
    rows.value = await listBatch()
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    loading.value = false
  }
}

function reset(row) {
  Object.keys(form).forEach((k) => delete form[k])
  Object.assign(form, {
    id: row?.id,
    name: row?.name || '',
    status: row?.status || 'DRAFT',
    holidayRange: row ? [row.holidayStart, row.holidayEnd] : [],
    leaveRange: row ? [row.leaveOpenStart, row.leaveOpenEnd] : [],
    stayRange: row ? [row.stayOpenStart, row.stayOpenEnd] : [],
    returnRange: row ? [row.returnOpenStart, row.returnOpenEnd] : []
  })
}

function openCreate() {
  reset(null)
  dialog.value = true
}
function openEdit(row) {
  reset(row)
  dialog.value = true
}

function buildPayload() {
  const [hs, he] = form.holidayRange || []
  const [ls, le] = form.leaveRange || []
  const [ss, se] = form.stayRange || []
  const [rs, re] = form.returnRange || []
  return {
    name: form.name,
    status: form.status,
    holidayStart: hs, holidayEnd: he,
    leaveOpenStart: ls, leaveOpenEnd: le,
    stayOpenStart: ss, stayOpenEnd: se,
    returnOpenStart: rs, returnOpenEnd: re
  }
}

async function save() {
  if (!form.name) return ElMessage.warning('请填写批次名称')
  saving.value = true
  try {
    if (form.id) await updateBatch(form.id, buildPayload())
    else await createBatch(buildPayload())
    ElMessage.success('保存成功')
    dialog.value = false
    load()
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    saving.value = false
  }
}

async function changeStatus(row, status) {
  try {
    await changeBatchStatus(row.id, status)
    ElMessage.success('状态已更新')
    load()
  } catch (e) {
    ElMessage.error(e.message)
  }
}

async function remove(row) {
  await ElMessageBox.confirm(`确认删除批次「${row.name}」？`, '提示', { type: 'warning' })
  try {
    await deleteBatch(row.id)
    ElMessage.success('已删除')
    load()
  } catch (e) {
    ElMessage.error(e.message)
  }
}

onMounted(load)
</script>

<style scoped>
.toolbar {
  margin-bottom: 12px;
}
</style>
