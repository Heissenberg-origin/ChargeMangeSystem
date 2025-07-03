<template>
  <div class="register-container">
    <!-- 患者信息区域 -->
    <el-card class="patient-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span>患者信息</span>
        </div>
      </template>
      
      <el-form 
        :model="searchForm"
        :rules="patientRules" 
        ref="patientFormRef" 
        label-width="120px"
        @submit.native.prevent
      >
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="就诊卡号" prop="healthcardId">
              <el-input 
                v-model.number="searchForm.healthcardId" 
                placeholder="请输入就诊卡号" 
                clearable
                type="number"
                @keyup.enter="loadPatientInfo"
                :disabled="patientLoading"
              >
                <template #append>
                  <el-button 
                    @click="loadPatientInfo" 
                    icon="el-icon-credit-card" 
                    :loading="patientLoading"
                  >
                    读卡
                  </el-button>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          
          <template v-if="patientInfoLoaded">
            <el-col :span="8">
              <el-form-item label="患者姓名">
                <el-input v-model="currentPatient.name" readonly />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="性别">
                <el-input :value="formatGender(currentPatient.gender)" readonly />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="年龄">
                <el-input v-model="currentPatient.age" readonly />
              </el-form-item>
            </el-col>
            
            <el-col :span="12">
              <el-form-item label="联系电话">
                <el-input v-model="currentPatient.phoneNumber" readonly />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="证件号">
                <el-input v-model="currentPatient.identificationId" readonly />
              </el-form-item>
            </el-col>
            
            <el-col :span="24">
              <el-form-item label="患者类型">
                <el-input v-model="currentPatient.type" readonly />
              </el-form-item>
            </el-col>
            
            <el-col :span="24">
              <el-form-item label="就诊卡余额">
                <el-input v-model="currentPatient.healthcardBalance" readonly>
                  <template #append>元</template>
                </el-input>
              </el-form-item>
            </el-col>
          </template>
        </el-row>
      </el-form>
    </el-card>

    <!-- 挂号类型选择区域 -->
    <el-card class="type-card" shadow="hover" v-if="patientInfoLoaded">
      <template #header>
        <div class="card-header">
          <span>挂号类型</span>
        </div>
      </template>
      
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="挂号类型" prop="regType">
            <el-select v-model="regInfo.regType" placeholder="请选择挂号类型">
              <el-option
                v-for="item in regTypeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        
        <el-col :span="8">
          <el-form-item label="费用类型" prop="regFeeType">
            <el-select v-model="regInfo.regFeeType" placeholder="请选择费用类型">
              <el-option
                v-for="item in regFeeTypeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        
        <el-col :span="8">
          <el-form-item label="就诊类型" prop="regConsultationType">
            <el-select v-model="regInfo.regConsultationType" placeholder="请选择就诊类型">
              <el-option
                v-for="item in regConsultationTypeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
    </el-card>

    <!-- 科室医生选择区域 -->
    <div class="selection-container">
      <!-- 科室选择 -->
      <el-card class="dept-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <span>科室选择</span>
          </div>
        </template>
        
        <el-input 
          v-model="deptSearch" 
          placeholder="搜索科室名称" 
          clearable
          @input="searchDepartments"
          prefix-icon="el-icon-search"
        />
        
        <el-tree
          class="dept-tree"
          :data="departmentList"
          :props="deptTreeProps"
          :filter-node-method="filterDeptNode"
          @node-click="handleDeptSelect"
          :highlight-current="true"
          node-key="departmentId"
          v-loading="deptLoading"
          :default-expand-all="true"
        />
      </el-card>
      
      <!-- 医生选择 -->
      <el-card class="doctor-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <span>医生选择</span>
          </div>
        </template>
        
        <el-input
          v-model="doctorSearch"
          placeholder="搜索医生姓名"
          clearable
          prefix-icon="el-icon-search"
          @input="searchDoctors"
        />
        
        <el-table
          :data="filteredDoctorList"
          highlight-current-row
          @current-change="handleDoctorSelect"
          v-loading="doctorLoading"
          height="300"
          style="width: 100%"
        >
          <el-table-column prop="docName" label="医生姓名" width="120" />
          <el-table-column prop="docRank" label="职称" width="120" />
          <el-table-column prop="docDpName" label="所属科室" />
          <el-table-column prop="docFee" label="挂号费" width="100">
            <template #default="{row}">
              ￥{{ row.docFee }}
            </template>
          </el-table-column>
        </el-table>
        
        <!-- 日期选择区域 -->
        <div v-if="selectedDoctor" class="date-selection">
          <div class="date-selection-title">请选择就诊日期：</div>
          <div class="date-buttons">
            <el-button
              v-for="date in availableDates"
              :key="date.dateStr"
              :type="scheduleDate === date.dateStr ? 'primary' : ''"
              @click="selectDate(date.dateStr)"
            >
              {{ date.displayText }}
            </el-button>
          </div>
        </div>
        
        <!-- 排班信息展示 -->
        <div v-if="scheduleLoading" class="schedule-loading">
          <el-icon class="is-loading"><Loading /></el-icon>
          正在加载排班信息...
        </div>
        
        <div v-else-if="currentSchedule.length > 0" class="schedule-list">
          <div class="schedule-header">
            <span>时间段</span>
            <span>剩余号源</span>
            <span>操作</span>
          </div>
          <div 
            v-for="schedule in currentSchedule" 
            :key="schedule.arrangeid"
            class="schedule-item"
            :class="{ 'selected': selectedSchedule?.arrangeid === schedule.arrangeid }"
          >
            <span>{{ formatTimeSlot(schedule.arrangetimezone) }}</span>
            <span>{{ schedule.arrangebalance }}</span>
            <el-button
              size="mini"
              :type="selectedSchedule?.arrangeid === schedule.arrangeid ? 'success' : 'primary'"
              :disabled="schedule.arrangebalance <= 0"
              @click="selectSchedule(schedule)"
            >
              {{ selectedSchedule?.arrangeid === schedule.arrangeid ? '已选择' : 
                 schedule.arrangebalance > 0 ? '选择' : '已满' }}
            </el-button>
          </div>
        </div>
        
        <div v-else-if="scheduleDate" class="no-schedule">
          该医生在选定日期没有排班
        </div>
      </el-card>
    </div>
    
    <!-- 操作按钮 -->
    <div class="action-buttons">
      <el-button 
        type="primary" 
        size="large" 
        :disabled="!canSubmit"
        @click="handleCreateRegistration"
        :loading="submitting"
      >
        确认信息
      </el-button>
      <el-button 
  type="success" 
  size="large" 
  :disabled="!canPay"
  @click="handlePayment"
