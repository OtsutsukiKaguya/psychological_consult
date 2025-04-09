<!-- 预约情况页面 -->
<script setup>
import ConsultantBaseLayout from '@/components/layout/ConsultantBaseLayout.vue'
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)

// 搜索表单 (结构保留，内容可后续调整)
const searchForm = ref({
    name: '',
    date: ''
})

// 静态占位数据 - 根据新列更新
const tableData = ref([
    { id: 1, visitor: 'Apple', appointmentDate: '12.09.2019', appointmentTime: '11:11:11', description: '我有什么什么问题', details: '查看详情' },
    { id: 2, visitor: 'Banana', appointmentDate: '12.10.2019', appointmentTime: '14:00', description: '最近感到焦虑', details: '查看详情' },
    { id: 3, visitor: 'Orange', appointmentDate: '12.11.2019', appointmentTime: '09:30', description: '学习压力大', details: '查看详情' },
    // 添加更多示例数据...
]);

// 计算当前页的数据
const currentPageData = computed(() => {
    const start = (currentPage.value - 1) * pageSize.value
    const end = start + pageSize.value
    return tableData.value.slice(start, end)
})

const totalItems = computed(() => tableData.value.length);

// 查看详情处理函数 (可以复用或修改)
const handleViewDetails = (row) => {
    console.log('查看预约详情:', row.id);
    // TODO: 实现跳转或显示详情逻辑
    // router.push(...) 或 显示弹窗
};
</script>

<template>
    <ConsultantBaseLayout>
        <div class="schedule-container"> <!-- 使用新的类名以区分 -->
            <!-- 搜索区域 (保持结构) -->
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
                <!-- 可以移除或修改按钮 -->
                <!-- <el-button class="export-button">其他操作</el-button> -->
            </div>

            <!-- 表格区域 - 更新列定义 -->
            <div class="table-area">
                <el-table :data="currentPageData" style="width: 100%" height="600">
                    <el-table-column prop="visitor" label="咨询人" width="150" />
                    <el-table-column prop="appointmentDate" label="预约日期" width="180" />
                    <el-table-column prop="appointmentTime" label="预约时间" width="180" />
                    <el-table-column prop="description" label="情况描述" min-width="250" />
                    <el-table-column label="咨询者情况" width="150" align="center">
                        <template #default="{ row }">
                            <el-button type="success" size="small" class="detail-button"
                                @click="handleViewDetails(row)">查看详情</el-button>
                        </template>
                    </el-table-column>
                </el-table>
            </div>

            <!-- 分页 (保持结构和样式) -->
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