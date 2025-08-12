<template>
  <div class="profile-page">
    <div class="page-header">
      <div class="header-content">
        <h1>
          <i class="fas fa-user-circle"></i>
          个人资料
        </h1>
        <p>管理您的账户信息和偏好设置</p>
      </div>
    </div>

    <div class="page-content">
      <div class="profile-container">
        <!-- 用户信息卡片 -->
        <div class="profile-card">
          <div class="profile-avatar-section">
            <div class="avatar-container">
              <div class="avatar" :style="{ backgroundColor: profile.avatarColor }">
                <i v-if="!profile.avatar" class="fas fa-user avatar-icon"></i>
                <img v-else :src="profile.avatar" alt="头像" class="avatar-image">
              </div>
              <button class="avatar-edit-btn" @click="showAvatarEditor = true">
                <i class="fas fa-camera"></i>
              </button>
            </div>
            <div class="user-info">
              <h2>{{ profile.name || '未设置昵称' }}</h2>
              <p class="user-id">ID: {{ profile.id }}</p>
              <div class="user-stats">
                <div class="stat-item">
                  <span class="stat-number">{{ userStats.totalDays }}</span>
                  <span class="stat-label">活跃天数</span>
                </div>
                <div class="stat-item">
                  <span class="stat-number">{{ userStats.totalConversations }}</span>
                  <span class="stat-label">总对话数</span>
                </div>
                <div class="stat-item">
                  <span class="stat-number">{{ userStats.totalMessages }}</span>
                  <span class="stat-label">总消息数</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 设置表单 -->
        <div class="settings-section">
          <div class="settings-card">
            <div class="card-header">
              <h3>
                <i class="fas fa-user-edit"></i>
                基本信息
              </h3>
            </div>
            <div class="card-content">
              <form @submit.prevent="saveProfile">
                <div class="form-group">
                  <label for="name">昵称</label>
                  <input
                      type="text"
                      id="name"
                      v-model="profile.name"
                      placeholder="请输入您的昵称"
                      maxlength="20"
                  >
                  <small class="form-hint">{{ profile.name.length }}/20</small>
                </div>

                <div class="form-group">
                  <label for="email">邮箱</label>
                  <input
                      type="email"
                      id="email"
                      v-model="profile.email"
                      placeholder="请输入您的邮箱地址"
                  >
                </div>

                <div class="form-group">
                  <label for="bio">个人简介</label>
                  <textarea
                      id="bio"
                      v-model="profile.bio"
                      placeholder="简单介绍一下自己..."
                      rows="3"
                      maxlength="200"
                  ></textarea>
                  <small class="form-hint">{{ (profile.bio || '').length }}/200</small>
                </div>

                <div class="form-actions">
                  <button type="submit" class="btn btn-primary" :disabled="saving">
                    <i v-if="saving" class="fas fa-spinner fa-spin"></i>
                    <i v-else class="fas fa-save"></i>
                    {{ saving ? '保存中...' : '保存信息' }}
                  </button>
                </div>
              </form>
            </div>
          </div>

          <!-- 应用设置 -->
          <div class="settings-card">
            <div class="card-header">
              <h3>
                <i class="fas fa-cogs"></i>
                应用设置
              </h3>
            </div>
            <div class="card-content">
              <div class="setting-item">
                <div class="setting-info">
                  <h4>主题模式</h4>
                  <p>选择您喜欢的界面主题</p>
                </div>
                <div class="setting-control">
                  <select v-model="settings.theme" @change="updateTheme">
                    <option value="light">浅色模式</option>
                    <option value="dark">深色模式</option>
                    <option value="auto">跟随系统</option>
                  </select>
                </div>
              </div>

              <div class="setting-item">
                <div class="setting-info">
                  <h4>语言设置</h4>
                  <p>选择界面显示语言</p>
                </div>
                <div class="setting-control">
                  <select v-model="settings.language" @change="updateLanguage">
                    <option value="zh-CN">简体中文</option>
                    <option value="zh-TW">繁体中文</option>
                    <option value="en">English</option>
                  </select>
                </div>
              </div>

              <div class="setting-item">
                <div class="setting-info">
                  <h4>自动保存对话</h4>
                  <p>自动保存您的对话记录到历史中</p>
                </div>
                <div class="setting-control">
                  <label class="switch">
                    <input
                        type="checkbox"
                        v-model="settings.autoSave"
                        @change="updateSettings"
                    >
                    <span class="slider"></span>
                  </label>
                </div>
              </div>

              <div class="setting-item">
                <div class="setting-info">
                  <h4>通知提醒</h4>
                  <p>接收系统通知和消息提醒</p>
                </div>
                <div class="setting-control">
                  <label class="switch">
                    <input
                        type="checkbox"
                        v-model="settings.notifications"
                        @change="updateSettings"
                    >
                    <span class="slider"></span>
                  </label>
                </div>
              </div>

              <div class="setting-item">
                <div class="setting-info">
                  <h4>发送方式</h4>
                  <p>选择消息发送的快捷键</p>
                </div>
                <div class="setting-control">
                  <select v-model="settings.sendKey" @change="updateSettings">
                    <option value="enter">Enter 发送</option>
                    <option value="ctrl-enter">Ctrl + Enter 发送</option>
                  </select>
                </div>
              </div>
            </div>
          </div>

          <!-- 数据管理 -->
          <div class="settings-card">
            <div class="card-header">
              <h3>
                <i class="fas fa-database"></i>
                数据管理
              </h3>
            </div>
            <div class="card-content">
              <div class="data-actions">
                <button class="action-btn export" @click="exportUserData">
                  <i class="fas fa-download"></i>
                  <div class="action-info">
                    <h4>导出数据</h4>
                    <p>导出您的所有用户数据</p>
                  </div>
                </button>

                <button class="action-btn import" @click="importUserData">
                  <i class="fas fa-upload"></i>
                  <div class="action-info">
                    <h4>导入数据</h4>
                    <p>从文件恢复您的数据</p>
                  </div>
                </button>

                <button class="action-btn clear" @click="clearUserData">
                  <i class="fas fa-trash"></i>
                  <div class="action-info">
                    <h4>清除数据</h4>
                    <p>删除所有本地存储的数据</p>
                  </div>
                </button>
              </div>
            </div>
          </div>

          <!-- 隐私设置 -->
          <div class="settings-card">
            <div class="card-header">
              <h3>
                <i class="fas fa-shield-alt"></i>
                隐私与安全
              </h3>
            </div>
            <div class="card-content">
              <div class="privacy-info">
                <div class="info-item">
                  <i class="fas fa-check-circle"></i>
                  <span>所有对话数据仅存储在您的设备本地</span>
                </div>
                <div class="info-item">
                  <i class="fas fa-check-circle"></i>
                  <span>我们不会收集您的个人敏感信息</span>
                </div>
                <div class="info-item">
                  <i class="fas fa-check-circle"></i>
                  <span>您可以随时清除所有数据</span>
                </div>
                <div class="info-item">
                  <i class="fas fa-check-circle"></i>
                  <span>所有通信均经过加密保护</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 头像编辑器 -->
    <div v-if="showAvatarEditor" class="modal-overlay" @click="closeAvatarEditor">
      <div class="avatar-editor-modal" @click.stop>
        <div class="modal-header">
          <h3>编辑头像</h3>
          <button @click="closeAvatarEditor" class="modal-close">
            <i class="fas fa-times"></i>
          </button>
        </div>

        <div class="modal-body">
          <div class="avatar-options">
            <h4>选择头像颜色</h4>
            <div class="color-picker">
              <div
                  v-for="color in avatarColors"
                  :key="color"
                  class="color-option"
                  :class="{ active: tempProfile.avatarColor === color }"
                  :style="{ backgroundColor: color }"
                  @click="tempProfile.avatarColor = color"
              ></div>
            </div>

            <h4>或上传头像图片</h4>
            <div class="upload-area">
              <input
                  type="file"
                  ref="avatarInput"
                  accept="image/*"
                  @change="handleAvatarUpload"
                  style="display: none"
              >
              <button @click="$refs.avatarInput.click()" class="upload-btn">
                <i class="fas fa-upload"></i>
                选择图片
              </button>
              <p class="upload-hint">支持 JPG、PNG 格式，建议尺寸 200x200</p>
            </div>

            <div class="avatar-preview">
              <h4>预览</h4>
              <div class="preview-avatar" :style="{ backgroundColor: tempProfile.avatarColor }">
                <i v-if="!tempProfile.avatar" class="fas fa-user avatar-icon"></i>
                <img v-else :src="tempProfile.avatar" alt="头像预览" class="avatar-image">
              </div>
            </div>
          </div>
        </div>

        <div class="modal-footer">
          <button class="btn btn-outline" @click="closeAvatarEditor">取消</button>
          <button class="btn btn-primary" @click="saveAvatar">保存头像</button>
        </div>
      </div>
    </div>

    <!-- 文件上传输入 -->
    <input
        type="file"
        ref="importInput"
        accept=".json"
        @change="handleImportFile"
        style="display: none"
    >
  </div>
