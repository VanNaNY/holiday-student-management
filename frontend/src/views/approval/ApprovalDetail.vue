<template>
  <div class="page" v-if="vo">
    <van-nav-bar title="留校审批详情" left-arrow @click-left="$router.back()" />

    <van-cell-group inset title="学生信息" class="block">
      <van-cell title="姓名" :value="vo.studentName || '-'" />
      <van-cell title="学号" :value="vo.studentNo || '-'" />
    </van-cell-group>

    <van-cell-group inset title="申请信息" class="block">
      <van-cell title="状态">
        <template #value>
          <van-tag :type="statusType(app.approvalStatus)">{{ statusText(app.approvalStatus) }}</van-tag>
        </template>
      </van-cell>
      <van-cell title="留校期间" :value="`${app.planStart || '-'} ~ ${app.planEnd || '-'}`" />
      <van-cell title="校区" :value="app.campus ? app.campus + ' 校区' : '-'" />
      <van-cell title="集中住宿" :value="vo.centralDormAddress || '-'" />
      <van-cell title="责任人" :value="vo.managerName || '-'" />
      <van-cell title="原宿舍" :value="app.originDorm || '-'" />
      <van-cell title="事由" :label="app.reason" />
      <van-cell title="紧急联系人" :value="`${app.emergencyName || '-'} ${app.emergencyPhone || ''}`" />
    </van-cell-group>

    <van-cell-group inset title="附件" class="block" v-if="vo.attachments?.length">
      <div style="padding: 12px 16px">
        <van-image v-for="(u, i) in vo.attachments" :key="i" width="80" height="80" :src="u"
                   fit="cover" style="margin: 4px" @click="preview(i)" />
      </div>
    </van-cell-group>

    <van-cell-group inset title="审批流程" class="block">
      <van-steps direction="vertical" :active="vo.approvalRecords.length - 1">
        <van-step v-for="rec in vo.approvalRecords" :key="rec.id">
          <h4>{{ actionText(rec.action) }}（{{ roleText(rec.approverRole) }}）</h4>
          <p>{{ fmt(rec.createTime) }}</p>
          <p v-if="rec.comment" class="comment">意见：{{ rec.comment }}</p>
        </van-step>
      </van-steps>
    </van-cell-group>

    <van-cell-group inset class="block" v-if="canAct">
      <van-field v-model="comment" label="审批意见" type="textarea" rows="2" placeholder="可填写审批意见（选填）" />
    </van-cell-group>

    <div class="actions" v-if="canAct">
      <van-button round type="danger" :loading="loading" @click="act('reject')">驳回</van-button>
      <van-button round type="success" :loading="loading" @click="act('approve')">通过</van-button>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showImagePreview, showSuccessToast, showToast } from 'vant'
import { approvalDetail, approve, reject } from '../../api/approval'

const route = useRoute()
const router = useRouter()
const vo = ref(null)
const comment = ref('')
const loading = ref(false)
const app = computed(() => vo.value?.application || {})
const canAct = computed(() => app.value.approvalStatus === 'PENDING')

const STATUS = { PENDING: '待审批', APPROVED: '已通过', REJECTED: '已驳回', WITHDRAWN: '已撤回' }
const statusText = (s) => STATUS[s] || s
const statusType = (s) =>
  ({ PENDING: 'warning', APPROVED: 'success', REJECTED: 'danger', WITHDRAWN: 'default' }[s] || 'default')
const ACTION = { SUBMIT: '提交申请', APPROVE: '通过', REJECT: '驳回', WITHDRAW: '撤回' }
const actionText = (a) => ACTION[a] || a
const ROLE = { STUDENT: '学生', COUNSELOR: '辅导员', SECRETARY: '副书记' }
const roleText = (r) => ROLE[r] || r
const fmt = (s) => (s ? s.replace('T', ' ').slice(0, 19) : '-')

async function load() {
  try {
    vo.value = await approvalDetail(route.params.id)
  } catch (e) {
    showToast(e.message)
  }
}
function preview(start) {
  showImagePreview({ images: vo.value.attachments, startPosition: start })
}
async function act(type) {
  loading.value = true
  try {
    if (type === 'approve') await approve(route.params.id, comment.value)
    else await reject(route.params.id, comment.value)
    showSuccessToast(type === 'approve' ? '已通过' : '已驳回')
    router.back()
  } catch (e) {
    showToast(e.message)
  } finally {
    loading.value = false
  }
}
onMounted(load)
</script>

<style scoped>
.block {
  margin-top: 12px;
}
.comment {
  color: #ee0a24;
}
.actions {
  display: flex;
  gap: 12px;
  margin: 16px;
}
.actions .van-button {
  flex: 1;
}
</style>
