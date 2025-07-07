<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <h2>个人中心</h2>
        </div>
      </template>
      
      <el-descriptions :column="1" border>
        <el-descriptions-item label="用户ID">{{ userId }}</el-descriptions-item>
        <el-descriptions-item label="账号">{{ userInfo.fullAccount }}</el-descriptions-item>
        <el-descriptions-item label="职务">{{ rankText }}</el-descriptions-item>
        <el-descriptions-item label="收费员ID" v-if="isOperator">{{ dealerId }}</el-descriptions-item>
        <el-descriptions-item label="上次登录时间">{{ userInfo.lastLoginTime }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { computed } from 'vue'

// 从localStorage获取用户信息
const userInfo = JSON.parse(localStorage.getItem('userInfo'))

// 计算属性：用户ID（直接使用存储的数字ID）
const userId = computed(() => userInfo.id)

// 计算属性：职务文本
const rankText = computed(() => {
  const rankMap = {
    'administrator': '管理员',
    'operator': '操作员',
    'doctor': '医生'
  }
  return rankMap[userInfo.rank] || userInfo.rank
})

// 判断是否是操作员
const isOperator = computed(() => userInfo.rank === 'operator')

// 收费员ID
const dealerId = computed(() => userInfo.id)

// 暴露dealerId给其他组件使用
defineExpose({
  dealerId
})
</script>

<style scoped>
.profile-container {
  padding: 20px;
}

.profile-card {
  max-width: 600px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>