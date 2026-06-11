<template>
  <div class="login">
    <van-nav-bar title="登录" />
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
        <van-button round block type="primary" native-type="submit">登录</van-button>
      </div>
    </van-form>
    <div class="tip">登录接口将在 Phase 1 接入（JWT + 角色路由）。</div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { showToast } from 'vant'

const loginName = ref('')
const password = ref('')

function onSubmit() {
  // Phase 1 接入 /auth/login
  showToast('登录功能将在 Phase 1 实现')
}
</script>

<style scoped>
.tip {
  margin: 16px;
  color: #969799;
  font-size: 13px;
  text-align: center;
}
</style>