</template>

<script>
import { ref, computed, onMounted, reactive } from 'vue'

export default {
  name: 'ProfilePage',
  setup() {
    // 响应式数据
    const profile = reactive({
      id: '',
      name: '',
      email: '',
      bio: '',
      avatar: '',
      avatarColor: '#667eea',
      createdAt: '',
      lastLoginAt: ''
    })

    const settings = reactive({
      theme: 'light',
      language: 'zh-CN',
      autoSave: true,
      notifications: true,
      sendKey: 'enter'
    })

    const tempProfile = reactive({
      avatar: '',
      avatarColor: '#667eea'
    })

    const saving = ref(false)
    const showAvatarEditor = ref(false)
    const importInput = ref(null)
    const avatarInput = ref(null)

    const avatarColors = [
      '#667eea', '#764ba2', '#f093fb', '#f5576c',
      '#4facfe', '#00f2fe', '#43e97b', '#38f9d7',
      '#ffecd2', '#fcb69f', '#a8edea', '#fed6e3',
      '#ff9a9e', '#fecfef', '#ffeaa7', '#fab1a0'
    ]

    // 计算属性
    const userStats = computed(() => {
      // 从本地存储获取统计数据
      const history = JSON.parse(localStorage.getItem('chat_history') || '[]')

      const totalConversations = history.length
      const totalMessages = history.reduce((sum, conv) => sum + conv.messages.length, 0)

      // 计算活跃天数
      const uniqueDates = new Set(
          history.map(conv => new Date(conv.timestamp).toDateString())
      )

      return {
        totalDays: uniqueDates.size,
        totalConversations,
        totalMessages
      }
    })

    // 方法
    const loadProfile = () => {
      const savedProfile = localStorage.getItem('user_profile')
      if (savedProfile) {
        Object.assign(profile, JSON.parse(savedProfile))
      } else {
        // 创建默认档案
        profile.id = `user_${Date.now()}`
        profile.createdAt = new Date().toISOString()
        profile.lastLoginAt = new Date().toISOString()
        saveProfileData()
      }

      const savedSettings = localStorage.getItem('app_settings')
      if (savedSettings) {
        Object.assign(settings, JSON.parse(savedSettings))
      }

      // 复制到临时对象
      tempProfile.avatar = profile.avatar
      tempProfile.avatarColor = profile.avatarColor
    }

    const saveProfile = async () => {
      saving.value = true

      try {
        // 模拟保存延迟
        await new Promise(resolve => setTimeout(resolve, 1000))

        profile.lastLoginAt = new Date().toISOString()
        saveProfileData()

        window.$emit('notification:show', {
          type: 'success',
          title: '保存成功',
          message: '个人信息已更新'
        })
      } catch (error) {
        console.error('保存失败:', error)
        window.$emit('notification:show', {
          type: 'error',
          title: '保存失败',
          message: '请稍后重试'
        })
      } finally {
        saving.value = false
      }
    }

    const saveProfileData = () => {
      localStorage.setItem('user_profile', JSON.stringify(profile))
    }

    const updateTheme = () => {
      document.documentElement.setAttribute('data-theme', settings.theme)
      updateSettings()

      if (settings.theme === 'auto') {
        const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)')
        const updateAutoTheme = (e) => {
          document.documentElement.setAttribute('data-theme', e.matches ? 'dark' : 'light')
        }

        updateAutoTheme(mediaQuery)
        mediaQuery.addEventListener('change', updateAutoTheme)
      }
    }

    const updateLanguage = () => {
      // 这里可以实现语言切换逻辑
      updateSettings()

      window.$emit('notification:show', {
        type: 'info',
        title: '语言设置',
        message: `已切换到${settings.language === 'zh-CN' ? '简体中文' : settings.language === 'zh-TW' ? '繁体中文' : '英文'}`
      })
    }

    const updateSettings = () => {
      localStorage.setItem('app_settings', JSON.stringify(settings))
    }

    const closeAvatarEditor = () => {
      showAvatarEditor.value = false
    }

    const handleAvatarUpload = (event) => {
      const file = event.target.files[0]
      if (!file) return

      if (!file.type.startsWith('image/')) {
        window.$emit('notification:show', {
          type: 'error',
          title: '文件类型错误',
          message: '请选择图片文件'
        })
        return
      }

      if (file.size > 2 * 1024 * 1024) {
        window.$emit('notification:show', {
          type: 'error',
          title: '文件太大',
          message: '图片大小不能超过2MB'
        })
        return
      }

      const reader = new FileReader()
      reader.onload = (e) => {
        tempProfile.avatar = e.target.result
      }
      reader.readAsDataURL(file)
    }

    const saveAvatar = () => {
      profile.avatar = tempProfile.avatar
      profile.avatarColor = tempProfile.avatarColor
      saveProfileData()
      closeAvatarEditor()

      window.$emit('notification:show', {
        type: 'success',
        title: '头像已更新',
        message: '您的头像已保存'
      })
    }

    const exportUserData = () => {
      const userData = {
        profile: { ...profile },
        settings: { ...settings },
        history: JSON.parse(localStorage.getItem('chat_history') || '[]'),
        exportTime: new Date().toISOString()
      }

      const blob = new Blob([JSON.stringify(userData, null, 2)], {
        type: 'application/json'
      })

      const url = URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = `心理健康AI助手_用户数据_${new Date().toLocaleDateString()}.json`
      a.click()
      URL.revokeObjectURL(url)

      window.$emit('notification:show', {
        type: 'success',
        title: '导出成功',
        message: '用户数据已保存到文件'
      })
    }

    const importUserData = () => {
      importInput.value.click()
    }

    const handleImportFile = (event) => {
      const file = event.target.files[0]
      if (!file) return

      const reader = new FileReader()
      reader.onload = (e) => {
        try {
          const userData = JSON.parse(e.target.result)

          if (userData.profile) {
            Object.assign(profile, userData.profile)
            saveProfileData()
          }

          if (userData.settings) {
            Object.assign(settings, userData.settings)
            updateSettings()
            updateTheme()
          }

          if (userData.history) {
            localStorage.setItem('chat_history', JSON.stringify(userData.history))
          }

          window.$emit('notification:show', {
            type: 'success',
            title: '导入成功',
            message: '用户数据已恢复'
          })
        } catch (error) {
          console.error('导入失败:', error)
          window.$emit('notification:show', {
            type: 'error',
            title: '导入失败',
            message: '文件格式不正确'
          })
        }
      }
      reader.readAsText(file)
    }

    const clearUserData = () => {
      window.$emit('modal:open', {
        title: '清除所有数据',
        content: '此操作将删除所有用户数据，包括个人信息、对话历史和设置。此操作无法撤销，确定要继续吗？',
        type: 'warning',
        confirmText: '确定清除',
        onConfirm: () => {
          // 清除所有数据
          localStorage.clear()

          // 重置为默认值
          Object.assign(profile, {
            id: `user_${Date.now()}`,
            name: '',
            email: '',
            bio: '',
            avatar: '',
            avatarColor: '#667eea',
            createdAt: new Date().toISOString(),
            lastLoginAt: new Date().toISOString()
          })

          Object.assign(settings, {
            theme: 'light',
            language: 'zh-CN',
            autoSave: true,
            notifications: true,
            sendKey: 'enter'
          })

          saveProfileData()
          updateSettings()
          updateTheme()

          window.$emit('notification:show', {
            type: 'success',
            title: '清除完成',
            message: '所有用户数据已清除'
          })
        }
      })
    }

    // 生命周期
    onMounted(() => {
      loadProfile()
    })

    return {
      profile,
      settings,
      tempProfile,
      saving,
      showAvatarEditor,
      importInput,
      avatarInput,
      avatarColors,
      userStats,
      saveProfile,
      updateTheme,
      updateLanguage,
      updateSettings,
      closeAvatarEditor,
      handleAvatarUpload,
      saveAvatar,
      exportUserData,
      importUserData,
      handleImportFile,
      clearUserData
    }
  }
}
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 20px;
}

