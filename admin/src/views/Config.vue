<template>
  <div>
    <el-row :gutter="16">
      <!-- 留校责任人 -->
      <el-col :span="12">
        <el-card shadow="never" class="card">
          <template #header>
            <div class="card-head">
              <span>留校责任人</span>
              <el-button type="primary" size="small" :icon="Plus" @click="mgrDialog = true">添加</el-button>
            </div>
          </template>
          <el-table :data="managers" v-loading="loading" border stripe>
            <el-table-column prop="name" label="姓名" min-width="100" />
            <el-table-column label="电话" min-width="140">
              <template #default="{ row }">
                <el-link v-if="row.phone" type="primary" :href="`tel:${row.phone}`">{{ row.phone }}</el-link>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column label="是否在校" width="100">
              <template #default="{ row }">
                <el-tag :type="row.onCampus ? 'success' : 'info'">{{ row.onCampus ? '在校' : '不在校' }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <!-- 集中住宿地址 -->
      <el-col :span="12">
        <el-card shadow="never" class="card">
          <template #header>
            <div class="card-head">
              <span>集中住宿地址</span>
              <el-button type="primary" size="small" :icon="Plus" @click="dormDialog = true">添加</el-button>
            </div>
          </template>
          <el-table :data="dorms" v-loading="loading" border stripe>
            <el-table-column label="校区" width="90">
              <template #default="{ row }">{{ row.campus ? row.campus + ' 区' : '-' }}</template>
            </el-table-column>
            <el-table-column prop="building" label="楼栋" min-width="120" />
            <el-table-column prop="address" label="地址" min-width="180" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 添加责任人 -->
    <el-dialog v-model="mgrDialog" title="添加留校责任人" width="420px">
      <el-form :model="mgrForm" label-width="90px">
        <el-form-item label="姓名" required>
          <el-input v-model="mgrForm.name" placeholder="责任人姓名" />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="mgrForm.phone" placeholder="联系电话" />
        </el-form-item>
        <el-form-item label="是否在校">
          <el-switch v-model="mgrForm.onCampus" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="mgrDialog = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="addMgr">保存</el-button>
      </template>
    </el-dialog>

    <!-- 添加集中住宿地址 -->
    <el-dialog v-model="dormDialog" title="添加集中住宿地址" width="420px">
      <el-form :model="dormForm" label-width="90px">
        <el-form-item label="校区">
          <el-radio-group v-model="dormForm.campus">
            <el-radio value="A">A 校区</el-radio>
            <el-radio value="B">B 校区</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="楼栋">
          <el-input v-model="dormForm.building" placeholder="楼栋（选填）" />
        </el-form-item>
        <el-form-item label="地址" required>
          <el-input v-model="dormForm.address" placeholder="集中住宿地址" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dormDialog = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="addDorm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { listManagers, addManager, listCentralDorms, addCentralDorm } from '../api/manage'

const managers = ref([])
const dorms = ref([])
const loading = ref(false)
const saving = ref(false)

const mgrDialog = ref(false)
const dormDialog = ref(false)
const mgrForm = reactive({ name: '', phone: '', onCampus: 1 })
const dormForm = reactive({ campus: 'A', building: '', address: '' })

async function load() {
  loading.value = true
  try {
    const [m, d] = await Promise.all([listManagers(), listCentralDorms()])
    managers.value = m
    dorms.value = d
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    loading.value = false
  }
}

async function addMgr() {
  if (!mgrForm.name) return ElMessage.warning('请填写姓名')
  saving.value = true
  try {
    await addManager({ ...mgrForm })
    ElMessage.success('已添加')
    mgrDialog.value = false
    mgrForm.name = ''
    mgrForm.phone = ''
    mgrForm.onCampus = 1
    load()
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    saving.value = false
  }
}

async function addDorm() {
  if (!dormForm.address) return ElMessage.warning('请填写地址')
  saving.value = true
  try {
    await addCentralDorm({ ...dormForm })
    ElMessage.success('已添加')
    dormDialog.value = false
    dormForm.building = ''
    dormForm.address = ''
    load()
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    saving.value = false
  }
}

onMounted(load)
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
