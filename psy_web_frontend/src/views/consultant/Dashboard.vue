<script setup>
import ConsultantBaseLayout from '@/components/layout/ConsultantBaseLayout.vue'
import { ElCalendar } from 'element-plus'
import profileImage from '@/assets/组合 5.png'
import { ref } from 'vue'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import { ElConfigProvider } from 'element-plus'

const rating = ref(3.5)
const locale = zhCn
const maxConsultations = ref(2)
const introduction = ref('')

// 请假相关
const leaveDialogVisible = ref(false)
const leaveDate = ref('')

// 标签数据
const tags = ref(['抑郁'])

// 模拟请假记录数据
const leaveRecords = [
    {
        date: '2024-03-15',
        status: '已批准',
        comment: '同意请假'
    },
    {
        date: '2024-03-20',
        status: '待审批',
        comment: null
    },
    {
        date: '2024-03-25',
        status: '已拒绝',
        comment: '当日值班人员已安排'
    }
]

// 处理请假按钮点击
const handleLeaveClick = () => {
    leaveDialogVisible.value = true
}

// 处理请假提交
const handleLeaveSubmit = () => {
    if (leaveDate.value) {
        // 这里添加请假提交逻辑
        leaveRecords.push({
            date: leaveDate.value,
            status: '待审批',
            comment: null
        })
        leaveDialogVisible.value = false
        leaveDate.value = ''
    }
}
</script>

<template>
    <ConsultantBaseLayout>
        <el-config-provider :locale="locale">
            <div class="home-container">
                <div class="left-section">
                    <div class="top-left-section">
                        <div class="profile">
                            <div class="avatar">
                                <img :src="profileImage" alt="组合5" class="profile-image" />
                            </div>
                            <div class="info">
                                <div class="info-header">
                                    <h2>咨询师</h2>
                                    <div class="status">空闲</div>
                                </div>
                                <p>我的综合评价</p>
                                <el-rate v-model="rating" disabled :max="5" class="rating-stars" />
                            </div>
                        </div>
                        <div class="stats">
                            <div class="completed">
                                <p>累计完成咨询</p>
                                <h1>12345</h1>
                            </div>
                        </div>
                    </div>
                    <div class="stats-panel">
                        <div class="stat-item">
                            <div class="stat-value">35</div>
                            <div class="stat-label">今日咨询数</div>
                        </div>
                        <div class="stat-item">
                            <div class="stat-value">6:12:30</div>
                            <div class="stat-label">今日咨询时长</div>
                        </div>
                        <div class="stat-item">
                            <div class="stat-value">2</div>
                            <div class="stat-label">当前会话数</div>
                        </div>
                    </div>
                    <div class="consultant-settings">
                        <div class="settings-left">
                            <div class="setting-section">
                                <h3>设置同时咨询人数</h3>
                                <el-select v-model="maxConsultations" class="consultation-select">
                                    <el-option :value="1" label="1" />
                                    <el-option :value="2" label="2" />
                                    <el-option :value="3" label="3" />
                                    <el-option :value="4" label="4" />
                                    <el-option :value="5" label="5" />
                                </el-select>
                            </div>
                            <div class="setting-section">
                                <h3>设置标签</h3>
                                <div class="tags-container">
                                    <div class="tag">抑郁</div>
                                    <el-button class="add-tag">+</el-button>
                                </div>
                            </div>
                        </div>
                        <div class="settings-right">
                            <h3>自我介绍</h3>
                            <el-input v-model="introduction" type="textarea" :rows="6" placeholder="请输入"
                                resize="none" />
                        </div>
                    </div>
                </div>
                <div class="right-section">
                    <div class="calendar-header">
                        <div class="duty-info">
                            <span class="duty-days">本月值班天数：</span>
                            <span class="days-count">15天</span>
                        </div>
                        <el-button type="primary" size="small" class="leave-btn"
                            @click="handleLeaveClick">请假</el-button>
                    </div>
                    <div class="calendar-container">
                        <el-calendar />
                    </div>
                    <div class="leave-records">
                        <h3>本月请假记录</h3>
                        <div class="records-table">
                            <div class="table-header">
                                <div class="header-cell date-cell">请假日期</div>
                                <div class="header-cell status-cell">审批情况</div>
                                <div class="header-cell comment-cell">审批意见</div>
                            </div>
                            <div class="table-body">
                                <div v-for="(record, index) in leaveRecords" :key="index" class="table-row">
                                    <div class="table-cell date-cell">{{ record.date }}</div>
                                    <div class="table-cell status-cell">
                                        <div
                                            :class="['status-tag', record.status === '已批准' ? 'approved' : record.status === '已拒绝' ? 'rejected' : 'pending']">
                                            {{ record.status }}
                                        </div>
                                    </div>
                                    <div class="table-cell comment-cell">{{ record.comment }}</div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 请假弹窗 -->
            <el-dialog v-model="leaveDialogVisible" title="选择请假日期" width="30%" :close-on-click-modal="false">
                <div class="leave-dialog-content">
                    <el-date-picker v-model="leaveDate" type="date" placeholder="选择日期" format="YYYY-MM-DD"
                        value-format="YYYY-MM-DD" :disabled-date="(time) => time.getTime() < Date.now() - 8.64e7" />
                </div>
                <template #footer>
                    <span class="dialog-footer">
                        <el-button @click="leaveDialogVisible = false">取消</el-button>
                        <el-button type="primary" @click="handleLeaveSubmit">
                            提交
                        </el-button>
                    </span>
                </template>
            </el-dialog>
        </el-config-provider>
    </ConsultantBaseLayout>
