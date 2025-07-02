<template>
  <!-- 圆形图标状态 -->
  <div 
    class="chat-icon" 
    v-if="!isExpanded"
    @mousedown="startDrag"
    @touchstart="startDrag"
    :style="iconPosition"
  >
    <svg viewBox="0 0 24 24" width="24" height="24">
      <path fill="currentColor" d="M12 3c-4.97 0-9 3.185-9 7.115 0 2.557 1.522 4.82 3.889 6.115l-.78 2.77 3.116-1.65c.88.275 1.823.425 2.775.425 4.97 0 9-3.186 9-7.115C21 6.185 16.97 3 12 3z"/>
    </svg>
  </div>

  <!-- 展开的聊天界面 -->
  <transition name="chat">
    <div 
      class="chat-window" 
      v-if="isExpanded"
      @mousedown="startDrag"
      @touchstart="startDrag"
      :style="windowPosition"
    >
      <!-- 关闭按钮 -->
      <button class="close-btn" @click.stop="closeChat">
        <svg viewBox="0 0 24 24" width="16" height="16">
          <path fill="currentColor" d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
        </svg>
      </button>
      
      <!-- 聊天记录区域 -->
      <div class="chat-messages" ref="messagesContainer">
        <div 
          v-for="(item, index) in chatHistory" 
          :key="index"
          :class="['message', item.type]"
        >
          <div class="message-content">{{ item.content }}</div>
          <div class="message-time">{{ item.timestamp }}</div>
        </div>
        <div v-if="isLoading" class="message ai">
          <div class="message-content">思考中<span class="loading-dots">...</span></div>
        </div>
      </div>
      
      <!-- 输入区域 -->
      <div class="container-chat-options" @mousedown.stop @touchstart.stop>
        <div class="chat">
          <div class="chat-bot">
            <textarea 
              id="chat_bot" 
              name="chat_bot" 
              placeholder="请输入您的问题..."
              v-model="message"
              @keyup.enter="submitMessage"
              :disabled="isLoading"
              ref="textInput"
            ></textarea>
          </div>
          <div class="options">
            <div class="btns-add">
              <button @click.stop="handleButtonClick('audio')" :disabled="isLoading">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24">
                  <path fill="none" stroke="currentColor" stroke-linecap="round" stroke-linejoin="round"
                    stroke-width="2" d="M7 8v8a5 5 0 1 0 10 0V6.5a3.5 3.5 0 1 0-7 0V15a2 2 0 0 0 4 0V8">
                  </path>
                </svg>
              </button>
              <button @click.stop="handleButtonClick('image')" :disabled="isLoading">
                <svg viewBox="0 0 24 24" height="20" width="20" xmlns="http://www.w3.org/2000/svg">
                  <path
                    d="M4 5a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v4a1 1 0 0 1-1 1H5a1 1 0 0 1-1-1zm0 10a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v4a1 1 0 0 1-1 1H5a1 1 0 0 1-1-1zm10 0a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v4a1 1 0 0 1-1 1h-4a1 1 0 0 1-1-1zm0-8h6m-3-3v6"
                    stroke-width="2" stroke-linejoin="round" stroke-linecap="round"
                    stroke="currentColor" fill="none"></path>
                </svg>
              </button>
              <button @click.stop="handleButtonClick('settings')" :disabled="isLoading">
                <svg viewBox="0 0 24 24" height="20" width="20" xmlns="http://www.w3.org/2000/svg">
                  <path
                    d="M12 22C6.477 22 2 17.523 2 12S6.477 2 12 2s10 4.477 10 10s-4.477 10-10 10m-2.29-2.333A17.9 17.9 0 0 1 8.027 13H4.062a8.01 8.01 0 0 0 5.648 6.667M10.03 13c.151 2.439.848 4.73 1.97 6.752A15.9 15.9 0 0 0 13.97 13zm9.908 0h-3.965a17.9 17.9 0 0 1-1.683 6.667A8.01 8.01 0 0 0 19.938 13M4.062 11h3.965A17.9 17.9 0 0 1 9.71 4.333A8.01 8.01 0 0 0 4.062 11m5.969 0h3.938A15.9 15.9 0 0 0 12 4.248A15.9 15.9 0 0 0 10.03 11m4.259-6.667A17.9 17.9 0 0 1 15.973 11h3.965a8.01 8.01 0 0 0-5.648-6.667"
                    fill="currentColor"></path>
                </svg>
              </button>
            </div>
            <button 
              class="btn-submit" 
              @click.stop="submitMessage"
              :disabled="isLoading || !message.trim()"
            >
              <i>
                <svg viewBox="0 0 512 512">
                  <path fill="currentColor"
                    d="M473 39.05a24 24 0 0 0-25.5-5.46L47.47 185h-.08a24 24 0 0 0 1 45.16l.41.13l137.3 58.63a16 16 0 0 0 15.54-3.59L422 80a7.07 7.07 0 0 1 10 10L226.66 310.26a16 16 0 0 0-3.59 15.54l58.65 137.38c.06.2.12.38.19.57c3.2 9.27 11.3 15.81 21.09 16.25h1a24.63 24.63 0 0 0 23-15.46L478.39 64.62A24 24 0 0 0 473 39.05">
                  </path>
                </svg>
              </i>
            </button>
          </div>
        </div>
      </div>
      <div class="tags" @mousedown.stop @touchstart.stop>
        <span @click.stop="handleTagClick('image')">创建图片</span>
        <span @click.stop="handleTagClick('data')">分析数据</span>
        <span @click.stop="handleTagClick('more')">更多功能</span>
      </div>
    </div>
  </transition>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed, watch, nextTick } from 'vue'
