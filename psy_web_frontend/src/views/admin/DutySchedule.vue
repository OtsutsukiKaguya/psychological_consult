<!-- 排班表页面 -->
<template>
    <BaseLayout>
        <div class="schedule-container">
            <!-- 日历区域 -->
            <div class="calendar-area">
                <Calendar :selected-date="selectedDate" :schedule-data="scheduleData" @date-change="handleDateChange"
                    @month-change="handleMonthChange" />
            </div>

            <!-- 右侧人员列表 -->
            <div class="staff-list">
                <div class="date-header">
                    {{ formatDate(selectedDate) }}
                </div>
                <el-button class="auto-schedule-btn" type="primary" @click="handleAutoSchedule">
                    <i class="el-icon-magic-stick" style="margin-right:6px;"></i>一键排班
                </el-button>
                <StaffList :consultants="currentSchedule.consultants" :supervisors="currentSchedule.supervisors"
                    @add-consultant="handleAddConsultant" @add-supervisor="handleAddSupervisor"
                    @remove-consultant="handleRemoveConsultant" />
            </div>
        </div>
        <!-- 添加人员弹窗 -->
        <el-dialog v-model="addDialogVisible" title="添加人员" width="300px">
            <el-form @submit.prevent>
                <el-form-item label="ID">
                    <el-input v-model="addStaffId" placeholder="请输入ID" />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="addDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="submitAddDuty">确定</el-button>
            </template>
        </el-dialog>
        <!-- 一键排班弹窗 -->
        <el-dialog v-model="showGenerateDialog" title="一键排班" width="400px">
            <el-form @submit.prevent>
                <el-form-item label="开始日期">
                    <el-date-picker v-model="generateForm.start" type="date" placeholder="选择开始日期" style="width: 100%" />
                </el-form-item>
                <el-form-item label="结束日期">
                    <el-date-picker v-model="generateForm.end" type="date" placeholder="选择结束日期" style="width: 100%" />
                </el-form-item>
                <el-form-item label="每天排班人数">
                    <el-input-number v-model="generateForm.perDay" :min="1" style="width: 100%" />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="showGenerateDialog = false">取消</el-button>
                <el-button type="primary" @click="handleGenerate">确定</el-button>
            </template>
        </el-dialog>
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
import dayjs from 'dayjs'

const selectedDate = ref(new Date())
const scheduleData = ref([])

// 新增：添加人员弹窗相关
const addDialogVisible = ref(false)
const addRole = ref('') // 'COUNSELOR' 或 'TUTOR'
const addStaffId = ref('')

// 新增：一键排班相关
const showGenerateDialog = ref(false)
const generateForm = ref({ start: '', end: '', perDay: 1 })

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
    const formattedDate = formatDateForApi(date)
    const response = await axios.get(API.DUTY.GET_BY_DATE(formattedDate))
    console.log(`【${formattedDate}】接口原始返回:`, response.data)
    let consultants = []
    let supervisors = []
    if (response.data.code === 0 && Array.isArray(response.data.data)) {
        consultants = response.data.data.filter(item => item.role === 'COUNSELOR')
        supervisors = response.data.data.filter(item => item.role === 'TUTOR')
    }
    const scheduleItem = {
        date: formattedDate,
        consultants: consultants.length > 0 ? consultants.map(c => c.name) : [],
        supervisors: supervisors.length > 0 ? supervisors.map(s => s.name) : []
    }
    console.log(`【${formattedDate}】处理后:`, scheduleItem)
    // 更新或添加该日期的排班信息
    const existingIndex = scheduleData.value.findIndex(item => item.date === formattedDate)
    if (existingIndex !== -1) {
        scheduleData.value[existingIndex] = scheduleItem
    } else {
        scheduleData.value.push(scheduleItem)
    }
}

