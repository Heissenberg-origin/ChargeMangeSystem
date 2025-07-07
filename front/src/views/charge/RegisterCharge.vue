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
          </el-row>
        </el-form>
        
        <div class="action-buttons">
          <el-button @click="handleReset">重置</el-button>
          <el-button @click="resetSelection">重置选中</el-button>
          <el-button type="primary" @click="handleSingleSearch">查询</el-button>
        </div>
      </div>
      
      <!-- 表格区域 -->
      <el-table 
        :data="tableData" 
        border 
        style="width: 100%"
        v-loading="loading"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
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
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button type="text" @click="handleDetail(row.regId)">详情</el-button>
            <el-button 
              type="text" 
              @click="handlePay(row)"
            >
              缴费
            </el-button>
            <el-checkbox 
              v-model="row.selected" 
              @change="handleCheckboxChange(row)"
            />
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

      <!-- 底部缴费栏 -->
      <div class="payment-footer" v-if="selectedItems.length > 0">
        <div class="total-amount">
          总金额：<span class="amount">¥{{ calculateTotalAmount().toFixed(2) }}</span>
        </div>
        <el-button type="primary" @click="handleBatchPay">批量缴费</el-button>
      </div>

      <!-- 支付对话框组件 -->
      <PaymentDialog 
        ref="paymentDialog"
        @success="handlePaymentSuccess"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getAllRegisters } from '@/api/registration'
import { ElMessage, ElMessageBox } from 'element-plus'
import PaymentDialog from '@/components/RegPaymentDialog.vue'
import { computed } from 'vue'
const router = useRouter()
const paymentDialog = ref(null)

// 搜索表单
const searchForm = ref({
  cardNumber: '',
  patientName: ''
})

// 加载状态
const loading = ref(false)

// 表格数据
const tableData = ref([])

// 原始数据（用于筛选）
const rawData = ref([])

// 选中的项目
const selectedItems = ref([])

// 分页
const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 重置选中状态
const resetSelection = () => {
  selectedItems.value = []
  tableData.value.forEach(item => {
    item.selected = false
  })
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
      rawData.value = (response.data || []).map(item => ({
        ...item,
        selected: false
      }))
      filterAndPaginateData()
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
  
  // 只显示待支付的挂号记录
  filteredData = filteredData.filter(item => item.regState === 'PENDING_PAYMENT')
  
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
  
  // 更新分页总数
  pagination.value.total = filteredData.length
  
  // 分页处理
  const start = (pagination.value.currentPage - 1) * pagination.value.pageSize
  const end = start + pagination.value.pageSize
  tableData.value = filteredData.slice(start, end)
}

// 处理选中行变化
const handleSelectionChange = (selection) => {
  selectedItems.value = selection
}

// 处理复选框变化
const handleCheckboxChange = (row) => {
  if (row.selected) {
    if (!selectedItems.value.some(item => item.regId === row.regId)) {
      selectedItems.value.push(row)
    }
  } else {
    selectedItems.value = selectedItems.value.filter(item => item.regId !== row.regId)
  }
}

// 计算总金额
const calculateTotalAmount = () => {
  return selectedItems.value.reduce((sum, item) => sum + (item.regfee || 0), 0)
}

// 查看详情
const handleDetail = (regId) => {
  router.push({ 
    name: 'RegisterFeeDetail', 
    params: { id: regId }
  })
}

// 单条缴费
const handlePay = (row) => {
  paymentDialog.value.show({
    regIds: [row.regId],
    amount: row.regfee,
    dealerId: getDealerId()
  })
}

// 批量缴费
const handleBatchPay = async () => {
  if (selectedItems.value.length === 0) {
    ElMessage.warning('请至少选择一条挂号记录')
    return
  }

  const regIds = selectedItems.value.map(item => item.regId)
  const totalAmount = calculateTotalAmount()
  const dealerId = getDealerId()

  if (!dealerId) {
    ElMessage.error('无法获取收费员信息')
    return
  }

  paymentDialog.value.show({
    regIds,
    amount: totalAmount,
    dealerId
  })
}

// 支付成功回调
const handlePaymentSuccess = () => {
  fetchRegisterList()
  resetSelection()
}

// 获取收费员ID
const getDealerId = () => {
  try {
    const userInfo = JSON.parse(localStorage.getItem('userInfo'))
    return userInfo?.id
  } catch (error) {
    console.error('获取收费员ID失败:', error)
    return null
  }
}

// 重置搜索条件
const handleReset = () => {
  searchForm.value = {
    cardNumber: '',
    patientName: ''
  }
  filterAndPaginateData()
}

// 单个搜索条件变化
const handleSingleSearch = () => {
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

.action-buttons {
  text-align: right;
  margin-top: 10px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.payment-footer {
  margin-top: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.total-amount {
  font-size: 16px;
}

.total-amount .amount {
  font-size: 18px;
  font-weight: bold;
  color: #f56c6c;
}
</style>