import { aiChatBot } from '@/api/prescription' // 请替换为你的实际API文件路径

// 组件状态
const isExpanded = ref(false)
const message = ref('')
const isLoading = ref(false)
const chatHistory = ref([])
const messagesContainer = ref(null)
const textInput = ref(null)

// 拖动相关状态
const isDragging = ref(false)
const startPos = ref({ x: 0, y: 0 })
const dragStartPos = ref({ x: 0, y: 0 })
const currentPos = ref({ 
  x: window.innerWidth - 80, 
  y: window.innerHeight - 80 
})

// 监听窗口大小变化
const handleResize = () => {
  adjustPosition()
}

onMounted(() => {
  window.addEventListener('resize', handleResize)
  // 添加新的监听器
  window.addEventListener('orientationchange', handleResize)
  
  // 初始化时检查两次（解决某些移动端浏览器的视口问题）
  setTimeout(adjustPosition, 100)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  window.removeEventListener('orientationchange', handleResize)
})

// 从本地存储加载聊天历史
const loadChatHistory = () => {
  const savedHistory = localStorage.getItem('aiChatHistory')
  if (savedHistory) {
    try {
      chatHistory.value = JSON.parse(savedHistory)
    } catch (e) {
      console.error('Failed to parse chat history:', e)
    }
  }
}

// 保存聊天历史到本地存储
const saveChatHistory = () => {
  localStorage.setItem('aiChatHistory', JSON.stringify(chatHistory.value))
}

// 调整位置确保窗口不会超出可视区域
const adjustPosition = () => {
  // 确保图标不会超出可视区域
  currentPos.value.x = Math.max(20, Math.min(window.innerWidth - 60, currentPos.value.x))
  currentPos.value.y = Math.max(20, Math.min(window.innerHeight - 60, currentPos.value.y))
  
  if (isExpanded.value) {
    // 获取实际窗口高度（而不是预估）
    const windowWidth = 280
    const windowHeight = messagesContainer.value?.clientHeight 
      ? Math.min(messagesContainer.value.clientHeight + 150, window.innerHeight * 0.7)
      : 400 // 默认值
    
    // 计算窗口位置（在图标上方居中）
    let windowX = currentPos.value.x - windowWidth / 2
    let windowY = currentPos.value.y - windowHeight - 20
    
    // 边界检查（增加10px的安全边距）
    const safeMargin = 10
    windowX = Math.max(safeMargin, Math.min(
      window.innerWidth - windowWidth - safeMargin, 
      windowX
    ))
    
    // 垂直方向智能调整
    if (windowY < safeMargin) {
      // 如果上方空间不足，改为向下展开
      windowY = currentPos.value.y + 60
      // 确保不会超出底部
      if (windowY + windowHeight > window.innerHeight - safeMargin) {
        windowY = window.innerHeight - windowHeight - safeMargin
      }
    } else if (windowY + windowHeight > window.innerHeight - safeMargin) {
      // 如果下方空间不足，改为向上展开
      windowY = currentPos.value.y - windowHeight - 60
      if (windowY < safeMargin) {
        windowY = safeMargin
      }
    }
    
    // 更新图标位置以匹配窗口位置
    currentPos.value.x = windowX + windowWidth / 2
    currentPos.value.y = windowY + windowHeight + 20
  }
}

