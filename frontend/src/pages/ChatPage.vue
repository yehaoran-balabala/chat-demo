<template>
  <div class="chat-page">
    <div class="chat-container">
      <!-- 聊天头部 -->
      <div class="chat-header">
        <div class="chat-info">
          <div class="ai-avatar">
            <i class="fas fa-heart"></i>
          </div>
          <div class="ai-info">
            <h3>心理健康AI助手</h3>
            <p class="status" :class="{ online: isConnected }">
              {{ isConnected ? '在线' : '连接中...' }}
            </p>
          </div>
        </div>
        <div class="chat-actions">
          <button class="btn-icon" @click="clearChat" title="清空对话">
            <i class="fas fa-trash"></i>
          </button>
          <button class="btn-icon" @click="exportChat" title="导出对话">
            <i class="fas fa-download"></i>
          </button>
        </div>
      </div>

      <!-- 消息列表 -->
      <div class="messages-container" ref="messagesContainer">
        <div class="messages-list">
          <!-- 欢迎消息 -->
          <div v-if="messages.length === 0" class="welcome-message">
            <div class="ai-avatar">
              <i class="fas fa-heart"></i>
            </div>
            <div class="welcome-content">
              <h4>你好！我是您的心理健康AI助手</h4>
              <p>我在这里倾听您的心声，为您提供支持和帮助。请随时与我分享您的想法和感受。</p>
              <div class="suggested-questions">
                <p>您可以这样开始：</p>
                <div class="question-chips">
                  <span
                      v-for="question in suggestedQuestions"
                      :key="question"
                      class="question-chip"
                      @click="sendSuggestedQuestion(question)"
                  >
                    {{ question }}
                  </span>
                </div>
              </div>
            </div>
          </div>

          <!-- 消息列表 -->
          <div
              v-for="(message, index) in messages"
              :key="message.id"
              class="message-item"
              :class="{ 'user-message': message.sender === 'user', 'ai-message': message.sender === 'ai' }"
          >
            <div class="message-avatar">
              <i v-if="message.sender === 'user'" class="fas fa-user"></i>
              <i v-else class="fas fa-heart"></i>
            </div>
            <div class="message-content">
              <div class="message-bubble">
                <p>{{ message.content }}</p>
              </div>
              <div class="message-meta">
                <span class="message-time">{{ formatTime(message.timestamp) }}</span>
                <span v-if="message.sender === 'ai'" class="message-actions">
                  <button @click="copyMessage(message)" title="复制">
                    <i class="fas fa-copy"></i>
                  </button>
                  <button @click="likeMessage(message)" title="有用"
                          :class="{ active: message.liked }">
                    <i class="fas fa-thumbs-up"></i>
                  </button>
                </span>
              </div>
            </div>
          </div>

          <!-- 输入指示器 -->
          <div v-if="isTyping" class="typing-indicator">
            <div class="message-avatar">
              <i class="fas fa-heart"></i>
            </div>
            <div class="typing-content">
              <div class="typing-dots">
                <span></span>
                <span></span>
                <span></span>
              </div>
              <p class="typing-text">AI助手正在思考...</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 输入区域 -->
      <div class="input-area">
        <div class="input-container">
          <div class="input-wrapper">
            <textarea
                v-model="inputMessage"
                ref="textInput"
                placeholder="在这里输入您的想法和感受..."
                @keydown="handleKeyDown"
                @input="adjustTextareaHeight"
                rows="1"
                :disabled="isTyping"
            ></textarea>
            <button
                class="send-button"
                @click="sendMessage"
                :disabled="!canSend"
                :class="{ active: canSend }"
            >
              <i class="fas fa-paper-plane"></i>
            </button>
          </div>
          <div class="input-tips">
            <span class="tip">按 Ctrl+Enter 发送消息</span>
            <span class="word-count">{{ inputMessage.length }}/500</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'

