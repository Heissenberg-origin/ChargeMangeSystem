<template>
  <div class="refund-container">
    <h1>挂号退费</h1>
    
    <!-- 患者信息查询 -->
    <div class="patient-query">
      <el-input
        v-model="medicalCardId"
        placeholder="就诊卡号：双击或选中回车加载数据"
        clearable
        @dblclick="loadRegistrationData"
        @keyup.enter="loadRegistrationData"
        class="card-input"
      />
      
      <!-- 加载状态指示 -->
      <el-card class="info-card" v-loading="loading">
        <el-descriptions :column="3" border v-if="patientInfo.name">
          <el-descriptions-item label="姓名">{{ patientInfo.name }}</el-descriptions-item>
     
        </el-descriptions>
        <div v-else class="empty-tip">
          请输入就诊卡号并加载数据
        </div>
      </el-card>
    </div>
    
    <!-- 可退挂号列表 -->
    <div class="refund-items">
      <h2>可退挂号</h2>
      <el-table 
        :data="filteredRegistrations" 
        border 
        style="width: 100%" 
        v-loading="loading"
        empty-text="暂无可退挂号"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="挂号ID" width="80" />
        <el-table-column prop="time" label="挂号时间" width="160" />
        <el-table-column prop="doctor" label="医生" width="120" />
        <el-table-column prop="department" label="科室" width="150" />
        <el-table-column prop="fee" label="费用(元)" width="100"  />
        <el-table-column prop="type" label="门诊类型" width="120" />

        <el-table-column prop="status" label="状态" width="100">
          <template #default="{row}">
            <el-tag :type="row.status === 'PENGDING' ? 'warning' : 'info'">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
       
      </el-table>
    </div>
    
    <!-- 退费按钮 -->
    <div class="refund-action">
      <el-button 
        type="primary" 
        @click="showConfirmDialog"
        :disabled="selectedRegistrations.length === 0 || !currentDealerId"
      >
        退费 ({{ selectedRegistrations.length }})
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
      <div>确定要退费选中的 {{ selectedRegistrations.length }} 个挂号吗？</div>
      <div class="refund-summary" v-if="selectedRegistrations.length > 0">
        <p>退费总金额: <strong>{{ calculateTotalRefund() }}元</strong></p>
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
import { ElMessage } from 'element-plus'
import { 
  getRegistrationsByHealthCardId,
  cancelRegistration
} from '@/api/registration'

// 就诊卡号输入
const medicalCardId = ref('')

// 患者信息数据
const patientInfo = ref({
  name: '',
  doctor: '',
  department: ''
})

// 挂号列表数据
const registrations = ref([])
const loading = ref(false)
const selectedRegistrations = ref([])
const confirmDialogVisible = ref(false)

// 当前操作员ID
const currentDealerId = ref('')

// 只显示待就诊的挂号
const filteredRegistrations = computed(() => {
  return registrations.value.filter(reg => reg.status === 'PENDING')
})

onMounted(() => {
  // 获取当前操作员信息
  const userInfo = JSON.parse(localStorage.getItem('userInfo'))
  currentDealerId.value = userInfo?.id || ''
})

// 加载挂号数据
const loadRegistrationData = async () => {
  if (!medicalCardId.value) {
    ElMessage.warning('请输入就诊卡号')
    return
  }
  
  loading.value = true
  selectedRegistrations.value = []
  
  try {
    const response = await getRegistrationsByHealthCardId(medicalCardId.value)
    
    if (response && response.length > 0) {
      // 更新患者信息
      const firstReg = response[0]
      patientInfo.value = {
        name: firstReg.regPname,
        doctor: firstReg.regdocName,
        department: firstReg.regdepName
      }
      
      // 格式化挂号数据
      registrations.value = response.map(reg => ({
        id: reg.regId,
        time: reg.regTime,
        doctor: reg.regdocName,
        department: reg.regdepName,
        fee: reg.regfee,
        type: reg.regType === '1' ? '普通门诊' : '专家门诊',
        payment: formatPaymentMethod(reg.regDealType),
        status: reg.regState === '0' ? '待就诊' : 
               reg.regState === '1' ? '已就诊' : 
               reg.regState === '2' ? '已退号' : reg.regState,
        timeRange: reg.regTimeRange
      }))
      
      if (filteredRegistrations.value.length === 0) {
        ElMessage.warning('没有可退的挂号记录')
      }
    } else {
      registrations.value = []
      ElMessage.warning('未找到相关挂号记录')
    }
  } catch (error) {
    console.error('加载数据失败:', error)
    ElMessage.error('加载数据失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 格式化支付方式
const formatPaymentMethod = (method) => {
  const methodMap = {
    'SCAN_PAY': '扫码支付',
    'CASH': '现金',
    'WECHAT': '微信',
    'ALIPAY': '支付宝'
  }
  return methodMap[method] || method
}

// 处理表格选择变化
const handleSelectionChange = (selection) => {
  selectedRegistrations.value = selection
}

// 计算退费总金额
const calculateTotalRefund = () => {
  return selectedRegistrations.value.reduce((total, reg) => {
    return total + (parseFloat(reg.fee) || 0)
  }, 0).toFixed(2)
}

// 显示确认对话框
const showConfirmDialog = () => {
  if (selectedRegistrations.value.length === 0) {
    ElMessage.warning('请选择要退费的挂号记录')
    return
  }
  
  confirmDialogVisible.value = true
}

// 执行退费操作
const doRefund = async () => {
  confirmDialogVisible.value = false
  loading.value = true
  
  try {
    const refundResults = await Promise.all(
      selectedRegistrations.value.map(reg => 
        cancelRegistration(reg.id)
          .then(() => true)
          .catch(() => false)
      )
    )
    
    const successCount = refundResults.filter(r => r).length
    if (successCount === selectedRegistrations.value.length) {
      ElMessage.success(`成功退号 ${successCount} 项`)
    } else {
      ElMessage.warning(`成功退号 ${successCount} 项，失败 ${selectedRegistrations.value.length - successCount} 项`)
    }
    
    // 重新加载数据
    await loadRegistrationData()
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

h1, h2 {
  color: #333;
  margin-bottom: 20px;
}

.card-input {
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

.refund-summary {
  margin-top: 15px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}
</style>