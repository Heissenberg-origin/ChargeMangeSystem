<template>
  <div class="outpatient-fee-query">
    <el-card class="search-card">
      <el-form :model="searchForm" label-width="80px" class="search-form">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="就诊卡号">
              <el-input v-model="searchForm.cardNumber" placeholder="请输入"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="患者姓名">
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
            <el-form-item label="门诊号">
              <el-input v-model="searchForm.outpatientNumber" placeholder="请输入"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="缴费状态">
              <el-select v-model="searchForm.paymentStatus" placeholder="全部状态">
                <el-option label="全部状态" value="all"></el-option>
                <el-option label="已缴费" value="paid"></el-option>
                <el-option label="已退费" value="refunded"></el-option>
                <el-option label="部分退费" value="partiallyRefunded"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="收费时间">
              <el-date-picker
                v-model="searchForm.paymentDate"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                value-format="YYYY-MM-DD">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="开单科室">
              <el-input v-model="searchForm.department" placeholder="请输入"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="开单医生">
              <el-input v-model="searchForm.doctor" placeholder="请输入"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <el-card class="summary-card">
      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <el-tab-pane label="全部缴费" name="all"></el-tab-pane>
        <el-tab-pane label="已缴费" name="paid"></el-tab-pane>
        <el-tab-pane label="已退费" name="refunded"></el-tab-pane>
        <el-tab-pane label="部分退费" name="partiallyRefunded"></el-tab-pane>
      </el-tabs>
      
      <div class="summary-info">
        <div class="summary-item">
          <span class="label">缴费单数：</span>
          <span class="value">{{ summary.totalOrders }}单</span>
        </div>
        <div class="summary-item">
          <span class="label">缴费金额：</span>
          <span class="value">{{ summary.totalAmount }}</span>
        </div>
        <div class="summary-item">
          <span class="label">退费单数：</span>
          <span class="value">{{ summary.refundOrders }}单</span>
        </div>
        <div class="summary-item">
          <span class="label">退费金额：</span>
          <span class="value">{{ summary.refundAmount }}</span>
        </div>
      </div>
    </el-card>

    <el-card>
      <el-table :data="tableData" border style="width: 100%">
        <el-table-column prop="outpatientNumber" label="门(就)诊号" width="150"></el-table-column>
        <el-table-column prop="cardNumber" label="就诊卡号" width="120"></el-table-column>
        <el-table-column prop="patientName" label="患者姓名" width="120"></el-table-column>
        <el-table-column prop="department" label="开单科室" width="120"></el-table-column>
        <el-table-column prop="doctor" label="开单医生" width="120"></el-table-column>
        <el-table-column prop="amount" label="缴费金额" width="120">
          <template #default="{row}">
            ¥{{ row.amount.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="缴费状态" width="120">
          <template #default="{row}">
            <el-tag :type="getStatusTagType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="paymentMethod" label="支付方式" width="120"></el-table-column>
        <el-table-column label="操作" width="220">
          <template #default="{row}">
            <el-button size="small" @click="handleDetail(row)">详情</el-button>
            <el-button size="small" @click="handleRefund(row)" v-if="row.status === 'paid'">医嘱退费</el-button>
            <el-dropdown>
              <el-button size="small">
                更多<el-icon class="el-icon--right"><arrow-down /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="handlePrint(row)">打印</el-dropdown-item>
                  <el-dropdown-item @click="handleExport(row)">导出</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
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
import { ArrowDown } from '@element-plus/icons-vue'

// 搜索表单
const searchForm = ref({
  cardNumber: '',
  patientName: '',
  idNumber: '',
  outpatientNumber: '',
  paymentStatus: 'all',
  paymentDate: ['2024-01-01', '2024-01-06'],
  department: '',
  doctor: ''
})

// 标签页
const activeTab = ref('all')

// 汇总信息
const summary = ref({
  totalOrders: 50,
  totalAmount: '￥6500.00',
  refundOrders: 10,
  refundAmount: '￥500.00'
})

// 表格数据
const tableData = ref([
  {
    outpatientNumber: '6520050869',
    cardNumber: '20050869',
    patientName: '张晓晓',
    department: '门诊外科',
    doctor: '李医生',
    amount: 30.00,
    status: 'paid',
    paymentMethod: '现金'
  },
  {
    outpatientNumber: '6520050868',
    cardNumber: '20050868',
    patientName: '王一',
    department: '门诊外科',
    doctor: '李医生',
    amount: 30.00,
    status: 'paid',
    paymentMethod: '现金'
  },
  {
    outpatientNumber: '6520050867',
    cardNumber: '20050867',
    patientName: '李梅',
    department: '门诊外科',
    doctor: '李医生',
    amount: 30.00,
    status: 'paid',
    paymentMethod: '现金'
  },
  {
    outpatientNumber: '6520050866',
    cardNumber: '20050866',
    patientName: '张晓珂',
    department: '儿科',
    doctor: '李医生',
    amount: 30.00,
    status: 'paid',
    paymentMethod: '现金'
  },
  {
    outpatientNumber: '6520050865',
    cardNumber: '20050865',
    patientName: '刘克',
    department: '骨科',
    doctor: '李医生',
    amount: 200.00,
    status: 'paid',
    paymentMethod: '现金'
  },
  {
    outpatientNumber: '6520050864',
    cardNumber: '20050864',
    patientName: '小明',
    department: '骨科',
    doctor: '李医生',
    amount: 200.00,
    status: 'paid',
    paymentMethod: '现金'
  },
  {
    outpatientNumber: '6520050863',
    cardNumber: '20050863',
    patientName: '王芳',
    department: '内科',
    doctor: '张医生',
    amount: 150.00,
    status: 'refunded',
    paymentMethod: '微信支付'
  },
  {
    outpatientNumber: '6520050862',
    cardNumber: '20050862',
    patientName: '李强',
    department: '眼科',
    doctor: '王医生',
    amount: 180.00,
    status: 'partiallyRefunded',
    paymentMethod: '支付宝'
  }
])

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(50)

// 状态标签类型
const getStatusTagType = (status) => {
  switch(status) {
    case 'paid': return 'success'
    case 'refunded': return 'info'
    case 'partiallyRefunded': return 'warning'
    default: return ''
  }
}

// 状态文本
const getStatusText = (status) => {
  switch(status) {
    case 'paid': return '已缴费'
    case 'refunded': return '已退费'
    case 'partiallyRefunded': return '部分退费'
    default: return ''
  }
}

// 搜索处理
const handleSearch = () => {
  console.log('搜索条件:', searchForm.value)
  // 这里应该是调用API获取数据，现在使用模拟数据
  currentPage.value = 1
}

// 标签页切换
const handleTabClick = (tab) => {
  console.log('切换标签:', tab.props.name)
  // 这里应该是根据标签过滤数据
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

// 退费操作
const handleRefund = (row) => {
  console.log('医嘱退费:', row)
  // 这里应该是打开退费对话框
}

// 打印操作
const handlePrint = (row) => {
  console.log('打印:', row)
}

// 导出操作
const handleExport = (row) => {
  console.log('导出:', row)
}

onMounted(() => {
  // 初始化时可以加载数据
})
</script>

<style scoped>
.outpatient-fee-query {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.search-form {
  margin-top: 20px;
}

.summary-card {
  margin-bottom: 20px;
}

.summary-info {
  display: flex;
  padding: 15px 0;
  border-top: 1px solid #ebeef5;
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