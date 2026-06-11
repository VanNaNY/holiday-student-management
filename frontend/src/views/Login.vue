<template>
  <div class="login">
    <van-nav-bar title="假期学生管理系统" />
    <div class="banner">假期「离校 / 留校 / 返校」管理</div>
    <van-form @submit="onSubmit">
      <van-cell-group inset>
        <van-field
          v-model="loginName"
          name="loginName"
          label="学号/工号"
          placeholder="请输入学号或工号"
          :rules="[{ required: true, message: '请填写学号/工号' }]"
        />
        <van-field
          v-model="password"
          type="password"
          name="password"
          label="密码"
          placeholder="请输入密码"
          :rules="[{ required: true, message: '请填写密码' }]"
        />
      </van-cell-group>
      <div style="margin: 16px">
        <van-button round block type="primary" native-type="submit" :loading="loading">
          登录
        </van-button>
      </div>
    </van-form>
    <div class="tip">测试账号：2023001 / T1001 / T2001，密码均为 123456</div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showSuccessToast } from 'vant'
import { useUserStore } from '../store/user'

const router = useRouter()
const userStore = useUserStore()
const loginName = ref('')
const password = ref('')
const loading = ref(false)

async function onSubmit() {
  loading.value = true
  try {
    const user = await userStore.login({ loginName: loginName.value, password: password.value })
    showSuccessToast(`欢迎，${user.name}`)
    router.replace('/home')
  } catch (e) {
    showToast(e.message)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.banner {
  margin: 24px 16px;
  font-size: 18px;
  font-weight: 600;
  color: #323233;
  text-align: center;
}
.tip {
  margin: 16px;
  color: #969799;
  font-size: 12px;
  text-align: center;
}
</style>
