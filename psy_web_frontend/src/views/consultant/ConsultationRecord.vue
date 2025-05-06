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

const tableData = ref([])
const loading = ref(false)

// 计算当前页的数据
const currentPageData = computed(() => {
    let filtered = tableData.value
    if (searchForm.value.name) {
        filtered = filtered.filter(item => item.visitor && item.visitor.includes(searchForm.value.name))
    }
    if (searchForm.value.date) {
        filtered = filtered.filter(item => item.date === searchForm.value.date)
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

// 处理查看详情按钮点击
const handleViewDetail = (row) => {
    console.log('跳转详情页参数 row.date:', row.date)
    router.push({
        name: 'consultationDetail',
        params: { consultId: row.consultId },
        query: {
            groupSessionId: row.groupSessionId,
            oneToOneSessionId: row.oneToOneSessionId,
            date: row.date
        }
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
        const consultId = row.consultId
        if (!consultId) {
            ElMessage.error('未获取到consultId')
            return
        }
        const url = `${CHAT_BASE_URL}/api/sessions/export-by-consult?consultId=${consultId}&format=${format}`
        const response = await axios.get(url, { responseType: 'blob' })
        // 获取文件名
        let filename = `consultation_${consultId}.${format}`
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

onMounted(async () => {
    loading.value = true
    // 获取当前用户id
    let userId = ''
    try {
        const userInfo = localStorage.getItem('userInfo')
        if (userInfo) {
            userId = JSON.parse(userInfo).id
        }
    } catch { }
    if (!userId) {
        console.warn('未获取到当前用户id')
        loading.value = false
        return
    }
    try {
        const res = await axios.get(`${CHAT_BASE_URL}/api/sessions/by-user/${userId}`)
        const consultArr = Array.isArray(res.data) ? res.data : []
        // 并发请求所有GROUP类型的sessionId详情
        const groupSessions = []
        consultArr.forEach(item => {
            const group = item.sessions.find(s => s.sessionType === 'GROUP')
            if (group) {
                groupSessions.push({
                    consultId: item.consultId,
                    groupSessionId: group.sessionId,
                    oneToOneSessionId: (item.sessions.find(s => s.sessionType === 'ONE_TO_ONE') || {}).sessionId || null
                })
            }
        })
        // 请求所有GROUP session详情
        const detailResults = await Promise.all(groupSessions.map(async s => {
            try {
                const detailRes = await axios.get(`${CHAT_BASE_URL}/api/sessions/${s.groupSessionId}`)
                const detail = detailRes.data
                // 咨询人
                const user = (detail.participants || []).find(p => p.role === 'USER')
                // 是否督导
                const tutor = (detail.participants || []).find(p => p.role === 'TUTOR')
                // 咨询日期
                const date = detail.createdAt ? detail.createdAt.split('T')[0] : ''
                return {
                    consultId: s.consultId,
                    groupSessionId: s.groupSessionId,
                    oneToOneSessionId: s.oneToOneSessionId,
                    visitor: user ? user.name : '-',
                    date,
                    tutor: tutor ? tutor.name : '无'
                }
            } catch (e) {
                return null
            }
        }))
        tableData.value = detailResults.filter(Boolean)
    } catch (e) {
        console.error('获取by-user数据失败:', e)
    } finally {
        loading.value = false
    }
})
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
                <el-table :data="currentPageData" style="width: 100%" height="600" border v-loading="loading">
                    <el-table-column prop="visitor" label="咨询人" min-width="120" />
                    <el-table-column prop="date" label="咨询日期" min-width="120" />
                    <el-table-column prop="tutor" label="是否督导" min-width="120" />
                    <el-table-column label="操作" min-width="200" fixed="right">
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