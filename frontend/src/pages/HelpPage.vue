<template>
  <div class="help-page">
    <div class="page-header">
      <div class="header-content">
        <h1>
          <i class="fas fa-question-circle"></i>
          帮助中心
        </h1>
        <p>了解如何使用心理健康AI助手，获取更多支持和帮助</p>
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
              placeholder="搜索帮助内容..."
              @input="handleSearch"
          >
        </div>
      </div>

      <!-- 快速链接 -->
      <div class="quick-links">
        <h2>快速链接</h2>
        <div class="links-grid">
          <div
              v-for="link in quickLinks"
              :key="link.id"
              class="link-card"
              @click="scrollToSection(link.target)"
          >
            <div class="link-icon" :style="{ backgroundColor: link.color }">
              <i :class="link.icon"></i>
            </div>
            <div class="link-info">
              <h4>{{ link.title }}</h4>
              <p>{{ link.description }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 常见问题 -->
      <div class="faq-section" id="faq">
        <h2>常见问题</h2>
        <div class="faq-list">
          <div
              v-for="faq in filteredFAQs"
              :key="faq.id"
              class="faq-item"
              :class="{ active: faq.isOpen }"
          >
            <div class="faq-question" @click="toggleFAQ(faq)">
              <h4>{{ faq.question }}</h4>
              <i class="fas fa-chevron-down"></i>
            </div>
            <div class="faq-answer" v-show="faq.isOpen">
              <div class="answer-content" v-html="faq.answer"></div>
            </div>
          </div>
        </div>
      </div>

      <!-- 使用指南 -->
      <div class="guide-section" id="guide">
        <h2>使用指南</h2>
        <div class="guide-tabs">
          <button
              v-for="tab in guideTabs"
              :key="tab.id"
              class="tab-button"
              :class="{ active: activeGuideTab === tab.id }"
              @click="activeGuideTab = tab.id"
          >
            <i :class="tab.icon"></i>
            {{ tab.title }}
          </button>
        </div>

        <div class="guide-content">
          <div
              v-for="tab in guideTabs"
              :key="tab.id"
              v-show="activeGuideTab === tab.id"
              class="guide-tab-content"
          >
            <h3>{{ tab.title }}</h3>
            <div class="steps-list">
              <div
                  v-for="(step, index) in tab.steps"
                  :key="index"
                  class="step-item"
              >
                <div class="step-number">{{ index + 1 }}</div>
                <div class="step-content">
                  <h4>{{ step.title }}</h4>
                  <p>{{ step.description }}</p>
                  <div v-if="step.image" class="step-image">
                    <img :src="step.image" :alt="step.title">
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 功能介绍 -->
      <div class="features-section" id="features">
        <h2>功能介绍</h2>
        <div class="features-grid">
          <div
              v-for="feature in features"
              :key="feature.id"
              class="feature-card"
          >
            <div class="feature-icon" :style="{ backgroundColor: feature.color }">
              <i :class="feature.icon"></i>
            </div>
            <div class="feature-content">
              <h4>{{ feature.title }}</h4>
              <p>{{ feature.description }}</p>
              <ul v-if="feature.highlights">
                <li v-for="highlight in feature.highlights" :key="highlight">
                  {{ highlight }}
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>

      <!-- 隐私与安全 -->
      <div class="privacy-section" id="privacy">
        <h2>隐私与安全</h2>
        <div class="privacy-content">
          <div class="privacy-card">
            <div class="card-icon">
              <i class="fas fa-shield-alt"></i>
            </div>
            <div class="card-content">
              <h4>数据安全保护</h4>
              <p>我们采用多层安全措施保护您的数据安全：</p>
              <ul>
                <li>所有对话数据仅存储在您的本地设备中</li>
                <li>通信采用端到端加密技术</li>
                <li>不会收集或存储您的个人敏感信息</li>
                <li>您可以随时删除所有本地数据</li>
              </ul>
            </div>
          </div>

          <div class="privacy-card">
            <div class="card-icon">
              <i class="fas fa-user-shield"></i>
            </div>
            <div class="card-content">
              <h4>隐私政策</h4>
              <p>我们承诺保护您的隐私权利：</p>
              <ul>
                <li>不会与第三方分享您的对话内容</li>
                <li>不会进行任何形式的数据挖掘或分析</li>
                <li>遵循国际数据保护法规要求</li>
                <li>提供透明的数据处理说明</li>
              </ul>
            </div>
          </div>
        </div>
      </div>

      <!-- 联系我们 -->
      <div class="contact-section" id="contact">
        <h2>联系我们</h2>
        <div class="contact-content">
          <div class="contact-info">
            <h4>需要更多帮助？</h4>
            <p>如果您在使用过程中遇到问题，或有任何建议，请随时联系我们：</p>

            <div class="contact-methods">
              <div class="contact-item">
                <i class="fas fa-envelope"></i>
                <div>
                  <strong>邮箱支持</strong>
                  <p>support@aihelper.com</p>
                </div>
              </div>

              <div class="contact-item">
                <i class="fas fa-comments"></i>
                <div>
                  <strong>在线客服</strong>
                  <p>工作日 9:00-18:00</p>
                </div>
              </div>

              <div class="contact-item">
                <i class="fas fa-question-circle"></i>
                <div>
                  <strong>FAQ更新</strong>
                  <p>常见问题定期更新</p>
                </div>
              </div>
            </div>
          </div>

          <div class="feedback-form">
            <h4>意见反馈</h4>
            <form @submit.prevent="submitFeedback">
              <div class="form-group">
                <label for="feedbackType">反馈类型</label>
                <select id="feedbackType" v-model="feedback.type">
                  <option value="bug">问题反馈</option>
                  <option value="feature">功能建议</option>
                  <option value="praise">表扬建议</option>
                  <option value="other">其他</option>
                </select>
              </div>

              <div class="form-group">
                <label for="feedbackContent">详细描述</label>
                <textarea
                    id="feedbackContent"
                    v-model="feedback.content"
                    placeholder="请详细描述您的问题或建议..."
                    rows="4"
                    required
                ></textarea>
              </div>

              <div class="form-group">
                <label for="feedbackEmail">联系邮箱（可选）</label>
                <input
                    type="email"
                    id="feedbackEmail"
                    v-model="feedback.email"
                    placeholder="如需回复请填写邮箱"
                >
              </div>

              <button type="submit" class="btn btn-primary" :disabled="submitting">
                <i v-if="submitting" class="fas fa-spinner fa-spin"></i>
                <i v-else class="fas fa-paper-plane"></i>
                {{ submitting ? '提交中...' : '提交反馈' }}
              </button>
            </form>
          </div>
        </div>
      </div>

      <!-- 版本信息 -->
      <div class="version-section">
        <h2>版本信息</h2>
        <div class="version-info">
          <div class="version-card">
            <h4>当前版本</h4>
            <p class="version-number">v1.0.0</p>
            <p class="version-date">发布日期：2024年12月</p>
          </div>

          <div class="changelog">
            <h4>更新日志</h4>
            <div class="changelog-list">
              <div class="changelog-item">
                <div class="version-tag">v1.0.0</div>
                <div class="changes">
                  <ul>
                    <li>首次发布心理健康AI助手</li>
                    <li>支持智能对话和情感支持</li>
                    <li>提供对话历史记录功能</li>
                    <li>完整的用户隐私保护</li>
                  </ul>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, reactive } from 'vue'

