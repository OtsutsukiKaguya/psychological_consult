<script setup>
import ConsultantBaseLayout from '@/components/layout/ConsultantBaseLayout.vue'
import { ElCalendar } from 'element-plus'
import profileImage from '@/assets/组合 5.png'
import { ref, computed, onMounted, h, watch } from 'vue'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import { ElConfigProvider, ElMessage } from 'element-plus'
import axios from 'axios'
import { API, BASE_URL, CHAT_BASE_URL } from '@/config'
import dayjs from 'dayjs'
import { ElMessageBox } from 'element-plus'
import { Plus, Close, Check } from '@element-plus/icons-vue'

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
const leaveReason = ref('')

// 判断日期是否可选
const disabledDate = (time) => {
    const formattedDate = dayjs(time).format('YYYY-MM-DD')
    const isPastDate = time.getTime() < Date.now() - 8.64e7  // 是否是过去的日期
    const isNotDutyDate = !dutyDates.value.includes(formattedDate)  // 是否不是值班日期
    return isPastDate || isNotDutyDate  // 如果是过去的日期或不是值班日期，则禁用
}

// 标签数据
const tags = ref(['抑郁'])

// 本月请假记录
const leaveRecords = ref([])

// 获取请假记录
const fetchLeaveRecords = async () => {
    if (!currentUser.value?.id) return
    try {
        const response = await axios.get(API.LEAVE.SHOW_LEAVE_BY_ID(currentUser.value.id))

        if (response.data.code === 0 && Array.isArray(response.data.data)) {
            // 只显示本月的请假记录
            const currentMonth = dayjs().format('YYYY-MM')

            leaveRecords.value = response.data.data.filter(item => item.dutyDate.startsWith(currentMonth)).map(item => {
                let status = ''
                let comment = ''

                if (item.isAgree) {
                    status = '同意请假'
                    comment = '同意请假'
                } else if (!item.isAgree && item.leaveComment == null) {
                    status = '待审批'
                    comment = ''
                } else if (!item.isAgree && item.leaveComment) {
                    status = '已拒绝'
                    comment = item.leaveComment
                }

                return {
                    date: item.dutyDate,
                    status,
                    comment
                }
            })
        } else {
            leaveRecords.value = []
        }
    } catch (e) {
        leaveRecords.value = []
    }
}

// 值班日期列表
const allDutyDates = ref([]) // 保存所有 dutyDate 及 isLeave 信息
const dutyDates = ref([])    // 只保存 isLeave === 0 的日期
const leaveDates = ref([])   // 只保存 isLeave === 1 的日期

// 计算当前月值班天数
const currentMonthDutyDays = computed(() => {
    const currentMonth = dayjs().format('YYYY-MM')
    return dutyDates.value.filter(date => date.startsWith(currentMonth)).length
})

// 当前选中的日期
const selectedDate = ref(null)

// 监听值班日期列表的变化
watch(dutyDates, (newDates) => {
}, { deep: true })

// 获取值班信息
const fetchDutyInfo = async () => {
    if (!currentUser.value?.id) {
        return
    }
    try {
        const response = await axios.get(API.DUTY.GET_BY_ID(currentUser.value.id))
        if (response.data.code === 0 && response.data.data) {
            allDutyDates.value = response.data.data
            dutyDates.value = response.data.data
                .filter(item => item.isLeave === 0)
                .map(item => dayjs(item.dutyDate).format('YYYY-MM-DD'))
            leaveDates.value = response.data.data
                .filter(item => item.isLeave === 1)
                .map(item => dayjs(item.dutyDate).format('YYYY-MM-DD'))
        }
    } catch (error) {
        ElMessage.error('获取值班信息失败')
    }
}

// 检查日期是否是值班日期
const isDutyDate = (date) => {
    const formattedDate = dayjs(date).format('YYYY-MM-DD')
    return dutyDates.value.includes(formattedDate)
}

