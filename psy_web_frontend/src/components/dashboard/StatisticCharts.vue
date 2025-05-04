<script setup>
import { ref, watch, onMounted } from 'vue'
import * as echarts from 'echarts'
import axios from 'axios'
import { API } from '@/config.js'

const props = defineProps({
    rankingData: {
        type: Array,
        default: () => []
    }
})

const periodConsult = ref('month')
const periodGood = ref('month')
const consultRankingData = ref([])
const goodRankingData = ref([])

function handlePeriodConsult(val) {
    periodConsult.value = val
    // 这里可以触发父组件事件或emit，后续如需联动可扩展
}
function handlePeriodGood(val) {
    periodGood.value = val
    // 这里可以触发父组件事件或emit，后续如需联动可扩展
}

async function fetchConsultRanking(period = 'month') {
    const res = await axios.get(API.STATISTICS.CONSULTATION_RANKING, { params: { period } })
    console.log('咨询数量排行榜接口返回：', res.data.data)
    // 适配后端字段
    consultRankingData.value = (res.data.data || []).map(item => ({
        name: item.counselorName,
        count: item.consultationCount
    }))
}

async function fetchGoodRanking(period = 'month') {
    const res = await axios.get(`${API.STATISTICS.CONSULTATION_RANKING.replace('consultation-ranking', 'positive-feedback-ranking')}`, { params: { period } })
    console.log('好评数量排行榜接口返回：', res.data.data)
    goodRankingData.value = (res.data.data || []).map(item => ({
        name: item.counselorName,
        count: item.consultationCount
    }))
}

watch(periodConsult, (val) => {
    fetchConsultRanking(val)
})

watch(periodGood, (val) => {
    fetchGoodRanking(val)
})

// 初始化图表
onMounted(() => {
    fetchConsultRanking(periodConsult.value)
    fetchGoodRanking(periodGood.value)
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
                    <el-button size="small" class="switch-btn" :type="periodConsult === 'month' ? 'primary' : 'default'"
                        @click="handlePeriodConsult('month')">按月</el-button>
                    <el-button size="small" class="switch-btn" :type="periodConsult === 'year' ? 'primary' : 'default'"
                        @click="handlePeriodConsult('year')">按年</el-button>
                </div>
            </div>
            <div class="ranking-list custom-scroll">
                <div v-for="(item, idx) in consultRankingData" :key="item.id" class="ranking-item custom-card">
                    <div class="rank-info">
                        <span class="rank-number">{{ idx + 1 }}</span>
                        <el-avatar :size="40" class="blue-avatar">{{ item.name && item.name.length > 0 ? item.name[0] :
                            '' }}</el-avatar>
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
                    <el-button size="small" class="switch-btn" :type="periodGood === 'month' ? 'primary' : 'default'"
                        @click="handlePeriodGood('month')">按月</el-button>
                    <el-button size="small" class="switch-btn" :type="periodGood === 'year' ? 'primary' : 'default'"
                        @click="handlePeriodGood('year')">按年</el-button>
                </div>
            </div>
            <div class="ranking-list custom-scroll">
                <div v-for="(item, idx) in goodRankingData" :key="item.id" class="ranking-item custom-card">
                    <div class="rank-info">
                        <span class="rank-number">{{ idx + 1 }}</span>
                        <el-avatar :size="40" class="blue-avatar">{{ item.name && item.name.length > 0 ? item.name[0] :
                            '' }}</el-avatar>
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
    display: flex;
    flex-direction: column;
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
    gap: 10px;
    flex: 1;
    overflow-y: auto;
    /* 新增滚动条样式 */
    max-height: 240px;
}

.custom-scroll::-webkit-scrollbar {
    width: 6px;
}

.custom-scroll::-webkit-scrollbar-thumb {
    background: #e0e0e0;
    border-radius: 3px;
}

.ranking-item.custom-card {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px 14px;
    border-radius: 10px;
    background: #fff;
    box-shadow: 0 1px 4px 0 rgba(103, 119, 239, 0.06);
    border: 1px solid #f0f0f0;
    margin: 0;
}

.rank-info {
    display: flex;
    align-items: center;
    gap: 10px;
}

.rank-number {
    font-size: 16px;
    color: #333;
    font-weight: bold;
    width: 22px;
    text-align: center;
}

.blue-avatar {
    background: #3b7cff !important;
    color: #fff !important;
    font-weight: bold;
    font-size: 18px;
}

.consultant-name {
    color: #606266;
    font-size: 15px;
    font-weight: 500;
}

.count {
    font-size: 16px;
    color: #333;
    font-weight: bold;
    min-width: 24px;
    text-align: right;
}

.ranking-tabs {
    display: flex;
    gap: 8px;
    align-items: center;
}

.switch-btn {
    padding: 2px 16px;
    font-size: 13px;
    border-radius: 6px;
    font-weight: 500;
    border: 1px solid #e0e0e0;
    transition: all 0.2s;
}

.switch-btn:hover {
    background: #f4f8ff;
    color: #2466d1;
    border-color: #b3d1ff;
}
</style>