export default {
  name: 'HelpPage',
  setup() {
    // 响应式数据
    const searchQuery = ref('')
    const activeGuideTab = ref('start')
    const submitting = ref(false)

    const feedback = reactive({
      type: 'bug',
      content: '',
      email: ''
    })

    // 快速链接
    const quickLinks = ref([
      {
        id: 'start',
        title: '快速开始',
        description: '了解如何开始使用AI助手',
        icon: 'fas fa-play-circle',
        color: '#4CAF50',
        target: '#guide'
      },
      {
        id: 'faq',
        title: '常见问题',
        description: '查看用户常见的问题解答',
        icon: 'fas fa-question-circle',
        color: '#2196F3',
        target: '#faq'
      },
      {
        id: 'features',
        title: '功能介绍',
        description: '详细了解所有功能特性',
        icon: 'fas fa-star',
        color: '#FF9800',
        target: '#features'
      },
      {
        id: 'privacy',
        title: '隐私安全',
        description: '了解数据安全和隐私保护',
        icon: 'fas fa-shield-alt',
        color: '#9C27B0',
        target: '#privacy'
      }
    ])

    // 常见问题
    const faqs = ref([
      {
        id: 1,
        question: '心理健康AI助手是什么？',
        answer: '心理健康AI助手是一个专为提供心理健康支持而设计的智能对话系统。它可以倾听您的心声，提供情感支持和实用建议，帮助您更好地管理情绪和心理健康。',
        isOpen: false,
        keywords: ['什么', '介绍', '心理健康', 'AI助手']
      },
      {
        id: 2,
        question: '如何开始使用？',
        answer: `开始使用非常简单：<br>
        1. 点击导航栏中的"对话"按钮<br>
        2. 在输入框中输入您想要分享的内容<br>
        3. 按回车键或点击发送按钮<br>
        4. AI助手会回复并提供支持<br>
        您可以随时开始新的对话主题。`,
        isOpen: false,
        keywords: ['如何', '开始', '使用', '操作']
      },
      {
        id: 3,
        question: '我的对话数据安全吗？',
        answer: '是的，您的数据完全安全：<br>• 所有对话仅存储在您的本地设备中<br>• 我们不会收集或上传您的对话内容<br>• 您可以随时清除所有数据<br>• 通信过程经过加密保护',
        isOpen: false,
        keywords: ['安全', '数据', '隐私', '保护']
      },
      {
        id: 4,
        question: '可以导出对话记录吗？',
        answer: '当然可以！您有多种方式管理对话记录：<br>• 在对话页面点击导出按钮保存单次对话<br>• 在历史记录页面导出所有对话<br>• 在个人资料页面导出完整用户数据<br>导出的文件为文本格式，方便您保存和查看。',
        isOpen: false,
        keywords: ['导出', '保存', '对话记录', '历史']
      },
      {
        id: 5,
        question: 'AI助手能提供专业心理治疗吗？',
        answer: 'AI助手主要提供情感支持和日常心理健康建议，<strong>不能替代专业心理治疗</strong>。如果您面临严重的心理健康问题，建议：<br>• 寻求专业心理咨询师帮助<br>• 联系心理健康服务机构<br>• 在紧急情况下拨打心理危机干预热线<br>AI助手可以作为日常情感支持的补充工具。',
        isOpen: false,
        keywords: ['专业', '治疗', '心理咨询', '医生']
      },
      {
        id: 6,
        question: '如何清除或重置数据？',
        answer: '您可以通过以下方式管理数据：<br><strong>清除单个对话：</strong><br>• 在历史记录页面点击删除按钮<br><strong>清除所有数据：</strong><br>• 进入个人资料页面<br>• 在"数据管理"部分点击"清除数据"<br>• 确认操作后所有数据将被删除<br>请注意，此操作无法撤销。',
        isOpen: false,
        keywords: ['清除', '删除', '重置', '数据']
      }
    ])

    // 使用指南标签
    const guideTabs = ref([
      {
        id: 'start',
        title: '快速开始',
        icon: 'fas fa-play-circle',
        steps: [
          {
            title: '进入对话页面',
            description: '点击导航栏中的"对话"按钮，进入与AI助手的对话界面。'
          },
          {
            title: '开始对话',
            description: '在输入框中输入您想要分享的内容，可以是感受、困扰或任何想法。'
          },
          {
            title: '发送消息',
            description: '按回车键或点击发送按钮，AI助手会倾听并提供支持和建议。'
          },
          {
            title: '继续交流',
            description: '您可以继续与AI助手交流，它会记住对话上下文，提供更好的帮助。'
          }
        ]
      },
      {
        id: 'history',
        title: '管理历史',
        icon: 'fas fa-history',
        steps: [
          {
            title: '查看历史记录',
            description: '点击"历史"页面，查看所有过往的对话记录。'
          },
          {
            title: '搜索对话',
            description: '使用搜索功能快速找到特定的对话内容。'
          },
          {
            title: '导出对话',
            description: '点击导出按钮，将对话保存为文本文件到本地。'
          },
          {
            title: '管理对话',
            description: '可以收藏重要对话，或删除不需要的记录。'
          }
        ]
      },
      {
        id: 'settings',
        title: '个性化设置',
        icon: 'fas fa-cogs',
        steps: [
          {
            title: '完善个人资料',
            description: '在个人资料页面设置您的昵称、邮箱等基本信息。'
          },
          {
            title: '选择主题',
            description: '根据喜好选择浅色、深色或自动主题模式。'
          },
          {
            title: '调整设置',
            description: '配置自动保存、通知提醒等功能选项。'
          },
          {
            title: '数据管理',
            description: '了解如何导出、导入或清除您的数据。'
          }
        ]
      }
    ])

    // 功能介绍
    const features = ref([
      {
        id: 'chat',
        title: '智能对话',
        description: '与AI助手进行自然流畅的对话交流',
        icon: 'fas fa-comments',
        color: '#4CAF50',
        highlights: [
          '支持多轮对话上下文理解',
          '提供个性化回应和建议',
          '24小时随时可用',
          '支持表情符号和富文本'
        ]
      },
      {
        id: 'emotion',
        title: '情感支持',
        description: '专业的情感识别和心理健康支持',
        icon: 'fas fa-heart',
        color: '#E91E63',
        highlights: [
          '情感状态智能识别',
          '提供针对性心理建议',
          '支持多种情绪场景',
          '温暖贴心的回应方式'
        ]
      },
      {
        id: 'privacy',
        title: '隐私保护',
        description: '完善的数据安全和隐私保护机制',
        icon: 'fas fa-shield-alt',
        color: '#9C27B0',
        highlights: [
          '本地存储不上云',
          '端到端加密通信',
          '用户完全控制数据',
          '随时可清除所有记录'
        ]
      },
      {
        id: 'history',
        title: '历史记录',
        description: '完整的对话历史管理功能',
        icon: 'fas fa-history',
        color: '#2196F3',
        highlights: [
          '自动保存所有对话',
          '强大的搜索和筛选',
          '支持导出和备份',
          '分类标签管理'
        ]
      }
    ])

    // 计算属性
    const filteredFAQs = computed(() => {
      if (!searchQuery.value) return faqs.value

      const query = searchQuery.value.toLowerCase()
      return faqs.value.filter(faq =>
          faq.question.toLowerCase().includes(query) ||
          faq.answer.toLowerCase().includes(query) ||
          faq.keywords.some(keyword => keyword.includes(query))
      )
    })

    // 方法
    const handleSearch = () => {
      // 搜索时自动展开匹配的FAQ
      filteredFAQs.value.forEach(faq => {
        if (searchQuery.value) {
          faq.isOpen = true
        }
      })
    }

    const scrollToSection = (target) => {
      const element = document.querySelector(target)
      if (element) {
        element.scrollIntoView({ behavior: 'smooth' })
      }
    }

    const toggleFAQ = (faq) => {
      faq.isOpen = !faq.isOpen
    }

    const submitFeedback = async () => {
      if (!feedback.content.trim()) {
        window.$emit('notification:show', {
          type: 'warning',
          title: '请填写反馈内容',
          message: '请详细描述您的问题或建议'
        })
        return
      }

      submitting.value = true

      try {
        // 模拟提交延迟
        await new Promise(resolve => setTimeout(resolve, 2000))

        // 这里可以实现真实的反馈提交逻辑
        console.log('反馈内容:', feedback)

        // 重置表单
        feedback.content = ''
        feedback.email = ''
        feedback.type = 'bug'

        window.$emit('notification:show', {
          type: 'success',
          title: '反馈提交成功',
          message: '感谢您的反馈，我们会认真处理您的建议'
        })
      } catch (error) {
        console.error('提交反馈失败:', error)
        window.$emit('notification:show', {
          type: 'error',
          title: '提交失败',
          message: '请稍后重试'
        })
      } finally {
        submitting.value = false
      }
    }

    return {
      searchQuery,
      activeGuideTab,
      submitting,
      feedback,
      quickLinks,
      faqs,
      guideTabs,
      features,
      filteredFAQs,
      handleSearch,
      scrollToSection,
      toggleFAQ,
      submitFeedback
    }
  }
}
</script>

