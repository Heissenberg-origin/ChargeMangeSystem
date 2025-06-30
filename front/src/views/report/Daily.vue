<template>
  <div class="daily-settlement-container">
    <!-- 面包屑导航 -->
    <el-breadcrumb separator="/" class="breadcrumb">
      <el-breadcrumb-item>报表统计</el-breadcrumb-item>
      <el-breadcrumb-item>门诊日结报表统计</el-breadcrumb-item>
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
        四川省测试中心医院门诊日结报表
      </div>
      <div class="report-subtitle">
        时间范围：{{ displayDateRange }}
      </div>

      <!-- 数据表格 -->
      <el-table 
        :data="tableData" 
        border 
        style="width: 100%"
        :header-cell-style="{
          'text-align': 'center',
          'font-weight': 'bold',
          'background-color': '#f5f7fa',
          'color': '#606266'
        }"
        :cell-style="{ 'text-align': 'center' }"
      >
        <el-table-column 
          prop="date" 
          label="日期" 
          header-align="center"
        />
        <el-table-column 
          prop="receivable" 
          label="应收金额(元)" 
          header-align="center"
        >
          <template #default="{ row }">
            <span class="amount">{{ formatCurrency(row.receivable) }}</span>
          </template>
        </el-table-column>
        <el-table-column 
          prop="received" 
          label="实收金额(元)" 
          header-align="center"
        >
          <template #default="{ row }">
            <span class="amount">{{ formatCurrency(row.received) }}</span>
          </template>
        </el-table-column>
        <el-table-column 
          prop="diff" 
          label="差额(元)" 
          header-align="center"
        >
          <template #default="{ row }">
            <span :class="[
              'amount',
              row.diff.startsWith('+') ? 'positive' : 
              row.diff.startsWith('-') ? 'negative' : ''
            ]">
              {{ row.diff }}
            </span>
          </template>
        </el-table-column>
        <el-table-column 
          prop="refund" 
          label="退费金额(元)" 
          header-align="center"
        >
          <template #default="{ row }">
            <span class="amount">{{ formatCurrency(row.refund) }}</span>
          </template>
        </el-table-column>
        <el-table-column 
          prop="settle" 
          label="结算金额(元)" 
          header-align="center"
        >
          <template #default="{ row }">
            <span class="amount">{{ formatCurrency(row.settle) }}</span>
          </template>
        </el-table-column>
      </el-table>

      <!-- 合计行 -->
      <div class="summary">
        <strong>合计：</strong>
        应收金额：<span class="amount">{{ formatCurrency(summary.receivable) }}</span>，
        实收金额：<span class="amount">{{ formatCurrency(summary.received) }}</span>，
        差额：<span :class="[
          'amount',
          summary.diff.startsWith('+') ? 'positive' : 
          summary.diff.startsWith('-') ? 'negative' : ''
        ]">{{ summary.diff }}</span>，
        退费金额：<span class="amount">{{ formatCurrency(summary.refund) }}</span>，
        结算金额：<span class="amount">{{ formatCurrency(summary.settle) }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import { Search, Refresh, Download, Printer } from '@element-plus/icons-vue';
import * as XLSX from 'xlsx';
import html2canvas from 'html2canvas';
import { getDailyInformation } from '@/api/prescription';
import { ElMessage } from 'element-plus';

// 日期范围（初始为空，由用户选择）
const dateRange = ref([]);

// 表格数据
const tableData = ref([]);

// 汇总数据
const summary = ref({
  receivable: 0,
  received: 0,
  diff: '0',
  refund: 0,
  settle: 0,
  operator: ''
});

// 格式化货币显示
const formatCurrency = (value) => {
  return parseFloat(value).toLocaleString('zh-CN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  });
};

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

    const response = await getDailyInformation(params);
    console.log('获取到的完整响应:', response);
    
    // 从 response.data 中解构数据
    const { 
      reportList, 
      totalReceivableSum, 
      actualReceivedSum, 
      differenceSum, 
      refundAmountSum, 
      settlementAmountSum 
    } = response.data;

    // 处理表格数据
    tableData.value = reportList.map(item => ({
      date: item.reportDate,
      receivable: item.totalReceivable,
      received: item.actualReceived,
      diff: item.difference > 0 ? `+${item.difference}` : item.difference.toString(),
      refund: item.refundAmount,
      settle: item.settlementAmount,
      operator: item.operator || '收费员001' // 如果接口返回操作员信息则使用，否则使用默认值
    }));

    // 处理汇总数据
    summary.value = {
      receivable: totalReceivableSum,
      received: actualReceivedSum,
      diff: differenceSum > 0 ? `+${differenceSum}` : differenceSum.toString(),
      refund: refundAmountSum,
      settle: settlementAmountSum,
      operator: ''
    };

    ElMessage.success(`成功获取 ${displayDateRange.value} 的数据`);
  } catch (error) {
    console.error('获取数据失败:', error);
    ElMessage.error('获取数据失败，请检查网络或参数');
  }
};