// 监听展开状态变化
watch(isExpanded, (newVal) => {
  if (newVal) {
    adjustPosition()
    // 展开后自动聚焦输入框
    nextTick(() => {
      textInput.value?.focus()
    })
  }
})

// 监听聊天历史变化
watch(chatHistory, () => {
  saveChatHistory()
  // 滚动到底部
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}, { deep: true })

// 计算样式
const iconPosition = computed(() => ({
  left: `${currentPos.value.x}px`,
  top: `${currentPos.value.y}px`
}))

const windowPosition = computed(() => {
  const windowWidth = 260
  const windowHeight = 200 // 预估高度
  
  // 计算窗口位置（在图标上方居中）
  let windowX = currentPos.value.x - windowWidth / 2
  let windowY = currentPos.value.y - windowHeight - 20
  
  // 确保窗口不会超出可视区域
  windowX = Math.max(10, Math.min(window.innerWidth - windowWidth - 10, windowX))
  windowY = Math.max(10, Math.min(window.innerHeight - windowHeight - 10, windowY))
  
  return {
    left: `${windowX}px`,
    top: `${windowY}px`
  }
})

// 开始拖动
const startDrag = (e) => {
  const clientX = e.clientX || e.touches[0].clientX
  const clientY = e.clientY || e.touches[0].clientY
  
  // 记录拖动开始位置
  dragStartPos.value = { x: clientX, y: clientY }
  
  startPos.value = {
    x: clientX - currentPos.value.x,
    y: clientY - currentPos.value.y
  }
  
  // 阻止默认行为和冒泡
  e.preventDefault()
  e.stopPropagation()
  
  // 添加移动和结束事件的监听
  document.addEventListener('mousemove', onDrag)
  document.addEventListener('mouseup', stopDrag)
  document.addEventListener('touchmove', onDrag, { passive: false })
  document.addEventListener('touchend', stopDrag)
}

// 拖动中
const onDrag = (e) => {
  const clientX = e.clientX || e.touches[0].clientX
  const clientY = e.clientY || e.touches[0].clientY
  
  // 计算移动距离
  const dx = clientX - dragStartPos.value.x
  const dy = clientY - dragStartPos.value.y
  const distance = Math.sqrt(dx * dx + dy * dy)
  
  // 如果移动距离大于5px，则认为是拖动
  if (distance > 5) {
    isDragging.value = true
  }
  
  if (!isDragging.value) return
  
  // 计算新位置
  let newX = clientX - startPos.value.x
  let newY = clientY - startPos.value.y
  
  // 限制在可视区域内
  newX = Math.max(20, Math.min(window.innerWidth - 60, newX))
  newY = Math.max(20, Math.min(window.innerHeight - 60, newY))
  
  currentPos.value = { x: newX, y: newY }
  
  e.preventDefault()
}

// 停止拖动
const stopDrag = (e) => {
  // 如果不是拖动，则认为是点击
  if (!isDragging.value) {
    openChat()
  }
  
  isDragging.value = false
  document.removeEventListener('mousemove', onDrag)
  document.removeEventListener('mouseup', stopDrag)
  document.removeEventListener('touchmove', onDrag)
  document.removeEventListener('touchend', stopDrag)
  
  // 阻止冒泡，避免触发其他点击事件
  e.stopPropagation()
}

