<template>
    <SupervisorBaseLayout>
        <div class="consultation-container">
            <!-- 搜索区域 -->
            <div class="search-area">
                <div class="search-item">
                    <div class="label">搜索姓名</div>
                    <el-input v-model="searchForm.name" placeholder="输入姓名进行搜索" clearable />
                </div>
                <div class="search-item">
                    <div class="label">选择日期</div>
                    <el-date-picker v-model="searchForm.date" type="date" placeholder="请选择日期" format="YYYY-MM-DD"
                        value-format="YYYY-MM-DD" />
                </div>
                <el-button class="export-button">批量导出记录</el-button>
            </div>

            <!-- 表格区域 -->
            <div class="table-area">
                <el-table :data="currentPageData" style="width: 100%" height="600">
                    <el-table-column prop="consultant" label="咨询师" />
                    <el-table-column prop="duration" label="求助时长" />
                    <el-table-column prop="date" label="求助日期" />
                    <el-table-column prop="visitor" label="咨询人" />
                    <el-table-column label="咨询人评价" width="200">
                        <template #default="{ row }">
                            <el-rate v-model="row.rating" :colors="['#ffd21e', '#ffd21e', '#ffd21e']" :allow-half="true"
                                show-score score-template="{value} points" disabled />
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" width="200">
                        <template #default>
                            <div class="button-group">
                                <el-button type="success" size="small" class="detail-button">查看详情</el-button>
                                <el-button type="danger" size="small" class="record-button">导出记录</el-button>
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
    </SupervisorBaseLayout>
</template>

<script setup>
import { ref, computed } from 'vue'
import SupervisorBaseLayout from '@/components/layout/SupervisorBaseLayout.vue'

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)

// 搜索表单
const searchForm = ref({
    name: '',
    date: ''
})

// 静态数据
const tableData = [
    {
        consultant: 'Apple',
        duration: '11:11:11',
        date: '12.09.2019',
        visitor: 'Apple',
        rating: 3.5
    },
    {
        consultant: 'Banana',
        duration: '22:22:22',
        date: '12.10.2019',
        visitor: 'Banana',
        rating: 4
    },
    {
        consultant: 'Orange',
        duration: '33:33:33',
        date: '12.11.2019',
        visitor: 'Orange',
        rating: 5
    },
    {
        consultant: 'David',
        duration: '44:44:44',
        date: '12.12.2019',
        visitor: 'David',
        rating: 4.5
    },
    {
        consultant: 'Emma',
        duration: '55:55:55',
        date: '12.13.2019',
        visitor: 'Emma',
        rating: 3
    },
    {
        consultant: 'Frank',
        duration: '01:30:00',
        date: '12.14.2019',
        visitor: 'Frank',
        rating: 4.5
    },
    {
        consultant: 'Grace',
        duration: '02:15:30',
        date: '12.15.2019',
        visitor: 'Grace',
        rating: 5
    },
    {
        consultant: 'Henry',
        duration: '01:45:20',
        date: '12.16.2019',
        visitor: 'Henry',
        rating: 3.5
    },
    {
        consultant: 'Ivy',
        duration: '02:30:15',
        date: '12.17.2019',
        visitor: 'Ivy',
        rating: 4
    },
    {
        consultant: 'Jack',
        duration: '01:20:45',
        date: '12.18.2019',
        visitor: 'Jack',
        rating: 4.5
    },
    {
        consultant: 'Kelly',
        duration: '02:45:00',
        date: '12.19.2019',
        visitor: 'Kelly',
        rating: 5
    },
    {
        consultant: 'Liam',
        duration: '01:50:30',
        date: '12.20.2019',
        visitor: 'Liam',
        rating: 3.5
    },
    {
        consultant: 'Mary',
        duration: '02:10:25',
        date: '12.21.2019',
        visitor: 'Mary',
        rating: 4
    },
    {
        consultant: 'Noah',
        duration: '01:35:40',
        date: '12.22.2019',
        visitor: 'Noah',
        rating: 4.5
    },
    {
        consultant: 'Olivia',
        duration: '02:20:15',
        date: '12.23.2019',
        visitor: 'Olivia',
        rating: 5
    }
]

// 计算当前页的数据
const currentPageData = computed(() => {
    const start = (currentPage.value - 1) * pageSize.value
    const end = start + pageSize.value
    return tableData.slice(start, end)
})
</script>

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