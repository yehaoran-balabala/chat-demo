<template>
  <div id="app">
    <!-- 加载动画 -->
    <div v-if="loading" class="app-loading">
      <div class="loading-spinner">
        <i class="fas fa-heart loading-heart"></i>
        <p>心理健康AI助手正在启动...</p>
      </div>
    </div>

    <!-- 主应用 -->
    <div v-else class="app-container">
      <!-- 头部导航 -->
      <AppHeader
          :current-page="currentPage"
          @page-change="changePage"
      />

      <!-- 主内容区 -->
      <main class="main-content">
        <!-- 路由视图 -->
        <component
            :is="currentPageComponent"
            :key="currentPage"
            @page-change="changePage"
        />
      </main>

      <!-- 底部信息 -->
      <footer class="app-footer">
        <p>&copy; 2025 心理健康AI助手 - 陪伴您的心理健康之旅</p>
      </footer>
    </div>

    <!-- 通知系统 -->
    <div class="notifications-container">
      <TransitionGroup name="notification" tag="div">
        <div
            v-for="notification in notifications"
            :key="notification.id"
            :class="['notification', notification.type]"
        >
          <div class="notification-header">
            <div :class="['notification-icon', notification.type]">
              <i :class="{
                'fas fa-check-circle': notification.type === 'success',
                'fas fa-exclamation-triangle': notification.type === 'warning',
                'fas fa-times-circle': notification.type === 'error',
                'fas fa-info-circle': notification.type === 'info'
              }"></i>
            </div>
            <h4 class="notification-title">{{ notification.title }}</h4>
            <button
                class="notification-close"
                @click="hideNotification(notification.id)"
            >
              <i class="fas fa-times"></i>
            </button>
          </div>
          <div class="notification-content">
            {{ notification.message }}
          </div>
        </div>
      </TransitionGroup>
    </div>

    <!-- 模态框系统 -->
    <div v-if="modals.length > 0" class="modals-container">
      <div
          v-for="modal in modals"
          :key="modal.id"
          class="modal-overlay"
          @click="handleModalAction(modal, 'cancel')"
      >
        <div class="modal-content" @click.stop>
          <div class="modal-header">
            <h3 class="modal-title">{{ modal.title }}</h3>
            <button
                class="modal-close"
                @click="handleModalAction(modal, 'cancel')"
            >
              <i class="fas fa-times"></i>
            </button>
          </div>

          <div class="modal-body" v-html="modal.content"></div>

          <div class="modal-footer">
            <button
                v-if="modal.showCancel"
                class="btn btn-secondary"
                @click="handleModalAction(modal, 'cancel')"
            >
              {{ modal.cancelText }}
            </button>
            <button
                v-if="modal.showConfirm"
                :class="['btn', modal.type === 'warning' || modal.type === 'error' ? 'btn-danger' : 'btn-primary']"
                @click="handleModalAction(modal, 'confirm')"
            >
              {{ modal.confirmText }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 网络状态指示器 -->
    <div v-if="!isOnline" class="network-status offline">
      <i class="fas fa-wifi-slash"></i>
      网络连接已断开
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, onUnmounted } from 'vue'

// 导入组件
import AppHeader from './components/AppHeader.vue'
import HomePage from './pages/HomePage.vue'
import ChatPage from './pages/ChatPage.vue'
import HistoryPage from './pages/HistoryPage.vue'
import ProfilePage from './pages/ProfilePage.vue'
import HelpPage from './pages/HelpPage.vue'