// 打开聊天窗口
const openChat = () => {
  isExpanded.value = true
  
}

// 关闭聊天窗口
const closeChat = () => {
  isExpanded.value = false
}

// 清空聊天记录
const clearHistory = () => {
  chatHistory.value = []
}

// 提交消息
const submitMessage = async () => {
  if (!message.value.trim()) return
  
  // 添加到聊天记录
  const userMessage = message.value.trim()
  chatHistory.value.push({
    type: 'user',
    content: userMessage,
    timestamp: new Date().toLocaleTimeString()
  })
  
  message.value = '' // 清空输入框
  isLoading.value = true // 开始加载
  
  try {
    // 调用AI接口
    const response = await aiChatBot({ 
      message: userMessage
      // 可以根据需要添加其他参数
    })

    console.log("hello::",response)
    
    if (response.data.code === "200") {

        console.log("hello")
      // 添加AI回复到聊天记录
      chatHistory.value.push({
        type: 'ai',
        content: response.data.data,
        timestamp: new Date().toLocaleTimeString()
      })
    } else {
      console.error('AI请求失败:', response.message)
      chatHistory.value.push({
        type: 'ai',
        content: '抱歉，我暂时无法处理您的请求，请稍后再试。',
        timestamp: new Date().toLocaleTimeString()
      })
    }
  } catch (error) {
    console.error('请求出错:', error)
    chatHistory.value.push({
      type: 'ai',
      content: '网络连接出现问题，请检查您的网络后重试。',
      timestamp: new Date().toLocaleTimeString()
    })
  } finally {
    isLoading.value = false
    // 确保输入框重新获得焦点
    nextTick(() => {
      textInput.value?.focus()
    })
  }
}

// 处理按钮点击
const handleButtonClick = (type) => {
  switch (type) {
    case 'audio':
      // 音频功能处理
      console.log('音频按钮点击')
      break
    case 'image':
      // 图片功能处理
      console.log('图片按钮点击')
      break
    case 'settings':
      // 设置功能处理
      console.log('设置按钮点击')
      break
  }
}

// 处理标签点击
const handleTagClick = (tag) => {
  switch (tag) {
    case 'image':
      message.value = '请帮我生成一张图片，主题是：'
      break
    case 'data':
      message.value = '请帮我分析以下数据：'
      break
    case 'more':
      // 更多功能处理
      console.log('更多功能点击')
      break
  }
  // 自动聚焦输入框
  nextTick(() => {
    textInput.value?.focus()
  })
}
</script>

<style scoped>
/* 圆形图标样式 */
.chat-icon {
  position: fixed;
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: linear-gradient(to bottom right, #7e7e7e, #363636, #363636);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: grab;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
  transition: all 0.3s ease;
  z-index: 1000;
  user-select: none;
}

.chat-icon:active {
  cursor: grabbing;
  transform: scale(0.95);
}

.chat-icon:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.4);
}

.chat-icon svg {
  width: 24px;
  height: 24px;
  color: white;
}

/* 聊天窗口样式 */
.chat-window {
  position: fixed;
  width: 280px;
  max-height: 70vh;
  display: flex;
  flex-direction: column;
  background: rgba(30, 30, 30, 0.9);
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(8px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  overflow: hidden;
  z-index: 1000;
  cursor: grab;
  user-select: none;
}

.chat-window:active {
  cursor: grabbing;
}

/* 关闭按钮样式 */
.close-btn {
  position: absolute;
  top: 10px;
  right: 10px;
  width: 28px;
  height: 28px;
  background: rgba(255, 255, 255, 0.1);
  border: none;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 1001;
  transition: all 0.2s ease;
}

.close-btn:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: rotate(90deg);
}

.close-btn svg {
  color: rgba(255, 255, 255, 0.8);
  width: 16px;
  height: 16px;
}

/* 聊天记录区域 */
.chat-messages {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
  max-height: calc(70vh - 150px);
  min-height: 100px;
}

/* 自定义滚动条 */
.chat-messages::-webkit-scrollbar {
  width: 6px;
}

.chat-messages::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 3px;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 3px;
}

