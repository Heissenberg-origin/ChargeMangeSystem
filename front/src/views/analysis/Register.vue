<template>
  <div class="outpatient-payment-dashboard">
    <!-- 顶部标题和日期选择 -->
    <div class="dashboard-header">
      <h2>全院分析管理 / 门诊收费分析</h2>
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
       <!-- 1. 缴费人数卡片 -->
  <el-card class="stat-card">
    <div class="stat-header">
      <el-icon class="stat-icon"><User /></el-icon>
      <span class="stat-title">缴费人数</span>
    </div>
    <div class="stat-content">
      <div class="stat-main">
        <span class="stat-value">266</span>
        <span class="stat-unit">人</span>
      </div>
      <div class="stat-compare negative">
        <el-icon><ArrowDown /></el-icon>
        较昨日减少20人
      </div>
      <div class="stat-detail">
        <div>处方缴费人数: <span class="detail-value">150</span></div>
        <div>处方费金额: <span class="detail-value">3,250.00</span></div>
      </div>
    </div>
  </el-card>

  <!-- 2. 缴费金额卡片 -->
  <el-card class="stat-card">
    <div class="stat-header">
      <el-icon class="stat-icon"><Money /></el-icon>
      <span class="stat-title">缴费金额</span>
    </div>
    <div class="stat-content">
      <div class="stat-main">
        <span class="stat-value">3,250.00</span>
        <span class="stat-unit">元</span>
      </div>
      <div class="stat-compare negative">
        <el-icon><ArrowDown /></el-icon>
        较昨日减少300元
      </div>
      <div class="stat-detail">
        <div>检查缴费人数: <span class="detail-value">80</span></div>
        <div>检查费金额: <span class="detail-value">1,200.00</span></div>
      </div>
    </div>
  </el-card>

  <!-- 3. 退费金额卡片 -->
  <el-card class="stat-card">
    <div class="stat-header">
      <el-icon class="stat-icon"><RefreshLeft /></el-icon>
      <span class="stat-title">退费金额</span>
    </div>
    <div class="stat-content">
      <div class="stat-main">
        <span class="stat-value">500.00</span>
        <span class="stat-unit">元</span>
      </div>
      <div class="stat-compare negative">
        <el-icon><ArrowDown /></el-icon>
        较昨日减少100元
      </div>
      <div class="stat-detail">
        <div>检验缴费人数: <span class="detail-value">60</span></div>
        <div>检验费金额: <span class="detail-value">800.00</span></div>
      </div>
    </div>
  </el-card>

  <!-- 4. 处方金额卡片 -->
  <el-card class="stat-card">
    <div class="stat-header">
      <el-icon class="stat-icon"><Document /></el-icon>
      <span class="stat-title">处方金额</span>
    </div>
    <div class="stat-content">
      <div class="stat-main">
        <span class="stat-value">3,250.00</span>
        <span class="stat-unit">元</span>
      </div>
      <div class="stat-compare negative">
        <el-icon><ArrowDown /></el-icon>
        较昨日减少300元
      </div>
      <div class="stat-detail">
        <div>医疗项目缴费人数: <span class="detail-value">90</span></div>
        <div>医疗项目费金额: <span class="detail-value">1,100.00</span></div>
      </div>
    </div>
  </el-card>
    </div>

    <!-- 图表区域 -->
    <div class="charts-container">
      <!-- 1. 门诊收退费趋势 -->
      <el-card class="chart-card">
        <div class="chart-title">门诊收退费趋势</div>
        <div class="chart-unit">单位：千元</div>
        <div class="chart-container" ref="trendChart" style="height: 300px;"></div>
      </el-card>

      <!-- 2. 支付方式比例 -->
      <el-card class="chart-card">
        <div class="chart-title">支付方式比例</div>
        <div class="chart-container" ref="paymentChart" style="height: 300px;"></div>
      </el-card>

      <!-- 3. 热门医生收入排行 -->
      <el-card class="chart-card">
        <div class="chart-title">热门医生收入排行(前五)</div>
        <div class="chart-container" ref="doctorChart" style="height: 300px;"></div>
      </el-card>

      <!-- 4. 热门科室收入排行 -->
      <el-card class="chart-card">
        <div class="chart-title">热门科室收入排行(前五)</div>
        <div class="chart-container" ref="departmentChart" style="height: 300px;"></div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { User, Money, RefreshLeft, Document, ArrowDown } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { format } from 'date-fns'

// 日期选择
const selectedDate = ref(format(new Date(), 'yyyy-MM-dd'))

// 统计数据
const paymentStats = reactive({
  patientCount: 266,
  patientCountChange: -20,
  prescriptionPatientCount: 150,
  prescriptionAmount: 3250.00,
  prescriptionAmountChange: -300.00,
  paymentAmount: 3250.00,
  paymentAmountChange: -300.00,
  examPatientCount: 80,
  examAmount: 1200.00,
  refundAmount: 500.00,
  refundAmountChange: -100.00,
  labPatientCount: 60,
  labAmount: 800.00,
  medicalItemPatientCount: 90,
  medicalItemAmount: 1100.00
})

