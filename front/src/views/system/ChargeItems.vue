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
            <!-- 新增的执行部门列 -->
            <el-table-column prop="depname" label="执行部门" align="center" width="120" />
            <!-- 新增的项目余量列 -->
            <el-table-column prop="chargeItemBalance" label="项目余量" align="center" width="100" />
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
        <el-form-item label="项目拼音码" prop="projectCode">
          <el-input v-model="projectForm.projectCode" placeholder="请输入项目拼音码" />
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

        <!-- 在对话框表单中添加新字段 -->
        <el-form-item label="执行部门" prop="depname">
          <el-input v-model="projectForm.depname" placeholder="请输入执行部门" />
        </el-form-item>

        <el-form-item label="执行方法" prop="chargeItemMethod">
          <el-input v-model="projectForm.chargeItemMethod" placeholder="请输入执行方法" />
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
import { ref, reactive, onMounted, computed } from 'vue'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getChargeitemList, deleteChargeItem, updateChargeItem, createChargeItem } from '@/api/prescription'

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

// 表格数据（显示用）
const tableData = ref([])
// 原始数据（从接口获取的全部数据）
const rawTableData = ref([])

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
  status: '1',
  depname: '', // 新增执行部门
  chargeItemMethod: '', // 新增执行方法
  chargeItemExDepId: 0 // 新增执行部门ID
})

// 计算最大ID，用于自动生成新ID
const maxId = computed(() => {
  return rawTableData.value.length > 0 
    ? Math.max(...rawTableData.value.map(item => item.id)) + 1
    : 1000
})

// 表单验证规则