>
  缴费
</el-button>
      <el-button size="large" @click="resetForm">重置</el-button>
    </div>
    
    <!-- 支付信息展示 -->
    <div v-if="selectedDoctor && selectedSchedule" class="payment-info">
      <el-card shadow="never">
        <div class="payment-summary">
          <h3>挂号信息摘要</h3>
          <div class="payment-details">
            <div><strong>患者姓名：</strong>{{ currentPatient.name }}</div>
            <div><strong>科室：</strong>{{ selectedDept.departmentName }}</div>
            <div><strong>医生：</strong>{{ selectedDoctor.docName }} ({{ selectedDoctor.docRank }})</div>
            <div><strong>挂号类型：</strong>{{ regTypeMap[regInfo.regType] }}</div>
            <div><strong>费用类型：</strong>{{ regFeeTypeMap[regInfo.regFeeType] }}</div>
            <div><strong>就诊类型：</strong>{{ regConsultationTypeMap[regInfo.regConsultationType] }}</div>
            <div><strong>就诊时间：</strong>{{ scheduleDate }} {{ formatTimeSlot(selectedSchedule.arrangetimezone) }}</div>
            <div class="payment-fee"><strong>挂号费用：</strong>￥{{ selectedDoctor.docFee }}</div>
          </div>
        </div>
      </el-card>
    </div>
    
    <!-- 支付对话框 -->
    <PaymentDialog
      ref="paymentDialog"
      :amount="selectedDoctor?.docFee || 0"
      :hcardId="currentPatient.healthcardId"
      :regId="tempRegId"
      @success="handlePaymentSuccess"
      @close="handlePaymentClose"
    />
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
import { 
  queryPatients
} from '@/api/patient'
import { 
  getAllDepartments,
  getDepartmentsByName 
} from '@/api/department'
import { 
  getDoctorByDeptId 
} from '@/api/doctor'
import { 
  getArrangeByDoctorAndDate 
} from '@/api/arrange'
import { createRegistration,getRegistrationsByHealthCardId } from '@/api/registration'
import PaymentDialog from '@/components/RegPaymentDialog.vue'

