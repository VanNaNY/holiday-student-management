<template>
  <div class="login-wrap">
    <el-card class="login-card">
      <h2 class="title">假期管理后台</h2>
      <el-form :model="form" label-width="0">
        <el-form-item>
          <el-input v-model="form.loginName" placeholder="工号" :prefix-icon="User" />
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <el-button type="primary" style="width: 100%" @click="onLogin">登录</el-button>
      </el-form>
      <p class="tip">登录接口将在 Phase 1 接入。</p>
    </el-card>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { User, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const form = reactive({ loginName: '', password: '' })

function onLogin() {
  ElMessage.info('登录功能将在 Phase 1 实现')
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
