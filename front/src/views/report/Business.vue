<template>
  <div class="daily-settlement-container">
    <!-- 面包屑导航 -->
    <el-breadcrumb separator="/" class="breadcrumb">
      <el-breadcrumb-item>报表统计</el-breadcrumb-item>
      <el-breadcrumb-item>门诊业务报表统计</el-breadcrumb-item>
    </el-breadcrumb>

    <!-- 查询条件区域 -->
    <el-card shadow="never" class="query-card">
      <div class="query-section">
        <el-date-picker
          v-model="dateRange"
          type="datetimerange"
          range-separator="至"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          format="YYYY-MM-DD HH:mm:ss"
          value-format="YYYY-MM-DD HH:mm:ss"
          class="date-picker"
        />
        <el-button type="primary" @click="fetchData" :icon="Search">汇总查询</el-button>
        <el-button @click="reset" :icon="Refresh">重置</el-button>
        <el-button type="success" @click="exportToExcel" :icon="Download">导出Excel</el-button>
        <el-button @click="printReport" :icon="Printer">打印报表</el-button>
      </div>
    </el-card>

    <!-- 报表内容区域 -->
    <div id="report-content">
      <!-- 报表标题 -->
      <div class="report-title">
        四川省测试中心医院门诊业务报表
      </div>
      <div class="report-subtitle">
        统计时间段：{{ displayDateRange }}
      </div>

      <!-- 业务数据表格 -->
      <div class="business-table">
        <div class="business-row">
          <div class="business-cell label">门诊挂号总数</div>
          <div class="business-cell value">{{ formatNumber(businessData.totalRegistrationCount) }}</div>
          <div class="business-cell label">门诊挂号费用(元)</div>
          <div class="business-cell value">{{ formatCurrency(businessData.totalRegistrationFee) }}</div>
        </div>
        <div class="business-row">
          <div class="business-cell label">门诊处方总数</div>
          <div class="business-cell value">{{ formatNumber(businessData.totalPrescriptionCount) }}</div>
          <div class="business-cell label">门诊处方总金额(元)</div>
          <div class="business-cell value">{{ formatCurrency(businessData.totalPrescriptionFee) }}</div>
        </div>
        <div class="business-row">
          <div class="business-cell label">门诊药占比(%)</div>
          <div class="business-cell value">{{ formatRatio(businessData.avgMedicineRatio) }}</div>
          <div class="business-cell label">门诊人均费用(元)</div>
          <div class="business-cell value">{{ formatCurrency(businessData.overallAvgFeePerPatient) }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { Search, Refresh, Download, Printer } from '@element-plus/icons-vue';
import * as XLSX from 'xlsx';
import html2canvas from 'html2canvas';
import { getBusinessInformation } from '@/api/prescription';
import { ElMessage } from 'element-plus';

// 日期范围

// 获取当前日期时间（格式：YYYY-MM-DD HH:mm:ss）
const getCurrentDateTime = () => {
  const now = new Date();
  return now.toISOString().replace('T', ' ').substring(0, 19);
};

// 获取3天前的日期时间（格式：YYYY-MM-DD HH:mm:ss）
const getThreeDaysAgoDateTime = () => {
  const date = new Date();
  date.setDate(date.getDate() - 3);
  return date.toISOString().replace('T', ' ').substring(0, 19);
};

const dateRange = ref([getThreeDaysAgoDateTime(), getCurrentDateTime()]);

// 业务数据
const businessData = ref({
  totalRegistrationCount: 0,
  totalRegistrationFee: 0,
  totalPrescriptionCount: 0,
  totalPrescriptionFee: 0,
  avgMedicineRatio: 0,
  overallAvgFeePerPatient: 0
});

// 格式化货币显示
const formatCurrency = (value) => {
  return parseFloat(value).toLocaleString('zh-CN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  });
};

// 格式化数字显示
const formatNumber = (value) => {
  return Number(value).toLocaleString('zh-CN');
};

// 格式化药占比显示
const formatRatio = (value) => {
  return (value * 1).toFixed(2);
};

// 显示日期范围
const displayDateRange = computed(() => {
  if (!dateRange.value || dateRange.value.length !== 2) return '未选择时间范围';
  const start = dateRange.value[0].substring(0, 10);
  const end = dateRange.value[1].substring(0, 10);
  return `${start} 至 ${end}`;
});

// 获取数据
const fetchData = async () => {
  try {
    if (!dateRange.value || dateRange.value.length !== 2) {
      ElMessage.warning('请先选择时间范围');
      return;
    }

    const params = {
      startDate: dateRange.value[0],
      endDate: dateRange.value[1]
    };

    const response = await getBusinessInformation(params);
    console.log('获取到的业务报表数据:', response.data);
    
    // 更新业务数据
    businessData.value = {
      totalRegistrationCount: response.data.totalRegistrationCount || 0,
      totalRegistrationFee: response.data.totalRegistrationFee || 0,
      totalPrescriptionCount: response.data.totalPrescriptionCount || 0,
      totalPrescriptionFee: response.data.totalPrescriptionFee || 0,
      avgMedicineRatio: response.data.avgMedicineRatio || 0,
      overallAvgFeePerPatient: response.data.overallAvgFeePerPatient || 0
    };

    ElMessage.success(`成功获取 ${displayDateRange.value} 的业务数据`);
  } catch (error) {
    console.error('获取业务数据失败:', error);
    ElMessage.error('获取业务数据失败，请检查网络或参数');
  }
};

// 重置查询
const reset = () => {
  dateRange.value = [getThreeDaysAgoDateTime(), getCurrentDateTime()]; // 重置为3天前到现在
  businessData.value = {
    totalRegistrationCount: 0,
    totalRegistrationFee: 0,
    totalPrescriptionCount: 0,
    totalPrescriptionFee: 0,
    avgMedicineRatio: 0,
    overallAvgFeePerPatient: 0
  };
};

// 导出Excel
const exportToExcel = () => {
  // 准备数据
  const exportData = [
    ['统计项', '数值', '统计项', '数值'],
    ['门诊挂号总数', businessData.value.totalRegistrationCount, '门诊挂号费用(元)', businessData.value.totalRegistrationFee],
    ['门诊处方总数', businessData.value.totalPrescriptionCount, '门诊处方总金额(元)', businessData.value.totalPrescriptionFee],
    ['门诊药占比(%)', formatRatio(businessData.value.avgMedicineRatio), '门诊人均费用(元)', businessData.value.overallAvgFeePerPatient],
    ['统计时间段', displayDateRange.value, '', '']
  ];

  // 创建工作簿
  const wb = XLSX.utils.book_new();
  const ws = XLSX.utils.aoa_to_sheet(exportData);
  
  // 设置列宽
  ws['!cols'] = [
    { wch: 20 }, 
    { wch: 15 },
    { wch: 20 },
    { wch: 15 }
  ];

  // 添加工作表
  XLSX.utils.book_append_sheet(wb, ws, '门诊业务报表');

  // 生成Excel文件并下载
  const fileName = `门诊业务报表_${displayDateRange.value.replace(/\s+/g, '')}.xlsx`;
  XLSX.writeFile(wb, fileName);
};

// 打印报表
const printReport = () => {
  const printContent = document.getElementById('report-content').innerHTML;
  const originalContent = document.body.innerHTML;
  
  document.body.innerHTML = `
    <div style="padding: 20px">
      <h2 style="text-align: center; margin-bottom: 20px;">四川省测试中心医院门诊业务报表</h2>
      <div style="text-align: center; margin-bottom: 20px;">统计时间段：${displayDateRange.value}</div>
      ${printContent}
    </div>
  `;
  
  window.print();
  document.body.innerHTML = originalContent;
  window.location.reload();
};

onMounted(() => {
  fetchData();
});
</script>

<style scoped>
.daily-settlement-container {
  padding: 20px;
  background-color: #f5f7fa;
}

.breadcrumb {
  margin-bottom: 20px;
}

.query-card {
  margin-bottom: 20px;
}

.query-section {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.date-picker {
  width: 400px;
}

.report-title {
  font-size: 18px;
  font-weight: bold;
  text-align: center;
  margin: 20px 0;
}

.report-subtitle {
  text-align: center;
  margin-bottom: 20px;
  color: #666;
}

.business-table {
  width: 100%;
  border: 1px solid #ebeef5;
  border-collapse: collapse;
  margin-bottom: 20px;
}

.business-row {
  display: flex;
  border-bottom: 1px solid #ebeef5;
}

.business-row:last-child {
  border-bottom: none;
}

.business-cell {
  padding: 12px 15px;
  flex: 1;
  text-align: center;
  border-right: 1px solid #ebeef5;
}

.business-cell:last-child {
  border-right: none;
}

.business-cell.label {
  font-weight: bold;
  background-color: #f5f7fa;
}

.business-cell.value {
  background-color: #fff;
}

@media print {
  body {
    padding: 0;
    margin: 0;
  }
  
  .query-card, .breadcrumb {
    display: none;
  }
  
  .report-title {
    font-size: 24px;
    margin: 10px 0;
  }
  
  .report-subtitle {
    margin-bottom: 15px;
  }
  
  .business-table {
    font-size: 14px;
  }
}
</style>