const isLeaveDate = (date) => {
    const formattedDate = dayjs(date).format('YYYY-MM-DD')
    return leaveDates.value.includes(formattedDate)
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
const handleLeaveSubmit = async () => {
    if (leaveDate.value && leaveReason.value) {
        try {
            const res = await axios.post(`${BASE_URL}/api/leave/addleave`, {
                staffId: currentUser.value.id,
                dutyDate: leaveDate.value,
                leaveReason: leaveReason.value
            })
            if (res.data.code === 0) {
                ElMessage.success('请假申请已提交')
            } else {
                ElMessage.error(res.data.message || '请假申请失败')
            }
        } catch (e) {
            ElMessage.error('请假申请失败')
        }
        leaveDialogVisible.value = false
        leaveDate.value = ''
        leaveReason.value = ''
        await fetchLeaveRecords()
        await fetchDutyInfo()
    } else {
        ElMessage.warning('请选择日期并填写请假理由')
    }
}

const homepageStats = ref({
    averageRating: 0,
    totalSessions: 0,
    dailySessions: 0,
    dailyTotalDuration: 0,
    ongoingSessions: 0
})

// 秒转时分秒
function formatDuration(seconds) {
    const h = Math.floor(seconds / 3600)
    const m = Math.floor((seconds % 3600) / 60)
    const s = seconds % 60
    return `${h}:${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`
}

const fetchHomepageStats = async () => {
    try {
        const id = currentUser.value?.id
        const date = dayjs().format('YYYY-MM-DD')
        const res = await axios.get(`${BASE_URL}/api/counselor/homepage`, {
            params: { id, date }
        })
        console.log('[首页] /api/counselor/homepage 返回:', res.data)
        if (res.data.code === 0 && res.data.data) {
            homepageStats.value = res.data.data
            rating.value = res.data.data.averageRating
            console.log('[首页] 最终赋值 homepageStats:', homepageStats.value)
        }
    } catch (e) {
        // 忽略错误
    }
}

// 获取当前会话数
const fetchOngoingSessions = async () => {
    try {
        const res = await axios.get(`${CHAT_BASE_URL}/api/statistics/ongoing-sessions`)
        if (res.data.code === 0 && Array.isArray(res.data.data)) {
            homepageStats.value.ongoingSessions = res.data.data.length
        }
    } catch (e) {
        console.error('获取当前会话数失败:', e)
        homepageStats.value.ongoingSessions = 0
    }
}

// 获取咨询师基本信息
const fetchCounselorInfo = async () => {
    if (!currentUser.value?.id) return

    try {
        const res = await axios.get(`${BASE_URL}/api/search/counselor/${currentUser.value.id}`)
        if (res.data.code === 0 && Array.isArray(res.data.data) && res.data.data.length > 0) {
            const counselorInfo = res.data.data[0]

            // 设置同时咨询人数
            if (counselorInfo.counselorSameTime) {
                maxConsultations.value = counselorInfo.counselorSameTime
            }

            // 设置标签
            if (counselorInfo.tag) {
                tags.value = counselorInfo.tag.split(',').map(tag => tag.trim()).filter(tag => tag)
            }

            // 设置评分和总咨询数
            if (counselorInfo.averageRating) {
                rating.value = counselorInfo.averageRating
            }

            if (counselorInfo.totalSessions) {
                homepageStats.value.totalSessions = counselorInfo.totalSessions
            }
        }
    } catch (e) {
        console.error('获取咨询师基本信息失败:', e)
    }
}

// 组件挂载时获取值班信息和请假记录
onMounted(async () => {
    await Promise.all([
        fetchHomepageStats(),
        fetchOngoingSessions(),
        fetchDutyInfo(),
        fetchLeaveRecords(),
        fetchCounselorInfo()
    ])
    if (currentUser.value?.selfDescription) {
        introduction.value = currentUser.value.selfDescription
    }
})

// 判断是否是过去的日期
const isPastDate = (date) => {
    return dayjs(date).isBefore(dayjs(), 'day')
}

// 判断是否是今天
const isToday = (date) => {
    return dayjs(date).isSame(dayjs(), 'day')
}

// 添加标签相关
const tagInputVisible = ref(false)
const tagInputValue = ref('')

// 处理添加标签
const showTagInput = () => {
    tagInputVisible.value = true
    // 等待DOM更新后聚焦输入框
    setTimeout(() => {
        document.getElementById('tagInput')?.focus()
    }, 10)
}

// 处理添加标签确认
const handleTagConfirm = async () => {
    const value = tagInputValue.value.trim()
    if (value) {
        // 先本地更新
        const newTags = [...tags.value, value]
        // 调用API
        await updateTags(newTags)
        // 成功后重置输入框
        tagInputValue.value = ''
        tagInputVisible.value = false
    }
}

// 处理删除标签
const handleTagDelete = async (index) => {
    try {
        await ElMessageBox.confirm('确定要删除这个标签吗？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
        })

        // 删除本地标签
        const newTags = [...tags.value]
        newTags.splice(index, 1)

        // 调用API更新
        await updateTags(newTags)
    } catch (e) {
        // 用户取消或发生错误，不做处理
    }
}

