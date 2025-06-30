<template>
  <div class="prescription-summary-container">
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
              @change="handleDepartmentChange"
            >
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
            <el-table-column prop="prescriptionCount" label="已开处方数" align="center" sortable />
            <el-table-column prop="prescriptionAmount" label="已开处方金额(元)" align="center" sortable>
              <template #default="{row}">
                {{ formatCurrency(row.prescriptionAmount) }}
              </template>
            </el-table-column>
            <el-table-column prop="paidPrescriptionCount" label="已收费处方数" align="center" sortable />
            <el-table-column prop="paidPrescriptionAmount" label="已收费处方金额(元)" align="center" sortable>
              <template #default="{row}">
                {{ formatCurrency(row.paidPrescriptionAmount) }}
              </template>
            </el-table-column>
            <el-table-column prop="unpaidPrescriptionCount" label="未收费处方数" align="center" sortable />
            <el-table-column prop="unpaidPrescriptionAmount" label="未收费处方金额(元)" align="center" sortable>
              <template #default="{row}">
                {{ formatCurrency(row.unpaidPrescriptionAmount) }}
              </template>
            </el-table-column>
            <el-table-column prop="unpaidRatio" label="未收费比例(%)" align="center" sortable>
              <template #default="{row}">
                <el-tag :type="row.unpaidRatio > 5 ? 'danger' : 'success'">
                  {{ row.unpaidRatio }}%
                </el-tag>
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
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { Refresh, Download, Printer, PieChart } from '@element-plus/icons-vue'
import * as XLSX from 'xlsx'
import { ElMessage } from 'element-plus'
import { getPrescriptionStats } from '@/api/prescription'

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
const departmentOptions = ref([]) // 初始为空，将从API获取数据后填充

// 原始表格数据
const tableData = ref([])

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

// 获取数据并处理
const fetchTableData = async () => {
  try {
    loading.value = true
    
    const params = {
      startTime: `${queryParams.dateRange[0]} 00:00:00`,
      endTime: `${queryParams.dateRange[1]} 23:59:59`,
      timeType: queryParams.timeType,
      groupBy: queryParams.statisticsType === 'department' ? 'department' : 'doctor'
    }
    
    const response = await getPrescriptionStats(params)
    const responseData = response.data
    
    if (!responseData) {
      throw new Error('响应数据为空')
    }
    
    // 处理表格数据
    tableData.value = processTableData(responseData)
    
    // 更新科室选项（只在按科室统计时更新）
    if (queryParams.statisticsType === 'department') {
      updateDepartmentOptions(responseData)
    }
    
    totalItems.value = tableData.value.length
    ElMessage.success('数据加载成功')
  } catch (error) {
    console.error('数据加载失败:', error)
    ElMessage.error(`数据加载失败: ${error.message}`)
  } finally {
    loading.value = false
  }
}

// 处理表格数据
const processTableData = (apiData) => {
  try {

    console.log('原始API数据：', apiData)
    const groupData = queryParams.statisticsType === 'department' 
      ? apiData.groupedStats
      : apiData.groupedStats

      console.log('处理后的分组数据：', groupData)
    
    if (!groupData) {
      console.error('缺少分组数据:', apiData)
      return []
    }
    
    return groupData.map(item => ({
      statisticsType: item.groupName,
      prescriptionCount: item.totalPrescriptions,
      prescriptionAmount: item.totalAmount,
      paidPrescriptionCount: item.paidPrescriptions,
      paidPrescriptionAmount: item.paidAmount,
      unpaidPrescriptionCount: item.unpaidPrescriptions,
      unpaidPrescriptionAmount: item.unpaidAmount,
      unpaidRatio: parseFloat(item.unpaidRatio).toFixed(2),
      timePeriod: item.timePeriod
    }))
  } catch (error) {
    console.error('表格数据处理失败:', error)
    return []
  }
}