export default {
  name: 'ChatPage',
  setup() {
    // 响应式数据
    const messages = ref([])
    const inputMessage = ref('')
    const isTyping = ref(false)
    const isConnected = ref(true)
    const messagesContainer = ref(null)
    const textInput = ref(null)

    // 建议问题
    const suggestedQuestions = ref([
      '我感到有些焦虑',
      '最近压力很大',
      '心情不太好',
      '需要一些建议'
    ])

    // 计算属性
    const canSend = computed(() => {
      return inputMessage.value.trim().length > 0 && !isTyping.value
    })

    // 方法
    const sendMessage = async () => {
      if (!canSend.value) return

      const userMessage = {
        id: generateMessageId(),
        sender: 'user',
        content: inputMessage.value.trim(),
        timestamp: new Date(),
        liked: false
      }

      // 添加用户消息
      messages.value.push(userMessage)
      const userInput = inputMessage.value.trim()
      inputMessage.value = ''

      // 调整textarea高度
      nextTick(() => {
        adjustTextareaHeight()
      })

      // 滚动到底部
      scrollToBottom()

      // 发送到AI
      await sendToAI(userInput)
    }

    const sendSuggestedQuestion = (question) => {
      inputMessage.value = question
      sendMessage()
    }

    const sendToAI = async (message) => {
      isTyping.value = true

      try {
        // 模拟API调用延迟
        await new Promise(resolve => setTimeout(resolve, 1000 + Math.random() * 2000))

        // 生成AI回复
        const aiResponse = generateAIResponse(message)

        const aiMessage = {
          id: generateMessageId(),
          sender: 'ai',
          content: aiResponse,
          timestamp: new Date(),
          liked: false
        }

        messages.value.push(aiMessage)
        scrollToBottom()

        // 保存对话到本地存储
        saveConversation()

      } catch (error) {
        console.error('发送消息失败:', error)

        const errorMessage = {
          id: generateMessageId(),
          sender: 'ai',
          content: '抱歉，我暂时无法回复您的消息。请稍后再试。',
          timestamp: new Date(),
          liked: false
        }

        messages.value.push(errorMessage)
        scrollToBottom()
      } finally {
        isTyping.value = false
      }
    }

    const generateAIResponse = (userMessage) => {
      // 简单的AI回复生成逻辑
      const responses = [
        '我理解您的感受。能具体告诉我发生了什么吗？',
        '听起来您正在经历一些困难。这种感受是很正常的。',
        '感谢您与我分享这些。您觉得什么时候开始有这种感觉的？',
        '我在这里倾听您的心声。您现在最需要什么样的支持？',
        '这确实是一个值得关注的问题。您之前是如何处理类似情况的？'
      ]

      return responses[Math.floor(Math.random() * responses.length)]
    }

    const generateMessageId = () => {
      return `msg_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`
    }

    const handleKeyDown = (event) => {
      if (event.key === 'Enter' && event.ctrlKey) {
        event.preventDefault()
        sendMessage()
      } else if (event.key === 'Enter' && !event.shiftKey) {
        event.preventDefault()
        sendMessage()
      }
    }

    const adjustTextareaHeight = () => {
      const textarea = textInput.value
      if (textarea) {
        textarea.style.height = 'auto'
        textarea.style.height = Math.min(textarea.scrollHeight, 120) + 'px'
      }
    }

    const scrollToBottom = () => {
      nextTick(() => {
        if (messagesContainer.value) {
          messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
        }
      })
    }

    const formatTime = (timestamp) => {
      return new Date(timestamp).toLocaleTimeString('zh-CN', {
        hour: '2-digit',
        minute: '2-digit'
      })
    }

    const copyMessage = async (message) => {
      try {
        await navigator.clipboard.writeText(message.content)
        window.$emit('notification:show', {
          type: 'success',
          title: '复制成功',
          message: '消息已复制到剪贴板'
        })
      } catch (error) {
        console.error('复制失败:', error)
      }
    }

    const likeMessage = (message) => {
      message.liked = !message.liked
      saveConversation()

      if (message.liked) {
        window.$emit('notification:show', {
          type: 'success',
          title: '谢谢您的反馈',
          message: '您的评价有助于我们改进服务'
        })
      }
    }

    const clearChat = () => {
      window.$emit('modal:open', {
        title: '清空对话',
        content: '确定要清空当前对话吗？此操作无法撤销。',
        type: 'warning',
        onConfirm: () => {
          messages.value = []
          localStorage.removeItem('current_conversation')
          window.$emit('notification:show', {
            type: 'success',
            title: '对话已清空',
            message: '您可以开始新的对话了'
          })
        }
      })
    }

    const exportChat = () => {
      if (messages.value.length === 0) {
        window.$emit('notification:show', {
          type: 'info',
          title: '暂无对话',
          message: '没有可导出的对话记录'
        })
        return
      }

      const chatContent = messages.value.map(msg => {
        const time = formatTime(msg.timestamp)
        const sender = msg.sender === 'user' ? '我' : 'AI助手'
        return `[${time}] ${sender}: ${msg.content}`
      }).join('\n\n')

      const blob = new Blob([chatContent], { type: 'text/plain' })
      const url = URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = `心理健康对话_${new Date().toLocaleDateString()}.txt`
      a.click()
      URL.revokeObjectURL(url)

      window.$emit('notification:show', {
        type: 'success',
        title: '导出成功',
        message: '对话记录已保存到文件'
      })
    }

    const saveConversation = () => {
      const conversation = {
        messages: messages.value,
        timestamp: new Date(),
        id: `conv_${Date.now()}`
      }

      localStorage.setItem('current_conversation', JSON.stringify(conversation))

      // 保存到历史记录
      const history = JSON.parse(localStorage.getItem('chat_history') || '[]')
      history.unshift(conversation)
      localStorage.setItem('chat_history', JSON.stringify(history.slice(0, 50))) // 只保留最近50次对话
    }

    const loadConversation = () => {
      const saved = localStorage.getItem('current_conversation')
      if (saved) {
        try {
          const conversation = JSON.parse(saved)
          messages.value = conversation.messages || []
          nextTick(() => {
            scrollToBottom()
          })
        } catch (error) {
          console.error('加载对话失败:', error)
        }
      }
    }

    // 生命周期
    onMounted(() => {
      loadConversation()

      // 监听页面可见性变化
      document.addEventListener('visibilitychange', () => {
        if (!document.hidden) {
          scrollToBottom()
        }
      })
    })

    onUnmounted(() => {
      saveConversation()
    })

    return {
      messages,
      inputMessage,
      isTyping,
      isConnected,
      messagesContainer,
      textInput,
      suggestedQuestions,
      canSend,
      sendMessage,
      sendSuggestedQuestion,
      handleKeyDown,
      adjustTextareaHeight,
      formatTime,
      copyMessage,
      likeMessage,
      clearChat,
      exportChat
    }
  }
}
</script>

