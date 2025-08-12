<template>
  <header class="app-header">
    <div class="header-container">
      <div class="header-brand">
        <i class="fas fa-heart"></i>
        心理健康AI助手
      </div>
      <nav class="header-nav">
        <button
            v-for="page in pages"
            :key="page.key"
            :class="['nav-btn', { active: currentPage === page.key }]"
            @click="$emit('page-change', page.key)"
        >
          <i :class="page.icon"></i>
          {{ page.label }}
        </button>
      </nav>
    </div>
  </header>
</template>

<script>
export default {
  name: 'AppHeader',
  props: {
    currentPage: {
      type: String,
      default: 'home'
    }
  },
  emits: ['page-change'],
  data() {
    return {
      pages: [
        { key: 'home', label: '首页', icon: 'fas fa-home' },
        { key: 'chat', label: '对话', icon: 'fas fa-comments' },
        { key: 'history', label: '历史', icon: 'fas fa-history' },
        { key: 'profile', label: '个人', icon: 'fas fa-user' },
        { key: 'help', label: '帮助', icon: 'fas fa-question-circle' }
      ]
    }
  }
}
</script>

<style scoped>
.app-header {
  background: white;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 1rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-brand {
  font-size: 1.25rem;
  font-weight: 600;
  color: #333;
}

.header-nav {
  display: flex;
  gap: 0.5rem;
}

.nav-btn {
  padding: 0.5rem 1rem;
  border: none;
  background: transparent;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.nav-btn:hover,
.nav-btn.active {
  background: #007bff;
  color: white;
}
</style>