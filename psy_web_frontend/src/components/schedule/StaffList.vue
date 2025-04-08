<template>
    <div class="staff-list-container">
        <!-- 咨询师列表 -->
        <div class="staff-section">
            <div class="section-header">
                <span class="title">咨询师列表</span>
                <el-button type="primary" size="small" @click="$emit('add-consultant')">
                    添加咨询师
                </el-button>
            </div>
            <div class="staff-items">
                <div v-for="consultant in consultants" :key="consultant.id" class="staff-item">
                    <el-avatar :size="32" :src="consultant.avatar" />
                    <span class="name">{{ consultant.name }}</span>
                    <el-button type="danger" size="small" link @click="$emit('remove-consultant', consultant.id)">
                        移除
                    </el-button>
                </div>
            </div>
        </div>

        <!-- 督导列表 -->
        <div class="staff-section">
            <div class="section-header">
                <span class="title">督导列表</span>
                <el-button type="primary" size="small" @click="$emit('add-supervisor')">
                    添加督导
                </el-button>
            </div>
            <div class="staff-items">
                <div v-for="supervisor in supervisors" :key="supervisor.id" class="staff-item">
                    <el-avatar :size="32" :src="supervisor.avatar" />
                    <span class="name">{{ supervisor.name }}</span>
                    <el-button type="danger" size="small" link @click="$emit('remove-supervisor', supervisor.id)">
                        移除
                    </el-button>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
defineProps({
    consultants: {
        type: Array,
        required: true
    },
    supervisors: {
        type: Array,
        required: true
    }
})

defineEmits([
    'add-consultant',
    'remove-consultant',
    'add-supervisor',
    'remove-supervisor'
])
</script>

<style scoped>
.staff-list-container {
    display: flex;
    flex-direction: column;
    gap: 24px;
}

.staff-section {
    display: flex;
    flex-direction: column;
    gap: 16px;
}

.section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.title {
    font-size: 16px;
    font-weight: 500;
    color: #333;
}

.staff-items {
    display: flex;
    flex-direction: column;
    gap: 12px;
}

.staff-item {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 8px;
    border-radius: 4px;
    background: #f6f6f9;
}

.name {
    flex: 1;
    font-size: 14px;
    color: #333;
}
</style>