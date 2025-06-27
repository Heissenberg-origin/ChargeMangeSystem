<template>
  <div class="recharge-query">
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
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="订单状态">
              <el-select v-model="searchForm.orderStatus" placeholder="全部状态">
                <el-option label="全部状态" value="all"></el-option>
                <el-option label="已充值" value="recharged"></el-option>
                <el-option label="已退费" value="refunded"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="充值时间">
              <el-date-picker
                v-model="searchForm.rechargeDate"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                value-format="YYYY-MM-DD">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="收费员">
              <el-select v-model="searchForm.cashier" placeholder="请选择">
                <el-option
                  v-for="item in cashierOptions"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row>
          <el-col :span="24" style="text-align: right;">
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <el-card>
      <div class="table-header">
        <div class="summary-info">
          <div class="summary-item">
            <span class="label">充值总金额：</span>
            <span class="value">{{ summary.totalAmount }}</span>
          </div>
          <div class="summary-item">
            <span class="label">退费总金额：</span>
            <span class="value">{{ summary.refundAmount }}</span>
          </div>
        </div>
      </div>
      
      <el-table :data="tableData" border style="width: 100%">
        <el-table-column prop="cardNumber" label="就诊卡号" width="120"></el-table-column>
        <el-table-column prop="patientName" label="姓名" width="120"></el-table-column>
        <el-table-column prop="amount" label="充值金额" width="120">
          <template #default="{row}">
            ¥{{ row.amount.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="订单状态" width="120">
          <template #default="{row}">
            <el-tag :type="getStatusTagType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="orderNumber" label="订单号" width="180"></el-table-column>
        <el-table-column prop="paymentMethod" label="支付方式" width="120"></el-table-column>
        <el-table-column prop="rechargeTime" label="充值时间" width="180"></el-table-column>
        <el-table-column prop="cashier" label="收费员" width="120"></el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{row}">
            <el-button 
              size="small" 
              @click="handleRefund(row)" 
              :disabled="row.status === 'refunded'"
            >
              退费
            </el-button>
            <el-button 
              size="small" 
              @click="handlePrint(row)"
              type="primary"
            >
              打印小票
            </el-button>
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
    
    <!-- 退费对话框 -->
    <el-dialog v-model="refundDialogVisible" title="退费确认" width="30%">
      <el-form :model="refundForm" label-width="100px">
        <el-form-item label="就诊卡号">
          <span>{{ refundForm.cardNumber }}</span>
        </el-form-item>
        <el-form-item label="患者姓名">
          <span>{{ refundForm.patientName }}</span>
        </el-form-item>
        <el-form-item label="充值金额">
          <span>¥{{ refundForm.amount.toFixed(2) }}</span>
        </el-form-item>
        <el-form-item label="退费金额">
          <el-input-number 
            v-model="refundForm.refundAmount" 
            :min="0" 
            :max="refundForm.amount" 
            :precision="2"
            controls-position="right"
          />
        </el-form-item>
        <el-form-item label="退费原因">
          <el-input 
            v-model="refundForm.reason" 
            type="textarea" 
            :rows="3"
            placeholder="请输入退费原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="refundDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmRefund">确认退费</el-button>
        </span>
      </template>
    </el-dialog>
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
  rechargeDate: ['2024-01-01', '2024-01-06'],
  cashier: ''
})

// 收费员选项
const cashierOptions = ref([
  { id: 'SFR001', name: '收费员001' },
  { id: 'SFR002', name: '收费员002' },
  { id: 'SFR003', name: '收费员003' }
])

// 汇总信息
const summary = ref({
  totalAmount: '¥6500.00',
  refundAmount: '¥500.00'
})

