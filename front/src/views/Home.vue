<template>
  <div class="outpatient-payment-dashboard">
    <!-- 顶部标题和日期选择 -->
    <div class="dashboard-header">
      <h2>首页</h2>
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

      <!-- 3. 缴费人数卡片 -->
      <el-card class="stat-card">
        <div class="stat-header">
          <el-icon class="stat-icon"><User /></el-icon>
          <span class="stat-title">缴费人数</span>
        </div>
        <div class="stat-content">
          <div class="stat-main">
            <span class="stat-value">{{ prescriptionStats.today?.paidPrescriptions || 0 }}</span>
            <span class="stat-unit">人</span>
          </div>
          <div class="stat-compare" :class="getCompareClass(calcComparison(prescriptionStats.today?.paidPrescriptions, prescriptionStats.yesterday?.paidPrescriptions))">
            <el-icon v-if="calcComparison(prescriptionStats.today?.paidPrescriptions, prescriptionStats.yesterday?.paidPrescriptions) > 0"><ArrowUp /></el-icon>
            <el-icon v-else><ArrowDown /></el-icon>
            {{ formatCompare(calcComparison(prescriptionStats.today?.paidPrescriptions, prescriptionStats.yesterday?.paidPrescriptions), '人') }}
          </div>
        </div>
      </el-card>

      <!-- 4. 缴费金额卡片 -->
      <el-card class="stat-card">
        <div class="stat-header">
          <el-icon class="stat-icon"><Money /></el-icon>
          <span class="stat-title">缴费金额</span>
        </div>
        <div class="stat-content">
          <div class="stat-main">
            <span class="stat-value">{{ formatCurrency(prescriptionStats.today?.paidAmount) }}</span>
            <span class="stat-unit">元</span>
          </div>
          <div class="stat-compare" :class="getCompareClass(calcComparison(prescriptionStats.today?.paidAmount, prescriptionStats.yesterday?.paidAmount))">
            <el-icon v-if="calcComparison(prescriptionStats.today?.paidAmount, prescriptionStats.yesterday?.paidAmount) > 0"><ArrowUp /></el-icon>
            <el-icon v-else><ArrowDown /></el-icon>
            {{ formatCompare(calcComparison(prescriptionStats.today?.paidAmount, prescriptionStats.yesterday?.paidAmount), '元') }}
          </div>
        </div>
      </el-card>
    </div>

    <!-- 图表区域 -->
    <div class="charts-container">
      <!-- 1. 门诊收退费趋势 -->
      <el-card class="chart-card">
        <div class="chart-title">门诊收退费趋势</div>
        <div class="chart-unit">单位：元</div>
        <div class="chart-container" ref="trendChart" style="height: 300px;"></div>
      </el-card>

      <!-- 2. 支付方式比例 -->
      <el-card class="chart-card">
        <div class="chart-title">支付方式比例</div>
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

    <div>
    <!-- <p>消息总数: {{ messageCount }}</p> -->
    <ChatBot 
      @message-submitted="handleMessage"
      @button-clicked="handleButton"
      @tag-clicked="handleTag"
    />
  </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch, nextTick, toRaw } from 'vue'
import { User, Money, RefreshLeft, Document, ArrowDown, ArrowUp } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getRegistrationByDate, getGenderStatsByDate, getPrescriptionStats, getStatisticsByPaymentType } from '@/api/prescription'
import dayjs from 'dayjs'
import ChatBot from '@/components/ChatBot.vue'

const messageCount = ref(0)

const handleMessage = (message) => {
  console.log('用户发送的消息:', message)
  messageCount.value++
  // 在这里处理消息发送逻辑
}

const handleButton = (type) => {
  console.log('按钮被点击:', type)
  // 根据不同类型执行不同操作
}

const handleTag = (tag) => {
  console.log('标签被点击:', tag)
  // 根据标签执行不同操作
}

// 日期选择
const selectedDate = ref(dayjs().format('YYYY-MM-DD'))

// 处方统计数据
const prescriptionStats = reactive({
  today: null,       // 当天挂号数据
  yesterday: null,   // 前一天挂号数据
  trendData: [],     // 趋势数据
  paymentTypes: [],  // 支付方式数据
  doctorStats: [],   // 医生统计数据
  departmentStats: [] // 科室统计数据
})

