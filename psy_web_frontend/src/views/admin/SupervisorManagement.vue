<!-- 督导管理页面 -->
<template>
    <BaseLayout>
        <div class="page-container">
            <!-- 搜索区域 -->
            <div class="search-container">
                <div class="search-title">搜索姓名</div>
                <el-input v-model="searchQuery" placeholder="输入姓名进行搜索" class="search-input" clearable
                    @input="handleSearch" />
                <el-button type="primary" class="add-button" @click="handleAdd">新增</el-button>
            </div>

            <!-- 表格区域 -->
            <div class="table-container">
                <el-table :data="currentTableData" style="width: 100%">
                    <!-- 姓名列 -->
                    <el-table-column label="姓名" min-width="120" prop="name" />
                    <el-table-column label="身份" min-width="100" prop="role" />
                    <el-table-column label="绑定咨询师" min-width="150" prop="consultants" />
                    <el-table-column label="平均督导评级" min-width="150">
                        <template #default="{ row }">
                            <el-rate v-model="row.rating" disabled show-score text-color="#ff9900" />
                        </template>
                    </el-table-column>
                    <el-table-column label="总督导次数" min-width="120" prop="supervisionCount" />
                    <el-table-column label="督导总时长" min-width="120" prop="totalTime" />
                    <el-table-column label="周值班安排" min-width="150" prop="schedule" />

                    <el-table-column label="操作" width="100" fixed="right">
                        <template #default="{ row }">
                            <el-button type="primary" @click="handleEdit(row)">
                                修改
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
const tableData = ref([
    {
        id: 1,
        name: '王教授',
        role: '督导',
        consultants: '张三、李四、王五',
        rating: 4.5,
        supervisionCount: 156,
        totalTime: '48h',
        schedule: '周一、周三、周五'
    },
    {
        id: 2,
        name: '李教授',
        role: '督导',
        consultants: '赵六、钱七',
        rating: 4.8,
        supervisionCount: 189,
        totalTime: '56h',
        schedule: '周二、周四、周六'
    },
    {
        id: 3,
        name: '陈教授',
        role: '督导',
        consultants: '孙八、周九',
        rating: 4.3,
        supervisionCount: 134,
        totalTime: '42h',
        schedule: '周一、周三、周日'
    },
    {
        id: 4,
        name: '张教授',
        role: '督导',
        consultants: '吴十、郑十一',
        rating: 4.6,
        supervisionCount: 167,
        totalTime: '52h',
        schedule: '周二、周五、周日'
    },
    {
        id: 5,
        name: '刘教授',
        role: '督导',
        consultants: '王十二、李十三',
        rating: 4.7,
        supervisionCount: 178,
        totalTime: '54h',
        schedule: '周一、周四、周六'
    },
    {
        id: 6,
        name: '杨教授',
        role: '督导',
        consultants: '赵十四、钱十五',
        rating: 4.4,
        supervisionCount: 145,
        totalTime: '46h',
        schedule: '周三、周五、周日'
    },
    {
        id: 7,
        name: '吴教授',
        role: '督导',
        consultants: '孙十六、周十七',
        rating: 4.9,
        supervisionCount: 198,
        totalTime: '58h',
        schedule: '周一、周四、周七'
    },
    {
        id: 8,
        name: '郑教授',
        role: '督导',
        consultants: '吴十八、郑十九',
        rating: 4.2,
        supervisionCount: 123,
        totalTime: '38h',
        schedule: '周二、周五、周日'
    },
    {
        id: 9,
        name: '周教授',
        role: '督导',
        consultants: '王二十、李二一',
        rating: 4.5,
        supervisionCount: 156,
        totalTime: '48h',
        schedule: '周一、周三、周六'
    },
    {
        id: 10,
        name: '赵教授',
        role: '督导',
        consultants: '张二二、陈二三',
        rating: 4.7,
        supervisionCount: 178,
        totalTime: '54h',
        schedule: '周二、周四、周日'
    },
    {
        id: 11,
        name: '钱教授',
        role: '督导',
        consultants: '刘二四、杨二五',
        rating: 4.6,
        supervisionCount: 167,
        totalTime: '52h',
        schedule: '周一、周五、周六'
    },
    {
        id: 12,
        name: '孙教授',
        role: '督导',
        consultants: '吴二六、郑二七',
        rating: 4.3,
        supervisionCount: 134,
        totalTime: '42h',
        schedule: '周三、周五、周日'
    },
    {
        id: 13,
        name: '李教授',
        role: '督导',
        consultants: '周二八、赵二九',
        rating: 4.8,
        supervisionCount: 189,
        totalTime: '56h',
        schedule: '周二、周四、周六'
    },
    {
        id: 14,
        name: '周教授',
        role: '督导',
        consultants: '钱三十、孙三一',
        rating: 4.4,
        supervisionCount: 145,
        totalTime: '46h',
        schedule: '周一、周三、周五'
    },
    {
        id: 15,
        name: '王教授',
        role: '督导',
        consultants: '李三二、张三三',
        rating: 4.5,
        supervisionCount: 156,
        totalTime: '48h',
        schedule: '周二、周四、周日'
    }
])

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)

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
    try {
        // TODO: 调用API获取数据
        await new Promise(resolve => setTimeout(resolve, 500))
    } catch (error) {
        console.error('获取数据失败：', error)
        ElMessage.error('获取数据失败')
    }
}

// 新增督导
const handleAdd = () => {
    // TODO: 实现新增功能
    ElMessage.info('新增功能待实现')
}

// 修改督导信息
const handleEdit = (row: any) => {
    // TODO: 实现修改功能
    console.log('修改督导信息：', row)
    ElMessage.info('修改功能待实现')
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

.add-button {
    background-color: #67C23A;
    border: none;
}

.add-button:hover {
    background-color: #5daf34;
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
}

:deep(.el-table__body-wrapper) {
    overflow-y: auto;
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

:deep(.el-button--primary) {
    background-color: #6777ef;
    border-color: #6777ef;
}

:deep(.el-rate) {
    display: inline-flex;
    align-items: center;
}

:deep(.el-table__fixed-right-patch) {
    background-color: #f2f4f9 !important;
}

:deep(.el-table__fixed-right) th {
    background-color: #f2f4f9 !important;
}

:deep(.el-table th.el-table-fixed-column--right) {
    background-color: #f2f4f9 !important;
}
</style>