// 更新标签API调用
const updateTags = async (newTags) => {
    if (!currentUser.value?.id) return

    try {
        const tagString = newTags.join(',')
        const res = await axios.post(`${BASE_URL}/api/counselor/update-tag`, null, {
            params: {
                id: currentUser.value.id,
                tag: tagString
            }
        })

        if (res.data.code === 0) {
            // 更新成功，更新本地标签
            tags.value = newTags
            ElMessage.success('标签更新成功')
        } else {
            ElMessage.error(res.data.message || '标签更新失败')
        }
    } catch (e) {
        console.error('更新标签失败:', e)
        ElMessage.error('标签更新失败')
    }
}

// 更新同时咨询人数
const handleMaxConsultationsChange = async (newValue) => {
    if (!currentUser.value?.id) return

    try {
        const res = await axios.post(`${BASE_URL}/api/counselor/update-sametime`, null, {
            params: {
                id: currentUser.value.id,
                counselorSametime: newValue
            }
        })

        if (res.data.code === 0) {
            ElMessage.success('同时咨询人数更新成功')
        } else {
            // 如果更新失败，恢复原值
            ElMessage.error(res.data.message || '更新同时咨询人数失败')
            await fetchCounselorInfo() // 重新获取原始值
        }
    } catch (e) {
        console.error('更新同时咨询人数失败:', e)
        ElMessage.error('更新同时咨询人数失败')
        await fetchCounselorInfo() // 重新获取原始值
    }
}

