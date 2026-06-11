<template>
  <div>
    <div class="toolbar">
      <el-input v-model="query.keyword" placeholder="学号/姓名" clearable style="width: 200px"
                @keyup.enter="reload" />
      <el-select v-model="query.collegeId" placeholder="学院" clearable style="width: 200px"
                 @change="reload">
        <el-option v-for="c in colleges" :key="c.id" :label="c.name" :value="c.id" />
      </el-select>
      <el-button type="primary" :icon="Search" @click="reload">查询</el-button>
      <div style="flex: 1" />
      <el-button :icon="Plus" type="primary" @click="openCreate">新增学生</el-button>
      <el-button :icon="Download" @click="downloadTpl">下载模板</el-button>
      <el-upload :show-file-list="false" :before-upload="onImport" accept=".xlsx,.xls"
                 :auto-upload="true">
        <el-button :icon="Upload" :loading="importing">Excel 导入</el-button>
      </el-upload>
    </div>

    <el-table :data="rows" v-loading="loading" border stripe>
      <el-table-column prop="loginName" label="学号" width="120" />
      <el-table-column prop="name" label="姓名" width="100" />
      <el-table-column prop="phone" label="手机号" width="130" />
      <el-table-column prop="collegeName" label="学院" min-width="160" />
      <el-table-column prop="className" label="班级" min-width="120" />
      <el-table-column prop="dormAddress" label="宿舍" min-width="140" />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      style="margin-top: 12px; justify-content: flex-end"
      layout="total, prev, pager, next, sizes"
      :total="total"
      :page-size="query.size"
      :current-page="query.page"
      :page-sizes="[10, 20, 50]"
      @current-change="(p) => { query.page = p; load() }"
      @size-change="(s) => { query.size = s; reload() }"
    />

    <el-dialog v-model="dialog" :title="form.id ? '编辑学生' : '新增学生'" width="480px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="学号"><el-input v-model="form.loginName" :disabled="!!form.id" /></el-form-item>
        <el-form-item label="姓名"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="学院">
          <el-select v-model="form.collegeId" clearable @change="onCollegeChange" style="width: 100%">
            <el-option v-for="c in colleges" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="班级">
          <el-select v-model="form.classId" clearable style="width: 100%">
            <el-option v-for="c in classes" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="宿舍"><el-input v-model="form.dormAddress" /></el-form-item>
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
import { Plus, Search, Upload, Download } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  pageStudent, saveStudent, deleteStudent,
  listColleges, listClasses, importStudents, downloadTemplate
} from '../api/student'

const rows = ref([])
const total = ref(0)
const loading = ref(false)
const importing = ref(false)
const colleges = ref([])
const classes = ref([])
const dialog = ref(false)
const saving = ref(false)
const query = reactive({ page: 1, size: 10, keyword: '', collegeId: null })
const form = reactive({})

async function load() {
  loading.value = true
  try {
    const data = await pageStudent({
      page: query.page, size: query.size,
      keyword: query.keyword || undefined,
      collegeId: query.collegeId || undefined
    })
    rows.value = data.records
    total.value = data.total
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    loading.value = false
  }
}
function reload() {
  query.page = 1
  load()
}

async function loadColleges() {
  try { colleges.value = await listColleges() } catch { /* ignore */ }
}
async function onCollegeChange() {
  form.classId = null
  classes.value = form.collegeId ? await listClasses(form.collegeId) : []
}

function reset(row) {
  Object.keys(form).forEach((k) => delete form[k])
  Object.assign(form, {
    id: row?.id,
    loginName: row?.loginName || '',
    name: row?.name || '',
    phone: row?.phone || '',
    collegeId: row?.collegeId || null,
    classId: row?.classId || null,
    dormAddress: row?.dormAddress || ''
  })
}
async function openCreate() {
  reset(null)
  classes.value = []
  dialog.value = true
}
async function openEdit(row) {
  reset(row)
  classes.value = row.collegeId ? await listClasses(row.collegeId) : []
  dialog.value = true
}

async function save() {
  if (!form.loginName || !form.name) return ElMessage.warning('请填写学号和姓名')
  saving.value = true
  try {
    await saveStudent({
      loginName: form.loginName, name: form.name, phone: form.phone,
      collegeId: form.collegeId, classId: form.classId, dormAddress: form.dormAddress
    })
    ElMessage.success('保存成功')
    dialog.value = false
    load()
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    saving.value = false
  }
}

async function remove(row) {
  await ElMessageBox.confirm(`确认删除学生「${row.name}」？`, '提示', { type: 'warning' })
  try {
    await deleteStudent(row.id)
    ElMessage.success('已删除')
    load()
  } catch (e) {
    ElMessage.error(e.message)
  }
}

async function onImport(file) {
  importing.value = true
  try {
    const res = await importStudents(file)
    ElMessageBox.alert(
      `总计 ${res.total}，成功 ${res.success}，失败 ${res.failed}` +
        (res.errors?.length ? '<br/>' + res.errors.join('<br/>') : ''),
      '导入结果',
      { dangerouslyUseHTMLString: true }
    )
    reload()
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    importing.value = false
  }
  return false // 阻止 el-upload 默认上传
}

async function downloadTpl() {
  try {
    const blob = await downloadTemplate()
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = '学生导入模板.xlsx'
    a.click()
    URL.revokeObjectURL(url)
  } catch (e) {
    ElMessage.error(e.message)
  }
}

onMounted(() => {
  loadColleges()
  load()
})
</script>

<style scoped>
.toolbar {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
  align-items: center;
}
</style>
