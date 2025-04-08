<template>
  <BaseLayout>
    <div class="tree-hole-detail">
      <!-- 帖子详情卡片 -->
      <div class="post-card">
        <div class="post-header">
          <div class="post-info">
            <div class="user-info">
              <el-avatar :size="40" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
              <div class="user-meta">
                <span class="username">plus</span>
                <span class="post-time">2022.1.1</span>
              </div>
            </div>
            <div class="post-title">震惊！巴拉巴拉巴拉</div>
          </div>
          <el-button type="danger" :icon="Delete" circle class="delete-button" @click="showDeleteDialog" />
        </div>
        <div class="post-content">
          从前有座山,山里有座庙,庙里有个老和尚和一个小和尚.老和尚对小和尚说:"庙里这 口大钟每个整点都要敲,1点敲1下、2点敲2下、3点敲3下....以此类推.每下敲钟 之间需要
        </div>
      </div>

      <!-- 回复列表 -->
      <div class="replies-list">
        <div class="reply-item" v-for="reply in replies" :key="reply.id">
          <div class="reply-header">
            <div class="reply-relation">
              <span class="replier">{{ reply.username }}</span>
              <span class="reply-to">回复</span>
              <span class="author">plus</span>
            </div>
            <span class="reply-time">{{ reply.time }}</span>
          </div>
          <div class="reply-content">{{ reply.content }}</div>
        </div>
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
            <el-button type="primary" class="send-button">发送</el-button>
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
            <el-input v-model="deleteForm.reason" type="textarea" :rows="4" placeholder="请输入删除理由" resize="none" />
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
  </BaseLayout>
</template>

<script setup>
import BaseLayout from '@/components/layout/BaseLayout.vue'
import { ref, computed } from 'vue'
import { Delete, Picture, ChatRound } from '@element-plus/icons-vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

const route = useRoute()
const postId = route.params.id
const replyContent = ref('')

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
const handleDelete = () => {
  if (!isFormValid.value) {
    ElMessage.warning('请填写完整信息')
    return
  }
  // TODO: 调用删除API
  console.log('删除帖子', {
    postId: postId,
    ...deleteForm.value
  })
  deleteDialogVisible.value = false
  ElMessage.success('删除成功')
}

// 模拟回复数据
const replies = [
  {
    id: 1,
    username: 'Alice',
    avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    time: '2022.1.1 10:30',
    content: '这个故事很有意思，让我想起了小时候听过的一个类似的故事...'
  },
  {
    id: 2,
    username: 'Bob',
    avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    time: '2022.1.1 11:15',
    content: '确实是个好故事，不过我觉得最关键的是要理解故事背后的含义。'
  },
  {
    id: 3,
    username: 'Carol',
    avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    time: '2022.1.1 14:20',
    content: '我也遇到过类似的情况，感同身受啊！'
  }
]
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
  gap: 4px;
  font-size: 14px;
}

.replier {
  color: #557ff7;
  font-weight: 500;
}

.reply-to {
  color: #999;
}

.author {
  color: #557ff7;
  font-weight: 500;
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
