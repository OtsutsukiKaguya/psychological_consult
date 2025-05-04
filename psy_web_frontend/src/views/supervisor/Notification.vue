<template>
    <SupervisorBaseLayout>
        <NotificationForm />
    </SupervisorBaseLayout>
</template>

<script setup>
import SupervisorBaseLayout from '@/components/layout/SupervisorBaseLayout.vue'
import NotificationForm from '@/components/notification/NotificationForm.vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { ref } from 'vue'

const currentUser = ref(null)

// 标记通知为已读
const handleMarkAsRead = async (notification) => {
    try {
        const response = await axios.post(
            API.NOTIFICATION.READ(notification.id, currentUser.value?.id)
        )
        if (response.data.code === 0) {
            ElMessage.success('已标记为已读')
            // 更新本地数据状态
            notification.isRead = true
        } else {
            ElMessage.error('操作失败')
        }
    } catch (error) {
        console.error('标记已读失败:', error)
        ElMessage.error('操作失败')
    }
}
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
    align-items: center;
    padding: 15px 0;
    border-bottom: 1px solid #f0f0f0;
}

.notification-item:last-child {
    border-bottom: none;
}

.notification-content {
    display: flex;
    align-items: center;
    gap: 12px;
}

.dot {
    width: 8px;
    height: 8px;
    background-color: #f5222d;
    border-radius: 50%;
    flex-shrink: 0;
}

.dot.read {
    background-color: #d9d9d9;
}

.notification-content p {
    margin: 0;
    color: #555;
    font-size: 14px;
}

.notification-meta {
    display: flex;
    align-items: center;
    gap: 20px;
}

.date {
    font-size: 14px;
    color: #999;
    min-width: 80px;
}

.notification-meta .el-button--success.is-plain {
    color: #19c490;
    background: #fff;
    border-color: #b3e8d6;
}

.notification-meta .el-button--success.is-plain:hover {
    background: #19c490;
    border-color: #19c490;
    color: #fff;
}

.pagination-container {
    margin-top: 0;
    padding-top: 0;
    display: flex;
    justify-content: flex-end;
}

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

:deep(.el-table .unread-row) {
    position: relative;
}

:deep(.el-table .unread-row::before) {
    content: '';
    position: absolute;
    width: 6px;
    height: 6px;
    background-color: #f56c6c;
    border-radius: 50%;
    left: 4px;
    top: 50%;
    transform: translateY(-50%);
}
</style>