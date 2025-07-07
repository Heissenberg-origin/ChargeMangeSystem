<template>
  <div class="patient-list-container">
    <!-- 搜索区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" label-width="80px">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="就诊卡号">
              <el-input 
                v-model="searchForm.healthcardId" 
                placeholder="请输入就诊卡号" 
                clearable
                @keyup.enter="handleSearch"
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="患者姓名">
              <el-input 
                v-model="searchForm.name" 
                placeholder="请输入患者姓名" 
                clearable
                @keyup.enter="handleSearch"
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="证件号">
              <el-input 
                v-model="searchForm.identificationId" 
                placeholder="请输入证件号" 
                clearable
                @keyup.enter="handleSearch"
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item>
              <el-button type="primary" @click="handleSearch" :loading="loading">
                <el-icon><Search /></el-icon>
                搜索
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

    <!-- 操作按钮 -->
    <div class="operation-bar">
      <el-button type="primary" @click="goToRegister">
        <el-icon><Plus /></el-icon>
        新建登记
      </el-button>
    </div>

    <!-- 患者列表 -->
    <el-card shadow="never" class="table-card">
      <el-table 
        :data="filteredTableData" 
        border 
        stripe 
        style="width: 100%"
        v-loading="loading"
        :height="tableHeight"
        highlight-current-row
      >
        <el-table-column prop="healthcardId" label="就诊卡号" width="120" sortable />
        <el-table-column prop="name" label="患者姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="{ row }">
            {{ formatGender(row.gender) }}
          </template>
        </el-table-column>
        <el-table-column prop="identificationType" label="证件类型" width="120" />
        <el-table-column prop="identificationId" label="证件号" width="180" />
        <el-table-column prop="birthdate" label="出生日期" width="120" />
        <el-table-column prop="age" label="年龄" width="80" />
        <el-table-column prop="phoneNumber" label="联系电话" width="120" />
        <el-table-column prop="type" label="患者类型" width="100" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button 
              size="small" 
              type="primary"
              @click="viewDetail(row.healthcardId)"
            >
              详情
            </el-button>
            <el-button 
              size="small"
              @click="editPatient(row.healthcardId)"
            >
              编辑
            </el-button>
            <el-button 
              size="small" 
              type="danger"
              @click="handleDeactivate(row)"
              :loading="deletingId === row.healthcardId"
            >
              注销
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Plus, Search, Refresh } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listAllPatients, deletePatient } from '@/api/patient'

const router = useRouter()

// 搜索表单
const searchForm = reactive({
  healthcardId: '',
  name: '',
  identificationId: ''
})

// 加载状态
const loading = ref(false)
const deletingId = ref(null)

// 表格数据
const tableData = ref([])
const tableHeight = ref('600px') // 增加表格默认高度

// 计算属性：过滤后的表格数据
const filteredTableData = computed(() => {
  if (!searchForm.healthcardId && !searchForm.name && !searchForm.identificationId) {
    return tableData.value
  }

  return tableData.value.filter(item => {
    const matchesHealthcardId = searchForm.healthcardId 
      ? String(item.healthcardId).includes(searchForm.healthcardId)
      : true
    
    const matchesName = searchForm.name 
      ? item.name.includes(searchForm.name)
      : true
    
    const matchesIdentificationId = searchForm.identificationId 
      ? String(item.identificationId).includes(searchForm.identificationId)
      : true
    
    return matchesHealthcardId && matchesName && matchesIdentificationId
  })
})

// 格式化性别显示
const formatGender = (gender) => {
  const genderMap = {
    '1': '男',
    '2': '女',
    '男': '男',
    '女': '女'
  }
  return genderMap[gender] || gender || '未知'
}

// 获取所有患者数据
const fetchAllPatients = async () => {
  try {
    loading.value = true
    const response = await listAllPatients()
    const data = response.data.data || []
    
    // 映射字段到表格数据
    tableData.value = data.map(item => ({
      healthcardId: item.healthcardId || item.healthcard_id,
      name: item.name,
      gender: item.gender,
      identificationType: item.identificationType || item.identification_type,
      identificationId: item.identificationId || item.identification_id,
      birthdate: item.birthdate,
      age: item.age,
      phoneNumber: item.phoneNumber || item.phonenumber,
      type: item.type,
      healthcardBalance: item.healthcardBalance || item.healthcard_balance,
      address: item.address
    }))
    
  } catch (error) {
    console.error('获取患者信息失败:', error)
    ElMessage.error('获取患者信息失败: ' + (error.response?.data?.message || error.message))
    tableData.value = []
  } finally {
    loading.value = false
  }
}

// 搜索处理（本地过滤）
const handleSearch = () => {
  // 使用计算属性 filteredTableData 自动过滤
}

// 重置搜索
const resetSearch = () => {
  searchForm.healthcardId = ''
  searchForm.name = ''
  searchForm.identificationId = ''
}

// 跳转到登记页面
const goToRegister = () => {
  router.push('/patient-register')
}

// 查看详情
const viewDetail = (healthcardId) => {
  router.push({
    name: 'PatientDetail',
    params: { id: healthcardId }
  })
}

// 编辑患者
const editPatient = (healthcardId) => {
  router.push({
    name: 'PatientEdit',
    params: { id: healthcardId }
  })
}

// 注销患者
const handleDeactivate = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要注销患者 ${row.name} (就诊卡号: ${row.healthcardId})？此操作不可恢复！`,
      '确认注销',
      {
        confirmButtonText: '确认注销',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    deletingId.value = row.healthcardId
    await deletePatient(row.healthcardId)
    ElMessage.success('患者注销成功')
    fetchAllPatients() // 重新加载数据
  } catch (error) {
    if (error !== 'cancel') {
      console.error('注销失败:', error)
      ElMessage.error(`注销失败: ${error.response?.data?.message || error.message}`)
    }
  } finally {
    deletingId.value = null
  }
}

// 组件挂载时加载数据
onMounted(() => {
  fetchAllPatients()
  // 动态计算表格高度
  window.addEventListener('resize', calculateTableHeight)
  calculateTableHeight()
})

// 动态计算表格高度
const calculateTableHeight = () => {
  const windowHeight = window.innerHeight
  tableHeight.value = `${windowHeight - 280}px` // 根据窗口高度动态调整
}
</script>

<style scoped>
.patient-list-container {
  padding: 20px;
  height: 100vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  box-sizing: border-box;
}

.search-card {
  margin-bottom: 20px;
  flex-shrink: 0;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.operation-bar {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
  flex-shrink: 0;
}

.table-card {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
  background-color: #ffffff;
  border-radius: 8px;
  overflow: hidden;
}

.el-table {
  flex: 1;
  border-radius: 8px;
}

:deep(.el-card__body) {
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 20px;
}

:deep(.el-table__inner-wrapper) {
  border-radius: 8px;
}
</style>