<template>
    <div class="calendar-cell-content">
        <!-- 日期显示 -->
        <div class="date-number" :class="{ 'other-month': !isCurrentMonth }">
            {{ date.getDate() }}
        </div>

        <!-- 排班信息 -->
        <div v-if="schedule" class="schedule-info">
            <!-- 咨询师列表 -->
            <div v-if="schedule.consultants && schedule.consultants.length" class="staff-group">
                <div class="staff-label">值班咨询师</div>
                <div class="staff-tags">
                    <el-tooltip v-for="consultant in schedule.consultants" :key="consultant.id"
                        :content="consultant.name" placement="top" effect="light">
                        <el-tag size="small" type="primary" effect="plain" class="staff-tag">
                            {{ consultant.name }}
                        </el-tag>
                    </el-tooltip>
                </div>
            </div>

            <!-- 督导列表 -->
            <div v-if="schedule.supervisors && schedule.supervisors.length" class="staff-group">
                <div class="staff-label">值班督导</div>
                <div class="staff-tags">
                    <el-tooltip v-for="supervisor in schedule.supervisors" :key="supervisor.id"
                        :content="supervisor.name" placement="top" effect="light">
                        <el-tag size="small" type="success" effect="plain" class="staff-tag">
                            {{ supervisor.name }}
                        </el-tag>
                    </el-tooltip>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
defineProps({
    date: {
        type: Date,
        required: true
    },
    isCurrentMonth: {
        type: Boolean,
        required: true
    },
    schedule: {
        type: Object,
        default: null
    }
})
</script>

<style scoped>
.calendar-cell-content {
    padding: 8px;
    height: 100%;
    display: flex;
    flex-direction: column;
    gap: 4px;
}

.date-number {
    font-size: 14px;
    color: #333;
    margin-bottom: 4px;
}

.date-number.other-month {
    color: #909399;
}

.schedule-info {
    display: flex;
    flex-direction: column;
    gap: 8px;
    font-size: 12px;
    overflow: auto;
}

.staff-group {
    display: flex;
    flex-direction: column;
    gap: 4px;
}

.staff-label {
    font-size: 12px;
    color: #909399;
}

.staff-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 4px;
}

.staff-tag {
    margin: 0;
    max-width: 100%;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

:deep(.el-tag--small) {
    height: 20px;
    padding: 0 4px;
    font-size: 11px;
}
</style>