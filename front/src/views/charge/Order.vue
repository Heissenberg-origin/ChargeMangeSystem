<template>
  <div class="charge-page">
    <!-- 第二块：患者信息 -->
    <div class="patient-info">
      <h3>患者信息</h3>
      <el-form :model="patientInfo" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="门(就)诊号">
              <el-input 
                v-model="patientInfo.visitNumber" 
                placeholder="输入门诊号后按回车" 
                @keyup.enter="fetchPatientInfo"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="费用类型">
              <el-select v-model="patientInfo.feeType" placeholder="请选择" disabled>
                <el-option label="自费" value="SELF_PAY" />
                <el-option label="职工医保" value="EMPLOYEE_INSURANCE" />
                <el-option label="居民医保" value="RESIDENT_INSURANCE" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="门诊号">
              <el-input v-model="patientInfo.outpatientNumber" readonly />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="就诊卡号">
              <el-input v-model="patientInfo.medicalCardNumber" readonly />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="姓名">
              <el-input v-model="patientInfo.name" readonly />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="挂号时间">
              <el-date-picker
                v-model="patientInfo.registerTime"
                type="datetime"
                placeholder="选择日期时间"
                readonly
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="挂号科室">
              <el-input v-model="patientInfo.department" readonly />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="挂号医生">
              <el-input v-model="patientInfo.doctor" readonly />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="医嘱内容">
          <el-input
            v-model="patientInfo.diagnosis"
            type="textarea"
            :rows="2"
            placeholder="请输入医嘱内容"
          />
        </el-form-item>
      </el-form>
    </div>
    
    <!-- 第三块：收费项目 -->
    <div class="charge-items">
      <div class="charge-header">
        <h3>收费项目</h3>
        <el-button type="primary" @click="showAddItemDialog">添加项目</el-button>
      </div>
      
      <el-table :data="chargeItems" border style="width: 100%">
        <el-table-column prop="itemId" label="项目ID" width="120" />
        <el-table-column prop="itemCode" label="项目编码" width="120" />
        <el-table-column prop="itemName" label="项目名称" width="180" />
        <el-table-column prop="quantity" label="数量" width="80">
          <template #default="{ row }">
            <el-input-number 
              v-model="row.quantity" 
              :min="1" 
              size="small" 
              @change="calculateTotal"
            />
          </template>
        </el-table-column>
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column prop="unitPrice" label="单价" width="100" />
        <el-table-column prop="receivableAmount" label="应收金额" width="100">
          <template #default="{ row }">
            {{ (row.unitPrice * row.quantity).toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="reductionAmount" label="减免金额" width="100">
          <template #default="{ row }">
            <el-input-number 
              v-model="row.reductionAmount" 
              :min="0" 
              :max="row.unitPrice * row.quantity" 
              size="small" 
              @change="calculateTotal"
            />
          </template>
        </el-table-column>
        <el-table-column prop="actualAmount" label="实收金额" width="100">
          <template #default="{ row }">
            {{ (row.unitPrice * row.quantity - row.reductionAmount).toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="prescribingDoctor" label="开单医生" width="120" />
        <el-table-column prop="prescribingDept" label="开单科室" width="120" />
        <el-table-column prop="executingDept" label="执行科室" width="120" />
        <el-table-column label="操作" width="80">
          <template #default="{ $index }">
            <el-button type="danger" size="small" @click="removeItem($index)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    
    <!-- 第四块：合计和操作按钮 -->
    <div class="charge-footer">
      <div class="total-info">
        <span>当前票据：{{ currentReceipt }}</span>
        <span>票据段：{{ receiptRange }}</span>
        <span class="total-amount">应收合计：￥{{ totalAmount.toFixed(2) }}</span>
      </div>
      <div class="action-buttons">
        <el-button @click="resetAll">重置</el-button>
        <el-button type="primary" @click="createPrescription">创建处方</el-button>
        <el-button type="success" @click="confirmCharge">确认收费</el-button>
      </div>
    </div>
    
    <!-- 添加项目弹窗 -->
    <add-item-dialog 
      v-model="showDialog" 
      @select-item="addChargeItem"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getRegistrationById,
  createCharge,
   createPrescription as apiCreatePrescription // 添加别名
} from '@/api/charge'
import AddItemDialog from '@/components/AddItemDialog.vue'

const router = useRouter()

// 患者信息数据
const patientInfo = ref({
  visitNumber: '',
  feeType: '',
  outpatientNumber: '',
  medicalCardNumber: '',
  name: '',
  gender: '',
  age: '',
  registerTime: '',
  diagnosis: '',
  department: '',
  doctor: ''
})

// 根据门诊号查询患者信息
const fetchPatientInfo = async () => {
  if (!patientInfo.value.visitNumber) {
    ElMessage.warning('请输入门诊号')
    return
  }
  
  try {
    const response = await getRegistrationById(patientInfo.value.visitNumber)
    const data = response.data.data
    
    patientInfo.value = {
      ...patientInfo.value,
      outpatientNumber: data.regId,
      name: data.regPname,
      feeType: data.regFeeType,
      department: data.regdepName,
      doctor: data.regdocName,
      registerTime: data.regTime,
      medicalCardNumber: data.regHcardId
    }
  } catch (error) {
    ElMessage.error('获取患者信息失败: ' + (error.response?.data?.message || error.message))
    resetPatientInfo()
  }
}

// 重置患者信息
const resetPatientInfo = () => {
  patientInfo.value = {
    visitNumber: patientInfo.value.visitNumber,
    feeType: '',
    outpatientNumber: '',
    medicalCardNumber: '',
    name: '',
    gender: '',
    age: '',
    registerTime: '',
    diagnosis: '',
    department: '',
    doctor: ''
  }
}

// 收费项目数据
const chargeItems = ref([])
const showDialog = ref(false)
const currentReceipt = ref('')
const receiptRange = ref('')
const totalAmount = ref(0)

// 显示添加项目弹窗
const showAddItemDialog = () => {
  if (!patientInfo.value.visitNumber) {
    ElMessage.warning('请先输入门诊号并获取患者信息')
    return
  }
  showDialog.value = true
}

// 添加收费项目
const addChargeItem = (item) => {
  const newItem = {
    ...item,
    quantity: 1,
    reductionAmount: 0,
    prescribingDoctor: patientInfo.value.doctor,
    prescribingDept: patientInfo.value.department,
    executingDept: item.executDept || '通用'
  }
  chargeItems.value.push(newItem)
  calculateTotal()
}

// 删除收费项目
const removeItem = (index) => {
  chargeItems.value.splice(index, 1)
  calculateTotal()
}

// 计算总金额
const calculateTotal = () => {
  totalAmount.value = chargeItems.value.reduce((sum, item) => {
    return sum + (item.unitPrice * item.quantity - item.reductionAmount)
  }, 0)
}

// 重置所有
const resetAll = () => {
  chargeItems.value = []
  totalAmount.value = 0
  resetPatientInfo()
}

// 创建处方
const createPrescription = async () => {
  if (!patientInfo.value.visitNumber) {
    ElMessage.warning('请先输入门诊号并获取患者信息')
    return
  }

  if (chargeItems.value.length === 0) {
    ElMessage.warning('请添加收费项目')
    return
  }

  // 格式化当前时间
  const formatTime = () => {
    const now = new Date()
    const year = now.getFullYear()
    const month = String(now.getMonth() + 1).padStart(2, '0')
    const day = String(now.getDate()).padStart(2, '0')
    const hours = String(now.getHours()).padStart(2, '0')
    const minutes = String(now.getMinutes()).padStart(2, '0')
    const seconds = String(now.getSeconds()).padStart(2, '0')
    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
  }

  const currentTime = formatTime()

  // 准备处方数据
  const prescriptionInfos = chargeItems.value.map(item => ({
    preRegId: patientInfo.value.outpatientNumber,
    prepname: patientInfo.value.name,
    predepname: patientInfo.value.department,
    predocname: patientInfo.value.doctor,
    preContent: patientInfo.value.diagnosis,
    preCiId: item.itemId,
    preCiNum: item.quantity,
    preprice: item.unitPrice,
    preState: "待缴费",
    preTime: currentTime,
    preDealerId: 2,
    preDealType: "现金",
    preDealTime: currentTime
  }))

  // 打印将要发送的数据
  console.log("准备发送给后端的处方数据:", JSON.stringify(prescriptionInfos, null, 2))
  
  try {
    const response = await apiCreatePrescription(prescriptionInfos)
    console.log("后端响应:", response)
    ElMessage.success('处方创建成功')
  } catch (error) {
    console.error("请求错误详情:", {
      requestData: prescriptionInfos,
      errorResponse: error.response?.data,
      errorMessage: error.message
    })
    ElMessage.error('处方创建失败: ' + (error.response?.data?.message || error.message))
  }
}

// 确认收费
const confirmCharge = async () => {
  if (chargeItems.value.length === 0) {
    ElMessage.warning('请添加收费项目')
    return
  }
  
  try {
    const chargeData = {
      regId: patientInfo.value.visitNumber,
      items: chargeItems.value.map(item => ({
        itemId: item.itemId,
        itemName: item.itemName,
        quantity: item.quantity,
        unitPrice: item.unitPrice,
        reductionAmount: item.reductionAmount,
        actualAmount: item.unitPrice * item.quantity - item.reductionAmount
      })),
      totalAmount: totalAmount.value,
      diagnosis: patientInfo.value.diagnosis
    }
    
    await createCharge(chargeData)
    ElMessage.success('收费成功')
    resetAll()
  } catch (error) {
    ElMessage.error('收费失败: ' + (error.response?.data?.message || error.message))
  }
}
</script>

<style scoped>
.charge-page {
  padding: 20px;
}

.header {
  margin-bottom: 20px;
}

.patient-info, .charge-items, .charge-footer {
  margin-bottom: 20px;
  padding: 15px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
}

.charge-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.total-info {
  margin-bottom: 15px;
}

.total-info span {
  margin-right: 20px;
}

.total-amount {
  font-weight: bold;
  color: #f56c6c;
}

.action-buttons {
  text-align: right;
}
</style>