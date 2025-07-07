<template>
  <el-dialog
    title="处方收费"
    v-model="visible"
    width="500px"
    :before-close="handleClose"
    :close-on-click-modal="false"
  >
    <el-form label-width="100px">
      <el-form-item label="处方序号" v-if="showData.preSequences.length === 1">
        <el-input :model-value="showData.preSequences[0]" readonly />
      </el-form-item>
      
      <el-form-item label="处方列表" v-else>
        <div class="prescription-list">
          <div 
            v-for="(seq, index) in showData.preSequences" 
            :key="index"
            class="prescription-item"
          >
            <el-tag>{{ seq }}</el-tag>
          </div>
        </div>
      </el-form-item>
      
      <el-form-item label="收费金额">
        <el-input :model-value="showData.amount.toFixed(2)" readonly>
          <template #append>元</template>
        </el-input>
      </el-form-item>
      
      <el-form-item label="收费方式" required>
        <el-select 
          v-model="paymentType" 
          placeholder="请选择收费方式"
          style="width: 100%"
        >
          <el-option
            v-for="item in paymentOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      
      <el-form-item label="操作员" v-if="showData.dealerId">
        <el-input :model-value="`操作员 ${showData.dealerId}`" readonly />
      </el-form-item>
    </el-form>
    
    <template #footer>
      <el-button @click="handleClose" :disabled="paying">取消</el-button>
      <el-button 
        type="primary" 
        @click="handlePayment" 
        :loading="paying"
        :disabled="!paymentType"
      >
        确认收费
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { payPrescription } from '@/api/prescription'

const emit = defineEmits(['success', 'close'])

const visible = ref(false)
const paying = ref(false)
const paymentType = ref('')

const showData = reactive({
  preSequences: [],
  amount: 0,
  dealerId: null
})

// 严格匹配后端枚举值
const paymentOptions = [
  { value: '现金', label: '现金' },
  { value: '医保支付', label: '医保支付' },
  { value: '就诊卡', label: '就诊卡' },
  { value: '扫码支付', label: '扫码支付' }
]

const show = (params) => {
  Object.assign(showData, {
    preSequences: Array.isArray(params.preSequences) ? params.preSequences : [params.preSequences],
    amount: params.amount || 0,
    dealerId: params.dealerId || null
  })
  paymentType.value = ''
  visible.value = true
}

const handleClose = () => {
  if (!paying.value) {
    visible.value = false
    emit('close')
  }
}

const handlePayment = async () => {
  if (!paymentType.value) {
    ElMessage.warning('请选择收费方式')
    return
  }

  paying.value = true
  const results = []
  
  try {
    // 逐个处理处方收费
    for (const seq of showData.preSequences) {
      try {
        await payPrescription(seq, showData.dealerId, paymentType.value)
        results.push({ seq, success: true })
      } catch (error) {
        console.error(`处方 ${seq} 收费失败:`, error)
        results.push({ 
          seq, 
          success: false, 
          message: error.response?.data?.message || error.message 
        })
      }
    }

    // 处理结果
    const successCount = results.filter(r => r.success).length
    const failedItems = results.filter(r => !r.success)
    
    if (successCount > 0) {
      ElMessage.success(`成功收费 ${successCount} 笔处方`)
      emit('success')
    }
    
    if (failedItems.length > 0) {
      const errorMsg = failedItems.map(i => `处方 ${i.seq}: ${i.message}`).join('；')
      ElMessage.error(`部分处方收费失败：${errorMsg}`)
    }

    // 只要有成功就关闭对话框
    if (successCount > 0) {
      visible.value = false
    }
  } catch (error) {
    ElMessage.error('收费过程出错：' + (error.message || '未知错误'))
  } finally {
    paying.value = false
  }
}

defineExpose({ show })
</script>

<style scoped>
.prescription-list {
  max-height: 200px;
  overflow-y: auto;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 10px;
}

.prescription-item {
  display: inline-block;
  margin: 0 8px 8px 0;
}
</style>