<template>
  <div class="outpatient-payment-dashboard">
    <!-- 顶部标题和日期选择 -->
    <div class="dashboard-header">
      <h2>全院分析管理 / 门诊挂号分析</h2>
      <div class="date-picker">
        <el-date-picker
          v-model="selectedDate"
          type="date"
          placeholder="选择日期"
          value-format="YYYY-MM-DD"
        />
      </div>
    </div>

    <!-- 看板统计信息 -->
    <div class="stats-container">
      <!-- 1. 挂号人数卡片 -->
      <el-card class="stat-card">
        <div class="stat-header">
          <el-icon class="stat-icon"><User /></el-icon>
          <span class="stat-title">挂号人数</span>
        </div>
        <div class="stat-content">
          <div class="stat-main">
            <span class="stat-value">{{ prescriptionStats.today?.paidPrescriptions || 0 }}</span>
            <span class="stat-unit">人</span>
          </div>
          <div class="stat-compare" :class="getCompareClass(calcComparison(prescriptionStats.today?.paidPrescriptions, prescriptionStats.yesterday?.paidPrescriptions))">
            <el-icon v-if="calcComparison(prescriptionStats.today?.paidPrescriptions, prescriptionStats.yesterday?.paidPrescriptions) > 0"><ArrowUp /></el-icon>
            <el-icon v-else-if="calcComparison(prescriptionStats.today?.paidPrescriptions, prescriptionStats.yesterday?.paidPrescriptions) < 0"><ArrowDown /></el-icon>
            {{ formatCompare(calcComparison(prescriptionStats.today?.paidPrescriptions, prescriptionStats.yesterday?.paidPrescriptions), '人') }}
          </div>
        </div>
      </el-card>

      <!-- 2. 挂号金额卡片 -->
      <el-card class="stat-card">
        <div class="stat-header">
          <el-icon class="stat-icon"><Money /></el-icon>
          <span class="stat-title">挂号金额</span>
        </div>
        <div class="stat-content">
          <div class="stat-main">
            <span class="stat-value">{{ formatCurrency(prescriptionStats.today?.paidAmount) }}</span>
            <span class="stat-unit">元</span>
          </div>
          <div class="stat-compare" :class="getCompareClass(calcComparison(prescriptionStats.today?.paidAmount, prescriptionStats.yesterday?.paidAmount))">
            <el-icon v-if="calcComparison(prescriptionStats.today?.paidAmount, prescriptionStats.yesterday?.paidAmount) > 0"><ArrowUp /></el-icon>
            <el-icon v-else-if="calcComparison(prescriptionStats.today?.paidAmount, prescriptionStats.yesterday?.paidAmount) < 0"><ArrowDown /></el-icon>
            {{ formatCompare(calcComparison(prescriptionStats.today?.paidAmount, prescriptionStats.yesterday?.paidAmount), '元') }}
          </div>
        </div>
      </el-card>

      <!-- 3. 退号人数卡片 -->
      <el-card class="stat-card">
        <div class="stat-header">
          <el-icon class="stat-icon"><RefreshLeft /></el-icon>
          <span class="stat-title">退号人数</span>
        </div>
        <div class="stat-content">
          <div class="stat-main">
            <span class="stat-value">{{ prescriptionStats.today?.refundPrescriptions || 0 }}</span>
            <span class="stat-unit">人</span>
          </div>
          <div class="stat-compare" :class="getCompareClass(calcComparison(prescriptionStats.today?.refundPrescriptions, prescriptionStats.yesterday?.refundPrescriptions))">
            <el-icon v-if="calcComparison(prescriptionStats.today?.refundPrescriptions, prescriptionStats.yesterday?.refundPrescriptions) > 0"><ArrowUp /></el-icon>
            <el-icon v-else-if="calcComparison(prescriptionStats.today?.refundPrescriptions, prescriptionStats.yesterday?.refundPrescriptions) < 0"><ArrowDown /></el-icon>
            {{ formatCompare(calcComparison(prescriptionStats.today?.refundPrescriptions, prescriptionStats.yesterday?.refundPrescriptions), '人') }}
          </div>
        </div>
      </el-card>

      <!-- 4. 退号金额卡片 -->
      <el-card class="stat-card">
        <div class="stat-header">
          <el-icon class="stat-icon"><Document /></el-icon>
          <span class="stat-title">退号金额</span>
        </div>
        <div class="stat-content">
          <div class="stat-main">
            <span class="stat-value">{{ formatCurrency(prescriptionStats.today?.refundAmount) }}</span>
            <span class="stat-unit">元</span>
          </div>
          <div class="stat-compare" :class="getCompareClass(calcComparison(prescriptionStats.today?.refundAmount, prescriptionStats.yesterday?.refundAmount))">
            <el-icon v-if="calcComparison(prescriptionStats.today?.refundAmount, prescriptionStats.yesterday?.refundAmount) > 0"><ArrowUp /></el-icon>
            <el-icon v-else-if="calcComparison(prescriptionStats.today?.refundAmount, prescriptionStats.yesterday?.refundAmount) < 0"><ArrowDown /></el-icon>
            {{ formatCompare(calcComparison(prescriptionStats.today?.refundAmount, prescriptionStats.yesterday?.refundAmount), '元') }}
          </div>
        </div>
      </el-card>
    </div>

    <!-- 图表区域 -->
    <div class="charts-container">
      <!-- 1. 挂号收退费趋势 -->
      <el-card class="chart-card">
        <div class="chart-title">挂号收退费趋势</div>
        <div class="chart-unit">单位：元</div>
        <div class="chart-container" ref="trendChart" style="height: 300px;"></div>
      </el-card>

      <!-- 2. 挂号人群性别比例 -->
      <el-card class="chart-card">
        <div class="chart-title">挂号人群性别比例</div>
        <div class="chart-container" ref="paymentChart" style="height: 300px;"></div>
      </el-card>

      <!-- 3. 热门医生排行 -->
      <el-card class="chart-card">
        <div class="chart-title">热门医生排行(前五)</div>
        <div class="chart-container" ref="doctorChart" style="height: 300px;"></div>
      </el-card>

      <!-- 4. 热门科室排行 -->
      <el-card class="chart-card">
        <div class="chart-title">热门科室排行(前五)</div>
        <div class="chart-container" ref="departmentChart" style="height: 300px;"></div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { User, Money, RefreshLeft, Document, ArrowDown, ArrowUp } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getRegistrationByDate, getGenderStatsByDate, getRegistrationTotal } from '@/api/prescription'
