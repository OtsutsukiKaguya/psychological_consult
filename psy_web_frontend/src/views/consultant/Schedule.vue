<!-- 预约情况页面 -->
<script setup>
import ConsultantBaseLayout from '@/components/layout/ConsultantBaseLayout.vue'
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { API } from '@/config'
import { ElMessage } from 'element-plus'

const router = useRouter()

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)

// 搜索表单
const searchForm = ref({
    name: '',
    date: ''
})

// 预约数据
const tableData = ref([])
const loading = ref(false)

// 获取当前用户信息
const currentUser = computed(() => {
    const userInfo = localStorage.getItem('userInfo')
    return userInfo ? JSON.parse(userInfo) : null
})

const layoutRef = ref()

// 获取预约记录
const fetchReservations = async () => {
    if (!currentUser.value?.id) {
        ElMessage.error('获取用户信息失败')
        return
    }

    loading.value = true
    try {
        const response = await axios.get(`${API.RESERVATION.GET_COUNSELOR_RESERVATIONS}/${currentUser.value.id}`)
        console.log(response.data)
        if (response.data.code === 0) {
            // 并发查询所有预约人的昵称
            const userIdList = response.data.data.map(item => item.userId)
            const nameMap = {}
            await Promise.all(userIdList.map(async (userId) => {
                try {
                    const res = await axios.get(API.USER.SEARCH_PERSON(userId))
                    if (res.data.code === 0 && Array.isArray(res.data.data) && res.data.data.length > 0) {
                        nameMap[userId] = res.data.data[0].name
                    } else {
                        nameMap[userId] = userId
                    }
                } catch {
                    nameMap[userId] = userId
                }
            }))
            // 处理返回的数据
            tableData.value = response.data.data.map(item => ({
                id: item.id || '',
                visitor: item.userId,
                nickname: nameMap[item.userId] || item.userId,
                appointmentDate: item.reservationTime.split(' ')[0],
                appointmentTime: item.reservationTime.split(' ')[1],
                description: item.reservationDescription,
                details: '查看详情'
            }))
        } else {
            ElMessage.error(response.data.message || '获取预约记录失败')
        }
    } catch (error) {
        console.error('获取预约记录失败:', error)
        ElMessage.error('获取预约记录失败')
    } finally {
        loading.value = false
    }
}

// 计算当前页的数据
const currentPageData = computed(() => {
    const start = (currentPage.value - 1) * pageSize.value
    const end = start + pageSize.value
    return tableData.value.slice(start, end)
})

const totalItems = computed(() => tableData.value.length)

// 查看详情处理函数
const handleViewDetails = async (row) => {
    try {
        const res = await axios.post(API.SESSIONS.CREATE, {
            participantIds: [currentUser.value.id, row.visitor]
        })
        console.log('会话创建返回：', res.data)
        const sessionId = res.data.id
        const consultId = res.data.consultId
        localStorage.setItem(`sessionId-${row.visitor}`, sessionId)
        localStorage.setItem(`consultId-${row.visitor}`, consultId)
        layoutRef.value.addConversation({
            id: String(row.visitor),
            name: row.nickname,
            avatar: '',
            unread: 0
        })
        router.push(`/consultant/chat/${row.visitor}`)
    } catch (e) {
        ElMessage.error('创建会话失败')
        console.error(e)
    }
}

// 搜索处理
const handleSearch = () => {
    // 重置页码
    currentPage.value = 1
    // TODO: 实现搜索逻辑
}

// 页面加载时获取数据
onMounted(() => {
    fetchReservations()
})
</script>

<template>
    <ConsultantBaseLayout ref="layoutRef">
        <div class="schedule-container">
            <!-- 搜索区域 -->
            <div class="search-area">
                <div class="search-item">
                    <div class="label">搜索姓名</div>
                    <el-input v-model="searchForm.name" placeholder="输入姓名进行搜索" clearable @keyup.enter="handleSearch" />
                </div>
                <div class="search-item">
                    <div class="label">选择日期</div>
                    <el-date-picker v-model="searchForm.date" type="date" placeholder="请选择日期" format="YYYY-MM-DD"
                        value-format="YYYY-MM-DD" @change="handleSearch" />
                </div>
            </div>

            <!-- 表格区域 -->
            <div class="table-area">
                <el-table :data="currentPageData" style="width: 100%" height="600" v-loading="loading">
                    <el-table-column prop="visitor" label="咨询人ID" width="150" />
                    <el-table-column prop="nickname" label="昵称" width="150" />
                    <el-table-column prop="appointmentDate" label="预约日期" width="180" />
                    <el-table-column prop="appointmentTime" label="预约时间" width="180" />
                    <el-table-column prop="description" label="情况描述" min-width="250" />
                    <el-table-column label="操作" width="150" align="center">
                        <template #default="{ row }">
                            <el-button type="success" size="small" class="detail-button"
                                @click="handleViewDetails(row)">开始咨询</el-button>
                        </template>
                    </el-table-column>
                </el-table>
            </div>

            <!-- 分页 -->
            <div class="pagination">
                <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize"
                    :page-sizes="[10, 20, 30, 50]" :total="totalItems" :pager-count="7"
                    layout="prev, pager, next, ->, total, sizes, jumper" background />
            </div>
        </div>
    </ConsultantBaseLayout>
