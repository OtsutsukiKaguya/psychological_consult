<template>
    <ConsultantBaseLayout>
        <div class="notification-container">
            <h2 class="page-title">通知列表</h2>
            <div class="content-card">
                <div class="notification-list">
                    <div v-for="item in notifications" :key="item.id" class="notification-item">
                        <div class="notification-content">
                            <span :class="['dot', { 'read': item.read }]"></span>
                            <p>{{ item.content }}</p>
                        </div>
                        <div class="notification-meta">
                            <span class="date">{{ item.date }}</span>
                            <el-button type="success" size="small" plain @click="viewDetails(item.id)">
                                查看详情
                            </el-button>
                        </div>
                    </div>
                </div>
            </div>
            <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize"
                :page-sizes="[10, 20, 30, 50]" layout="total, sizes, prev, pager, next, jumper"
                :total="totalNotifications" @size-change="handleSizeChange" @current-change="handleCurrentChange"
                class="pagination-container" />
        </div>
    </ConsultantBaseLayout>
</template>

<script setup>
import { ref } from 'vue';
import ConsultantBaseLayout from '@/components/layout/ConsultantBaseLayout.vue';
import { ElPagination, ElButton } from 'element-plus';

// Mock data for notifications
const notifications = ref([
    { id: 1, content: '系统将于今晚10点进行维护，请提前保存工作。', date: '2023.12.10', read: false },
    { id: 2, content: '您有一条新的督导反馈，请查收。', date: '2023.12.09', read: false },
    { id: 3, content: '张三预约了明天下午3点的咨询，请确认。', date: '2023.12.08', read: true },
    { id: 4, content: '【重要】请更新您的个人信息以符合最新要求。', date: '2023.12.07', read: false },
    { id: 5, content: '本月度培训安排已发布，请查看。', date: '2023.12.06', read: true },
]);

// Pagination state
const currentPage = ref(1);
const pageSize = ref(10);
const totalNotifications = ref(notifications.value.length * 5); // Mock total count

// Pagination handlers
const handleSizeChange = (val) => {
    pageSize.value = val;
    // TODO: Fetch data for the new page size
    console.log(`每页 ${val} 条`);
};
const handleCurrentChange = (val) => {
    currentPage.value = val;
    // TODO: Fetch data for the new page
    console.log(`当前页: ${val}`);
};

// View details handler
const viewDetails = (id) => {
    console.log('查看通知详情:', id);
    // TODO: Navigate to detail page or show modal
    // Mark as read potentially
    const notification = notifications.value.find(n => n.id === id);
    if (notification) {
        notification.read = true;
    }
};
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