<template>
    <BaseLayout>
        <div class="page-container">
            <!-- 搜索区域 -->
            <div class="search-container">
                <div class="search-title">搜索</div>
                <el-input v-model="searchQuery" placeholder="输入ID或姓名进行搜索" class="search-input" clearable
                    @input="handleSearch" />
            </div>

            <!-- 表格区域 -->
            <div class="table-container">
                <el-table :data="currentTableData" style="width: 100%" v-loading="loading">
                    <!-- ID列（带头像） -->
                    <el-table-column label="id" min-width="200">
                        <template #default="{ row }">
                            <div class="name-cell">
                                <el-avatar :size="40" :src="row.avatar" />
                                <span class="name">{{ row.id }}</span>
                            </div>
                        </template>
                    </el-table-column>

                    <el-table-column prop="gender" label="性别" min-width="80" />
                    <el-table-column prop="username" label="用户名" min-width="120" />
                    <el-table-column prop="phone" label="联系电话" min-width="120" />
                    <el-table-column prop="emergencyContact" label="紧急联系人" min-width="120" />
                    <el-table-column prop="emergencyPhone" label="紧急联系人电话" min-width="120" />
                    <el-table-column prop="role" label="身份" min-width="100" />
                    <el-table-column prop="lastLoginTime" label="最近登录时间" min-width="160" />

                    <el-table-column label="操作" width="100" fixed="right">
                        <template #default="{ row }">
                            <el-button type="primary" :class="{ 'button-disabled': row.disabled }"
                                @click="handleUserStatus(row)">
                                {{ row.disabled ? '启用' : '禁用' }}
                            </el-button>
                        </template>
                    </el-table-column>
                </el-table>
            </div>

            <!-- 分页 -->
            <div class="pagination">
                <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10]"
                    :total="tableData.length" :pager-count="7" layout="prev, pager, next, ->, total, sizes, jumper"
                    background />
            </div>
        </div>
    </BaseLayout>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import BaseLayout from '../../components/layout/BaseLayout.vue'
import axios from 'axios'
import { API } from '../../config'

// 搜索相关
const searchQuery = ref('')
const handleSearch = () => {
    currentPage.value = 1
    const query = searchQuery.value.toLowerCase().trim()
    if (!query) {
        fetchData()
        return
    }

    // 本地过滤搜索结果
    const filteredData = tableData.value.filter(item =>
        (item.id && item.id.toString().toLowerCase().includes(query)) ||
        (item.username && item.username.toLowerCase().includes(query))
    )
    tableData.value = filteredData
}

// 表格数据
const loading = ref(false)
const tableData = ref([])

// 分页相关
const currentPage = ref(1)
const pageSize = ref(12)

// 表格数据计算属性
const currentTableData = computed(() => {
    const start = (currentPage.value - 1) * pageSize.value
    const end = start + pageSize.value
    return tableData.value.slice(start, end)
})

const handleSizeChange = (val: number) => {
    pageSize.value = val
    currentPage.value = 1
    fetchData()
}

const handleCurrentChange = (val: number) => {
    currentPage.value = val
    fetchData()
}

// 获取数据
const fetchData = async () => {
    loading.value = true
    try {
        const response = await axios.get(API.USER.SEARCH)
        if (response.data.code === 0) {
            // 处理返回的数据，映射字段名
            tableData.value = response.data.data.map(item => ({
                id: item.id,
                avatar: 'https://example.com/avatar.jpg', // 默认头像
                gender: item.gender || '-',
                username: item.name || '-',
                phone: item.phone || '-',
                emergencyContact: item.urgentName || '-',
                emergencyPhone: item.urgentPhone || '-',
                role: item.role || '-',
                lastLoginTime: item.lastLoginTime ? new Date(item.lastLoginTime).toLocaleString() : '-',
                disabled: false // 默认启用状态
            }))
        } else {
            ElMessage.error('获取数据失败')
        }
    } catch (error) {
        console.error('获取数据失败：', error)
        ElMessage.error('获取数据失败')
    } finally {
        loading.value = false
    }
}

// 处理用户状态变更
const handleUserStatus = async (row) => {
    try {
        // TODO: 调用API更新用户状态
        row.disabled = !row.disabled
        ElMessage.success(`已${row.disabled ? '禁用' : '启用'}该用户`)
    } catch (error) {
        console.error('操作失败：', error)
        ElMessage.error('操作失败')
    }
}

// 初始化
onMounted(() => {
    fetchData()
})
</script>

<style scoped>
.page-container {
    background-color: #f6f6f9;
    display: flex;
    flex-direction: column;
    height: 100%;
}

.search-container {
    margin-bottom: 20px;
    display: flex;
    align-items: flex-end;
    gap: 12px;
}

.search-title {
    font-size: 16px;
    color: #333;
    margin-bottom: 12px;
}

.search-input {
    width: 300px;
}

.table-container {
    flex: 1;
    background: white;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
    margin-bottom: 20px;
    height: 600px;
    overflow: auto;
}

.name-cell {
    display: flex;
    align-items: center;
    gap: 12px;
}

.name {
    color: #333;
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
    height: 100%;
    --el-table-header-bg-color: #f2f4f9;
    --el-table-border: none;
    --el-table-border-color: #EBEEF5;
    border: none;
    border-radius: 15px;
    overflow: hidden;
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

:deep(.el-table td.el-table__cell) {
    border-bottom: none;
    border-top: 1px solid #EBEEF5;
}

:deep(.el-table tr:first-child td.el-table__cell) {
    border-top: none;
}

:deep(.el-table__body-wrapper) {
    overflow-y: auto;
}

:deep(.el-button) {
    border-radius: 4px;
    padding: 8px 16px;
    font-size: 14px;
}

:deep(.el-button--primary) {
    background-color: #6777ef;
    border-color: #6777ef;
}

.button-disabled {
    background-color: #f56c6c !important;
    border-color: #f56c6c !important;
}
</style>