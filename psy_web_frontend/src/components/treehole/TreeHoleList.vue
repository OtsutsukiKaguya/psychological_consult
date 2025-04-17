<!-- 树洞列表组件 -->
<script setup>
import { Delete } from '@element-plus/icons-vue'
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { API } from '@/config'

const router = useRouter()

// 获取当前用户角色
const currentUser = computed(() => {
    const userInfo = localStorage.getItem('userInfo')
    return userInfo ? JSON.parse(userInfo) : null
})

// 判断是否为管理员
const isAdmin = computed(() => {
    return currentUser.value?.role === 'ADMIN'
})

// 帖子数据
const posts = ref([])
const loading = ref(false)
const totalPosts = ref(0)

// 获取帖子数据
const fetchPosts = async () => {
    try {
        loading.value = true
        const response = await axios.get(`${API.TREE_HOLE.GET_ALL_POSTS}`)
        console.log(response.data)
        // 检查响应数据的结构
        if (Array.isArray(response.data)) {
            // 如果直接返回数组
            posts.value = response.data
            totalPosts.value = posts.value.length
        } else if (response.data.data && Array.isArray(response.data.data)) {
            // 如果返回的是包装对象
            posts.value = response.data.data
            totalPosts.value = posts.value.length
        } else {
            ElMessage.error('数据格式不正确')
            posts.value = []
            totalPosts.value = 0
        }
    } catch (error) {
        console.error('获取帖子失败:', error)
        ElMessage.error('获取帖子列表失败')
        posts.value = []
        totalPosts.value = 0
    } finally {
        loading.value = false
    }
}

// 在组件挂载时获取数据
onMounted(() => {
    fetchPosts()
})

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)

// 计算当前页显示的帖子
const displayedPosts = computed(() => {
    const start = (currentPage.value - 1) * pageSize.value
    const end = start + pageSize.value
    return posts.value.slice(start, end)
})

// 跳转到详情页
const goToDetail = (id) => {
    // 根据用户角色跳转到不同的详情页
    if (isAdmin.value) {
        router.push({
            name: 'adminTreeHoleDetail',
            params: { id }
        })
    } else if (currentUser.value?.role === 'consultant') {
        router.push({
            name: 'consultantTreeHoleDetail',
            params: { id }
        })
    } else if (currentUser.value?.role === 'supervisor') {
        router.push({
            name: 'supervisorTreeHoleDetail',
            params: { id }
        })
    }
}

// 删除相关
const deleteDialogVisible = ref(false)
const currentPostId = ref(null)
const deleteForm = ref({
    reviewer: '',
    reason: ''
})

// 表单验证
const isFormValid = computed(() => {
    return deleteForm.value.reviewer.trim() !== '' && deleteForm.value.reason.trim() !== ''
})

// 显示删除对话框
const showDeleteDialog = (id) => {
    currentPostId.value = id
    deleteDialogVisible.value = true
}

// 处理删除
const handleDelete = async () => {
    if (!isFormValid.value) {
        ElMessage.warning('请填写完整信息')
        return
    }

    try {
        await axios.delete(API.TREE_HOLE.DELETE_POST(currentPostId.value), {
            data: {
                reviewer: deleteForm.value.reviewer,
                reason: deleteForm.value.reason
            }
        })
        deleteDialogVisible.value = false
        deleteForm.value = { reviewer: '', reason: '' }
        ElMessage.success('删除成功')
        // 刷新列表
        fetchPosts()
    } catch (error) {
        console.error('删除失败:', error)
        ElMessage.error('删除失败')
    }
}
</script>

