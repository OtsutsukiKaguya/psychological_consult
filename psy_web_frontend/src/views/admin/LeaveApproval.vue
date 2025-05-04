<!-- 请假审批页面 -->
<template>
    <BaseLayout>
        <div class="leave-approval-container">

            <!-- 请假列表 -->
            <div class="leave-list">
                <el-table :data="leaveList" style="width: 100%" border>
                    <el-table-column prop="applicant" label="申请人" width="120" />
                    <el-table-column prop="role" label="角色" width="100">
                        <template #default="scope">
                            {{ scope.row.role === 'COUNSELOR' ? '咨询师' : '督导' }}
                        </template>
                    </el-table-column>
                    <el-table-column prop="startDate" label="开始日期" width="120" />
                    <el-table-column prop="endDate" label="结束日期" width="120" />
                    <el-table-column prop="reason" label="请假原因" />
                    <el-table-column prop="status" label="状态" width="100">
                        <template #default="scope">
                            <el-tag :type="getStatusType(scope.row.status)">
                                {{ getStatusText(scope.row.status) }}
                            </el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" width="200">
                        <template #default="scope">
                            <el-button v-if="!scope.row.isAgree" type="primary" size="small"
                                @click="handleApprove(scope.row)">
                                批准
                            </el-button>
                            <el-button v-if="!scope.row.isAgree" type="danger" size="small"
                                @click="handleReject(scope.row)">
                                拒绝
                            </el-button>
                        </template>
                    </el-table-column>
                </el-table>
            </div>
        </div>
    </BaseLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import BaseLayout from '@/components/layout/BaseLayout.vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { API } from '@/config'

// 请假列表数据
const leaveList = ref([])

// 获取当前用户信息
const currentUser = computed(() => {
    const userInfo = localStorage.getItem('userInfo')
    return userInfo ? JSON.parse(userInfo) : null
})

// 获取请假列表
const fetchLeaveList = async () => {
    try {
        console.log('正在获取请假列表...')
        const response = await axios.get(API.LEAVE.SHOW_LEAVE)
        console.log('API响应:', response.data)
        if (response.data.code === 0) {
            // 转换数据格式
            leaveList.value = response.data.data.map(item => ({
                id: item.staffId,
                applicant: item.staffId,
                role: item.role,
                startDate: item.dutyDate,
                endDate: item.dutyDate,
                reason: item.leaveReason,
                status: item.isAgree ? 'APPROVED' : 'PENDING',
                isAgree: item.isAgree
            }))
            console.log('处理后的数据:', leaveList.value)
        }
    } catch (error) {
        console.error('获取请假列表失败:', error)
        ElMessage.error('获取请假列表失败')
    }
}

// 获取状态类型
const getStatusType = (status) => {
    switch (status) {
        case 'PENDING':
            return 'warning'
        case 'APPROVED':
            return 'success'
        case 'REJECTED':
            return 'danger'
        default:
            return 'info'
    }
}

// 获取状态文本
const getStatusText = (status) => {
    switch (status) {
        case 'PENDING':
            return '待审批'
        case 'APPROVED':
            return '已批准'
        case 'REJECTED':
            return '已拒绝'
        default:
            return '未知'
    }
}

// 发送通知
const sendNotification = async (staffId, dutyDate) => {
    try {
        const requestData = {
            notificationTitle: "您的请假审批已通过",
            senderId: currentUser.value?.id,
            notificationContent: `[${staffId}]您好，您在[${dutyDate}]的请假申请已经通过，祝您生活愉快！`,
            pictureLink: null,
            receiverIds: [staffId]
        }

        const response = await axios.post(API.NOTIFICATION.SEND, requestData)
        if (response.data.code === 0) {
            console.log('通知发送成功')
        } else {
            console.error('通知发送失败:', response.data.message)
        }
    } catch (error) {
        console.error('发送通知失败:', error)
    }
}

// 处理批准
const handleApprove = async (row) => {
    try {
        console.log('正在批准请假申请:', row)
        const params = {
            staffId: row.applicant,
            dutyDate: row.startDate,
            isAgree: true
        }
        console.log('请求参数:', params)
        const response = await axios.post(API.LEAVE.ADD_LEAVE_AGREE, null, { params })
        console.log('API响应:', response.data)
        if (response.data.code === 0) {
            ElMessage.success('已批准请假申请')
            // 发送通知
            await sendNotification(row.applicant, row.startDate)
            // 刷新列表
            await fetchLeaveList()
        } else {
            ElMessage.error(response.data.message || '操作失败')
        }
    } catch (error) {
        console.error('批准请假申请失败:', error)
        ElMessage.error('操作失败')
    }
}

// 处理拒绝
const handleReject = async (row) => {
    try {
        console.log('正在拒绝请假申请:', row)
        const params = {
            staffId: row.applicant,
            dutyDate: row.startDate,
            isAgree: false
        }
        console.log('请求参数:', params)
        const response = await axios.post(API.LEAVE.ADD_LEAVE_AGREE, null, { params })
        console.log('API响应:', response.data)
        if (response.data.code === 0) {
            ElMessage.error('已拒绝请假申请')
            // 刷新列表
            await fetchLeaveList()
        } else {
            ElMessage.error(response.data.message || '操作失败')
        }
    } catch (error) {
        console.error('拒绝请假申请失败:', error)
        ElMessage.error('操作失败')
    }
}

// 页面加载时获取数据
onMounted(() => {
    fetchLeaveList()
})
</script>

<style scoped>
.leave-approval-container {
    background-color: #f6f6f9;
    min-height: 100%;
}

.page-header h2 {
    font-size: 24px;
    color: #333;
    margin: 0;
}

.leave-list {
    background: white;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

:deep(.el-table) {
    border-radius: 8px;
    overflow: hidden;
}

:deep(.el-table th) {
    background-color: #f2f4f9;
    color: #333;
    font-weight: 500;
}


:deep(.el-table__fixed-right) th {
    background-color: #f2f4f9;
}

:deep(.el-table td) {
    padding: 12px 0;
}

:deep(.el-button) {
    margin-right: 8px;
}

:deep(.el-button:last-child) {
    margin-right: 0;
}
</style>