// 更新科室选项
const updateDepartmentOptions = (apiData) => {
  try {
    if (!apiData.byDepartment) {
      console.warn('API响应中缺少科室数据')
      departmentOptions.value = [{ value: '', label: '全部科室' }]
      return
    }
    
    // 使用Set去重并排序
    const uniqueDepartments = [...new Set(
      apiData.byDepartment.map(item => item.groupName)
    )].sort()
    
    departmentOptions.value = [
      { value: '', label: '全部科室' },
      ...uniqueDepartments.map(dept => ({
        value: dept,
        label: dept
      }))
    ]
  } catch (error) {
    console.error('更新科室选项失败:', error)
    departmentOptions.value = [{ value: '', label: '全部科室' }]
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
      
      // 格式化金额和百分比
      if (column.property.includes('Amount')) {
        sums[index] = formatCurrency(sums[index])
      } else if (column.property === 'unpaidRatio') {
        // 计算未收费比例
        const totalPrescription = data.reduce((sum, item) => sum + item.prescriptionCount, 0)
        const totalUnpaid = data.reduce((sum, item) => sum + item.unpaidPrescriptionCount, 0)
        sums[index] = totalPrescription > 0 ? ((totalUnpaid / totalPrescription) * 100).toFixed(2) : '0.00'
      }
    } else {
      sums[index] = 'N/A'
    }
  })
  
  return sums
}

// 科室变更处理
const handleDepartmentChange = () => {
  currentPage.value = 1 // 重置页码
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
}

// 打印功能
const handlePrint = () => {
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

// 导出功能
const handleExportData = () => {
  try {
    // 1. 创建工作簿
    const wb = XLSX.utils.book_new()
    
    // 2. 准备数据（处理表头和表格数据）
    const headers = [
      '统计方式', '已开处方数', '已开处方金额(元)',
      '已收费处方数', '已收费处方金额(元)', 
      '未收费处方数', '未收费处方金额(元)', '未收费比例(%)'
    ]
    
    const data = [
      headers,
      ...tableData.value.map(item => [
        item.statisticsType,
        item.prescriptionCount,
        item.prescriptionAmount,
        item.paidPrescriptionCount,
        item.paidPrescriptionAmount,
        item.unpaidPrescriptionCount,
        item.unpaidPrescriptionAmount,
        item.unpaidRatio + '%'
      ])
    ]
    
    // 添加合计行
    const totals = getSummaries({
      columns: [
        { property: 'statisticsType' },
        { property: 'prescriptionCount' },
        { property: 'prescriptionAmount' },
        { property: 'paidPrescriptionCount' },
        { property: 'paidPrescriptionAmount' },
        { property: 'unpaidPrescriptionCount' },
        { property: 'unpaidPrescriptionAmount' },
        { property: 'unpaidRatio' }
      ],
      data: tableData.value
    })
    data.push(['合计', ...totals.slice(1)])
    
    // 3. 创建工作表
    const ws = XLSX.utils.aoa_to_sheet(data)
    
    // 4. 设置列宽
    ws['!cols'] = [
      { wch: 15 }, // 统计方式
      { wch: 12 }, // 已开处方数
      { wch: 18 }, // 已开处方金额
      { wch: 12 }, // 已收费处方数
      { wch: 18 }, // 已收费处方金额
      { wch: 12 }, // 未收费处方数
      { wch: 18 }, // 未收费处方金额
      { wch: 15 }  // 未收费比例
    ]
    
    // 5. 将工作表添加到工作簿
    XLSX.utils.book_append_sheet(wb, ws, '门诊处方汇总')
    
    // 6. 生成Excel文件并下载
    const fileName = `门诊处方汇总_${queryParams.dateRange[0]}_至_${queryParams.dateRange[1]}.xlsx`
    XLSX.writeFile(wb, fileName)
    
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败: ' + (error.message || '请检查数据'))
  }
}

// 初始化加载数据
onMounted(() => {
  fetchTableData()
})
</script>

<style scoped>
.prescription-summary-container {
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
</style>