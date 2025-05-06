<script setup>
import { ref, onMounted } from 'vue'
import { TrendCharts } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

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

let chartInstance = null

onMounted(() => {
    // 初始化图表
    const chartContainer = document.getElementById('consultChart')
    if (chartContainer) {
        chartInstance = echarts.init(chartContainer)
        renderConsultationChart()
    }
})

// 渲染咨询数量变化折线图
const renderConsultationChart = () => {
    const option = {
        tooltip: {
            trigger: 'axis'
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            top: '10%',
            containLabel: true
        },
        xAxis: {
            type: 'category',
            boundaryGap: false,
            data: ['8:00', '9:00', '10:00', '11:00', '12:00', '13:00', '14:00', '15:00', '16:00', '17:00']
        },
        yAxis: {
            type: 'value',
            minInterval: 1
        },
        series: [
            {
                name: '咨询数量',
                type: 'line',
                smooth: true,
                data: [5, 12, 18, 15, 9, 8, 20, 16, 14, 10],
                itemStyle: {
                    color: '#557ff7'
                },
                areaStyle: {
                    color: {
                        type: 'linear',
                        x: 0,
                        y: 0,
                        x2: 0,
                        y2: 1,
                        colorStops: [
                            {
                                offset: 0,
                                color: 'rgba(85, 127, 247, 0.3)'
                            },
                            {
                                offset: 1,
                                color: 'rgba(85, 127, 247, 0.1)'
                            }
                        ]
                    }
                }
            }
        ]
    }

    chartInstance && chartInstance.setOption(option)
}
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
            <div class="chart-title">昨日咨询数量变化</div>
            <div class="chart-content">
                <div id="consultChart" class="chart-container"></div>
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
    height: 140px;
}

.chart-container {
    width: 100%;
    height: 100%;
}
</style>