</template>

<style scoped>
.home-container {
    display: flex;
    justify-content: space-between;
    gap: 20px;
    background-color: #f5f7fa;
    height: 100vh;
}

.left-section {
    display: flex;
    flex-direction: column;
    gap: 20px;
    width: 50%;
    padding: 20px;
}

.right-section {
    flex: 1;
    margin-top: 20px;
    margin-right: 0;
    display: flex;
    flex-direction: column;
}

.top-left-section {
    display: flex;
    background-color: #ffffff;
    border-radius: 20px 0 0 20px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    justify-content: space-between;
    padding: 0;
    overflow: hidden;
}

.profile {
    display: flex;
    align-items: center;
    flex: 0.65;
    height: 100%;
}

.avatar {
    margin-right: 20px;
    height: 100%;
}

.profile-image {
    width: 140px;
    height: 100%;
    border-radius: 0;
    display: block;
    object-fit: cover;
}

.info {
    padding: 20px;
    position: relative;
    flex: 1;
}

.info-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 10px;
}

.info h2 {
    margin: 0;
    font-size: 22px;
    color: #333;
}

.info p {
    margin: 5px 0;
    color: #666;
    font-size: 12px;
}

.status {
    background-color: #4caf50;
    color: #fff;
    padding: 2px 8px;
    border-radius: 12px;
    font-size: 12px;
}

.stats {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    background-color: #557ff7;
    color: #fff;
    padding: 20px;
    flex: 0.35;
}

.completed {
    text-align: center;
    width: 100%;
}

.completed p {
    margin: 0;
    font-size: 12px;
    font-weight: 600;
}

.completed h1 {
    margin: 0;
    font-size: 35px;
    font-weight: 600;
}

:deep(.rating-stars) {
    display: flex;
    gap: 0px;
}

:deep(.rating-stars .el-rate__item) {
    --el-rate-star-size: 40px !important;
}

:deep(.rating-stars .el-rate__icon) {
    font-size: 40px !important;
    margin-right: 0px;
}

.stats-panel {
    background-color: #ffffff;
    border-radius: 20px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    padding: 20px;
    display: flex;
    position: relative;
}

.stat-item {
    flex: 1;
    text-align: center;
    position: relative;
    padding: 20px 0;
    display: flex;
    flex-direction: column;
    align-items: center;
}

.stat-item:not(:last-child)::after {
    content: '';
    position: absolute;
    right: 0;
    top: 50%;
    transform: translateY(-50%);
    height: 60%;
    width: 1px;
    background-color: #e0e0e0;
}

.stat-value {
    font-size: 35px;
    font-weight: 600;
    color: #333;
    margin-bottom: 8px;
}

.stat-label {
    font-size: 14px;
    color: #666;
}

.calendar-container {
    height: 405px;
    display: flex;
    flex-direction: column;
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    margin-right: 20px;
}

.calendar-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-bottom: 1px solid #eee;
    height: 45px;
    margin-right: 20px;
}

.duty-info {
    display: flex;
    align-items: center;
    gap: 8px;
}

.duty-days {
    font-size: 14px;
    color: #666;
}

.days-count {
    font-size: 16px;
    font-weight: 600;
    color: #333;
}

.leave-btn {
    background-color: #557ff7;
    border: none;
    width: 80px;
    height: 35px;
    padding: 0;
    font-size: 16px;
}

:deep(.el-button--small) {
    width: 80px;
    height: 35px;
    padding: 0;
    font-size: 16px;
}

:deep(.el-calendar) {
    border: none;
    height: calc(100% - 45px);
    display: flex;
    flex-direction: column;
    padding: 0;
}

