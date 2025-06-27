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
                <el-button type="primary" @click="handleQuery" :icon="Search">
                  查询
                </el-button>
                <el-button @click="handleReset" :icon="Refresh">
                  重置
                </el-button>
                <el-button type="success" @click="handleExport" :icon="Download">
                  导出
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
              <el-option
                v-for="item in departmentOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </div>
          
          <div class="display-actions">
            <el-button :icon="Printer" @click="handlePrint">打印</el-button>
            <el-button type="success" :icon="Download" @click="handleExportData">导出</el-button>
          </div>
        </div>
        
        <div class="table-container">
          <el-table 
            :data="tableData" 
            border 
            style="width: 100%"
            show-summary
            :summary-method="getSummaries"
          >
            <el-table-column prop="statisticsType" label="统计方式" align="center" />
            <el-table-column prop="prescriptionCount" label="已开处方数" align="center" />
            <el-table-column prop="prescriptionAmount" label="已开处方金额(元)" align="center" />
            <el-table-column prop="paidPrescriptionCount" label="已收费处方数" align="center" />
            <el-table-column prop="paidPrescriptionAmount" label="已收费处方金额(元)" align="center" />
            <el-table-column prop="unpaidPrescriptionCount" label="未收费处方数" align="center" />
            <el-table-column prop="unpaidPrescriptionAmount" label="未收费处方金额(元)" align="center" />
            <el-table-column prop="unpaidRatio" label="未收费比例(%)" align="center" />
          </el-table>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { Search, Refresh, Download, Printer } from '@element-plus/icons-vue'

// 查询参数
const queryParams = reactive({
  timeType: 'day',
  statisticsType: 'department',
  dateRange: ['2024-06-09', '2024-07-08']
})

// 科室选择
const selectedDepartment = ref('儿科')
const departmentOptions = ref([
  { value: '儿科', label: '儿科' },
  { value: '门诊外科', label: '门诊外科' },
  { value: '门诊内科', label: '门诊内科' },
  { value: '骨科', label: '骨科' },
  { value: '耳鼻喉科', label: '耳鼻喉科' }
])

// 表格数据
const tableData = ref([
  {
    statisticsType: '门诊外科',
    prescriptionCount: 130,
    prescriptionAmount: '3000.00',
    paidPrescriptionCount: 120,
    paidPrescriptionAmount: '2900.00',
    unpaidPrescriptionCount: 10,
    unpaidPrescriptionAmount: '100.00',
    unpaidRatio: '8.33'
  },
  {
    statisticsType: '门诊内科',
    prescriptionCount: 235,
    prescriptionAmount: '5000.00',
    paidPrescriptionCount: 230,
    paidPrescriptionAmount: '4500.00',
    unpaidPrescriptionCount: 5,
    unpaidPrescriptionAmount: '500.00',
    unpaidRatio: '2.13'
  },
  {
    statisticsType: '骨科',
    prescriptionCount: 125,
    prescriptionAmount: '2000.00',
    paidPrescriptionCount: 125,
    paidPrescriptionAmount: '2000.00',
    unpaidPrescriptionCount: 0,
    unpaidPrescriptionAmount: '0.00',
    unpaidRatio: '0'
  },
  {
    statisticsType: '耳鼻喉科',
    prescriptionCount: 150,
    prescriptionAmount: '3000.00',
    paidPrescriptionCount: 150,
    paidPrescriptionAmount: '3000.00',
    unpaidPrescriptionCount: 0,
    unpaidPrescriptionAmount: '0.00',
    unpaidRatio: '0'
  }
])

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
        sums[index] = sums[index].toFixed(2)
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

// 操作函数
const handleQuery = () => {
  console.log('查询参数:', queryParams)
  // 这里应该调用API获取数据
}

const handleReset = () => {
  queryParams.timeType = 'day'
  queryParams.statisticsType = 'department'
  queryParams.dateRange = ['2024-06-09', '2024-07-08']
}

const handleExport = () => {
  console.log('导出查询结果')
}

const handlePrint = () => {
  console.log('打印表格')
}

const handleExportData = () => {
  console.log('导出表格数据')
}
</script>

<style scoped>
.prescription-summary-container {
  padding: 20px;
  background-color: #f5f7fa;
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
</style>