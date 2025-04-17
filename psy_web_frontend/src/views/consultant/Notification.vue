<template>
    <ConsultantBaseLayout>
        <NotificationForm />
    </ConsultantBaseLayout>
</template>

<script setup>
import ConsultantBaseLayout from '@/components/layout/ConsultantBaseLayout.vue'
import NotificationForm from '@/components/notification/NotificationForm.vue'
</script>

<style scoped>
.notification-container {
    padding: 24px;
    background-color: #f5f7fa;
    /* 页面内容区域的浅色背景 */
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
    /* 卡片的白色背景 */
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    flex-grow: 1;
    /* 卡片需要增长以填充空间 */
    display: flex;
    flex-direction: column;
    overflow: hidden;
    /* 确保内容遵守卡片边界 */
    margin-bottom: 20px;
    /* 在卡片下方添加边距以与分页分隔 */
}

.notification-list {
    flex-grow: 1;
    overflow-y: auto;
    /* margin-bottom 已移除，因为分页在外部 */
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
    /* 未读为红色 */
    border-radius: 50%;
    flex-shrink: 0;
}

.dot.read {
    background-color: #d9d9d9;
    /* 已读为灰色 */
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
    /* 确保日期有足够空间 */
}

/* 调整按钮样式以匹配 UI */
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
    /* 移除 margin-top:auto */
    padding-top: 0;
    /* 移除 padding-top */
    display: flex;
    justify-content: flex-end;
    /* 将分页对齐到右侧 */
}

/* 通知列表的自定义滚动条 */
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