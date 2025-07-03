<template>
  <div class="login-container">
    <!-- 粒子背景 -->
    <vue-particles 
      class="particles" 
      color="#409EFF" 
      :particleOpacity="0.7" 
      :particlesNumber="60" 
      shapeType="circle"
      :particleSize="4" 
      linesColor="#409EFF" 
      :linesWidth="1" 
      :lineLinked="true" 
      :lineOpacity="0.4"
      :linesDistance="150" 
      :moveSpeed="2"
    ></vue-particles>
    
    <el-card class="login-box" :class="{'animated-box': animatedBox}">
      <h2 class="login-title">门诊挂号收费系统</h2>
      <el-form :model="loginForm" :rules="loginRules" ref="loginFormRef">
        <el-form-item prop="account">
          <el-input
            v-model="loginForm.account"
            placeholder="请输入账号"
            size="large"
            type="text"
            @focus="handleInputFocus('account')"
            @blur="handleInputBlur"
          >
            <template #prefix>
              <el-icon class="input-icon"><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            show-password
            @focus="handleInputFocus('password')"
            @blur="handleInputBlur"
          >
            <template #prefix>
              <el-icon class="input-icon"><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item>
          <el-button 
            type="primary" 
            size="large"
            @click="handleLogin"
            class="login-btn"
            :loading="loading"
          >
            <span v-if="!loading">登 录</span>
            <span v-else>登 录 中...</span>
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { login } from '@/api/auth'
import VueParticles from 'vue-particles'

const router = useRouter()
const loading = ref(false)
const loginFormRef = ref(null)
const animatedBox = ref(false)
const activeInput = ref(null)

const loginForm = ref({
  account: '',
  password: ''
})

const loginRules = {
  account: [
    { required: true, message: '请输入账号', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 4, message: '密码长度不能小于4个字符', trigger: 'blur' }
  ]
}

onMounted(() => {
  setTimeout(() => {
    animatedBox.value = true
  }, 100)
})

const handleInputFocus = (field) => {
  activeInput.value = field
}

const handleInputBlur = () => {
  activeInput.value = null
}

// 提取账号中的数字部分
const extractNumberFromAccount = (account) => {
  const match = account.match(/\d+/)
  return match ? parseInt(match[0]) : 0
}

const handleLogin = () => {
  loginFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    loading.value = true
    
    try {
      const { data } = await login({
        account: loginForm.value.account,
        password: loginForm.value.password,
      });
      
      if (data) {
        // 存储完整的用户信息，包括提取的数字ID
        const userData = {
          id: extractNumberFromAccount(data.account), // 这里修改为提取的数字ID
          account: data.account,
          name: data.name || `用户${data.account}`,
          rank: data.rank,
          lastLoginTime: data.lastLoginTime || new Date().toLocaleString(),
          encryptedPassword: data.encryptedPassword,
          fullAccount: data.account // 保留完整账号
        }
        localStorage.setItem('userInfo', JSON.stringify(userData))
        router.push('/home')
        ElMessage.success('登录成功')
      }
    } catch (error) {
      console.error('登录出错:', error)
      let errorMessage = '登录失败'
      if (error.response) {
        errorMessage += `: ${error.response.data?.message || error.response.statusText}`
      } else {
        errorMessage += `: ${error.message}`
      }
      ElMessage.error(errorMessage)
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  position: relative;
  overflow: hidden;
}

.particles {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
}

.login-box {
  width: 400px;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  background-color: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(5px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  position: relative;
  z-index: 1;
  opacity: 0;
  transform: translateY(20px);
  transition: all 0.5s ease;
}

.login-box.animated-box {
  opacity: 1;
  transform: translateY(0);
}

.login-title {
  text-align: center;
  margin-bottom: 40px;
  color: #409EFF;
  font-size: 24px;
  font-weight: bold;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  position: relative;
}

.login-title::after {
  content: '';
  position: absolute;
  bottom: -10px;
  left: 50%;
  transform: translateX(-50%);
  width: 50px;
  height: 3px;
  background: #409EFF;
  border-radius: 3px;
}

.login-btn {
  width: 100%;
  font-size: 16px;
  letter-spacing: 2px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.login-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 5px 15px rgba(64, 158, 255, 0.4);
}

.login-btn:active {
  transform: translateY(0);
}

.login-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: 0.5s;
}

.login-btn:hover::before {
  left: 100%;
}

:deep(.el-input__wrapper) {
  padding: 0 15px;
  transition: all 0.3s ease;
  background-color: rgba(255, 255, 255, 0.8);
  box-shadow: 0 0 0 1px #dcdfe6 inset;
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #409EFF inset;
  transform: translateY(-2px);
}

:deep(.el-input__prefix) {
  display: flex;
  align-items: center;
  margin-right: 10px;
  transition: all 0.3s ease;
}

.input-icon {
  transition: all 0.3s ease;
}

:deep(.el-input__wrapper.is-focus) .input-icon {
  color: #409EFF;
  transform: scale(1.2);
}

/* 移除数字输入框的增减按钮 */
:deep(input[type="number"]::-webkit-outer-spin-button),
:deep(input[type="number"]::-webkit-inner-spin-button) {
  -webkit-appearance: none;
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .login-box {
    width: 90%;
    padding: 30px 20px;
  }
}
</style>