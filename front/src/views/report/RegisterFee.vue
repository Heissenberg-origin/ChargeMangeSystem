<template>
  <div class="registration-summary-container">
    <!-- 查询条件区域 -->
    <div class="query-section">
      <el-card shadow="never">
        <el-form :model="queryParams" label-width="80px">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="时间类型">
                <el-radio-group v-model="queryParams.timeType">
                  <el-radio-button label="day">按天</el-radio-button>
                  <el-radio-button label="week">按周</el-radio-button>
                  <el-radio-button label="month">按月</el-radio-button>
                </el-radio-group>
              </el-form-item>
              
              <el-form-item label="统计方式">
                <el-radio-group v-model="queryParams.statisticsType">
                  <el-radio-button label="department">按科室</el-radio-button>
                  <el-radio-button label="doctor">按医生</el-radio-button>
                  <el-radio-button label="payment">按支付方式</el-radio-button>
                </el-radio-group>
              </el-form-item>
            </el-col>
            
            <el-col :span="12">
              <el-form-item label="时间范围">
                <el-date-picker
                  v-model="queryParams.dateRange"
                  type="daterange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  value-format="YYYY-MM-DD"
                  style="width: 100%"
                />
              </el-form-item>
              
              <div class="action-buttons">
                <el-button @click="handleReset" :icon="Refresh">
                  重置
                </el-button>
                <el-button 
                  type="primary" 
                  @click="handleSummary" 
                  :icon="PieChart"
                  :loading="loading"
                >
                  汇总
                </el-button>
              </div>
            </el-col>
          </el-row>
        </el-form>
      </el-card>
    </div>
    
    <!-- 展示区域 -->
    <div class="display-section">
      <el-card shadow="never">
        <div class="display-header">
          <div class="department-selector">
            <el-select 
              v-model="selectedDepartment" 
              placeholder="请选择科室"
              clearable
              style="width: 200px"
            >
              <el-option label="全部科室" value="" />
              <el-option
                v-for="item in departmentOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </div>
          
          <div class="display-actions">
            <el-tooltip content="打印当前数据" placement="top">
              <el-button :icon="Printer" @click="handlePrint" circle />
            </el-tooltip>
            <el-tooltip content="导出Excel" placement="top">
              <el-button type="success" :icon="Download" @click="handleExportData" circle />
            </el-tooltip>
          </div>
        </div>
        
        <div class="table-container">
          <el-table 
            :data="filteredTableData" 
            border 
            style="width: 100%"
            show-summary
            :summary-method="getSummaries"
            v-loading="loading"
            stripe
          >
            <el-table-column prop="statisticsType" label="统计方式" align="center" fixed />
            <el-table-column prop="totalCount" label="挂号总数" align="center" sortable />
            <el-table-column prop="cancelCount" label="退号数" align="center" sortable />
            <el-table-column prop="receivableAmount" label="应收金额(元)" align="center" sortable>
              <template #default="{row}">
                {{ formatCurrency(row.receivableAmount) }}
              </template>
            </el-table-column>
            <el-table-column prop="receivedAmount" label="实收金额(元)" align="center" sortable>
              <template #default="{row}">
                {{ formatCurrency(row.receivedAmount) }}
              </template>
            </el-table-column>
            <el-table-column prop="cancelAmount" label="退号金额(元)" align="center" sortable>
              <template #default="{row}">
                {{ formatCurrency(row.cancelAmount) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" align="center" width="120">
              <template #default="{row}">
                <el-button type="primary" size="small" @click="showDetail(row)">
                  明细
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
        
        <div class="pagination" v-if="showPagination">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="totalItems"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handlePageChange"
          />
        </div>
      </el-card>
    </div>
    
    <!-- 挂号明细对话框 -->
    <el-dialog 
      v-model="detailDialogVisible" 
      :title="`${currentDetail.statisticsType} - 挂号明细`"
      width="90%"
      top="5vh"
    >
      <div class="detail-table-container">
        <el-table 
          :data="registrationDetails" 
          border 
          style="width: 100%"
          v-loading="detailLoading"
          stripe
        >
          <el-table-column prop="registrationNo" label="就诊号" align="center" />
          <el-table-column prop="cardNo" label="就诊卡号" align="center" />
          <el-table-column prop="patientName" label="患者姓名" align="center" />
          <el-table-column prop="department" label="挂号科室" align="center" />
          <el-table-column prop="doctor" label="挂号医生" align="center" />
          <el-table-column prop="amount" label="挂号金额" align="center">
            <template #default="{row}">
              {{ formatCurrency(row.amount) }}
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="120">
          <template #default="{row}">
            <el-tag 
            :type="getStatusTagType(row.statusCode)" 
            class="status-tag"
            effect="plain"
            >
              {{ row.status }}
            </el-tag>
          </template>
          </el-table-column>
          <el-table-column prop="date" label="挂号日期" align="center" />
        </el-table>
        
        <div class="detail-pagination" v-if="detailTotal > 0">
          <el-pagination
            v-model:current-page="detailCurrentPage"
            v-model:page-size="detailPageSize"
            :total="detailTotal"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleDetailSizeChange"
            @current-change="handleDetailPageChange"
          />
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { Refresh, Download, Printer, PieChart } from '@element-plus/icons-vue'
import * as XLSX from 'xlsx'
import { ElMessage } from 'element-plus'
import { getRegistrationList } from '@/api/prescription'

// 查询参数
const queryParams = reactive({
  timeType: 'month',
  statisticsType: 'department',
  dateRange: [getThreeDaysAgo(), getToday()] // 改为3天前到今天
})

// 表格数据
const tableData = ref([])
const loading = ref(false)

// 科室筛选
const selectedDepartment = ref('')
const departmentOptions = ref([])

// 分页参数
const currentPage = ref(1)
const pageSize = ref(10)
const totalItems = ref(0)
const showPagination = computed(() => tableData.value.length > pageSize.value)

// 明细对话框
const detailDialogVisible = ref(false)
const detailLoading = ref(false)
const registrationDetails = ref([])
const currentDetail = ref({})
const detailCurrentPage = ref(1)
const detailPageSize = ref(10)
const detailTotal = ref(0)

// 状态映射
const statusMap = {
  'PENDING': '待就诊',
  'COMPLETED': '已就诊',
  'CANCELLED': '已退号',
  'EXPIRED': '已失效',
  'PENDING_PAYMENT': '待缴费'
}

// 支付方式映射
const paymentMethodMap = {
  'CASH': '现金',
  'ALIPAY': '支付宝',
  'WECHAT': '微信',
  'UNIONPAY': '银联',
  'MEDICAL_INSURANCE': '医保',
  'SCAN_PAY': '扫码支付',       // 新增
  'MEDICAL_CARD': '就诊卡支付',  // 新增
  'INSURANCE_PAY': '医保支付'    // 新增
}

/* 工具函数 */
// 获取当月第一天
function getFirstDayOfMonth() {
  const date = new Date()
  return new Date(date.getFullYear(), date.getMonth(), 1).toISOString().split('T')[0]
}

// 获取当月最后一天
function getLastDayOfMonth() {
  const date = new Date()
  return new Date(date.getFullYear(), date.getMonth() + 1, 0).toISOString().split('T')[0]
}

// 获取今天日期（格式：YYYY-MM-DD）
function getToday() {
  const date = new Date()
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

// 获取3天前的日期（格式：YYYY-MM-DD）
function getThreeDaysAgo() {
  const date = new Date()
  date.setDate(date.getDate() - 3)  // 改为减3天
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

// 格式化金额
const formatCurrency = (value) => {
  return '¥' + parseFloat(value || 0).toFixed(2)
}

// 获取状态标签类型
const getStatusTagType = (status) => {
  switch(status) {
    case 'COMPLETED': return 'success'
    case 'PENDING': return 'warning'
    case 'CANCELLED': return 'danger'
    case 'PENDING_PAYMENT': return 'primary'
    default: return 'info'
  }
}

// 获取状态中文显示
const getStatusText = (status) => {
  return statusMap[status] || status
}

// 获取支付方式中文显示
const getPaymentMethodText = (method) => {
  return paymentMethodMap[method] || method || '未知'
}

/* 数据获取与处理 */
// 获取表格数据
const fetchTableData = async () => {
  try {
    loading.value = true
    
    // 确保dateRange有值
    if (!queryParams.dateRange || queryParams.dateRange.length !== 2) {
      queryParams.dateRange = [getFirstDayOfMonth(), getLastDayOfMonth()]
    }
    
    // 构造请求参数，添加时间范围过滤
    const params = {
      startTime: `${queryParams.dateRange[0]} 00:00:00`,
      endTime: `${queryParams.dateRange[1]} 23:59:59`,
      timeType: queryParams.timeType,
      groupBy: queryParams.statisticsType === 'department' ? 'department' 
             : queryParams.statisticsType === 'doctor' ? 'doctor'
             : 'payment' // 按支付方式分组
    }
    
    // 调用接口
    const response = await getRegistrationList(params)
    const responseData = response.data
    
    console.log('接口返回数据:', responseData)
    
    if (!responseData) {
      throw new Error('响应数据为空')
    }
    
    // 处理数据
    tableData.value = processRegistrationData(responseData)
    
    // 更新科室选项
    updateDepartmentOptions(responseData)
    
    totalItems.value = tableData.value.length
    ElMessage.success(`成功加载 ${tableData.value.length} 条数据`)
  } catch (error) {
    console.error('数据加载失败:', error)
    ElMessage.error(`数据加载失败: ${error.message}`)
  } finally {
    loading.value = false
  }
}

// 处理挂号数据
const processRegistrationData = (apiData) => {
  console.log('处理挂号数据:', apiData)
  try {
    // 默认处理原始数组数据
    if (Array.isArray(apiData.data)) {
      console.log('处理原始数组数据:', apiData.data)
      // 添加时间范围过滤
      return apiData.data
        .filter(item => {
          const regDate = new Date(item.regTime).toISOString().split('T')[0]
          return regDate >= queryParams.dateRange[0] && regDate <= queryParams.dateRange[1]
        })
        .map(item => ({
          ...item,
          statisticsType: getStatisticsTypeLabel(item),
          totalCount: 1,
          cancelCount: item.regState === 'CANCELLED' ? 1 : 0,
          receivableAmount: item.regfee || 0,
          receivedAmount: item.regState !== 'CANCELLED' ? (item.regfee || 0) : 0,
          cancelAmount: item.regState === 'CANCELLED' ? (item.regfee || 0) : 0
        }))
    }
    
    // 处理分组数据
    let groupData = [];
    if (queryParams.statisticsType === 'department') {
      groupData = apiData.data.byDepartment || []
    } else if (queryParams.statisticsType === 'doctor') {
      groupData = apiData.data.byDoctor || []
    } else {
      // 按支付方式分组
      groupData = apiData.data.byPayment || []
    }

    console.log('处理后的分组数据:', groupData)
    
    return groupData
      .filter(item => {
        // 添加时间范围过滤
        const regDate = new Date(item.registerTime || item.regTime).toISOString().split('T')[0]
        return regDate >= queryParams.dateRange[0] && regDate <= queryParams.dateRange[1]
      })
      .map(item => {
        const total = item.totalRegistrations || 0
        const canceled = item.canceledRegistrations || 0
        
        // 根据统计类型设置统计类型标签
        let statisticsType = '';
        if (queryParams.statisticsType === 'department') {
          statisticsType = item.departmentName || item.groupName
        } else if (queryParams.statisticsType === 'doctor') {
          statisticsType = item.doctorName
        } else {
          // 按支付方式显示中文
          statisticsType = getPaymentMethodText(item.paymentMethod)
        }
        
        return {
          ...item,
          statisticsType,
          regdepName: item.departmentName,
          regdocName: item.doctorName,
          regfee: item.fee,
          regState: item.status,
          regTime: item.registerTime,
          regDealType: item.paymentMethod, // 支付方式
          totalCount: total,
          cancelCount: canceled,
          receivableAmount: item.totalFee || 0,
          receivedAmount: item.paidFee || 0,
          cancelAmount: item.canceledFee || 0,
          cancelRatio: total > 0 ? ((canceled / total) * 100).toFixed(2) + '%' : '0%'
        }
      })
  } catch (error) {
    console.error('数据处理失败:', error)
    return []
  }
}

// 获取统计类型标签
const getStatisticsTypeLabel = (item) => {
  switch(queryParams.statisticsType) {
    case 'department': return item.regdepName
    case 'doctor': return item.regdocName
    case 'payment': return getPaymentMethodText(item.regDealType) // 支付方式显示中文
    default: return item.regdepName
  }
}

// 更新科室选项
const updateDepartmentOptions = (apiData) => {
  try {
    let departments = []
    
    if (apiData?.byDepartment) {
      departments = apiData.byDepartment.map(d => d.departmentName || d.groupName)
    } else {
      departments = [...new Set(tableData.value.map(item => item.regdepName))]
    }
    
    departmentOptions.value = departments
      .filter(name => name)
      .map(name => ({ value: name, label: name }))
  } catch (error) {
    console.error('更新科室选项失败:', error)
  }
}

/* 表格相关 */
// 过滤后的数据
const filteredTableData = computed(() => {
  let data = tableData.value
  
  // 科室筛选
  if (selectedDepartment.value) {
    data = data.filter(item => item.regdepName === selectedDepartment.value)
  }
  
  // 分页
  totalItems.value = data.length
  const start = (currentPage.value - 1) * pageSize.value
  return data.slice(start, start + pageSize.value)
})

// 合计行计算
const getSummaries = (param) => {
  const { columns, data } = param
  const sums = []
  const numberFields = ['totalCount', 'cancelCount', 'receivableAmount', 'receivedAmount', 'cancelAmount']
  
  columns.forEach((column, index) => {
    if (index === 0) {
      sums[index] = '合计'
      return
    }
    
    if (numberFields.includes(column.property)) {
      const values = data.map(item => {
        const val = item[column.property]
        return isNaN(val) ? 0 : Number(val)
      })
      
      if (values.length > 0) {
        const sum = values.reduce((acc, val) => acc + val, 0)
        
        if (column.property.includes('Amount')) {
          sums[index] = formatCurrency(sum)
        } else {
          sums[index] = sum
        }
      } else {
        sums[index] = 'N/A'
      }
    } else {
      sums[index] = 'N/A'
    }
  })
  
  return sums
}

/* 操作函数 */
// 汇总/查询
const handleSummary = async () => {
  await fetchTableData()
}

// 重置
const handleReset = () => {
  queryParams.timeType = 'month'
  queryParams.statisticsType = 'department'
  queryParams.dateRange = [getThreeDaysAgo(), getToday()] // 改为3天前到今天
  selectedDepartment.value = ''
  currentPage.value = 1
}

// 显示明细
const showDetail = (row) => {
  currentDetail.value = row
  detailDialogVisible.value = true
  detailCurrentPage.value = 1
  fetchRegistrationDetails(row)
}

// 获取明细数据
const fetchRegistrationDetails = async (row) => {
  try {
    detailLoading.value = true
    
    // 模拟筛选当前统计类型的明细数据
    registrationDetails.value = tableData.value
      .filter(item => {
        if (queryParams.statisticsType === 'department') {
          return item.regdepName === row.regdepName
        } else if (queryParams.statisticsType === 'doctor') {
          return item.regdocName === row.regdocName
        } else {
          // 按支付方式筛选
          return item.regDealType === row.regDealType
        }
      })
      .map(item => ({
        registrationNo: item.regId,
        cardNo: item.regHcardId,
        patientName: item.regPname,
        department: item.regdepName,
        doctor: item.regdocName,
        amount: item.regfee,
        paymentMethod: getPaymentMethodText(item.regDealType), // 支付方式显示中文
        status: getStatusText(item.regState), // 使用中文状态
        statusCode: item.regState, // 保留原始状态码
        date: item.regTime
      }))
    
    detailTotal.value = registrationDetails.value.length
  } catch (error) {
    ElMessage.error('获取明细失败: ' + error.message)
  } finally {
    detailLoading.value = false
  }
}

/* 分页处理 */
const handleSizeChange = (val) => {
  pageSize.value = val
}

const handlePageChange = (val) => {
  currentPage.value = val
}

const handleDetailSizeChange = (val) => {
  detailPageSize.value = val
}

const handleDetailPageChange = (val) => {
  detailCurrentPage.value = val
}

/* 导出与打印 */
// 导出Excel
const handleExportData = () => {
  try {
    // 准备数据
    const headers = [
      '统计类型', '挂号总数', '退号数', 
      '应收金额(元)', '实收金额(元)', '退号金额(元)'
    ]
    
    const data = [
      headers,
      ...filteredTableData.value.map(item => [
        item.statisticsType,
        item.totalCount,
        item.cancelCount,
        item.receivableAmount,
        item.receivedAmount,
        item.cancelAmount
      ])
    ]
    
    // 添加合计行
    const totals = getSummaries({
      columns: headers.map((h, i) => ({ property: i === 0 ? 'statisticsType' : h })),
      data: filteredTableData.value
    })
    data.push(['合计', ...totals.slice(1)])
    
    // 创建工作簿
    const wb = XLSX.utils.book_new()
    const ws = XLSX.utils.aoa_to_sheet(data)
    
    // 设置列宽
    ws['!cols'] = headers.map(() => ({ wch: 15 }))
    
    // 导出文件
    XLSX.utils.book_append_sheet(wb, ws, '挂号统计')
    const fileName = `挂号统计_${new Date().toLocaleDateString()}.xlsx`
    XLSX.writeFile(wb, fileName)
    
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败: ' + error.message)
  }
}

// 打印
const handlePrint = () => {
  window.print()
}

// 初始化加载数据
fetchTableData()

// 监听日期范围变化
watch(() => queryParams.dateRange, (newVal) => {
  if (newVal && newVal.length === 2) {
    fetchTableData()
  }
}, { deep: true })
</script>

<style scoped>

/* 状态标签样式 */
.status-tag {
  margin: 2px;
}

/* 特定状态标签的额外样式（可选） */
:deep(.el-tag.el-tag--success) { /* 已就诊 */
  background-color: #f0f9eb;
  color: #67c23a;
}

:deep(.el-tag.el-tag--warning) { /* 待就诊 */
  background-color: #fdf6ec;
  color: #e6a23c;
}

:deep(.el-tag.el-tag--danger) { /* 已退号 */
  background-color: #fef0f0;
  color: #f56c6c;
}

:deep(.el-tag.el-tag--info) { /* 已失效 */
  background-color: #f4f4f5;
  color: #909399;
}

/* 待缴费（默认primary色） */
:deep(.el-tag:not([class*="el-tag--"])),
:deep(.el-tag.el-tag--primary) {
  background-color: #ecf5ff;
  color: #409eff;
}

.registration-summary-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.query-section {
  margin-bottom: 20px;
}

.display-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.table-container {
  margin-top: 20px;
}

.action-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.detail-table-container {
  margin-top: -20px;
}

.detail-pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

@media print {
  .query-section, .display-header, .pagination {
    display: none;
  }
  
  .el-table {
    width: 100% !important;
  }
  
  .el-table__fixed-right {
    display: none !important;
  }
}

/* 修改对话框样式 */
:deep(.el-dialog__header) {
  padding-top: 20px;  /* 增加顶部内边距 */
  margin-bottom: -10px; /* 减少标题与内容间距 */
}

/* 调整标题样式 */
:deep(.el-dialog__title) {
  font-size: 16px; /* 适当减小字体大小 */
  line-height: 1.5; /* 调整行高 */
}

/* 调整表格与标题的间距 */
.detail-table-container {
  margin-top: 0; /* 移除原来的负边距 */
  padding-top: 10px; /* 添加少量内边距 */
}

/* 可选：调整对话框整体位置 */
:deep(.el-dialog) {
  margin-top: 5vh !important; /* 稍微降低对话框位置 */
}
</style>