<style scoped>
.chat-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.chat-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  max-width: 800px;
  margin: 0 auto;
  background: white;
  border-radius: 20px 20px 0 0;
  box-shadow: 0 -10px 30px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.chat-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.ai-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.ai-info h3 {
  margin: 0;
  font-size: 18px;
}

.status {
  margin: 5px 0 0 0;
  font-size: 14px;
  opacity: 0.8;
}

.status.online {
  color: #4CAF50;
}

.chat-actions {
  display: flex;
  gap: 10px;
}

.btn-icon {
  width: 40px;
  height: 40px;
  border: none;
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.btn-icon:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: scale(1.1);
}

.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: #f8fafc;
}

.welcome-message {
  display: flex;
  gap: 15px;
  margin-bottom: 30px;
  padding: 20px;
  background: white;
  border-radius: 15px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.welcome-message .ai-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.welcome-content h4 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 18px;
}

.welcome-content p {
  margin: 0 0 20px 0;
  color: #666;
  line-height: 1.6;
}

.suggested-questions p {
  margin: 0 0 10px 0;
  font-weight: 500;
  color: #333;
}

.question-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.question-chip {
  padding: 8px 15px;
  background: #e3f2fd;
  color: #1976d2;
  border-radius: 20px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.question-chip:hover {
  background: #1976d2;
  color: white;
  transform: translateY(-2px);
}

.message-item {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
}

.message-item.user-message {
  flex-direction: row-reverse;
}

.message-item.user-message .message-content {
  align-items: flex-end;
}

.message-item.user-message .message-bubble {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
}

.user-message .message-avatar {
  background: #4CAF50;
  color: white;
}

.ai-message .message-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.message-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.message-bubble {
  background: white;
  padding: 15px;
  border-radius: 15px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  margin-bottom: 5px;
  max-width: 80%;
}

.message-bubble p {
  margin: 0;
  line-height: 1.6;
  word-wrap: break-word;
}

.message-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}

.message-actions {
  display: flex;
  gap: 10px;
}

.message-actions button {
  background: none;
  border: none;
  color: #999;
  cursor: pointer;
  padding: 5px;
  border-radius: 3px;
  transition: all 0.3s ease;
}

.message-actions button:hover {
  color: #667eea;
  background: rgba(102, 126, 234, 0.1);
}

.message-actions button.active {
  color: #4CAF50;
}

.typing-indicator {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
}

.typing-content {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.typing-dots {
  display: flex;
  gap: 5px;
  align-items: center;
}

.typing-dots span {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #667eea;
  animation: typing 1.4s infinite;
}

.typing-dots span:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-dots span:nth-child(3) {
  animation-delay: 0.4s;
}

.typing-text {
  margin: 0;
  font-size: 14px;
  color: #999;
}

@keyframes typing {
  0%, 60%, 100% {
    transform: translateY(0);
    opacity: 0.5;
  }
  30% {
    transform: translateY(-10px);
    opacity: 1;
  }
}

.input-area {
  padding: 20px;
  background: white;
  border-top: 1px solid #e0e0e0;
}

.input-container {
  max-width: 100%;
}

.input-wrapper {
  display: flex;
  align-items: flex-end;
  gap: 10px;
  background: #f8fafc;
  padding: 10px;
  border-radius: 25px;
  border: 2px solid #e0e0e0;
  transition: border-color 0.3s ease;
}

.input-wrapper:focus-within {
  border-color: #667eea;
}

.input-wrapper textarea {
  flex: 1;
  border: none;
  background: none;
  resize: none;
  outline: none;
  font-family: inherit;
  font-size: 16px;
  line-height: 1.5;
  max-height: 120px;
  padding: 5px 10px;
}

.send-button {
  width: 40px;
  height: 40px;
  border: none;
  background: #e0e0e0;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.send-button:disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

.send-button.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  transform: scale(1.1);
}

.input-tips {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
  font-size: 12px;
  color: #999;
}

@media (max-width: 768px) {
  .chat-container {
    border-radius: 0;
    height: 100vh;
  }

  .message-bubble {
    max-width: 90%;
  }

  .question-chips {
    flex-direction: column;
  }

  .input-tips .tip {
    display: none;
  }
}
</style>