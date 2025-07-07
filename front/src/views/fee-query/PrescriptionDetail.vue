<template>
  <div class="prescription-detail">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>处方详情</span>
          <div class="button-group">
            <el-button type="primary" @click="handleBack">返回列表</el-button>
            <el-button type="primary" @click="handleBackCharge">返回缴费</el-button>
          </div>
        </div>
      </template>

      <template v-if="loading">
        <el-skeleton :rows="10" animated />
      </template>
      
      <template v-else-if="error">
        <el-alert :title="error" type="error" show-icon />
      </template>
      
      <el-descriptions v-else :column="2" border>
        <el-descriptions-item label="处方流水号">{{ detailData.preSequence }}</el-descriptions-item>
        <el-descriptions-item label="处方ID">{{ detailData.preId }}</el-descriptions-item>
        <el-descriptions-item label="挂号ID">{{ detailData.preRegId }}</el-descriptions-item>
        <el-descriptions-item label="就诊卡号">{{ detailData.prehcard }}</el-descriptions-item>
        <el-descriptions-item label="患者姓名">{{ detailData.prepname }}</el-descriptions-item>
        <el-descriptions-item label="科室名称">{{ detailData.predepname }}</el-descriptions-item>
        <el-descriptions-item label="医生姓名">{{ detailData.predocname }}</el-descriptions-item>
        <el-descriptions-item label="处方内容">{{ detailData.preContent }}</el-descriptions-item>
        <el-descriptions-item label="药品ID">{{ detailData.preCiId }}</el-descriptions-item>
        <el-descriptions-item label="药品数量">{{ detailData.preCiNum }}</el-descriptions-item>
        <el-descriptions-item label="处方金额">¥{{ (detailData.preprice || 0).toFixed(2) }}</el-descriptions-item>
        <el-descriptions-item label="处方状态">
          <el-tag :type="getStatusTagType(detailData.preState)">
            {{ detailData.preState }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="处方时间">{{ detailData.preTime }}</el-descriptions-item>
        <el-descriptions-item label="收据ID">{{ detailData.preReceiptId }}</el-descriptions-item>
        <el-descriptions-item label="处理人ID">{{ detailData.preDealerId }}</el-descriptions-item>
        <el-descriptions-item label="支付类型">{{ getDealTypeText(detailData.preDealType) }}</el-descriptions-item>
        <el-descriptions-item label="处理时间">{{ detailData.preDealTime }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getPrescriptionById } from '@/api/prescription'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

const detailData = ref({})
const loading = ref(true)
const error = ref('')

// 打印路由参数，用于调试
console.log('路由参数:', route.params)
console.log('preSequence:', route.params.id)

// 获取状态标签类型
const getStatusTagType = (status) => {
  if (!status) return ''
  const statusMap = {
    '已完成': 'success',
    '待执行': 'warning',
    '已退费': 'danger',
    '已取消': 'info'
  }
  return statusMap[status] || ''
}

// 获取支付类型文本
const getDealTypeText = (type) => {
  if (!type) return ''
  const map = {
    'CASH': '现金',
    'CARD': '银行卡',
    'WECHAT': '微信',
    'ALIPAY': '支付宝',
    'INSURANCE_PAY': '医保支付',
    '就诊卡': '就诊卡支付'
  }
  return map[type] || type
}

// 获取详情数据
const fetchDetail = async () => {
  try {
    loading.value = true
    error.value = ''
    
    console.log('开始请求处方详情，preSequence:', route.params.id)
    
    const response = await getPrescriptionById(route.params.id)
    console.log('接口响应:', response)
    
    if (response && response.code === '200') {
      detailData.value = response.data || {}
      console.log('获取到的处方详情数据:', detailData.value)
    } else {
      error.value = response?.message || '获取处方详情失败'
      ElMessage.error(error.value)
    }
  } catch (err) {
    console.error('获取处方详情失败:', err)
    error.value = '获取处方详情失败: ' + (err.response?.data?.message || err.message)
    ElMessage.error(error.value)
  } finally {
    loading.value = false
  }
}

// 返回列表
const handleBack = () => {
  router.push({ name: 'PrescriptionFeeQuery' })
}
const handleBackCharge = () => {
  router.push({ name: 'OrderCharge' })
}
onMounted(() => {
  fetchDetail()
})
</script>

<style scoped>
.prescription-detail {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
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