.chat-messages::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.3);
}

/* 消息样式 */
.message {
  margin-bottom: 12px;
  padding: 10px 14px;
  border-radius: 12px;
  max-width: 85%;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.message.user {
  background: linear-gradient(135deg, #409EFF, #1a73e8);
  margin-left: auto;
  border-bottom-right-radius: 4px;
  color: white;
}

.message.ai {
  background: rgba(60, 60, 60, 0.7);
  margin-right: auto;
  border-bottom-left-radius: 4px;
  color: #f1f1f1;
}

.message-content {
  word-wrap: break-word;
  white-space: pre-wrap;
  line-height: 1.5;
  font-size: 14px;
}

.message-time {
  font-size: 10px;
  opacity: 0.7;
  margin-top: 4px;
  text-align: right;
}

/* 加载动画 */
.loading-dots {
  display: inline-block;
}
.loading-dots::after {
  content: '...';
  animation: dotPulse 1.5s infinite steps(4, end);
}

@keyframes dotPulse {
  0%, 100% { opacity: 0; }
  50% { opacity: 1; }
}

/* 输入区域 */
.container-chat-options {
  padding: 12px;
  background: rgba(40, 40, 40, 0.7);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.chat {
  display: flex;
  flex-direction: column;
}

.chat-bot {
  position: relative;
  display: flex;
}

.chat-bot textarea {
  flex: 1;
  background: rgba(20, 20, 20, 0.8);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  padding: 12px 40px 12px 16px;
  color: white;
  font-family: inherit;
  font-size: 14px;
  resize: none;
  outline: none;
  min-height: 50px;
  max-height: 120px;
  transition: all 0.3s ease;
}

.chat-bot textarea:focus {
  border-color: rgba(64, 158, 255, 0.5);
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.chat-bot textarea::placeholder {
  color: rgba(255, 255, 255, 0.4);
}

/* 选项按钮区域 */
.options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}

.btns-add {
  display: flex;
  gap: 8px;
}

.btns-add button {
  background: transparent;
  border: none;
  color: rgba(255, 255, 255, 0.6);
  padding: 6px;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btns-add button:hover {
  color: white;
  background: rgba(255, 255, 255, 0.1);
  transform: translateY(-2px);
}

.btns-add button:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.btns-add button svg {
  width: 20px;
  height: 20px;
}

/* 提交按钮 */
.btn-submit {
  background: linear-gradient(to right, #409EFF, #1a73e8);
  border: none;
  border-radius: 12px;
  padding: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.btn-submit:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

.btn-submit:active:not(:disabled) {
  transform: translateY(0);
}

.btn-submit:disabled {
  background: rgba(64, 158, 255, 0.3);
  cursor: not-allowed;
}

.btn-submit i {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  backdrop-filter: blur(4px);
}

.btn-submit svg {
  width: 18px;
  height: 18px;
  color: white;
  transition: all 0.3s ease;
}

.btn-submit:not(:disabled):hover svg {
  filter: drop-shadow(0 0 4px rgba(255, 255, 255, 0.6));
}

/* 标签区域 */
.tags {
  display: flex;
  gap: 6px;
  padding: 0 12px 12px;
  flex-wrap: wrap;
}

.tags span {
  padding: 6px 10px;
  background: rgba(60, 60, 60, 0.7);
  border-radius: 8px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.8);
  cursor: pointer;
  transition: all 0.2s ease;
  user-select: none;
}

.tags span:hover {
  background: rgba(80, 80, 80, 0.9);
  color: white;
}

/* 过渡动画 */
.chat-enter-active,
.chat-leave-active {
  transition: all 0.3s ease;
  transform-origin: bottom right;
}

.chat-enter-from,
.chat-leave-to {
  opacity: 0;
  transform: scale(0.9) translateY(10px);
}

/* 响应式设计 */
@media (max-width: 480px) {
  .chat-window {
    width: 90vw;
    max-height: 80vh;
  }
  
  .chat-messages {
    max-height: calc(80vh - 150px);
  }
  
  .message {
    max-width: 90%;
  }
}
</style>