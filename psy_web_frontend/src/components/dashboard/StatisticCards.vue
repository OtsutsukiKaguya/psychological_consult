<script setup>
import { ref } from 'vue'
import { TrendCharts } from '@element-plus/icons-vue'

defineProps({
    consultCount: {
        type: [Number, String],
        default: 0
    },
    consultDuration: {
        type: String,
        default: '00:00:00'
    }
})
</script>

<template>
    <div class="statistic-cards">
        <div class="card-container">
            <div class="statistic-card">
                <div class="card-title">今日咨询数</div>
                <div class="card-value">{{ consultCount }}</div>
            </div>
            <div class="divider"></div>
            <div class="statistic-card">
                <div class="card-title">今日咨询时长</div>
                <div class="card-value">{{ consultDuration }}</div>
            </div>
        </div>
        <div class="trend-chart">
            <div class="chart-title">今日咨询数量变化</div>
            <div class="chart-content">
                <!-- 图表将在后续添加 -->
            </div>
        </div>
    </div>
</template>

<style scoped>
.statistic-cards {
    display: flex;
    gap: 20px;
    margin: 0;
}

.card-container {
    flex: 1;
    background: white;
    border-radius: 8px;
    padding: 24px;
    display: flex;
    align-items: center;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
    height: 200px;
}

.trend-chart {
    flex: 2;
    background: white;
    border-radius: 8px;
    padding: 24px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
    height: 200px;
}

.statistic-card {
    flex: 1;
    text-align: center;
    padding: 30px 0;
}

.divider {
    width: 1px;
    height: 50px;
    background-color: #e0e0e0;
    margin: 0 24px;
}

.card-title {
    font-size: 16px;
    color: #909399;
    margin-bottom: 20px;
}

.card-value {
    font-size: 48px;
    color: #303133;
    font-weight: bold;
    line-height: 1.5;
}

.chart-title {
    font-size: 14px;
    color: #909399;
    margin-bottom: 16px;
}

.chart-content {
    height: 230px;
}
</style>