import dayjs from 'dayjs'

// 当前选中的日期
const selectedDate = ref(dayjs().format('YYYY-MM-DD'))

// 处方统计数据
const prescriptionStats = ref({
  today: null,
  yesterday: null
})

// 7天趋势数据
const trendData = ref({
  dates: [],
  totalAmounts: [],
  totalRefunds: []
})

// 图表引用
const trendChart = ref(null)
const paymentChart = ref(null)
const doctorChart = ref(null)
const departmentChart = ref(null)

// 图表实例
let trendChartInstance = null
let paymentChartInstance = null
let doctorChartInstance = null
let departmentChartInstance = null

// 统一色调配置
const chartColors = {
  blue: '#409EFF',
  green: '#67C23A',
  orange: '#E6A23C',
  red: '#F56C6C',
  gray: '#909399',
  lightBlue: '#79bbff',
  lightGreen: '#95d475',
  lightOrange: '#eebe77',
  lightRed: '#f89898',
  lightGray: '#a6a9ad'
}

// 获取最近7天日期（包括所选日期）
const getLast7Days = (date) => {
  const dates = []
  for (let i = 6; i >= 0; i--) {
    dates.push(dayjs(date).subtract(i, 'day').format('YYYY-MM-DD'))
  }
  return dates
}

// 获取7天趋势数据
const fetchTrendData = async () => {
  try {
    const dates = getLast7Days(selectedDate.value)
    const requests = dates.map(date => {
      return getRegistrationTotal({
        startTime: `${date} 00:00:00`,
        endTime: `${date} 23:59:59`,
        timeType: "day",
        groupBy: "department"
      })
    })

    const responses = await Promise.all(requests)
    
    const totalAmounts = []
    const totalRefunds = []
    
    responses.forEach(res => {
      totalAmounts.push(res.data.totalAmount || 0)
      totalRefunds.push(res.data.totalRefund || 0)
    })

    trendData.value = {
      dates: dates.map(date => dayjs(date).format('MM-DD')),
      totalAmounts,
      totalRefunds
    }

    updateTrendChart()
  } catch (error) {
    console.error('获取趋势数据失败:', error)
    // 重置数据
    const dates = getLast7Days(selectedDate.value)
    trendData.value = {
      dates: dates.map(date => dayjs(date).format('MM-DD')),
      totalAmounts: Array(7).fill(0),
      totalRefunds: Array(7).fill(0)
    }
    updateTrendChart()
  }
}

