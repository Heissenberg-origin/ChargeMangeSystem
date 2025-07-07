<template>
  <el-dialog
    title="支付挂号费"
    v-model="visible"
    width="500px"
    :before-close="handleClose"
  >
    <el-form label-width="100px">
      <el-form-item label="挂号ID" v-if="showData.regIds.length === 1">
        <el-input :model-value="showData.regIds[0]" readonly />
      </el-form-item>
      
      <el-form-item label="挂号ID列表" v-if="showData.regIds.length > 1">
        <el-tag 
          v-for="id in showData.regIds" 
          :key="id" 
          style="margin-right: 5px; margin-bottom: 5px"
        >
          {{ id }}
        </el-tag>
      </el-form-item>
      
      <el-form-item label="支付金额">
        <el-input :model-value="showData.amount" readonly>
          <template #append>元</template>
        </el-input>
      </el-form-item>
      
      <el-form-item label="支付方式" prop="paymentType">
        <el-select v-model="paymentType" placeholder="请选择支付方式">
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
      <el-button @click="handleClose">取消</el-button>
      <el-button 
        type="primary" 
        @click="handlePayment" 
        :loading="paying"
      >
        确认支付
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { processRegistrationPayment } from '@/api/registration'

const emit = defineEmits(['success', 'close'])

const visible = ref(false)
const paying = ref(false)
const paymentType = ref('')

// 使用 reactive 对象来存储显示数据
const showData = reactive({
  regIds: [],
  amount: 0,
  dealerId: null
})

const paymentOptions = [
  { value: 'CASH', label: '现金支付' },
  { value: 'SCAN_PAY', label: '扫码支付' },
  { value: 'MEDICAL_CARD', label: '就诊卡支付' },
  { value: 'INSURANCE_PAY', label: '医保支付' }
]

const show = (params) => {
  // 更新 showData 而不是直接修改 props
  Object.assign(showData, params)
  visible.value = true
  paymentType.value = ''
}

const handleClose = () => {
  visible.value = false
  emit('close')
}

const handlePayment = async () => {
  if (!paymentType.value) {
    ElMessage.warning('请选择支付方式')
    return
  }
  
  paying.value = true
  try {
    // 批量处理支付
    for (const regId of showData.regIds) {
      await processRegistrationPayment(
        regId,
        showData.dealerId,
        paymentType.value
      )
    }
    
    ElMessage.success(`成功支付 ${showData.regIds.length} 笔挂号费用，总金额 ¥${showData.amount.toFixed(2)}`)
    emit('success')
    visible.value = false
  } catch (error) {
    console.error('支付失败:', {
      error,
      regIds: showData.regIds,
      dealerId: showData.dealerId,
      paymentType: paymentType.value
    })
    ElMessage.error('支付失败: ' + (error.message || '请稍后重试'))
  } finally {
    paying.value = false
  }
}

defineExpose({ show })
</script>