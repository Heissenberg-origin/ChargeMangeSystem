<template>
  <div class="refund-container">
    <h1>患者收费信息</h1>
    
    <!-- 患者信息 -->
    <div class="patient-info">
      <el-input
        v-model="visitNumber"
        placeholder="门(就)诊号：双击或选中回车加载数据"
        clearable
        @dblclick="loadPatientData"
        @keyup.enter="loadPatientData"
        class="visit-number-input"
      />
      
      <!-- 添加加载状态指示 -->
      <el-card class="info-card" v-loading="loading">
        <el-descriptions :column="3" border v-if="patientInfo.outpatientNumber">
          <el-descriptions-item label="门诊号">{{ patientInfo.outpatientNumber }}</el-descriptions-item>
          <el-descriptions-item label="就诊卡号">{{ patientInfo.medicalCardNumber }}</el-descriptions-item>
          <el-descriptions-item label="姓名">{{ patientInfo.name }}</el-descriptions-item>
          
          <el-descriptions-item label="性别">{{ patientInfo.gender || '未知' }}</el-descriptions-item>
          <el-descriptions-item label="年龄">{{ patientInfo.age || '未知' }}</el-descriptions-item>
          <el-descriptions-item label="挂号时间">{{ patientInfo.registerTime }}</el-descriptions-item>
          
          <el-descriptions-item label="费用类型">{{ patientInfo.feeType }}</el-descriptions-item>
          <el-descriptions-item label="支付方式">{{ patientInfo.paymentMethod }}</el-descriptions-item>
          <el-descriptions-item label="收费单号">{{ patientInfo.receiptNumber || '无' }}</el-descriptions-item>
          
          <el-descriptions-item label="收费时间">{{ patientInfo.chargeTime }}</el-descriptions-item>
          <el-descriptions-item label="收费员工号">{{ patientInfo.staffId }}</el-descriptions-item>
          <el-descriptions-item label="收费员">{{ patientInfo.staffName || '未知' }}</el-descriptions-item>
        </el-descriptions>
        <div v-else class="empty-tip">
          请输入门(就)诊号并加载数据
        </div>
      </el-card>
    </div>
    
    <!-- 可退项目 -->
    <div class="refund-items">
      <h2>可退项目</h2>
      <el-table 
        :data="filteredRefundableItems" 
        border 
        style="width: 100%" 
        v-loading="loading"
        empty-text="暂无可退项目"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="itemCode" label="项目编码" width="120" />
        <!-- <el-table-column prop="itemName" label="项目名称" width="150" /> -->
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column prop="unitPrice" label="单价" width="100" />
        <el-table-column prop="receivableAmount" label="应收金额" width="100" />
        <el-table-column prop="reductionAmount" label="减免金额" width="100" />
        <el-table-column prop="actualAmount" label="实收金额" width="100" />
        <el-table-column prop="prescribingDoctor" label="开单医生" width="120" />
        <el-table-column prop="prescribingDept" label="开单科室" width="120" />
        <el-table-column prop="executingDept" label="执行科室" width="120" />
        <el-table-column prop="status" label="状态" width="100" />
      </el-table>
    </div>
    
    <!-- 退费按钮 -->
    <div class="refund-action">
      <el-button 
        type="primary" 
        @click="showConfirmDialog"
        :disabled="selectedItems.length === 0 || !currentDealerId"
      >
        退费 ({{ selectedItems.length }})
      </el-button>
      <div v-if="currentDealerId" class="dealer-info">
        当前操作员: {{ currentDealerId }}
      </div>
    </div>
    
    <!-- 退费确认对话框 -->
    <el-dialog
      v-model="confirmDialogVisible"
      title="确认退费"
      width="30%"
    >
      <div>确定要退费选中的 {{ selectedItems.length }} 个项目吗？</div>
      <div v-if="currentDealerId" style="margin-top: 10px;">
        操作员ID: {{ currentDealerId }}
      </div>
      <template #footer>
        <el-button @click="confirmDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="doRefund">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getRegistrationById, 
  getPrescriptionsByRegistrationId,
  refundPrescription
} from '@/api/prescription'

// 门(就)诊号输入
const visitNumber = ref('')

// 患者信息数据
const patientInfo = ref({
  outpatientNumber: '',
  medicalCardNumber: '',
  name: '',
  gender: '',
  age: '',
  registerTime: '',
  feeType: '',
  paymentMethod: '',
  receiptNumber: '',
  chargeTime: '',
  staffId: '',
  staffName: ''
})

// 可退项目数据
const refundableItems = ref([])
const loading = ref(false)
const selectedItems = ref([])
const confirmDialogVisible = ref(false)

// 当前操作员ID（从个人中心获取）
const currentDealerId = ref(null)

// 只显示待执行的处方项目
const filteredRefundableItems = computed(() => {
  return refundableItems.value.filter(item => item.status === '待执行')
})

// 在组件挂载时获取当前操作员ID
onMounted(() => {
  // 从localStorage获取用户信息
  const userInfo = JSON.parse(localStorage.getItem('userInfo'))
  if (userInfo && userInfo.id) {
    currentDealerId.value = userInfo.id
  }
  
  if (!currentDealerId.value) {
    ElMessage.warning('未获取到操作员信息，请确保已登录')
  }
})