<style scoped>
.help-page {
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
  line-height: 1.6;
}

.page-content {
  max-width: 1200px;
  margin: 0 auto;
}

.page-content h2 {
  margin: 0 0 25px 0;
  color: #333;
  font-size: 24px;
  font-weight: 600;
}

/* 搜索栏 */
.search-section {
  background: white;
  padding: 25px;
  border-radius: 15px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.1);
  margin-bottom: 30px;
}

.search-bar {
  position: relative;
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

/* 快速链接 */
.quick-links {
  background: white;
  padding: 30px;
  border-radius: 15px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.1);
  margin-bottom: 30px;
}

.links-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.link-card {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 20px;
  border: 2px solid #f0f0f0;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.link-card:hover {
  border-color: #667eea;
  transform: translateY(-3px);
  box-shadow: 0 5px 20px rgba(102, 126, 234, 0.2);
}

.link-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
}

.link-info h4 {
  margin: 0 0 5px 0;
  color: #333;
  font-size: 16px;
}

.link-info p {
  margin: 0;
  color: #666;
  font-size: 14px;
  line-height: 1.4;
}

/* 常见问题 */
.faq-section {
  background: white;
  padding: 30px;
  border-radius: 15px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.1);
  margin-bottom: 30px;
}

.faq-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.faq-item {
  border: 2px solid #f0f0f0;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.faq-item.active {
  border-color: #667eea;
}

