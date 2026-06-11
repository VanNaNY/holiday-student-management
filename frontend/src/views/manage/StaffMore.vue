<template>
  <div class="page">
    <van-nav-bar title="更多功能" left-arrow @click-left="$router.back()" />

    <van-cell-group inset title="审批与统计" class="block">
      <van-cell title="假期审批" is-link icon="audit" to="/approval" />
      <van-cell title="审批记录" is-link icon="records" to="/approval/records" />
      <van-cell title="批次统计" is-link icon="bar-chart-o" to="/stat" />
      <van-cell title="留校签到汇总" is-link icon="location-o" to="/checkin/summary" />
    </van-cell-group>

    <van-cell-group inset title="假期管理" class="block">
      <van-cell title="假期未登记（催办）" is-link icon="warning-o" to="/manage/unregistered" />
      <van-cell title="帮助重置登记" is-link icon="replay" to="/manage/help-reset" />
      <van-cell title="留校责任人添加" is-link icon="friends-o" to="/manage/managers" />
      <van-cell title="集中住宿地址添加" is-link icon="hotel-o" to="/manage/central-dorms" />
    </van-cell-group>

    <van-cell-group inset title="账号" class="block" v-if="multiRole">
      <van-cell title="角色切换" is-link icon="exchange" @click="showRole = true" />
    </van-cell-group>

    <van-action-sheet v-model:show="showRole" :actions="roleActions" cancel-text="取消"
                      title="切换角色" @select="onSwitch" />
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { showSuccessToast, showToast } from 'vant'
import { useUserStore } from '../../store/user'

const userStore = useUserStore()
const showRole = ref(false)

const ROLE = { STUDENT: '学生', COUNSELOR: '辅导员', SECRETARY: '副书记', ADMIN: '管理员' }
const multiRole = computed(() => (userStore.user?.roles?.length || 0) > 1)
const roleActions = computed(() =>
  (userStore.user?.roles || [])
    .filter((r) => r !== userStore.user?.activeRole)
    .map((r) => ({ name: ROLE[r] || r, role: r }))
)

async function onSwitch(action) {
  showRole.value = false
  try {
    await userStore.switchRole(action.role)
    showSuccessToast('已切换为' + (ROLE[action.role] || action.role))
  } catch (e) {
    showToast(e.message)
  }
}
</script>

<style scoped>
.block {
  margin-top: 12px;
}
</style>