</template>

<style scoped>
/* 完全复制 ConsultationRecord.vue 的样式 */
.schedule-container {
    /* 使用新的类名以防样式冲突 */
    padding: 20px;
    height: 100%;
    display: flex;
    flex-direction: column;
}

.search-area {
    display: flex;
    align-items: flex-start;
    gap: 20px;
    margin-bottom: 20px;
}

.search-item {
    display: flex;
    flex-direction: column;
    gap: 8px;
}

.label {
    font-size: 14px;
    color: #333;
}

.export-button {
    /* 如果保留按钮，样式也保留 */
    margin-left: auto;
    background-color: #E9E9E9;
    border: none;
    color: #333;
}

.export-button:hover {
    background-color: #d9d9d9;
}

.table-area {
    flex: 1;
    overflow-y: auto;
    background: #fff;
    border-radius: 8px;
    padding: 20px;
}

.button-group {
    display: flex;
    align-items: center;
    white-space: nowrap;
}

/* 确保查看详情按钮样式正确 */
.detail-button {
    margin-right: 0;
    /* 如果只有一个按钮，移除右边距 */
    --el-button-bg-color: #19c490;
    /* 使用UI图中的绿色 */
    --el-button-border-color: #19c490;
    --el-button-hover-bg-color: #15a374;
    /* 悬停颜色变深 */
    --el-button-hover-border-color: #15a374;
    --el-button-text-color: #ffffff;
    /* 文字颜色为白色 */
    border-radius: 15px;
    /* 更圆的边角 */
    padding: 6px 15px;
    /* 调整内边距 */
    height: auto;
    /* 自适应高度 */
    line-height: 1.2;
}

/* 保持分页样式一致 */
.pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
    padding: 0 20px 20px 0;
}

:deep(.el-pagination) {
    justify-content: flex-end;
    white-space: nowrap;
}

:deep(.el-pagination .el-pager) {
    margin: 0 4px;
}

:deep(.el-pagination .el-pager li) {
    background-color: transparent;
    margin: 0 3px;
}

:deep(.el-pagination .el-pager li.active) {
    background-color: #557ff7;
}

:deep(.el-pagination .btn-prev),
:deep(.el-pagination .btn-next) {
    background-color: transparent;
}

/* 保持表格样式一致 */
:deep(.el-table) {
    --el-table-header-bg-color: #f2f4f9;
    --el-table-border: none;
    --el-table-border-color: #EBEEF5;
    border: none;
    border-radius: 15px;
    overflow: hidden;
}

:deep(.el-table td.el-table__cell) {
    border-bottom: 1px solid #EBEEF5;
}

:deep(.el-table tr:last-child td.el-table__cell) {
    border-bottom: none;
}

:deep(.el-table .cell) {
    padding: 0 12px;
}

:deep(.el-table td:first-child .cell) {
    padding-left: 17px;
}

:deep(.el-table th:first-child .cell) {
    padding-left: 17px;
}

:deep(.el-table th.el-table__cell) {
    border-top: none;
}

:deep(.el-table__header) {
    border-radius: 15px;
}

:deep(.el-table th) {
    font-weight: normal;
    color: #333;
    height: 60px;
    background-color: #f2f4f9;

    &:first-child {
        border-top-left-radius: 15px;
    }

    &:last-child {
        border-top-right-radius: 15px;
    }
}

:deep(.el-table__header-wrapper) {
    border-radius: 15px;
    overflow: hidden;
}

:deep(.el-table td) {
    height: 60px;
}

:deep(.el-table::before) {
    display: none;
}

:deep(.el-table__inner-wrapper::before) {
    display: none;
}

:deep(.el-table--enable-row-hover .el-table__body tr:hover > td) {
    background-color: #f4f9ff;
}

/* 保留可能需要的输入框和日期选择器样式 */
:deep(.el-input__wrapper) {
    width: 200px;
}

:deep(.el-date-editor.el-input) {
    width: 200px;
}

/* 移除 ConsultationRecord 特有的 Rate 样式 */
/* :deep(.el-rate) { ... } */
/* :deep(.el-rate__icon) { ... } */
/* :deep(.el-rate__text) { ... } */
</style>