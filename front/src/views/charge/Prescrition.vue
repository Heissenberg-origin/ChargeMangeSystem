<template>
  <div class="prescription-charge-container">
    <el-card shadow="never">
      <!-- 搜索区域 -->
      <div class="search-area">
        <el-form :model="searchForm" label-width="100px">
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="处方ID">
                <el-input 
                  v-model="searchForm.preId" 
                  placeholder="请输入处方ID" 
                  clearable
                  @input="handleSearch"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="就诊卡号">
                <el-input 
                  v-model="searchForm.prehcard" 
                  placeholder="请输入就诊卡号" 
                  clearable 
                  @input="handleSearch"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="患者姓名">
                <el-input 
                  v-model="searchForm.prepname" 
                  placeholder="请输入患者姓名" 
                  clearable
                  @input="handleSearch"
                />
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="医生">
                <el-input 
                  v-model="searchForm.predocname" 
                  placeholder="请输入医生姓名" 
                  clearable
                  @input="handleSearch"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="科室">
                <el-input 
                  v-model="searchForm.predepname" 
                  placeholder="请输入科室" 
                  clearable
                  @input="handleSearch"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        
        <div class="action-buttons">
          <el-button @click="handleReset">重置</el-button>
          <el-button @click="resetSelection">重置选中</el-button>
          <el-button type="primary" @click="handleSearch">查询</el-button>
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
        <el-table-column prop="preId" label="处方ID" width="100" />
        <el-table-column prop="preSequence" label="处方序号" width="100" />
        <el-table-column prop="prehcard" label="就诊卡号" width="120" />
        <el-table-column prop="prepname" label="患者姓名" width="100" />
        <el-table-column prop="predepname" label="科室" width="150" />
        <el-table-column prop="predocname" label="医生" width="120" />
        <el-table-column prop="preContent" label="处方内容" width="200" />
         <el-table-column prop="preState" label="处方状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.preState)">
              {{ row.preState }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="preprice" label="处方金额" width="120">
          <template #default="{ row }">
            ¥{{ formatPrice(row.preprice) }}
          </template>
        </el-table-column>
       
        <el-table-column prop="preTime" label="开方时间" width="180" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button type="text" @click="handleDetail(row.preSequence)">详情</el-button>
            <el-button 
              type="text" 
              @click="handlePay(row)"
              :disabled="row.preState !== '待缴费'"
            >
              收费
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

      <!-- 底部收费栏 -->
      <div class="payment-footer" v-if="selectedItems.length > 0">
        <div class="total-amount">
          总金额：<span class="amount">¥{{ calculateTotalAmount().toFixed(2) }}</span>
        </div>
        <el-button type="primary" @click="handleBatchPay">批量收费</el-button>
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
import { getAllPrescriptions, payPrescription } from '@/api/prescription'
import { ElMessage, ElMessageBox } from 'element-plus'
import PaymentDialog from '@/components/PrePaymentDialog.vue'

const router = useRouter()
const paymentDialog = ref(null)

// 搜索表单
const searchForm = ref({
  preId: '',
  prehcard: '',
  prepname: '',
  predocname: '',
  predepname: ''
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
    '已完成': 'success',
    '待执行': 'warning',
    '待缴费': 'primary',
    '已退费': 'danger'
  }
  return map[status] || ''
}

// 金额格式化方法
const formatPrice = (price) => {
  if (price === undefined || price === null || price === '') return '0.00'
  const num = Number(price)
  return isNaN(num) ? '0.00' : num.toFixed(2)
}

// 获取处方列表
const fetchPrescriptionList = async () => {
  loading.value = true
  try {
    const response = await getAllPrescriptions()
    
    if (response.code === '200') {
      rawData.value = (response.data || []).map(item => ({
        ...item,
        preprice: Number(item.preprice) || 0,
        selected: false
      }))
      filterAndPaginateData()
    } else {
      ElMessage.error(response.message || '获取处方信息失败')
    }
  } catch (error) {
    console.error('获取处方列表失败:', error)
    ElMessage.error('获取处方列表失败: ' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// 筛选和分页数据
const filterAndPaginateData = () => {
  let filteredData = [...rawData.value]
  
  // 只显示待缴费的处方记录
  filteredData = filteredData.filter(item => item.preState === '待缴费')
  
  // 根据搜索条件过滤
  if (searchForm.value.preId) {
    filteredData = filteredData.filter(item => 
      String(item.preId).includes(searchForm.value.preId))
  }
  if (searchForm.value.prehcard) {
    filteredData = filteredData.filter(item => 
      String(item.prehcard).includes(searchForm.value.prehcard))
  }
  if (searchForm.value.prepname) {
    filteredData = filteredData.filter(item => 
      item.prepname?.includes(searchForm.value.prepname))
  }
  if (searchForm.value.predocname) {
    filteredData = filteredData.filter(item => 
      item.predocname?.includes(searchForm.value.predocname))
  }
  if (searchForm.value.predepname) {
    filteredData = filteredData.filter(item => 
      item.predepname?.includes(searchForm.value.predepname))
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
    if (!selectedItems.value.some(item => item.preSequence === row.preSequence)) {
      selectedItems.value.push(row)
    }
  } else {
    selectedItems.value = selectedItems.value.filter(item => item.preSequence !== row.preSequence)
  }
}

// 计算总金额
const calculateTotalAmount = () => {
  return selectedItems.value.reduce((sum, item) => sum + (item.preprice || 0), 0)
}

// 查看详情
const handleDetail = (preSequence) => {
  router.push({ 
    name: 'PrescriptionDetail', 
    params: { id: preSequence }
  })
}

// 单条收费
const handlePay = (row) => {
  paymentDialog.value.show({
    preSequences: [row.preSequence],
    amount: row.preprice,
    dealerId: getDealerId()
  })
}

// 批量收费
const handleBatchPay = async () => {
  if (selectedItems.value.length === 0) {
    ElMessage.warning('请至少选择一条处方记录')
    return
  }

  const preSequences = selectedItems.value.map(item => item.preSequence)
  const totalAmount = calculateTotalAmount()
  const dealerId = getDealerId()

  if (!dealerId) {
    ElMessage.error('无法获取收费员信息')
    return
  }

  paymentDialog.value.show({
    preSequences,
    amount: totalAmount,
    dealerId
  })
}

// 支付成功回调
const handlePaymentSuccess = () => {
  fetchPrescriptionList()
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
    preId: '',
    prehcard: '',
    prepname: '',
    predocname: '',
    predepname: ''
  }
  filterAndPaginateData()
}

// 搜索
const handleSearch = () => {
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
  fetchPrescriptionList()
})
</script>

<style scoped>
.prescription-charge-container {
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