<template>
  <div class="home-page">
    <div class="welcome-section">
      <h1>欢迎使用心理健康AI助手</h1>
      <p>我是您的专业心理健康伙伴，随时为您提供支持和帮助。</p>
      <button class="start-chat-btn" @click="$emit('page-change', 'chat')">
        <i class="fas fa-comments"></i>
        开始对话
      </button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'HomePage',
  emits: ['page-change']
}
</script>

<style scoped>
.home-page {
  padding: 2rem;
  text-align: center;
}

.welcome-section {
  max-width: 600px;
  margin: 0 auto;
}

.start-chat-btn {
  background: #007bff;
  color: white;
  border: none;
  padding: 1rem 2rem;
  border-radius: 8px;
  font-size: 1.1rem;
  cursor: pointer;
  margin-top: 2rem;
  transition: all 0.2s;
}

.start-chat-btn:hover {
  background: #0056b3;
  transform: translateY(-1px);
}
</style>