.faq-question {
  padding: 20px;
  background: #f8fafc;
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: center;
  transition: all 0.3s ease;
}

.faq-question:hover {
  background: #e3f2fd;
}

.faq-question h4 {
  margin: 0;
  color: #333;
  font-size: 16px;
  font-weight: 500;
}

.faq-question i {
  color: #667eea;
  transition: transform 0.3s ease;
}

.faq-item.active .faq-question i {
  transform: rotate(180deg);
}

.faq-answer {
  padding: 0 20px;
  max-height: 0;
  overflow: hidden;
  transition: all 0.3s ease;
}

.faq-item.active .faq-answer {
  max-height: 500px;
  padding: 20px;
}

.answer-content {
  color: #333;
  line-height: 1.6;
}

.answer-content strong {
  color: #667eea;
}

/* 使用指南 */
.guide-section {
  background: white;
  padding: 30px;
  border-radius: 15px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.1);
  margin-bottom: 30px;
}

.guide-tabs {
  display: flex;
  gap: 10px;
  margin-bottom: 30px;
  border-bottom: 2px solid #f0f0f0;
}

.tab-button {
  padding: 15px 25px;
  border: none;
  background: none;
  color: #666;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 500;
  border-radius: 8px 8px 0 0;
  transition: all 0.3s ease;
  position: relative;
}

