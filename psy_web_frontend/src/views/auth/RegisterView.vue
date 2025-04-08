<!-- 注册页面 -->
<script setup>
import { ref, reactive } from 'vue'
import AuthCard from '../../components/auth/AuthCard.vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// 表单数据
const registerForm = reactive({
  email: '',
  username: '',
  password: '',
  acceptTerms: false
})

// 表单规则
const rules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, message: '用户名长度不能小于2位', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  acceptTerms: [
    { 
      validator: (rule, value, callback) => {
        if (!value) {
          callback(new Error('请同意服务条款'))
        } else {
          callback()
        }
      }, 
      trigger: 'change'
    }
  ]
}

// 表单引用
const formRef = ref(null)

// 注册方法
const handleRegister = async (formEl) => {
  if (!formEl) return
  await formEl.validate((valid) => {
    if (valid) {
      console.log('注册成功', registerForm)
    }
  })
}

// 跳转到登录页
const goToLogin = () => {
  router.push('/login')
}
</script>

<template>
  <auth-card 
    title="创建账户" 
    subtitle="创建账户以继续"
  >
    <el-form
      ref="formRef"
      :model="registerForm"
      :rules="rules"
      label-position="top"
      @keyup.enter="handleRegister(formRef)"
    >
      <!-- 邮箱输入框 -->
      <el-form-item prop="email" label="邮箱">
        <el-input
          v-model="registerForm.email"
          placeholder="请输入邮箱"
          type="email"
          size="large"
        />
      </el-form-item>

      <!-- 用户名输入框 -->
      <el-form-item prop="username" label="用户名">
        <el-input
          v-model="registerForm.username"
          placeholder="请输入用户名"
          size="large"
        />
      </el-form-item>

      <!-- 密码输入框 -->
      <el-form-item prop="password" label="密码">
        <el-input
          v-model="registerForm.password"
          placeholder="请输入密码"
          type="password"
          size="large"
          show-password
        />
      </el-form-item>

      <!-- 服务条款 -->
      <el-form-item prop="acceptTerms">
        <el-checkbox v-model="registerForm.acceptTerms">
          我接受条款
        </el-checkbox>
      </el-form-item>

      <!-- 注册按钮 -->
      <el-button
        type="primary"
        size="large"
        class="register-button"
        @click="handleRegister(formRef)"
      >
        注册
      </el-button>

      <!-- 登录链接 -->
      <div class="login-link">
        已经有账号了？
        <el-link type="primary" :underline="true" @click="goToLogin">登录</el-link>
      </div>
    </el-form>
  </auth-card>
</template>

<style scoped>
.register-button {
  width: 100%;
  margin-bottom: 16px;
}

.login-link {
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

:deep(.el-checkbox__label) {
  color: #606266;
}
</style> 