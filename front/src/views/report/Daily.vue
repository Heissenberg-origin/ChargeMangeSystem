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
        :header-cell-style="{ 'text-align': 'center', 'font-weight': 'bold' }"
        :cell-style="{ 'text-align': 'center' }"
      >
        <el-table-column prop="date" label="日期" width="120" />
        <el-table-column prop="receivable" label="应收金额(元)" width="150">
          <template #default="{ row }">
            {{ formatCurrency(row.receivable) }}
          </template>
        </el-table-column>
        <el-table-column prop="received" label="实收金额(元)" width="150">
          <template #default="{ row }">
            {{ formatCurrency(row.received) }}
          </template>
        </el-table-column>
        <el-table-column prop="diff" label="差额(元)" width="120">
          <template #default="{ row }">
            <span :class="row.diff.startsWith('+') ? 'positive' : row.diff.startsWith('-') ? 'negative' : ''">
              {{ row.diff }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="refund" label="退费金额(元)" width="150">
          <template #default="{ row }">
            {{ formatCurrency(row.refund) }}
          </template>
        </el-table-column>
        <el-table-column prop="settle" label="结算金额(元)" width="150">
          <template #default="{ row }">
            {{ formatCurrency(row.settle) }}
          </template>
        </el-table-column>
        <el-table-column prop="operator" label="日结操作员" width="150" />
      </el-table>

      <!-- 合计行 -->
      <div class="summary">
        <strong>合计：</strong>
        应收金额：{{ formatCurrency(summary.receivable) }}，
        实收金额：{{ formatCurrency(summary.received) }}，
        差额：<span :class="summary.diff.startsWith('+') ? 'positive' : summary.diff.startsWith('-') ? 'negative' : ''">{{ summary.diff }}</span>，
        退费金额：{{ formatCurrency(summary.refund) }}，
        结算金额：{{ formatCurrency(summary.settle) }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { Search, Refresh, Download, Printer } from '@element-plus/icons-vue';
import * as XLSX from 'xlsx';
import html2canvas from 'html2canvas';

const dateRange = ref([
  '2024-07-01 00:00:00',
  '2024-07-08 23:59:59',
]);

// 模拟数据
const tableData = ref([
  { date: '2024-07-01', receivable: 564900.0, received: 565000.0, diff: '+100', refund: 900.0, settle: 564100.0, operator: '收费员001' },
  { date: '2024-07-02', receivable: 650000.0, received: 650000.0, diff: '0', refund: 1000.0, settle: 649000.0, operator: '收费员001' },
  { date: '2024-07-03', receivable: 550000.0, received: 550000.0, diff: '0', refund: 2000.0, settle: 548000.0, operator: '收费员001' },
  { date: '2024-07-04', receivable: 725000.0, received: 725000.0, diff: '0', refund: 30000.0, settle: 695000.0, operator: '收费员001' },
  { date: '2024-07-05', receivable: 395000.0, received: 395000.0, diff: '0', refund: 50000.0, settle: 345000.0, operator: '收费员001' },
  { date: '2024-07-06', receivable: 954000.0, received: 954000.0, diff: '0', refund: 900.0, settle: 953100.0, operator: '收费员001' },
  { date: '2024-07-07', receivable: 900000.0, received: 900000.0, diff: '0', refund: 900.0, settle: 899100.0, operator: 'GLY' },
  { date: '2024-07-08', receivable: 850000.0, received: 850000.0, diff: '0', refund: 900.0, settle: 849100.0, operator: 'GLY' },
]);

// 汇总数据
const summary = computed(() => {
  const sum = {
    receivable: 0,
    received: 0,
    diff: '+100',
    refund: 0,
    settle: 0,
  };
  for (const row of tableData.value) {
    sum.receivable += row.receivable;
    sum.received += row.received;
    sum.refund += row.refund;
    sum.settle += row.settle;
  }
  return sum;
});

// 格式化货币显示
const formatCurrency = (value) => {
  return parseFloat(value).toLocaleString('zh-CN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  });
};

// 获取数据
const fetchData = () => {
  // 实际项目中通过 axios 获取数据
  console.log('查询数据:', dateRange.value);
  // 这里可以添加加载状态
};

// 重置查询
const reset = () => {
  dateRange.value = ['2024-07-01 00:00:00', '2024-07-08 23:59:59'];
  // tableData.value = []; // 实际项目中可能需要清空数据
};

// 显示日期范围
const displayDateRange = computed(() => {
  if (!dateRange.value || dateRange.value.length !== 2) return '未选择时间范围';
  const [start, end] = dateRange.value;
  return `${start.slice(0, 10)} 至 ${end.slice(0, 10)}`;
});

// 导出Excel
const exportToExcel = () => {
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
  const fileName = `门诊日结报表_${displayDateRange.value.replace(/至/g, '至')}.xlsx`;
  XLSX.writeFile(wb, fileName);
};

// 打印报表
const printReport = () => {
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

// 或者使用html2canvas打印（更美观）
const printWithCanvas = async () => {
  try {
    const element = document.getElementById('report-content');
    const canvas = await html2canvas(element, {
      scale: 2,
      useCORS: true,
      allowTaint: true
    });
    
    const dataUrl = canvas.toDataURL('image/png');
    const windowContent = `
      <!DOCTYPE html>
      <html>
        <head>
          <title>打印报表</title>
          <style>
            body { margin: 0; padding: 0; }
            img { max-width: 100%; }
          </style>
        </head>
        <body>
          <img src="${dataUrl}" />
        </body>
      </html>
    `;
    
    const printWindow = window.open('', '_blank');
    printWindow.document.open();
    printWindow.document.write(windowContent);
    printWindow.document.close();
    
    printWindow.onload = function() {
      printWindow.focus();
      printWindow.print();
    };
  } catch (error) {
    console.error('打印失败:', error);
    ElMessage.error('打印失败，请重试');
  }
};
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
</style>