// 格式化费用类型
const formatFeeType = (type) => {
  const typeMap = {
    'EMPLOYEE_INSURANCE': '职工医保',
    'GOLDEN_INSURANCE': '金卡医保',
    'SILVER_INSURANCE': '银卡医保',
    'PUBLIC_INSURANCE': '公费医疗',
    'SELF_PAY': '自费',
    'CHRONIC': '慢性病'
  }
  return typeMap[type] || type
}

// 格式化支付方式
const formatPaymentMethod = (method) => {
  const methodMap = {
   'SCAN_PAY': '扫码支付',
    'INSURANCE_PAY': '医保支付',
    'CASH': '现金',
    'WECHAT': '微信支付',
    'ALIPAY': '支付宝',
    'BANK_CARD': '银行卡',
    'MEDICAL_CARD': '医保卡',
    'CREDIT_CARD': '信用卡',
    'DEBIT_CARD': '借记卡',
    'UNION_PAY': '银联支付'
  }
  return methodMap[method] || method
}

// 格式化处方状态
const formatPrescriptionStatus = (status) => {
  const statusMap = {
    'PENDING': '待执行',
    'COMPLETED': '已完成',
    'REFUNDED': '已退费'
  }
  return statusMap[status] || status
}

// 加载患者数据
const loadPatientData = async () => {
  if (!visitNumber.value) {
    ElMessage.warning('请输入门(就)诊号')
    return
  }
  
  loading.value = true
  selectedItems.value = []
  try {
    // 1. 查询挂号信息
    const registrationData = await getRegistrationById(visitNumber.value)
    console.log('挂号信息:', registrationData)
    
    // 2. 查询处方信息
    const prescriptionData = await getPrescriptionsByRegistrationId(visitNumber.value)
    console.log('处方信息:', prescriptionData)
    
    // 更新患者信息
    patientInfo.value = {
      outpatientNumber: visitNumber.value,
      medicalCardNumber: registrationData.regHcardId,
      name: registrationData.regPname,
      gender: registrationData.regGender || '未知',
      age: registrationData.regAge || '未知',
      registerTime: registrationData.regTime,
      feeType: formatFeeType(registrationData.regFeeType),
      paymentMethod: formatPaymentMethod(registrationData.regDealType),
      receiptNumber: registrationData.regId.toString(),
      chargeTime: registrationData.regDealTime,
      staffId: registrationData.regDealerId,
      staffName: registrationData.regdocName || '未知收费员'
    }
    
    // 更新可退项目
    refundableItems.value = prescriptionData.map(item => ({
      itemCode: item.preCiId,
      itemName: item.preContent,
      quantity: item.preCiNum,
      unit: '次',
      unitPrice: item.preprice,
      receivableAmount: item.preprice * item.preCiNum,
      reductionAmount: item.preReductionAmount || 0,
      actualAmount: (item.preprice * item.preCiNum) - (item.preReductionAmount || 0),
      prescribingDoctor: item.predocname,
      prescribingDept: item.predepname,
      executingDept: item.execDepName || item.predepname,
      sequence: item.preSequence,
      status: formatPrescriptionStatus(item.preState)
    }))
    
    if (filteredRefundableItems.value.length === 0) {
      ElMessage.warning('没有可退项目')
    } else {
      ElMessage.success(`加载成功，共 ${filteredRefundableItems.value.length} 个可退项目`)
    }
  } catch (error) {
    console.error('加载数据失败:', error)
    ElMessage.error(error.message || '加载数据失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 处理表格选择变化
const handleSelectionChange = (selection) => {
  selectedItems.value = selection
}

// 显示确认对话框
const showConfirmDialog = () => {
  if (selectedItems.value.length === 0) {
    ElMessage.warning('请选择要退费的项目')
    return
  }
  
  if (!currentDealerId.value) {
    ElMessage.warning('未获取到操作员信息，无法执行退费')
    return
  }
  
  confirmDialogVisible.value = true
}

// 执行退费操作
const doRefund = async () => {
  confirmDialogVisible.value = false
  
  try {
    loading.value = true
    
    // 执行批量退费
    const results = await Promise.all(
      selectedItems.value.map(item => 
        refundPrescription(item.sequence, currentDealerId.value)
          .then(() => ({ success: true, item }))
          .catch(error => ({ success: false, item, error }))
      )
    )
    
    // 统计成功和失败数量
    const successCount = results.filter(r => r.success).length
    const failCount = results.length - successCount
    
    if (failCount > 0) {
      ElMessage.warning(`成功退费 ${successCount} 项，失败 ${failCount} 项`)
    } else {
      ElMessage.success(`成功退费 ${successCount} 项`)
    }
    
    // 重新加载数据
    await loadPatientData()
  } catch (error) {
    console.error('退费失败:', error)
    ElMessage.error('退费过程中发生错误')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.refund-container {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

h1 {
  color: #333;
  margin-bottom: 20px;
}

h2 {
  color: #666;
  margin: 20px 0 15px 0;
}

.visit-number-input {
  width: 100%;
  margin-bottom: 20px;
}

.info-card {
  margin-bottom: 30px;
}

.empty-tip {
  padding: 20px;
  text-align: center;
  color: #999;
}

.refund-items {
  margin-bottom: 30px;
}

.refund-action {
  margin-top: 20px;
  display: flex;
  align-items: center;
  gap: 20px;
}

.dealer-info {
  color: #666;
  font-size: 14px;
}

.el-descriptions {
  margin-top: 20px;
}

.el-table {
  margin-top: 15px;
}
</style>