// 图表引用
const trendChart = ref(null)
const paymentChart = ref(null)
const doctorChart = ref(null)
const departmentChart = ref(null)

// 格式化货币
const formatCurrency = (value) => {
  return parseFloat(value).toLocaleString('zh-CN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
}

// 格式化比较值
const formatCompare = (value, unit) => {
  if (value > 0) {
    return `较昨日增加${Math.abs(value)}${unit}`
  } else if (value < 0) {
    return `较昨日减少${Math.abs(value)}${unit}`
  } else {
    return `与昨日持平`
  }
}

// 获取比较样式类
const getCompareClass = (value) => {
  if (value > 0) {
    return 'positive'
  } else if (value < 0) {
    return 'negative'
  } else {
    return 'neutral'
  }
}

// 初始化趋势图表
const initTrendChart = () => {
  const chart = echarts.init(trendChart.value)
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    legend: {
      data: ['挂号金额', '退费金额']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: ['6-8', '6-13', '6-15', '6-18', '6-21', '6-24', '6-27', '7-1', '7-5', '7-8']
    },
    yAxis: {
      type: 'value',
      name: '金额(千元)'
    },
    series: [
      {
        name: '挂号金额',
        type: 'line',
        data: [12, 13, 10, 13, 9, 23, 21, 18, 15, 20],
        smooth: true,
        lineStyle: {
          width: 3
        },
        itemStyle: {
          color: '#409EFF'
        }
      },
      {
        name: '退费金额',
        type: 'line',
        data: [1, 2, 1.5, 1, 0.5, 2, 1.8, 1.5, 1.2, 1.8],
        smooth: true,
        lineStyle: {
          width: 3
        },
        itemStyle: {
          color: '#F56C6C'
        }
      }
    ]
  }
  chart.setOption(option)
}

// 初始化支付方式图表
const initPaymentChart = () => {
  const chart = echarts.init(paymentChart.value)
  const option = {
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center'
    },
    series: [
      {
        name: '支付方式',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: '18',
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: [
          { value: 40, name: '现金' },
          { value: 30, name: '微信' },
          { value: 20, name: '支付宝' },
          { value: 10, name: '医保卡' }
        ]
      }
    ]
  }
  chart.setOption(option)
}

// 初始化医生排行图表
const initDoctorChart = () => {
  const chart = echarts.init(doctorChart.value)
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
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
      show: false
    },
    yAxis: {
      type: 'category',
      data: ['李医生', '王医生', '张医生', '刘医生', '黄医生'],
      axisLabel: {
        interval: 0,
        fontSize: 12
      }
    },
    series: [
      {
        name: '收入',
        type: 'bar',
        data: [50000, 45000, 40000, 35000, 30000],
        itemStyle: {
          color: function(params) {
            const colorList = ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399']
            return colorList[params.dataIndex]
          },
          borderRadius: [0, 4, 4, 0]
        },
        label: {
          show: true,
          position: 'right',
          formatter: '{c}元'
        }
      }
    ]
  }
  chart.setOption(option)
}

// 初始化科室排行图表
const initDepartmentChart = () => {
  const chart = echarts.init(departmentChart.value)
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
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
      max: 500
    },
    yAxis: {
      type: 'category',
      data: ['门诊外科', '呼吸内科', '耳鼻喉科', '骨科', '眼科'],
      axisLabel: {
        interval: 0,
        fontSize: 12
      }
    },
    series: [
      {
        name: '收入',
        type: 'bar',
        data: [450, 380, 320, 280, 200],
        itemStyle: {
          color: function(params) {
            const colorList = ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399']
            return colorList[params.dataIndex]
          },
          borderRadius: [0, 4, 4, 0]
        },
        label: {
          show: true,
          position: 'right',
          formatter: '{c}元'
        }
      }
    ]
  }
  chart.setOption(option)
}

// 监听日期变化
watch(selectedDate, (newDate) => {
  console.log('日期变化:', newDate)
  // 这里可以添加数据更新逻辑
  refreshCharts()
})

// 刷新图表
const refreshCharts = () => {
  initTrendChart()
  initPaymentChart()
  initDoctorChart()
  initDepartmentChart()
}

// 组件挂载时初始化图表
onMounted(() => {
  refreshCharts()
})

// 窗口大小变化时重新调整图表大小
window.addEventListener('resize', () => {
  if (trendChart.value) {
    echarts.getInstanceByDom(trendChart.value)?.resize()
  }
  if (paymentChart.value) {
    echarts.getInstanceByDom(paymentChart.value)?.resize()
  }
  if (doctorChart.value) {
    echarts.getInstanceByDom(doctorChart.value)?.resize()
  }
  if (departmentChart.value) {
    echarts.getInstanceByDom(departmentChart.value)?.resize()
  }
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