<template>
  <div class="order-fee-summary-container">
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
                  <el-radio-button label="cashier">按收费员</el-radio-button>
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
            <el-table-column prop="paymentCount" label="缴费单数" align="center" sortable />
            <el-table-column prop="paymentAmount" label="缴费金额(元)" align="center" sortable>
              <template #default="{row}">
                {{ formatCurrency(row.paymentAmount) }}
              </template>
            </el-table-column>
            <el-table-column prop="refundAmount" label="退费金额(元)" align="center" sortable>
              <template #default="{row}">
                {{ formatCurrency(row.refundAmount) }}
              </template>
            </el-table-column>
            <el-table-column prop="actualAmount" label="实际金额(元)" align="center" sortable>
              <template #default="{row}">
                {{ formatCurrency(row.actualAmount) }}
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
    
    <!-- 缴费明细对话框 -->
    <el-dialog 
      v-model="detailDialogVisible" 
      :title="`${currentDetail.statisticsType} - 缴费明细`"
      width="90%"
      top="5vh"
    >
      <div class="detail-table-container">
        <el-table 
          :data="paymentDetails" 
          border 
          style="width: 100%"
          v-loading="detailLoading"
          stripe
        >
          <el-table-column prop="registrationNo" label="门(就)诊号" align="center" width="120" />
          <el-table-column prop="cardNo" label="就诊卡号" align="center" width="120" />
          <el-table-column prop="patientName" label="患者姓名" align="center" width="100" />
          <el-table-column prop="department" label="开单科室" align="center" width="120" />
          <el-table-column prop="doctor" label="开单医生" align="center" width="100" />
          <el-table-column prop="amount" label="缴费金额(元)" align="center" width="120">
            <template #default="{row}">
              {{ formatCurrency(row.amount) }}
            </template>
          </el-table-column>
          <el-table-column prop="status" label="缴费状态" align="center" width="120">
            <template #default="{row}">
              <el-tag :type="getStatusTagType(row.status)">
                {{ row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="paymentMethod" label="支付方式" align="center" width="120" />
          <el-table-column prop="receiptNo" label="订单收据号" align="center" width="150" />
          <el-table-column prop="paymentTime" label="收费时间" align="center" width="180" />
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
import { ref, reactive, computed } from 'vue'
import { Refresh, Download, Printer, PieChart } from '@element-plus/icons-vue'
import * as XLSX from 'xlsx'
import { ElMessage } from 'element-plus'

// 查询参数
const queryParams = reactive({
  timeType: 'month',
  statisticsType: 'department',
  dateRange: [getFirstDayOfMonth(), getLastDayOfMonth()]
})

// 分页参数
const currentPage = ref(1)
const pageSize = ref(10)
const totalItems = ref(0)
const showPagination = ref(true)

// 加载状态
const loading = ref(false)

// 科室选择
const selectedDepartment = ref('')
const departmentOptions = ref([
  { value: '门诊外科', label: '门诊外科' },
  { value: '门诊内科', label: '门诊内科' },
  { value: '骨科', label: '骨科' },
  { value: '耳鼻喉科', label: '耳鼻喉科' }
])

// 原始表格数据
const tableData = ref([])

// 明细对话框相关
const detailDialogVisible = ref(false)
const currentDetail = ref({})
const paymentDetails = ref([])
const detailLoading = ref(false)
const detailCurrentPage = ref(1)
const detailPageSize = ref(10)
const detailTotal = ref(0)

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

// 根据选择的科室过滤数据
const filteredTableData = computed(() => {
  let data = tableData.value
  if (selectedDepartment.value) {
    data = data.filter(item => item.statisticsType.includes(selectedDepartment.value))
  }
  
  // 分页处理
  if (showPagination.value) {
    const start = (currentPage.value - 1) * pageSize.value
    return data.slice(start, start + pageSize.value)
  }
  return data
})

// 格式化货币显示
const formatCurrency = (value) => {
  return parseFloat(value).toLocaleString('zh-CN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
}

// 获取状态标签类型
const getStatusTagType = (status) => {
  switch(status) {
    case '已缴费': return 'success'
    case '部分退费': return 'warning'
    case '已退费': return 'danger'
    case '未缴费': return 'info'
    default: return ''
  }
}

// 获取表格数据
const fetchTableData = async () => {
  try {
    loading.value = true
    // 模拟API请求
    await new Promise(resolve => setTimeout(resolve, 800))
    
    // 模拟数据 - 实际项目中替换为API调用
    const mockData = [
      {
        statisticsType: '门诊外科',
        paymentCount: 200,
        paymentAmount: '6000.00',
        refundAmount: '300.00',
        actualAmount: '5700.00'
      },
      {
        statisticsType: '门诊内科',
        paymentCount: 300,
        paymentAmount: '8000.00',
        refundAmount: '200.00',
        actualAmount: '7800.00'
      },
      {
        statisticsType: '骨科',
        paymentCount: 125,
        paymentAmount: '5600.00',
        refundAmount: '500.00',
        actualAmount: '5100.00'
      },
      {
        statisticsType: '耳鼻喉科',
        paymentCount: 150,
        paymentAmount: '6800.00',
        refundAmount: '350.00',
        actualAmount: '6450.00'
      },
      // 更多模拟数据...
      ...Array.from({length: 10}, (_, i) => ({
        statisticsType: `测试科室${i+1}`,
        paymentCount: Math.floor(Math.random() * 200) + 50,
        paymentAmount: (Math.random() * 10000 + 2000).toFixed(2),
        refundAmount: (Math.random() * 1000).toFixed(2),
        actualAmount: (Math.random() * 10000 + 2000).toFixed(2)
      }))
    ]
    
    tableData.value = mockData
    totalItems.value = mockData.length
    ElMessage.success('数据加载成功')
  } catch (error) {
    ElMessage.error('数据加载失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 获取缴费明细数据
const fetchPaymentDetails = async (item) => {
  try {
    detailLoading.value = true
    // 模拟API请求
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // 模拟数据 - 实际项目中替换为API调用
    const mockDetails = [
      {
        registrationNo: '6520050869',
        cardNo: '20050869',
        patientName: '张晓晓',
        department: item.statisticsType,
        doctor: '李医生',
        amount: '30.00',
        status: '已缴费',
        paymentMethod: '现金',
        receiptNo: 'REC20240106001',
        paymentTime: '2024-01-06 08:00:00'
      },
      {
        registrationNo: '6520050868',
        cardNo: '20050868',
        patientName: '王一',
        department: item.statisticsType,
        doctor: '李医生',
        amount: '30.00',
        status: '已缴费',
        paymentMethod: '现金',
        receiptNo: 'REC20240106002',
        paymentTime: '2024-01-06 08:05:00'
      },
      {
        registrationNo: '6520050867',
        cardNo: '20050867',
        patientName: '李梅',
        department: item.statisticsType,
        doctor: '李医生',
        amount: '30.00',
        status: '已缴费',
        paymentMethod: '现金',
        receiptNo: 'REC20240106003',
        paymentTime: '2024-01-06 08:10:00'
      },
      {
        registrationNo: '6520050866',
        cardNo: '20050866',
        patientName: '张晓珂',
        department: '儿科',
        doctor: '王医生',
        amount: '30.00',
        status: '已缴费',
        paymentMethod: '现金',
        receiptNo: 'REC20240106004',
        paymentTime: '2024-01-06 08:15:00'
      },
      {
        registrationNo: '6520050865',
        cardNo: '20050865',
        patientName: '刘克',
        department: '骨科',
        doctor: '张医生',
        amount: '200.00',
        status: '已缴费',
        paymentMethod: '现金',
        receiptNo: 'REC20240106005',
        paymentTime: '2024-01-06 08:20:00'
      },
      {
        registrationNo: '6520050864',
        cardNo: '20050864',
        patientName: '小明',
        department: '骨科',
        doctor: '张医生',
        amount: '200.00',
        status: '已缴费',
        paymentMethod: '现金',
        receiptNo: 'REC20240106006',
        paymentTime: '2024-01-06 08:25:00'
      },
      {
        registrationNo: '6520050863',
        cardNo: '20050863',
        patientName: '张三',
        department: '骨科',
        doctor: '张医生',
        amount: '200.00',
        status: '已缴费',
        paymentMethod: '微信扫码',
        receiptNo: 'REC20240106007',
        paymentTime: '2024-01-06 08:30:00'
      },
      {
        registrationNo: '6520050862',
        cardNo: '20050862',
        patientName: '张三',
        department: '皮肤科',
        doctor: '李医生',
        amount: '66.00',
        status: '已缴费',
        paymentMethod: '微信扫码',
        receiptNo: 'REC20240106008',
        paymentTime: '2024-01-06 08:35:00'
      },
      {
        registrationNo: '6520050860',
        cardNo: '20050860',
        patientName: '张三',
        department: '皮肤科',
        doctor: '李医生',
        amount: '20.00',
        status: '部分退费',
        paymentMethod: '微信扫码',
        receiptNo: 'REC20240106009',
        paymentTime: '2024-01-06 08:40:00'
      }
    ]
    
    paymentDetails.value = mockDetails
    detailTotal.value = mockDetails.length
  } catch (error) {
    ElMessage.error('获取明细数据失败: ' + error.message)
  } finally {
    detailLoading.value = false
  }
}

// 合计行计算方法
const getSummaries = (param) => {
  const { columns, data } = param
  const sums = []
  columns.forEach((column, index) => {
    if (index === 0) {
      sums[index] = '合计'
      return
    }
    
    const values = data.map(item => {
      const val = item[column.property]
      return !isNaN(parseFloat(val)) ? parseFloat(val) : 0
    })
    
    if (!values.every(value => isNaN(value))) {
      sums[index] = values.reduce((prev, curr) => {
        const value = Number(curr)
        if (!isNaN(value)) {
          return prev + curr
        } else {
          return prev
        }
      }, 0)
      
      // 格式化金额
      if (column.property.includes('Amount')) {
        sums[index] = formatCurrency(sums[index])
      }
    } else {
      sums[index] = 'N/A'
    }
  })
  
  return sums
}

// 显示明细对话框
const showDetail = (item) => {
  currentDetail.value = item
  detailDialogVisible.value = true
  detailCurrentPage.value = 1
  fetchPaymentDetails(item)
}

// 汇总功能（相当于查询功能）
const handleSummary = async () => {
  await fetchTableData()
}

// 重置功能
const handleReset = () => {
  queryParams.timeType = 'month'
  queryParams.statisticsType = 'department'
  queryParams.dateRange = [getFirstDayOfMonth(), getLastDayOfMonth()]
  selectedDepartment.value = ''
  currentPage.value = 1
  console.log('重置筛选条件')
}

// 打印功能
const handlePrint = () => {
  console.log('打印表格')
  window.print()
}

// 分页大小变化
const handleSizeChange = (val) => {
  pageSize.value = val
}

// 页码变化
const handlePageChange = (val) => {
  currentPage.value = val
}

// 明细分页大小变化
const handleDetailSizeChange = (val) => {
  detailPageSize.value = val
}

// 明细页码变化
const handleDetailPageChange = (val) => {
  detailCurrentPage.value = val
}

// 导出功能
const handleExportData = () => {
  // 1. 创建工作簿
  const wb = XLSX.utils.book_new()
  
  // 2. 准备数据（处理表头和表格数据）
  const headers = [
    '统计方式', '缴费单数', '缴费金额(元)', 
    '退费金额(元)', '实际金额(元)'
  ]
  
  const data = [
    headers,
    ...tableData.value.map(item => [
      item.statisticsType,
      item.paymentCount,
      item.paymentAmount,
      item.refundAmount,
      item.actualAmount
    ])
  ]
  
  // 添加合计行
  const totals = getSummaries({
    columns: [
      { property: 'statisticsType' },
      { property: 'paymentCount' },
      { property: 'paymentAmount' },
      { property: 'refundAmount' },
      { property: 'actualAmount' }
    ],
    data: tableData.value
  })
  data.push(['合计', ...totals.slice(1)])
  
  // 3. 创建工作表
  const ws = XLSX.utils.aoa_to_sheet(data)
  
  // 4. 设置列宽
  ws['!cols'] = [
    { wch: 15 }, // 统计方式
    { wch: 12 }, // 缴费单数
    { wch: 15 }, // 缴费金额
    { wch: 15 }, // 退费金额
    { wch: 15 }  // 实际金额
  ]
  
  // 5. 将工作表添加到工作簿
  XLSX.utils.book_append_sheet(wb, ws, '医嘱费用汇总')
  
  // 6. 生成Excel文件并下载
  const fileName = `医嘱费用汇总_${queryParams.dateRange[0]}_至_${queryParams.dateRange[1]}.xlsx`
  XLSX.writeFile(wb, fileName)
  
  ElMessage.success('导出成功')
}

// 初始化加载数据
fetchTableData()
</script>

<style scoped>
.order-fee-summary-container {
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

/* 调整对话框样式 */
:deep(.el-dialog__header) {
  padding-top: 20px;
  margin-bottom: -10px;
}

:deep(.el-dialog__title) {
  font-size: 16px;
  line-height: 1.5;
}
</style>