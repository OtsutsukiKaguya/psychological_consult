# 创建督导的咨询详情页面
<template>
    <SupervisorBaseLayout>
        <ConsultationDetailContent :counselor-comment="counselorComment" :counselor-messages="counselorMessages"
            :tutor-comment="tutorComment" :tutor-messages="tutorMessages" :has-tutor="hasTutor" :date="date"
            :loading="loading" />
    </SupervisorBaseLayout>
</template>

<script setup>
import SupervisorBaseLayout from '@/components/layout/SupervisorBaseLayout.vue'
import ConsultationDetailContent from '@/components/consultation/ConsultationDetailContent.vue'
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'
import { CHAT_BASE_URL } from '@/config'

const route = useRoute()
const consultId = route.params.consultId

// 这里假设groupSessionId和oneToOneSessionId通过query传递，实际可根据业务调整
const groupSessionId = route.query.groupSessionId
const oneToOneSessionId = route.query.oneToOneSessionId

const counselorComment = ref('')
const counselorMessages = ref([])
const tutorComment = ref('')
const tutorMessages = ref([])
const hasTutor = ref(false)
const date = route.query.date || ''
console.log('详情页接收到的 date:', date)
const loading = ref(true)

onMounted(async () => {
    // 打印consultId和sessionId
    console.log('consultId:', consultId)
    console.log('groupSessionId:', groupSessionId)
    console.log('oneToOneSessionId:', oneToOneSessionId)

    // 并发请求group和one-to-one会话记录
    const groupPromise = groupSessionId
        ? axios.get(`${CHAT_BASE_URL}/api/sessions/${groupSessionId}/records`).catch(e => e)
        : Promise.resolve(null)
    const oneToOnePromise = oneToOneSessionId
        ? axios.get(`${CHAT_BASE_URL}/api/sessions/${oneToOneSessionId}/records`).catch(e => e)
        : Promise.resolve(null)
    const [groupRes, oneToOneRes] = await Promise.all([groupPromise, oneToOnePromise])

    // 处理group会话
    if (groupRes && groupRes.data) {
        console.log('group会话记录:', groupRes.data)
        counselorComment.value = groupRes.data.counselorComment || ''
        counselorMessages.value = Array.isArray(groupRes.data.messages) ? groupRes.data.messages : []
    } else {
        counselorComment.value = ''
        counselorMessages.value = []
        if (groupRes) console.log('group会话记录获取失败:', groupRes)
    }

    // 处理one-to-one会话
    if (oneToOneRes && oneToOneRes.data) {
        console.log('one-to-one会话记录:', oneToOneRes.data)
        tutorComment.value = oneToOneRes.data.tutorComment || ''
        tutorMessages.value = Array.isArray(oneToOneRes.data.messages) ? oneToOneRes.data.messages : []
        hasTutor.value = true
    } else {
        tutorComment.value = ''
        tutorMessages.value = []
        hasTutor.value = false
        if (oneToOneRes) console.log('one-to-one会话记录获取失败:', oneToOneRes)
    }
    loading.value = false
})
</script>

<style scoped>
.consultation-detail-container {
    padding: 40px;
    background: #fff;
    border-radius: 12px;
    margin: 40px auto;
    max-width: 900px;
    min-height: 300px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}
</style>