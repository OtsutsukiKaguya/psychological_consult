<script setup>
import { ref } from 'vue'
import * as echarts from 'echarts'
import { onMounted } from 'vue'

// 模拟排行榜数据
const rankingData = ref([
    { id: 1, avatar: '/avatar.jpg', name: '咨询师A', count: 123 }
])

// 初始化图表
onMounted(() => {
    const chartDom = document.getElementById('weeklyChart')
    const myChart = echarts.init(chartDom)

    const option = {
        grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            top: '3%',
            containLabel: true
        },
        xAxis: {
            type: 'category',
            data: ['M', 'T', 'W', 'T', 'F', 'S', 'S']
        },
        yAxis: {
            type: 'value'
        },
        series: [
            {
                data: [20, 35, 15, 40, 30, 25, 28],
                type: 'bar',
                itemStyle: {
                    color: '#6777ef'
                },
                barWidth: '30%'
            }
        ]
    }

    myChart.setOption(option)
})
</script>

<template>
    <div class="statistic-charts">
        <!-- 7天咨询数量统计 -->
        <div class="chart-card">
            <div class="card-header">
                <h3>7日咨询数量统计</h3>
            </div>
            <div id="weeklyChart" class="chart-content"></div>
        </div>

        <!-- 排行榜 -->
        <div class="ranking-card">
            <div class="card-header">
                <h3>当月咨询数量排行榜</h3>
                <div class="ranking-tabs">
                    <el-button-group>
                        <el-button type="primary" plain>按月</el-button>
                        <el-button type="primary" plain>按年</el-button>
                    </el-button-group>
                </div>
            </div>
            <div class="ranking-list">
                <div v-for="item in rankingData" :key="item.id" class="ranking-item">
                    <div class="rank-info">
                        <span class="rank-number">1</span>
                        <el-avatar :src="item.avatar" :size="40"></el-avatar>
                        <span class="consultant-name">{{ item.name }}</span>
                    </div>
                    <span class="count">{{ item.count }}</span>
                </div>
            </div>
        </div>

        <!-- 好评排行榜 -->
        <div class="ranking-card">
            <div class="card-header">
                <h3>当月好评数量排行榜</h3>
                <div class="ranking-tabs">
                    <el-button-group>
                        <el-button type="primary" plain>按月</el-button>
                        <el-button type="primary" plain>按年</el-button>
                    </el-button-group>
                </div>
            </div>
            <div class="ranking-list">
                <div v-for="item in rankingData" :key="item.id" class="ranking-item">
                    <div class="rank-info">
                        <span class="rank-number">1</span>
                        <el-avatar :src="item.avatar" :size="40"></el-avatar>
                        <span class="consultant-name">{{ item.name }}</span>
                    </div>
                    <span class="count">{{ item.count }}</span>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
.statistic-charts {
    display: flex;
    gap: 20px;
    margin-top: 20px;
    height: 340px;
}

.chart-card {
    flex: 2;
    background: white;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
    height: 340px;
}

.ranking-card {
    flex: 1;
    background: white;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
    height: 340px;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}

.card-header h3 {
    font-size: 16px;
    font-weight: normal;
    color: #333;
    margin: 0;
}

.chart-content {
    height: 280px;
}

.ranking-list {
    display: flex;
    flex-direction: column;
    gap: 15px;
}

.ranking-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px;
}

.rank-info {
    display: flex;
    align-items: center;
    gap: 12px;
}

.rank-number {
    font-size: 16px;
    color: #333;
    font-weight: bold;
}

.consultant-name {
    color: #606266;
}

.count {
    font-size: 16px;
    color: #333;
    font-weight: bold;
}

:deep(.el-button-group .el-button) {
    background-color: transparent;
}

:deep(.el-button-group .el-button.is-active) {
    background-color: #6777ef;
    color: white;
}
</style>