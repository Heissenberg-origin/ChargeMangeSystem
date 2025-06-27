<template>
  <div class="prescription-statistics-container">
    <!-- 搜索区域 -->
    <div class="search-container">
      <el-card shadow="never">
        <div class="search-header">
          <span class="title">门诊处方汇总统计</span>
        </div>
        <el-form :model="searchForm" label-width="100px">
          <el-row :gutter="20">
            <el-col :span="6">
              <el-form-item label="时间类型">
                <el-radio-group v-model="searchForm.timeType">
                  <el-radio label="day">按天</el-radio>
                  <el-radio label="week">按周</el-radio>
                  <el-radio label="month">按月</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="时间范围">
                <el-date-picker
                  v-model="searchForm.dateRange"
                  type="daterange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  value-format="YYYY-MM-DD"
                />
              </el-form-item>
            </el-col>
            <el-col :span="4">
              <el-button type="primary" @click="handleSearch">汇总</el-button>
              <el-button @click="resetSearch">重置</el-button>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="6">
              <el-form-item label="统计方式">
                <el-radio-group v-model="searchForm.statisticType">
                  <el-radio label="department">按科室</el-radio>
                  <el-radio label="doctor">按医生</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="科室">
                <el-select v-model="searchForm.department" placeholder="全部">
                  <el-option label="全部" value="" />
                  <el-option label="门诊外科" value="外科" />
                  <el-option label="门诊内科" value="内科" />
                  <el-option label="骨科" value="骨科" />
                  <el-option label="耳鼻喉科" value="耳鼻喉科" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label=" ">
                <el-button type="primary" @click="exportData">导出</el-button>
                <el-button>打印</el-button>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </el-card>
    </div>
    <!-- 统计信息 -->
    <div class="stats-container">
      <el-table :data="tableData" border style="width: 100%">
        <el-table-column prop="department" label="统计方式" width="150" />
        <el-table-column prop="totalPrescriptions" label="已开处方数" width="150" />
        <el-table-column prop="totalAmount" label="已开处方金额(元)" width="150" />
        <el-table-column prop="paidPrescriptions" label="已收费处方数" width="150" />
        <el-table-column prop="paidAmount" label="已收费处方金额(元)" width="150" />
        <el-table-column prop="unpaidPrescriptions" label="未收费处方数" width="150" />
        <el-table-column prop="unpaidAmount" label="未收费处方金额(元)" width="150" />
        <el-table-column prop="unpaidRatio" label="未收费比列(%)" width="150" />
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

// 搜索表单
const searchForm = ref({
  timeType: 'day',
  dateRange: ['2024-06-09', '2024-07-08'],
  statisticType: 'department',
  department: '',
});

// 表格数据
const tableData = ref([
  {
    department: '门诊外科',
    totalPrescriptions: 130,
    totalAmount: 3000.00,
    paidPrescriptions: 120,
    paidAmount: 2900.00,
    unpaidPrescriptions: 10,
    unpaidAmount: 100.00,
    unpaidRatio: 8.33,
  },
  {
    department: '门诊内科',
    totalPrescriptions: 235,
    totalAmount: 5000.00,
    paidPrescriptions: 230,
    paidAmount: 4500.00,
    unpaidPrescriptions: 5,
    unpaidAmount: 500.00,
    unpaidRatio: 2.13,
  },
  {
    department: '合计',
    totalPrescriptions: 365,
    totalAmount: 8000.00,
    paidPrescriptions: 350,
    paidAmount: 7400.00,
    unpaidPrescriptions: 15,
    unpaidAmount: 600.00,
    unpaidRatio: 10.46,
  },
]);

// 方法
const handleSearch = () => {
  console.log('搜索条件:', searchForm.value);
};

const resetSearch = () => {
  searchForm.value = {
    timeType: 'day',
    dateRange: ['2024-06-09', '2024-07-08'],
    statisticType: 'department',
    department: '',
  };
};

const exportData = () => {
  console.log('导出数据');
};
</script>

<style scoped>
.prescription-statistics-container {
  padding: 20px;
}
.search-container {
  margin-bottom: 20px;
}
.search-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.search-header .title {
  font-size: 16px;
  font-weight: bold;
}
.stats-container {
  margin-bottom: 20px;
}
</style>