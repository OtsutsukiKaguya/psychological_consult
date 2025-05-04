<!-- 树洞详情组件 -->
<template>
    <div class="tree-hole-detail">
        <!-- 帖子详情卡片 -->
        <div class="post-card">
            <div class="post-header">
                <div class="post-info">
                    <div class="user-info">
                        <el-avatar :size="40"
                            src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
                        <div class="user-meta">
                            <span class="username">{{ personId }}</span>
                            <span class="post-time">{{ postTime }}</span>
                        </div>
                    </div>
                    <div class="post-title">{{ postTitle }}</div>
                </div>
                <!-- 只有管理员才显示删除按钮 -->
                <el-button v-if="isAdmin" type="danger" :icon="Delete" circle class="delete-button"
                    @click="showDeleteDialog" />
            </div>
            <div class="post-content">
                {{ postContent }}
            </div>
        </div>

        <!-- 回复列表 -->
        <div class="replies-list">
            <el-empty v-if="!loading && replies.length === 0" description="暂无回复" />
            <el-skeleton :rows="3" animated v-if="loading" />

            <template v-else>
                <div class="reply-item" v-for="reply in replies" :key="reply.replyId">
                    <div class="reply-header">
                        <div class="reply-relation">
                            <div class="reply-box">
                                <span class="replier-name">{{ reply.userInfo.id }}</span>
                                <span class="reply-action">回复了</span>
                                <span class="replier-name">{{ personId }}</span>
                            </div>
                        </div>
                        <span class="reply-time">{{ reply.replyTime }}</span>
                    </div>
                    <div class="reply-content">{{ reply.replyContent }}</div>
                </div>
            </template>
        </div>

        <!-- 回复区域 -->
        <div class="reply-area">
            <div class="reply-input-wrapper">
                <el-input v-model="replyContent" type="textarea" :rows="3" placeholder="请输入回复内容..." resize="none" />
                <div class="reply-tools">
                    <div class="tool-buttons">
                        <el-button :icon="Picture" circle />
                        <el-button :icon="ChatRound" circle />
                    </div>
                    <el-button type="primary" class="send-button" @click="handleSendReply">发送</el-button>
                </div>
            </div>
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

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Delete, Picture, ChatRound } from '@element-plus/icons-vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { API } from '@/config'

const route = useRoute()
const router = useRouter()
const postId = route.params.id
const replyContent = ref('')

// 获取当前用户角色
const currentUser = computed(() => {
    const userInfo = localStorage.getItem('userInfo')
    return userInfo ? JSON.parse(userInfo) : null
})

// 判断是否为管理员
const isAdmin = computed(() => {
    return currentUser.value?.role === 'ADMIN'
})

// 帖子详情
const postTitle = ref('')
const postContent = ref('')
const postTime = ref('')
const personId = ref('')

// 删除相关
const deleteDialogVisible = ref(false)
const deleteForm = ref({
    reviewer: '',
    reason: ''
})

// 表单验证
const isFormValid = computed(() => {
    return deleteForm.value.reviewer.trim() !== '' && deleteForm.value.reason.trim() !== ''
})

// 显示删除对话框
const showDeleteDialog = () => {
    deleteDialogVisible.value = true
}

// 处理删除
const handleDelete = async () => {
    if (!isFormValid.value) {
        ElMessage.warning('请填写完整信息')
        return
    }
    try {
        await axios.delete(API.TREE_HOLE.DELETE_POST(postId), {
            data: {
                reviewer: deleteForm.value.reviewer,
                reason: deleteForm.value.reason
            }
        })
        deleteDialogVisible.value = false
        ElMessage.success('删除成功')
        // 删除成功后返回列表页
        router.back()
    } catch (error) {
        console.error('删除失败:', error)
        ElMessage.error('删除失败')
    }
}

// 回复数据
const replies = ref([])
const loading = ref(false)

