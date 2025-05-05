<template>
    <div class="calendar">
        <!-- 日历头部 -->
        <div class="calendar-header">
            <el-button-group>
                <el-button @click="previousMonth">
                    <el-icon>
                        <ArrowLeft />
                    </el-icon>
                </el-button>
                <el-button @click="nextMonth">
                    <el-icon>
                        <ArrowRight />
                    </el-icon>
                </el-button>
            </el-button-group>
            <span class="month-title">{{ currentYear }}年 {{ currentMonth + 1 }}月</span>
        </div>

        <!-- 星期标题 -->
        <div class="weekdays">
            <div v-for="day in weekDays" :key="day" class="weekday">{{ day }}</div>
        </div>

        <!-- 日历格子 -->
        <div class="calendar-grid">
            <div v-for="(day, index) in days" :key="index" class="calendar-cell" :class="{
                'current-month': day.currentMonth,
                'selected': isSelected(day.date),
                'today': isToday(day.date)
            }" @click="selectDate(day.date)">
                <CalendarCell :date="day.date" :is-current-month="day.currentMonth"
                    :schedule="getScheduleForDate(day.date)" />
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ArrowLeft, ArrowRight } from '@element-plus/icons-vue'
import CalendarCell from './CalendarCell.vue'

// Props
const props = defineProps({
    selectedDate: {
        type: Date,
        required: true
    },
    scheduleData: {
        type: Array,
        required: true
    }
})

// Emits
const emit = defineEmits(['dateChange', 'monthChange'])

// 当前显示的年月
const currentYear = ref(new Date().getFullYear())
const currentMonth = ref(new Date().getMonth())

// 星期标题
const weekDays = ['日', '一', '二', '三', '四', '五', '六']

// 计算当月的所有日期
const days = computed(() => {
    const result = []
    const firstDay = new Date(currentYear.value, currentMonth.value, 1)
    const lastDay = new Date(currentYear.value, currentMonth.value + 1, 0)

    // 上个月的最后几天
    const firstDayWeekday = firstDay.getDay()
    for (let i = firstDayWeekday - 1; i >= 0; i--) {
        const date = new Date(currentYear.value, currentMonth.value, -i)
        result.push({
            date,
            currentMonth: false
        })
    }

    // 当月的所有天
    for (let i = 1; i <= lastDay.getDate(); i++) {
        const date = new Date(currentYear.value, currentMonth.value, i)
        result.push({
            date,
            currentMonth: true
        })
    }

    // 下个月的开始几天
    const remainingDays = 42 - result.length
    for (let i = 1; i <= remainingDays; i++) {
        const date = new Date(currentYear.value, currentMonth.value + 1, i)
        result.push({
            date,
            currentMonth: false
        })
    }

    return result
})

// 切换月份
const previousMonth = () => {
    if (currentMonth.value === 0) {
        currentYear.value--
        currentMonth.value = 11
    } else {
        currentMonth.value--
    }
    emit('monthChange', new Date(currentYear.value, currentMonth.value, 1))
}

const nextMonth = () => {
    if (currentMonth.value === 11) {
        currentYear.value++
        currentMonth.value = 0
    } else {
        currentMonth.value++
    }
    emit('monthChange', new Date(currentYear.value, currentMonth.value, 1))
}

// 日期选择
const selectDate = (date) => {
    emit('dateChange', date)
}

// 判断日期是否被选中
const isSelected = (date) => {
    return date.toDateString() === props.selectedDate.toDateString()
}

// 判断是否是今天
const isToday = (date) => {
    return date.toDateString() === new Date().toDateString()
}

// 获取指定日期的排班数据
const getScheduleForDate = (date) => {
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const dateStr = `${year}-${month}-${day}`
    const schedule = props.scheduleData.find(schedule => schedule.date === dateStr)
    if (schedule) {
        // console.log('Found schedule for date:', dateStr, schedule)
    }
    return schedule
}

// 初始化
onMounted(() => {
    // console.log('Calendar mounted with year:', props.selectedDate.getFullYear())
    // console.log('Calendar mounted with month:', props.selectedDate.getMonth())
    currentYear.value = props.selectedDate.getFullYear()
    currentMonth.value = props.selectedDate.getMonth()
})
</script>

<style scoped>
.calendar {
    width: 100%;
    background: white;
    display: flex;
    flex-direction: column;
    flex: 1;
    min-height: 0;
}

.calendar-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 16px;
    padding-top: 0;
    border-bottom: 1px solid #ebeef5;
}

.month-title {
    font-size: 18px;
    font-weight: 500;
    color: #333;
}

.weekdays {
    display: grid;
    grid-template-columns: repeat(7, 1fr);
    text-align: center;
    padding: 12px 0;
    background: #f6f6f9;
    border-bottom: 1px solid #ebeef5;
}

.weekday {
    font-size: 14px;
    color: #606266;
}

.calendar-grid {
    display: grid;
    grid-template-columns: repeat(7, 1fr);
    gap: 1px;
    background: #ebeef5;
    flex: 1;
    min-height: 0;
    overflow: auto;
}

.calendar-cell {
    background: white;
    min-height: 120px;
    cursor: pointer;
    transition: all 0.3s;
    border-bottom: 1px solid #ebeef5;
}

.calendar-cell:hover {
    background: #f5f7fa;
}

.calendar-cell.current-month {
    background: white;
}

.calendar-cell.selected {
    background: #ecf5ff;
}

.calendar-cell.today {
    background: #f2f6fc;
}
</style>