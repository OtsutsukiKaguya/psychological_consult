<template>
    <div class="notification-container">
        <h2 class="page-title">通知列表</h2>
        <div class="content-card">
            <div class="notification-list">
                <div v-for="notification in paginatedData" :key="notification.notificationId" class="notification-item">
                    <div class="notification-content">
                        <div class="dot"></div>
                        <div>
                            <h4 class="notification-title">{{ notification.notificationTitle }}</h4>
                            <p>{{ notification.notificationContent }}</p>
                        </div>
                    </div>
                    <div class="notification-meta">
                        <span class="date">{{ notification.notificationTime }}</span>
                        <span class="sender">发送者：{{ notification.senderId }}</span>
                    </div>
                </div>
            </div>
        </div>
        <div class="pagination-container">
            <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :total="total"
                :page-sizes="[10, 20, 30]" layout="total, sizes, prev, pager, next" background
                @size-change="handleSizeChange" @current-change="handleCurrentChange" />
        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { API } from '@/config'
import { useRoute } from 'vue-router'

const route = useRoute()

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const notifications = ref([])

// 获取当前登录用户的userId
const getCurrentUserId = () => {
    const userInfo = localStorage.getItem('userInfo')
    if (!userInfo) {
        ElMessage.warning('未获取到用户信息')
        return null
    }
    const user = JSON.parse(userInfo)
    return user.id
}

// 获取通知列表
const fetchNotifications = async () => {
    try {
        const userId = getCurrentUserId()
        if (!userId) {
            return
        }
        const response = await axios.get(API.NOTIFICATION.GET_RECEIVED_LIST(userId))
        console.log('接口返回数据：', response.data)
        if (response.data.code === 0) {
            notifications.value = response.data.data
            total.value = response.data.data.length
        } else {
            ElMessage.error('获取通知列表失败')
        }
    } catch (error) {
        console.error('获取通知列表失败:', error)
        ElMessage.error('获取通知列表失败')
    }
}

// 处理分页
const paginatedData = computed(() => {
    const start = (currentPage.value - 1) * pageSize.value
    const end = start + pageSize.value
    return notifications.value.slice(start, end)
})

// 处理页码变化
const handleCurrentChange = (val) => {
    currentPage.value = val
}

// 处理每页条数变化
const handleSizeChange = (val) => {
    pageSize.value = val
    currentPage.value = 1
}

// 组件挂载时获取数据
onMounted(async () => {
    await fetchNotifications()
})
</script>

<style scoped>
.notification-container {
    padding: 24px;
    background-color: #f5f7fa;
    height: 100%;
    display: flex;
    flex-direction: column;
}

.page-title {
    font-size: 20px;
    font-weight: bold;
    color: #333;
    margin-bottom: 20px;
}

.content-card {
    background-color: #fff;
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    flex-grow: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    margin-bottom: 20px;
}

.notification-list {
    flex-grow: 1;
    overflow-y: auto;
}

.notification-item {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    padding: 15px 0;
    border-bottom: 1px solid #f0f0f0;
}

.notification-item:last-child {
    border-bottom: none;
}

.notification-content {
    display: flex;
    align-items: flex-start;
    gap: 12px;
    flex: 1;
}

.dot {
    width: 8px;
    height: 8px;
    background-color: #f5222d;
    border-radius: 50%;
    flex-shrink: 0;
    margin-top: 8px;
}

.notification-title {
    margin: 0 0 8px 0;
    font-size: 16px;
    color: #333;
}

.notification-content p {
    margin: 0;
    color: #666;
    font-size: 14px;
}

.notification-meta {
    display: flex;
    flex-direction: column;
    align-items: flex-end;
    gap: 8px;
    min-width: 150px;
}

.date {
    font-size: 14px;
    color: #999;
}

.sender {
    font-size: 14px;
    color: #666;
}

.pagination-container {
    display: flex;
    justify-content: flex-end;
    padding: 16px 0;
}

/* 自定义滚动条样式 */
.notification-list::-webkit-scrollbar {
    width: 6px;
}

.notification-list::-webkit-scrollbar-track {
    background: transparent;
}

.notification-list::-webkit-scrollbar-thumb {
    background-color: #ccc;
    border-radius: 3px;
}
</style>