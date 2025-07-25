<template>
  <div class="recharge-container">
    <h1>就诊卡充值</h1>
    
    <div class="recharge-content">
      <!-- 左侧：患者信息 -->
      <div class="patient-info">
        <div class="card-input">
          <el-input 
            v-model.number="healthcardId" 
            placeholder="请输入就诊卡号" 
            clearable
            @change="fetchPatientInfo"
          />
          <el-button type="primary" @click="fetchPatientInfo" :loading="loading">查询</el-button>
        </div>
        
        <el-card class="info-card" v-if="patientInfo.healthcard_id">
          <div class="info-item">
            <span class="label">就诊卡号：</span>
            <span class="value">{{ patientInfo.healthcard_id }}</span>
          </div>
          <div class="info-item">
            <span class="label">姓名：</span>
            <span class="value">{{ patientInfo.name || '-' }}</span>
          </div>
          <div class="info-item">
            <span class="label">证件号：</span>
            <span class="value">{{ formatIdNumber(patientInfo.identification_id) }}</span>
          </div>
          <div class="info-item">
            <span class="label">账户余额(元)：</span>
            <span class="value">{{ formatCurrency(patientInfo.healthcard_balance) }}</span>
          </div>
          <div class="info-item">
            <span class="label">性别：</span>
            <span class="value">{{ formatGender(patientInfo.gender) }}</span>
          </div>
          <div class="info-item">
            <span class="label">联系电话：</span>
            <span class="value">{{ patientInfo.phonenumber || '-' }}</span>
          </div>
        </el-card>
      </div>
      
      <!-- 右侧：充值金额 -->
      <div class="recharge-amount">
        <h3>充值金额</h3>
        <div class="quick-amount">
          <el-button 
            v-for="amount in quickAmounts" 
            :key="amount" 
            @click="selectAmount(amount)"
            :type="selectedAmount === amount ? 'primary' : ''"
          >
            {{ amount }}元
          </el-button>
        </div>
        
        <el-input-number 
          v-model="customAmount" 
          :min="1" 
          :max="10000" 
          :precision="2" 
          :step="100"
          placeholder="自定义金额"
          class="custom-amount"
          @change="handleCustomAmountChange"
        />
        
        <div class="action-buttons">
          <el-button 
            type="primary" 
            @click="confirmRecharge"
            :loading="submitting"
            :disabled="!canRecharge"
          >
            确认充值
          </el-button>
          <el-button @click="resetForm">重置</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { queryPatients, recharge } from '@/api/patient'

const healthcardId = ref('')
const patientInfo = ref({})
const loading = ref(false)
const submitting = ref(false)

// 充值金额相关
const quickAmounts = ref([100, 300, 500, 1000, 3000, 5000])
const customAmount = ref(null)
const selectedAmount = ref(null)

// 是否可以充值
const canRecharge = computed(() => {
  return patientInfo.value.healthcard_id && (customAmount.value > 0)
})

// 格式化显示
const formatGender = (gender) => {
  const genderMap = {
    '1': '男',
    '2': '女',
    'male': '男',
    'female': '女'
  }
  return genderMap[gender] || gender || '未知'
}

const formatIdNumber = (id) => {
  if (!id) return '-'
  if (id.length === 18) {
    return id.replace(/(\d{6})\d+(\d{4})/, '$1******$2')
  }
  return `****${id.slice(-4)}`
}

const formatCurrency = (amount) => {
  if (amount === null || amount === undefined) return '-'
  return `¥${amount.toFixed(2)}`
}

// 获取患者信息
const fetchPatientInfo = async () => {
  if (!healthcardId.value) {
    ElMessage.warning('请输入就诊卡号')
    return
  }

  try {
    loading.value = true
    const response = await queryPatients(healthcardId.value)
    
    if (response.data?.data) {
      patientInfo.value = response.data.data
      ElMessage.success('患者信息查询成功')
    } else {
      ElMessage.warning('未找到该就诊卡号的患者信息')
      patientInfo.value = {}
    }
  } catch (error) {
    console.error('查询失败:', error)
    ElMessage.error(`查询失败: ${error.response?.data?.message || error.message || '未知错误'}`)
    patientInfo.value = {}
  } finally {
    loading.value = false
  }
}

// 金额选择
const selectAmount = (amount) => {
  selectedAmount.value = amount
  customAmount.value = amount
}

const handleCustomAmountChange = (value) => {
  if (value && quickAmounts.value.includes(value)) {
    selectedAmount.value = value
  } else {
    selectedAmount.value = null
  }
}

// 确认充值
// 确认充值
const confirmRecharge = async () => {
  try {
    submitting.value = true
    
    // 直接调用API，不做预验证
    await recharge(healthcardId.value, customAmount.value)
    
    // 只要不报错就显示成功
    ElMessage({
      message: '充值操作已提交',
      type: 'success',
      duration: 2000
    })
    
    // 简单更新本地余额（不推荐但按您要求）
    if (typeof patientInfo.value.healthcard_balance === 'number') {
      patientInfo.value.healthcard_balance += customAmount.value
    }
    
    resetAmount()
  } catch (error) {
    // 所有错误都显示相同提示
    ElMessage({
      message: '请规范输入',
      type: 'warning',
      duration: 2000
    })
  } finally {
    submitting.value = false
  }
}
const resetAmount = () => {
  selectedAmount.value = null
  customAmount.value = null
}

const resetForm = () => {
  healthcardId.value = ''
  patientInfo.value = {}
  resetAmount()
}
</script>

<style scoped>
.recharge-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.recharge-content {
  display: flex;
  gap: 30px;
  margin-top: 20px;
}

.patient-info {
  flex: 1;
  min-width: 400px;
}

.recharge-amount {
  flex: 1;
  min-width: 400px;
}

.card-input {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.card-input .el-input {
  flex: 1;
}

.info-card {
  padding: 20px;
}

.info-item {
  display: flex;
  margin-bottom: 15px;
  font-size: 16px;
}

.info-item .label {
  font-weight: bold;
  width: 120px;
  color: #666;
}

.info-item .value {
  flex: 1;
  color: #333;
}

.quick-amount {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
  margin: 20px 0;
}

.quick-amount .el-button {
  width: 100%;
  height: 50px;
  font-size: 16px;
}

.custom-amount {
  width: 100%;
  margin-bottom: 20px;
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 30px;
}

.action-buttons .el-button {
  width: 120px;
  height: 40px;
  font-size: 16px;
}

h1 {
  color: #333;
  margin-bottom: 30px;
  text-align: center;
  font-size: 24px;
}

h3 {
  color: #666;
  margin-bottom: 15px;
  font-size: 18px;
}
</style>