<!-- 管理员树洞详情页面 -->
<template>
  <BaseLayout :active-menu="'/admin/tree-hole'">
    <TreeHoleDetail />
  </BaseLayout>
</template>

<script setup>
import BaseLayout from '@/components/layout/BaseLayout.vue'
import TreeHoleDetail from '@/components/treehole/TreeHoleDetail.vue'
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
