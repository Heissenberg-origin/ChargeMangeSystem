<template>
  <div class="balance-container">
    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-form :model="searchForm" label-width="100px" @submit.prevent="handleSearch">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="就诊卡号" prop="healthcardId">
              <el-input 
                v-model.number="searchForm.healthcardId" 
                placeholder="请输入就诊卡号" 
                clearable 
                @keyup.enter="handleSearch"
                @dblclick="mockSwipeCard"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item>
              <el-button type="primary" native-type="submit" :loading="loading">
                <el-icon><Search /></el-icon>
                查询
              </el-button>
              <el-button @click="resetSearch">
                <el-icon><Refresh /></el-icon>
                重置
              </el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <!-- 患者信息展示 -->
    <el-card class="info-card" v-loading="loading">
      <el-descriptions :column="2" border v-if="patientData.healthcardId">
        <el-descriptions-item label="就诊卡号">{{ patientData.healthcardId }}</el-descriptions-item>
        <el-descriptions-item label="账户余额">
          <span :class="balanceClass">{{ patientData.balance.toFixed(2) }} 元</span>
        </el-descriptions-item>
        
        <el-descriptions-item label="患者姓名">{{ patientData.name }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ formatGender(patientData.gender) }}</el-descriptions-item>
        
        <el-descriptions-item label="年龄">{{ patientData.age }}</el-descriptions-item>
        <el-descriptions-item label="患者类型">{{ formatPatientType(patientData.type) }}</el-descriptions-item>
        
        <el-descriptions-item label="联系电话">{{ patientData.phoneNumber }}</el-descriptions-item>
        <el-descriptions-item label="建档时间">{{ patientData.createTime }}</el-descriptions-item>
      </el-descriptions>
      <div v-else class="empty-tip">
        请输入就诊卡号查询患者信息
      </div>
    </el-card>

    <!-- 结算操作区域 -->
    <el-card class="action-card" v-if="patientData.healthcardId && patientData.balance > 0">
      <div class="refund-section">
        <h3>结算操作</h3>
        <el-button 
          type="primary" 
          @click="handleRefund"
          :loading="refundLoading"
        >
          确认结算
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import { queryPatients, settlehcard } from '@/api/patient'

// 搜索表单
const searchForm = reactive({
  healthcardId: null
})

// 患者数据
const patientData = reactive({
  healthcardId: '',
  name: '',
  gender: '',
  age: '',
  type: '',
  phoneNumber: '',
  createTime: '',
  balance: 0
})

// 加载状态
const loading = ref(false)
const refundLoading = ref(false)

// 计算属性
const balanceClass = computed(() => {
  return patientData.balance > 0 ? 'positive-balance' : 'zero-balance'
})

// 格式化函数
const formatGender = (gender) => {
  const genderMap = { '1': '男', '2': '女', '男': '男', '女': '女' }
  return genderMap[gender] || gender || '未知'
}

const formatPatientType = (type) => {
  const typeMap = {
    'REGULAR': '普通',
    'VIP': 'VIP',
    'CHILD': '儿童',
    'ELDERLY': '老年',
    'DISABLED': '残疾'
  }
  return typeMap[type] || type
}

// 模拟刷卡（开发用）
const mockSwipeCard = () => {
  if (!searchForm.healthcardId) {
    searchForm.healthcardId = '530101199805666666'
    handleSearch()
  }
}

// 查询患者信息
const handleSearch = async () => {
  if (!searchForm.healthcardId) {
    ElMessage.warning('请输入就诊卡号')
    return
  }

  try {
    loading.value = true
    const response = await queryPatients(searchForm.healthcardId)
    
    if (response.data) {
      const data = response.data.data || response.data
      Object.assign(patientData, {
        healthcardId: data.healthcardId || data.healthcard_id,
        name: data.name,
        gender: data.gender,
        age: data.age,
        type: data.type,
        phoneNumber: data.phoneNumber || data.phonenumber,
        createTime: data.createTime || data.create_time,
        balance: data.balance || data.healthcard_balance || 0
      })
      
      if (patientData.balance <= 0) {
        ElMessage.warning('该就诊卡没有可结算余额')
      }
    } else {
      ElMessage.error('未找到患者信息')
      resetPatientData()
    }
  } catch (error) {
    console.error('查询患者失败:', error)
    ElMessage.error(error.response?.data?.message || error.message || '查询患者信息失败')
    resetPatientData()
  } finally {
    loading.value = false
  }
}

// 重置搜索
const resetSearch = () => {
  searchForm.healthcardId = null
  resetPatientData()
}

// 重置患者数据
const resetPatientData = () => {
  Object.assign(patientData, {
    healthcardId: '',
    name: '',
    gender: '',
    age: '',
    type: '',
    phoneNumber: '',
    createTime: '',
    balance: 0
  })
}

// 执行结算
const handleRefund = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要结算就诊卡 ${patientData.healthcardId} 的全部余额 ${patientData.balance.toFixed(2)} 元吗？`,
      '确认结算',
      { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
    )

    refundLoading.value = true
    await settlehcard(patientData.healthcardId)
    
    // 更新本地余额
    patientData.balance = 0
    
    ElMessage.success('结算成功')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('结算失败:', error)
      ElMessage.error(error.response?.data?.message || error.message || '结算失败')
    }
  } finally {
    refundLoading.value = false
  }
}
</script>

<style scoped>
.balance-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.info-card {
  margin-bottom: 20px;
}

.empty-tip {
  padding: 20px;
  text-align: center;
  color: #909399;
}

.action-card {
  margin-top: 20px;
}

.refund-section {
  padding: 20px;
}

.refund-section h3 {
  margin-bottom: 20px;
  color: #606266;
}

.positive-balance {
  color: #67c23a;
  font-weight: bold;
}

.zero-balance {
  color: #f56c6c;
  font-weight: bold;
}
</style>