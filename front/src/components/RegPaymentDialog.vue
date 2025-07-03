<template>
  <el-dialog
    title="支付挂号费"
    v-model="visible"
    width="500px"
    :before-close="handleClose"
  >
    <el-form label-width="100px">
      <el-form-item label="患者ID">
        <el-input :model-value="hcardId" readonly />
      </el-form-item>
      
      <el-form-item label="挂号ID">
        <el-input :model-value="regId" readonly />
      </el-form-item>
      
      <el-form-item label="支付金额">
        <el-input :model-value="amount" readonly>
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
      
      <el-form-item label="操作员" prop="dealerId">
        <el-select v-model="dealerId" placeholder="请选择操作员">
          <el-option
            v-for="dealer in dealers"
            :key="dealer.id"
            :label="`操作员 ${dealer.id}`"
            :value="dealer.id"
          />
        </el-select>
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
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { processRegistrationPayment } from '@/api/registration'

const emit = defineEmits(['success', 'close'])

const visible = ref(false)
const paying = ref(false)
const paymentType = ref('')
const dealerId = ref(null)

const paymentOptions = [
  { value: 'CASH', label: '现金支付' },
  { value: 'SCAN_PAY', label: '扫码支付' },
  { value: 'MEDICAL_CARD', label: '就诊卡支付' },
  { value: 'INSURANCE_PAY', label: '医保支付' }
]

const dealers = [
  { id: 1, name: '操作员1' },
  { id: 2, name: '操作员2' },
  { id: 3, name: '操作员3' }
]

const props = defineProps({
  amount: {
    type: Number,
    required: true
  },
  hcardId: {
    type: Number,
    required: true
  },
  regId: {
    type: Number,
    required: true,
    validator: value => value > 0
  }
})

const show = () => {
  visible.value = true
  paymentType.value = ''
  dealerId.value = null
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
  
  if (!dealerId.value) {
    ElMessage.warning('请选择操作员')
    return
  }
  
  paying.value = true
  try {
    await processRegistrationPayment(
      props.regId,
      dealerId.value,
      paymentType.value
    )
    ElMessage.success('支付成功')
    emit('success')
    visible.value = false
  } catch (error) {
    console.error('支付失败:', {
      error,
      regId: props.regId,
      dealerId: dealerId.value,
      paymentType: paymentType.value
    })
    ElMessage.error('支付失败: ' + (error.message || '请稍后重试'))
  } finally {
    paying.value = false
  }
}

defineExpose({ show })
</script>