<script setup>
import BaseLayout from '@/components/layout/BaseLayout.vue'
import StatisticCards from '@/components/dashboard/StatisticCards.vue'
import ConsultantStatus from '@/components/dashboard/ConsultantStatus.vue'
import StatisticCharts from '@/components/dashboard/StatisticCharts.vue'
</script>

<template>
    <BaseLayout>
        <div class="dashboard-container">
            <StatisticCards />
            <ConsultantStatus />
            <StatisticCharts />
            <!-- 后续添加其他内容 -->
        </div>
    </BaseLayout>
</template>

<style scoped>
.dashboard-container {
    background-color: #f5f7fa;
    min-height: 100vh;
}

.page-title {
    font-size: 20px;
    color: #333;
    margin-bottom: 20px;
    font-weight: normal;
    padding: 20px;
}
</style>