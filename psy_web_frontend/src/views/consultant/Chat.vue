# 创建聊天页面
<template>
    <ConsultantBaseLayout>
        <div class="chat-layout" v-if="chatInfo">
            <!-- 左侧栏：咨询信息 -->
            <div class="left-panel">
                <div class="consult-info">
                    <img src="@/assets/avatar.png" alt="头像" class="avatar" />
                    <div class="info-text">
                        <h2>{{ chatInfo.name }}</h2>
                        <p>{{ chatInfo.id }}</p>
                    </div>
                </div>
                <hr class="divider" />
                <div class="consult-status">
                    <p>正在咨询中</p>
                    <p>已咨询时间</p>
                    <h3>{{ consultationTime }}</h3>
                </div>
                <div class="actions">
                    <button @click="requestSupervisor">请求督导</button>
                    <button @click="endConsultation">结束咨询</button>
                </div>
            </div>

            <!-- 中间栏：咨询师与用户聊天 -->
            <div class="center-panel">
                <div class="chat-box" ref="chatBoxRef">
                    <div v-for="(message, index) in messages" :key="index" :class="['message', message.type]">
                        <template v-if="message.type === 'user'">
                            <img src="@/assets/avatar.png" :alt="`${message.type}头像`" class="avatar" />
                            <div class="bubble user-bubble">
                                {{ message.content }}
                            </div>
                        </template>
                        <template v-else>
                            <div class="bubble consultant-bubble">
                                {{ message.content }}
                            </div>
                            <img src="@/assets/avatar.png" :alt="`${message.type}头像`" class="avatar" />
                        </template>
                    </div>
                </div>
                <div class="input-area">
                    <div class="toolbar">
                        <img src="@/assets/chat/icon_microphone.png" alt="Mic" @click="handleVoice" />
                        <img src="@/assets/chat/icon-photo.png" alt="Image" @click="handleImage" />
                        <img src="@/assets/chat/icon-emoji.png" alt="Emoji" @click="handleEmoji" />
                        <img src="@/assets/chat/Phone.png" alt="Phone" @click="handleCall" />
                    </div>
                    <textarea v-model="inputMessage" placeholder="输入消息..." @keyup.enter="sendMessage"></textarea>
                    <button class="send-button" @click="sendMessage">发送</button>
                </div>
            </div>

            <!-- 右侧栏：咨询师与督导聊天 -->
            <div class="right-panel">
                <div class="supervisor-header">
                    <img src="@/assets/avatar.png" alt="督导头像" class="avatar" />
                    <h3>督导</h3>
                </div>
                <div class="chat-box supervisor-chat-box" ref="supervisorChatBoxRef">
                    <div v-for="(message, index) in supervisorMessages" :key="index" :class="['message', message.type]">
                        <template v-if="message.type === 'supervisor'">
                            <img src="@/assets/avatar.png" alt="督导头像" class="avatar" />
                            <div class="bubble supervisor-bubble">
                                {{ message.content }}
                            </div>
                        </template>
                        <template v-else>
                            <div class="bubble consultant-bubble">
                                {{ message.content }}
                            </div>
                            <img src="@/assets/avatar.png" alt="咨询师头像" class="avatar" />
                        </template>
                    </div>
                </div>
                <div class="input-area supervisor-input-area">
                    <div class="toolbar">
                        <img src="@/assets/chat/icon_microphone.png" alt="Mic" @click="handleSupervisorVoice" />
                        <img src="@/assets/chat/icon-photo.png" alt="Image" @click="handleSupervisorImage" />
                        <img src="@/assets/chat/icon-emoji.png" alt="Emoji" @click="handleSupervisorEmoji" />
                    </div>
                    <textarea v-model="supervisorInputMessage" placeholder="输入消息..."
                        @keyup.enter="sendSupervisorMessage"></textarea>
                    <button class="send-button" @click="sendSupervisorMessage">发送</button>
                </div>
            </div>
        </div>
    </ConsultantBaseLayout>
</template>

