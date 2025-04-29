<!-- 登录页面 -->
<script setup>
import { ref, reactive } from 'vue'
import AuthCard from '../../components/auth/AuthCard.vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { API } from '@/config'

const router = useRouter()

// 表单数据
const loginForm = reactive({
  id: '',
  password: '',
  remember: false
})

// 表单规则
const rules = {
  id: [
    { required: true, message: '请输入ID', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

// 表单引用
const formRef = ref(null)

// 获取当前格式化的时间
const getCurrentFormattedTime = () => {
  const now = new Date()
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const day = String(now.getDate()).padStart(2, '0')
  const hours = String(now.getHours()).padStart(2, '0')
  const minutes = String(now.getMinutes()).padStart(2, '0')
  const seconds = String(now.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

// 登录方法
const handleLogin = async (formEl) => {
  if (!formEl) return
  await formEl.validate(async (valid) => {
    if (valid) {
      try {
        // 准备登录参数
        const loginData = {
          id: loginForm.id,
          password: loginForm.password,
          lastLoginTime: getCurrentFormattedTime()
        }

        // 打印登录参数
        console.log('登录参数：', loginData)

        // 发送POST请求，参数通过query string传递
        const response = await axios.post(API.AUTH.LOGIN, null, {
          params: loginData
        })

        console.log('登录返回数据：', response.data)

        if (response.data.code === 0) {
          const userData = response.data.data[0]

          // 确保角色名称与路由守卫中的一致
          const roleMap = {
            'ADMIN': 'ADMIN',
            'COUNSELOR': 'consultant',
            'TUTOR': 'supervisor'
          }

          // 更新用户角色
          userData.role = roleMap[userData.role] || userData.role

          // 存储用户信息
          localStorage.setItem('userInfo', JSON.stringify({
            id: userData.id,
            role: userData.role,
            name: userData.name,
            email: userData.email,
            gender: userData.gender,
            idcard: userData.idcard,
            age: userData.age,
            phone: userData.phone,
            lastLoginTime: userData.lastLoginTime,
            state: userData.state,
            selfDescription: userData.selfDescription,
            idPictureLink: userData.idPictureLink
          }))

          // 根据角色跳转到不同页面
          switch (userData.role) {
            case 'ADMIN':
              router.push('/admin/dashboard')
              break
            case 'consultant':
              router.push('/consultant/dashboard')
              break
            case 'supervisor':
              router.push('/supervisor/dashboard')
              break
            default:
              ElMessage.error('未知的用户角色')
              return
          }

          ElMessage.success('登录成功')
        } else {
          ElMessage.error(response.data.message || '登录失败')
        }
      } catch (error) {
        console.error('登录失败:', error)
        ElMessage.error('登录失败，请检查网络连接')
      }
    }
  })
}

// 跳转到注册页面
const goToRegister = () => {
  router.push('/register')
}
</script>

<template>
  <auth-card title="登录" subtitle="请输入您的ID和密码以登录">
    <el-form ref="formRef" :model="loginForm" :rules="rules" label-position="top" @keyup.enter="handleLogin(formRef)">
      <!-- ID输入框 -->
      <el-form-item prop="id" label="ID">
        <el-input v-model="loginForm.id" placeholder="请输入ID" size="large" />
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
      <el-button type="primary" class="login-button" @click="handleLogin(formRef)">
        登录
      </el-button>

      <!-- 注册链接 -->
      <div class="register-link">
        还没有账号？
        <el-link type="primary" :underline="true" @click="goToRegister">立即注册</el-link>
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
  height: 40px;
  margin-bottom: 16px;
  background-color: #557ff7;
  border: none;
}

.login-button:hover {
  background-color: #4a6fd9;
}

.register-link {
  text-align: center;
  font-size: 14px;
  color: #666;
}
</style>