// 获取当前月份的排班信息（确保每个格子只请求一次）
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

        // 1. 上个月最后几天
        for (let i = daysFromPrevMonth; i > 0; i--) {
            const date = new Date(currentYear, currentMonth - 1, prevMonthDays - i + 1)
            await fetchDutyByDate(date)
        }
        // 2. 当前月所有天
        for (let i = 1; i <= daysInMonth; i++) {
            const date = new Date(currentYear, currentMonth, i)
            await fetchDutyByDate(date)
        }
        // 3. 下个月前几天
        for (let i = 1; i <= nextMonthDays; i++) {
            const date = new Date(currentYear, currentMonth + 1, i)
            await fetchDutyByDate(date)
        }
    } catch (error) {
        ElMessage.error('获取当月排班信息失败')
    }
}

// 监听月份切换，重新拉取数据
watch(selectedDate, async (newDate, oldDate) => {
    if (
        newDate.getFullYear() !== oldDate.getFullYear() ||
        newDate.getMonth() !== oldDate.getMonth()
    ) {
        await fetchCurrentMonthDuty()
    }
})

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

// 新增：切换月份时自动加载排班
const handleMonthChange = async (date) => {
    selectedDate.value = date // 保持selectedDate和日历同步
    await fetchCurrentMonthDuty()
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

// 新增：添加人员方法
const handleAddConsultant = () => {
    addRole.value = 'COUNSELOR'
    addDialogVisible.value = true
    addStaffId.value = ''
}
const handleAddSupervisor = () => {
    addRole.value = 'TUTOR'
    addDialogVisible.value = true
    addStaffId.value = ''
}

// 新增：提交添加
const submitAddDuty = async () => {
    if (!addStaffId.value) {
        ElMessage.error('请输入ID')
        return
    }
    const dutyDate = formatDateForApi(selectedDate.value)
    try {
        await axios.post(API.DUTY.ADD_DUTY, {
            staffId: addStaffId.value,
            dutyDate,
            isLeave: 0
        })
        ElMessage.success('添加成功')
        addDialogVisible.value = false
        await fetchDutyByDate(selectedDate.value)
    } catch (e) {
        ElMessage.error('添加失败')
    }
}

// 移除咨询师
const handleRemoveConsultant = (id) => {
    // TODO: 实现移除咨询师逻辑
    ElMessage.info('移除咨询师功能待实现')
}

// 一键排班点击事件（预留）
const handleAutoSchedule = () => {
    showGenerateDialog.value = true
}

// 一键排班生成
const handleGenerate = async () => {
    if (!generateForm.value.start || !generateForm.value.end || !generateForm.value.perDay) {
        ElMessage.error('请填写完整信息')
        return
    }
    // 日期格式化
    const start = dayjs(generateForm.value.start).format('YYYY-MM-DD')
    const end = dayjs(generateForm.value.end).format('YYYY-MM-DD')
    const url = API.DUTY.GENERATE(start, end, generateForm.value.perDay)
    console.log('生成排班请求URL:', url)
    try {
        await axios.post(url, null, {
            headers: { 'Content-Type': 'application/json' }
        })
        ElMessage.success('排班生成成功')
        showGenerateDialog.value = false
        await fetchCurrentMonthDuty()
    } catch (e) {
        ElMessage.error('排班生成失败')
        console.error('排班生成失败:', e)
    }
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
    max-height: 100%;
    overflow-y: auto;
}

.date-header {
    font-size: 16px;
    font-weight: 500;
    color: #333;
    margin-bottom: 20px;
    text-align: center;
}

.auto-schedule-btn {
    width: 100%;
    margin-bottom: 18px;
    background: linear-gradient(90deg, #557ff7 0%, #6e93ff 100%);
    color: #fff;
    font-size: 16px;
    font-weight: 600;
    border: none;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(85, 127, 247, 0.15);
    padding: 12px 0;
    transition: all 0.2s;
    display: flex;
    align-items: center;
    justify-content: center;
}

.auto-schedule-btn:hover {
    background: linear-gradient(90deg, #4066d6 0%, #557ff7 100%);
    box-shadow: 0 4px 16px rgba(85, 127, 247, 0.25);
    transform: translateY(-2px) scale(1.02);
}
</style>