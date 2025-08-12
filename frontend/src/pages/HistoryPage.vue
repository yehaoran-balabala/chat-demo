<template>
  <div class="history-page">
    <div class="page-header">
      <div class="header-content">
        <h1>
          <i class="fas fa-history"></i>
          对话历史
        </h1>
        <p>查看您的历史对话记录</p>
      </div>
      <div class="header-actions">
        <button class="btn btn-outline" @click="exportAllHistory">
          <i class="fas fa-download"></i>
          导出全部
        </button>
        <button class="btn btn-danger" @click="clearAllHistory">
          <i class="fas fa-trash"></i>
          清空历史
        </button>
      </div>
    </div>

    <div class="page-content">
      <!-- 搜索栏 -->
      <div class="search-section">
        <div class="search-bar">
          <i class="fas fa-search"></i>
          <input
              type="text"
              v-model="searchQuery"
              placeholder="搜索对话内容..."
              @input="handleSearch"
          >
          <button v-if="searchQuery" @click="clearSearch" class="clear-search">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="filter-options">
          <select v-model="selectedTimeFilter" @change="applyFilters">
            <option value="all">全部时间</option>
            <option value="today">今天</option>
            <option value="week">本周</option>
            <option value="month">本月</option>
            <option value="year">今年</option>
          </select>
          <select v-model="selectedSortOrder" @change="applyFilters">
            <option value="newest">最新优先</option>
            <option value="oldest">最早优先</option>
          </select>
        </div>
      </div>

      <!-- 统计信息 -->
      <div class="stats-section">
        <div class="stat-card">
          <div class="stat-icon">
            <i class="fas fa-comments"></i>
          </div>
          <div class="stat-info">
            <h3>{{ stats.totalConversations }}</h3>
            <p>总对话数</p>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">
            <i class="fas fa-message"></i>
          </div>
          <div class="stat-info">
            <h3>{{ stats.totalMessages }}</h3>
            <p>总消息数</p>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">
            <i class="fas fa-clock"></i>
          </div>
          <div class="stat-info">
            <h3>{{ stats.totalTime }}</h3>
            <p>总对话时长</p>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">
            <i class="fas fa-calendar"></i>
          </div>
          <div class="stat-info">
            <h3>{{ stats.activeDays }}</h3>
            <p>活跃天数</p>
          </div>
        </div>
      </div>

      <!-- 历史记录列表 -->
      <div class="history-section">
        <div v-if="loading" class="loading-container">
          <div class="loading-spinner">
            <i class="fas fa-heart loading-heart"></i>
            <p>加载历史记录中...</p>
          </div>
        </div>

        <div v-else-if="filteredHistories.length === 0" class="empty-state">
          <div class="empty-icon">
            <i class="fas fa-history"></i>
          </div>
          <h3>{{ searchQuery ? '没有找到相关对话' : '暂无历史记录' }}</h3>
          <p>{{ searchQuery ? '尝试使用不同的搜索关键词' : '开始您的第一次对话吧' }}</p>
          <button v-if="!searchQuery" class="btn btn-primary" @click="goToChat">
            开始对话
          </button>
        </div>

        <div v-else class="history-list">
          <div
              v-for="history in paginatedHistories"
              :key="history.id"
              class="history-item"
              @click="viewConversation(history)"
          >
            <div class="history-header">
              <div class="history-info">
                <h4 class="history-title">
                  {{ getConversationTitle(history) }}
                </h4>
                <div class="history-meta">
                  <span class="history-date">
                    <i class="fas fa-calendar-alt"></i>
                    {{ formatDate(history.timestamp) }}
                  </span>
                  <span class="history-count">
                    <i class="fas fa-comment-dots"></i>
                    {{ history.messages.length }} 条消息
                  </span>
                  <span class="history-duration">
                    <i class="fas fa-clock"></i>
                    {{ getConversationDuration(history) }}
                  </span>
                </div>
              </div>
              <div class="history-actions">
                <button
                    @click.stop="toggleFavorite(history)"
                    class="action-btn"
                    :class="{ active: history.favorite }"
                    title="收藏"
                >
                  <i class="fas fa-star"></i>
                </button>
                <button
                    @click.stop="exportConversation(history)"
                    class="action-btn"
                    title="导出"
                >
                  <i class="fas fa-download"></i>
                </button>
                <button
                    @click.stop="deleteConversation(history)"
                    class="action-btn delete"
                    title="删除"
                >
                  <i class="fas fa-trash"></i>
                </button>
              </div>
            </div>

            <div class="history-preview">
              <div class="message-preview">
                <div class="preview-message user-message">
                  <span class="message-label">我:</span>
                  <span class="message-text">{{ getFirstUserMessage(history) }}</span>
                </div>
                <div class="preview-message ai-message">
                  <span class="message-label">AI:</span>
                  <span class="message-text">{{ getFirstAIMessage(history) }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div v-if="totalPages > 1" class="pagination">
          <button
              @click="currentPage--"
              :disabled="currentPage === 1"
              class="pagination-btn"
          >
            <i class="fas fa-chevron-left"></i>
            上一页
          </button>

          <div class="pagination-info">
            第 {{ currentPage }} 页，共 {{ totalPages }} 页
          </div>

          <button
              @click="currentPage++"
              :disabled="currentPage === totalPages"
              class="pagination-btn"
          >
            下一页
            <i class="fas fa-chevron-right"></i>
          </button>
        </div>
      </div>
    </div>

    <!-- 对话详情模态框 -->
    <div v-if="selectedConversation" class="modal-overlay" @click="closeConversationModal">
      <div class="conversation-modal" @click.stop>
        <div class="modal-header">
          <h3>{{ getConversationTitle(selectedConversation) }}</h3>
          <button @click="closeConversationModal" class="modal-close">
            <i class="fas fa-times"></i>
          </button>
        </div>

        <div class="modal-body">
          <div class="conversation-details">
            <div class="detail-item">
              <strong>对话时间:</strong> {{ formatDetailDate(selectedConversation.timestamp) }}
            </div>
            <div class="detail-item">
              <strong>消息数量:</strong> {{ selectedConversation.messages.length }} 条
            </div>
            <div class="detail-item">
              <strong>对话时长:</strong> {{ getConversationDuration(selectedConversation) }}
            </div>
          </div>

          <div class="conversation-messages">
            <div
                v-for="message in selectedConversation.messages"
                :key="message.id"
                class="modal-message"
                :class="{ 'user-message': message.sender === 'user' }"
            >
              <div class="message-header">
                <span class="message-sender">
                  {{ message.sender === 'user' ? '我' : 'AI助手' }}
                </span>
                <span class="message-time">
                  {{ formatTime(message.timestamp) }}
                </span>
              </div>
              <div class="message-content">
                {{ message.content }}
              </div>
            </div>
          </div>
        </div>

        <div class="modal-footer">
          <button class="btn btn-outline" @click="exportConversation(selectedConversation)">
            <i class="fas fa-download"></i>
            导出对话
          </button>
          <button class="btn btn-primary" @click="continueConversation">
            <i class="fas fa-comment"></i>
            继续对话
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'

export default {
  name: 'HistoryPage',
  emits: ['page-change'],
  setup(props, { emit }) {
    // 响应式数据
    const loading = ref(true)
    const histories = ref([])
    const searchQuery = ref('')
    const selectedTimeFilter = ref('all')
    const selectedSortOrder = ref('newest')
    const currentPage = ref(1)
    const pageSize = ref(10)
    const selectedConversation = ref(null)

    // 计算属性
    const stats = computed(() => {
      const totalConversations = histories.value.length
      const totalMessages = histories.value.reduce((sum, h) => sum + h.messages.length, 0)

      // 计算总对话时长（估算）
      const totalMinutes = histories.value.reduce((sum, h) => {
        return sum + Math.max(1, Math.floor(h.messages.length / 2))
      }, 0)

      // 计算活跃天数
      const uniqueDates = new Set(
          histories.value.map(h => new Date(h.timestamp).toDateString())
      )

      return {
        totalConversations,
        totalMessages,
        totalTime: formatDuration(totalMinutes),
        activeDays: uniqueDates.size
      }
    })

    const filteredHistories = computed(() => {
      let filtered = [...histories.value]

      // 搜索过滤
      if (searchQuery.value) {
        const query = searchQuery.value.toLowerCase()
        filtered = filtered.filter(history => {
          return history.messages.some(message =>
              message.content.toLowerCase().includes(query)
          )
        })
      }

      // 时间过滤
      if (selectedTimeFilter.value !== 'all') {
        const now = new Date()
        const filterDate = new Date()

        switch (selectedTimeFilter.value) {
          case 'today':
            filterDate.setHours(0, 0, 0, 0)
            break
          case 'week':
            filterDate.setDate(now.getDate() - 7)
            break
          case 'month':
            filterDate.setMonth(now.getMonth() - 1)
            break
          case 'year':
            filterDate.setFullYear(now.getFullYear() - 1)
            break
        }

        filtered = filtered.filter(history =>
            new Date(history.timestamp) >= filterDate
        )
      }

      // 排序
      filtered.sort((a, b) => {
        const dateA = new Date(a.timestamp)
        const dateB = new Date(b.timestamp)

        if (selectedSortOrder.value === 'newest') {
          return dateB - dateA
        } else {
          return dateA - dateB
        }
      })

      return filtered
    })

    const totalPages = computed(() => {
      return Math.ceil(filteredHistories.value.length / pageSize.value)
    })

    const paginatedHistories = computed(() => {
      const start = (currentPage.value - 1) * pageSize.value
      const end = start + pageSize.value
      return filteredHistories.value.slice(start, end)
    })

    // 方法
    const loadHistory = async () => {
      loading.value = true

      try {
        // 从本地存储加载历史记录
        const saved = localStorage.getItem('chat_history')
        if (saved) {
          histories.value = JSON.parse(saved)
        }

        // 模拟加载延迟
        await new Promise(resolve => setTimeout(resolve, 500))

      } catch (error) {
        console.error('加载历史记录失败:', error)
        window.$emit('notification:show', {
          type: 'error',
          title: '加载失败',
          message: '无法加载历史记录'
        })
      } finally {
        loading.value = false
      }
    }

    const handleSearch = () => {
      currentPage.value = 1
    }

    const clearSearch = () => {
      searchQuery.value = ''
      currentPage.value = 1
    }

    const applyFilters = () => {
      currentPage.value = 1
    }

    const getConversationTitle = (history) => {
      if (history.title) return history.title

      const firstUserMessage = history.messages.find(m => m.sender === 'user')
      if (firstUserMessage) {
        const title = firstUserMessage.content.slice(0, 30)
        return title + (firstUserMessage.content.length > 30 ? '...' : '')
      }

      return `对话 - ${formatDate(history.timestamp)}`
    }

    const getFirstUserMessage = (history) => {
      const userMessage = history.messages.find(m => m.sender === 'user')
      return userMessage ? truncateText(userMessage.content, 60) : '...'
    }

    const getFirstAIMessage = (history) => {
      const aiMessage = history.messages.find(m => m.sender === 'ai')
      return aiMessage ? truncateText(aiMessage.content, 80) : '...'
    }

    const getConversationDuration = (history) => {
      if (history.messages.length < 2) return '< 1分钟'

      const firstMsg = history.messages[0]
      const lastMsg = history.messages[history.messages.length - 1]

      const duration = new Date(lastMsg.timestamp) - new Date(firstMsg.timestamp)
      const minutes = Math.floor(duration / (1000 * 60))

      if (minutes < 1) return '< 1分钟'
      if (minutes < 60) return `${minutes}分钟`

      const hours = Math.floor(minutes / 60)
      const remainingMinutes = minutes % 60

      return `${hours}小时${remainingMinutes > 0 ? remainingMinutes + '分钟' : ''}`
    }

    const formatDate = (timestamp) => {
      const date = new Date(timestamp)
      const now = new Date()
      const diffDays = Math.floor((now - date) / (1000 * 60 * 60 * 24))

      if (diffDays === 0) return '今天'
      if (diffDays === 1) return '昨天'
      if (diffDays < 7) return `${diffDays}天前`

      return date.toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
      })
    }

    const formatDetailDate = (timestamp) => {
      return new Date(timestamp).toLocaleString('zh-CN', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      })
    }

    const formatTime = (timestamp) => {
      return new Date(timestamp).toLocaleTimeString('zh-CN', {
        hour: '2-digit',
        minute: '2-digit'
      })
    }

    const formatDuration = (minutes) => {
      if (minutes < 60) return `${minutes}分钟`

      const hours = Math.floor(minutes / 60)
      const remainingMinutes = minutes % 60

      if (hours < 24) {
        return `${hours}小时${remainingMinutes > 0 ? remainingMinutes + '分钟' : ''}`
      }

      const days = Math.floor(hours / 24)
      const remainingHours = hours % 24

      return `${days}天${remainingHours > 0 ? remainingHours + '小时' : ''}`
    }

    const truncateText = (text, maxLength) => {
      return text.length > maxLength ? text.slice(0, maxLength) + '...' : text
    }

    const viewConversation = (history) => {
      selectedConversation.value = history
    }

    const closeConversationModal = () => {
      selectedConversation.value = null
    }

    const continueConversation = () => {
      // 将选中的对话设置为当前对话
      localStorage.setItem('current_conversation', JSON.stringify(selectedConversation.value))
      closeConversationModal()
      emit('page-change', 'chat')
    }

    const toggleFavorite = (history) => {
      history.favorite = !history.favorite
      saveHistories()

      const message = history.favorite ? '已添加到收藏' : '已取消收藏'
      window.$emit('notification:show', {
        type: 'success',
        title: '操作成功',
        message
      })
    }

    const exportConversation = (history) => {
      const content = history.messages.map(msg => {
        const time = formatTime(msg.timestamp)
        const sender = msg.sender === 'user' ? '我' : 'AI助手'
        return `[${time}] ${sender}: ${msg.content}`
      }).join('\n\n')

      const title = getConversationTitle(history)
      const date = formatDate(history.timestamp)

      const blob = new Blob([content], { type: 'text/plain' })
      const url = URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = `${title}_${date}.txt`
      a.click()
      URL.revokeObjectURL(url)

      window.$emit('notification:show', {
        type: 'success',
        title: '导出成功',
        message: '对话记录已保存到文件'
      })
    }

    const exportAllHistory = () => {
      if (histories.value.length === 0) {
        window.$emit('notification:show', {
          type: 'info',
          title: '暂无数据',
          message: '没有可导出的历史记录'
        })
        return
      }

      const allContent = histories.value.map(history => {
        const title = getConversationTitle(history)
        const date = formatDetailDate(history.timestamp)
        const messages = history.messages.map(msg => {
          const time = formatTime(msg.timestamp)
          const sender = msg.sender === 'user' ? '我' : 'AI助手'
          return `[${time}] ${sender}: ${msg.content}`
        }).join('\n')

        return `=== ${title} (${date}) ===\n${messages}\n`
      }).join('\n\n')

      const blob = new Blob([allContent], { type: 'text/plain' })
      const url = URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = `心理健康对话历史_${new Date().toLocaleDateString()}.txt`
      a.click()
      URL.revokeObjectURL(url)

      window.$emit('notification:show', {
        type: 'success',
        title: '导出成功',
        message: '所有历史记录已保存到文件'
      })
    }

    const deleteConversation = (history) => {
      window.$emit('modal:open', {
        title: '删除对话',
        content: '确定要删除这个对话吗？此操作无法撤销。',
        type: 'warning',
        onConfirm: () => {
          const index = histories.value.findIndex(h => h.id === history.id)
          if (index > -1) {
            histories.value.splice(index, 1)
            saveHistories()

            window.$emit('notification:show', {
              type: 'success',
              title: '删除成功',
              message: '对话已删除'
            })
          }
        }
      })
    }

    const clearAllHistory = () => {
      if (histories.value.length === 0) {
        window.$emit('notification:show', {
          type: 'info',
          title: '暂无数据',
          message: '没有可清空的历史记录'
        })
        return
      }

      window.$emit('modal:open', {
        title: '清空历史记录',
        content: '确定要清空所有历史记录吗？此操作无法撤销。',
        type: 'warning',
        onConfirm: () => {
          histories.value = []
          saveHistories()

          window.$emit('notification:show', {
            type: 'success',
            title: '清空成功',
            message: '所有历史记录已清空'
          })
        }
      })
    }

    const saveHistories = () => {
      localStorage.setItem('chat_history', JSON.stringify(histories.value))
    }

    const goToChat = () => {
      emit('page-change', 'chat')
    }

    // 生命周期
    onMounted(() => {
      loadHistory()
    })

    return {
      loading,
      histories,
      searchQuery,
      selectedTimeFilter,
      selectedSortOrder,
      currentPage,
      selectedConversation,
      stats,
      filteredHistories,
      totalPages,
      paginatedHistories,
      handleSearch,
      clearSearch,
      applyFilters,
      getConversationTitle,
      getFirstUserMessage,
      getFirstAIMessage,
      getConversationDuration,
      formatDate,
      formatDetailDate,
      formatTime,
      viewConversation,
      closeConversationModal,
      continueConversation,
      toggleFavorite,
      exportConversation,
      exportAllHistory,
      deleteConversation,
      clearAllHistory,
      goToChat
    }
  }
}
</script>

