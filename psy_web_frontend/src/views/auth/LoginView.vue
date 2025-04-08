<!-- 登录页面 -->
<script setup>
import { ref, reactive } from 'vue'
import AuthCard from '../../components/auth/AuthCard.vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// 表单数据
const loginForm = reactive({
  email: '',
  password: '',
  remember: false
})

// 表单规则
const rules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ]
}

// 表单引用
const formRef = ref(null)

// 登录方法
const handleLogin = async (formEl) => {
  if (!formEl) return
  await formEl.validate((valid) => {
    if (valid) {
      console.log('登录成功', loginForm)
    }
  })
}

// 跳转到注册页面
const goToRegister = () => {
  router.push('/register')
}
</script>

<template>
  <auth-card title="登录" subtitle="请输入您的邮箱和密码以登录">
    <el-form ref="formRef" :model="loginForm" :rules="rules" label-position="top" @keyup.enter="handleLogin(formRef)">
      <!-- 邮箱输入框 -->
      <el-form-item prop="email" label="邮箱">
        <el-input v-model="loginForm.email" placeholder="请输入邮箱" type="email" size="large" />
      </el-form-item>

      <!-- 密码输入框 -->
      <el-form-item prop="password" label="密码">
        <el-input v-model="loginForm.password" placeholder="请输入密码" type="password" size="large" show-password />
      </el-form-item>

      <!-- 记住密码和忘记密码 -->
      <div class="login-options">
        <el-checkbox v-model="loginForm.remember">
          Remember Password
        </el-checkbox>
        <el-link type="primary" :underline="true">忘记密码？</el-link>
      </div>

      <!-- 登录按钮 -->
      <el-button type="primary" size="large" class="login-button" @click="handleLogin(formRef)">
        登录
      </el-button>

      <!-- 注册链接 -->
      <div class="register-link">
        还没有账号？
        <el-link type="primary" :underline="true" @click="goToRegister">注册</el-link>
      </div>
    </el-form>
  </auth-card>
</template>

<style scoped>
.login-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.login-button {
  width: 100%;
  margin-bottom: 16px;
}

.register-link {
  text-align: center;
  color: #606266;
}

:deep(.el-form-item__label) {
  font-size: 14px;
  color: #606266;
}

:deep(.el-input__wrapper) {
  background-color: #f5f7fa;
}

:deep(.el-button--primary) {
  background-color: #6c8cff;
  border-color: #6c8cff;
}

:deep(.el-button--primary:hover) {
  background-color: #5a75d9;
  border-color: #5a75d9;
}
</style>