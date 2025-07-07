<template>
  <div class="register-fee-query">
    <el-card shadow="never">
      <!-- 搜索区域 -->
      <div class="search-area">
        <el-form :model="searchForm" label-width="100px">
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="就诊卡号">
                <el-input 
                  v-model="searchForm.cardNumber" 
                  placeholder="请输入就诊卡号" 
                  clearable 
                  @input="handleSingleSearch('cardNumber')"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="患者姓名">
                <el-input 
                  v-model="searchForm.patientName" 
                  placeholder="请输入患者姓名" 
                  clearable
                  @input="handleSingleSearch('patientName')"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="挂号状态">
                <el-select 
                  v-model="searchForm.regState" 
                  placeholder="全部状态" 
                  clearable
                  @change="handleSingleSearch('regState')"
                >
                  <el-option label="全部状态" value="" />
                  <el-option label="待就诊" value="PENDING" />
                  <el-option label="已就诊" value="COMPLETED" />
                  <el-option label="已取消" value="CANCELLED" />
                  <el-option label="已过期" value="EXPIRED" />
                  <el-option label="待支付" value="PENDING_PAYMENT" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        
        <div class="action-buttons">
          <el-button type="primary" @click="handleExport">导出</el-button>
          <el-button @click="handleReset">重置</el-button>
        </div>
      </div>
      
      <!-- 状态标签 -->
      <div class="status-tabs">
        <el-tabs v-model="activeStatus" @tab-change="handleStatusChange">
          <el-tab-pane label="全部挂号" name="all" />
          <el-tab-pane label="待就诊" name="PENDING" />
          <el-tab-pane label="已就诊" name="COMPLETED" />
          <el-tab-pane label="已取消" name="CANCELLED" />
          <el-tab-pane label="已过期" name="EXPIRED" />
          <el-tab-pane label="待支付" name="PENDING_PAYMENT" />
        </el-tabs>
      </div>
      
      <!-- 统计信息 -->
      <div class="statistics">
        <div class="statistics-info">
          <span>挂号总数：{{ statistics.total }}个</span>
          <span>挂号总金额：¥{{ statistics.totalAmount.toFixed(2) }}</span>
          <span>已取消数：{{ statistics.canceled }}个</span>
          <span>已取消金额：¥{{ statistics.canceledAmount.toFixed(2) }}</span>
          <span>待就诊数：{{ statistics.pending }}个</span>
          <span>待支付数：{{ statistics.pendingPayment }}个</span>
        </div>
      </div>
      
      <!-- 表格区域 -->
      <el-table 
        :data="tableData" 
        border 
        style="width: 100%"
        v-loading="loading"
      >
        <el-table-column prop="regId" label="挂号ID" width="100" />
        <el-table-column prop="regHcardId" label="就诊卡号" width="120" />
        <el-table-column prop="regPname" label="患者姓名" width="100" />
        <el-table-column prop="regdepName" label="挂号科室" width="150" />
        <el-table-column prop="regdocName" label="挂号医生" width="120" />
        <el-table-column prop="regfee" label="挂号金额" width="120">
          <template #default="{ row }">
            ¥{{ row.regfee.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="regState" label="挂号状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.regState)">
              {{ getStatusText(row.regState) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="regTime" label="挂号时间" width="180" />
        <el-table-column prop="regType" label="挂号类型" width="120" />
        <el-table-column prop="regFeeType" label="费用类型" width="120" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
           <el-table-column label="操作" width="120" fixed="right">
  <template #default="{ row }">
    <el-button type="text" @click="handleDetail(row.regId)">详情</el-button>
    <!-- <el-button 
      type="text" 
      @click="handleCancelRegister(row)"
      :disabled="!canCancelRegister(row.regState)"
    >
      取消挂号
    </el-button> -->
   
    
  

  </template>
</el-table-column>
          
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowDown } from '@element-plus/icons-vue'
import { getAllRegisters, cancelRegistration } from '@/api/registration'
import { ElMessage, ElMessageBox } from 'element-plus'
import {  watch } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const router = useRouter()
// 监听路由变化
watch(() => route.query.healthcardId, (newVal) => {
  if (newVal) {
    searchForm.value.cardNumber = newVal
    handleSingleSearch('cardNumber')
  }
})
onMounted(() => {
  fetchRegisterList()
  
  // 检查是否有路由参数传入
  if (route.query.healthcardId) {
    searchForm.value.cardNumber = route.query.healthcardId
    // 延迟执行查询，确保数据已加载
    setTimeout(() => {
      handleSingleSearch('cardNumber')
    }, 300)
  }
})
// 搜索表单
const searchForm = ref({
  cardNumber: '',
  patientName: '',
  regState: ''
})

// 状态标签
const activeStatus = ref('all')

// 加载状态
const loading = ref(false)

// 统计信息
const statistics = ref({
  total: 0,
  totalAmount: 0,
  canceled: 0,
  canceledAmount: 0,
  pending: 0,
  expired: 0,
  pendingPayment: 0
})

// 表格数据
const tableData = ref([])

// 原始数据（用于筛选）
const rawData = ref([])

// 分页
const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 检查是否可以取消挂号
const canCancelRegister = (status) => {
  // 只有待就诊和待支付状态可以取消
  return ['PENDING'].includes(status)
}

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

// 获取挂号列表
const fetchRegisterList = async () => {
  loading.value = true
  try {
    const response = await getAllRegisters()
    
    if (response.code === '200') {
      rawData.value = response.data || []
      filterAndPaginateData()
      updateStatistics()
    } else {
      ElMessage.error(response.message || '获取挂号信息失败')
    }
  } catch (error) {
    console.error('获取挂号列表失败:', error)
    ElMessage.error('获取挂号列表失败: ' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// 筛选和分页数据
const filterAndPaginateData = () => {
  let filteredData = [...rawData.value]
  
  // 根据状态过滤
  if (activeStatus.value !== 'all') {
    filteredData = filteredData.filter(item => item.regState === activeStatus.value)
  }
  
  // 根据搜索条件过滤
  if (searchForm.value.cardNumber) {
    filteredData = filteredData.filter(item => 
      String(item.regHcardId).includes(searchForm.value.cardNumber)
    )
  }
  if (searchForm.value.patientName) {
    filteredData = filteredData.filter(item => 
      item.regPname?.includes(searchForm.value.patientName)
    )
  }
  if (searchForm.value.regState) {
    filteredData = filteredData.filter(item => 
      item.regState === searchForm.value.regState
    )
  }
  
  // 更新分页总数
  pagination.value.total = filteredData.length
  
  // 分页处理
  const start = (pagination.value.currentPage - 1) * pagination.value.pageSize
  const end = start + pagination.value.pageSize
  tableData.value = filteredData.slice(start, end)
}

// 更新统计信息
const updateStatistics = () => {
  const allData = rawData.value
  
  statistics.value = {
    total: allData.length,
    totalAmount: allData.reduce((sum, item) => sum + (item.regfee || 0), 0),
    canceled: allData.filter(item => item.regState === 'CANCELLED').length,
    canceledAmount: allData
      .filter(item => item.regState === 'CANCELLED')
      .reduce((sum, item) => sum + (item.regfee || 0), 0),
    pending: allData.filter(item => item.regState === 'PENDING').length,
    expired: allData.filter(item => item.regState === 'EXPIRED').length,
    pendingPayment: allData.filter(item => item.regState === 'PENDING_PAYMENT').length
  }
}

// 查看详情 - 只传递regId
const handleDetail = (regId) => {
  router.push({ 
    name: 'RegisterFeeDetail', 
    params: { id: regId }
  })
}

// 取消挂号
// 取消挂号
// 取消挂号
const handleCancelRegister = async (row) => {
  try {
    // 检查状态是否允许取消
    if (!canCancelRegister(row.regState)) {
      ElMessage.warning('当前状态不允许取消挂号')
      return
    }
    
    await ElMessageBox.confirm('确定要取消此挂号吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    loading.value = true
    // 调用取消挂号接口
    await cancelRegistration(row.regId)
    // 由于后端没有返回值，所以只要没有抛出异常就视为成功
    ElMessage.success('取消挂号成功')
    // 刷新列表
    await fetchRegisterList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消挂号失败:', error)
      ElMessage.error('取消挂号失败: ' + (error.message || '未知错误'))
    }
  } finally {
    loading.value = false
  }
}



// 重置搜索条件
const handleReset = () => {
  searchForm.value = {
    cardNumber: '',
    patientName: '',
    regState: ''
  }
  activeStatus.value = 'all'
  filterAndPaginateData()
}

// 单个搜索条件变化
const handleSingleSearch = () => {
  pagination.value.currentPage = 1
  filterAndPaginateData()
}

// 状态标签变化
const handleStatusChange = () => {
  pagination.value.currentPage = 1
  filterAndPaginateData()
}

// 分页大小变化
const handleSizeChange = (val) => {
  pagination.value.pageSize = val
  filterAndPaginateData()
}

// 当前页变化
const handleCurrentChange = (val) => {
  pagination.value.currentPage = val
  filterAndPaginateData()
}

// 页面加载时获取数据
onMounted(() => {
  fetchRegisterList()
})
</script>

<style scoped>
.register-fee-query {
  padding: 20px;
}

.search-area {
  margin-bottom: 20px;
}

.status-tabs {
  margin-bottom: 20px;
}

.action-buttons {
  text-align: right;
  margin-top: 10px;
}

.statistics {
  margin-bottom: 20px;
}

.statistics-info {
  margin-top: 10px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.statistics-info span {
  white-space: nowrap;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}
</style>