<script setup>
import ConsultantBaseLayout from '@/components/layout/ConsultantBaseLayout.vue'
import { ref, onMounted, nextTick, watch, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { API } from '@/config'

// 硬编码的值
const SESSION_ID = 'bee364bb-9df6-4d3d-9d89-b362c1353056'
const TOKEN = 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiLolq_ppbzlpKfluIgiLCJyb2xlcyI6IlJPTEVfQ09VTlNFTE9SIiwidXNlcklkIjoienlxenhzIiwiaWF0IjoxNzQ1OTQ2MDI0LCJleHAiOjE3NDYwMzI0MjR9.rhNVzvcZLqPdxDJqNyviKfcceBVlfFlqT9mfBttgYR0VO1XPr9yREg672ELABiztKa9aS_H8qjC_agPqcSSLlQ'

const route = useRoute()
const chatInfo = ref(null)
const messages = ref([])
const supervisorMessages = ref([])
const inputMessage = ref('')
const supervisorInputMessage = ref('')

// WebSocket相关
const stompClient = ref(null)
const connected = ref(false)

// 添加计时相关的响应式变量
const consultationTime = ref('00:00:00')
const timer = ref(null)
const startTime = ref(null)

// 保存聊天状态到localStorage
const saveStateToStorage = () => {
    const chatState = {
        messages: messages.value,
        startTime: startTime.value ? startTime.value.getTime() : null,
        consultationTime: consultationTime.value
    }
    localStorage.setItem('chatState', JSON.stringify(chatState))
}

// 从localStorage加载聊天状态
const loadStateFromStorage = () => {
    const savedState = localStorage.getItem('chatState')
    if (savedState) {
        const state = JSON.parse(savedState)
        messages.value = state.messages
        if (state.startTime) {
            startTime.value = new Date(state.startTime)
            consultationTime.value = state.consultationTime
        }
    }
}

// 清除localStorage中的聊天状态
const clearChatState = () => {
    localStorage.removeItem('chatState')
    messages.value = []
    consultationTime.value = '00:00:00'
    startTime.value = null
}

// 聊天框ref
const chatBoxRef = ref(null)
const supervisorChatBoxRef = ref(null)

// 滚动到底部
const scrollToBottom = () => {
    nextTick(() => {
        if (chatBoxRef.value) {
            chatBoxRef.value.scrollTop = chatBoxRef.value.scrollHeight
        }
    })
}
const scrollSupervisorToBottom = () => {
    nextTick(() => {
        if (supervisorChatBoxRef.value) {
            supervisorChatBoxRef.value.scrollTop = supervisorChatBoxRef.value.scrollHeight
        }
    })
}

// 开始计时
const startTimer = () => {
    if (timer.value) return
    if (!startTime.value) {
        startTime.value = new Date()
    }
    timer.value = setInterval(() => {
        const now = new Date()
        const diff = now - startTime.value
        const hours = Math.floor(diff / 3600000)
        const minutes = Math.floor((diff % 3600000) / 60000)
        const seconds = Math.floor((diff % 60000) / 1000)
        consultationTime.value = `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
        saveStateToStorage() // 每次更新时间时保存状态
    }, 1000)
}

// 停止计时
const stopTimer = () => {
    if (timer.value) {
        clearInterval(timer.value)
        timer.value = null
    }
}

// 连接WebSocket
const connectWebSocket = () => {
    const socket = new window.SockJS('http://47.117.102.116:8081/ws')
    stompClient.value = window.Stomp.over(socket)

    // 关闭 STOMP 的调试信息
    stompClient.value.debug = null

    const headers = {
        Authorization: 'Bearer ' + TOKEN
    }

    stompClient.value.connect(headers, frame => {
        console.log('✅ STOMP连接成功', frame)
        connected.value = true

        // 订阅个人消息队列
        stompClient.value.subscribe('/user/queue/messages', message => {
            console.log('📩 收到推送：', message.body)
            const receivedMessage = JSON.parse(message.body)
            // 添加到消息列表
            messages.value.push({
                type: 'user',  // 接收到的消息显示在左侧
                content: receivedMessage.content
            })
            scrollToBottom()
        })
    }, error => {
        console.error('❌ STOMP连接失败', error)
        ElMessage.error('连接失败，请检查网络')
        connected.value = false
    })
}

// 发送消息
const sendMessage = () => {
    if (!inputMessage.value.trim()) return

    const messagePayload = {
        content: inputMessage.value,
        type: "TEXT",
        fileId: 0
    }

    // 发送消息到服务器
    fetch(`${API.MESSAGES.SESSION}/bee364bb-9df6-4d3d-9d89-b362c1353056`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${TOKEN}`
        },
        body: JSON.stringify(messagePayload)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('发送失败')
            }
            return response.json()
        })
        .then(() => {
            // 发送成功，添加到消息列表
            messages.value.push({
                type: 'consultant',
                content: inputMessage.value
            })
            inputMessage.value = ''
            scrollToBottom()
            saveStateToStorage() // 保存新的消息状态
        })
        .catch(error => {
            console.error('发送消息失败:', error)
            ElMessage.error('发送消息失败')
        })
}

