<template>
  <div class="page">
    <van-nav-bar title="更多功能" left-arrow @click-left="$router.back()" />
    <van-cell-group inset title="登记与申请" class="block">
      <van-cell title="离校登记" is-link icon="logistics" to="/leave" />
      <van-cell title="留校申请" is-link icon="completed" to="/stay" />
      <van-cell title="返校登记" is-link icon="back-top" to="/return" />
    </van-cell-group>
    <van-cell-group inset title="我的记录" class="block">
      <van-cell title="离校登记记录" is-link icon="records" to="/leave/records" />
      <van-cell title="留校申请记录" is-link icon="records" to="/stay/records" />
      <van-cell title="返校登记记录" is-link icon="records" to="/return/records" />
    </van-cell-group>
  </div>
</template>

<script setup>
// 更多功能聚合页
</script>

<style scoped>
.block {
  margin-top: 12px;
}
</style>
