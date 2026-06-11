<template>
  <div class="home">
    <van-nav-bar :title="roleLabel(activeRole) + '工作台'" />

    <!-- 用户信息 -->
    <van-cell-group inset class="block">
      <van-cell center :title="user?.name || '-'" :label="'学号/工号：' + (user?.loginName || '-')">
        <template #icon>
          <van-icon name="manager" size="28" style="margin-right: 10px; color: #1989fa" />
        </template>
        <template #value>
          <van-tag type="primary">{{ roleLabel(activeRole) }}</van-tag>
        </template>
      </van-cell>
      <van-cell
        v-if="(user?.roles?.length || 0) > 1"
        title="切换角色"
        is-link
        @click="showRoleSheet = true"
      />
    </van-cell-group>

    <!-- 当前批次 -->
    <van-cell-group inset class="block" title="当前假期批次">
      <template v-if="batch">
        <van-cell title="批次" :value="batch.name" />
        <van-cell title="假期" :value="`${batch.holidayStart} ~ ${batch.holidayEnd}`" />
        <van-cell title="状态">
          <template #value><van-tag type="success">进行中</van-tag></template>
        </van-cell>
      </template>
      <van-empty v-else description="暂无生效批次" image-size="60" />
    </van-cell-group>

    <!-- 功能入口（按角色） -->
    <van-grid :column-num="3" :border="false" class="block grid">
      <van-grid-item
        v-for="f in features"
        :key="f.key"
        :icon="f.icon"
        :text="f.text"
        @click="onFeature(f)"
      />
    </van-grid>

    <div style="margin: 24px 16px">
      <van-button round block type="danger" plain @click="onLogout">退出登录</van-button>
    </div>

    <!-- 角色切换面板 -->
    <van-action-sheet
      v-model:show="showRoleSheet"
      :actions="roleActions"
      cancel-text="取消"
      title="切换角色"
      @select="onSwitchRole"
    />

    <!-- 底部 tabbar（按角色） -->
    <van-tabbar v-model="tab" @change="onTabChange">
      <van-tabbar-item v-for="t in tabs" :key="t.key" :icon="t.icon">{{ t.text }}</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showSuccessToast, showConfirmDialog } from 'vant'
import { useUserStore } from '../store/user'
import { currentBatch } from '../api/auth'

const router = useRouter()
const userStore = useUserStore()
const user = computed(() => userStore.user)
const activeRole = computed(() => userStore.user?.activeRole || 'STUDENT')

const batch = ref(null)
const showRoleSheet = ref(false)
const tab = ref(0)

const ROLE_LABEL = { STUDENT: '学生', COUNSELOR: '辅导员', SECRETARY: '副书记', ADMIN: '管理员' }
const roleLabel = (r) => ROLE_LABEL[r] || r

const isStaff = computed(() => ['COUNSELOR', 'SECRETARY'].includes(activeRole.value))

// 角色切换可选项（排除当前角色）
const roleActions = computed(() =>
  (user.value?.roles || [])
    .filter((r) => r !== activeRole.value)
    .map((r) => ({ name: roleLabel(r), role: r }))
)

// 功能入口（按角色）—— Phase 1 为占位，后续阶段接入
const STUDENT_FEATURES = [
  { key: 'leave', icon: 'logistics', text: '离校登记' },
  { key: 'stay', icon: 'completed', text: '留校申请' },
  { key: 'return', icon: 'back-top', text: '返校登记' },
  { key: 'checkin', icon: 'location-o', text: '留校签到' },
  { key: 'records', icon: 'records', text: '我的记录' },
  { key: 'profile', icon: 'contact', text: '个人信息' }
]
const STAFF_FEATURES = [
  { key: 'approval', icon: 'audit', text: '假期审批' },
  { key: 'stat', icon: 'bar-chart-o', text: '批次统计' },
  { key: 'checkinSum', icon: 'location-o', text: '签到汇总' },
  { key: 'unreg', icon: 'warning-o', text: '假期未登记' },
  { key: 'reset', icon: 'replay', text: '帮助重置' },
  { key: 'more', icon: 'apps-o', text: '更多功能' }
]
const features = computed(() => (isStaff.value ? STAFF_FEATURES : STUDENT_FEATURES))

const STUDENT_TABS = [
  { key: 'batch', icon: 'calendar-o', text: '假期批次' },
  { key: 'checkin', icon: 'location-o', text: '留校签到' },
  { key: 'more', icon: 'apps-o', text: '更多' }
]
const STAFF_TABS = [
  { key: 'batch', icon: 'calendar-o', text: '假期批次' },
  { key: 'approval', icon: 'audit', text: '假期审批' },
  { key: 'checkin', icon: 'location-o', text: '留校签到' },
  { key: 'more', icon: 'apps-o', text: '更多' }
]
const tabs = computed(() => (isStaff.value ? STAFF_TABS : STUDENT_TABS))

function onFeature(f) {
  showToast(`「${f.text}」将在后续阶段开放`)
}
function onTabChange(i) {
  if (i !== 0) showToast(`「${tabs.value[i].text}」将在后续阶段开放`)
}

async function onSwitchRole(action) {
  showRoleSheet.value = false
  try {
    await userStore.switchRole(action.role)
    showSuccessToast('已切换为' + roleLabel(action.role))
  } catch (e) {
    showToast(e.message)
  }
}

async function onLogout() {
  await showConfirmDialog({ title: '提示', message: '确认退出登录？' })
  userStore.logout()
  router.replace('/login')
}

onMounted(async () => {
  if (!userStore.user) {
    try {
      await userStore.fetchMe()
    } catch { /* 401 由拦截器跳转 */ }
  }
  try {
    batch.value = await currentBatch()
  } catch { /* ignore */ }
})
</script>

<style scoped>
.home {
  padding-bottom: 60px;
}
.block {
  margin-top: 12px;
}
.grid {
  padding: 8px 0;
}
</style>