// 发送督导消息
const sendSupervisorMessage = () => {
    if (!supervisorInputMessage.value.trim()) return
    supervisorMessages.value.push({
        type: 'consultant',
        content: supervisorInputMessage.value
    })
    supervisorInputMessage.value = ''
    scrollSupervisorToBottom()
}

// 请求督导
const requestSupervisor = () => {
    ElMessage.success('已发送督导请求')
}

// 结束咨询
const endConsultation = () => {
    ElMessageBox.confirm(
        '是否结束本次咨询？',
        '提示',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
        }
    )
        .then(() => {
            clearChatState() // 清除聊天记录和计时
            stopTimer()
            ElMessage({
                type: 'success',
                message: '咨询已结束'
            })
        })
        .catch(() => {
            // 用户点击取消，不做任何操作
        })
}

// 工具栏功能
const handleVoice = () => ElMessage.info('语音功能开发中')
const handleImage = () => ElMessage.info('图片功能开发中')
const handleEmoji = () => ElMessage.info('表情功能开发中')
const handleCall = () => ElMessage.info('通话功能开发中')
const handleSupervisorVoice = () => ElMessage.info('督导语音功能开发中')
const handleSupervisorImage = () => ElMessage.info('督导图片功能开发中')
const handleSupervisorEmoji = () => ElMessage.info('督导表情功能开发中')

// 初始化聊天
const initChat = async () => {
    try {
        chatInfo.value = {
            id: 'cxy',
            name: 'ppplusss',
            duration: '00:00:00'
        }
        loadStateFromStorage() // 加载保存的状态
        scrollToBottom()
        scrollSupervisorToBottom()
        startTimer()
    } catch (error) {
        console.error('初始化聊天失败:', error)
        ElMessage.error('初始化聊天失败')
    }
}

onMounted(() => {
    initChat()
    connectWebSocket()
})

watch(messages, () => {
    scrollToBottom()
    saveStateToStorage()
}, { deep: true })
watch(supervisorMessages, () => {
    scrollSupervisorToBottom()
})

// 组件卸载时断开连接
onUnmounted(() => {
    if (stompClient.value) {
        stompClient.value.disconnect()
    }
    stopTimer()
    saveStateToStorage() // 保存最终状态
})
</script>

<style scoped>
.chat-layout {
    display: flex;
    height: 100%;
    /* Inherit height from parent container in BaseLayout */
}

.left-panel {
    width: 230px;
    background-color: #5060c5;
    color: #fff;
    padding: 20px;
    display: flex;
    flex-direction: column;
    align-items: center;
}

.consult-info {
    display: flex;
    align-items: center;
    margin-bottom: 10px;
    width: 100%;
    padding-left: 0;
}

.consult-info .avatar {
    margin-left: 6px;
}

.consult-info h2 {
    font-size: 22px;
    font-weight: bold;
}

.consult-info p {
    font-size: 10px;
    margin: 5px 0;
}

.consult-status {
    margin-top: 10px;
    text-align: left;
    flex-grow: 0;
    width: 100%;
}

.consult-status p:first-child {
    font-weight: bold;
    font-size: 16px;
    margin-bottom: 14px;
}

.consult-status h3 {
    font-size: 36px;
    font-weight: bold;
    margin: 5px 0;
}

.actions {
    margin-top: auto;
    width: 100%;
}

