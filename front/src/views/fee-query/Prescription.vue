<template>
  <div class="prescription-query-container">
    <el-card shadow="never">
      <!-- 搜索区域 -->
      <div class="search-area">
        <el-form :model="searchForm" label-width="100px">
          <el-row :gutter="20">
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
            <el-col :span="8">
              <el-form-item label="处方状态">
                <el-select 
                  v-model="searchForm.preState" 
                  placeholder="全部状态" 
                  clearable
                  @change="handleSearch"
                >
                  <el-option label="全部状态" value="" />
                  <el-option label="已完成" value="已完成" />
                  <el-option label="待执行" value="待执行" />
                  <el-option label="已退费" value="已退费" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-row :gutter="20">
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
              <el-form-item label="处方ID">
                <el-input 
                  v-model="searchForm.preId" 
                  placeholder="请输入处方ID" 
                  clearable
                  @input="handleSearch"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        
        <div class="action-buttons">
          <el-button @click="handleReset">重置</el-button>
          <el-button type="primary" @click="handleSearch">查询</el-button>
        </div>
      </div>
      
      <!-- 统计信息 -->
      <div class="statistics">
        <div class="statistics-info">
          <span>处方总数：{{ statistics.total }}单</span>
          <span>总金额：¥{{ formatPrice(statistics.totalAmount) }}</span>
          <span>已完成：{{ statistics.completed }}单</span>
          <span>待执行：{{ statistics.pending }}单</span>
          <span>已退费：{{ statistics.canceled }}单</span>
        </div>
      </div>
      
      <!-- 表格区域 -->
      <el-table 
        :data="tableData" 
        border 
        style="width: 100%"
        v-loading="loading"
      >
        <el-table-column prop="preId" label="处方ID" width="100" />
        <el-table-column prop="preSequence" label="处方序号" width="100" />
        <el-table-column prop="prehcard" label="就诊卡号" width="120" />
        <el-table-column prop="prepname" label="患者姓名" width="100" />
        <el-table-column prop="predepname" label="科室" width="150" />
        <el-table-column prop="predocname" label="医生" width="120" />
        <el-table-column prop="preContent" label="处方内容" width="200" />
        <el-table-column prop="preprice" label="处方金额" width="120">
          <template #default="{ row }">
            ¥{{ formatPrice(row.preprice) }}
          </template>
        </el-table-column>
        <el-table-column prop="preState" label="处方状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.preState)">
              {{ row.preState }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="preTime" label="开方时间" width="180" />
        <el-table-column prop="preDealType" label="支付方式" width="120" />
           <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
           <el-table-column label="操作" width="120" fixed="right">
  <template #default="{ row }">
    <el-button type="text" @click="handleDetail(row.preSequence)">详情</el-button>
    <!-- <el-button 
      type="text" 
      @click="handleCancelRegister(row)"
      :disabled="!canCancelRegister(row.regState)"
    >
      取消挂号
    </el-button> -->
   
    
  

  </template>
</el-table-column>
            <!-- <el-button 
              type="text" 
              @click="handleRefund(row)"
              :disabled="row.preState !== '待执行' || !row.selectedDealerId"
            >
              退费
            </el-button> -->
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
import { getAllPrescriptions, refundPrescription } from '@/api/prescription'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const handleDetail = (preSequence) => {
  router.push({ 
    name: 'PrescriptionDetail', 
    params: { id: preSequence }
  })
}
// 搜索表单
const searchForm = ref({
  prehcard: '',
  prepname: '',
  preState: '',
  predepname: '',
  predocname: '',
  preId: ''
})

// 加载状态
const loading = ref(false)

// 统计信息
const statistics = ref({
  total: 0,
  totalAmount: 0,
  completed: 0,
  pending: 0,
  canceled: 0
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

// 金额格式化方法
const formatPrice = (price) => {
  if (price === undefined || price === null || price === '') return '0.00'
  const num = Number(price)
  return isNaN(num) ? '0.00' : num.toFixed(2)
}

// 获取状态标签类型
const getStatusTagType = (status) => {
  const map = {
    '已完成': 'success',
    '待执行': 'warning',
    '已退费': 'danger'
  }
  return map[status] || ''
}

// 获取处方列表
const dealers = ref([
  { id: 1, name: '操作员1' },
  { id: 2, name: '操作员2' },
  { id: 3, name: '操作员3' }
])

// 获取处方列表
const fetchPrescriptionList = async () => {
  loading.value = true
  try {
    const response = await getAllPrescriptions()
    
    if (response.code === '200') {
      rawData.value = (response.data || []).map(item => ({
        ...item,
        preprice: Number(item.preprice) || 0,
        selectedDealerId: null // 添加操作员选择状态
      }))
      filterAndPaginateData()
      updateStatistics()
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
  
  // 根据搜索条件过滤
  if (searchForm.value.prehcard) {
    filteredData = filteredData.filter(item => 
      String(item.prehcard).includes(searchForm.value.prehcard))
  }
  if (searchForm.value.prepname) {
    filteredData = filteredData.filter(item => 
      item.prepname?.includes(searchForm.value.prepname))
  }
  if (searchForm.value.preState) {
    filteredData = filteredData.filter(item => 
      item.preState === searchForm.value.preState)
  }
  if (searchForm.value.predepname) {
    filteredData = filteredData.filter(item => 
      item.predepname?.includes(searchForm.value.predepname))
  }
  if (searchForm.value.predocname) {
    filteredData = filteredData.filter(item => 
      item.predocname?.includes(searchForm.value.predocname))
  }
  if (searchForm.value.preId) {
    filteredData = filteredData.filter(item => 
      String(item.preId).includes(searchForm.value.preId))
  }
  
  pagination.value.total = filteredData.length
  const start = (pagination.value.currentPage - 1) * pagination.value.pageSize
  const end = start + pagination.value.pageSize
  tableData.value = filteredData.slice(start, end)
}

// 更新统计信息
const updateStatistics = () => {
  const allData = rawData.value
  
  statistics.value = {
    total: allData.length,
    totalAmount: allData.reduce((sum, item) => sum + (item.preprice || 0), 0),
    completed: allData.filter(item => item.preState === '已完成').length,
    pending: allData.filter(item => item.preState === '待执行').length,
    canceled: allData.filter(item => item.preState === '已退费').length
  }
}
const handleDealerSelect = (row) => {
  console.log(`处方 ${row.preId} 选择操作员: ${row.selectedDealerId}`)
}
// 退费操作
const handleRefund = (row) => {
  ElMessageBox.confirm(`确定要退费处方 ${row.preId} 吗?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    if (!row.selectedDealerId) {
      ElMessage.error('请先选择操作员')
      return
    }
    
    loading.value = true
    refundPrescription(row.preSequence, row.selectedDealerId)
      .then(() => {
        ElMessage.success('退费操作成功')
        fetchPrescriptionList()
      })
      .catch(error => {
        console.error('退费失败:', error)
        ElMessage.error(error.message || '退费操作失败')
      })
      .finally(() => {
        loading.value = false
      })
  }).catch(() => {
    // 取消操作
  })
}
// 重置搜索条件
const handleReset = () => {
  searchForm.value = {
    prehcard: '',
    prepname: '',
    preState: '',
    predepname: '',
    predocname: '',
    preId: ''
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
.prescription-query-container {
  padding: 20px;
}

.search-area {
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
.el-select {
  vertical-align: middle;
  margin-right: 10px;
}
</style>