// 表格数据
const tableData = ref([
  {
    cardNumber: '20050869',
    patientName: '张晓晓',
    amount: 30.00,
    status: 'recharged',
    orderNumber: 'DD20180606175013',
    paymentMethod: '现金',
    rechargeTime: '2024-01-06 09:20:30',
    cashier: '收费员001'
  },
  {
    cardNumber: '20050868',
    patientName: '王一',
    amount: 30.00,
    status: 'recharged',
    orderNumber: 'DD20180606175013',
    paymentMethod: '现金',
    rechargeTime: '2024-01-06 09:20:30',
    cashier: '收费员001'
  },
  {
    cardNumber: '20050867',
    patientName: '李梅',
    amount: 30.00,
    status: 'recharged',
    orderNumber: 'DD20180606175013',
    paymentMethod: '现金',
    rechargeTime: '2024-01-06 09:20:30',
    cashier: '收费员001'
  },
  {
    cardNumber: '20050866',
    patientName: '张晓珂',
    amount: 30.00,
    status: 'recharged',
    orderNumber: 'DD20180606175013',
    paymentMethod: '现金',
    rechargeTime: '2024-01-06 09:20:30',
    cashier: '收费员001'
  },
  {
    cardNumber: '20050865',
    patientName: '刘克',
    amount: 200.00,
    status: 'recharged',
    orderNumber: 'DD20180606175013',
    paymentMethod: '现金',
    rechargeTime: '2024-01-06 09:20:30',
    cashier: '收费员001'
  },
  {
    cardNumber: '20050864',
    patientName: '小明',
    amount: 200.00,
    status: 'recharged',
    orderNumber: 'DD20180606175013',
    paymentMethod: '现金',
    rechargeTime: '2024-01-06 09:20:30',
    cashier: '收费员001'
  },
  {
    cardNumber: '20050863',
    patientName: '张三',
    amount: 200.00,
    status: 'refunded',
    orderNumber: 'DD20180606175013',
    paymentMethod: '微信支付',
    rechargeTime: '2024-01-06 09:20:30',
    cashier: '收费员001'
  },
  {
    cardNumber: '20050862',
    patientName: '张三',
    amount: 66.00,
    status: 'refunded',
    orderNumber: 'DD20180606175013',
    paymentMethod: '微信支付',
    rechargeTime: '2024-01-06 09:20:30',
    cashier: '收费员001'
  },
  {
    cardNumber: '20050861',
    patientName: '张三',
    amount: 20.00,
    status: 'refunded',
    orderNumber: 'DD20180606175013',
    paymentMethod: '微信支付',
    rechargeTime: '2024-01-06 09:20:30',
    cashier: '收费员001'
  },
  {
    cardNumber: '20050860',
    patientName: '张三',
    amount: 20.00,
    status: 'refunded',
    orderNumber: 'DD20180606175013',
    paymentMethod: '微信支付',
    rechargeTime: '2024-01-06 09:20:30',
    cashier: '收费员001'
  }
])

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(50)

// 退费对话框
const refundDialogVisible = ref(false)
const refundForm = ref({
  cardNumber: '',
  patientName: '',
  amount: 0,
  refundAmount: 0,
  reason: ''
})

// 状态标签类型
const getStatusTagType = (status) => {
  switch(status) {
    case 'recharged': return 'success'
    case 'refunded': return 'info'
    default: return ''
  }
}

// 状态文本
const getStatusText = (status) => {
  switch(status) {
    case 'recharged': return '已充值'
    case 'refunded': return '已退费'
    default: return ''
  }
}

// 搜索处理
const handleSearch = () => {
  console.log('搜索条件:', searchForm.value)
  // 这里应该是调用API获取数据，现在使用模拟数据
  currentPage.value = 1
}

// 重置搜索
const handleReset = () => {
  searchForm.value = {
    cardNumber: '',
    patientName: '',
    idNumber: '',
    orderStatus: 'all',
    rechargeDate: ['2024-01-01', '2024-01-06'],
    cashier: ''
  }
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

// 退费操作
const handleRefund = (row) => {
  refundForm.value = {
    cardNumber: row.cardNumber,
    patientName: row.patientName,
    amount: row.amount,
    refundAmount: row.amount,
    reason: ''
  }
  refundDialogVisible.value = true
}

// 确认退费
const confirmRefund = () => {
  console.log('退费信息:', refundForm.value)
  // 这里应该是调用退费API
  refundDialogVisible.value = false
  // 模拟退费成功后更新状态
  const index = tableData.value.findIndex(item => item.cardNumber === refundForm.value.cardNumber)
  if (index !== -1) {
    tableData.value[index].status = 'refunded'
  }
}

// 打印小票
const handlePrint = (row) => {
  console.log('打印小票:', row)
  // 这里应该是调用打印API或打开打印预览
}

onMounted(() => {
  // 初始化时可以加载数据
})
</script>

<style scoped>
.recharge-query {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.search-form {
  margin-top: 20px;
}

.table-header {
  margin-bottom: 15px;
}

.summary-info {
  display: flex;
}

.summary-item {
  margin-right: 30px;
  font-size: 14px;
}

.summary-item .label {
  color: #909399;
}

.summary-item .value {
  color: #606266;
  font-weight: bold;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>