<style scoped>
.history-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 30px;
  background: white;
  padding: 30px;
  border-radius: 15px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.1);
}

.header-content h1 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 32px;
  display: flex;
  align-items: center;
  gap: 15px;
}

.header-content h1 i {
  color: #667eea;
}

.header-content p {
  margin: 0;
  color: #666;
  font-size: 16px;
}

.header-actions {
  display: flex;
  gap: 15px;
}

.btn {
  padding: 12px 24px;
  border: none;
  border-radius: 25px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
  text-decoration: none;
}

.btn-outline {
  background: white;
  color: #667eea;
  border: 2px solid #667eea;
}

.btn-outline:hover {
  background: #667eea;
  color: white;
  transform: translateY(-2px);
}

.btn-danger {
  background: #ff5252;
  color: white;
}

.btn-danger:hover {
  background: #f44336;
  transform: translateY(-2px);
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 20px rgba(102, 126, 234, 0.4);
}

.page-content {
  max-width: 1200px;
  margin: 0 auto;
}

.search-section {
  background: white;
  padding: 25px;
  border-radius: 15px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.1);
  margin-bottom: 30px;
}

.search-bar {
  position: relative;
  margin-bottom: 20px;
}

.search-bar i {
  position: absolute;
  left: 15px;
  top: 50%;
  transform: translateY(-50%);
  color: #999;
}

