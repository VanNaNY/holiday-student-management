<template>
  <div class="home">
    <van-nav-bar title="假期学生管理系统" />
    <div class="content">
      <van-cell-group inset title="后端连通性自检">
        <van-cell title="状态" :value="status" />
        <van-cell title="应用" :value="app" />
      </van-cell-group>
      <div class="tip">移动端脚手架已就绪，后续按角色（学生 / 辅导员 / 副书记）填充页面。</div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../api/request'

const status = ref('检测中…')
const app = ref('-')

onMounted(async () => {
  try {
    const data = await request.get('/ping')
    status.value = data.status
    app.value = data.app
  } catch (e) {
    status.value = '连接失败：' + e.message
  }
})
</script>

<style scoped>
.content {
  padding-top: 12px;
}
.tip {
  margin: 16px;
  color: #969799;
  font-size: 13px;
  line-height: 1.6;
}
</style>
