<template>
  <div class="receipt-management-container">
    <!-- 查询条件区域 -->
    <div class="query-section">
      <el-card shadow="never">
        <el-form :model="queryParams" label-width="80px">
          <el-row :gutter="20">
            <el-col :span="6">
              <el-form-item label="领用人">
                <el-input v-model="queryParams.receiver" placeholder="请输入领用人" clearable />
              </el-form-item>
            </el-col>
            
            <el-col :span="6">
              <el-form-item label="收费类型">
                <el-select v-model="queryParams.receiptType" placeholder="请选择收费类型" clearable>
                  <el-option label="全部类型" value="" />
                  <el-option 
                    v-for="item in receiptTypeOptions"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
              </el-form-item>
            </el-col>
            
            <el-col :span="6">
              <el-form-item label="状态">
                <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
                  <el-option label="全部状态" value="" />
                  <el-option label="启用" value="1" />
                  <el-option label="禁用" value="0" />
                </el-select>
              </el-form-item>
            </el-col>
            
            <el-col :span="6" class="action-buttons">
              <el-button @click="handleReset" :icon="Refresh">
                重置
              </el-button>
              <el-button 
                type="primary" 
                @click="handleSearch" 
                :icon="Search"
              >
                搜索
              </el-button>
            </el-col>
          </el-row>
        </el-form>
      </el-card>
    </div>
    
    <!-- 展示区域 -->
    <div class="display-section">
      <el-card shadow="never">
        <div class="display-header">
          <div class="display-actions">
            <el-button type="primary" @click="showCreateDialog" :icon="Plus">
              新增收据
            </el-button>
          </div>
        </div>
        
        <div class="table-container">
          <el-table 
            :data="tableData" 
            border 
            style="width: 100%"
            v-loading="loading"
            stripe
          >
            <el-table-column prop="receiptType" label="收费类型" align="center" width="150">
              <template #default="{row}">
                {{ receiptTypeOptions.find(t => t.value === row.receiptType)?.label || row.receiptType }}
              </template>
            </el-table-column>
            <el-table-column prop="receiver" label="领用人" align="center" width="120" />
            <el-table-column prop="currentNo" label="当前号" align="center" width="100" />
            <el-table-column prop="startPrefix" label="起始首字母" align="center" width="120" />
            <el-table-column prop="startNo" label="起始号" align="center" width="100" />
            <el-table-column prop="endPrefix" label="结束首字母" align="center" width="120" />
            <el-table-column prop="endNo" label="结束号" align="center" width="100" />
            <el-table-column prop="status" label="状态" align="center" width="100">
              <template #default="{row}">
                <el-tag :type="row.status === '1' ? 'success' : 'danger'">
                  {{ row.status === '1' ? '启用' : '禁用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="creator" label="创建人" align="center" width="100" />
            <el-table-column prop="createTime" label="创建时间" align="center" width="160" />
            <el-table-column prop="modifier" label="修改人" align="center" width="100" />
            <el-table-column prop="modifyTime" label="最后修改时间" align="center" width="160" />
            <el-table-column label="操作" align="center" width="150" fixed="right">
              <template #default="{row}">
                <el-button type="primary" size="small" @click="handleEdit(row)">
                  编辑
                </el-button>
                <el-button type="danger" size="small" @click="handleDelete(row)">
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
        
        <div class="pagination">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="totalItems"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handlePageChange"
          />
        </div>
      </el-card>
    </div>
    
    <!-- 新增/编辑收据对话框 -->
    <el-dialog 
      v-model="receiptDialogVisible" 
      :title="`${isEdit ? '编辑' : '新增'}收据`"
      width="600px"
    >
      <el-form :model="receiptForm" :rules="rules" ref="receiptFormRef" label-width="100px">
        <el-form-item label="收费类型" prop="receiptType">
          <el-select v-model="receiptForm.receiptType" placeholder="请选择收费类型">
            <el-option
              v-for="item in receiptTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="起始首字母" prop="startPrefix">
              <el-input v-model="receiptForm.startPrefix" placeholder="请输入起始首字母" maxlength="1" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="起始号" prop="startNo">
              <el-input-number 
                v-model="receiptForm.startNo" 
                :min="1" 
                :controls="false"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="结束首字母" prop="endPrefix">
              <el-input v-model="receiptForm.endPrefix" placeholder="请输入结束首字母" maxlength="1" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束号" prop="endNo">
              <el-input-number 
                v-model="receiptForm.endNo" 
                :min="receiptForm.startNo || 1" 
                :controls="false"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="领用人" prop="receiver">
          <el-input v-model="receiptForm.receiver" placeholder="请输入领用人" />
        </el-form-item>
        
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="receiptForm.status">
            <el-radio label="1">启用</el-radio>
            <el-radio label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="receiptDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReceiptForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 查询参数
const queryParams = reactive({
  receiver: '',
  receiptType: '',
  status: ''
})

// 收费类型选项
const receiptTypeOptions = [
  { value: '1', label: '挂号收据' },
  { value: '2', label: '门诊医嘱收据' },
  { value: '3', label: '就诊卡充值收据' },
  { value: '4', label: '处方划价收据' }
]

// 分页参数
const currentPage = ref(1)
const pageSize = ref(10)
const totalItems = ref(0)
const loading = ref(false)

// 表格数据
const tableData = ref([])

// 收据对话框相关
const receiptDialogVisible = ref(false)
const isEdit = ref(false)
const receiptFormRef = ref(null)
const receiptForm = reactive({
  receiptType: '',
  startPrefix: '',
  startNo: 1,
  endPrefix: '',
  endNo: 1,
  receiver: '',
  status: '1'
})

// 表单验证规则
const rules = {
  receiptType: [
    { required: true, message: '请选择收费类型', trigger: 'change' }
  ],
  startPrefix: [
    { required: true, message: '请输入起始首字母', trigger: 'blur' },
    { pattern: /^[A-Za-z]$/, message: '请输入单个字母', trigger: 'blur' }
  ],
  startNo: [
    { required: true, message: '请输入起始号', trigger: 'blur' }
  ],
  endPrefix: [
    { required: true, message: '请输入结束首字母', trigger: 'blur' },
    { pattern: /^[A-Za-z]$/, message: '请输入单个字母', trigger: 'blur' }
  ],
  endNo: [
    { required: true, message: '请输入结束号', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        if (value < receiptForm.startNo) {
          callback(new Error('结束号不能小于起始号'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  receiver: [
    { required: true, message: '请输入领用人', trigger: 'blur' }
  ]
}

// 监听起始号变化，自动调整结束号最小值
watch(() => receiptForm.startNo, (newVal) => {
  if (receiptForm.endNo < newVal) {
    receiptForm.endNo = newVal
  }
})

// 获取表格数据
const fetchTableData = async () => {
  try {
    loading.value = true
    // 模拟API请求
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // 模拟数据 - 实际项目中替换为API调用
    const mockData = [
      {
        id: 1,
        receiptType: '1',
        receiver: '张三',
        currentNo: 'A1005',
        startPrefix: 'A',
        startNo: 1001,
        endPrefix: 'A',
        endNo: 1500,
        status: '1',
        creator: '管理员',
        createTime: '2023-05-10 09:30:25',
        modifier: '李四',
        modifyTime: '2023-06-15 14:20:10'
      },
      {
        id: 2,
        receiptType: '2',
        receiver: '李四',
        currentNo: 'B205',
        startPrefix: 'B',
        startNo: 201,
        endPrefix: 'B',
        endNo: 500,
        status: '1',
        creator: '王五',
        createTime: '2023-05-12 10:15:30',
        modifier: '王五',
        modifyTime: '2023-05-12 10:15:30'
      },
      {
        id: 3,
        receiptType: '3',
        receiver: '王五',
        currentNo: 'C3050',
        startPrefix: 'C',
        startNo: 3001,
        endPrefix: 'C',
        endNo: 4000,
        status: '0',
        creator: '赵六',
        createTime: '2023-05-15 13:45:00',
        modifier: '赵六',
        modifyTime: '2023-06-01 16:30:45'
      },
      // 更多模拟数据...
      ...Array.from({length: 17}, (_, i) => ({
        id: i + 4,
        receiptType: (Math.floor(Math.random() * 4) + 1).toString(),
        receiver: ['张三', '李四', '王五', '赵六'][Math.floor(Math.random() * 4)],
        currentNo: String.fromCharCode(65 + Math.floor(Math.random() * 26)) + 
                  (Math.floor(Math.random() * 9000) + 1000),
        startPrefix: String.fromCharCode(65 + Math.floor(Math.random() * 26)),
        startNo: Math.floor(Math.random() * 9000) + 1000,
        endPrefix: String.fromCharCode(65 + Math.floor(Math.random() * 26)),
        endNo: Math.floor(Math.random() * 9000) + 1000,
        status: Math.random() > 0.3 ? '1' : '0',
        creator: ['管理员', '张三', '李四', '王五'][Math.floor(Math.random() * 4)],
        createTime: '2023-06-' + (Math.floor(Math.random() * 30) + 1).toString().padStart(2, '0') + ' ' + 
                   Math.floor(Math.random() * 24).toString().padStart(2, '0') + ':' + 
                   Math.floor(Math.random() * 60).toString().padStart(2, '0') + ':' + 
                   Math.floor(Math.random() * 60).toString().padStart(2, '0'),
        modifier: ['管理员', '张三', '李四', '王五'][Math.floor(Math.random() * 4)],
        modifyTime: '2023-06-' + (Math.floor(Math.random() * 30) + 1).toString().padStart(2, '0') + ' ' + 
                   Math.floor(Math.random() * 24).toString().padStart(2, '0') + ':' + 
                   Math.floor(Math.random() * 60).toString().padStart(2, '0') + ':' + 
                   Math.floor(Math.random() * 60).toString().padStart(2, '0')
      }))
    ]
    
    // 模拟筛选
    let filteredData = mockData
    if (queryParams.receiver) {
      filteredData = filteredData.filter(item => item.receiver.includes(queryParams.receiver))
    }
    if (queryParams.receiptType) {
      filteredData = filteredData.filter(item => item.receiptType === queryParams.receiptType)
    }
    if (queryParams.status) {
      filteredData = filteredData.filter(item => item.status === queryParams.status)
    }
    
    tableData.value = filteredData
    totalItems.value = filteredData.length
  } catch (error) {
    ElMessage.error('数据加载失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchTableData()
}

// 重置
const handleReset = () => {
  queryParams.receiver = ''
  queryParams.receiptType = ''
  queryParams.status = ''
  currentPage.value = 1
  fetchTableData()
}

// 分页大小变化
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchTableData()
}

// 页码变化
const handlePageChange = (val) => {
  currentPage.value = val
  fetchTableData()
}

// 显示新建对话框
const showCreateDialog = () => {
  isEdit.value = false
  Object.assign(receiptForm, {
    receiptType: '',
    startPrefix: '',
    startNo: 1,
    endPrefix: '',
    endNo: 1,
    receiver: '',
    status: '1'
  })
  receiptDialogVisible.value = true
}

// 编辑收据
const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(receiptForm, {
    ...row,
    // 转换类型等操作可以在这里进行
  })
  receiptDialogVisible.value = true
}

// 删除收据
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除收据 "${row.startPrefix}${row.startNo}-${row.endPrefix}${row.endNo}" 吗?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 实际项目中调用删除API
    ElMessage.success('删除成功')
    fetchTableData()
  }).catch(() => {
    ElMessage.info('已取消删除')
  })
}

// 提交收据表单
const submitReceiptForm = () => {
  receiptFormRef.value.validate((valid) => {
    if (valid) {
      // 模拟保存操作
      const action = isEdit.value ? '修改' : '新增'
      ElMessage.success(`${action}收据成功`)
      receiptDialogVisible.value = false
      fetchTableData()
    }
  })
}

// 初始化加载数据
onMounted(() => {
  fetchTableData()
})
</script>

<style scoped>
.receipt-management-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.query-section {
  margin-bottom: 20px;
}

.display-header {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 20px;
}

.table-container {
  margin-top: 20px;
}

.action-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>