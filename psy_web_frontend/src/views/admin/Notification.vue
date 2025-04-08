<template>
    <BaseLayout>
        <div class="notification-container">
            <!-- 发送通知表单 -->
            <div class="form-container">
                <el-form :model="notificationForm" ref="formRef" :rules="rules">
                    <!-- 标题 -->
                    <el-form-item prop="title" label="标题：">
                        <el-input v-model="notificationForm.title" placeholder="请输入标题" />
                    </el-form-item>

                    <!-- 收件人邮箱 -->
                    <el-form-item prop="email" label="收件人邮箱：">
                        <el-input v-model="notificationForm.email" placeholder="请输入收件人邮箱" />
                    </el-form-item>

                    <!-- 内容 -->
                    <el-form-item prop="content" label="内容：">
                        <el-input v-model="notificationForm.content" type="textarea" :rows="8" placeholder="请输入" />
                    </el-form-item>

                    <!-- 发送按钮 -->
                    <el-form-item>
                        <el-button type="primary" @click="handleSubmit(formRef)">发送</el-button>
                    </el-form-item>
                </el-form>
            </div>

            <!-- 已发送列表 -->
            <div class="sent-container">
                <h3 class="sent-title">已发送：</h3>
                <el-table :data="sentList" style="width: 100%">
                    <el-table-column prop="recipient" label="收件人" />
                    <el-table-column prop="title" label="标题" />
                    <el-table-column prop="sendTime" label="发送时间" width="180" />
                </el-table>

                <!-- 分页 -->
                <div class="pagination">
                    <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10]"
                        :total="total" layout="total, prev, pager, next, jumper" @size-change="handleSizeChange"
                        @current-change="handleCurrentChange" />
                </div>
            </div>
        </div>
    </BaseLayout>
</template>

<script setup>
import { ref, reactive } from 'vue'
import BaseLayout from '@/components/layout/BaseLayout.vue'
import { ElMessage } from 'element-plus'

// 表单数据
const notificationForm = reactive({
    title: '',
    email: '',
    content: ''
})

// 表单规则
const rules = {
    title: [
        { required: true, message: '请输入标题', trigger: 'blur' }
    ],
    email: [
        { required: true, message: '请输入收件人邮箱', trigger: 'blur' },
        { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
    ],
    content: [
        { required: true, message: '请输入内容', trigger: 'blur' }
    ]
}

const formRef = ref(null)

// 已发送列表数据
const sentList = ref([
    {
        recipient: 'Apple',
        title: '11:11:11',
        sendTime: '12.09.2019'
    }
])

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(23)

// 处理表单提交
const handleSubmit = async (formEl) => {
    if (!formEl) return
    await formEl.validate((valid) => {
        if (valid) {
            // TODO: 发送通知的API调用
            ElMessage.success('发送成功')
            // 重置表单
            formEl.resetFields()
            // 刷新列表
            fetchSentList()
        }
    })
}

// 获取已发送列表
const fetchSentList = async () => {
    try {
        // TODO: 获取已发送列表的API调用
        // const { data } = await getNotificationList({
        //     page: currentPage.value,
        //     pageSize: pageSize.value
        // })
        // sentList.value = data.list
        // total.value = data.total
    } catch (error) {
        console.error('获取已发送列表失败：', error)
        ElMessage.error('获取已发送列表失败')
    }
}

// 分页处理
const handleSizeChange = (val) => {
    pageSize.value = val
    fetchSentList()
}

const handleCurrentChange = (val) => {
    currentPage.value = val
    fetchSentList()
}
</script>

<style scoped>
.notification-container {
    display: flex;
    flex-direction: column;
    gap: 20px;
    height: 100%;
    background-color: #f6f6f9;
}

.form-container {
    background: white;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.sent-container {
    background: white;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
    flex: 1;
    display: flex;
    flex-direction: column;
}

.sent-title {
    font-size: 16px;
    font-weight: 500;
    color: #333;
    margin-bottom: 20px;
}

.pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
}

:deep(.el-form-item__label) {
    font-weight: normal;
}

:deep(.el-input__wrapper) {
    background-color: #f6f6f9;
}
</style>