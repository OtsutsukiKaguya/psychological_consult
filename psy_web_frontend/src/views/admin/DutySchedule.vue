<!-- 排班表页面 -->
<template>
    <BaseLayout>
        <div class="schedule-container">
            <!-- 日历区域 -->
            <div class="calendar-area">
                <Calendar :selected-date="selectedDate" :schedule-data="scheduleData" @date-change="handleDateChange" />
            </div>

            <!-- 右侧人员列表 -->
            <div class="staff-list">
                <div class="date-header">
                    {{ formatDate(selectedDate) }}
                </div>
                <StaffList :consultants="currentSchedule.consultants" :supervisors="currentSchedule.supervisors"
                    @add-consultant="handleAddConsultant" @remove-consultant="handleRemoveConsultant" />
            </div>
        </div>
    </BaseLayout>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import BaseLayout from '@/components/layout/BaseLayout.vue'
import Calendar from '@/components/schedule/Calendar.vue'
import StaffList from '@/components/schedule/StaffList.vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { API } from '@/config'

const selectedDate = ref(new Date())
const scheduleData = ref([])

// 格式化日期显示
const formatDate = (date) => {
    if (!date) return ''
    const month = date.getMonth() + 1
    const day = date.getDate()
    const weekDay = ['日', '一', '二', '三', '四', '五', '六'][date.getDay()]
    return `${month}月${day}日 星期${weekDay}`
}

// 格式化日期为 YYYY-MM-DD（用于API请求）
const formatDateForApi = (date) => {
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    return `${year}-${month}-${day}`
}

// 获取指定日期的排班信息
const fetchDutyByDate = async (date) => {
    try {
        const formattedDate = formatDateForApi(date)
        const response = await axios.get(API.DUTY.GET_BY_DATE(formattedDate))

        if (response.data.code === 0) {
            const consultants = response.data.data.filter(item => item.role === 'COUNSELOR')
            const supervisors = response.data.data.filter(item => item.role === 'TUTOR')

            // 更新或添加该日期的排班信息
            const existingIndex = scheduleData.value.findIndex(item => item.date === formattedDate)
            const scheduleItem = {
                date: formattedDate,
                consultants: consultants.length > 0 ? consultants.map(c => c.name) : null,
                supervisors: supervisors.length > 0 ? supervisors.map(s => s.name) : null
            }

            if (existingIndex !== -1) {
                scheduleData.value[existingIndex] = scheduleItem
            } else {
                scheduleData.value.push(scheduleItem)
            }
        }
    } catch (error) {
        ElMessage.error('获取排班信息失败')
    }
}

// 获取当前月份的排班信息
const fetchCurrentMonthDuty = async () => {
    try {
        // 清空现有数据
        scheduleData.value = []

        const currentYear = selectedDate.value.getFullYear()
        const currentMonth = selectedDate.value.getMonth()
        const daysInMonth = new Date(currentYear, currentMonth + 1, 0).getDate()

        // 获取上个月的最后几天
        const firstDayOfMonth = new Date(currentYear, currentMonth, 1).getDay()
        const daysFromPrevMonth = firstDayOfMonth === 0 ? 6 : firstDayOfMonth - 1
        const prevMonth = new Date(currentYear, currentMonth, 0)
        const prevMonthDays = prevMonth.getDate()

        // 获取下个月的前几天
        const lastDayOfMonth = new Date(currentYear, currentMonth + 1, 0).getDay()
        const nextMonthDays = lastDayOfMonth === 6 ? 0 : 6 - lastDayOfMonth

        // 获取上个月最后几天的数据
        for (let i = daysFromPrevMonth; i > 0; i--) {
            const date = new Date(currentYear, currentMonth - 1, prevMonthDays - i + 1)
            await fetchDutyByDate(date)
        }

        // 获取当前月份的数据
        for (let i = 1; i <= daysInMonth; i++) {
            const date = new Date(currentYear, currentMonth, i)
            await fetchDutyByDate(date)
        }

        // 获取下个月前几天的数据
        for (let i = 1; i <= nextMonthDays; i++) {
            const date = new Date(currentYear, currentMonth + 1, i)
            await fetchDutyByDate(date)
        }
    } catch (error) {
        ElMessage.error('获取当月排班信息失败')
    }
}

// 获取选中日期的排班信息
const getScheduleForSelectedDate = (date) => {
    const formattedDate = formatDateForApi(date)
    const schedule = scheduleData.value.find(item => item.date === formattedDate)
    return {
        consultants: schedule?.consultants || [],
        supervisors: schedule?.supervisors || []
    }
}

// 处理日期变化
const handleDateChange = (date) => {
    selectedDate.value = date
}

// 当前值班人员列表
const currentSchedule = computed(() => {
    const schedule = getScheduleForSelectedDate(selectedDate.value)
    return {
        consultants: schedule.consultants.map(name => ({
            id: name,
            name: name,
            avatar: ''
        })),
        supervisors: schedule.supervisors.map(name => ({
            id: name,
            name: name,
            avatar: ''
        }))
    }
})

// 添加咨询师
const handleAddConsultant = () => {
    // TODO: 实现添加咨询师逻辑
    ElMessage.info('添加咨询师功能待实现')
}

// 移除咨询师
const handleRemoveConsultant = (id) => {
    // TODO: 实现移除咨询师逻辑
    ElMessage.info('移除咨询师功能待实现')
}

// 页面加载时获取数据
onMounted(async () => {
    await fetchCurrentMonthDuty()
})
</script>

<style scoped>
.schedule-container {
    display: flex;
    gap: 20px;
    height: 100%;
    background-color: #f6f6f9;
}

.calendar-area {
    flex: 1;
    background: white;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
    display: flex;
    flex-direction: column;
    min-height: 0;
}

.staff-list {
    width: 300px;
    background: white;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
    display: flex;
    flex-direction: column;
    min-height: 0;
}

.date-header {
    font-size: 16px;
    font-weight: 500;
    color: #333;
    margin-bottom: 20px;
    text-align: center;
}
</style>