export default {
  name: 'RegisterPage',
  components: { Loading, PaymentDialog },
  
  setup() {
    // 添加计算属性
const canPay = computed(() => {
  return tempRegId.value !== null
})
   const checkLatestRegistration = async () => {
  if (!currentPatient.value.healthcardId) {
    console.error('当前就诊卡号为空')
    ElMessage.warning('未获取到就诊卡号')
    return null
  }

  try {
    console.log('正在查询就诊卡号:', currentPatient.value.healthcardId)
    const registrations = await getRegistrationsByHealthCardId(currentPatient.value.healthcardId)
    console.log('查询结果:', registrations)

    if (!registrations || registrations.length === 0) {
      console.warn('未找到任何挂号记录')
      return null
    }

    // 按regId降序排序，获取最新的挂号记录
    const sortedRegistrations = [...registrations].sort((a, b) => b.regId - a.regId)
    const latestReg = sortedRegistrations[0]
    console.log('最新挂号记录:', latestReg)

   
    if (latestReg.regState === "PENDING_PAYMENT") {
 
      return latestReg
    }
    
    console.warn('最新挂号记录状态不是"PENDING_PAYMENT":', latestReg.regState)
    return null
  } catch (error) {
    console.error('查询挂号记录失败:', {
      error,
      currentHealthcardId: currentPatient.value.healthcardId
    })
    ElMessage.error('查询挂号记录失败: ' + (error.message || '未知错误'))
    return null
  }
}

    // 患者信息相关
    const searchForm = ref({
      healthcardId: null
    })
    
    const patientRules = {
      healthcardId: [
        { required: true, message: '请输入就诊卡号', trigger: 'blur' },
        { type: 'number', message: '就诊卡号必须为数字', trigger: 'blur' }
      ]
    }
    
    const currentPatient = ref({})
    const patientInfoLoaded = ref(false)
    const patientLoading = ref(false)
    
    // 挂号类型相关
    const regInfo = ref({
      regType: '普通门诊',
      regFeeType: '自费',
      regConsultationType: '初诊'
    })
    
    const regTypeOptions = [
      { value: '普通门诊', label: '普通门诊' },
      { value: '急诊', label: '急诊' },
      { value: '慢病门诊', label: '慢病门诊' },
      { value: '儿童门诊', label: '儿童门诊' },
      { value: '简易门诊', label: '简易门诊' },
      { value: '特病门诊', label: '特病门诊' }
    ]
    
    const regFeeTypeOptions = [
      { value: '自费', label: '自费' },
      { value: '职工医保', label: '职工医保' },
      { value: '居民医保', label: '居民医保' },
      { value: '金保', label: '金保' },
      { value: '一卡通', label: '一卡通' },
      { value: '其他', label: '其他' }
    ]
    
    const regConsultationTypeOptions = [
      { value: '初诊', label: '初诊' },
      { value: '复诊', label: '复诊' }
    ]
    
    // 修改映射关系为中文
    const regTypeMap = {
      '普通门诊': '普通门诊',
      '急诊': '急诊',
      '慢病门诊': '慢病门诊',
      '儿童门诊': '儿童门诊',
      '简易门诊': '简易门诊',
      '特病门诊': '特病门诊'
    }
    
    const regFeeTypeMap = {
      '自费': '自费',
      '职工医保': '职工医保',
      '居民医保': '居民医保',
      '金保': '金保',
      '一卡通': '一卡通',
      '其他': '其他'
    }
    
    const regConsultationTypeMap = {
      '初诊': '初诊',
      '复诊': '复诊'
    }
    
    // 科室相关
    const deptSearch = ref('')
    const departmentList = ref([])
    const deptTreeProps = {
      children: 'children',
      label: 'departmentName'
    }
    const deptLoading = ref(false)
    const selectedDept = ref(null)
    
    // 医生相关
    const doctorSearch = ref('')
    const doctorList = ref([])
    const doctorLoading = ref(false)
    const selectedDoctor = ref(null)
    
    // 排班相关
    const scheduleDate = ref('')
    const currentSchedule = ref([])
    const scheduleLoading = ref(false)
    const selectedSchedule = ref(null)
    
    // 提交状态
    const submitting = ref(false)
    const tempRegId = ref(null)
    
    // 支付相关
    const paymentDialog = ref(null)
    
    // 计算属性
    const filteredDoctorList = computed(() => {
      return doctorList.value.filter(doctor => 
        doctor.docName.includes(doctorSearch.value))
    })
    
    const canSubmit = computed(() => {
      return patientInfoLoaded.value && 
             selectedDoctor.value && 
             selectedSchedule.value &&
             regInfo.value.regType &&
             regInfo.value.regFeeType &&
             regInfo.value.regConsultationType
    })
    
    // 生成未来7天的日期
    const availableDates = computed(() => {
      const dates = []
      const today = new Date()
      const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
      
      for (let i = 0; i < 7; i++) {
        const date = new Date(today)
        date.setDate(today.getDate() + i)
        
        const year = date.getFullYear()
        const month = (date.getMonth() + 1).toString().padStart(2, '0')
        const day = date.getDate().toString().padStart(2, '0')
        const weekday = weekdays[date.getDay()]
        
        const dateStr = `${year}-${month}-${day}`
        
        let displayText
        if (i === 0) {
          displayText = `今天 (${month}月${day}日)`
        } else if (i === 1) {
          displayText = `明天 (${month}月${day}日)`
        } else {
          displayText = `${weekday} (${month}月${day}日)`
        }
        
        dates.push({
          dateStr,
          displayText
        })
      }
      
      return dates
    })
    
    // 方法
    const formatGender = (gender) => {
      const genderMap = {
        '1': '男',
        '2': '女',
        '男': '男',
        '女': '女'
      }
      return genderMap[gender] || gender || '未知'
    }
    
    // 格式化时间段显示
    const formatTimeSlot = (slot) => {
      const timeMap = {
        'SLOT_8_9': '08:00-09:00',
        'SLOT_9_10': '09:00-10:00',
        'SLOT_10_11': '10:00-11:00',
        'SLOT_14_15': '14:00-15:00',
        'SLOT_15_16': '15:00-16:00'
      }
      return timeMap[slot] || slot.replace('SLOT_', '').replace('_', ':') + ':00'
    }
    
    // 格式化当前时间为指定格式
    const formatCurrentTime = () => {
      const now = new Date()
      const year = now.getFullYear()
      const month = String(now.getMonth() + 1).padStart(2, '0')
      const day = String(now.getDate()).padStart(2, '0')
      const hours = String(now.getHours()).padStart(2, '0')
      const minutes = String(now.getMinutes()).padStart(2, '0')
      const seconds = String(now.getSeconds()).padStart(2, '0')
      
      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
    }
    
    const loadPatientInfo = async () => {
      if (!searchForm.value.healthcardId) {
        ElMessage.warning('请输入就诊卡号')
        return
      }
      
      try {
        patientLoading.value = true
        const response = await queryPatients(searchForm.value.healthcardId)
        const data = response.data.data
        
        currentPatient.value = {
          healthcardId: data.healthcardId || data.healthcard_id,
          name: data.name,
          gender: data.gender,
          identificationType: data.identificationType || data.identification_type,
          identificationId: data.identificationId || data.identification_id,
          birthdate: data.birthdate,
          age: data.age,
          phoneNumber: data.phoneNumber || data.phonenumber,
          type: data.type,
          healthcardBalance: data.healthcardBalance || data.healthcard_balance,
          address: data.address
        }
        
        patientInfoLoaded.value = true
        ElMessage.success('患者信息加载成功')
      } catch (error) {
        console.error('获取患者信息失败:', error)
        ElMessage.warning('请输入已有的就诊卡号')
        currentPatient.value = {}
        patientInfoLoaded.value = false
      } finally {
        patientLoading.value = false
      }
    }
    
    const loadAllDepartments = async () => {
      deptLoading.value = true
      try {
        const response = await getAllDepartments()
        departmentList.value = response.data
      } catch (error) {
        console.error('加载科室列表失败:', error)
        ElMessage.error('加载科室列表失败: ' + (error.response?.data?.message || error.message))
      } finally {
        deptLoading.value = false
      }
    }
    
    const searchDepartments = async () => {
      if (!deptSearch.value) {
        loadAllDepartments()
        return
      }
      
      deptLoading.value = true
      try {
        const response = await getDepartmentsByName(deptSearch.value)
        departmentList.value = response.data
      } catch (error) {
        console.error('搜索科室失败:', error)
        ElMessage.error('搜索科室失败: ' + (error.response?.data?.message || error.message))
      } finally {
        deptLoading.value = false
      }
    }
    
    const filterDeptNode = (value, data) => {
      if (!value) return true
      return data.departmentName.includes(value)
    }
    
    const handleDeptSelect = async (dept) => {
      if (!dept.departmentId) return
      
      selectedDept.value = dept
      doctorLoading.value = true
      try {
        const response = await getDoctorByDeptId(dept.departmentId)
        doctorList.value = response.data.data
        doctorSearch.value = ''
        // 重置排班相关状态
        scheduleDate.value = ''
        currentSchedule.value = []
        selectedSchedule.value = null
        ElMessage.success(`已选择科室: ${dept.departmentName}`)
      } catch (error) {
        console.error('加载医生列表失败:', error)
        ElMessage.error('加载医生列表失败: ' + (error.response?.data?.message || error.message))
      } finally {
        doctorLoading.value = false
      }
    }
    
    const searchDoctors = () => {
      // 前端本地搜索，无需API调用
    }
    
    const handleDoctorSelect = (doctor) => {
      if (!doctor) return
      
      selectedDoctor.value = doctor
      // 切换医生时清空排班信息
      scheduleDate.value = ''
      currentSchedule.value = []
      selectedSchedule.value = null
      
      ElMessage.success(`已选择医生: ${doctor.docName}`)
    }
    
    const selectDate = (date) => {
      scheduleDate.value = date
      loadSchedule()
    }
    
    const loadSchedule = async () => {
      if (!selectedDoctor.value) {
        ElMessage.warning('请先选择医生')
        return
      }

      if (!scheduleDate.value) {
        ElMessage.warning('请选择日期')
        return
      }

      scheduleLoading.value = true
      currentSchedule.value = []
      selectedSchedule.value = null
      
      try {
        const response = await getArrangeByDoctorAndDate(
          selectedDoctor.value.docId,
          scheduleDate.value
        )
        
        currentSchedule.value = response?.data?.data || []
        
        if(currentSchedule.value.length > 0) {
          ElMessage.success(`已加载 ${scheduleDate.value} 的排班信息`)
        } else {
          ElMessage.warning('该日期没有排班信息')
        }
      } catch (error) {
        console.error('加载排班失败:', error)
        ElMessage.error('加载排班失败: ' + (error.message || '请稍后重试'))
      } finally {
        scheduleLoading.value = false
      }
    }
    
    const selectSchedule = (schedule) => {
      selectedSchedule.value = schedule
      ElMessage.success(`已选择 ${formatTimeSlot(schedule.arrangetimezone)} 时段`)
    }
    
    const handleCreateRegistration = async () => {
  if (!canSubmit.value) {
    ElMessage.warning('请完成所有必填信息')
    return
  }
  
  submitting.value = true
  try {
    const registrationData = {
      regHcardId: currentPatient.value.healthcardId,
      regState: "PENDING_PAYMENT",
      regTime: formatCurrentTime(),
      regType: regInfo.value.regType,
      regFeeType: regInfo.value.regFeeType,
      regConsultationType: regInfo.value.regConsultationType,
      regArrangeId: selectedSchedule.value.arrangeid,
      regDealerId: 1,
      regDealTime: formatCurrentTime(),
      regDealType: "CASH"
    }
    
    await createRegistration(registrationData)
    
    // 创建成功后查询最新挂号记录
   const latestReg = await checkLatestRegistration()
    console.log('创建后查询结果:', latestReg)
    
    if (latestReg && latestReg.regState === "PENDING_PAYMENT") {
      tempRegId.value = latestReg.regId
      ElMessage.success(`挂号记录创建成功(编号: ${tempRegId.value})`)
    } else {
      ElMessage.warning('未找到PENDING_PAYMENT的挂号记录')
    }
  } catch (error) {
    // ...错误处理
  } finally {
    submitting.value = false
  }
}
    
   const handlePayment = async () => {
  if (!tempRegId.value) {
    ElMessage.warning('请先创建挂号记录')
    return
  }
  
  try {
    const latestReg = await checkLatestRegistration()
    console.log('支付前验证:', latestReg)
    
    if (!latestReg || latestReg.regId !== tempRegId.value) {
      ElMessage.warning('挂号记录已变更，请重新确认')
      tempRegId.value = null
      return
    }
    
    if (latestReg.regState !== "PENDING_PAYMENT") {
      ElMessage.warning(`当前挂号记录状态不可支付(状态: ${latestReg.regState})`)
      return
    }
    
    paymentDialog.value.show()
  } catch (error) {
    console.error('支付前验证失败:', error)
    ElMessage.error('支付验证失败: ' + (error.message || '未知错误'))
  }
}
    
    const handlePaymentSuccess = () => {
      // 支付成功后的处理
      ElMessage.success('挂号支付流程完成')
      resetForm()
    }
    
    const handlePaymentClose = () => {
      // 支付对话框关闭时的处理
      console.log('支付对话框关闭')
    }
    
    const resetForm = () => {
      searchForm.value = { healthcardId: null }
      currentPatient.value = {}
      patientInfoLoaded.value = false
      selectedDept.value = null
      selectedDoctor.value = null
      doctorList.value = []
      deptSearch.value = ''
      doctorSearch.value = ''
      scheduleDate.value = ''
      currentSchedule.value = []
      selectedSchedule.value = null
      tempRegId.value = null
   
      regInfo.value = {
        regType: '普通门诊',
        regFeeType: '自费',
        regConsultationType: '初诊'
      }
      ElMessage.success('已重置表单')
    }
    
    // 生命周期钩子
    onMounted(() => {
      loadAllDepartments()
    })
    
    return {
      // 数据
      searchForm,
      patientRules,
      currentPatient,
      patientInfoLoaded,
      patientLoading,
      regInfo,
      regTypeOptions,
      regFeeTypeOptions,
      regConsultationTypeOptions,
      regTypeMap,
      regFeeTypeMap,
      regConsultationTypeMap,
      deptSearch,
      departmentList,
      deptTreeProps,
      deptLoading,
      doctorSearch,
      doctorList,
      doctorLoading,
      selectedDept,
      selectedDoctor,
      scheduleDate,
      currentSchedule,
      scheduleLoading,
      selectedSchedule,
      submitting,
      paymentDialog,
      tempRegId,
      
      // 计算属性
      filteredDoctorList,
      availableDates,
      canSubmit,
      canPay,
      
      // 方法
      formatGender,
      formatTimeSlot,
      loadPatientInfo,
      searchDepartments,
      filterDeptNode,
      handleDeptSelect,
      searchDoctors,
      handleDoctorSelect,
      selectDate,
      loadSchedule,
      selectSchedule,
      handleCreateRegistration,
      handlePayment,
      handlePaymentSuccess,
      handlePaymentClose,
      resetForm
    }
  }
}
</script>