<template>
    <div class="tree-hole-container">
        <!-- 操作按钮 -->
        <div class="action-buttons">
            <el-button type="primary" class="share-button" size="small" round>经历分享</el-button>
            <el-button class="consult-button" size="small" round>问题咨询</el-button>
        </div>

        <!-- 帖子列表 -->
        <div class="post-list">
            <el-empty v-if="!loading && posts.length === 0" description="暂无帖子" />
            <el-skeleton :rows="3" animated v-if="loading" />

            <template v-else>
                <div v-for="post in displayedPosts" :key="post.postId" class="post-item"
                    @click="goToDetail(post.postId)">
                    <div class="post-content">
                        <div class="post-title">{{ post.postTitle }}</div>
                        <div class="post-info">
                            <span class="post-author">发布人：{{ post.personId }}</span>
                            <span class="post-time">发布时间：{{ post.postTime }}</span>
                        </div>
                        <div class="post-content-text">{{ post.postContent }}</div>
                    </div>
                    <!-- 只有管理员才显示删除按钮 -->
                    <el-button v-if="isAdmin" type="danger" :icon="Delete" circle class="delete-button"
                        @click.stop="showDeleteDialog(post.postId)" />
                </div>
            </template>
        </div>

        <!-- 分页 -->
        <div class="pagination">
            <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10]"
                :total="totalPosts" :pager-count="7" layout="prev, pager, next, ->, total, sizes, jumper" background />
        </div>

        <!-- 删除确认对话框 -->
        <el-dialog v-model="deleteDialogVisible" title="删除帖子" width="500px" :show-close="false"
            :close-on-click-modal="false" :close-on-press-escape="false">
            <div class="delete-form">
                <div class="form-item">
                    <div class="form-label">审批人：</div>
                    <el-input v-model="deleteForm.reviewer" placeholder="请输入审批人" />
                </div>
                <div class="form-item">
                    <div class="form-label">删除理由：</div>
                    <el-input v-model="deleteForm.reason" type="textarea" :rows="4" placeholder="请输入删除理由"
                        resize="none" />
                </div>
            </div>
            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="deleteDialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="handleDelete" :disabled="!isFormValid">
                        确定
                    </el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<style scoped>
.tree-hole-container {
    height: 100%;
    display: flex;
    flex-direction: column;
}

.action-buttons {
    margin-bottom: 20px;
}

.share-button {
    margin-right: 12px;
    background-color: #F8C4C4;
    border-color: #F8C4C4;
}

.share-button:hover {
    background-color: #f5b1b1;
    border-color: #f5b1b1;
}

.consult-button {
    background-color: #F4B183;
    border-color: #F4B183;
    color: #fff;
}

.consult-button:hover {
    background-color: #f1a06e;
    border-color: #f1a06e;
}

.post-list {
    flex: 1;
    overflow-y: auto;
}

.post-item {
    background: #fff;
    border-radius: 8px;
    padding: 20px;
    margin-bottom: 16px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
    display: flex;
    justify-content: space-between;
    align-items: center;
    cursor: pointer;
    transition: all 0.3s ease;
}

.post-item:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
}

.post-content {
    flex: 1;
}

.post-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
}

.post-title {
    font-size: 16px;
    font-weight: 500;
    color: #333;
}

.post-meta {
    display: flex;
    gap: 16px;
    color: #999;
    font-size: 14px;
}

.post-info {
    font-size: 14px;
    color: #666;
}

.post-info span {
    margin-right: 20px;
}

.delete-button {
    padding: 6px 16px;
    background-color: #F1908C;
    border: none;
    border-radius: 20px;
    color: #fff;
}

.delete-button:hover {
    background-color: #e17f7a;
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

.delete-form {
    padding: 20px 0;
}

.form-item {
    margin-bottom: 20px;
}

.form-item:last-child {
    margin-bottom: 0;
}

.form-label {
    font-size: 14px;
    color: #333;
    margin-bottom: 8px;
}

.dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
}

:deep(.el-dialog__header) {
    margin-right: 0;
    border-bottom: 1px solid #eee;
    padding: 20px;
}

:deep(.el-dialog__body) {
    padding: 0 20px;
}

:deep(.el-dialog__footer) {
    border-top: 1px solid #eee;
    padding: 20px;
}

.post-content-text {
    margin-top: 10px;
    color: #666;
    font-size: 14px;
    line-height: 1.5;
}
</style>