// 重置查询
const reset = () => {
  dateRange.value = [];
  tableData.value = [];
  summary.value = {
    receivable: 0,
    received: 0,
    diff: '0',
    refund: 0,
    settle: 0,
    operator: ''
  };
};

// 显示日期范围（格式化显示）
const displayDateRange = computed(() => {
  if (!dateRange.value || dateRange.value.length !== 2) return '未选择时间范围';
  const start = dateRange.value[0].substring(0, 10);
  const end = dateRange.value[1].substring(0, 10);
  return `${start} 至 ${end}`;
});

// 导出Excel
const exportToExcel = () => {
  if (tableData.value.length === 0) {
    ElMessage.warning('没有数据可导出');
    return;
  }

  // 准备数据
  const exportData = [
    ['日期', '应收金额(元)', '实收金额(元)', '差额(元)', '退费金额(元)', '结算金额(元)', '日结操作员'],
    ...tableData.value.map(item => [
      item.date,
      item.receivable,
      item.received,
      item.diff,
      item.refund,
      item.settle,
      item.operator
    ]),
    ['合计', summary.value.receivable, summary.value.received, summary.value.diff, 
     summary.value.refund, summary.value.settle, '']
  ];

  // 创建工作簿
  const wb = XLSX.utils.book_new();
  const ws = XLSX.utils.aoa_to_sheet(exportData);
  
  // 设置列宽
  ws['!cols'] = [
    { wch: 12 }, // 日期
    { wch: 15 }, // 应收金额
    { wch: 15 }, // 实收金额
    { wch: 12 }, // 差额
    { wch: 15 }, // 退费金额
    { wch: 15 }, // 结算金额
    { wch: 15 }  // 操作员
  ];

  // 添加工作表
  XLSX.utils.book_append_sheet(wb, ws, '门诊日结报表');

  // 生成Excel文件并下载
  const fileName = `门诊日结报表_${displayDateRange.value.replace(/\s+/g, '')}.xlsx`;
  XLSX.writeFile(wb, fileName);
};

// 打印报表
const printReport = () => {
  if (tableData.value.length === 0) {
    ElMessage.warning('没有数据可打印');
    return;
  }

  const printContent = document.getElementById('report-content').innerHTML;
  const originalContent = document.body.innerHTML;
  
  document.body.innerHTML = `
    <div style="padding: 20px">
      <h2 style="text-align: center; margin-bottom: 20px;">四川省测试中心医院门诊日结报表</h2>
      <div style="text-align: center; margin-bottom: 20px;">时间范围：${displayDateRange.value}</div>
      ${printContent}
    </div>
  `;
  
  window.print();
  document.body.innerHTML = originalContent;
  window.location.reload();
};

// 可选：监听日期范围变化自动查询（根据需要开启）
// watch(dateRange, (newVal) => {
//   if (newVal && newVal.length === 2) {
//     fetchData();
//   }
// });
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

.summary {
  text-align: right;
  margin-top: 20px;
  padding: 10px;
  background-color: #f9f9f9;
  border: 1px solid #ebeef5;
  border-radius: 4px;
}

.positive {
  color: #67c23a;
}

.negative {
  color: #f56c6c;
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
  
  .el-table {
    font-size: 12px;
  }
}

/* 确保表格填满容器 */
.el-table {
  width: 100% !important;
}

.el-table-column {
  flex: 1;
}
</style>