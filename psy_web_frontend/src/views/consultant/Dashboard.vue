<script setup>
import ConsultantBaseLayout from '@/components/layout/ConsultantBaseLayout.vue'
import { ElCalendar } from 'element-plus'
import profileImage from '@/assets/组合 5.png'
import { ref, computed, onMounted, h, watch } from 'vue'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import { ElConfigProvider, ElMessage } from 'element-plus'
import axios from 'axios'
import { API } from '@/config'
import dayjs from 'dayjs'

// 获取当前用户信息
const currentUser = computed(() => {
    const userInfo = localStorage.getItem('userInfo')
    return userInfo ? JSON.parse(userInfo) : null
})

const rating = ref(3.5)
const locale = zhCn
const maxConsultations = ref(2)
const introduction = ref('')

// 请假相关
const leaveDialogVisible = ref(false)
const leaveDate = ref('')

// 判断日期是否可选
const disabledDate = (time) => {
    const formattedDate = dayjs(time).format('YYYY-MM-DD')
    const isPastDate = time.getTime() < Date.now() - 8.64e7  // 是否是过去的日期
    const isNotDutyDate = !dutyDates.value.includes(formattedDate)  // 是否不是值班日期
    return isPastDate || isNotDutyDate  // 如果是过去的日期或不是值班日期，则禁用
}

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

// 值班日期列表
const dutyDates = ref([])

// 当前选中的日期
const selectedDate = ref(null)

// 监听值班日期列表的变化
watch(dutyDates, (newDates) => {
    console.log('值班日期列表已更新:', newDates)
}, { deep: true })

// 获取值班信息
const fetchDutyInfo = async () => {
    if (!currentUser.value?.id) {
        console.error('未获取到用户ID')
        return
    }

    try {
        const response = await axios.get(API.DUTY.GET_BY_ID(currentUser.value.id))

        if (response.data.code === 0 && response.data.data) {
            dutyDates.value = response.data.data.map(item => {
                const formattedDate = dayjs(item.dutyDate).format('YYYY-MM-DD')
                return formattedDate
            })
            console.log('处理后的值班日期列表:', dutyDates.value)
        }
    } catch (error) {
        console.error('获取值班信息失败:', error)
        ElMessage.error('获取值班信息失败')
    }
}

// 检查日期是否是值班日期
const isDutyDate = (date) => {
    console.log('==================== 日期匹配检查 ====================')
    console.log('当前检查的日期:', date)
    console.log('值班日期列表:', dutyDates.value)

    const formattedDate = dayjs(date).format('YYYY-MM-DD')
    console.log('格式化后的当前日期:', formattedDate)

    const isMatch = dutyDates.value.includes(formattedDate)
    console.log('是否匹配:', isMatch)
    console.log('=================================================')
    return isMatch
}

// 格式化日期
const formatDate = (date) => {
    return dayjs(date).format('YYYY-MM-DD')
}

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

// 组件挂载时获取值班信息
onMounted(async () => {
    await fetchDutyInfo()
})

// 判断是否是过去的日期
const isPastDate = (date) => {
    return dayjs(date).isBefore(dayjs(), 'day')
}

// 判断是否是今天
const isToday = (date) => {
    return dayjs(date).isSame(dayjs(), 'day')
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
                            <el-input v-model="introduction" type="textarea" :rows="6"
                                :placeholder="currentUser?.selfDescription || '请输入'" resize="none" />
                        </div>
                    </div>
                </div>
                <div class="right-section">
                    <div class="calendar-header">
                        <div class="duty-info">
                            <span class="duty-days">本月值班天数：</span>
                            <span class="days-count">{{ dutyDates.length }}天</span>
                        </div>
                        <el-button type="primary" size="small" class="leave-btn"
                            @click="handleLeaveClick">请假</el-button>
                    </div>
                    <div class="calendar-container">
                        <el-calendar v-model="selectedDate">
                            <template #date-cell="{ data }">
                                <div :class="[
                                    'calendar-cell',
                                    { 'duty-cell': isDutyDate(data.day) },
                                    { 'past-date': isPastDate(data.day) },
                                    { 'today': isToday(data.day) }
                                ]">
                                    <span>{{ data.day.split('-')[2] }}</span>
                                    <div v-if="isDutyDate(data.day)" class="duty-text">值班</div>
                                </div>
                            </template>
                        </el-calendar>
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
                    <el-date-picker v-model="leaveDate" type="date" placeholder="选择值班日期" format="YYYY-MM-DD"
                        value-format="YYYY-MM-DD" :disabled-date="disabledDate" />
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
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    margin-right: 20px;
    padding: 0;
    overflow: hidden;
    max-width: 800px;
}