// 加载状态
const loading = ref(false)
const trendLoading = ref(false)
const paymentTypeLoading = ref(false)
const doctorLoading = ref(false)
const departmentLoading = ref(false)
// const genderLoading = ref(false)

// 图表引用
const trendChart = ref(null)
const paymentChart = ref(null)
const doctorChart = ref(null)
const departmentChart = ref(null)

// 获取挂号统计数据
const fetchRegistrationStats = async () => {
  loading.value = true
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
    prescriptionStats.today = {
      paidPrescriptions: paidToday.length,
      paidAmount: paidToday.reduce((sum, item) => sum + item.regfee, 0),
      refundPrescriptions: refundToday.length,
      refundAmount: refundToday.reduce((sum, item) => sum + item.regfee, 0),
      totalAmount: paidToday.reduce((sum, item) => sum + (item.prescriptionAmount || 0), 0)
    }
    
    prescriptionStats.yesterday = {
      paidPrescriptions: paidYesterday.length,
      paidAmount: paidYesterday.reduce((sum, item) => sum + item.regfee, 0),
      refundPrescriptions: refundYesterday.length,
      refundAmount: refundYesterday.reduce((sum, item) => sum + item.regfee, 0),
      totalAmount: paidYesterday.reduce((sum, item) => sum + (item.prescriptionAmount || 0), 0)
    }

  } catch (error) {
    console.error('获取挂号数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取趋势数据
const fetchTrendData = async () => {
  trendLoading.value = true
  try {
    const endDate = dayjs(selectedDate.value)
    const startDate = endDate.subtract(9, 'day')
    
    const dateRange = []
    let currentDate = startDate.clone()
    
    while (currentDate.isBefore(endDate) || currentDate.isSame(endDate, 'day')) {
      dateRange.push(currentDate.format('YYYY-MM-DD'))
      currentDate = currentDate.add(1, 'day')
    }
    
    const promises = dateRange.map(date => {
      return getRegistrationByDate({ date })
    })
    
    const results = await Promise.all(promises)
    
    prescriptionStats.trendData = results.map((res, index) => {
      const data = res.data.data || []
      const paid = data.filter(item => item.regState !== 'CANCELLED')
      const refund = data.filter(item => item.regState === 'CANCELLED')
      
      return {
        date: dateRange[index],
        paidAmount: paid.reduce((sum, item) => sum + item.regfee, 0),
        refundAmount: refund.reduce((sum, item) => sum + item.regfee, 0)
      }
    })
    
    updateTrendChart()
    
  } catch (error) {
    console.error('获取趋势数据失败:', error)
  } finally {
    trendLoading.value = false
  }
}

// 获取支付方式数据
const fetchPaymentTypeData = async () => {
  paymentTypeLoading.value = true
  try {
    const res = await getStatisticsByPaymentType(selectedDate.value)
    prescriptionStats.paymentTypes = Array.isArray(res.data?.data) ? res.data.data : []
    await nextTick()
    updatePaymentChart()
  } catch (error) {
    console.error('获取支付方式数据失败:', error)
  } finally {
    paymentTypeLoading.value = false
  }
}

// 获取性别统计数据
// const fetchGenderStats = async () => {
//   genderLoading.value = true
//   try {
//     const res = await getGenderStatsByDate(selectedDate.value)
//     const genderData = res.data.data || { '男': 0, '女': 0 }
//     updateGenderChart(genderData)
//   } catch (error) {
//     console.error('获取性别数据失败:', error)
//     updateGenderChart({ '男': 0, '女': 0 })
//   } finally {
//     genderLoading.value = false
//   }
// }

// 获取医生统计数据
const fetchDoctorStats = async () => {
  doctorLoading.value = true
  try {
    const res = await getRegistrationByDate({ date: selectedDate.value })
    const todayData = res.data.data || []
    
    // 合并同一医生的挂号人数
    const doctorMap = new Map()
    todayData.forEach(item => {
      if (item.regState !== 'CANCELLED') {
        const currentCount = doctorMap.get(item.regdocName) || 0
        doctorMap.set(item.regdocName, currentCount + 1)
      }
    })
    
    // 转换为数组并排序
    prescriptionStats.doctorStats = Array.from(doctorMap.entries())
      .map(([name, count]) => ({ name, count }))
      .sort((a, b) => b.count - a.count)
      .slice(0, 5)
    
    updateDoctorChart()
    
  } catch (error) {
    console.error('获取医生统计数据失败:', error)
  } finally {
    doctorLoading.value = false
  }
}

// 获取科室统计数据
const fetchDepartmentStats = async () => {
  departmentLoading.value = true
  try {
    const res = await getRegistrationByDate({ date: selectedDate.value })
    const todayData = res.data.data || []
    
    // 合并同一科室的挂号人数
    const departmentMap = new Map()
    todayData.forEach(item => {
      if (item.regState !== 'CANCELLED') {
        const currentCount = departmentMap.get(item.regdepName) || 0
        departmentMap.set(item.regdepName, currentCount + 1)
      }
    })
    
    // 转换为数组并排序
    prescriptionStats.departmentStats = Array.from(departmentMap.entries())
      .map(([name, count]) => ({ name, count }))
      .sort((a, b) => b.count - a.count)
      .slice(0, 5)
    
    updateDepartmentChart()
    
  } catch (error) {
    console.error('获取科室统计数据失败:', error)
  } finally {
    departmentLoading.value = false
  }
}

// 更新趋势图表
const updateTrendChart = () => {
  if (!prescriptionStats.trendData.length || !trendChart.value) return
  
  const chart = echarts.getInstanceByDom(trendChart.value) || echarts.init(trendChart.value)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      formatter: params => `
        <div>日期: ${params[0].axisValue}</div>
        <div>挂号金额: ${formatCurrency(params[0].data)}元</div>
        <div>退号金额: ${formatCurrency(params[1].data)}元</div>
      `
    },
    legend: { data: ['挂号金额', '退号金额'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      data: prescriptionStats.trendData.map(item => dayjs(item.date).format('M-DD'))
    },
    yAxis: {
      type: 'value',
      name: '金额(元)'
    },
    series: [
      {
        name: '挂号金额',
        type: 'line',
        data: prescriptionStats.trendData.map(item => item.paidAmount),
        smooth: true,
        lineStyle: { width: 3 },
        itemStyle: { color: '#409EFF' }
      },
      {
        name: '退号金额',
        type: 'line',
        data: prescriptionStats.trendData.map(item => item.refundAmount),
        smooth: true,
        lineStyle: { width: 3 },
        itemStyle: { color: '#F56C6C' }
      }
    ]
  }
  
  chart.setOption(option)
}

// 更新支付方式图表
const updatePaymentChart = () => {
  const paymentTypes = toRaw(prescriptionStats.paymentTypes) || []
  if (!paymentTypes.length || !paymentChart.value) return
  
  const chart = echarts.getInstanceByDom(paymentChart.value) || echarts.init(paymentChart.value)
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: ¥{c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center',
      data: paymentTypes.map(item => item.paymentType)
    },
    series: [{
      name: '支付方式',
      type: 'pie',
      radius: ['50%', '70%'],
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: { show: false },
      emphasis: {
        label: { show: true, fontSize: '18', fontWeight: 'bold' }
      },
      labelLine: { show: false },
      data: paymentTypes.map(item => ({
        value: item.amount,
        name: item.paymentType
      }))
    }]
  }
  
  chart.setOption(option)
}

