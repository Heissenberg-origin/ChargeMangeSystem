<template>
  <div class="consultation-container">
    <el-card class="header-card">
      <div class="header">
        <h2>今日待接诊患者</h2>
        <el-tag type="info">{{ currentDate }}</el-tag>
      </div>
    </el-card>

    <div class="patient-list" v-loading="loading">
      <template v-if="registrations.length > 0">
        <el-card 
          v-for="reg in registrations" 
          :key="reg.regId" 
          class="patient-card"
          shadow="hover"
        >
          <div class="patient-info">
            <div class="info-left">
              <el-avatar :size="60" :src="getRandomAvatar()" />
              <div class="patient-details">
                <h3>{{ reg.regPname }}</h3>
                <p>主治医生: {{ reg.regdocName }}</p>
                <p>科室: {{ reg.regdepName }}</p>
                <p>挂号时间: {{ formatTime(reg.regTime) }}</p>
                <p>时段: {{ reg.regTimezone }}</p>
                <p>类型: {{ formatType(reg.regType) }}</p>
                <p>状态: {{ formatState(reg.regState) }}</p>
              </div>
            </div>
            <div class="info-right">
              <el-button 
                type="primary" 
                size="large" 
                @click="handleConsult(reg.regId)"
                :disabled="reg.regState !== 'PENDING'"
              >
                接 诊
              </el-button>
            </div>
          </div>
        </el-card>
      </template>
      <el-empty v-else-if="!loading" description="暂无待接诊患者" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRegistrationByNeed, updateRegistrationState } from '@/api/doctor'

const router = useRouter()
const loading = ref(true)
const registrations = ref([])
const doctorId = ref(JSON.parse(localStorage.getItem('userInfo'))?.id || 0)
const currentDate = ref(new Date().toISOString().split('T')[0])

// 随机头像
const getRandomAvatar = () => {
  const avatars = [
    'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
    'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    'https://cube.elemecdn.com/9/c2/f0ee8a3c7c9638a54940382568c9dpng.png'
  ]
  return avatars[Math.floor(Math.random() * avatars.length)]
}

// 格式化时间显示
const formatTime = (time) => {
  return time ? time.split(' ')[1].substring(0, 5) : '--:--'
}

// 格式化类型显示 - 与后端枚举完全匹配
const formatType = (type) => {
  const typeMap = {
    'GENERAL': '普通门诊',
    'EMERGENCY': '急诊',
    'CHRONIC': '慢病门诊',
    'PEDIATRIC': '儿童门诊',
    'SIMPLE': '简易门诊',
    'SPECIAL': '特病门诊',
    // 保留原有类型的兼容
    'EXPERT': '专家号',
    'FIRST_VISIT': '初诊'
  }
  return typeMap[type] || type
}

// 格式化状态显示
const formatState = (state) => {
  const stateMap = {
    'PENDING': '待就诊',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消'
  }
  return stateMap[state] || state
}

// 获取待接诊患者列表
const fetchPendingRegistrations = async () => {
  try {
    const response = await getRegistrationByNeed(
      doctorId.value, 
      currentDate.value, 
      'PENDING'
    )
    
    if (response.data && response.data.code === '200') {
      registrations.value = response.data.data || []
    }
  } catch (error) {
    ElMessage.error(`获取待接诊列表失败: ${error.message || error}`)
  } finally {
    loading.value = false
  }
}

// 接诊处理
const handleConsult = (regId) => {
  ElMessageBox.confirm(
    '确认接诊该患者吗？接诊后将跳转到处方创建页面。',
    '接诊确认',
    {
      confirmButtonText: '确认接诊',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await updateRegistrationState(regId, 'COMPLETED')
      ElMessage.success('接诊成功')
      // 修改跳转逻辑，传递regId作为visitNumber
      router.push({
        path: '/order-charge',
        query: { 
          visitNumber: regId, // 传递门诊号
          fromConsultation: true // 标记来自接诊页面
        }
      })
    } catch (error) {
      console.error('接诊失败:', error)
      ElMessage.error(`接诊失败: ${error.message || error}`)
    }
  }).catch(() => {
    console.log('用户取消接诊')
  })
}

onMounted(() => {
  if (doctorId.value) {
    fetchPendingRegistrations()
  } else {
    ElMessage.error('无法获取医生ID，请重新登录')
    loading.value = false
  }
})
</script>

<style scoped>
.consultation-container {
  padding: 20px;
}

.header-card {
  margin-bottom: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.patient-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 20px;
}

.patient-card {
  transition: all 0.3s ease;
}

.patient-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.patient-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
}

.info-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.patient-details h3 {
  margin: 0 0 5px 0;
  font-size: 18px;
}

.patient-details p {
  margin: 3px 0;
  font-size: 14px;
  color: #666;
}

.info-right {
  display: flex;
  align-items: center;
}

@media (max-width: 768px) {
  .patient-list {
    grid-template-columns: 1fr;
  }
}
</style>