// 更新趋势图表
const updateTrendChart = () => {
  if (!trendChartInstance) return
  
  trendChartInstance.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
      formatter: params => {
        let result = `${params[0].axisValue}<br/>`
        params.forEach(item => {
          result += `${item.seriesName}: ${formatCurrency(item.value)}元<br/>`
        })
        return result
      }
    },
    legend: {
      data: ['挂号金额', '退号金额'],
      bottom: 0,
      textStyle: {
        color: '#606266'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: trendData.value.dates,
      axisLine: {
        lineStyle: {
          color: '#DCDFE6'
        }
      },
      axisLabel: {
        color: '#606266'
      }
    },
    yAxis: {
      type: 'value',
      name: '金额（元）',
      nameTextStyle: {
        color: '#606266'
      },
      axisLine: {
        lineStyle: {
          color: '#DCDFE6'
        }
      },
      axisLabel: {
        color: '#606266',
        formatter: value => formatCurrency(value)
      },
      splitLine: {
        lineStyle: {
          color: '#EBEEF5'
        }
      }
    },
    series: [
      {
        name: '挂号金额',
        type: 'line',
        data: trendData.value.totalAmounts,
        smooth: true,
        itemStyle: {
          color: chartColors.blue
        },
        lineStyle: {
          width: 3
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64, 158, 255, 0.5)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
          ])
        },
        symbol: 'circle',
        symbolSize: 8,
        emphasis: {
          itemStyle: {
            color: chartColors.lightBlue
          }
        }
      },
      {
        name: '退号金额',
        type: 'line',
        data: trendData.value.totalRefunds,
        smooth: true,
        itemStyle: {
          color: chartColors.red
        },
        lineStyle: {
          width: 3
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(245, 108, 108, 0.5)' },
            { offset: 1, color: 'rgba(245, 108, 108, 0.1)' }
          ])
        },
        symbol: 'circle',
        symbolSize: 8,
        emphasis: {
          itemStyle: {
            color: chartColors.lightRed
          }
        }
      }
    ]
  })
}

// 获取热门医生数据
const getTopDoctors = (data) => {
  const doctorMap = {}
  
  data.forEach(item => {
    if (item.regState !== 'CANCELLED') {
      const doctorName = item.regdocName
      doctorMap[doctorName] = (doctorMap[doctorName] || 0) + 1
    }
  })

  return Object.entries(doctorMap)
    .map(([name, count]) => ({ name, count }))
    .sort((a, b) => b.count - a.count)
    .slice(0, 5)
}

// 获取热门科室数据
const getTopDepartments = (data) => {
  const departmentMap = {}
  
  data.forEach(item => {
    if (item.regState !== 'CANCELLED') {
      const depName = item.regdepName
      departmentMap[depName] = (departmentMap[depName] || 0) + 1
    }
  })

  return Object.entries(departmentMap)
    .map(([name, count]) => ({ name, count }))
    .sort((a, b) => b.count - a.count)
    .slice(0, 5)
}

