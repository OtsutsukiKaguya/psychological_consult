<!-- 树洞管理页面 -->
<script setup>
import BaseLayout from '@/components/layout/BaseLayout.vue'
import { Delete } from '@element-plus/icons-vue'
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)

// 跳转到详情页
const goToDetail = (id) => {
  router.push({
    name: 'adminTreeHoleDetail',
    params: { id }
  })
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
const handleDelete = () => {
  if (!isFormValid.value) {
    ElMessage.warning('请填写完整信息')
    return
  }
  // TODO: 调用删除API
  console.log('删除帖子', {
    postId: currentPostId.value,
    ...deleteForm.value
  })
  deleteDialogVisible.value = false
  deleteForm.value = { reviewer: '', reason: '' }
  ElMessage.success('删除成功')
}
</script>

<template>
  <BaseLayout>
    <div class="tree-hole-container">
      <!-- 操作按钮 -->
      <div class="action-buttons">
        <el-button type="primary" class="share-button" size="small" round>经历分享</el-button>
        <el-button class="consult-button" size="small" round>问题咨询</el-button>
      </div>

      <!-- 帖子列表 -->
      <div class="post-list">
        <!-- 帖子1 -->
        <div class="post-item" @click="goToDetail('1')">
          <div class="post-content">
            <div class="post-title">震惊！你见过凌晨四点的上海吗</div>
            <div class="post-info">
              <span class="post-author">发布人：plus</span>
              <span class="post-count">贴子数：15</span>
              <span class="post-time">发布时间：2022.1.1</span>
            </div>
          </div>
          <el-button type="danger" :icon="Delete" circle class="delete-button" @click.stop="showDeleteDialog('1')" />
        </div>

        <!-- 帖子2 -->
        <div class="post-item" @click="goToDetail('2')">
          <div class="post-content">
            <div class="post-title">上面的，我没见过凌晨四点的上海</div>
            <div class="post-info">
              <span class="post-author">发布人：zyq</span>
              <span class="post-count">贴子数：15</span>
              <span class="post-time">发布时间：2022.1.1</span>
            </div>
          </div>
          <el-button type="danger" :icon="Delete" circle class="delete-button" @click.stop="showDeleteDialog('2')" />
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10]" :total="50"
          :pager-count="7" layout="prev, pager, next, ->, total, sizes, jumper" background />
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
</style>