// // 更新性别比例图表
// const updateGenderChart = (genderData) => {
//   if (!paymentChart.value) return
  
//   const chart = echarts.getInstanceByDom(paymentChart.value) || echarts.init(paymentChart.value)
  
//   chart.setOption({
//     tooltip: {
//       trigger: 'item'
//     },
//     legend: {
//       top: '5%',
//       left: 'center'
//     },
//     series: [{
//       name: '性别比例',
//       type: 'pie',
//       radius: ['40%', '70%'],
//       avoidLabelOverlap: false,
//       itemStyle: {
//         borderRadius: 10,
//         borderColor: '#fff',
//         borderWidth: 2
//       },
//       label: {
//         show: false,
//         position: 'center'
//       },
//       emphasis: {
//         label: {
//           show: true,
//           fontSize: '18',
//           fontWeight: 'bold'
//         }
//       },
//       labelLine: {
//         show: false
//       },
//       data: [
//         { value: genderData['男'] || 0, name: '男性' },
//         { value: genderData['女'] || 0, name: '女性' }
//       ]
//     }]
//   })
// }

// 更新医生排行图表
const updateDoctorChart = () => {
  if (!prescriptionStats.doctorStats.length || !doctorChart.value) return
  
  const chart = echarts.getInstanceByDom(doctorChart.value) || echarts.init(doctorChart.value)
  
  // 反转数据顺序（高收入在上）
  const reversedData = [...prescriptionStats.doctorStats].reverse()
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      formatter: params => `
        <div>医生: ${params[0].axisValue}</div>
        <div>挂号人数: ${params[0].data}人</div>
      `
    },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'value',
      name: '挂号人数'
    },
    yAxis: {
      type: 'category',
      data: reversedData.map(item => item.name),
      axisLabel: { interval: 0, fontSize: 12 }
    },
    series: [{
      name: '挂号人数',
      type: 'bar',
      data: reversedData.map(item => item.count),
      itemStyle: {
        color: params => ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399'][params.dataIndex],
        borderRadius: [0, 4, 4, 0]
      },
      label: {
        show: true,
        position: 'right'
      }
    }]
  }
  
  chart.setOption(option)
}

