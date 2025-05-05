<!-- 咨询记录页面 -->
<script setup>
import ConsultantBaseLayout from '@/components/layout/ConsultantBaseLayout.vue'
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { API, CHAT_BASE_URL } from '@/config'
import { ElMessageBox, ElMessage } from 'element-plus'

const router = useRouter()

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)

// 计算当前页的数据
const currentPageData = computed(() => {
    let filtered = tableData.value
    if (searchForm.value.date) {
        filtered = filtered.filter(item => item.date === searchForm.value.date)
    }
    if (searchForm.value.name) {
        filtered = filtered.filter(item => item.visitor && item.visitor.includes(searchForm.value.name))
    }
    const start = (currentPage.value - 1) * pageSize.value
    const end = start + pageSize.value
    return filtered.slice(start, end)
})

// 搜索表单
const searchForm = ref({
    name: '',
    date: ''
})

// 静态数据
const tableData = ref([])

// 获取当前用户信息
const currentUser = computed(() => {
    const userInfo = localStorage.getItem('userInfo')
    return userInfo ? JSON.parse(userInfo) : null
})

// 获取咨询记录数据
const fetchRecords = async () => {
    let params = {}
    if (!currentUser.value) return
    if (currentUser.value.role === 'consultant') {
        params.counselorId = currentUser.value.id
    } else if (currentUser.value.role === 'supervisor') {
        params.supervisorId = currentUser.value.id
    }
    try {
        const res = await axios.get(`${CHAT_BASE_URL}/api/sessions/records`, { params })
        console.log('接口原始返回：', res)
        console.log('接口返回data：', res.data)
        if (res.data && Array.isArray(res.data.data)) {
            tableData.value = res.data.data.map(item => ({
                sessionId: item.sessionId,
                visitor: item.visitorName || '-',
                duration: item.duration || '-',
                date: item.date || '-',
                rating: item.rating,
                comment: item.userComment || '-'
            }))
        } else if (Array.isArray(res.data)) {
            tableData.value = res.data.map(item => ({
                sessionId: item.sessionId,
                visitor: item.visitorName || '-',
                duration: item.duration || '-',
                date: item.date || '-',
                rating: item.rating,
                comment: item.userComment || '-'
            }))
        } else {
            tableData.value = []
        }
    } catch (e) {
        tableData.value = []
    }
}

onMounted(() => {
    fetchRecords()
})

// 处理查看详情按钮点击
const handleViewDetail = (row) => {
    router.push({
        name: 'consultationDetail',
        params: { id: row.sessionId }
    })
}

const handleDateChange = () => {
    currentPage.value = 1
}

const handleNameInput = () => {
    currentPage.value = 1
}

const handleExportRecord = async (row) => {
    try {
        const { value: format } = await ElMessageBox.prompt(
            '请选择导出格式（txt、csv、pdf）',
            '导出咨询记录',
            {
                confirmButtonText: '导出',
                cancelButtonText: '取消',
                inputPlaceholder: '请输入格式（txt、csv、pdf）',
                inputValue: 'txt',
                inputValidator: (val) => ['txt', 'csv', 'pdf'].includes(val),
                inputErrorMessage: '格式只能是txt、csv或pdf'
            }
        )
        const sessionId = row.sessionId
        if (!sessionId) {
            ElMessage.error('未获取到sessionId')
            return
        }
        const url = `${CHAT_BASE_URL}/api/sessions/${sessionId}/export?format=${format}`
        const response = await axios.get(url, { responseType: 'blob' })
        // 获取文件名
        let filename = `consultation_${sessionId}.${format}`
        const disposition = response.headers['content-disposition']
        if (disposition) {
            const match = disposition.match(/filename="(.+)"/)
            if (match) filename = decodeURIComponent(match[1])
        }
        // 下载
        const blob = new Blob([response.data])
        const link = document.createElement('a')
        link.href = window.URL.createObjectURL(blob)
        link.download = filename
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(link.href)
        ElMessage.success('导出成功')
    } catch (e) {
        if (e !== 'cancel') ElMessage.error('导出失败')
    }
}
</script>

<template>
    <ConsultantBaseLayout>
        <div class="consultation-container">
            <!-- 搜索区域 -->
            <div class="search-area">
                <div class="search-item">
                    <div class="label">搜索姓名</div>
                    <el-input v-model="searchForm.name" placeholder="输入姓名进行搜索" clearable @input="handleNameInput" />
                </div>
                <div class="search-item">
                    <div class="label">选择日期</div>
                    <el-date-picker v-model="searchForm.date" type="date" placeholder="请选择日期" format="YYYY-MM-DD"
                        value-format="YYYY-MM-DD" @change="handleDateChange" />
                </div>

            </div>

            <!-- 表格区域 -->
            <div class="table-area">
                <el-table :data="currentPageData" style="width: 100%" height="600">
                    <el-table-column prop="visitor" label="咨询人" width="150" />
                    <el-table-column prop="duration" label="咨询时长" width="150" />
                    <el-table-column prop="date" label="咨询日期" width="150" />
                    <el-table-column label="咨询评级" width="200">
                        <template #default="{ row }">
                            <el-rate v-model="row.rating" :colors="['#ffd21e', '#ffd21e', '#ffd21e']" :allow-half="true"
                                show-score score-template="{value} points" disabled />
                        </template>
                    </el-table-column>
                    <el-table-column label="评价" prop="comment" min-width="200" />
                    <el-table-column label="操作" width="200">
                        <template #default="{ row }">
                            <div class="button-group">
                                <el-button type="success" size="small" class="detail-button"
                                    @click="handleViewDetail(row)">查看详情</el-button>
                                <el-button type="danger" size="small" class="record-button"
                                    @click="handleExportRecord(row)">导出记录</el-button>
                            </div>
                        </template>
                    </el-table-column>
                </el-table>
            </div>

            <!-- 分页 -->
            <div class="pagination">
                <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10]"
                    :total="15" :pager-count="7" layout="prev, pager, next, ->, total, sizes, jumper" background />
            </div>
        </div>
    </ConsultantBaseLayout>
</template>

<style scoped>
.consultation-container {
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

.detail-button {
    margin-right: 8px;
    --el-button-bg-color: #8DC99D;
    --el-button-border-color: #8DC99D;
    --el-button-hover-bg-color: #7eb98d;
    --el-button-hover-border-color: #7eb98d;
    border-radius: 20px;
    padding: 8px 16px;
    height: 32px;
    line-height: 1;
}

.record-button {
    --el-button-bg-color: #F1908C;
    --el-button-border-color: #F1908C;
    --el-button-hover-bg-color: #e17f7a;
    --el-button-hover-border-color: #e17f7a;
    border-radius: 20px;
    padding: 8px 16px;
    height: 32px;
    line-height: 1;
}

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

:deep(.el-rate) {
    display: flex;
    align-items: center;
}

:deep(.el-rate__icon) {
    width: 20px;
    height: 20px;
    font-size: 20px;
    margin-right: 6px;
}

:deep(.el-rate__icon:last-child) {
    margin-right: 0;
}

:deep(.el-icon) {
    font-size: 20px;
}

:deep(.el-rate__text) {
    font-size: 14px;
    color: #FF9F40;
    margin-left: 8px;
}

:deep(.el-input__wrapper) {
    width: 200px;
}

:deep(.el-date-editor.el-input) {
    width: 200px;
}
</style>