const rules = {
  projectCode: [
    { required: true, message: '请输入项目拼音码', trigger: 'blur' },
    { max: 20, message: '长度不能超过20个字符', trigger: 'blur' }
  ],
  projectName: [
    { required: true, message: '请输入项目名称', trigger: 'blur' },
    { max: 50, message: '长度不能超过50个字符', trigger: 'blur' }
  ],
  unit: [
    { required: true, message: '请选择单位', trigger: 'change' }
  ],
  price: [
    { required: true, message: '请输入单价', trigger: 'blur' },
    { type: 'number', min: 0, message: '单价不能小于0', trigger: 'blur' }
  ],
  feeType: [
    { required: true, message: '请选择费用类型', trigger: 'change' }
  ],
  // 新增字段的验证规则
  depname: [
    { required: true, message: '请输入执行部门', trigger: 'blur' },
    { max: 50, message: '长度不能超过50个字符', trigger: 'blur' }
  ],
  chargeItemMethod: [
    { required: true, message: '请输入执行方法', trigger: 'blur' },
    { max: 50, message: '长度不能超过50个字符', trigger: 'blur' }
  ],
  chargeItemExDepId: [
    { required: true, message: '请选择执行部门ID', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        if (value === null || value === undefined) {
          callback(new Error('请输入部门ID'))
        } else if (value < 0 || value > 88) {
          callback(new Error('部门ID必须在0-88之间'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 格式化金额显示
const formatCurrency = (value) => {
  return parseFloat(value).toLocaleString('zh-CN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
}

// 获取表格数据（一次性获取全部数据）
const fetchTableData = async () => {
  try {
    loading.value = true
    
    const response = await getChargeitemList({
      pageNum: 1,
      pageSize: 10000
    })

    console.log('响应数据为------', response)

    if (response.data?.code === '200') {
      rawTableData.value = response.data.data.map(item => ({
        id: item.chargeItemId,
        projectCode: item.chargeItemId,
        projectName: item.chargeItemName,
        pinyinCode: item.chargeItemCode,
        depname: item.depname || '未指定', // 新增的执行部门字段
        chargeItemBalance: item.chargeItemBalance || 0, // 新增的项目余量字段
        unit: item.chargeItemUnit,
        price: item.chargeItemPrice,
        status: item.chargeItemState === '启用' ? '1' : '0',
        feeType: getFeeTypeValue(item.chargeItemType),
        creator: item.chargeItemCreator,
        createTime: item.chargeItemTime,
        originalData: {
          chargeItemId: item.chargeItemId,
          chargeItemName: item.chargeItemName,
          chargeItemType: item.chargeItemType,
          chargeItemCode: item.chargeItemCode,
          chargeItemExDepId: item.chargeItemExDepId,
          depname: item.depname || 'string',
          chargeItemMethod: item.chargeItemMethod,
          chargeItemUnit: item.chargeItemUnit,
          chargeItemBalance: item.chargeItemBalance,
          chargeItemPrice: item.chargeItemPrice,
          chargeItemState: item.chargeItemState,
          chargeItemCreator: item.chargeItemCreator,
          chargeItemTime: item.chargeItemTime,
          chargeItemLatestFixer: item.chargeItemLatestFixer || 'admin'
        }
      }))
      
      filterTableData()
    } else {
      ElMessage.error(response.data?.message || '获取数据失败')
    }
  } catch (error) {
    console.error('获取收费项目列表失败:', error)
    ElMessage.error('获取数据失败: ' + (error.message || '网络错误'))
  } finally {
    loading.value = false
  }
}

// 前端过滤数据（实现模糊查询）
const filterTableData = () => {
  let filteredData = [...rawTableData.value]
  
  // 项目编码模糊查询
  if (queryParams.projectCode) {
    const searchCode = queryParams.projectCode.toLowerCase()
    filteredData = filteredData.filter(item => 
      item.projectCode.toLowerCase().includes(searchCode)
    )
  }
  
  // 项目名称模糊查询
  if (queryParams.projectName) {
    const searchName = queryParams.projectName.toLowerCase()
    filteredData = filteredData.filter(item => 
      item.projectName.toLowerCase().includes(searchName)
    )
  }
  
  // 状态筛选
  if (queryParams.status) {
    filteredData = filteredData.filter(item => item.status === queryParams.status)
  }
  
  // 更新分页总数
  totalItems.value = filteredData.length
  
  // 计算当前页显示的数据
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  tableData.value = filteredData.slice(start, end) // 正确赋值给tableData
}

// 将费用类型文本转换为对应的值
const getFeeTypeValue = (type) => {
  switch(type) {
    case '检查': return '1'
    case '检验': return '1'
    case '治疗': return '2'
    case '手术': return '3'
    case '材料': return '4'
    default: return '1'
  }
}

// 搜索（前端模糊查询）
const handleSearch = () => {
  currentPage.value = 1
  filterTableData()
}

// 重置查询条件
const handleReset = () => {
  queryParams.projectCode = ''
  queryParams.projectName = ''
  queryParams.status = ''
  currentPage.value = 1
  filterTableData()
}

// 分页大小变化
const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
  filterTableData()
}

// 页码变化
const handlePageChange = (val) => {
  currentPage.value = val
  filterTableData()
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
    status: '1',
    depname: '',
    chargeItemMethod: '',
    chargeItemExDepId: Math.floor(Math.random() * 88 + 1) // 随机0-88的部门ID
  })
  projectDialogVisible.value = true
}

// 提交创建表单
const submitCreateForm = async () => {
  try {
    loading.value = true
    
    const requestBody = {
      chargeItemId: maxId.value, // 自动生成ID
      chargeItemName: projectForm.projectName,
      chargeItemType: getFeeTypeLabel(projectForm.feeType),
      chargeItemCode: projectForm.projectCode,
      chargeItemExDepId: projectForm.chargeItemExDepId,
      depname: projectForm.depname,
      chargeItemMethod: projectForm.chargeItemMethod,
      chargeItemUnit: projectForm.unit,
      chargeItemBalance: 50, // 默认余量50
      chargeItemPrice: projectForm.price,
      chargeItemState: projectForm.status === '1' ? '启用' : '禁用',
      chargeItemCreator: '系统管理员',
      chargeItemTime: formatDateTime(new Date()),
      chargeItemLatestFixer: '系统管理员'
    }

    const response = await createChargeItem(requestBody)
    
    if (response.status === 200 || response.data?.code === '200') {
      ElMessage.success('创建成功')
      projectDialogVisible.value = false
      fetchTableData()
    } else {
      ElMessage.error(response.data?.message || '创建失败')
    }
  } catch (error) {
    console.error('创建项目失败:', error)
    ElMessage.error('创建失败: ' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}


// 编辑项目
const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(projectForm, {
    id: row.id,
    projectCode: row.pinyinCode,
    projectName: row.projectName,
    unit: row.unit,
    price: row.price,
    feeType: row.feeType,
    status: row.status,
    // 新增字段的赋值
    depname: row.originalData.depname,
    chargeItemMethod: row.originalData.chargeItemMethod,
    chargeItemExDepId: row.originalData.chargeItemExDepId
  })
  projectDialogVisible.value = true
}

// 删除项目
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除项目 "${row.projectName}" 吗?`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 调用删除接口
    await deleteChargeItem(row.id)
    
    ElMessage.success('删除成功')
    
    // 方案2：重新获取数据（推荐，保证数据一致性）
    fetchTableData()
    
  } catch (error) {
    // 区分是取消删除还是删除失败
    if (error !== 'cancel' && error !== 'close') {
      ElMessage.error('删除失败: ' + (error.message || '未知错误'))
    } else {
      ElMessage.info('已取消删除')
    }
  }
}

const submitUpdateForm = async () => {
  try {
    loading.value = true
    
    const originalItem = rawTableData.value.find(item => item.id === projectForm.id)
    if (!originalItem) {
      ElMessage.error('未找到原始项目数据')
      return
    }
    
    const requestBody = {
      ...originalItem.originalData,
      chargeItemName: projectForm.projectName,
      chargeItemCode: projectForm.projectCode,
      chargeItemUnit: projectForm.unit,
      chargeItemPrice: projectForm.price,
      chargeItemType: getFeeTypeLabel(projectForm.feeType),
      chargeItemState: projectForm.status === '1' ? '启用' : '禁用',
      chargeItemLatestFixer: 'admin',
      chargeItemTime: formatDateTime(new Date()),
      depname: originalItem.originalData.depname,
      chargeItemBalance: originalItem.originalData.chargeItemBalance
    }

    const response = await updateChargeItem(projectForm.id, requestBody)
    
    if (response.status === 200 || response.data?.code === '200') {
      ElMessage.success('更新成功')
      projectDialogVisible.value = false
      fetchTableData()
    } else {
      ElMessage.error(response.data?.message || '更新失败')
    }
  } catch (error) {
    console.error('更新项目失败:', error)
    ElMessage.error('更新失败: ' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// 提交项目表单（更新逻辑）
const submitProjectForm = () => {
  projectFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    try {
      if (isEdit.value) {
        await submitUpdateForm()  // 添加await确保完成
      } else {
        await submitCreateForm()  // 添加await确保完成
      }
    } catch (error) {
      console.error('操作失败:', error)
      ElMessage.error('操作失败: ' + (error.message || '未知错误'))
    }
  })
}

// 格式化时间为 "YYYY-MM-DD HH:mm:ss"
const formatDateTime = (date) => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0') // 补零
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

// 将费用类型值转换为中文标签
const getFeeTypeLabel = (value) => {
  switch(value) {
    case '1': return '检查'
    case '2': return '治疗'
    case '3': return '手术'
    case '4': return '材料'
    default: return '检查'
  }
}

// 初始化加载数据
onMounted(() => {
  fetchTableData()
})

// 添加输入框回车搜索功能
const handleKeyUp = (e) => {
  if (e.key === 'Enter') {
    handleSearch()
  }
}
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