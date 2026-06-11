<template>
  <div>
    <!-- 教职工列表 -->
    <el-card shadow="never" class="card">
      <template #header>
        <div class="card-head">
          <span>教职工列表</span>
          <div>
            <el-button type="primary" :icon="Plus" @click="openCreate">新增教职工</el-button>
            <el-button :icon="Refresh" @click="loadAll">刷新</el-button>
          </div>
        </div>
      </template>

      <el-table :data="staff" v-loading="loading" border stripe>
        <el-table-column prop="loginName" label="工号" width="120" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="phone" label="电话" width="140" />
        <el-table-column prop="collegeName" label="所属学院" min-width="180" />
        <el-table-column prop="title" label="职务" width="120" />
        <el-table-column label="角色" min-width="160">
          <template #default="{ row }">
            <el-tag v-for="r in (row.roles || [])" :key="r" size="small" style="margin-right: 4px">
              {{ roleText(r) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="remove(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 班级辅导员指派 -->
    <el-card shadow="never" class="card">
      <template #header>
        <div class="card-head"><span>班级辅导员指派</span></div>
      </template>
      <el-table :data="classes" v-loading="loading" border stripe>
        <el-table-column prop="name" label="班级" min-width="140" />
        <el-table-column prop="grade" label="年级" width="100" />
        <el-table-column prop="major" label="专业" min-width="160" />
        <el-table-column label="辅导员" min-width="220">
          <template #default="{ row }">
            <el-select
              v-model="row.counselorId"
              placeholder="未指派"
              clearable
              filterable
              style="width: 200px"
              @change="(v) => assignCounselor(row, v)"
            >
              <el-option
                v-for="s in counselorOptions"
                :key="s.id"
                :label="`${s.name}（${s.loginName}）`"
                :value="s.id"
              />
            </el-select>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑教职工 -->
    <el-dialog v-model="dialog" :title="form.id ? '编辑教职工' : '新增教职工'" width="520px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="工号" required>
          <el-input v-model="form.loginName" :disabled="!!form.id" placeholder="登录工号" />
        </el-form-item>
        <el-form-item label="姓名" required>
          <el-input v-model="form.name" placeholder="姓名" />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="form.phone" placeholder="联系电话" />
        </el-form-item>
        <el-form-item label="所属学院">
          <el-select v-model="form.collegeId" placeholder="选择学院" clearable style="width: 100%">
            <el-option v-for="c in colleges" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="职务">
          <el-input v-model="form.title" placeholder="如 辅导员 / 副书记" />
        </el-form-item>
        <el-form-item label="角色">
          <el-checkbox-group v-model="form.roles">
            <el-checkbox value="COUNSELOR">辅导员</el-checkbox>
            <el-checkbox value="SECRETARY">副书记</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <el-alert
        v-if="!form.id"
        title="新建账号默认密码为 123456，请提醒本人登录后修改。"
        type="info"
        :closable="false"
        style="margin-bottom: 8px"
      />
      <template #footer>
        <el-button @click="dialog = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { Plus, Refresh } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  listStaff, saveStaff, deleteStaff,
  listColleges, listClasses, updateClass
} from '../api/staff'

const ROLE = { STUDENT: '学生', COUNSELOR: '辅导员', SECRETARY: '副书记', ADMIN: '管理员' }
const roleText = (r) => ROLE[r] || r

const staff = ref([])
const classes = ref([])
const colleges = ref([])
const loading = ref(false)
const dialog = ref(false)
const saving = ref(false)
const form = reactive({ roles: [] })

// 可作为辅导员的教职工（含 COUNSELOR 角色）
const counselorOptions = computed(() =>
  staff.value.filter((s) => (s.roles || []).includes('COUNSELOR'))
)

async function loadAll() {
  loading.value = true
  try {
    const [s, c, col] = await Promise.all([listStaff(), listClasses(), listColleges()])
    staff.value = s
    classes.value = c
    colleges.value = col
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
    loginName: row?.loginName || '',
    name: row?.name || '',
    phone: row?.phone || '',
    collegeId: row?.collegeId ?? null,
    title: row?.title || '',
    roles: row ? (row.roles || []).filter((r) => r === 'COUNSELOR' || r === 'SECRETARY') : ['COUNSELOR']
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

async function save() {
  if (!form.loginName) return ElMessage.warning('请填写工号')
  if (!form.name) return ElMessage.warning('请填写姓名')
  saving.value = true
  try {
    await saveStaff({ ...form })
    ElMessage.success('保存成功')
    dialog.value = false
    loadAll()
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    saving.value = false
  }
}

async function remove(row) {
  await ElMessageBox.confirm(`确认删除教职工「${row.name}」？账号将保留但解除其辅导员/副书记角色。`, '提示', {
    type: 'warning'
  })
  try {
    await deleteStaff(row.id)
    ElMessage.success('已删除')
    loadAll()
  } catch (e) {
    ElMessage.error(e.message)
  }
}

async function assignCounselor(row, counselorId) {
  try {
    await updateClass(row.id, { counselorId: counselorId ?? null })
    ElMessage.success('辅导员已更新')
  } catch (e) {
    ElMessage.error(e.message)
    loadAll()
  }
}

onMounted(loadAll)
</script>

<style scoped>
.card {
  margin-bottom: 16px;
}
.card-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
</style>