<style scoped>
.register-container {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.card-header {
  font-size: 18px;
  font-weight: bold;
  display: flex;
  align-items: center;
}

.patient-card,
.type-card {
  margin-bottom: 20px;
}

.selection-container {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.dept-card {
  width: 320px;
  min-width: 320px;
}

.doctor-card {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.dept-tree {
  margin-top: 10px;
  height: 400px;
  overflow-y: auto;
}

.action-buttons {
  text-align: center;
  margin-top: 20px;
  display: flex;
  justify-content: center;
  gap: 15px;
}

.el-form-item {
  margin-bottom: 18px;
}

.el-card {
  border-radius: 8px;
}

.el-table {
  border-radius: 8px;
  margin-bottom: 15px;
}

/* 日期选择区域 */
.date-selection {
  margin: 15px 0;
  padding: 15px;
  border-top: 1px solid #ebeef5;
  border-bottom: 1px solid #ebeef5;
}

.date-selection-title {
  font-weight: bold;
  margin-bottom: 10px;
}

.date-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

/* 排班信息样式 */
.schedule-loading,
.no-schedule {
  text-align: center;
  padding: 20px;
  color: #909399;
}

.schedule-list {
  margin-top: 15px;
  border-top: 1px solid #ebeef5;
  max-height: 300px;
  overflow-y: auto;
}

.schedule-header {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr;
  padding: 10px 0;
  font-weight: bold;
  border-bottom: 1px solid #ebeef5;
}

.schedule-item {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr;
  padding: 10px 0;
  align-items: center;
  transition: background-color 0.3s;
}

.schedule-item:hover {
  background-color: #f5f7fa;
}

.schedule-item.selected {
  background-color: #f0f9eb;
}

/* 支付信息样式 */
.payment-info {
  margin-top: 20px;
}

.payment-summary {
  padding: 15px;
}

.payment-summary h3 {
  margin-top: 0;
  margin-bottom: 15px;
  color: #303133;
  border-bottom: 1px solid #ebeef5;
  padding-bottom: 10px;
}

.payment-details div {
  margin-bottom: 8px;
  line-height: 1.6;
}

.payment-fee {
  font-size: 16px;
  color: #f56c6c;
  font-weight: bold;
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px dashed #dcdfe6;
}
</style>