.tab-button:hover {
  color: #667eea;
  background: rgba(102, 126, 234, 0.05);
}

.tab-button.active {
  color: #667eea;
  background: rgba(102, 126, 234, 0.1);
}

.tab-button.active::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  right: 0;
  height: 3px;
  background: #667eea;
}

.guide-content h3 {
  margin: 0 0 25px 0;
  color: #333;
  font-size: 20px;
}

.steps-list {
  display: flex;
  flex-direction: column;
  gap: 25px;
}

.step-item {
  display: flex;
  gap: 20px;
  align-items: flex-start;
}

.step-number {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  flex-shrink: 0;
}

.step-content h4 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 18px;
}

.step-content p {
  margin: 0;
  color: #666;
  line-height: 1.6;
}

/* 功能介绍 */
.features-section {
  background: white;
  padding: 30px;
  border-radius: 15px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.1);
  margin-bottom: 30px;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 25px;
}

.feature-card {
  padding: 25px;
  border: 2px solid #f0f0f0;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.feature-card:hover {
  border-color: #667eea;
  transform: translateY(-5px);
  box-shadow: 0 10px 30px rgba(102, 126, 234, 0.1);
}

.feature-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  margin-bottom: 20px;
}

.feature-content h4 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 18px;
}

.feature-content p {
  margin: 0 0 15px 0;
  color: #666;
  line-height: 1.6;
}

