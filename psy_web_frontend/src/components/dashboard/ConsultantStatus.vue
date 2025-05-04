<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
    consultants: {
        type: Array,
        default: () => []
    },
    supervisors: {
        type: Array,
        default: () => []
    },
    activeConversations: {
        type: Number,
        default: 0
    },
    activeGuidance: {
        type: Number,
        default: 0
    }
})

const currentPage = ref(1)
const pageSize = ref(6)

// 计算当前页的咨询师列表
const currentConsultants = computed(() => {
    const start = (currentPage.value - 1) * pageSize.value
    const end = start + pageSize.value
    return props.consultants.slice(start, end)
})
</script>

<template>
    <div class="consultant-status">
        <div class="status-list">
            <div class="list-header">
                <h3>在线咨询师</h3>
                <div class="pagination">
                    <el-pagination v-model:current-page="currentPage" :total="consultants.length" :page-size="pageSize"
                        layout="prev, pager, next" background />
                </div>
            </div>
            <div class="list-content">
                <div v-for="consultant in currentConsultants" :key="consultant.id" class="consultant-item">
                    <span class="consultant-name">{{ consultant.name }}</span>
                    <el-tag :type="consultant.status === '空闲' ? 'success' : 'danger'" size="small" effect="plain">
                        {{ consultant.status }}
                    </el-tag>
                </div>
            </div>
        </div>

        <div class="status-card">
            <div class="card-header">进行的会话</div>
            <div class="card-number">{{ activeConversations }}</div>
        </div>

        <div class="status-card">
            <div class="card-header">在线督导</div>
            <div class="list-content">
                <div v-for="supervisor in supervisors" :key="supervisor.id" class="consultant-item">
                    <span class="consultant-name">{{ supervisor.name }}</span>
                    <el-tag :type="supervisor.status === '空闲' ? 'success' : 'danger'" size="small" effect="plain">
                        {{ supervisor.status }}
                    </el-tag>
                </div>
            </div>
        </div>

        <div class="status-card">
            <div class="card-header">进行的指导</div>
            <div class="card-number">{{ activeGuidance }}</div>
        </div>
    </div>
</template>

<style scoped>
.consultant-status {
    display: flex;
    gap: 20px;
    margin-top: 20px;
    height: 260px;
    /* 设置整体高度 */
}

.status-list {
    flex: 3;
    background: white;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
    /* 设置高度 */
    display: flex;
    flex-direction: column;
}

.status-card {
    flex: 1;
    background: white;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
    height: 260px;
    /* 设置高度 */
}

.list-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}

.list-header h3 {
    font-size: 16px;
    font-weight: normal;
    color: #333;
    margin: 0;
}

.list-content {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 15px;
    overflow-y: auto;
    flex: 1;
    /* 让内容区域填充剩余空间 */
}

.consultant-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px;
    border-radius: 4px;
    background-color: #f5f7fa;
}

.consultant-name {
    color: #606266;
}

.card-header {
    font-size: 16px;
    color: #333;
    margin-bottom: 20px;
}

.card-number {
    font-size: 72px;
    color: #409EFF;
    text-align: center;
}

:deep(.el-pagination.is-background .el-pager li:not(.is-disabled).is-active) {
    background-color: #6777ef;
}

.status-card:nth-child(2),
.status-card:last-child {
    background-color: #6777ef;
    color: white;
}

.status-card:nth-child(2) .card-header,
.status-card:last-child .card-header {
    color: white;
}

.status-card:nth-child(2) .card-number,
.status-card:last-child .card-number {
    color: white;
}

/* 修改督导列表的最大高度 */
.status-card:nth-child(3) .list-content {
    max-height: 170px;
    /* 考虑标题和内边距的空间 */
}

/* 自定义滚动条样式 */
.status-card:nth-child(3) .list-content::-webkit-scrollbar {
    width: 5px;
}

.status-card:nth-child(3) .list-content::-webkit-scrollbar-track {
    background: transparent;
    border-radius: 3px;
    margin: 5px 0;
}

.status-card:nth-child(3) .list-content::-webkit-scrollbar-thumb {
    background: rgba(144, 147, 153, 0.3);
    border-radius: 3px;
    transition: background-color 0.3s;
}

.status-card:nth-child(3) .list-content::-webkit-scrollbar-thumb:hover {
    background: rgba(144, 147, 153, 0.5);
}

/* 确保滚动区域有足够的内边距 */
.status-card:nth-child(3) .list-content {
    padding-right: 10px;
    margin-right: -10px;
}

/* 添加平滑滚动效果 */
.status-card:nth-child(3) .list-content {
    scroll-behavior: smooth;
}

.status-card:nth-child(3) .consultant-item {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    padding: 12px 15px;
    border-radius: 4px;
    background-color: #f5f7fa;
}

.status-card:nth-child(3) .consultant-name {
    color: #606266;
    font-size: 14px;
}

.status-card:nth-child(3) :deep(.el-tag) {
    background-color: white;
    font-size: 12px;
    padding: 0 12px;
    height: 24px;
    line-height: 22px;
}

.status-card:nth-child(3) :deep(.el-tag--success) {
    border-color: #67c23a;
    color: #67c23a;
}

.status-card:nth-child(3) :deep(.el-tag--danger) {
    border-color: #f56c6c;
    color: #f56c6c;
}

.status-card:nth-child(3) {
    background-color: white;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    /* 添加overflow:hidden确保内容不溢出 */
}

.status-card:nth-child(3) .card-header {
    color: #333;
}

.status-card:nth-child(3) .list-content {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 10px;
    padding-right: 10px;
    margin-right: -10px;
    overflow-y: auto;
    height: calc(100% - 56px);
    /* 减去header的高度和margin */
}
</style>