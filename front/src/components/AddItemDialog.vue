 <template>
  <el-dialog v-model="visible" title="添加收费项目" width="80%">
    <el-form :inline="true" class="filter-form">
      <el-form-item label="项目类型">
        <el-select 
          v-model="selectedType" 
          placeholder="请选择项目类型"
          @change="fetchChargeItems"
          :loading="typeLoading"
        >
          <el-option 
            v-for="type in chargeItemTypes" 
            :key="type.value" 
            :label="type.label" 
            :value="type.value"
          />
        </el-select>
      </el-form-item>
    </el-form>
    
    <el-table 
      :data="medicalItems" 
      border 
      style="width: 100%"
      v-loading="itemLoading"
    >
    <el-table-column prop="chargeItemId" label="项目ID" width="120" />
      <el-table-column prop="chargeItemCode" label="项目编码" width="120" />
      <el-table-column prop="chargeItemName" label="项目名称" width="180" />
      <el-table-column prop="chargeItemUnit" label="单位" width="80" />
      <el-table-column prop="chargeItemPrice" label="单价" width="100" />
      <el-table-column prop="depname" label="执行科室" width="120" />
      <el-table-column prop="chargeItemMethod" label="备注" width="100" />
      <el-table-column label="操作" width="80">
        <template #default="{ row }">
          <el-button 
            type="primary" 
            size="small" 
            @click="handleSelect(row)"
            :disabled="row.chargeItemState !== '启用'"
          >
            选择
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-dialog>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  getAllChargeItemTypes,
  getChargeItemsByType 
} from '@/api/charge'

const props = defineProps({
  modelValue: Boolean
})

const emit = defineEmits(['update:modelValue', 'select-item'])

const visible = computed({
  get() {
    return props.modelValue
  },
  set(value) {
    emit('update:modelValue', value)
  }
})

const selectedType = ref('')
const medicalItems = ref([])
const chargeItemTypes = ref([])
const typeLoading = ref(false)
const itemLoading = ref(false)

// 加载项目类型
const loadChargeItemTypes = async () => {
  typeLoading.value = true
  try {
    const response = await getAllChargeItemTypes()
    // 假设后端返回的是字符串数组，如 ["毒性处方", "手术", ...]
    chargeItemTypes.value = response.data.data.map(type => ({
      value: type,
      label: type
    }))
    
    // 如果有默认类型，可以设置
    if (chargeItemTypes.value.length > 0) {
      selectedType.value = chargeItemTypes.value[0].value
    }
  } catch (error) {
    ElMessage.error('加载项目类型失败: ' + (error.response?.data?.message || error.message))
    chargeItemTypes.value = []
  } finally {
    typeLoading.value = false
  }
}

// 获取收费项目
const fetchChargeItems = async () => {
  if (!selectedType.value) return
  
  itemLoading.value = true
  medicalItems.value = []
  
  try {
    const response = await getChargeItemsByType(selectedType.value)
    medicalItems.value = response.data.data.map(item => ({
      ...item,
      // 保持与父组件兼容的字段
      itemId: item.chargeItemId,
      itemCode: item.chargeItemCode,
      itemName: item.chargeItemName,
      unit: item.chargeItemUnit,
      unitPrice: item.chargeItemPrice,
      pinyinCode: item.chargeItemCode, // 如果没有拼音码，使用编码
      executDept: item.depname,
      note: item.chargeItemMethod
    }))
  } catch (error) {
    ElMessage.error('获取收费项目失败: ' + (error.response?.data?.message || error.message))
  } finally {
    itemLoading.value = false
  }
}

const handleSelect = (item) => {
  // 构造父组件需要的数据结构
  const selectedItem = {
    itemId: item.chargeItemId,
    itemCode: item.chargeItemCode,
    itemName: item.chargeItemName,
    unit: item.chargeItemUnit,
    unitPrice: item.chargeItemPrice,
    pinyinCode: item.chargeItemCode,
    executDept: item.depname,
    note: item.chargeItemMethod
  }
  emit('select-item', selectedItem)
  visible.value = false
}

// 组件挂载时加载类型
onMounted(() => {
  loadChargeItemTypes()
})
</script>

<style scoped>
.filter-form {
  margin-bottom: 20px;
}
</style>