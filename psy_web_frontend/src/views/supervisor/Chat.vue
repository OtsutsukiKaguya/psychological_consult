<template>
    <SupervisorBaseLayout ref="layoutRef">
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
                    <button @click="endConsultation">结束咨询</button>
                </div>
            </div>

            <!-- 中间栏：用户与咨询师聊天记录（只读） -->
            <div class="center-panel">
                <div class="chat-box" ref="userConsultantChatBoxRef">
                    <div v-for="(message, index) in userConsultantMessages" :key="index"
                        :class="['message', message.type]">
                        <template v-if="message.type === 'user'">
                            <img src="@/assets/avatar.png" alt="用户头像" class="avatar" />
                            <div class="bubble user-bubble">{{ message.content }}</div>
                        </template>
                        <template v-else-if="message.type === 'consultant'">
                            <div class="bubble consultant-bubble">{{ message.content }}</div>
                            <img src="@/assets/avatar.png" alt="咨询师头像" class="avatar" />
                        </template>
                    </div>
                </div>
            </div>

            <!-- 右侧栏：督导与咨询师聊天 -->
            <div class="right-panel">
                <div class="supervisor-header">
                    <img src="@/assets/avatar.png" alt="咨询师头像" class="avatar" />
                    <h3>咨询师</h3>
                </div>
                <div class="chat-box supervisor-chat-box" ref="supervisorConsultantChatBoxRef">
                    <div v-for="(message, index) in supervisorConsultantMessages" :key="index"
                        :class="['message', message.isSelf ? 'supervisor' : 'consultant']">
                        <template v-if="message.isSelf">
                            <div class="bubble supervisor-bubble">{{ message.content }}</div>
                            <img src="@/assets/avatar.png" alt="督导头像" class="avatar" />
                        </template>
                        <template v-else>
                            <img src="@/assets/avatar.png" alt="咨询师头像" class="avatar" />
                            <div class="bubble consultant-bubble">{{ message.content }}</div>
                        </template>
                    </div>
                </div>
                <div class="input-area supervisor-input-area">
                    <div class="toolbar toolbar-align" style="position:relative;">
                        <img src="@/assets/chat/icon_microphone.png" alt="Mic" @click="handleUserVoice" />
                        <img src="@/assets/chat/icon-photo.png" alt="Image" @click="handleUserImage" />
                        <div style="display:inline-block;position:relative;">
                            <img src="@/assets/chat/icon-emoji.png" alt="Emoji" @click="handleUserEmoji"
                                class="emoji-icon" ref="userEmojiIconRef" />
                        </div>
                    </div>
                    <textarea v-model="inputMessage" placeholder="输入消息..." @keyup.enter="sendConsultMessage"></textarea>
                    <button class="send-button" @click="sendConsultMessage">发送</button>
                    <!-- fixed全局表情面板 for 右侧 -->
                    <transition name="fade">
                        <div v-if="showUserEmojiPanel" class="emoji-panel-fixed" :style="userEmojiPanelStyle"
                            ref="userEmojiPanelRef">
                            <div class="emoji-arrow-fixed"></div>
                            <div class="emoji-list">
                                <span v-for="emoji in emojiList" :key="emoji" class="emoji-item"
                                    @click="insertUserEmoji(emoji)">{{
                                        emoji }}</span>
                            </div>
                        </div>
                    </transition>
                </div>
            </div>
        </div>
    </SupervisorBaseLayout>
</template>

