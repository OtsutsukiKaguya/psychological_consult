<template>
    <SupervisorBaseLayout>
        <div class="schedule-container">
            <!-- 表格区域 -->
            <div class="table-area">
                <el-table :data="groupedSessions" style="width: 100%" height="600" v-loading="loading">
                    <el-table-column prop="counselorName" label="咨询师名字" width="200" />
                    <el-table-column prop="counselorId" label="咨询师id" width="200" />
                    <el-table-column label="督导咨询师-会话ID" width="350">
                        <template #default="{ row }">
                            {{ row.oneToOne?.sessionId || '-' }}
                        </template>
                    </el-table-column>
                    <el-table-column label="用户咨询师-会话ID" width="350">
                        <template #default="{ row }">
                            {{ row.group?.sessionId || '-' }}
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" width="150" align="center">
                        <template #default="{ row }">
                            <el-button type="success" size="small" class="detail-button"
                                @click="handleAssist(row)">开始协助</el-button>
                        </template>
                    </el-table-column>
                </el-table>
            </div>
            <!-- 分页 -->
            <div class="pagination">
                <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize"
                    :page-sizes="[10, 20, 30, 50]" :total="groupedSessions.length" :pager-count="7"
                    layout="prev, pager, next, ->, total, sizes, jumper" background />
            </div>
        </div>
    </SupervisorBaseLayout>
</template>

<script setup>
import SupervisorBaseLayout from '@/components/layout/SupervisorBaseLayout.vue'
import { ref, onMounted, computed, onUnmounted } from 'vue'
import axios from 'axios'
import { CHAT_BASE_URL } from '@/config'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

const loading = ref(false)
const groupedSessions = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const router = useRouter()
let timer = null
const firstLoad = ref(true)

const fetchSessions = async () => {
    if (firstLoad.value) loading.value = true
    try {
        const res = await axios.get(`${CHAT_BASE_URL}/api/statistics/ongoing-sessions`)
        if (res.data.code === 0 && Array.isArray(res.data.data)) {
            // 先分组
            const oneToOne = res.data.data.filter(item => item.type === 'ONE_TO_ONE')
            const group = res.data.data.filter(item => item.type === 'GROUP')
            // 用consultId分组（假设participantIds[0]为consultId）
            const groupMap = {}
            oneToOne.forEach(item => {
                const consultId = item.participantIds[0]
                if (!groupMap[consultId]) groupMap[consultId] = {}
                groupMap[consultId].oneToOne = item
            })
            group.forEach(item => {
                const consultId = item.participantIds[0]
                if (!groupMap[consultId]) groupMap[consultId] = {}
                groupMap[consultId].group = item
            })
            // 只展示有ONE_TO_ONE的分组
            groupedSessions.value = Object.entries(groupMap)
                .filter(([_, v]) => v.oneToOne)
                .map(([consultId, v]) => ({
                    consultId,
                    oneToOne: v.oneToOne,
                    group: v.group
                }))

            await Promise.all(groupedSessions.value.map(async (item) => {
                if (item.oneToOne?.sessionId) {
                    try {
                        const resp = await axios.get(`${CHAT_BASE_URL}/api/sessions/${item.oneToOne.sessionId}/participants`)
                        if (Array.isArray(resp.data)) {
                            const counselor = resp.data.find(u => u.role === 'COUNSELOR')
                            if (counselor) {
                                item.counselorName = counselor.name
                                item.counselorId = counselor.id
                                console.log('获取到的咨询师:', counselor.name, counselor.id)
                            }
                        }
                    } catch (e) { }
                }
            }))
        } else {
            groupedSessions.value = []
            ElMessage.error('获取会话数据失败')
        }
    } catch (e) {
        groupedSessions.value = []
        ElMessage.error('请求失败')
    } finally {
        if (firstLoad.value) {
            loading.value = false
            firstLoad.value = false
        }
    }
}

const handleAssist = (row) => {
    // 存储督导咨询师-会话ID和用户咨询师-会话ID到localStorage
    localStorage.setItem('supervisorSessionId', row.oneToOne?.sessionId || '')
    localStorage.setItem('userSessionId', row.group?.sessionId || '')
    // 跳转到 Chat 页面，参数为督导咨询师的 sessionId
    router.push(`/supervisor/chat/${row.oneToOne?.sessionId}`)
}

onMounted(() => {
    fetchSessions()
    timer = setInterval(fetchSessions, 3000)
})

onUnmounted(() => {
    if (timer) clearInterval(timer)
})
</script>

<style scoped>
.schedule-container {
    padding-left: 20px;
    padding-right: 20px;
    height: 100%;
    display: flex;
    flex-direction: column;
}

.table-area {
    padding: 20px;
    flex: 1;
    overflow-y: auto;
    background: #fff;
    border-radius: 8px;
    padding: 20px 0 20px 0;
}

.detail-button {
    margin-right: 0;
    --el-button-bg-color: #19c490;
    --el-button-border-color: #19c490;
    --el-button-hover-bg-color: #15a374;
    --el-button-hover-border-color: #15a374;
    --el-button-text-color: #ffffff;
    border-radius: 15px;
    padding: 6px 15px;
    height: auto;
    line-height: 1.2;
}

.pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
    padding: 0 0 20px 0;
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
</style>