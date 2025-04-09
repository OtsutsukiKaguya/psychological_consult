# 创建咨询详情页面
<template>
    <ConsultantBaseLayout>
        <div class="consultation-detail">
            <div class="chat-container">
                <!-- 咨询师与用户的聊天记录 -->
                <div class="chat-section">
                    <div class="section-header">
                        <h3>咨询记录</h3>
                        <div class="consultation-info">
                            <span>咨询时间：{{ consultationDate }}</span>
                            <span class="divider">|</span>
                            <span>咨询时长：{{ duration }}</span>
                        </div>
                        <div class="rating-section">
                            <span class="rating-label">访客评价：</span>
                            <span class="rating-text">{{ visitorComment }}</span>
                        </div>
                    </div>
                    <div class="chat-content">
                        <div v-for="(message, index) in consultationMessages" :key="index"
                            :class="['message', message.role]">
                            <template v-if="message.role === 'visitor'">
                                <div class="avatar">
                                    <el-avatar :size="40">访</el-avatar>
                                </div>
                                <div class="message-content">
                                    <div class="name">{{ message.name }}</div>
                                    <div class="text">{{ message.content }}</div>
                                </div>
                            </template>
                            <template v-else>
                                <div class="message-content">
                                    <div class="name">{{ message.name }}</div>
                                    <div class="text">{{ message.content }}</div>
                                </div>
                                <div class="avatar">
                                    <img src="@/assets/组合 5.png" alt="咨询师头像" />
                                </div>
                            </template>
                        </div>
                    </div>
                </div>

                <!-- 督导与咨询师的聊天记录 -->
                <div class="chat-section supervisor-chat">
                    <div class="section-header">
                        <h3>督导记录</h3>
                        <div class="rating-section">
                            <span class="rating-label">督导评价：</span>
                            <span class="rating-text">{{ supervisorComment }}</span>
                        </div>
                    </div>
                    <div class="chat-content">
                        <div v-for="(message, index) in supervisorMessages" :key="index"
                            :class="['message', message.role]">
                            <div class="avatar" v-if="message.role === 'supervisor'">
                                <el-avatar :size="40">督</el-avatar>
                            </div>
                            <div class="message-content">
                                <div class="name">{{ message.name }}</div>
                                <div class="text">{{ message.content }}</div>
                            </div>
                            <div class="avatar" v-if="message.role === 'consultant'">
                                <img src="@/assets/组合 5.png" alt="咨询师头像" />
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </ConsultantBaseLayout>
</template>

<script setup>
import ConsultantBaseLayout from '@/components/layout/ConsultantBaseLayout.vue'
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import profileImage from '@/assets/组合 5.png'

const route = useRoute()
const consultationId = route.params.id

// 咨询基本信息
const consultationDate = ref('2025/3/11 20:00:00')
const duration = ref('1小时30分钟')

// 咨询聊天记录
const consultationMessages = ref([
    {
        role: 'consultant',
        name: '咨询师',
        content: '你好'
    },
    {
        role: 'visitor',
        name: '访客',
        content: '你好'
    }
])

// 督导聊天记录
const supervisorMessages = ref([
    {
        role: 'supervisor',
        name: '督导',
        content: '本咨询者有双相障碍迹象，建议其去专业的医院'
    },
    {
        role: 'consultant',
        name: '咨询师',
        content: '好的，我明白了'
    }
])

// 评价数据
const visitorComment = ref('咨询师很专业，帮助我理清了思路')
const supervisorComment = ref('咨询方向把握准确，技巧运用合理')

onMounted(() => {
    // 这里可以根据 consultationId 加载具体的咨询记录数据
    console.log('加载咨询记录：', consultationId)
})
</script>

<style scoped>
.consultation-detail {
    padding: 20px;
    height: 100%;
    background-color: #f5f7fa;
}

.chat-container {
    display: flex;
    gap: 20px;
    height: 100%;
}

.chat-section {
    flex: 1;
    background: white;
    border-radius: 8px;
    display: flex;
    flex-direction: column;
    overflow: hidden;
}

.section-header {
    padding: 20px;
    padding-bottom: 10px;
    border-bottom: 1px solid #e6e6e6;
}

.section-header h3 {
    margin: 0;
    color: #333;
    font-size: 16px;
    font-weight: 500;
}

.consultation-info {
    margin-top: 10px;
    font-size: 14px;
    color: #666;
}

.consultation-info .divider {
    margin: 0 10px;
    color: #e6e6e6;
}

.chat-content {
    flex: 1;
    padding: 20px;
    overflow-y: auto;
    display: flex;
    flex-direction: column;
    gap: 12px;
}

.message {
    display: flex;
    align-items: flex-start;
    gap: 12px;
    max-width: 80%;
}

/* 左侧用户与咨询师的聊天样式 */
.chat-section:not(.supervisor-chat) {
    .message {
        display: flex;
        align-items: flex-start;
        gap: 12px;
        max-width: 80%;
    }

    .message.visitor {
        align-self: flex-start;
        flex-direction: row;
    }

    .message.consultant {
        align-self: flex-end;
        flex-direction: row-reverse;
    }

    .message.visitor .message-content {
        background: #f5f7fa;
    }

    .message.consultant .message-content {
        background: #e5edff;
    }
}

/* 右侧督导聊天样式 */
.supervisor-chat {
    .message {
        align-self: flex-start;
        flex-direction: row;
    }

    .message.consultant {
        align-self: flex-end;
        flex-direction: row-reverse;
    }

    .message-content {
        background: #f5f7fa;
    }

    .message.consultant .message-content {
        background: #e5edff;
    }
}

.avatar {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    overflow: hidden;
}

.avatar img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.message-content {
    background: #f5f7fa;
    padding: 12px;
    border-radius: 8px;
    position: relative;
}

.message.consultant .message-content {
    background: #e5edff;
}

.name {
    font-size: 12px;
    color: #999;
    margin-bottom: 4px;
}

.text {
    font-size: 14px;
    color: #333;
    line-height: 1.5;
}

.supervisor-chat {
    border-left: 1px solid #e6e6e6;
}

.rating-section {
    margin-top: 8px;
    display: flex;
    align-items: center;
    gap: 8px;
}

.rating-label {
    font-size: 14px;
    color: #666;
}

.rating-text {
    font-size: 14px;
    color: #333;
}
</style>