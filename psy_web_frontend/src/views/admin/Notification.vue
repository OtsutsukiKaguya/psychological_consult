<template>
    <BaseLayout>
        <div class="notification-container">
            <!-- 发送通知表单 -->
            <div class="notification-form">
                <div class="form-item">
                    <div class="label">标题：</div>
                    <el-input v-model="form.notificationTitle" placeholder="请输入标题" />
                </div>
                <div class="form-item">
                    <div class="label">接收者ID：</div>
                    <el-input v-model="form.receiverIds" placeholder="请输入接收者ID，多个ID用逗号分隔" />
                </div>
                <div class="form-item">
                    <div class="label">内容：</div>
                    <el-input v-model="form.notificationContent" type="textarea" :rows="8" placeholder="请输入内容" />
                </div>
                <el-button type="primary" class="send-button" @click="handleSend">发送</el-button>
            </div>

            <!-- 已发送列表 -->
            <div class="sent-list">
                <div class="title">已发送：</div>
                <div class="list-content">
                    <el-table :data="tableData" style="width: 100%">
                        <el-table-column prop="notificationTitle" label="标题" />
                        <el-table-column prop="notificationContent" label="内容" />
                        <el-table-column prop="notificationTime" label="发送时间" />
                        <el-table-column prop="receiverId" label="接收者" align="center" min-width="120">
                            <template #default="scope">
                                <span>{{ scope.row.receiverId }}</span>
                            </template>
                        </el-table-column>
                    </el-table>
                </div>

                <!-- 分页 -->
                <div class="pagination">
                    <div class="pagination-info">
                        前往 <el-input v-model="currentPage" class="page-input" /> 页
                        每页 <el-select v-model="pageSize" class="page-select">
                            <el-option :value="10" label="10" />
                        </el-select> 条
                    </div>
                    <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :total="total"
                        layout="prev, pager, next" background />
                </div>
            </div>
        </div>
    </BaseLayout>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import BaseLayout from '@/components/layout/BaseLayout.vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { API } from '@/config'

// 表单数据
const form = ref({
    notificationTitle: '',
    senderId: '',
    notificationContent: '',
    pictureLink: null,
    receiverIds: ''  // 改为单个输入框，用逗号分隔多个ID
})

// 表单验证规则
const rules = {
    notificationTitle: [{ required: true, message: '请输入标题', trigger: 'blur' }],
    notificationContent: [{ required: true, message: '请输入内容', trigger: 'blur' }],
    receiverIds: [{ required: true, message: '请输入接收者ID', trigger: 'blur' }]
}

// 获取当前用户信息
const currentUser = computed(() => {
    const userInfo = localStorage.getItem('userInfo')
    return userInfo ? JSON.parse(userInfo) : null
})

// 发送通知
const handleSend = async () => {
    // 验证必填字段
    if (!form.value.notificationTitle.trim() || !form.value.receiverIds || !form.value.notificationContent.trim()) {
        ElMessage.warning('请填写完整信息')
        return
    }

    try {
        // 准备请求数据，按照指定顺序排列参数
        const requestData = {
            notificationTitle: form.value.notificationTitle,
            senderId: currentUser.value?.id,
            notificationContent: form.value.notificationContent,
            pictureLink: form.value.pictureLink || null,
            receiverIds: form.value.receiverIds.split(',').map(id => id.trim())
        }

        // 发送请求
        const response = await axios.post(API.NOTIFICATION.SEND, requestData)
        console.log('发送通知返回数据:', response.data)

        // 清空表单
        form.value = {
            notificationTitle: '',
            senderId: '',
            notificationContent: '',
            pictureLink: null,
            receiverIds: ''
        }

        // 显示成功消息
        ElMessage.success('发送成功')

        // 刷新已发送列表
        await fetchSentList()
    } catch (error) {
        console.error('发送失败:', error)
        console.log('错误响应数据:', error.response?.data)
        ElMessage.error('发送失败')
    }
}

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const tableData = ref([])

// 获取已发送列表
const fetchSentList = async () => {
    try {
        const response = await axios.get(API.NOTIFICATION.GET_SENT_LIST(currentUser.value?.id))
        console.log('接口返回数据：', response.data)
        if (response.data.code === 0) {
            tableData.value = response.data.data
            allNotifications.value = response.data.data
            total.value = response.data.data.length
        } else {
            ElMessage.error('获取已发送列表失败')
        }
    } catch (error) {
        console.error('获取已发送列表失败:', error)
        ElMessage.error('获取已发送列表失败')
    }
}

// 监听分页变化
watch([currentPage, pageSize], () => {
    // 由于后端没有分页，我们在前端进行分页处理
    const start = (currentPage.value - 1) * pageSize.value
    const end = start + pageSize.value
    tableData.value = allNotifications.value.slice(start, end)
})

// 所有通知数据
const allNotifications = ref([])

// 计算当前页数据
const paginatedData = computed(() => {
    const start = (currentPage.value - 1) * pageSize.value
    const end = start + pageSize.value
    return allNotifications.value.slice(start, end)
})

// 组件挂载时获取列表
onMounted(async () => {
    await fetchSentList()
})

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
    height: 100%;
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.notification-form {
    background: #fff;
    padding: 20px;
    border-radius: 8px;
}

.form-item {
    margin-bottom: 20px;
}

.label {
    font-size: 14px;
    color: #333;
    margin-bottom: 8px;
}

.send-button {
    --el-button-bg-color: #0040D6;
    --el-button-border-color: #0040D6;
    --el-button-hover-bg-color: #335EE6;
    --el-button-hover-border-color: #335EE6;
    width: 80px;
}

.sent-list {
    background: #fff;
    padding: 20px;
    border-radius: 8px;
    flex: 1;
    display: flex;
    flex-direction: column;
}

.title {
    font-size: 14px;
    color: #333;
    margin-bottom: 20px;
}

.list-content {
    flex: 1;
}

.pagination {
    margin-top: 20px;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.pagination-info {
    display: flex;
    align-items: center;
    gap: 8px;
    color: #333;
    font-size: 14px;
}

.page-input {
    width: 50px;
    margin: 0 8px;
}

.page-select {
    width: 70px;
    margin: 0 8px;
}

:deep(.el-input__wrapper) {
    background-color: #fff;
}

:deep(.el-textarea__inner) {
    background-color: #fff;
}

:deep(.el-pagination) {
    justify-content: flex-end;
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