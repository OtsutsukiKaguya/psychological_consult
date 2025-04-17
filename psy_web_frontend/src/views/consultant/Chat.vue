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
                    <h3>{{ chatInfo.duration || '00:00:00' }}</h3>
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
import { ref, onMounted, nextTick, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

const route = useRoute()
const chatId = route.params.id

// 聊天信息
const chatInfo = ref(null)
const messages = ref([])
const supervisorMessages = ref([])
const inputMessage = ref('')
const supervisorInputMessage = ref('')

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

// 初始化聊天
const initChat = async () => {
    try {
        chatInfo.value = {
            id: chatId,
            name: '张先生',
            duration: '00:12:11'
        }
        messages.value = [
            { type: 'user', content: '您好，我是xxx，我想问' },
            { type: 'consultant', content: '您好，请问您要咨询什么' }
        ]
        supervisorMessages.value = [
            { type: 'supervisor', content: '您好，请问您要咨询什么' },
            { type: 'consultant', content: '您好，我是xxx，我想问' }
        ]
        scrollToBottom()
        scrollSupervisorToBottom()
    } catch (error) {
        console.error('初始化聊天失败:', error)
        ElMessage.error('初始化聊天失败')
    }
}

// 发送消息
const sendMessage = () => {
    if (!inputMessage.value.trim()) return
    messages.value.push({
        type: 'consultant',
        content: inputMessage.value
    })
    inputMessage.value = ''
    scrollToBottom()
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
    ElMessage.warning('确定要结束咨询吗？')
}

// 工具栏功能
const handleVoice = () => ElMessage.info('语音功能开发中')
const handleImage = () => ElMessage.info('图片功能开发中')
const handleEmoji = () => ElMessage.info('表情功能开发中')
const handleCall = () => ElMessage.info('通话功能开发中')
const handleSupervisorVoice = () => ElMessage.info('督导语音功能开发中')
const handleSupervisorImage = () => ElMessage.info('督导图片功能开发中')
const handleSupervisorEmoji = () => ElMessage.info('督导表情功能开发中')

onMounted(() => {
    initChat()
})

watch(messages, () => {
    scrollToBottom()
})
watch(supervisorMessages, () => {
    scrollSupervisorToBottom()
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