<template>
  <div class="project-management-container">
    <!-- 查询条件区域 -->
    <div class="query-section">
      <el-card shadow="never">
        <el-form :model="queryParams" label-width="80px">
          <el-row :gutter="20">
            <el-col :span="6">
              <el-form-item label="项目编码">
                <el-input v-model="queryParams.projectCode" placeholder="请输入项目编码" clearable />
              </el-form-item>
            </el-col>
            
            <el-col :span="6">
              <el-form-item label="项目名称">
                <el-input v-model="queryParams.projectName" placeholder="请输入项目名称" clearable />
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
              新建项目
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
            <el-table-column prop="projectCode" label="项目编码" align="center" width="120" />
            <el-table-column prop="projectName" label="项目名称" align="center" width="150" />
            <el-table-column prop="pinyinCode" label="拼音码" align="center" width="120" />
            <el-table-column prop="unit" label="单位" align="center" width="80">
              <template #default="{row}">
                {{ unitOptions.find(u => u.value === row.unit)?.label || row.unit }}
              </template>
            </el-table-column>
            <el-table-column prop="price" label="单价(元)" align="center" width="100">
              <template #default="{row}">
                {{ formatCurrency(row.price) }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" align="center" width="100">
              <template #default="{row}">
                <el-tag :type="row.status === '1' ? 'success' : 'danger'">
                  {{ row.status === '1' ? '启用' : '禁用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="feeType" label="费用类型" align="center" width="120">
              <template #default="{row}">
                {{ feeTypeOptions.find(f => f.value === row.feeType)?.label || row.feeType }}
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
    
    <!-- 新建/编辑项目对话框 -->
    <el-dialog 
      v-model="projectDialogVisible" 
      :title="`${isEdit ? '编辑' : '新建'}项目`"
      width="600px"
    >
      <el-form :model="projectForm" :rules="rules" ref="projectFormRef" label-width="100px">
        <el-form-item label="项目编码" prop="projectCode">
          <el-input v-model="projectForm.projectCode" placeholder="请输入项目编码" />
        </el-form-item>
        
        <el-form-item label="项目名称" prop="projectName">
          <el-input v-model="projectForm.projectName" placeholder="请输入项目名称" />
        </el-form-item>
        
        <el-form-item label="单位" prop="unit">
          <el-select v-model="projectForm.unit" placeholder="请选择单位">
            <el-option
              v-for="item in unitOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="单价(元)" prop="price">
          <el-input-number 
            v-model="projectForm.price" 
            :min="0" 
            :precision="2" 
            :controls="false"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="费用类型" prop="feeType">
          <el-select v-model="projectForm.feeType" placeholder="请选择费用类型">
            <el-option
              v-for="item in feeTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="projectForm.status">
            <el-radio label="1">启用</el-radio>
            <el-radio label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="projectDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitProjectForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 查询参数
const queryParams = reactive({
  projectCode: '',
  projectName: '',
  status: ''
})

// 分页参数
const currentPage = ref(1)
const pageSize = ref(10)
const totalItems = ref(0)
const loading = ref(false)

// 表格数据
const tableData = ref([])

// 单位选项
const unitOptions = [
  { value: '次', label: '次' },
  { value: '小时', label: '小时' },
  { value: '天', label: '天' },
  { value: '月', label: '月' },
  { value: '年', label: '年' }
]

// 费用类型选项
const feeTypeOptions = [
  { value: '1', label: '检查费' },
  { value: '2', label: '治疗费' },
  { value: '3', label: '手术费' },
  { value: '4', label: '材料费' }
]

// 项目对话框相关
const projectDialogVisible = ref(false)
const isEdit = ref(false)
const projectFormRef = ref(null)
const projectForm = reactive({
  projectCode: '',
  projectName: '',
  unit: '次',
  price: 0,
  feeType: '',
  status: '1'
})

// 表单验证规则
const rules = {
  projectCode: [
    { required: true, message: '请输入项目编码', trigger: 'blur' }
  ],
  projectName: [
    { required: true, message: '请输入项目名称', trigger: 'blur' }
  ],
  unit: [
    { required: true, message: '请选择单位', trigger: 'change' }
  ],
  price: [
    { required: true, message: '请输入单价', trigger: 'blur' }
  ],
  feeType: [
    { required: true, message: '请选择费用类型', trigger: 'change' }
  ]
}

// 格式化货币
const formatCurrency = (value) => {
  return parseFloat(value).toLocaleString('zh-CN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
}

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
        projectCode: 'P001',
        projectName: '血常规检查',
        pinyinCode: 'XCJJC',
        unit: '次',
        price: 50.00,
        status: '1',
        feeType: '1',
        creator: '张三',
        createTime: '2023-05-10 09:30:25',
        modifier: '李四',
        modifyTime: '2023-06-15 14:20:10'
      },
      {
        id: 2,
        projectCode: 'P002',
        projectName: '心电图检查',
        pinyinCode: 'XDTJC',
        unit: '次',
        price: 80.00,
        status: '1',
        feeType: '1',
        creator: '王五',
        createTime: '2023-05-12 10:15:30',
        modifier: '王五',
        modifyTime: '2023-05-12 10:15:30'
      },
      {
        id: 3,
        projectCode: 'P003',
        projectName: '拔牙手术',
        pinyinCode: 'BYSS',
        unit: '次',
        price: 200.00,
        status: '0',
        feeType: '3',
        creator: '赵六',
        createTime: '2023-05-15 13:45:00',
        modifier: '赵六',
        modifyTime: '2023-06-01 16:30:45'
      },
      // 更多模拟数据...
      ...Array.from({length: 17}, (_, i) => ({
        id: i + 4,
        projectCode: `P${(i + 4).toString().padStart(3, '0')}`,
        projectName: `医疗项目${i + 4}`,
        pinyinCode: `YLXM${i + 4}`,
        unit: unitOptions[Math.floor(Math.random() * unitOptions.length)].value,
        price: (Math.random() * 500 + 50).toFixed(2),
        status: Math.random() > 0.3 ? '1' : '0',
        feeType: (Math.floor(Math.random() * 4) + 1).toString(),
        creator: ['张三', '李四', '王五', '赵六'][Math.floor(Math.random() * 4)],
        createTime: '2023-06-' + (Math.floor(Math.random() * 30) + 1).toString().padStart(2, '0') + ' ' + 
                   Math.floor(Math.random() * 24).toString().padStart(2, '0') + ':' + 
                   Math.floor(Math.random() * 60).toString().padStart(2, '0') + ':' + 
                   Math.floor(Math.random() * 60).toString().padStart(2, '0'),
        modifier: ['张三', '李四', '王五', '赵六'][Math.floor(Math.random() * 4)],
        modifyTime: '2023-06-' + (Math.floor(Math.random() * 30) + 1).toString().padStart(2, '0') + ' ' + 
                   Math.floor(Math.random() * 24).toString().padStart(2, '0') + ':' + 
                   Math.floor(Math.random() * 60).toString().padStart(2, '0') + ':' + 
                   Math.floor(Math.random() * 60).toString().padStart(2, '0')
      }))
    ]
    
    // 模拟筛选
    let filteredData = mockData
    if (queryParams.projectCode) {
      filteredData = filteredData.filter(item => item.projectCode.includes(queryParams.projectCode))
    }
    if (queryParams.projectName) {
      filteredData = filteredData.filter(item => item.projectName.includes(queryParams.projectName))
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
  queryParams.projectCode = ''
  queryParams.projectName = ''
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
  Object.assign(projectForm, {
    projectCode: '',
    projectName: '',
    unit: '次',
    price: 0,
    feeType: '',
    status: '1'
  })
  projectDialogVisible.value = true
}

// 编辑项目
const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(projectForm, {
    ...row,
    // 转换类型等操作可以在这里进行
  })
  projectDialogVisible.value = true
}

// 删除项目
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除项目 "${row.projectName}" 吗?`, '提示', {
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

// 提交项目表单
const submitProjectForm = () => {
  projectFormRef.value.validate((valid) => {
    if (valid) {
      // 模拟保存操作
      ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
      projectDialogVisible.value = false
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
.project-management-container {
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