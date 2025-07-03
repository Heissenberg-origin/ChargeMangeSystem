<template>
  <div class="prescription-detail-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>处方详情</span>
          <el-button 
            type="danger" 
            @click="handleRefund"
            :disabled="prescriptionData.preState !== '待执行'"
            v-if="prescriptionData.preState !== '已退费'"
          >
            退费
          </el-button>
        </div>
      </template>

      <!-- 调试信息显示 -->
      <div v-if="debugInfo" style="color: red; margin-bottom: 20px;">
        <p>接收到的路由参数: {{ JSON.stringify(route.params) }}</p>
        <p>接收到的state数据: {{ JSON.stringify(routeState) }}</p>
        <p>当前处方数据: {{ JSON.stringify(prescriptionData) }}</p>
      </div>

      <el-descriptions :column="2" border>
        <!-- 描述项保持不变 -->
      </el-descriptions>

      <!-- 其他部分保持不变 -->
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const route = useRoute()

// 调试模式
const debugInfo = ref(true)
const routeState = ref(history.state)

// 处方数据
const prescriptionData = ref({
  // 初始化数据保持不变
})

// 初始化数据
// 初始化数据
onMounted(() => {
  console.log('完整history.state:', history.state)
  
  // 从state获取处方数据
  if (history.state?.prescriptionData) {
    // 检查是否是完整对象
    if (typeof history.state.prescriptionData === 'object' && history.state.prescriptionData.preId) {
      prescriptionData.value = history.state.prescriptionData
      console.log('成功获取处方数据:', prescriptionData.value)
    } else {
      console.error('处方数据格式不正确:', history.state.prescriptionData)
      ElMessage.error('处方数据格式错误')
      router.go(-1)
    }
  } else {
    console.error('未找到处方数据')
    ElMessage.error('未获取到处方数据')
    router.go(-1)
  }
})

// 其他方法保持不变
</script>