export default {
  name: 'App',
  components: {
    AppHeader,
    HomePage,
    ChatPage,
    HistoryPage,
    ProfilePage,
    HelpPage
  },
  setup() {
    // 响应式数据
    const loading = ref(true)
    const currentPage = ref('home')
    const appReady = ref(false)
    const networkStatus = ref(navigator.onLine)
    const notifications = ref([])
    const modals = ref([])

    // 计算属性
    const currentPageComponent = computed(() => {
      const componentMap = {
        'home': 'HomePage',
        'chat': 'ChatPage',
        'history': 'HistoryPage',
        'profile': 'ProfilePage',
        'help': 'HelpPage'
      }

      return componentMap[currentPage.value] || 'HomePage'
    })

    const isOnline = computed(() => networkStatus.value)

    // 方法
    const changePage = (page) => {
      if (page === currentPage.value) return

      currentPage.value = page
      window.router.push(page)
    }

    const initializeApp = async () => {
      try {
        console.log('正在初始化应用...')

        // 模拟加载时间
        await new Promise(resolve => setTimeout(resolve, 1000))

        // 初始化用户数据
        await initializeUserData()

        // 初始化主题
        initializeTheme()

        // 初始化通知系统
        initializeNotifications()

        // 初始化模态框系统
        initializeModals()

        // 设置路由
        const initialRoute = window.router.initFromHash()
        currentPage.value = initialRoute

        // 设置网络状态监听
        setupNetworkListeners()

        // 设置全局错误处理
        setupGlobalErrorHandling()

        // 应用初始化完成
        appReady.value = true
        loading.value = false

        console.log('应用初始化完成')

        // 触发应用就绪事件
        window.$emit('app:ready')

      } catch (error) {
        console.error('应用初始化失败:', error)
        loading.value = false

        // 显示错误通知
        showNotification({
          type: 'error',
          title: '初始化失败',
          message: '应用启动时发生错误，请刷新页面重试'
        })
      }
    }

    const initializeUserData = async () => {
      // 检查并初始化用户数据
      const hasUserData = window.$getLocal('user_profile')
      if (!hasUserData) {
        const defaultProfile = {
          id: window.utils.generateId('user'),
          name: '',
          email: '',
          createdAt: new Date().toISOString(),
          lastLoginAt: new Date().toISOString()
        }

        window.$setLocal('user_profile', defaultProfile)
      } else {
        const profile = window.$getLocal('user_profile')
        profile.lastLoginAt = new Date().toISOString()
        window.$setLocal('user_profile', profile)
      }

      // 初始化应用设置
      const defaultSettings = {
        theme: 'light',
        language: 'zh-CN',
        autoSave: true,
        notifications: true
      }

      const currentSettings = window.$getLocal('app_settings', defaultSettings)
      window.$setLocal('app_settings', { ...defaultSettings, ...currentSettings })
    }

    const initializeTheme = () => {
      const settings = window.$getLocal('app_settings')
      const theme = settings.theme || 'light'

      document.documentElement.setAttribute('data-theme', theme)

      if (theme === 'auto') {
        const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)')
        const updateTheme = (e) => {
          document.documentElement.setAttribute('data-theme', e.matches ? 'dark' : 'light')
        }

        updateTheme(mediaQuery)
        mediaQuery.addEventListener('change', updateTheme)
      }
    }

    const initializeNotifications = () => {
      window.$on(window.EVENT_NAMES.NOTIFICATION_SHOW, (notification) => {
        showNotification(notification)
      })

      window.$on(window.EVENT_NAMES.NOTIFICATION_HIDE, (notificationId) => {
        hideNotification(notificationId)
      })
    }

    const initializeModals = () => {
      window.$on(window.EVENT_NAMES.MODAL_OPEN, (modal) => {
        showModal(modal)
      })

      window.$on(window.EVENT_NAMES.MODAL_CLOSE, (modalId) => {
        hideModal(modalId)
      })
    }

    const setupNetworkListeners = () => {
      const updateNetworkStatus = () => {
        networkStatus.value = navigator.onLine
      }

      window.addEventListener('online', updateNetworkStatus)
      window.addEventListener('offline', updateNetworkStatus)
    }

    const setupGlobalErrorHandling = () => {
      window.$on(window.EVENT_NAMES.ERROR_GLOBAL, (errorInfo) => {
        console.error('全局错误:', errorInfo)

        showNotification({
          type: 'error',
          title: '系统错误',
          message: '发生了一个意外错误，我们正在处理中',
          duration: 5000
        })
      })

      window.$on(window.EVENT_NAMES.ERROR_API, (errorInfo) => {
        console.error('API错误:', errorInfo)

        if (!networkStatus.value) {
          showNotification({
            type: 'warning',
            title: '网络连接',
            message: '网络连接断开，请检查您的网络设置',
            duration: 5000
          })
        }
      })
    }

    const showNotification = (notification) => {
      const id = notification.id || window.utils.generateId('notification')
      const newNotification = {
        id,
        type: notification.type || 'info',
        title: notification.title || '通知',
        message: notification.message || '',
        duration: notification.duration || 3000,
        timestamp: Date.now()
      }

      notifications.value.push(newNotification)

      if (newNotification.duration > 0) {
        setTimeout(() => {
          hideNotification(id)
        }, newNotification.duration)
      }
    }

    const hideNotification = (notificationId) => {
      const index = notifications.value.findIndex(n => n.id === notificationId)
      if (index > -1) {
        notifications.value.splice(index, 1)
      }
    }

    const showModal = (modal) => {
      const id = modal.id || window.utils.generateId('modal')
      const newModal = {
        id,
        title: modal.title || '提示',
        content: modal.content || '',
        type: modal.type || 'info',
        showCancel: modal.showCancel !== false,
        showConfirm: modal.showConfirm !== false,
        cancelText: modal.cancelText || '取消',
        confirmText: modal.confirmText || '确定',
        onCancel: modal.onCancel,
        onConfirm: modal.onConfirm
      }

      modals.value.push(newModal)
    }

    const hideModal = (modalId) => {
      const index = modals.value.findIndex(m => m.id === modalId)
      if (index > -1) {
        modals.value.splice(index, 1)
      }
    }

    const handleModalAction = (modal, action) => {
      if (action === 'cancel' && modal.onCancel) {
        modal.onCancel()
      } else if (action === 'confirm' && modal.onConfirm) {
        modal.onConfirm()
      }

      hideModal(modal.id)
    }

    // 生命周期
    onMounted(() => {
      initializeApp()

      window.$on(window.EVENT_NAMES.PAGE_CHANGE, (page) => {
        currentPage.value = page
      })

      document.addEventListener('visibilitychange', () => {
        if (!document.hidden) {
          networkStatus.value = navigator.onLine
        }
      })
    })

    onUnmounted(() => {
      window.removeEventListener('online', () => {})
      window.removeEventListener('offline', () => {})
    })

    return {
      loading,
      currentPage,
      appReady,
      networkStatus,
      notifications,
      modals,
      currentPageComponent,
      isOnline,
      changePage,
      hideNotification,
      handleModalAction
    }
  }
}
</script>

<style scoped>
/* 组件特定样式 */
</style>