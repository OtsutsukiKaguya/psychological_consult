<script setup>
import BaseLayout from '@/components/layout/BaseLayout.vue'
import StatisticCards from '@/components/dashboard/StatisticCards.vue'
import ConsultantStatus from '@/components/dashboard/ConsultantStatus.vue'
import StatisticCharts from '@/components/dashboard/StatisticCharts.vue'
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { API, BASE_URL } from '@/config'

const consultants = ref([])
const supervisors = ref([])
const activeConversations = ref(0)
const activeGuidance = ref(0)
const rankingData = ref([])
const todayConsultDuration = ref('00:00:00')
const todayConsultCount = ref(0)

const fetchOnlineStatus = async () => {
    try {
        const res = await axios.get(API.STATISTICS.ONLINE_STATUS)
        if (res.data.code === 0 && res.data.data) {
            // 在线咨询师
            consultants.value = (res.data.data.counselors || [])
                .filter(item => item.state === 'ONLINE' || item.state === 'BUSY')
                .map(item => ({
                    id: item.id,
                    name: item.name,
                    status: item.state === 'ONLINE' ? '空闲' : (item.state === 'BUSY' ? '忙碌' : '')
                }))
            // 在线督导
            supervisors.value = (res.data.data.tutors || [])
                .filter(item => item.state === 'ONLINE' || item.state === 'BUSY')
                .map(item => ({
                    id: item.id,
                    name: item.name,
                    status: item.state === 'ONLINE' ? '空闲' : (item.state === 'BUSY' ? '忙碌' : '')
                }))
            // 进行的会话/指导
            activeConversations.value = res.data.data.counselorOngoingSessions || 0
            activeGuidance.value = res.data.data.tutorOngoingSessions || 0
        }
    } catch (e) {
        consultants.value = []
        supervisors.value = []
        activeConversations.value = 0
        activeGuidance.value = 0
    }
}

const fetchConsultationRanking = async () => {
    try {
        const res = await axios.get(API.STATISTICS.CONSULTATION_RANKING, { params: { period: 'month' } })
        if (res.data.code === 0 && Array.isArray(res.data.data)) {
            rankingData.value = res.data.data.map((item, idx) => ({
                id: idx + 1,
                name: item.counselorName,
                count: item.consultationCount
            }))
        } else {
            rankingData.value = []
        }
    } catch (e) {
        rankingData.value = []
    }
}

const fetchTodayConsultStats = async () => {
    try {
        // 今日咨询时长
        const res1 = await axios.get(`${BASE_URL}/api/statistics/today/consult-duration`)
        console.log('今日咨询时长接口返回:', res1.data)
        if (res1.data.code === 0) {
            todayConsultDuration.value = res1.data.data || '00:00:00'
        }
    } catch (e) {
        console.error('今日咨询时长接口异常:', e)
    }
    try {
        // 今日咨询数
        const res2 = await axios.get(`${BASE_URL}/api/statistics/today/consult-count`)
        console.log('今日咨询数接口返回:', res2.data)
        if (res2.data.code === 0) {
            todayConsultCount.value = res2.data.data || 0
        }
    } catch (e) {
        console.error('今日咨询数接口异常:', e)
    }
}

onMounted(() => {
    fetchOnlineStatus()
    fetchConsultationRanking()
    fetchTodayConsultStats()
})
</script>

<template>
    <BaseLayout>
        <div class="dashboard-container">
            <StatisticCards :consultCount="todayConsultCount" :consultDuration="todayConsultDuration" />
            <ConsultantStatus :consultants="consultants" :supervisors="supervisors"
                :active-conversations="activeConversations" :active-guidance="activeGuidance" />
            <StatisticCharts :ranking-data="rankingData" />
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