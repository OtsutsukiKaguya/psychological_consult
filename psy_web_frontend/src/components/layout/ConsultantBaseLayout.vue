<template>
    <div class="layout-container">
        <!-- 左侧边栏 -->
        <div class="sidebar">
            <!-- 用户信息区域 -->
            <div class="user-info">
                <el-avatar :size="50" :src="userInfo.avatar" @click="handleAvatarClick" style="cursor:pointer;">
                    {{ userInfo.name?.charAt(0) }}
                </el-avatar>
                <input ref="fileInputRef" type="file" accept="image/*" style="display:none"
                    @change="handleFileUpload" />
                <div class="user-detail">
                    <div class="welcome">欢迎，</div>
                    <div class="role">咨询师{{ currentUser?.name }}</div>
                </div>
            </div>

            <!-- 菜单列表 -->
            <el-menu class="sidebar-menu" :default-active="activeMenu" @select="handleMenuClick">
                <el-menu-item v-for="item in menuItems" :key="item.path" :index="item.path">
                    <el-icon>
                        <component :is="item.icon" />
                    </el-icon>
                    <span>{{ item.label }}</span>
                </el-menu-item>
            </el-menu>

            <!-- 会话列表 -->
            <div class="conversation-list">
                <h3>会话列表</h3>
                <div class="conversation-item" v-for="conversation in conversations" :key="conversation.id"
                    :class="{ 'is-active': activeConversationId === conversation.id }"
                    @click="handleConversationClick(conversation)">
                    <el-avatar :size="40" :src="conversation.avatar" />
                    <span>{{ conversation.name }}</span>
                    <el-badge v-if="conversation.unread" :value="conversation.unread" class="unread-badge" />
                </div>
            </div>

            <div class="flex-spacer"></div>

            <!-- 退出登录 -->
            <div class="logout-section">
                <el-menu>
                    <el-menu-item index="logout" @click="handleLogout">
                        <el-icon>
                            <SwitchButton />
                        </el-icon>
                        <span>退出登录</span>
                    </el-menu-item>
                </el-menu>
            </div>
        </div>

        <!-- 右侧内容区 -->
        <div class="main-container">
            <!-- 顶部栏 -->
            <div class="header">
                <h1 class="page-title">心理学院热线咨询</h1>
            </div>

            <!-- 主要内容区域 -->
            <div class="main-content">
                <slot></slot>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, watch, computed, defineExpose } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { House, Document, Calendar, Bell, SwitchButton } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { CHAT_BASE_URL, BASE_URL } from '@/config'
import axios from 'axios'

// 路由
const router = useRouter()
const route = useRoute()

// 处理退出登录
const handleLogout = async () => {
    try {
        // 获取当前用户ID
        const userInfo = localStorage.getItem('userInfo')
        const userId = userInfo ? JSON.parse(userInfo).id : null

        if (userId) {
            // 调用退出登录接口
            await axios.post(`${BASE_URL}/api/quit`, null, {
                params: { id: userId }
            })
        }
    } catch (error) {
        console.error('退出登录接口调用失败:', error)
    } finally {
        // 清空本地存储的用户信息和token
        localStorage.removeItem('userInfo')
        localStorage.removeItem('token')
        // 跳转到登录页面
        router.push('/login')
        // 显示提示消息
        ElMessage.success('退出登录成功')
    }
}

// 菜单配置
const menuItems = [
    { icon: 'House', label: '首页', path: '/consultant/dashboard' },
    { icon: 'Document', label: '咨询记录', path: '/consultant/consultation' },
    { icon: Calendar, label: '预约情况', path: '/consultant/schedule' },
    { icon: 'Share', label: '树洞', path: '/consultant/tree-hole' },
    { icon: Bell, label: '通知', path: '/consultant/notification' }
]

// 用户信息
const fileInputRef = ref(null)
const userInfo = ref({
    avatar: '',
    name: '咨询师',
    role: '咨询师'
})

// 会话列表
const conversations = ref([])

// 当前激活的会话ID
const activeConversationId = ref(null)

function addConversation(conversation) {
    if (!conversations.value.find(c => c.id === conversation.id)) {
        conversations.value.push(conversation)
        localStorage.setItem('conversations', JSON.stringify(conversations.value))
    }
    activeConversationId.value = conversation.id
    localStorage.setItem('activeConversationId', conversation.id)
}

