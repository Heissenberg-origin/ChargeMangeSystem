<template>
  <div class="card-settlement-query">
    <el-card class="search-card">
      <el-form :model="searchForm" label-width="80px" class="search-form">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="就诊卡号">
              <el-input v-model="searchForm.cardNumber" placeholder="请输入"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="姓名">
              <el-input v-model="searchForm.patientName" placeholder="请输入"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="证件号">
              <el-input v-model="searchForm.idNumber" placeholder="请输入"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item>
              <el-button type="primary" @click="handleSearch">刷卡</el-button>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="订单状态">
              <el-select v-model="searchForm.orderStatus" placeholder="全部状态">
                <el-option label="全部状态" value="all"></el-option>
                <el-option label="已结算" value="settled"></el-option>
                <el-option label="未结算" value="unsettled"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结算时间">
              <el-date-picker
                v-model="searchForm.settlementDate"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                value-format="YYYY-MM-DD">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <el-card>
      <el-table :data="tableData" border style="width: 100%">
        <el-table-column prop="cardNumber" label="就诊卡号" width="120"></el-table-column>
        <el-table-column prop="patientName" label="姓名" width="120"></el-table-column>
        <el-table-column prop="accountBalance" label="账户余额" width="120">
          <template #default="{row}">
            ¥{{ row.accountBalance.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="settlementAmount" label="结算金额" width="120">
          <template #default="{row}">
            ¥{{ row.settlementAmount.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="refundMethod" label="返还方式" width="120"></el-table-column>
        <el-table-column prop="settlementTime" label="结算时间" width="180"></el-table-column>
        <el-table-column prop="operatorId" label="操作员工号" width="120"></el-table-column>
        <el-table-column prop="operatorName" label="操作员" width="120"></el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{row}">
            <el-button size="small" @click="handleDetail(row)">详情</el-button>
            <el-button size="small" @click="handlePrint(row)">打印</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

// 搜索表单
const searchForm = ref({
  cardNumber: '',
  patientName: '',
  idNumber: '',
  orderStatus: 'all',
  settlementDate: ['2024-01-01', '2024-01-06']
})

// 表格数据
const tableData = ref([
  {
    cardNumber: '20050869',
    patientName: '张晓晓',
    accountBalance: 30.00,
    settlementAmount: 30.00,
    refundMethod: '现金',
    settlementTime: '2024-01-06 09:20:30',
    operatorId: 'SFR001',
    operatorName: '收费员001'
  },
  {
    cardNumber: '20050868',
    patientName: '王一',
    accountBalance: 30.00,
    settlementAmount: 30.00,
    refundMethod: '现金',
    settlementTime: '2024-01-06 09:20:30',
    operatorId: 'SFR001',
    operatorName: '收费员001'
  },
  {
    cardNumber: '20050867',
    patientName: '李梅',
    accountBalance: 30.00,
    settlementAmount: 30.00,
    refundMethod: '现金',
    settlementTime: '2024-01-06 09:20:30',
    operatorId: 'SFR001',
    operatorName: '收费员001'
  },
  {
    cardNumber: '20050866',
    patientName: '张晓珂',
    accountBalance: 30.00,
    settlementAmount: 30.00,
    refundMethod: '现金',
    settlementTime: '2024-01-06 09:20:30',
    operatorId: 'SFR001',
    operatorName: '收费员001'
  },
  {
    cardNumber: '20050865',
    patientName: '刘亮',
    accountBalance: 200.00,
    settlementAmount: 200.00,
    refundMethod: '现金',
    settlementTime: '2024-01-06 09:20:30',
    operatorId: 'SFR001',
    operatorName: '收费员001'
  },
  {
    cardNumber: '20050864',
    patientName: '小明',
    accountBalance: 200.00,
    settlementAmount: 200.00,
    refundMethod: '现金',
    settlementTime: '2024-01-06 09:20:30',
    operatorId: 'SFR001',
    operatorName: '收费员001'
  },
  {
    cardNumber: '20050863',
    patientName: '张三',
    accountBalance: 200.00,
    settlementAmount: 200.00,
    refundMethod: '微信支付',
    settlementTime: '2024-01-06 09:20:30',
    operatorId: 'SFR001',
    operatorName: '收费员001'
  },
  {
    cardNumber: '20050862',
    patientName: '张三',
    accountBalance: 66.00,
    settlementAmount: 66.00,
    refundMethod: '微信支付',
    settlementTime: '2024-01-06 09:20:30',
    operatorId: 'SFR001',
    operatorName: '收费员001'
  },
  {
    cardNumber: '20050861',
    patientName: '张三',
    accountBalance: 20.00,
    settlementAmount: 20.00,
    refundMethod: '微信支付',
    settlementTime: '2024-01-06 09:20:30',
    operatorId: 'SFR001',
    operatorName: '收费员001'
  },
  {
    cardNumber: '20050860',
    patientName: '张三',
    accountBalance: 20.00,
    settlementAmount: 20.00,
    refundMethod: '微信支付',
    settlementTime: '2024-01-06 09:20:30',
    operatorId: 'SFR001',
    operatorName: '收费员001'
  }
])

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(50)

// 搜索处理
const handleSearch = () => {
  console.log('搜索条件:', searchForm.value)
  // 这里应该是调用API获取数据，现在使用模拟数据
  currentPage.value = 1
}

// 分页大小改变
const handleSizeChange = (val) => {
  pageSize.value = val
  // 这里应该是重新获取数据
}

// 当前页改变
const handleCurrentChange = (val) => {
  currentPage.value = val
  // 这里应该是重新获取数据
}

// 详情操作
const handleDetail = (row) => {
  console.log('查看详情:', row)
  // 这里应该是跳转到详情页或打开详情对话框
}

// 打印操作
const handlePrint = (row) => {
  console.log('打印:', row)
  // 这里应该是调用打印API或打开打印预览
}

onMounted(() => {
  // 初始化时可以加载数据
})
</script>

<style scoped>
.card-settlement-query {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.search-form {
  margin-top: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>