.search-bar input {
  width: 100%;
  padding: 15px 15px 15px 45px;
  border: 2px solid #e0e0e0;
  border-radius: 25px;
  font-size: 16px;
  outline: none;
  transition: border-color 0.3s ease;
}

.search-bar input:focus {
  border-color: #667eea;
}

.clear-search {
  position: absolute;
  right: 15px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: #999;
  cursor: pointer;
  padding: 5px;
  border-radius: 50%;
  transition: all 0.3s ease;
}

.clear-search:hover {
  color: #667eea;
  background: rgba(102, 126, 234, 0.1);
}

.filter-options {
  display: flex;
  gap: 15px;
}

.filter-options select {
  padding: 10px 15px;
  border: 2px solid #e0e0e0;
  border-radius: 20px;
  background: white;
  cursor: pointer;
  outline: none;
  transition: border-color 0.3s ease;
}

.filter-options select:focus {
  border-color: #667eea;
}

.stats-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: white;
  padding: 25px;
  border-radius: 15px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  gap: 20px;
  transition: transform 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
}

.stat-info h3 {
  margin: 0 0 5px 0;
  font-size: 32px;
  color: #333;
  font-weight: bold;
}

.stat-info p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.history-section {
  background: white;
  border-radius: 15px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.loading-container,
.empty-state {
  padding: 60px 20px;
  text-align: center;
}

.loading-spinner {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.loading-heart {
  font-size: 48px;
  color: #667eea;
  animation: pulse 1.5s infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.1); }
}