:deep(.el-calendar__header) {
    padding: 8px 20px 0;
    flex-shrink: 0;
    margin-bottom: 0;
    height: 40px;
}

:deep(.el-calendar__body) {
    padding: 4px 20px 12px;
    flex: 1;
    display: flex;
    flex-direction: column;
}

:deep(.el-calendar__title) {
    font-size: 16px;
    color: #333;
    font-weight: 600;
}

:deep(.el-button) {
    padding: 4px 8px;
}

:deep(.el-calendar-table) {
    border: none;
    flex: 1;
    height: 100%;
}

:deep(.el-calendar-table tr) {
    height: 40px;
}

:deep(.el-calendar-table td) {
    border: none;
    padding: 2px;
    height: 40px;
}

:deep(.el-calendar-day) {
    height: 36px;
    line-height: 36px;
    padding: 0;
    font-size: 14px;
    display: flex;
    align-items: center;
    justify-content: center;
}

:deep(.el-calendar__button-group) {
    padding-top: 0;
}

:deep(.el-calendar__button-group .el-button-group) {
    transform: scale(0.8);
    transform-origin: right top;
}

:deep(.is-selected) {
    background-color: #557ff7;
    color: #fff;
    border-radius: 4px;
}

:deep(.current) {
    color: #557ff7;
    font-weight: bold;
}

.records-table {
    width: 100%;
}

.table-header {
    display: flex;
    border-bottom: none;
    background: none;
    margin-bottom: 20px;
}

.header-cell {
    padding: 0;
    font-size: 16px;
    color: #333;
    font-weight: 600;
    display: flex;
    align-items: center;
}

.header-cell.date-cell {
    flex: 1;
    justify-content: center;
}

.header-cell.status-cell {
    width: 120px;
    justify-content: center;
}

.header-cell.comment-cell {
    flex: 1;
    justify-content: center;
}

.table-body {
    display: flex;
    flex-direction: column;
    min-height: 200px;
}

.table-row {
    display: flex;
    border: none;
}

.table-cell {
    padding: 12px 0;
    color: #666;
    font-size: 14px;
    display: flex;
    align-items: center;
}

.table-cell.date-cell {
    flex: 1;
    justify-content: center;
}

.table-cell.status-cell {
    width: 120px;
    justify-content: center;
}

.table-cell.comment-cell {
    flex: 1;
    justify-content: center;
}

.status-tag {
    padding: 2px 12px;
    border-radius: 12px;
    font-size: 14px;
    min-width: 80px;
    text-align: center;
}

.status-tag.approved {
    background-color: #e8f5e9;
    color: #4caf50;
}

.status-tag.rejected {
    background-color: #ffebee;
    color: #f44336;
}

.status-tag.pending {
    background-color: #fff3e0;
    color: #ff9800;
}

.leave-records {
    margin-top: 20px;
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    margin-right: 20px;
    margin-bottom: 20px;
    overflow: hidden;
    padding: 24px;
}

.leave-records h3 {
    font-size: 16px;
    color: #333;
    margin: 0 0 24px 0;
    text-align: center;
    border: none;
    padding: 0;
}

.consultant-settings {
    background-color: #ffffff;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    padding: 20px;
    display: flex;
    gap: 20px;
    margin-top: 20px;
}

.consultant-settings .settings-left {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.consultant-settings .settings-right {
    flex: 1;
    display: flex;
    flex-direction: column;
}

.setting-section {
    background: #f5f7fa;
    border-radius: 12px;
    padding: 20px;
}

.setting-section h3,
.consultant-settings .settings-right h3 {
    font-size: 16px;
    color: #333;
    margin: 0 0 16px 0;
    font-weight: normal;
}

.consultation-select {
    width: 100%;
}

:deep(.el-input__wrapper) {
    background-color: #fff;
}

.tags-container {
    display: flex;
    gap: 8px;
    flex-wrap: wrap;
}

.tag {
    background-color: #e6f0ff;
    color: #557ff7;
    padding: 6px 12px;
    border-radius: 4px;
    font-size: 14px;
}

.add-tag {
    padding: 6px 12px;
    border: 1px dashed #dcdfe6;
    background: none;
    color: #909399;
}

:deep(.el-textarea__inner) {
    background-color: #f5f7fa;
    border: none;
    padding: 12px;
    font-size: 14px;
    color: #333;
    border-radius: 12px;
}

:deep(.el-select .el-input__wrapper) {
    background-color: #fff;
    box-shadow: none;
    border: 1px solid #dcdfe6;
}

.leave-dialog-content {
    display: flex;
    justify-content: center;
    padding: 20px 0;
}

.dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
}
</style>