// 获取挂号统计数据
const fetchRegistrationStats = async () => {
  try {
    // 获取当天数据
    const todayRes = await getRegistrationByDate({
      date: selectedDate.value
    })

    // 获取昨天数据
    const yesterdayDate = dayjs(selectedDate.value).subtract(1, 'day').format('YYYY-MM-DD')
    const yesterdayRes = await getRegistrationByDate({
      date: yesterdayDate
    })

    // 处理当天数据
    const todayData = todayRes.data.data || []
    const paidToday = todayData.filter(item => item.regState !== 'CANCELLED')
    const refundToday = todayData.filter(item => item.regState === 'CANCELLED')

    // 处理昨天数据
    const yesterdayData = yesterdayRes.data.data || []
    const paidYesterday = yesterdayData.filter(item => item.regState !== 'CANCELLED')
    const refundYesterday = yesterdayData.filter(item => item.regState === 'CANCELLED')

    // 更新统计数据
    prescriptionStats.value = {
      today: {
        paidPrescriptions: paidToday.length,
        paidAmount: paidToday.reduce((sum, item) => sum + item.regfee, 0),
        refundPrescriptions: refundToday.length,
        refundAmount: refundToday.reduce((sum, item) => sum + item.regfee, 0),
        topDoctors: getTopDoctors(todayData),
        topDepartments: getTopDepartments(todayData)
      },
      yesterday: {
        paidPrescriptions: paidYesterday.length,
        paidAmount: paidYesterday.reduce((sum, item) => sum + item.regfee, 0),
        refundPrescriptions: refundYesterday.length,
        refundAmount: refundYesterday.reduce((sum, item) => sum + item.regfee, 0)
      }
    }

    // 更新图表
    updateDoctorChart()
    updateDepartmentChart()
    updateGenderChart()
  } catch (error) {
    console.error('获取挂号数据失败:', error)
    prescriptionStats.value = {
      today: {
        paidPrescriptions: 0,
        paidAmount: 0,
        refundPrescriptions: 0,
        refundAmount: 0,
        topDoctors: [],
        topDepartments: []
      },
      yesterday: {
        paidPrescriptions: 0,
        paidAmount: 0,
        refundPrescriptions: 0,
        refundAmount: 0
      }
    }
  }
}

// 更新医生排行榜图表
const updateDoctorChart = () => {
  if (!doctorChartInstance || !prescriptionStats.value.today?.topDoctors) return

  const topDoctors = prescriptionStats.value.today.topDoctors
  
  doctorChartInstance.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: { 
        type: 'shadow' 
      },
      formatter: params => `
        <div>医生: ${params[0].axisValue}</div>
        <div>挂号人数: ${params[0].data}</div>
      `,
      backgroundColor: 'rgba(255, 255, 255, 0.9)',
      borderColor: '#DCDFE6',
      textStyle: {
        color: '#606266'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'value',
      name: '挂号人数',
      nameTextStyle: {
        color: '#606266'
      },
      axisLine: {
        lineStyle: {
          color: '#DCDFE6'
        }
      },
      axisLabel: {
        color: '#606266'
      },
      splitLine: {
        lineStyle: {
          color: '#EBEEF5'
        }
      }
    },
    yAxis: {
      type: 'category',
      inverse: true,
      data: topDoctors.map(doctor => doctor.name),
      axisLine: {
        lineStyle: {
          color: '#DCDFE6'
        }
      },
      axisLabel: {
        color: '#606266',
        fontSize: 12
      }
    },
    series: [{
      name: '挂号人数',
      type: 'bar',
      data: topDoctors.map(doctor => doctor.count),
      itemStyle: {
        color: params => [chartColors.blue, chartColors.green, chartColors.orange, chartColors.red, chartColors.gray][params.dataIndex],
        borderRadius: [0, 4, 4, 0]
      },
      label: {
        show: true,
        position: 'right',
        color: '#606266'
      }
    }]
  })
}

// 获取性别统计数据
const fetchGenderStats = async () => {
  try {
    // 直接传递字符串参数而不是对象
    const res = await getGenderStatsByDate(selectedDate.value)
    const genderData = res.data.data || { '男': 0, '女': 0 }
    updateGenderChart(genderData)
  } catch (error) {
    console.error('获取性别数据失败:', error)
    updateGenderChart({ '男': 0, '女': 0 })
  }
}