.feature-content ul {
  margin: 0;
  padding-left: 20px;
  color: #666;
}

.feature-content li {
  margin-bottom: 5px;
  line-height: 1.4;
}

/* 隐私安全 */
.privacy-section {
  background: white;
  padding: 30px;
  border-radius: 15px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.1);
  margin-bottom: 30px;
}

.privacy-content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 25px;
}

.privacy-card {
  display: flex;
  gap: 20px;
  padding: 25px;
  background: #f8fafc;
  border-radius: 12px;
  border-left: 4px solid #4CAF50;
}

.card-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: #4CAF50;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}

.card-content h4 {
  margin: 0 0 15px 0;
  color: #333;
  font-size: 18px;
}

.card-content p {
  margin: 0 0 15px 0;
  color: #666;
  line-height: 1.6;
}

.card-content ul {
  margin: 0;
  padding-left: 20px;
  color: #666;
}

.card-content li {
  margin-bottom: 8px;
  line-height: 1.5;
}

/* 联系我们 */
.contact-section {
  background: white;
  padding: 30px;
  border-radius: 15px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.1);
  margin-bottom: 30px;
}

.contact-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
}

.contact-info h4 {
  margin: 0 0 15px 0;
  color: #333;
  font-size: 20px;
}

.contact-info > p {
  margin: 0 0 25px 0;
  color: #666;
  line-height: 1.6;
}

.contact-methods {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 20px;
  background: #f8fafc;
  border-radius: 10px;
}

.contact-item i {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #667eea;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
}

.contact-item strong {
  display: block;
  color: #333;
  font-size: 14px;
  margin-bottom: 5px;
}

.contact-item p {
  margin: 0;
  color: #666;
  font-size: 13px;
}

.feedback-form h4 {
  margin: 0 0 20px 0;
  color: #333;
  font-size: 20px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #333;
  font-weight: 500;
  font-size: 14px;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 12px 15px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 14px;
  transition: border-color 0.3s ease;
  box-sizing: border-box;
  font-family: inherit;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #667eea;
}

.form-group textarea {
  resize: vertical;
  line-height: 1.5;
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

/* 版本信息 */
.version-section {
  background: white;
  padding: 30px;
  border-radius: 15px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.1);
}

.version-info {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 30px;
}

.version-card {
  padding: 25px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 12px;
  text-align: center;
}

.version-card h4 {
  margin: 0 0 20px 0;
  font-size: 18px;
}

.version-number {
  margin: 0 0 10px 0;
  font-size: 36px;
  font-weight: bold;
}

.version-date {
  margin: 0;
  opacity: 0.8;
  font-size: 14px;
}

.changelog h4 {
  margin: 0 0 20px 0;
  color: #333;
  font-size: 18px;
}

.changelog-item {
  display: flex;
  gap: 20px;
  align-items: flex-start;
}

.version-tag {
  padding: 5px 15px;
  background: #e3f2fd;
  color: #1976d2;
  border-radius: 15px;
  font-size: 12px;
  font-weight: 500;
  white-space: nowrap;
}

.changes ul {
  margin: 0;
  padding-left: 20px;
  color: #666;
}

.changes li {
  margin-bottom: 8px;
  line-height: 1.5;
}

@media (max-width: 768px) {
  .help-page {
    padding: 10px;
  }

  .header-content h1 {
    font-size: 24px;
    flex-direction: column;
    text-align: center;
    gap: 10px;
  }

  .links-grid {
    grid-template-columns: 1fr;
  }

  .link-card {
    flex-direction: column;
    text-align: center;
    gap: 10px;
  }

  .guide-tabs {
    flex-direction: column;
    gap: 5px;
  }

  .tab-button {
    justify-content: center;
  }

  .features-grid {
    grid-template-columns: 1fr;
  }

  .privacy-content,
  .contact-content,
  .version-info {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .step-item {
    flex-direction: column;
    align-items: center;
    text-align: center;
    gap: 15px;
  }

  .privacy-card {
    flex-direction: column;
    text-align: center;
    gap: 15px;
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