.actions button {
    width: 100%;
    margin-top: 10px;
    padding: 10px;
    background-color: #fff;
    color: #5060c5;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
}

.actions button+button {
    margin-top: 20px;
}

.center-panel {
    flex: 1;
    padding: 0;
    /* Remove padding */
    display: flex;
    flex-direction: column;
    background-color: #f8f8f8;
}

.chat-box {
    flex: 1;
    overflow-y: auto;
    padding: 20px;
    display: flex;
    flex-direction: column;
    gap: 15px;
}

.message {
    display: flex;
    align-items: flex-start;
    gap: 10px;
    max-width: 70%;
}

.message.consultant {
    align-self: flex-end;
}

.message.user {
    align-self: flex-start;
}

.message .avatar {
    width: 40px;
    height: 40px;
    border-radius: 50%;
}

.bubble {
    padding: 10px 15px;
    border-radius: 10px;
    word-wrap: break-word;
    white-space: pre-wrap;
}

.consultant-bubble {
    background-color: #19c490;
    color: #fff;
}

.user-bubble {
    background-color: #fff;
    border: 1px solid #e0e0e0;
    color: #333;
}

.input-area {
    border-top: 1px solid #e0e0e0;
    background-color: #fff;
}

.toolbar {
    padding: 10px 20px;
    display: flex;
    gap: 15px;
    align-items: center;
}

.toolbar img {
    width: 20px;
    height: 20px;
    cursor: pointer;
}

.input-area textarea {
    width: calc(100% - 40px);
    /* Adjust width considering padding */
    height: 80px;
    border: none;
    resize: none;
    padding: 10px 20px;
    font-size: 14px;
    outline: none;
}

.send-button {
    display: block;
    margin: 10px 20px 10px auto;
    /* Align to the right */
    padding: 8px 20px;
    background-color: #19c490;
    color: #fff;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    font-size: 14px;
}

.send-button:hover {
    background-color: #15a374;
}

/* Hide default scrollbar and style for webkit browsers */
.chat-box::-webkit-scrollbar {
    width: 6px;
}

.chat-box::-webkit-scrollbar-track {
    background: transparent;
}

.chat-box::-webkit-scrollbar-thumb {
    background-color: #ccc;
    border-radius: 3px;
}

.avatar {
    width: 80px;
    height: 80px;
    border-radius: 50%;
}

.divider {
    width: 100%;
    border: 0;
    border-top: 1px solid #fff;
    margin: 5px 0;
}

.info-text {
    margin-left: 10px;
    flex: 1;
}

.right-panel {
    flex: 1;
    padding: 0;
    /* Remove padding */
    display: flex;
    flex-direction: column;
    border-left: 1px solid #e0e0e0;
    background-color: #fff;
}

.supervisor-header {
    background-color: #6c757d;
    /* Dark grey background */
    color: #fff;
    padding: 15px 20px;
    display: flex;
    align-items: center;
    gap: 15px;
}

.supervisor-header .avatar {
    width: 50px;
    height: 50px;
    border-radius: 50%;
}

.supervisor-header h3 {
    margin: 0;
    font-size: 18px;
    font-weight: normal;
}

.supervisor-chat-box {
    /* Inherits base chat-box styles, add specifics if needed */
    background-color: #fff;
}

/* Message styling within supervisor chat */
.supervisor-chat-box .message.supervisor {
    align-self: flex-start;
}

.supervisor-chat-box .message.consultant {
    align-self: flex-end;
    flex-direction: row;
    /* Avatar on the right, bubble on the left */
}

.supervisor-chat-box .supervisor-bubble {
    background-color: #fff;
    border: 1px solid #e0e0e0;
    color: #333;
}

.supervisor-chat-box .consultant-bubble-right {
    background-color: #19c490;
    color: #fff;
}

.supervisor-input-area {
    /* Inherits base input-area styles */
    border-top: 1px solid #e0e0e0;
    background-color: #f8f8f8;
    /* Slightly different background for input */
}

.supervisor-input-area .toolbar {
    background-color: #f8f8f8;
}

.supervisor-input-area textarea {
    background-color: #f8f8f8;
}
</style>