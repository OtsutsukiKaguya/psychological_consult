<template>
    <BaseLayout>
        <div class="page-container">
            <!-- 搜索区域 -->
            <div class="search-container">
                <div class="search-title">搜索姓名</div>
                <el-input v-model="searchQuery" placeholder="输入姓名进行搜索" class="search-input" clearable
                    @input="handleSearch" />
            </div>

            <!-- 表格区域 -->
            <div class="table-container">
                <el-table :data="currentTableData" style="width: 100%" v-loading="loading">
                    <!-- 姓名列（带头像） -->
                    <el-table-column label="姓名" min-width="200">
                        <template #default="{ row }">
                            <div class="name-cell">
                                <el-avatar :size="40" :src="row.avatar" />
                                <span class="name">{{ row.name }}</span>
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

// 搜索相关
const searchQuery = ref('')
const handleSearch = () => {
    currentPage.value = 1
    fetchData()
}

// 表格数据
const loading = ref(false)
const tableData = ref([
    {
        id: 1,
        name: 'Apple',
        avatar: 'https://example.com/avatar1.jpg',
        gender: '女',
        username: 'wx123456789',
        phone: '18888888888',
        emergencyContact: 'Apple',
        emergencyPhone: '18888888888',
        role: '访客',
        lastLoginTime: '2022/1/1 20:00:00',
        disabled: false
    },
    {
        id: 2,
        name: '张三',
        avatar: 'https://example.com/avatar2.jpg',
        gender: '男',
        username: 'zhangsan123',
        phone: '13811112222',
        emergencyContact: '李四',
        emergencyPhone: '13822223333',
        role: '访客',
        lastLoginTime: '2022/1/2 15:30:00',
        disabled: true
    },
    {
        id: 3,
        name: '李四',
        avatar: 'https://example.com/avatar3.jpg',
        gender: '男',
        username: 'lisi456',
        phone: '13833334444',
        emergencyContact: '张三',
        emergencyPhone: '13811112222',
        role: '咨询师',
        lastLoginTime: '2022/1/3 09:15:00',
        disabled: false
    },
    {
        id: 4,
        name: '王五',
        avatar: 'https://example.com/avatar4.jpg',
        gender: '男',
        username: 'wangwu789',
        phone: '13844445555',
        emergencyContact: '赵六',
        emergencyPhone: '13855556666',
        role: '访客',
        lastLoginTime: '2022/1/3 14:20:00',
        disabled: false
    },
    {
        id: 5,
        name: '赵六',
        avatar: 'https://example.com/avatar5.jpg',
        gender: '女',
        username: 'zhaoliu123',
        phone: '13855556666',
        emergencyContact: '王五',
        emergencyPhone: '13844445555',
        role: '督导',
        lastLoginTime: '2022/1/4 10:45:00',
        disabled: false
    },
    {
        id: 6,
        name: '小明',
        avatar: 'https://example.com/avatar6.jpg',
        gender: '男',
        username: 'xiaoming888',
        phone: '13866667777',
        emergencyContact: '小红',
        emergencyPhone: '13877778888',
        role: '访客',
        lastLoginTime: '2022/1/4 16:30:00',
        disabled: true
    },
    {
        id: 7,
        name: '小红',
        avatar: 'https://example.com/avatar7.jpg',
        gender: '女',
        username: 'xiaohong999',
        phone: '13877778888',
        emergencyContact: '小明',
        emergencyPhone: '13866667777',
        role: '咨询师',
        lastLoginTime: '2022/1/5 11:20:00',
        disabled: false
    },
    {
        id: 8,
        name: '小张',
        avatar: 'https://example.com/avatar8.jpg',
        gender: '男',
        username: 'xiaozhang111',
        phone: '13888889999',
        emergencyContact: '小李',
        emergencyPhone: '13899990000',
        role: '访客',
        lastLoginTime: '2022/1/5 14:40:00',
        disabled: false
    },
    {
        id: 9,
        name: '小李',
        avatar: 'https://example.com/avatar9.jpg',
        gender: '女',
        username: 'xiaoli222',
        phone: '13899990000',
        emergencyContact: '小张',
        emergencyPhone: '13888889999',
        role: '督导',
        lastLoginTime: '2022/1/6 09:30:00',
        disabled: false
    },
    {
        id: 10,
        name: '老王',
        avatar: 'https://example.com/avatar10.jpg',
        gender: '男',
        username: 'laowang333',
        phone: '13900001111',
        emergencyContact: '老李',
        emergencyPhone: '13911112222',
        role: '访客',
        lastLoginTime: '2022/1/6 15:50:00',
        disabled: true
    },
    {
        id: 11,
        name: '老李',
        avatar: 'https://example.com/avatar11.jpg',
        gender: '男',
        username: 'laoli444',
        phone: '13911112222',
        emergencyContact: '老王',
        emergencyPhone: '13900001111',
        role: '咨询师',
        lastLoginTime: '2022/1/7 10:15:00',
        disabled: false
    },
    {
        id: 12,
        name: '小王',
        avatar: 'https://example.com/avatar12.jpg',
        gender: '女',
        username: 'xiaowang555',
        phone: '13922223333',
        emergencyContact: '小陈',
        emergencyPhone: '13933334444',
        role: '访客',
        lastLoginTime: '2022/1/7 16:25:00',
        disabled: false
    },
    {
        id: 13,
        name: '小陈',
        avatar: 'https://example.com/avatar13.jpg',
        gender: '男',
        username: 'xiaochen666',
        phone: '13933334444',
        emergencyContact: '小王',
        emergencyPhone: '13922223333',
        role: '督导',
        lastLoginTime: '2022/1/8 11:40:00',
        disabled: false
    },
    {
        id: 14,
        name: '大张',
        avatar: 'https://example.com/avatar14.jpg',
        gender: '男',
        username: 'dazhang777',
        phone: '13944445555',
        emergencyContact: '大李',
        emergencyPhone: '13955556666',
        role: '访客',
        lastLoginTime: '2022/1/8 14:55:00',
        disabled: true
    },
    {
        id: 15,
        name: '大李',
        avatar: 'https://example.com/avatar15.jpg',
        gender: '女',
        username: 'dali888',
        phone: '13955556666',
        emergencyContact: '大张',
        emergencyPhone: '13944445555',
        role: '咨询师',
        lastLoginTime: '2022/1/9 09:45:00',
        disabled: false
    }
])

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
        // TODO: 调用API获取数据
        // const { data } = await getUserList({
        //     page: currentPage.value,
        //     pageSize: pageSize.value,
        //     name: searchQuery.value
        // })
        // tableData.value = data.list
        // total.value = data.total
    } catch (error) {
        console.error('获取数据失败：', error)
        ElMessage.error('获取数据失败')
    } finally {
        loading.value = false
    }
}

// 处理用户状态变更
const handleUserStatus = async (row: any) => {
    try {
        // TODO: 调用API更新用户状态
        // await updateUserStatus(row.id, !row.disabled)
        ElMessage.success(`已${row.disabled ? '启用' : '禁用'}该用户`)
        fetchData()
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