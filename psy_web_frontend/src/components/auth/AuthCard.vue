<!-- 
  AuthCard.vue
  这是一个通用的认证卡片组件，用于登录和注册页面
  使用方式：
  <auth-card title="登录" subtitle="请输入你的邮箱和密码以登录">
    这里放置表单内容
  </auth-card>
-->
<script setup>
// 定义组件的属性
const props = defineProps({
  // 卡片标题
  title: {
    type: String,
    required: true
  },
  // 卡片副标题
  subtitle: {
    type: String,
    required: true
  }
})
</script>

<template>
  <div class="auth-container">
    <div class="auth-card">
      <!-- 卡片标题 -->
      <h2 class="auth-title">{{ title }}</h2>
      <p class="auth-subtitle">{{ subtitle }}</p>
      
      <!-- 卡片内容插槽，用于放置具体的表单内容 -->
      <div class="auth-content">
        <slot></slot>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 容器样式 */
.auth-container {
  min-height: 100vh;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}

/* 卡片样式 */
.auth-card {
  background: white;
  border-radius: 16px;
  padding: 2.5rem;
  width: 100%;
  max-width: 420px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
}

/* 标题样式 */
.auth-title {
  text-align: center;
  margin-bottom: 0.5rem;
  color: #333;
  font-size: 1.75rem;
  font-weight: 600;
}

/* 副标题样式 */
.auth-subtitle {
  text-align: center;
  color: #666;
  font-size: 0.95rem;
  margin-bottom: 2rem;
}

/* 内容区域样式 */
.auth-content {
  margin-top: 1rem;
}

/* 确保在移动设备上也有良好的显示效果 */
@media (max-width: 480px) {
  .auth-card {
    margin: 1rem;
    padding: 1.5rem;
  }
}
</style> 