// 更新自我介绍
const handleIntroductionUpdate = async () => {
    if (!currentUser.value?.id) return

    try {
        const res = await axios.post(`${BASE_URL}/api/person/update-description`, null, {
            params: {
                id: currentUser.value.id,
                selfDescription: introduction.value
            }
        })

        if (res.data.code === 0) {
            ElMessage.success('自我介绍更新成功')
            // 更新本地存储的用户信息
            if (currentUser.value) {
                const userInfo = { ...currentUser.value, selfDescription: introduction.value }
                localStorage.setItem('userInfo', JSON.stringify(userInfo))
            }
        } else {
            ElMessage.error(res.data.message || '更新自我介绍失败')
        }
    } catch (e) {
        console.error('更新自我介绍失败:', e)
        ElMessage.error('更新自我介绍失败')
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
                                <h1>{{ homepageStats.totalSessions }}</h1>
                            </div>
                        </div>
                    </div>
                    <div class="stats-panel">
                        <div class="stat-item">
                            <div class="stat-value">{{ homepageStats.dailySessions }}</div>
                            <div class="stat-label">今日咨询数</div>
                        </div>
                        <div class="stat-item">
                            <div class="stat-value">{{ formatDuration(homepageStats.dailyTotalDuration) }}</div>
                            <div class="stat-label">累计咨询时长</div>
                        </div>
                        <div class="stat-item">
                            <div class="stat-value">{{ homepageStats.ongoingSessions }}</div>
                            <div class="stat-label">当前会话数</div>
                        </div>
                    </div>
                    <div class="consultant-settings">
                        <div class="settings-left">
                            <div class="setting-section">
                                <h3>设置同时咨询人数</h3>
                                <el-select v-model="maxConsultations" class="consultation-select"
                                    @change="handleMaxConsultationsChange">
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
                                    <div v-for="(tag, index) in tags" :key="index" class="tag">
                                        {{ tag }}
                                        <el-icon class="tag-delete-icon" @click="handleTagDelete(index)">
                                            <Close />
                                        </el-icon>
                                    </div>

                                    <!-- 添加标签按钮 -->
                                    <el-button v-if="!tagInputVisible" class="add-tag" @click="showTagInput">
                                        <el-icon>
                                            <Plus />
                                        </el-icon>
                                    </el-button>

                                    <!-- 添加标签输入框 -->
                                    <div v-else class="tag-input-container">
                                        <el-input id="tagInput" v-model="tagInputValue" class="tag-input" size="small"
                                            placeholder="请输入标签名称" @keyup.enter="handleTagConfirm"
                                            @blur="handleTagConfirm" />
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="settings-right">
                            <h3>自我介绍</h3>
                            <div class="introduction-container">
                                <el-input v-model="introduction" type="textarea" :rows="6"
                                    :placeholder="currentUser?.selfDescription || '请输入'" resize="none"
                                    @blur="handleIntroductionUpdate" />
                                <el-button type="primary" class="save-btn" @click="handleIntroductionUpdate">
                                    <el-icon class="save-icon">
                                        <Check />
                                    </el-icon>
                                    保存介绍
                                </el-button>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="right-section">
                    <div class="calendar-header">
                        <div class="duty-info">
                            <span class="duty-days">本月值班天数：</span>
                            <span class="days-count">{{ currentMonthDutyDays }}天</span>
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
                                    { 'leave-cell': isLeaveDate(data.day) },
                                    { 'past-date': isPastDate(data.day) },
                                    { 'today': isToday(data.day) }
                                ]">
                                    <span>{{ data.day.split('-')[2] }}</span>
                                    <div v-if="isDutyDate(data.day)" class="duty-text">值班</div>
                                    <div v-else-if="isLeaveDate(data.day)" class="leave-text">请假</div>
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
                <div class="leave-dialog-content">
                    <el-input v-model="leaveReason" type="textarea" :rows="3" placeholder="请输入请假理由" />
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
    position: relative;
    display: flex;
    align-items: center;
}

.tag-delete-icon {
    margin-left: 6px;
    font-size: 12px;
    color: #909399;
    cursor: pointer;
    transition: all 0.2s;
}

.tag-delete-icon:hover {
    color: #f56c6c;
}

.tag-input-container {
    display: inline-block;
    margin-left: 8px;
    width: 120px;
}

.tag-input {
    width: 100%;
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

.leave-cell {
    background-color: #e0eaff !important;
    border: 1px solid #b3c6ff !important;
}

.leave-text {
    font-size: 11px;
    color: #409EFF;
    background: #fff;
    padding: 1px 6px;
    border-radius: 8px;
    border: 1px solid #b3c6ff;
}

.settings-right .introduction-container {
    position: relative;
    display: flex;
    flex-direction: column;
}

.settings-right .save-btn {
    position: absolute;
    bottom: 12px;
    right: 12px;
    z-index: 2;
    background: linear-gradient(45deg, #557ff7, #6e93ff);
    border: none;
    padding: 8px 16px;
    border-radius: 20px;
    font-size: 14px;
    font-weight: 500;
    color: white;
    transition: all 0.3s ease;
    box-shadow: 0 2px 6px rgba(85, 127, 247, 0.3);
    display: flex;
    align-items: center;
    gap: 4px;
}

.settings-right .save-btn:hover {
    background: linear-gradient(45deg, #4066d6, #557ff7);
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(85, 127, 247, 0.4);
}

.settings-right .save-btn:active {
    transform: translateY(0);
    box-shadow: 0 2px 6px rgba(85, 127, 247, 0.3);
}

.save-icon {
    font-size: 14px;
}
</style>