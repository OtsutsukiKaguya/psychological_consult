<!-- 咨询师管理页面 -->
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
                <el-table :data="currentTableData" style="width: 100%" v-loading="loading">
                    <!-- ID列 -->
                    <el-table-column label="id" min-width="120" prop="id" />
                    <el-table-column label="身份" min-width="100" prop="role" />
                    <el-table-column label="绑定督导" min-width="120" prop="tutorId" />
                    <el-table-column label="平均咨询评级" min-width="150">
                        <template #default="{ row }">
                            <el-rate v-model="row.averageRating" disabled show-score text-color="#ff9900" />
                        </template>
                    </el-table-column>
                    <el-table-column label="总咨询数" min-width="120" prop="totalSessions" />
                    <el-table-column label="周值班安排" min-width="150" prop="dutyArrangement" />

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
                <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize"
                    :page-sizes="[10, 20, 30, 50]" :total="total" @size-change="handleSizeChange"
                    @current-change="handleCurrentChange" layout="total, sizes, prev, pager, next, jumper" />
            </div>

            <!-- 新增咨询师弹窗 -->
            <el-dialog v-model="dialogVisible" title="新增咨询师" width="500px">
                <el-form ref="addFormRef" :model="addForm" :rules="addFormRules" label-width="120px">
                    <el-form-item label="ID" prop="id">
                        <el-input v-model="addForm.id" placeholder="请输入ID" />
                    </el-form-item>
                    <el-form-item label="值班安排" prop="dutyArrangement">
                        <el-input v-model="addForm.dutyArrangement" placeholder="请输入值班安排，如：周一-周三" />
                    </el-form-item>
                    <el-form-item label="所属学院" prop="institute">
                        <el-input v-model="addForm.institute" placeholder="请输入所属学院" />
                    </el-form-item>
                    <el-form-item label="职称" prop="jobTitle">
                        <el-input v-model="addForm.jobTitle" placeholder="请输入职称" />
                    </el-form-item>
                    <el-form-item label="同时咨询人数" prop="counselorSameTime">
                        <el-input-number v-model="addForm.counselorSameTime" :min="1" :max="10" />
                    </el-form-item>
                    <el-form-item label="专长标签" prop="tag">
                        <el-input v-model="addForm.tag" placeholder="请输入专长标签" />
                    </el-form-item>
                </el-form>
                <template #footer>
                    <span class="dialog-footer">
                        <el-button @click="dialogVisible = false">取消</el-button>
                        <el-button type="primary" @click="submitAdd">确认</el-button>
                    </span>
                </template>
            </el-dialog>
        </div>
    </BaseLayout>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import BaseLayout from '../../components/layout/BaseLayout.vue'
import axios from 'axios'
import { API } from '@/config'
import { debounce } from 'lodash'

// 搜索相关
const searchQuery = ref('')

// 加载状态
const loading = ref(false)

// 表格数据
const tableData = ref<any[]>([])
const total = ref(0)

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)

// 获取数据
const fetchData = async () => {
    loading.value = true
    try {
        const response = await axios.get(API.COUNSELOR.SEARCH)
        console.log('获取咨询师列表返回数据：', response)
        if (response.data.code === 0) {
            tableData.value = response.data.data
            total.value = response.data.data.length
        } else {
            ElMessage.error(response.data.message || '获取咨询师列表失败')
        }
    } catch (error) {
        ElMessage.error('获取咨询师列表失败')
        console.error('Error fetching consultants:', error)
    } finally {
        loading.value = false
    }
}

// 搜索处理
const handleSearch = debounce(async () => {
    loading.value = true
    try {
        const response = await axios.get(API.COUNSELOR.SEARCH)
        console.log('搜索咨询师返回数据：', response)
        if (response.data.code === 0) {
            tableData.value = response.data.data
            total.value = response.data.data.length
        } else {
            ElMessage.error(response.data.message || '搜索失败')
        }
    } catch (error) {
        ElMessage.error('搜索失败')
        console.error('Error searching consultants:', error)
    } finally {
        loading.value = false
    }
}, 300)

// 表格数据计算属性
const currentTableData = computed(() => {
    const start = (currentPage.value - 1) * pageSize.value
    const end = start + pageSize.value
    return tableData.value.slice(start, end)
})

const handleSizeChange = (val: number) => {
    pageSize.value = val
    fetchData()
}

const handleCurrentChange = (val: number) => {
    currentPage.value = val
    fetchData()
}

// 新增相关
const dialogVisible = ref(false)
const addFormRef = ref(null)
const addForm = ref({
    id: '',
    dutyArrangement: '',
    institute: '',
    jobTitle: '',
    counselorSameTime: 1,
    tag: ''
})

// 表单验证规则
const addFormRules = {
    id: [{ required: true, message: '请输入ID', trigger: 'blur' }],
    dutyArrangement: [{ required: true, message: '请输入值班安排', trigger: 'blur' }],
    institute: [{ required: true, message: '请输入所属学院', trigger: 'blur' }],
    jobTitle: [{ required: true, message: '请输入职称', trigger: 'blur' }],
    counselorSameTime: [{ required: true, message: '请输入同时咨询人数', trigger: 'blur' }],
    tag: [{ required: true, message: '请输入专长标签', trigger: 'blur' }]
}

// 打开新增弹窗
const handleAdd = () => {
    dialogVisible.value = true
    // 重置表单
    addForm.value = {
        id: '',
        dutyArrangement: '',
        institute: '',
        jobTitle: '',
        counselorSameTime: 1,
        tag: ''
    }
}

// 提交新增
const submitAdd = async () => {
    if (!addFormRef.value) return

    await addFormRef.value.validate(async (valid) => {
        if (valid) {
            try {
                const response = await axios.post(API.COUNSELOR.INSERT, addForm.value)
                console.log('新增咨询师返回数据：', response.data)

                if (response.data.code === 0) {
                    ElMessage.success('添加成功')
                    dialogVisible.value = false
                    // 重新获取数据
                    fetchData()
                } else {
                    ElMessage.error('添加失败')
                }
            } catch (error) {
                console.error('添加失败:', error)
                ElMessage.error('添加失败，请检查网络连接')
            }
        }
    })
}

// 修改咨询师信息
const handleEdit = (row: any) => {
    // TODO: 实现修改功能
    console.log('修改咨询师信息：', row)
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
    background-color: #f2f4f9 !important;

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

:deep(.el-table__fixed-right-patch) {
    background-color: #f2f4f9 !important;
}

:deep(.el-table__fixed-right) th {
    background-color: #f2f4f9 !important;
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

/* 新增弹窗样式 */
.dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    margin-top: 20px;
}

:deep(.el-dialog__body) {
    padding: 20px 40px;
}

:deep(.el-input-number) {
    width: 100%;
}
</style>