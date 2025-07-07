<template>
  <div class="register-detail">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>挂号详情</span>
          <div class="button-group">
            <el-button type="primary" @click="handleBack">返回列表</el-button>
            <el-button type="primary" @click="handleBackCharge">返回缴费</el-button>
          </div>
        </div>
      </template>

      <el-descriptions :column="2" border>
        <el-descriptions-item label="挂号ID">{{ detailData.regId }}</el-descriptions-item>
        <el-descriptions-item label="就诊卡号">{{ detailData.regHcardId }}</el-descriptions-item>
        <el-descriptions-item label="患者姓名">{{ detailData.regPname }}</el-descriptions-item>
        <el-descriptions-item label="挂号科室">{{ detailData.regdepName }}</el-descriptions-item>
        <el-descriptions-item label="挂号医生">{{ detailData.regdocName }}</el-descriptions-item>
        <el-descriptions-item label="挂号金额">¥{{ detailData.regfee?.toFixed(2) }}</el-descriptions-item>
        <el-descriptions-item label="挂号状态">
          <el-tag :type="getStatusTagType(detailData.regState)">
            {{ getStatusText(detailData.regState) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="挂号时间">{{ detailData.regTime }}</el-descriptions-item>
        <el-descriptions-item label="挂号类型">{{ detailData.regType }}</el-descriptions-item>
        <el-descriptions-item label="费用类型">{{ getFeeTypeText(detailData.regFeeType) }}</el-descriptions-item>
        <el-descriptions-item label="就诊时段">{{ detailData.regTimezone }}</el-descriptions-item>
        <el-descriptions-item label="处理时间">{{ detailData.regDealTime }}</el-descriptions-item>
        <el-descriptions-item label="支付类型">{{ getDealTypeText(detailData.regDealType) }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getRegistrationById } from '@/api/charge' // 使用收费页面相同的API
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

const detailData = ref({})

// 获取状态标签类型
const getStatusTagType = (status) => {
  const map = {
    PENDING: 'warning',
    COMPLETED: 'success',
    CANCELLED: 'info',
    EXPIRED: 'danger',
    PENDING_PAYMENT: 'warning'
  }
  return map[status] || ''
}

// 获取状态文本
const getStatusText = (status) => {
  const map = {
    PENDING: '待就诊',
    COMPLETED: '已就诊',
    CANCELLED: '已取消',
    EXPIRED: '已过期',
    PENDING_PAYMENT: '待支付'
  }
  return map[status] || status
}

// 获取费用类型文本
const getFeeTypeText = (type) => {
  const map = {
    SELF_PAY: '自费',
    EMPLOYEE_INSURANCE: '职工医保',
    RESIDENT_INSURANCE: '居民医保'
  }
  return map[type] || type
}

// 获取支付类型文本
const getDealTypeText = (type) => {
  const map = {
    CASH: '现金',
    CARD: '银行卡',
    WECHAT: '微信',
    ALIPAY: '支付宝',
    INSURANCE_PAY: '医保支付'
  }
  return map[type] || type
}

// 获取详情数据 - 使用与收费页面相同的API
const fetchDetail = async () => {
  try {
    const response = await getRegistrationById(route.params.id)
    if (response.data && response.data.code === '200') {
      detailData.value = response.data.data || {}
    } else {
      ElMessage.error(response.data?.message || '获取详情失败')
    }
  } catch (error) {
    console.error('获取详情失败:', error)
    ElMessage.error('获取详情失败: ' + (error.response?.data?.message || error.message))
  }
}

// 返回列表
const handleBack = () => {
  router.push({ name: 'RegisterFeeQuery' })
}

// 返回缴费
const handleBackCharge = () => {
  router.push({ name: 'RegisterCharge'} )
}

onMounted(() => {
  fetchDetail()
})
</script>

<style scoped>
.register-detail {
  padding: 20px;
}



.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.button-group {
  display: flex;
  gap: 8px; /* 调整按钮之间的间距，可以设为更小的值如4px */
}
</style>