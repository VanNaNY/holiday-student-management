<template>
  <div class="login-wrap">
    <el-card class="login-card">
      <h2 class="title">假期管理后台</h2>
      <el-form :model="form" @submit.prevent="onLogin">
        <el-form-item>
          <el-input v-model="form.loginName" placeholder="工号 / admin" :prefix-icon="User" />
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码"
            :prefix-icon="Lock"
            show-password
            @keyup.enter="onLogin"
          />
        </el-form-item>
        <el-button type="primary" style="width: 100%" :loading="loading" @click="onLogin">
          登录
        </el-button>
      </el-form>
      <p class="tip">测试账号：admin / 123456</p>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../store/user'

const router = useRouter()
const userStore = useUserStore()
const form = reactive({ loginName: '', password: '' })
const loading = ref(false)

async function onLogin() {
  if (!form.loginName || !form.password) {
    ElMessage.warning('请输入账号和密码')
    return
  }
  loading.value = true
  try {
    const user = await userStore.login({ ...form })
    ElMessage.success(`欢迎，${user.name}`)
    router.push('/dashboard')
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-wrap {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0f2f5;
}
.login-card {
  width: 360px;
}
.title {
  text-align: center;
  margin: 0 0 20px;
}
.tip {
  text-align: center;
  color: #909399;
  font-size: 12px;
}
</style>