function removeConversation(conversationId) {
    const idx = conversations.value.findIndex(c => c.id === conversationId)
    if (idx !== -1) {
        conversations.value.splice(idx, 1)
        localStorage.setItem('conversations', JSON.stringify(conversations.value))
        // 如果当前激活的会话被删了，重置activeConversationId
        if (activeConversationId.value === conversationId) {
            activeConversationId.value = null
            localStorage.removeItem('activeConversationId')
        }
    }
}

defineExpose({ addConversation, removeConversation })

function handleConversationClick(conversation) {
    activeConversationId.value = conversation.id
    localStorage.setItem('activeConversationId', conversation.id)
    router.push(`/consultant/chat/${conversation.id}`)
}

// 在 setup 中添加监听路由变化
const updateActiveConversation = () => {
    const chatId = route.params.id
    if (route.path.includes('/consultant/chat/') && chatId) {
        activeConversationId.value = chatId
    } else {
        activeConversationId.value = null
    }
}

// 监听路由变化
onMounted(() => {
    // 每次挂载都同步一次
    conversations.value = JSON.parse(localStorage.getItem('conversations') || '[]')
    activeConversationId.value = localStorage.getItem('activeConversationId') || null
    updateActiveConversation()
    initUserInfo()
})

watch(() => route.params.id, () => {
    updateActiveConversation()
})

// 计算当前激活的菜单项
const activeMenu = computed(() => {
    // 如果是树洞详情页面，返回树洞列表的路径
    if (route.path.startsWith('/consultant/tree-hole/')) {
        return '/consultant/tree-hole'
    }
    return route.path
})

// 菜单点击处理
const handleMenuClick = (path) => {
    router.push(path)
}

// 获取当前用户信息
const currentUser = computed(() => {
    const info = localStorage.getItem('userInfo')
    return info ? JSON.parse(info) : null
})

// 欢迎文本
const welcomeText = computed(() => {
    return currentUser.value ? `欢迎，咨询师${currentUser.value.name}` : '欢迎'
})

// 初始化用户信息
const initUserInfo = () => {
    const info = localStorage.getItem('userInfo')
    if (info) {
        const parsed = JSON.parse(info)
        userInfo.value.avatar = parsed.idPictureLink || ''
        userInfo.value.name = parsed.name || '咨询师'
    }
}

// 头像点击
const handleAvatarClick = () => {
    if (fileInputRef.value) {
        fileInputRef.value.value = ''
        fileInputRef.value.click()
    }
}