// 更新科室排行图表
const updateDepartmentChart = () => {
  if (!prescriptionStats.departmentStats.length || !departmentChart.value) return
  
  const chart = echarts.getInstanceByDom(departmentChart.value) || echarts.init(departmentChart.value)
  
  // 反转数据顺序（高收入在上）
  const reversedData = [...prescriptionStats.departmentStats].reverse()
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      formatter: params => `
        <div>科室: ${params[0].axisValue}</div>
        <div>挂号人数: ${params[0].data}人</div>
      `
    },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'value',
      name: '挂号人数'
    },
    yAxis: {
      type: 'category',
      data: reversedData.map(item => item.name),
      axisLabel: { 
        interval: 0,
        fontSize: 12,
        formatter: value => value.length > 6 ? value.substring(0, 6) + '...' : value
      }
    },
    series: [{
      name: '挂号人数',
      type: 'bar',
      data: reversedData.map(item => item.count),
      itemStyle: {
        color: params => ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399'][params.dataIndex],
        borderRadius: [0, 4, 4, 0]
      },
      label: {
        show: true,
        position: 'right'
      }
    }]
  }
  
  chart.setOption(option)
}

// 工具方法
const formatCurrency = (value) => {
  if (value === null || value === undefined) return '0.00'
  return parseFloat(value).toLocaleString('zh-CN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
}

const calcComparison = (todayValue, yesterdayValue) => {
  if (!yesterdayValue || todayValue === undefined || todayValue === null) return 0
  return todayValue - yesterdayValue
}

const formatCompare = (value, unit) => {
  if (value > 0) return `较昨日增加${Math.abs(value)}${unit}`
  if (value < 0) return `较昨日减少${Math.abs(value)}${unit}`
  return `与昨日持平`
}

const getCompareClass = (value) => {
  if (value > 0) return 'positive'
  if (value < 0) return 'negative'
  return 'neutral'
}

// 监听日期变化
watch(selectedDate, () => {
  fetchRegistrationStats()
  fetchTrendData()
  fetchPaymentTypeData()
  // fetchGenderStats()
  fetchDoctorStats()
  fetchDepartmentStats()
})

// 组件挂载时初始化
onMounted(() => {
  fetchRegistrationStats()
  fetchTrendData()
  fetchPaymentTypeData()
  // fetchGenderStats()
  fetchDoctorStats()
  fetchDepartmentStats()
})

// 窗口大小变化时重新调整图表大小
window.addEventListener('resize', () => {
  ;[trendChart, paymentChart, doctorChart, departmentChart].forEach(chartRef => {
    if (chartRef.value) {
      echarts.getInstanceByDom(chartRef.value)?.resize()
    }
  })
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