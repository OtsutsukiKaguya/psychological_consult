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
import { ref, onMounted, computed } from 'vue'
import BaseLayout from '@/components/layout/BaseLayout.vue'
import Calendar from '@/components/schedule/Calendar.vue'
import StaffList from '@/components/schedule/StaffList.vue'
import { ElMessage } from 'element-plus'

// 当前选中的日期
const selectedDate = ref(new Date(2025, 3, 1)) // 设置为2025年4月1日

// 日程数据
const scheduleData = ref([
    {
        date: '2025-04-01',
        consultants: ['张三', '李四'],
        supervisors: ['王督导']
    },
    {
        date: '2025-04-02',
        consultants: ['王五', '赵六'],
        supervisors: ['李督导']
    },
    {
        date: '2025-04-03',
        consultants: ['孙七', '周八'],
        supervisors: ['陈督导']
    },
    {
        date: '2025-04-08',
        consultants: ['吴九', '郑十'],
        supervisors: ['刘督导']
    },
    {
        date: '2025-04-09',
        consultants: ['张三', '王五'],
        supervisors: ['王督导']
    },
    {
        date: '2025-04-10',
        consultants: ['李四', '赵六'],
        supervisors: ['李督导']
    }
])

// 根据选中日期获取值班人员
const getScheduleForSelectedDate = () => {
    const year = selectedDate.value.getFullYear()
    const month = String(selectedDate.value.getMonth() + 1).padStart(2, '0')
    const day = String(selectedDate.value.getDate()).padStart(2, '0')
    const dateStr = `${year}-${month}-${day}`
    return scheduleData.value.find(schedule => schedule.date === dateStr)
}

// 当前值班人员列表
const currentSchedule = computed(() => {
    const schedule = getScheduleForSelectedDate()
    return {
        consultants: schedule ? schedule.consultants.map(name => ({
            id: name,
            name: name,
            avatar: 'path/to/avatar'
        })) : [],
        supervisors: schedule ? schedule.supervisors.map(name => ({
            id: name,
            name: name,
            avatar: 'path/to/avatar'
        })) : []
    }
})

// 日期变化处理
const handleDateChange = (date) => {
    selectedDate.value = date
}

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

// 格式化日期
const formatDate = (date) => {
    const month = date.getMonth() + 1
    const day = date.getDate()
    const weekDay = ['日', '一', '二', '三', '四', '五', '六'][date.getDay()]
    return `${month}月${day}日 星期${weekDay}`
}

// 初始化
onMounted(() => {
    // TODO: 获取初始数据
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