.page-header {
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

.page-content {
  max-width: 1200px;
  margin: 0 auto;
}

.profile-container {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 30px;
}

.profile-card {
  background: white;
  padding: 30px;
  border-radius: 15px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.1);
  height: fit-content;
}

.profile-avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.avatar-container {
  position: relative;
  margin-bottom: 20px;
}

.avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.avatar-icon {
  font-size: 48px;
  color: rgba(255, 255, 255, 0.8);
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-edit-btn {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 35px;
  height: 35px;
  border: none;
  background: #667eea;
  color: white;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
  transition: all 0.3s ease;
}

.avatar-edit-btn:hover {
  background: #5a6fd8;
  transform: scale(1.1);
}

.user-info h2 {
  margin: 0 0 5px 0;
  color: #333;
  font-size: 24px;
}

.user-id {
  margin: 0 0 20px 0;
  color: #999;
  font-size: 14px;
  font-family: monospace;
}

.user-stats {
  display: flex;
  justify-content: space-around;
  padding: 20px 0;
  border-top: 1px solid #f0f0f0;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #667eea;
}

.stat-label {
  font-size: 12px;
  color: #999;
}

.settings-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.settings-card {
  background: white;
  border-radius: 15px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.card-header {
  padding: 20px 25px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.card-header h3 {
  margin: 0;
  font-size: 18px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.card-content {
  padding: 25px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #333;
  font-weight: 500;
}

.form-group input,
.form-group textarea,
.form-group select {
  width: 100%;
  padding: 12px 15px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 14px;
  transition: border-color 0.3s ease;
  box-sizing: border-box;
}

.form-group input:focus,
.form-group textarea:focus,
.form-group select:focus {
  outline: none;
  border-color: #667eea;
}

.form-hint {
  display: block;
  margin-top: 5px;
  color: #999;
  font-size: 12px;
}

.form-actions {
  margin-top: 30px;
}

.btn {
  padding: 12px 24px;
  border: none;
  border-radius: 25px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
  text-decoration: none;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 5px 20px rgba(102, 126, 234, 0.4);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-outline {
  background: white;
  color: #667eea;
  border: 2px solid #667eea;
}

.btn-outline:hover {
  background: #667eea;
  color: white;
}

.setting-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 0;
  border-bottom: 1px solid #f0f0f0;
}

.setting-item:last-child {
  border-bottom: none;
}

.setting-info h4 {
  margin: 0 0 5px 0;
  color: #333;
  font-size: 16px;
}

.setting-info p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.setting-control select {
  min-width: 150px;
}

.switch {
  position: relative;
  display: inline-block;
  width: 50px;
  height: 24px;
}

.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ccc;
  transition: .4s;
  border-radius: 24px;
}

.slider:before {
  position: absolute;
  content: "";
  height: 16px;
  width: 16px;
  left: 4px;
  bottom: 4px;
  background-color: white;
  transition: .4s;
  border-radius: 50%;
}

input:checked + .slider {
  background-color: #667eea;
}

input:checked + .slider:before {
  transform: translateX(26px);
}

.data-actions {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 20px;
  border: 2px solid #e0e0e0;
  border-radius: 10px;
  background: white;
  cursor: pointer;
  transition: all 0.3s ease;
}

.action-btn:hover {
  border-color: #667eea;
  transform: translateY(-2px);
}

.action-btn i {
  font-size: 24px;
  width: 40px;
  text-align: center;
}

.action-btn.export i {
  color: #4CAF50;
}

.action-btn.import i {
  color: #2196F3;
}

.action-btn.clear i {
  color: #ff5252;
}

.action-info h4 {
  margin: 0 0 5px 0;
  color: #333;
  font-size: 16px;
}

.action-info p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.privacy-info {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #333;
  font-size: 14px;
}

.info-item i {
  color: #4CAF50;
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

.avatar-editor-modal {
  background: white;
  border-radius: 15px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  max-width: 500px;
  width: 100%;
  max-height: 80vh;
  overflow-y: auto;
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
  padding: 25px;
}

.avatar-options h4 {
  margin: 0 0 15px 0;
  color: #333;
  font-size: 16px;
}

.color-picker {
  display: grid;
  grid-template-columns: repeat(8, 1fr);
  gap: 10px;
  margin-bottom: 30px;
}

.color-option {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  cursor: pointer;
  border: 3px solid transparent;
  transition: all 0.3s ease;
}

.color-option:hover {
  transform: scale(1.1);
}

.color-option.active {
  border-color: #333;
  transform: scale(1.2);
}

.upload-area {
  text-align: center;
  margin-bottom: 30px;
}

.upload-btn {
  padding: 12px 24px;
  border: 2px dashed #667eea;
  background: rgba(102, 126, 234, 0.05);
  color: #667eea;
  border-radius: 10px;
  cursor: pointer;
  font-size: 14px;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
}

.upload-btn:hover {
  background: rgba(102, 126, 234, 0.1);
}

.upload-hint {
  margin: 10px 0 0 0;
  color: #999;
  font-size: 12px;
}

.avatar-preview {
  text-align: center;
}

.preview-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  margin-top: 10px;
  overflow: hidden;
}

.preview-avatar .avatar-icon {
  font-size: 32px;
  color: rgba(255, 255, 255, 0.8);
}

.preview-avatar .avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
  padding: 20px 25px;
  border-top: 1px solid #f0f0f0;
}

@media (max-width: 768px) {
  .profile-page {
    padding: 10px;
  }

  .profile-container {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .color-picker {
    grid-template-columns: repeat(4, 1fr);
  }

  .setting-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .setting-control {
    width: 100%;
  }

  .setting-control select {
    width: 100%;
    min-width: auto;
  }

  .user-stats {
    flex-direction: column;
    gap: 15px;
  }

  .data-actions .action-btn {
    flex-direction: column;
    text-align: center;
    gap: 10px;
  }

  .avatar-editor-modal {
    margin: 10px;
    max-height: calc(100vh - 20px);
  }
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.fa-spin {
  animation: spin 1s linear infinite;
}
</style>