// 发送回复
const handleSendReply = async () => {
    if (!replyContent.value.trim()) {
        ElMessage.warning('请输入回复内容')
        return
    }

    try {
        // 获取当前时间并格式化
        const now = new Date()
        const year = now.getFullYear()
        const month = String(now.getMonth() + 1).padStart(2, '0')
        const day = String(now.getDate()).padStart(2, '0')
        const hours = String(now.getHours()).padStart(2, '0')
        const minutes = String(now.getMinutes()).padStart(2, '0')
        const seconds = String(now.getSeconds()).padStart(2, '0')
        const formattedTime = `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`

        const replyData = {
            postId: postId,
            replyContent: replyContent.value,
            personId: currentUser.value.id,
            pictureLink: null,
            idPictureLink: null,
            replyIsDeleted: false,
            thumbNum: 0,
            deleteReason: null,
            replyTime: formattedTime
        }

        await axios.post(`${API.TREE_HOLE.GET_POST_REPLIES(postId)}`, replyData)

        // 清空输入框
        replyContent.value = ''
        // 重新获取回复列表
        await fetchReplies()
        ElMessage.success('回复成功')
    } catch (error) {
        console.error('发送回复失败:', error)
        ElMessage.error('发送回复失败')
    }
}

// 获取帖子详情
const fetchPostDetails = async () => {
    try {
        const response = await axios.get(API.TREE_HOLE.GET_POST_DETAIL(postId))
        console.log('帖子详情接口返回数据:', response.data)
        if (response.data.code === 0 && response.data.data) {
            const post = response.data.data
            postTitle.value = post.postTitle
            postContent.value = post.postContent
            postTime.value = post.postTime
            personId.value = post.userInfo?.id || ''
        } else {
            ElMessage.error('获取帖子详情失败')
        }
    } catch (error) {
        console.error('获取帖子详情失败:', error)
        ElMessage.error('获取帖子详情失败')
    }
}

// 获取回复数据
const fetchReplies = async () => {
    try {
        loading.value = true
        const response = await axios.get(API.TREE_HOLE.GET_POST_REPLIES(postId))
        if (response.data.code === 0) {
            replies.value = response.data.data
        } else if (response.data.code === -1 && response.data.message === '暂无回复') {
            replies.value = []
            // 不弹出错误提示
        } else {
            ElMessage.error('获取回复列表失败')
        }
    } catch (error) {
        console.error('获取回复失败:', error)
        ElMessage.error('获取回复列表失败')
    } finally {
        loading.value = false
    }
}

onMounted(() => {
    fetchPostDetails()
    fetchReplies()
})
</script>

<style scoped>
.tree-hole-detail {
    height: 100%;
    display: flex;
    flex-direction: column;
}

.post-card {
    background: #fff;
    border-radius: 8px;
    padding: 24px;
    margin-bottom: 20px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.post-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 20px;
}

.post-info {
    flex: 1;
}

.user-info {
    display: flex;
    align-items: center;
    margin-bottom: 16px;
}

.user-meta {
    margin-left: 12px;
    display: flex;
    flex-direction: column;
}

.username {
    font-size: 16px;
    font-weight: 500;
    color: #333;
    line-height: 1.2;
}

.post-time {
    font-size: 14px;
    color: #999;
    margin-top: 4px;
}

.post-title {
    font-size: 18px;
    font-weight: 500;
    color: #333;
    margin-bottom: 12px;
}

.post-content {
    font-size: 14px;
    color: #666;
    line-height: 1.6;
}

.delete-button {
    background-color: #F1908C;
    border: none;
}

.delete-button:hover {
    background-color: #e17f7a;
}

.replies-list {
    margin-bottom: 20px;
}

.reply-item {
    background: #fff;
    border-radius: 8px;
    padding: 16px;
    padding-left: 0px;
    margin-bottom: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.reply-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
    padding-left: 25px;
}

.reply-relation {
    display: flex;
    align-items: center;
}

.reply-box {
    display: inline-block;
    border-radius: 4px;
    font-size: 14px;
}

.replier-name {
    color: #333;
    font-weight: 500;
}

.reply-action {
    color: #409EFF;
    margin: 0 4px;
}

.reply-time {
    font-size: 12px;
    color: #999;
}

.reply-content {
    font-size: 14px;
    color: #666;
    line-height: 1.6;
    padding-left: 25px;
    margin-top: 8px;
}

.reply-area {
    margin-top: 20px;
    background: #fff;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.reply-input-wrapper {
    width: 100%;
}

.reply-tools {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 12px;
}

.tool-buttons {
    display: flex;
    gap: 8px;
}

.tool-buttons .el-button {
    background-color: #f5f7fa;
    border: none;
    color: #909399;
}

.send-button {
    background-color: #67C23A;
    border: none;
    padding: 8px 24px;
}

.send-button:hover {
    background-color: #5daf34;
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
</style>