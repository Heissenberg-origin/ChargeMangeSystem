<template>
  <div class="parent-container">
    <!-- 其他内容 -->
    
    <!-- 使用ChatBot组件 -->
    <ChatBot 
      @message-submitted="handleMessage"
      @button-clicked="handleButton"
      @tag-clicked="handleTag"
    />
  </div>
</template>

<script>
import ChatBot from '@/components/ChatBot.vue'

export default {
  components: {
    ChatBot
  },
  methods: {
    handleMessage(message) {
      console.log('用户发送的消息:', message)
      // 在这里处理消息发送逻辑
    },
    handleButton(type) {
      console.log('按钮被点击:', type)
      // 根据不同类型执行不同操作
    },
    handleTag(tag) {
      console.log('标签被点击:', tag)
      // 根据标签执行不同操作
    }
  }
}
</script>