// 更新性别比例图表
const updateGenderChart = (genderData = { '男': 0, '女': 0 }) => {
  if (!paymentChartInstance) return
  
  paymentChartInstance.setOption({
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c}人 ({d}%)',
      backgroundColor: 'rgba(255, 255, 255, 0.9)',
      borderColor: '#DCDFE6',
      textStyle: {
        color: '#606266'
      }
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center',
      textStyle: {
        color: '#606266'
      },
      data: ['男性', '女性']
    },
    series: [{
      name: '性别比例',
      type: 'pie',
      radius: ['50%', '70%'],
      center: ['40%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2,
        color: params => params.dataIndex === 0 ? chartColors.blue : chartColors.red
      },
      label: { 
        show: false,
        color: '#606266'
      },
      emphasis: {
        label: { 
          show: true, 
          fontSize: '14', 
          fontWeight: 'bold',
          color: '#303133'
        },
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      },
      labelLine: { show: false },
      data: [
        { value: genderData['男'] || 0, name: '男性' },
        { value: genderData['女'] || 0, name: '女性' }
      ]
    }]
  })
}

// 更新科室排行榜图表
const updateDepartmentChart = () => {
  if (!departmentChartInstance || !prescriptionStats.value.today?.topDepartments) return

  const topDepartments = prescriptionStats.value.today.topDepartments
  
  departmentChartInstance.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: { 
        type: 'shadow' 
      },
      formatter: params => `
        <div>科室: ${params[0].axisValue}</div>
        <div>挂号人数: ${params[0].data}</div>
      `,
      backgroundColor: 'rgba(255, 255, 255, 0.9)',
      borderColor: '#DCDFE6',
      textStyle: {
        color: '#606266'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'value',
      name: '挂号人数',
      nameTextStyle: {
        color: '#606266'
      },
      axisLine: {
        lineStyle: {
          color: '#DCDFE6'
        }
      },
      axisLabel: {
        color: '#606266'
      },
      splitLine: {
        lineStyle: {
          color: '#EBEEF5'
        }
      }
    },
    yAxis: {
      type: 'category',
      inverse: true,
      data: topDepartments.map(dep => dep.name.length > 6 ? dep.name.substring(0, 6) + '...' : dep.name),
      axisLine: {
        lineStyle: {
          color: '#DCDFE6'
        }
      },
      axisLabel: {
        color: '#606266',
        fontSize: 12
      }
    },
    series: [{
      name: '挂号人数',
      type: 'bar',
      data: topDepartments.map(dep => dep.count),
      itemStyle: {
        color: params => [chartColors.blue, chartColors.green, chartColors.orange, chartColors.red, chartColors.gray][params.dataIndex],
        borderRadius: [0, 4, 4, 0]
      },
      label: {
        show: true,
        position: 'right',
        color: '#606266'
      }
    }]
  })
}