.calendar-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 20px;
    border-bottom: 1px solid #f0f0f0;
    background-color: #fff;
    height: 60px;
}

.duty-info {
    display: flex;
    align-items: center;
    gap: 10px;
    height: 40px;
    background-color: #f8f9fc;
    padding: 0 16px;
    border-radius: 8px;
}

.duty-days {
    font-size: 14px;
    color: #666;
}

.days-count {
    font-size: 16px;
    font-weight: 600;
    color: #557ff7;
}

.leave-btn {
    background: linear-gradient(45deg, #557ff7, #6e93ff);
    border: none;
    padding: 10px 24px;
    border-radius: 8px;
    font-size: 15px;
    font-weight: 500;
    color: white;
    transition: all 0.3s ease;
    box-shadow: 0 2px 6px rgba(85, 127, 247, 0.3);
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
}

.leave-btn:hover {
    background: linear-gradient(45deg, #4066d6, #557ff7);
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(85, 127, 247, 0.4);
}

.leave-btn:active {
    transform: translateY(0);
    box-shadow: 0 2px 6px rgba(85, 127, 247, 0.3);
}

:deep(.el-calendar) {
    --el-calendar-cell-width: 50px;
    background: none;
    border: none;
    height: 420px;
    display: flex;
    flex-direction: column;
}

:deep(.el-calendar__header) {
    padding: 12px 20px;
    border-bottom: 1px solid #f0f0f0;
    flex-shrink: 0;
}

:deep(.el-calendar__title) {
    color: #333;
    font-size: 16px;
    font-weight: 600;
}

:deep(.el-calendar__body) {
    padding: 8px 20px 16px;
    flex: 1;
    overflow: auto;
    height: calc(420px - 60px);
}

:deep(.el-calendar-table) {
    border-collapse: separate;
    border-spacing: 4px;
    height: 100%;
}

:deep(.el-calendar-table td) {
    border: none;
    padding: 2px;
    height: 60px;
    border-radius: 8px;
    transition: all 0.3s ease;
}

:deep(.el-calendar-table th) {
    text-align: center;
    padding: 6px 0;
    color: #666;
    font-weight: 600;
    font-size: 14px;
    height: 32px;
}

.calendar-cell {
    height: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 4px;
    border-radius: 8px;
    transition: all 0.3s ease;
    cursor: pointer;
}

.past-date {
    opacity: 0.5;
    background-color: #f5f5f5;
    cursor: not-allowed;
}

.past-date.duty-cell {
    background-color: #fef0f0 !important;
    opacity: 0.7;
}

.today {
    border: 2px solid #409EFF !important;
    font-weight: bold;
}

.today span {
    color: #409EFF;
}

.calendar-cell:not(.past-date):hover {
    background-color: #f5f7fa;
}

.duty-cell {
    background-color: #fef0f0 !important;
    border: 1px solid #fde2e2 !important;
}

.duty-cell:hover {
    background-color: #fde2e2 !important;
}

.duty-text {
    font-size: 11px;
    color: #f56c6c;
    background: #fff;
    padding: 1px 6px;
    border-radius: 8px;
    border: 1px solid #fde2e2;
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

/* 当前月份日期样式 */
:deep(.el-calendar-table td.current) {
    background-color: #fff;
    color: #333;
}

/* 其他月份日期样式 */
:deep(.el-calendar-table td.prev, .el-calendar-table td.next) {
    background-color: #fafafa;
    color: #c0c4cc;
}

/* 今天日期样式 */
:deep(.el-calendar-table td.is-today) {
    color: var(--el-color-primary);
    font-weight: 600;
}

/* 选中日期样式 */
:deep(.el-calendar-table td.is-selected) {
    background-color: var(--el-color-primary-light-9);
}

/* 日历按钮组样式 */
:deep(.el-calendar__button-group) {
    padding-left: 20px;
}

:deep(.el-calendar__button-group .el-button-group .el-button) {
    border-radius: 4px;
    margin-right: 8px;
}

/* 请假记录样式 */
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
</style>