.empty-icon {
  font-size: 64px;
  color: #ccc;
  margin-bottom: 20px;
}

.empty-state h3 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 24px;
}

.empty-state p {
  margin: 0 0 30px 0;
  color: #666;
  font-size: 16px;
}

.history-list {
  padding: 20px;
}

.history-item {
  padding: 25px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: all 0.3s ease;
}

.history-item:hover {
  background: #f8fafc;
}

.history-item:last-child {
  border-bottom: none;
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.history-title {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 18px;
  font-weight: 600;
}

.history-meta {
  display: flex;
  gap: 20px;
  font-size: 14px;
  color: #999;
}

.history-meta span {
  display: flex;
  align-items: center;
  gap: 5px;
}

.history-actions {
  display: flex;
  gap: 10px;
}

.action-btn {
  width: 35px;
  height: 35px;
  border: none;
  background: #f0f0f0;
  color: #666;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.action-btn:hover {
  background: #e0e0e0;
  transform: scale(1.1);
}

.action-btn.active {
  background: #ffd700;
  color: #f57c00;
}

.action-btn.delete:hover {
  background: #ff5252;
  color: white;
}

.history-preview {
  padding: 15px;
  background: #f8fafc;
  border-radius: 10px;
}

.message-preview {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.preview-message {
  display: flex;
  gap: 10px;
  align-items: flex-start;
}

.message-label {
  font-weight: 600;
  min-width: 30px;
}

.user-message .message-label {
  color: #4CAF50;
}

.ai-message .message-label {
  color: #667eea;
}

.message-text {
  flex: 1;
  color: #333;
  line-height: 1.4;
}

.pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-top: 1px solid #f0f0f0;
}

.pagination-btn {
  padding: 10px 20px;
  border: 2px solid #667eea;
  background: white;
  color: #667eea;
  border-radius: 20px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
}

.pagination-btn:hover:not(:disabled) {
  background: #667eea;
  color: white;
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination-info {
  color: #666;
  font-size: 14px;
}

/* 模态框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
}

.conversation-modal {
  background: white;
  border-radius: 15px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  max-width: 800px;
  max-height: 80vh;
  width: 100%;
  display: flex;
  flex-direction: column;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 25px;
  border-bottom: 1px solid #f0f0f0;
}

.modal-header h3 {
  margin: 0;
  color: #333;
  font-size: 20px;
}

.modal-close {
  width: 30px;
  height: 30px;
  border: none;
  background: none;
  color: #999;
  cursor: pointer;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.modal-close:hover {
  background: #f0f0f0;
  color: #333;
}

.modal-body {
  flex: 1;
  overflow-y: auto;
  padding: 25px;
}

.conversation-details {
  margin-bottom: 25px;
  padding: 20px;
  background: #f8fafc;
  border-radius: 10px;
}

.detail-item {
  margin-bottom: 10px;
  font-size: 14px;
  color: #333;
}

.detail-item:last-child {
  margin-bottom: 0;
}

.conversation-messages {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.modal-message {
  padding: 15px;
  background: white;
  border: 1px solid #e0e0e0;
  border-radius: 10px;
}

.modal-message.user-message {
  background: #e3f2fd;
  border-color: #2196f3;
  margin-left: 40px;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  font-size: 12px;
}

.message-sender {
  font-weight: 600;
  color: #333;
}

.message-time {
  color: #999;
}

.message-content {
  color: #333;
  line-height: 1.6;
  white-space: pre-wrap;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
  padding: 20px 25px;
  border-top: 1px solid #f0f0f0;
}

@media (max-width: 768px) {
  .history-page {
    padding: 10px;
  }

  .page-header {
    flex-direction: column;
    gap: 20px;
    align-items: flex-start;
  }

  .header-actions {
    width: 100%;
    justify-content: space-between;
  }

  .stats-section {
    grid-template-columns: repeat(2, 1fr);
  }

  .filter-options {
    flex-direction: column;
    gap: 10px;
  }

  .history-header {
    flex-direction: column;
    gap: 10px;
  }

  .history-meta {
    flex-wrap: wrap;
    gap: 10px;
  }

  .pagination {
    flex-direction: column;
    gap: 15px;
  }

  .conversation-modal {
    margin: 10px;
    max-height: calc(100vh - 20px);
  }

  .modal-message.user-message {
    margin-left: 20px;
  }
}
</style>