// 格式化金额
const formatCurrency = (value) => {
  if (!value) return '0.00'
  return parseFloat(value).toLocaleString('zh-CN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
}

// 计算比较值
const calcComparison = (todayVal, yesterdayVal) => {
  if (todayVal === null || yesterdayVal === null) return 0
  return todayVal - yesterdayVal
}

// 格式化比较显示
const formatCompare = (value, unit) => {
  const formattedValue = Math.abs(value).toFixed(2)
  if (value > 0) return `较昨日增加${formattedValue}${unit}`
  if (value < 0) return `较昨日减少${formattedValue}${unit}`
  return `与昨日持平`
}

// 获取比较样式类
const getCompareClass = (value) => {
  if (value > 0) return 'positive'
  if (value < 0) return 'negative'
  return ''
}

// 初始化图表
const initCharts = () => {
  // 销毁旧图表
  if (trendChartInstance) trendChartInstance.dispose()
  if (paymentChartInstance) paymentChartInstance.dispose()
  if (doctorChartInstance) doctorChartInstance.dispose()
  if (departmentChartInstance) departmentChartInstance.dispose()

  // 1. 挂号收退费趋势图
  trendChartInstance = echarts.init(trendChart.value)
  updateTrendChart()

  // 2. 挂号人群性别比例
  paymentChartInstance = echarts.init(paymentChart.value)
  updateGenderChart()

  // 3. 热门医生排行
  doctorChartInstance = echarts.init(doctorChart.value)
  if (prescriptionStats.value.today?.topDoctors) {
    updateDoctorChart()
  } else {
    doctorChartInstance.setOption({
      xAxis: { type: 'value', name: '挂号人数' },
      yAxis: { type: 'category', data: [] },
      series: [{ type: 'bar', data: [] }]
    })
  }

  // 4. 热门科室排行
  departmentChartInstance = echarts.init(departmentChart.value)
  if (prescriptionStats.value.today?.topDepartments) {
    updateDepartmentChart()
  } else {
    departmentChartInstance.setOption({
      xAxis: { type: 'value', name: '挂号人数' },
      yAxis: { type: 'category', data: [] },
      series: [{ type: 'bar', data: [] }]
    })
  }
}

// 监听日期变化
watch(selectedDate, () => {
  fetchRegistrationStats()
  fetchGenderStats()
  fetchTrendData()
})

// 组件挂载时初始化
onMounted(() => {
  initCharts()
  fetchRegistrationStats()
  fetchGenderStats()
  fetchTrendData()
})

// 窗口大小变化时重新调整图表大小
window.addEventListener('resize', () => {
  trendChartInstance?.resize()
  paymentChartInstance?.resize()
  doctorChartInstance?.resize()
  departmentChartInstance?.resize()
})
</script>

<style scoped>
.outpatient-payment-dashboard {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.dashboard-header h2 {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.stats-container {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  height: 100%;
  position: relative;
}

.stat-main {
  display: flex;
  align-items: flex-end;
  margin-bottom: 10px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
}

.stat-unit {
  font-size: 14px;
  color: #999;
  margin-left: 5px;
  margin-bottom: 3px;
}

.stat-compare {
  font-size: 12px;
  margin-bottom: 15px;
}

.stat-compare.positive {
  color: #67C23A;
}

.stat-compare.negative {
  color: #F56C6C;
}

.stat-compare.neutral {
  color: #909399;
}

.stat-detail {
  font-size: 13px;
  color: #666;
  margin-bottom: 10px;
}

.stat-detail div {
  margin-bottom: 5px;
}

.view-detail {
  position: absolute;
  right: 15px;
  bottom: 15px;
  padding: 0;
}

.charts-container {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  grid-template-rows: repeat(2, 1fr);
  gap: 20px;
}

.chart-card {
  height: 100%;
}

.chart-title {
  font-size: 14px;
  font-weight: bold;
  margin-bottom: 5px;
}

.chart-unit {
  font-size: 12px;
  color: #999;
  margin-bottom: 10px;
}

.chart-container {
  width: 100%;
}

.stat-card {
  height: 100%;
  position: relative;
}

.stat-header {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  font-size: 14px;
  color: #666;
}

.stat-icon {
  margin-right: 8px;
  font-size: 16px;
}

.stat-title {
  font-weight: 500;
}

.stat-content {
  padding: 0 8px;
}

.stat-main {
  display: flex;
  align-items: flex-end;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.stat-unit {
  font-size: 14px;
  color: #999;
  margin-left: 4px;
  margin-bottom: 2px;
}

.stat-compare {
  display: flex;
  align-items: center;
  font-size: 12px;
  margin-bottom: 12px;
}

.stat-compare .el-icon {
  margin-right: 4px;
  font-size: 12px;
}

.stat-compare.negative {
  color: #f56c6c;
}

.stat-compare.positive {
  color: #67c23a;
}

.stat-detail {
  font-size: 13px;
  color: #666;
  line-height: 1.6;
}

.detail-value {
  font-weight: 500;
  color: #333;
}
</style>