// 上传头像
const handleFileUpload = async (e) => {
    const file = e.target.files[0]
    if (!file) return
    if (!file.type.startsWith('image/')) {
        ElMessage.error('请上传图片文件')
        return
    }
    const formData = new FormData()
    formData.append('file', file)
    try {
        const res = await axios.post(
            `${CHAT_BASE_URL}/api/files/upload`,
            formData,
            {
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`,
                    'Content-Type': 'multipart/form-data'
                }
            }
        )
        if (res.data && res.data.ossUrl) {
            // 更新本地 userInfo
            const info = JSON.parse(localStorage.getItem('userInfo') || '{}')
            info.idPictureLink = res.data.ossUrl
            localStorage.setItem('userInfo', JSON.stringify(info))
            userInfo.value.avatar = res.data.ossUrl
            // 同步数据库
            const params = {
                newid: info.id,
                id: info.id,
                email: info.email || '',
                phone: info.phone || '',
                selfDescription: info.selfDescription || '',
                idPictureLink: info.idPictureLink || ''
            }
            console.log('[person update] 请求参数:', params)
            try {
                const updateRes = await axios.post(`${BASE_URL}/api/person/update`, null, { params })
                console.log('[person update] 返回数据:', updateRes.data)
                ElMessage.success('头像上传并同步成功')
            } catch (e) {
                ElMessage.error('头像已上传，但信息同步失败')
            }
        } else {
            ElMessage.error('上传失败')
        }
    } catch (error) {
        ElMessage.error('上传失败')
    }
}
</script>

<style scoped>
.layout-container {
    display: flex;
    width: 100%;
    height: 100%;
    overflow: hidden;
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
}

/* 侧边栏样式 */
.sidebar {
    width: 230px;
    height: 100%;
    background-color: #fff;
    border-right: 1px solid #e6e6e6;
    display: flex;
    flex-direction: column;
    overflow-y: auto;
    position: fixed;
    top: 0;
    left: 0;
    bottom: 0;
}

.user-info {
    padding: 20px;
    padding-top: 0;
    display: flex;
    align-items: center;
    gap: 12px;
    border-bottom: 1px solid #f0f0f0;
}

.user-detail {
    display: flex;
    flex-direction: column;
}

.welcome {
    font-size: 14px;
    color: #666;
    padding: 16px;
}

.role {
    font-size: 16px;
    font-weight: 500;
    color: #333;
}

.sidebar-menu {
    border-right: none;
}

.conversation-list {
    border-top: 1px solid #e6e6e6;
}

.conversation-item {
    display: flex;
    align-items: center;
    padding-left: 35px !important;
    height: 60px;
    margin: 4px 20px;
    border-radius: 6px;
    background-color: transparent;
    transition: all 0.5s ease;
    cursor: pointer;
    gap: 12px;
    color: #333;
    width: calc(100% - 32px);
    /* 确保宽度与菜单项一致 */
}

.conversation-item .el-avatar {
    margin-right: 12px;
    font-size: 20px;
    width: 20px;
    height: 20px;
}

.conversation-item:hover {
    background-color: rgba(85, 127, 247, 0.8);
    color: #ffffff;
}

.conversation-item.is-active {
    background-color: #557ff7 !important;
    color: #ffffff !important;
    position: relative;
}

.conversation-item.is-active::before {
    content: '';
    position: absolute;
    left: -20px;
    top: 0;
    bottom: 0;
    width: 5px;
    background-color: #557ff7;
    border-radius: 0 10px 10px 0;
}

.conversation-item:hover :deep(.el-avatar),
.conversation-item.is-active :deep(.el-avatar) {
    color: #ffffff;
}

.unread-badge {
    margin-left: auto;
    margin-right: 16px;
}

.conversation-list h3 {
    color: #000;
    font-size: 15px;
    margin: 10px 0 12px 35px;
    font-weight: bold;
}

.flex-spacer {
    flex: 1;
}

.logout-section {
    border-top: 1px solid #f0f0f0;
}

/* 主容器样式 */
.main-container {
    flex: 1;
    height: 100%;
    background-color: #f5f7fa;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    min-width: 0;
    /* 防止内容溢出 */
    margin-left: 230px;
}

/* 顶部栏样式 */
.header {
    height: 64px;
    background-color: #fff;
    padding: 0 24px;
    display: flex;
    align-items: center;
    justify-content: flex-start;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}

.page-title {
    font-size: 20px;
    font-weight: 500;
    color: #333;
    margin: 0;
}

/* 主内容区域样式 */
.main-content {
    flex: 1;
    overflow-y: auto;
    background-color: #f5f7fa;
}

/* Element Plus 图标样式调整 */
.el-menu-item {
    display: flex;
    align-items: center;
    padding-left: 35px !important;
    height: 50px;
    /* 设置统一高度 */
    margin: 4px 16px;
    /* 添加外边距使其有空间显示圆角 */
    border-radius: 6px;
    /* 添加圆角 */
    background-color: transparent;
    transition: all 0.5s ease;
}

.el-menu-item .el-icon {
    margin-right: 12px;
    font-size: 20px;
    width: 20px;
    height: 20px;
}

:deep(.el-menu-item) {
    color: #333;
    transition: all 0.5s ease;
}

:deep(.el-menu-item.is-active) {
    background-color: #557ff7 !important;
    color: #ffffff !important;
    position: relative;
}

:deep(.el-menu-item.is-active::before) {
    content: '';
    position: absolute;
    left: -16px;
    top: 0;
    bottom: 0;
    width: 5px;
    background-color: #557ff7;
    border-radius: 0 10px 10px 0;
}

:deep(.el-menu-item.is-active .el-icon) {
    color: #ffffff !important;
}

:deep(.el-menu-item:hover) {
    background-color: rgba(85, 127, 247, 0.8);
    color: #ffffff;
}

:deep(.el-menu-item.is-active:hover) {
    background-color: #557ff7 !important;
    color: #ffffff !important;
}

:deep(.el-menu) {
    border-right: none;
}

/* 退出登录菜单项也需要同样的内边距 */
:deep(.logout-section .el-menu-item) {
    padding-left: 35px !important;
    margin: 4px 16px;
    border-radius: 6px;
    transition: all 0.5s ease;
}

:deep(.logout-section .el-menu-item .el-icon) {
    font-size: 20px;
    width: 20px;
    height: 20px;
}
</style>