<script setup>
import SupervisorBaseLayout from '@/components/layout/SupervisorBaseLayout.vue'
import { ref, onMounted, nextTick, watch, onUnmounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { API, CHAT_BASE_URL } from '@/config'
import axios from 'axios'

const route = useRoute()
const router = useRouter()
const chatInfo = ref(null)
const userConsultantMessages = ref([])
const supervisorConsultantMessages = ref([])
const inputMessage = ref('')
const userInputMessage = ref('')

// 获取当前用户token
const TOKEN = localStorage.getItem('token') || ''

// WebSocket相关
const stompClient = ref(null)
const connected = ref(false)

// 添加计时相关的响应式变量
const consultationTime = ref('00:00:00')
const timer = ref(null)
const startTime = ref(null)

// emoji 相关
const showEmojiPanel = ref(false)
const emojiList = [
    '😀', '😁', '😂', '🤣', '😃', '😄', '😅', '😆', '😉', '😊', '😋', '😎', '😍', '😘', '🥰', '😗', '😙', '😚', '🙂', '🤗', '🤩', '🤔', '🤨', '😐', '😑', '😶', '🙄', '😏', '😣', '😥', '😮', '🤐', '😯', '😪', '😫', '🥱', '😴', '😌', '😛', '😜', '😝', '🤤', '😒', '😓', '😔', '😕', '🙃', '🤑', '😲', '☹️', '🙁', '😖', '😞', '😟', '😤', '😢', '😭', '😦', '😧', '😨', '😩', '🤯', '😬', '😰', '😱', '🥵', '🥶', '😳', '🤪', '😵', '😡', '😠', '🤬', '😷', '🤒', '🤕', '🤢', '🤮', '🥴', '😇', '🥳', '🥺', '🤠', '🤡', '🤥', '🤫', '🤭', '🧐', '🤓', '😈', '👿', '👹', '👺', '💀', '👻', '👽', '🤖', '💩', '😺', '😸', '😹', '😻', '😼', '😽', '🙀', '😿', '😾'
]

const emojiPanelRef = ref(null)
const emojiIconRef = ref(null)
const emojiPanelStyle = ref({ left: '0px', top: '0px' })

const handleEmoji = (event) => {
    showEmojiPanel.value = !showEmojiPanel.value
    nextTick(() => {
        // 让输入框保持焦点
        const textarea = document.querySelector('.input-area textarea')
        if (textarea) textarea.focus()
        // 定位emoji面板
        if (showEmojiPanel.value) {
            const icon = emojiIconRef.value
            const panel = emojiPanelRef.value
            if (icon && panel) {
                const rect = icon.getBoundingClientRect()
                const panelRect = panel.getBoundingClientRect()
                // 让面板居中于icon上方
                const left = rect.left + rect.width / 2 - panelRect.width / 2
                const top = rect.top - panelRect.height - 12 // 12px为小角高度
                emojiPanelStyle.value = {
                    left: `${Math.max(left, 8)}px`,
                    top: `${Math.max(top, 8)}px`
                }
            }
        }
    })
}

// 保存聊天状态到localStorage
const saveStateToStorage = () => {
    if (!chatInfo.value) return
    const chatState = {
        userConsultantMessages: userConsultantMessages.value,
        supervisorConsultantMessages: supervisorConsultantMessages.value,
        startTime: startTime.value ? startTime.value.getTime() : null,
        consultationTime: consultationTime.value
    }
    localStorage.setItem(`chat-timer-${chatInfo.value.id}`, JSON.stringify(chatState))
}

// 从localStorage加载聊天状态
const loadStateFromStorage = () => {
    if (!chatInfo.value) return
    const savedState = localStorage.getItem(`chat-timer-${chatInfo.value.id}`)
    if (savedState) {
        const state = JSON.parse(savedState)
        userConsultantMessages.value = state.userConsultantMessages || []
        supervisorConsultantMessages.value = state.supervisorConsultantMessages || []
        if (state.startTime) {
            startTime.value = new Date(state.startTime)
            consultationTime.value = state.consultationTime
        }
    } else {
        userConsultantMessages.value = []
        supervisorConsultantMessages.value = []
        consultationTime.value = '00:00:00'
        startTime.value = null
    }
}

// 清除localStorage中的聊天状态
const clearChatState = () => {
    if (!chatInfo.value) return
    localStorage.removeItem(`chat-timer-${chatInfo.value.id}`)
    userConsultantMessages.value = []
    supervisorConsultantMessages.value = []
    consultationTime.value = '00:00:00'
    startTime.value = null
}

// 聊天框ref
const userConsultantChatBoxRef = ref(null)
const supervisorConsultantChatBoxRef = ref(null)

// 滚动到底部
const scrollToBottom = () => {
    nextTick(() => {
        if (userConsultantChatBoxRef.value) {
            userConsultantChatBoxRef.value.scrollTop = userConsultantChatBoxRef.value.scrollHeight
        }
    })
}
const scrollSupervisorToBottom = () => {
    nextTick(() => {
        if (supervisorConsultantChatBoxRef.value) {
            supervisorConsultantChatBoxRef.value.scrollTop = supervisorConsultantChatBoxRef.value.scrollHeight
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

// 需要的sessionId
const USER_CONSULTANT_SESSION_ID = ref('') // 用户-咨询师
const SUPERVISOR_CONSULTANT_SESSION_ID = ref('') // 督导-咨询师

// onMounted时从路由参数获取
onMounted(() => {
    // 从localStorage获取督导咨询师-会话ID和用户咨询师-会话ID
    SUPERVISOR_CONSULTANT_SESSION_ID.value = localStorage.getItem('supervisorSessionId') || ''
    USER_CONSULTANT_SESSION_ID.value = localStorage.getItem('userSessionId') || ''
    console.log('[督导Chat页面] 中间栏sessionId:', USER_CONSULTANT_SESSION_ID.value)
    console.log('[督导Chat页面] 右边栏sessionId:', SUPERVISOR_CONSULTANT_SESSION_ID.value)
    initChat() // 先初始化聊天状态
    fetchParticipants() // 再获取参与者信息
    if (USER_CONSULTANT_SESSION_ID.value && SUPERVISOR_CONSULTANT_SESSION_ID.value) {
        connectWebSocket()
    } else {
        ElMessage.warning('请先从请求列表进入')
    }
})

// WebSocket推送处理
const connectWebSocket = () => {
    const socket = new window.SockJS('http://47.117.102.116:8081/ws')
    stompClient.value = window.Stomp.over(socket)
    stompClient.value.debug = null
    const headers = {
        Authorization: 'Bearer ' + TOKEN
    }
    stompClient.value.connect(headers, frame => {
        connected.value = true
        stompClient.value.subscribe('/user/queue/messages', message => {
            const receivedMessage = JSON.parse(message.body)
            console.log('[收到消息]', receivedMessage)
            // 中间栏：用户与咨询师
            if (receivedMessage.sessionId === USER_CONSULTANT_SESSION_ID.value) {
                console.log('[中间栏收到消息]', receivedMessage)
                if (receivedMessage.senderType === 'USER') {
                    userConsultantMessages.value.push({
                        type: 'user',
                        content: receivedMessage.content
                    })
                } else if (receivedMessage.senderType === 'COUNSELOR') {
                    userConsultantMessages.value.push({
                        type: 'consultant',
                        content: receivedMessage.content
                    })
                }
                scrollToBottom()
            }
            // 右侧栏：督导与咨询师
            if (receivedMessage.sessionId === SUPERVISOR_CONSULTANT_SESSION_ID.value) {
                console.log('[右侧栏收到消息]', receivedMessage)
                if (receivedMessage.senderType === 'COUNSELOR') {
                    supervisorConsultantMessages.value.push({
                        type: 'consultant', // 自己发的显示为consultant-bubble（绿色、靠右）
                        content: receivedMessage.content,
                        isSelf: false
                    })
                } else if (receivedMessage.senderType === 'TUTOR') {
                    supervisorConsultantMessages.value.push({
                        type: 'supervisor', // 督导发来的消息显示为supervisor-bubble（白底、靠左）
                        content: receivedMessage.content,
                        isSelf: true
                    })
                }
                scrollSupervisorToBottom()
            }
        })
    }, error => {
        ElMessage.error('连接失败，请检查网络')
        connected.value = false
    })
}

// 结束咨询
const endConsultation = () => {
    ElMessageBox.prompt(
        '请输入对本次协助的评价',
        '结束协助',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            inputPlaceholder: '请输入评价',
            inputType: 'textarea',
            inputValue: '',
        }
    ).then(async ({ value }) => {
        // value为评价内容
        const sessionId = SUPERVISOR_CONSULTANT_SESSION_ID.value
        if (!sessionId) {
            ElMessage.error('未获取到会话ID')
            return
        }
        try {
            await axios.post(`${CHAT_BASE_URL}/api/sessions/${sessionId}/end`, {
                comment: value,
                rating: 0
            }, {
                headers: {
                    'Authorization': `Bearer ${TOKEN}`
                }
            })
            // 成功后清理本地缓存
            clearChatState()
            stopTimer()
            if (layoutRef.value && layoutRef.value.removeConversation) {
                layoutRef.value.removeConversation(sessionId)
            }
            // 额外：同步localStorage中的supervisor_conversations
            const conversations = JSON.parse(localStorage.getItem('supervisor_conversations') || '[]')
            const idx = conversations.findIndex(c => String(c.id) === sessionId)
            if (idx !== -1) {
                conversations.splice(idx, 1)
                localStorage.setItem('supervisor_conversations', JSON.stringify(conversations))
            }
            router.push('/supervisor/request-list')
            ElMessage({
                type: 'success',
                message: '协助已结束'
            })
        } catch (e) {
            ElMessage.error('结束协助失败')
        }
    }).catch(() => {
        // 用户点击取消，无需处理
    })
}

// 工具栏功能
const handleVoice = () => ElMessage.info('语音功能开发中')
const handleImage = () => ElMessage.info('图片功能开发中')
const handleUserVoice = () => ElMessage.info('用户语音功能开发中')
const handleUserImage = () => ElMessage.info('用户图片功能开发中')

// 右侧emoji面板相关
const showUserEmojiPanel = ref(false)
const userEmojiPanelRef = ref(null)
const userEmojiIconRef = ref(null)
const userEmojiPanelStyle = ref({ left: '0px', top: '0px' })

const handleUserEmoji = () => {
    showUserEmojiPanel.value = !showUserEmojiPanel.value
    nextTick(() => {
        // 让输入框保持焦点
        const textarea = document.querySelector('.supervisor-input-area textarea')
        if (textarea) textarea.focus()
        // 定位emoji面板
        if (showUserEmojiPanel.value) {
            const icon = userEmojiIconRef.value
            const panel = userEmojiPanelRef.value
            if (icon && panel) {
                const rect = icon.getBoundingClientRect()
                const panelRect = panel.getBoundingClientRect()
                const left = rect.left + rect.width / 2 - panelRect.width / 2
                const top = rect.top - panelRect.height - 12
                userEmojiPanelStyle.value = {
                    left: `${Math.max(left, 8)}px`,
                    top: `${Math.max(top, 8)}px`
                }
            }
        }
    })
}

const insertUserEmoji = (emoji) => {
    const textarea = document.querySelector('.supervisor-input-area textarea')
    if (!textarea) return
    const start = textarea.selectionStart
    const end = textarea.selectionEnd
    const value = userInputMessage.value
    userInputMessage.value = value.slice(0, start) + emoji + value.slice(end)
    nextTick(() => {
        textarea.focus()
        textarea.selectionStart = textarea.selectionEnd = start + emoji.length
        showUserEmojiPanel.value = false
    })
}

// 监听空白关闭左侧emoji面板
watch(showEmojiPanel, (val) => {
    if (val) {
        document.addEventListener('mousedown', handleClickOutsideEmoji)
    } else {
        document.removeEventListener('mousedown', handleClickOutsideEmoji)
    }
})
const handleClickOutsideEmoji = (e) => {
    const panel = emojiPanelRef.value
    const icon = emojiIconRef.value
    if (panel && !panel.contains(e.target) && icon && !icon.contains(e.target)) {
        showEmojiPanel.value = false
    }
}

// 监听空白关闭右侧emoji面板
watch(showUserEmojiPanel, (val) => {
    if (val) {
        document.addEventListener('mousedown', handleClickOutsideUserEmoji)
    } else {
        document.removeEventListener('mousedown', handleClickOutsideUserEmoji)
    }
})
const handleClickOutsideUserEmoji = (e) => {
    const panel = userEmojiPanelRef.value
    const icon = userEmojiIconRef.value
    if (panel && !panel.contains(e.target) && icon && !icon.contains(e.target)) {
        showUserEmojiPanel.value = false
    }
}

const counselorInfo = ref({ id: '', name: '' })

const layoutRef = ref(null)

const fetchParticipants = async () => {
    const sessionId = route.params.id
    if (!sessionId) return
    try {
        const res = await axios.get(`${CHAT_BASE_URL}/api/sessions/${sessionId}/participants`)
        if (Array.isArray(res.data)) {
            const counselor = res.data.find(p => p.role === 'COUNSELOR')
            if (counselor) {
                counselorInfo.value = { id: counselor.id, name: counselor.name }
                // 只在chatInfo不存在时才设置
                if (!chatInfo.value) {
                    chatInfo.value = {
                        id: counselor.id,
                        name: counselor.name,
                        duration: '00:00:00'
                    }
                }
                if (layoutRef.value) {
                    // 先查找会话是否已存在
                    const conversations = JSON.parse(localStorage.getItem('supervisor_conversations') || '[]')
                    const exist = conversations.find(c => String(c.id) === String(sessionId))
                    if (!exist && layoutRef.value.addConversation) {
                        layoutRef.value.addConversation({ id: sessionId, name: counselor.name, avatar: '', unread: 0 })
                    }
                    if (layoutRef.value.updateConversationName) {
                        layoutRef.value.updateConversationName(sessionId, counselor.name)
                    }
                }
            }
        }
    } catch (e) {
        // 可以根据需要提示错误
    }
}

// 初始化聊天
const initChat = async () => {
    try {
        // 根据当前路由id查找会话信息
        const chatId = route.params.id
        let name = chatId
        const conversations = JSON.parse(localStorage.getItem('supervisor_conversations') || '[]')
        const found = conversations.find(c => String(c.id) === String(chatId))
        if (found) {
            name = found.name
        }
        chatInfo.value = {
            id: chatId,
            name: name,
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

watch(userConsultantMessages, () => {
    scrollToBottom()
    saveStateToStorage()
}, { deep: true })

watch(supervisorConsultantMessages, () => {
    scrollSupervisorToBottom()
    saveStateToStorage()
}, { deep: true })

watch(() => route.params.id, () => {
    stopTimer()
    saveStateToStorage() // 保存当前会话状态
    initChat() // 初始化新会话
})

onUnmounted(() => {
    if (stompClient.value) {
        stompClient.value.disconnect()
    }
    stopTimer()
    saveStateToStorage() // 只保存，不清除
    document.removeEventListener('mousedown', handleClickOutsideEmoji)
    document.removeEventListener('mousedown', handleClickOutsideUserEmoji)
})

const insertEmoji = (emoji) => {
    const textarea = document.querySelector('.input-area textarea')
    if (!textarea) return
    const start = textarea.selectionStart
    const end = textarea.selectionEnd
    const value = inputMessage.value
    inputMessage.value = value.slice(0, start) + emoji + value.slice(end)
    nextTick(() => {
        textarea.focus()
        textarea.selectionStart = textarea.selectionEnd = start + emoji.length
        showEmojiPanel.value = false
    })
}

// 发送右侧栏消息（督导与咨询师）
const sendConsultMessage = () => {
    if (!inputMessage.value.trim() || !SUPERVISOR_CONSULTANT_SESSION_ID.value) return
    fetch(`${CHAT_BASE_URL}/api/messages/session/${SUPERVISOR_CONSULTANT_SESSION_ID.value}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${TOKEN}`
        },
        body: JSON.stringify({
            content: inputMessage.value,
            type: 'TEXT',
            fileId: 0
        })
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('发送失败')
            }
            return response.json()
        })
        .then(() => {
            supervisorConsultantMessages.value.push({
                content: inputMessage.value,
                type: 'supervisor', // 自己发的显示为supervisor-bubble（右，绿色）
                isSelf: true
            })
            inputMessage.value = ''
            scrollSupervisorToBottom()
            saveStateToStorage()
        })
        .catch(error => {
            ElMessage.error('发送消息失败')
        })
}
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

.supervisor-bubble {
    background-color: #5060c5;
    color: #fff;
}

.input-area {
    border-top: 1px solid #e0e0e0;
    background-color: #fff;
}

.toolbar {
    display: flex;
    gap: 15px;
    align-items: center;
    height: 40px;
    padding: 10px 20px;
    background: none;
}

.toolbar-align {
    height: 40px;
    padding: 10px 20px;
    align-items: center;
    display: flex;
    gap: 15px;
}

.toolbar img,
.toolbar .emoji-icon {
    width: 20px;
    height: 20px;
    cursor: pointer;
    vertical-align: middle;
    margin: 0 2px;
    display: inline-block;
    position: relative;
    top: 0;
}

.emoji-icon {
    vertical-align: middle;
    top: 0;
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
    align-self: flex-end;
    display: flex;
    flex-direction: row;
}

.supervisor-chat-box .message.consultant {
    align-self: flex-start;
    display: flex;
    flex-direction: row;
}

.supervisor-chat-box .supervisor-bubble {
    background-color: #19c490;
    color: #fff;
}

.supervisor-chat-box .consultant-bubble {
    background-color: #fff;
    color: #333;
    border: 1px solid #e0e0e0;
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

.emoji-panel-fixed {
    position: fixed;
    z-index: 9999;
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
    padding: 10px 12px 6px 12px;
    min-width: 320px;
    max-width: 400px;
    max-height: 220px;
    overflow-y: auto;
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    opacity: 1;
    border: 1px solid #e0e0e0;
}

.emoji-arrow-fixed {
    position: absolute;
    left: 50%;
    transform: translateX(-50%);
    bottom: -12px;
    width: 0;
    height: 0;
    border-left: 10px solid transparent;
    border-right: 10px solid transparent;
    border-top: 12px solid #fff;
    filter: drop-shadow(0 2px 2px rgba(0, 0, 0, 0.08));
}

.emoji-list {
    display: flex;
    flex-wrap: wrap;
    gap: 6px;
    width: 100%;
}

.emoji-item {
    font-size: 22px;
    cursor: pointer;
    padding: 2px 4px;
    border-radius: 4px;
    transition: background 0.15s;
}

.emoji-item:hover {
    background: #f5f5f5;
}

.fade-enter-active,
.fade-leave-active {
    transition: opacity 0.2s;
}

.fade-enter-from,
.fade-leave-to {
    opacity: 0;
}
</style>