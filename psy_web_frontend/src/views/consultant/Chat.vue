# 创建聊天页面
<template>
    <ConsultantBaseLayout ref="layoutRef">
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
                                <template v-if="message.fileUrl">
                                    <template v-if="isImage(message.fileType, message.fileUrl)">
                                        <el-image :src="message.fileUrl"
                                            style="max-width: 200px; max-height: 200px; cursor: pointer"
                                            :preview-src-list="[message.fileUrl]" fit="contain" />
                                    </template>
                                    <template v-else-if="message.isAudio">
                                        <span>[语音消息，{{ message.duration || '?' }}秒]</span>
                                    </template>
                                    <template v-else-if="message.isFile">
                                        <a :href="message.fileUrl" target="_blank"
                                            style="color: #222 !important; font-weight: bold !important;"
                                            rel="noopener noreferrer" download>
                                            [收到文件，点击可以下载] {{ message.content }}
                                        </a>
                                    </template>
                                    <template v-else>
                                        <a :href="message.fileUrl" target="_blank" style="color: white;"
                                            rel="noopener noreferrer" download>{{ message.content }}</a>
                                    </template>
                                </template>
                                <template v-else>
                                    {{ message.content }}
                                </template>
                            </div>
                        </template>
                        <template v-else-if="message.type === 'consultant'">
                            <div class="bubble consultant-bubble">
                                <template v-if="message.fileUrl">
                                    <template v-if="isImage(message.fileType, message.fileUrl)">
                                        <el-image :src="message.fileUrl"
                                            style="max-width: 200px; max-height: 200px; cursor: pointer"
                                            :preview-src-list="[message.fileUrl]" fit="contain" />
                                    </template>
                                    <template v-else-if="message.isFile">
                                        <a :href="message.fileUrl" target="_blank"
                                            style="color: #222 !important; font-weight: bold !important;"
                                            rel="noopener noreferrer" download>
                                            [收到文件，点击可以下载]{{ message.content }}
                                        </a>
                                    </template>
                                    <template v-else>
                                        <a :href="message.fileUrl" target="_blank" style="color: white;"
                                            rel="noopener noreferrer" download>{{ message.content }}</a>
                                    </template>
                                </template>
                                <template v-else>
                                    {{ message.content }}
                                </template>
                            </div>
                            <img src="@/assets/avatar.png" :alt="`${message.type}头像`" class="avatar" />
                        </template>
                    </div>
                </div>
                <div class="input-area">
                    <div class="toolbar toolbar-align" style="position:relative;">
                        <img src="@/assets/chat/icon_microphone.png" alt="Mic" @click="handleVoice" />
                        <img src="@/assets/chat/icon-photo.png" alt="Image" @click="handleImage" />
                        <input ref="fileInputRef" type="file" style="display:none" @change="handleFileChange" />
                        <div style="display:inline-block;position:relative;">
                            <img src="@/assets/chat/icon-emoji.png" alt="Emoji" @click="handleEmoji" class="emoji-icon"
                                ref="emojiIconRef" />
                        </div>
                        <img src="@/assets/chat/Phone.png" alt="Phone" @click="handleCall" />
                    </div>
                    <textarea v-model="inputMessage" placeholder="输入消息..." @keyup.enter="sendMessage"></textarea>
                    <button class="send-button" @click="sendMessage">发送</button>
                </div>
                <!-- fixed全局表情面板 -->
                <transition name="fade">
                    <div v-if="showEmojiPanel" class="emoji-panel-fixed" :style="emojiPanelStyle" ref="emojiPanelRef">
                        <div class="emoji-arrow-fixed"></div>
                        <div class="emoji-list">
                            <span v-for="emoji in emojiList" :key="emoji" class="emoji-item"
                                @click="insertEmoji(emoji)">{{ emoji
                                }}</span>
                        </div>
                    </div>
                </transition>
            </div>

            <!-- 右侧栏：咨询师与督导聊天 -->
            <div class="right-panel">
                <div class="supervisor-header">
                    <img src="@/assets/avatar.png" alt="督导头像" class="avatar" />
                    <h3>{{ tutorName }}</h3>
                </div>
                <div class="chat-box supervisor-chat-box" ref="supervisorChatBoxRef">
                    <div v-for="(message, index) in tutorMessages" :key="index" :class="['message', message.type]">
                        <template v-if="message.type === 'supervisor'">
                            <img src="@/assets/avatar.png" alt="督导头像" class="avatar" />
                            <div class="bubble supervisor-bubble">
                                <template v-if="message.fileUrl">
                                    <template v-if="isImage(message.fileType, message.fileUrl)">
                                        <el-image :src="message.fileUrl"
                                            style="max-width: 200px; max-height: 200px; cursor: pointer"
                                            :preview-src-list="[message.fileUrl]" fit="contain" />
                                    </template>
                                    <template v-else-if="message.isFile">
                                        <a :href="message.fileUrl" target="_blank"
                                            style="color: #222 !important; font-weight: bold !important;"
                                            rel="noopener noreferrer" download>
                                            [收到文件，点击可以下载]{{ message.content }}
                                        </a>
                                    </template>
                                    <template v-else>
                                        <a :href="message.fileUrl" target="_blank" style="color: white;"
                                            rel="noopener noreferrer" download>{{ message.content }}</a>
                                    </template>
                                </template>
                                <template v-else>
                                    {{ message.content }}
                                </template>
                            </div>
                        </template>
                        <template v-else-if="message.type === 'consultant'">
                            <div class="bubble consultant-bubble">
                                <template v-if="message.fileUrl">
                                    <template v-if="isImage(message.fileType, message.fileUrl)">
                                        <el-image :src="message.fileUrl"
                                            style="max-width: 200px; max-height: 200px; cursor: pointer"
                                            :preview-src-list="[message.fileUrl]" fit="contain" />
                                    </template>
                                    <template v-else-if="message.isFile">
                                        <a :href="message.fileUrl" target="_blank"
                                            style="color: #222 !important; font-weight: bold !important;"
                                            rel="noopener noreferrer" download>
                                            [收到文件，点击可以下载]{{ message.content }}
                                        </a>
                                    </template>
                                    <template v-else>
                                        <a :href="message.fileUrl" target="_blank" style="color: white;"
                                            rel="noopener noreferrer" download>{{ message.content }}</a>
                                    </template>
                                </template>
                                <template v-else>
                                    {{ message.content }}
                                </template>
                            </div>
                            <img src="@/assets/avatar.png" alt="咨询师头像" class="avatar" />
                        </template>
                    </div>
                </div>
                <div class="input-area supervisor-input-area">
                    <div class="toolbar toolbar-align" style="position:relative;">
                        <img src="@/assets/chat/icon_microphone.png" alt="Mic" @click="handleSupervisorVoice" />
                        <img src="@/assets/chat/icon-photo.png" alt="Image" @click="handleSupervisorImage"
                            :style="{ opacity: hasRequestedSupervisor ? 1 : 0.5, cursor: hasRequestedSupervisor ? 'pointer' : 'not-allowed' }" />
                        <input ref="supervisorFileInputRef" type="file" style="display:none"
                            @change="handleSupervisorFileChange" :disabled="!hasRequestedSupervisor" />
                        <div style="display:inline-block;position:relative;">
                            <img src="@/assets/chat/icon-emoji.png" alt="Emoji" @click="handleSupervisorEmoji"
                                class="emoji-icon" ref="supervisorEmojiIconRef" />
                        </div>
                    </div>
                    <textarea v-model="supervisorInputMessage"
                        :placeholder="hasRequestedSupervisor ? '输入消息...' : '请先请求督导'"
                        @keyup.enter="sendSupervisorMessage" :disabled="!hasRequestedSupervisor"></textarea>
                    <button class="send-button" @click="sendSupervisorMessage"
                        :disabled="!hasRequestedSupervisor">发送</button>

                    <!-- fixed全局表情面板 for 右侧 -->
                    <transition name="fade">
                        <div v-if="showSupervisorEmojiPanel" class="emoji-panel-fixed"
                            :style="supervisorEmojiPanelStyle" ref="supervisorEmojiPanelRef">
                            <div class="emoji-arrow-fixed"></div>
                            <div class="emoji-list">
                                <span v-for="emoji in emojiList" :key="emoji" class="emoji-item"
                                    @click="insertSupervisorEmoji(emoji)">{{ emoji }}</span>
                            </div>
                        </div>
                    </transition>
                </div>
            </div>
        </div>
        <div v-if="showTutorSelect" style="padding: 20px; background: #fff; border-top: 1px solid #eee;">
            <el-select v-model="selectedTutorId" placeholder="请选择督导" style="width: 200px;">
                <el-option v-for="tutor in tutorList" :key="tutor.id" :label="tutor.name" :value="tutor.id" />
            </el-select>
            <el-button type="primary" @click="confirmSelectTutor" style="margin-left: 16px;">确定</el-button>
        </div>
        <el-dialog v-model="showTutorSelect" title="请选择督导" width="350px" :close-on-click-modal="false" center>
            <el-select v-model="selectedTutorId" placeholder="请选择督导" style="width: 100%;">
                <el-option v-for="tutor in tutorList" :key="tutor.id" :label="tutor.name" :value="tutor.id" />
            </el-select>
            <template #footer>
                <el-button @click="showTutorSelect = false">取消</el-button>
                <el-button type="primary" @click="confirmSelectTutor">确定</el-button>
            </template>
        </el-dialog>
    </ConsultantBaseLayout>
</template>

<script setup>
import ConsultantBaseLayout from '@/components/layout/ConsultantBaseLayout.vue'
import { ref, onMounted, nextTick, watch, onUnmounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, ElSelect, ElOption } from 'element-plus'
import { API, CHAT_BASE_URL } from '@/config'
import axios from 'axios'

const route = useRoute()
const router = useRouter()
const chatInfo = ref(null)
const messages = ref([])
const tutorMessages = ref([])
const inputMessage = ref('')
const supervisorInputMessage = ref('')

// 获取当前用户token
const TOKEN = localStorage.getItem('token') || ''
// 动态获取SESSION_ID
const SESSION_ID = computed(() => {
    const chatId = route.params.id
    return localStorage.getItem(`sessionId-${chatId}`) || ''
})

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
        messages: messages.value,
        tutorMessages: tutorMessages.value,
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
        messages.value = state.messages
        tutorMessages.value = state.tutorMessages || []
        if (state.startTime) {
            startTime.value = new Date(state.startTime)
            consultationTime.value = state.consultationTime
        }
    } else {
        messages.value = []
        tutorMessages.value = []
        consultationTime.value = '00:00:00'
        startTime.value = null
    }
}

// 清除localStorage中的聊天状态
const clearChatState = () => {
    localStorage.removeItem(`chat-timer-${chatInfo.value.id}`)
    messages.value = []
    tutorMessages.value = []
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

// 右侧栏sessionId
const tutorSessionId = ref('')
const tutorName = ref('督导')

// 根据当前会话id加载自己的tutorSessionId和tutorName
function loadTutorSessionInfo() {
    const chatId = route.params.id
    const localTutorSessionId = localStorage.getItem(`tutorSessionId-${chatId}`)
    if (localTutorSessionId) {
        tutorSessionId.value = localTutorSessionId
    } else {
        tutorSessionId.value = ''
    }
    const localTutorName = localStorage.getItem(`tutorName-${chatId}`)
    if (localTutorName) {
        tutorName.value = localTutorName
    } else {
        tutorName.value = '督导'
    }
}

// 监听请求督导接口返回sessionId
const confirmSelectTutor = async () => {
    const supervisorId = selectedTutorId.value
    const supervisor = tutorList.value.find(t => t.id === supervisorId)
    const consultId = localStorage.getItem(`consultId-${route.params.id}`) || ''
    if (!consultId) {
        ElMessage.error('未找到consultId')
        return
    }
    // 先请求获取groupSessionId（即咨询师与督导的sessionId）
    // 这里假设当前页面的sessionId就是groupSessionId
    const groupSessionId = localStorage.getItem(`sessionId-${route.params.id}`) || ''
    if (!groupSessionId) {
        ElMessage.error('未找到sessionId')
        return
    }
    try {
        const res = await axios.post(`${CHAT_BASE_URL}/api/sessions/supervisor/with-group`, {
            consultId,
            supervisorId,
            groupSessionId
        })
        console.log('[请求督导] /api/sessions/supervisor/with-group 返回:', res.data)
        if (res.data && res.data.sessionId) {
            tutorSessionId.value = res.data.sessionId
            tutorName.value = res.data.userName || '督导'
            localStorage.setItem(`tutorSessionId-${route.params.id}`, res.data.sessionId)
            localStorage.setItem(`tutorName-${route.params.id}`, res.data.userName || '')
            localStorage.setItem(`tutorId-${route.params.id}`, res.data.userId || '')
            ElMessage.success('已选择督导: ' + (supervisor?.name || ''))
            showTutorSelect.value = false
        } else {
            ElMessage.error('未获取到督导会话ID')
        }
    } catch (e) {
        ElMessage.error('请求失败')
    }
}

// 新增：所有会话卡的tutorMessages都存储在localStorage，按sessionId区分
function appendTutorMessageToStorage(sessionId, message) {
    const key = `tutorMessages-${sessionId}`
    let msgs = []
    try {
        msgs = JSON.parse(localStorage.getItem(key)) || []
    } catch (e) { }
    msgs.push(message)
    localStorage.setItem(key, JSON.stringify(msgs))
}

function loadTutorMessages() {
    const key = `tutorMessages-${tutorSessionId.value}`
    let msgs = []
    try {
        msgs = JSON.parse(localStorage.getItem(key)) || []
    } catch (e) { }
    tutorMessages.value = msgs
}

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
            if (receivedMessage.sessionId === SESSION_ID.value) {
                console.log('[中间栏收到消息]', receivedMessage)
                if (receivedMessage.type === 'IMAGE' || isOssImageUrl(receivedMessage.content)) {
                    messages.value.push({
                        type: 'user',
                        content: '',
                        fileUrl: receivedMessage.content,
                        isImage: true
                    })
                } else if (receivedMessage.type === 'FILE') {
                    let fileInfo = {}
                    try {
                        fileInfo = JSON.parse(receivedMessage.content)
                        // 判断是否为音频
                        if (isAudio(fileInfo.fileType, fileInfo.url)) {
                            messages.value.push({
                                type: 'user',
                                content: `[语音消息，${fileInfo.duration || '?'}秒]`,
                                fileUrl: fileInfo.url,
                                fileType: fileInfo.fileType || '',
                                isAudio: true,
                                duration: fileInfo.duration || ''
                            })
                        } else if (isImage(fileInfo.fileType, fileInfo.url)) {
                            messages.value.push({
                                type: 'user',
                                content: fileInfo.fileName,
                                fileUrl: fileInfo.url,
                                fileType: fileInfo.fileType || ''
                                // 不设置isFile
                            })
                        } else {
                            messages.value.push({
                                type: 'user',
                                content: fileInfo.fileName,
                                fileUrl: fileInfo.url,
                                fileType: fileInfo.fileType || '',
                                isFile: true
                            })
                        }
                    } catch (e) {
                        console.error('解析文件信息失败:', e)
                        messages.value.push({
                            type: 'user',
                            content: '未知文件',
                            fileUrl: '',
                            fileType: '',
                            isFile: true
                        })
                    }
                } else {
                    messages.value.push({
                        type: 'user',
                        content: receivedMessage.content
                    })
                }
                scrollToBottom()
            }
            // 右侧栏：督导与咨询师
            // 新增：无论当前页面在哪个会话卡，只要收到属于某个会话卡（sessionId匹配）的督导消息，都追加到对应localStorage
            // 这里假设所有tutorSessionId都以localStorage的tutorSessionId-<chatId>存储
            const allKeys = Object.keys(localStorage).filter(k => k.startsWith('tutorSessionId-'))
            const allSessionIds = allKeys.map(k => localStorage.getItem(k)).filter(Boolean)
            if (allSessionIds.includes(receivedMessage.sessionId)) {
                // 组装消息对象
                let msgObj = null
                if (receivedMessage.type === 'FILE') {
                    let fileInfo = {}
                    try {
                        fileInfo = JSON.parse(receivedMessage.content)
                        msgObj = {
                            type: receivedMessage.senderType === 'TUTOR' || receivedMessage.senderType === 'SYSTEM' ? 'supervisor' : 'consultant',
                            content: fileInfo.fileName,
                            fileUrl: fileInfo.url,
                            fileType: getFileTypeFromUrl(fileInfo.url),
                            isSelf: receivedMessage.senderType === 'TUTOR' || receivedMessage.senderType === 'SYSTEM',
                            isFile: true
                        }
                    } catch (e) {
                        console.error('解析文件信息失败:', e)
                        msgObj = {
                            type: receivedMessage.senderType === 'TUTOR' || receivedMessage.senderType === 'SYSTEM' ? 'supervisor' : 'consultant',
                            content: '未知文件',
                            fileUrl: '',
                            fileType: '',
                            isSelf: receivedMessage.senderType === 'TUTOR' || receivedMessage.senderType === 'SYSTEM',
                            isFile: true
                        }
                    }
                } else {
                    // SYSTEM消息显示在supervisor bubble
                    let msgType = 'consultant'
                    let isSelf = false
                    if (receivedMessage.senderType === 'TUTOR' || receivedMessage.senderType === 'SYSTEM') {
                        msgType = 'supervisor'
                        isSelf = true
                    }
                    msgObj = {
                        type: msgType,
                        content: receivedMessage.content,
                        isSelf
                    }
                }
                appendTutorMessageToStorage(receivedMessage.sessionId, msgObj)
                // 如果当前页面的tutorSessionId就是这条消息的sessionId，则同步到tutorMessages.value
                if (tutorSessionId.value && receivedMessage.sessionId === tutorSessionId.value) {
                    loadTutorMessages()
                    scrollSupervisorToBottom()
                }
            }
        })
    }, error => {
        ElMessage.error('连接失败，请检查网络')
        connected.value = false
    })
}

// 发送督导消息（右侧栏）
const sendSupervisorMessage = () => {
    if (!supervisorInputMessage.value.trim() || !tutorSessionId.value) return
    const msg = supervisorInputMessage.value
    // 先添加到本地消息列表
    const myMsg = {
        type: 'consultant',
        content: msg,
        isSelf: true
    }
    tutorMessages.value.push(myMsg)
    appendTutorMessageToStorage(tutorSessionId.value, myMsg)
    // 清空输入框
    supervisorInputMessage.value = ''
    // 滚动到底部
    scrollSupervisorToBottom()
    // 保存状态
    saveStateToStorage()

    // 发送到服务器
    fetch(`${CHAT_BASE_URL}/api/messages/session/${tutorSessionId.value}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${TOKEN}`
        },
        body: JSON.stringify({
            content: msg,
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
            console.log('[右侧栏发送消息]', msg)
        })
        .catch(error => {
            ElMessage.error('发送消息失败')
            // 发送失败时从消息列表中移除
            tutorMessages.value.pop()
        })
}

// 请求督导
const showTutorSelect = ref(false)

const hasRequestedSupervisor = ref(
    localStorage.getItem(`hasRequestedSupervisor-${route.params.id}`) === 'true'
)

const requestSupervisor = async () => {
    // 这里假设 dutyDate 为今天日期，格式为 yyyy-MM-dd
    const today = new Date()
    const yyyy = today.getFullYear()
    const mm = String(today.getMonth() + 1).padStart(2, '0')
    const dd = String(today.getDate()).padStart(2, '0')
    const dutyDate = `${yyyy}-${mm}-${dd}`
    const url = API.DUTY.GET_BY_DATE(dutyDate)
    console.log('[请求督导] dutyDate:', dutyDate)
    console.log('[请求督导] url:', url)
    try {
        const res = await axios.get(url)
        console.log('[请求督导] 接口返回:', res)
        console.log('[请求督导] data内容:', res.data.data)
        const tutors = Array.isArray(res.data.data) ? res.data.data.filter(item => item.role === 'TUTOR') : [];
        console.log('[请求督导] TUTOR数组:', tutors)
        if (res.data.code === 0 && tutors.length > 0) {
            tutorList.value = tutors
            selectedTutorId.value = tutorList.value[0].id
            showTutorSelect.value = true
            hasRequestedSupervisor.value = true
            localStorage.setItem(`hasRequestedSupervisor-${route.params.id}`, 'true')
        } else if (res.data.code === 0 && tutors.length === 0) {
            ElMessage.warning('今日无可用督导')
            return
        } else {
            console.warn('[请求督导] 获取失败，返回数据：', res.data)
            ElMessage.error('获取督导列表失败')
        }
    } catch (e) {
        console.error('[请求督导] 请求异常:', e)
        ElMessage.error('请求失败')
    }
}

// 结束咨询
const endConsultation = () => {
    ElMessageBox.prompt(
        '请输入对本次咨询用户的建议',
        '结束咨询',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            inputPlaceholder: '请输入评价',
            inputType: 'textarea',
            inputValue: '',
        }
    ).then(async ({ value }) => {
        // value为评价内容
        const sessionId = SESSION_ID.value
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

            // 彻底清除所有相关数据
            messages.value = []
            tutorMessages.value = []
            startTime.value = null
            consultationTime.value = '00:00:00'

            // 清除chat-timer中存储的聊天记录
            const chatId = String(route.params.id)
            localStorage.removeItem(`chat-timer-${chatId}`)

            // 清除右侧栏tutorMessages的localStorage数据
            if (tutorSessionId.value) {
                localStorage.removeItem(`tutorMessages-${tutorSessionId.value}`)
                // 有些消息可能存储在chatId的key下
                localStorage.removeItem(`tutorMessages-${chatId}`)
            }

            // 额外清理所有可能相关的数据
            localStorage.removeItem(`sessionId-${chatId}`)
            localStorage.removeItem(`consultId-${chatId}`)
            localStorage.removeItem(`hasRequestedSupervisor-${chatId}`)
            localStorage.removeItem(`tutorSessionId-${chatId}`)
            localStorage.removeItem(`tutorName-${chatId}`)
            localStorage.removeItem(`tutorId-${chatId}`)

            // 检查是否有其他相关数据
            Object.keys(localStorage).forEach(key => {
                if (key.includes(chatId) || (tutorSessionId.value && key.includes(tutorSessionId.value))) {
                    localStorage.removeItem(key)
                }
            })

            if (layoutRef.value && layoutRef.value.removeConversation) {
                layoutRef.value.removeConversation(chatId)
            }
            // 跳转到预约情况页面
            router.push('/consultant/schedule').then(() => {
                window.location.reload()
            })
            ElMessage({
                type: 'success',
                message: '咨询已结束'
            })
        } catch (e) {
            ElMessage.error('结束咨询失败')
        }
    }).catch(() => {
        // 用户点击取消，无需处理
    })
}

// 工具栏功能
const handleVoice = () => ElMessage.info('语音功能开发中')
const handleImage = () => {
    if (fileInputRef.value) fileInputRef.value.value = '' // 清空上次选择
    fileInputRef.value && fileInputRef.value.click()
}
const handleCall = () => ElMessage.info('通话功能开发中')
const handleSupervisorVoice = () => ElMessage.info('督导语音功能开发中')
const handleSupervisorImage = () => {
    if (!hasRequestedSupervisor.value) return
    if (supervisorFileInputRef.value) supervisorFileInputRef.value.value = '' // 清空上次选择
    supervisorFileInputRef.value && supervisorFileInputRef.value.click()
}

// 右侧emoji面板相关
const showSupervisorEmojiPanel = ref(false)
const supervisorEmojiPanelRef = ref(null)
const supervisorEmojiIconRef = ref(null)
const supervisorEmojiPanelStyle = ref({ left: '0px', top: '0px' })

const handleSupervisorEmoji = () => {
    showSupervisorEmojiPanel.value = !showSupervisorEmojiPanel.value
    nextTick(() => {
        // 让输入框保持焦点
        const textarea = document.querySelector('.supervisor-input-area textarea')
        if (textarea) textarea.focus()
        // 定位emoji面板
        if (showSupervisorEmojiPanel.value) {
            const icon = supervisorEmojiIconRef.value
            const panel = supervisorEmojiPanelRef.value
            if (icon && panel) {
                const rect = icon.getBoundingClientRect()
                const panelRect = panel.getBoundingClientRect()
                const left = rect.left + rect.width / 2 - panelRect.width / 2
                const top = rect.top - panelRect.height - 12
                supervisorEmojiPanelStyle.value = {
                    left: `${Math.max(left, 8)}px`,
                    top: `${Math.max(top, 8)}px`
                }
            }
        }
    })
}

const insertSupervisorEmoji = (emoji) => {
    const textarea = document.querySelector('.supervisor-input-area textarea')
    if (!textarea) return
    const start = textarea.selectionStart
    const end = textarea.selectionEnd
    const value = supervisorInputMessage.value
    supervisorInputMessage.value = value.slice(0, start) + emoji + value.slice(end)
    nextTick(() => {
        textarea.focus()
        textarea.selectionStart = textarea.selectionEnd = start + emoji.length
        showSupervisorEmojiPanel.value = false
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
watch(showSupervisorEmojiPanel, (val) => {
    if (val) {
        document.addEventListener('mousedown', handleClickOutsideSupervisorEmoji)
    } else {
        document.removeEventListener('mousedown', handleClickOutsideSupervisorEmoji)
    }
})
const handleClickOutsideSupervisorEmoji = (e) => {
    const panel = supervisorEmojiPanelRef.value
    const icon = supervisorEmojiIconRef.value
    if (panel && !panel.contains(e.target) && icon && !icon.contains(e.target)) {
        showSupervisorEmojiPanel.value = false
    }
}

// 初始化聊天
const initChat = async () => {
    try {
        // 根据当前路由id查找会话信息
        const chatId = route.params.id
        let name = chatId
        const conversations = JSON.parse(localStorage.getItem('conversations') || '[]')
        const found = conversations.find(c => String(c.id) === String(chatId))
        if (found) {
            name = found.name
        }
        chatInfo.value = {
            id: chatId,
            name: name,
            duration: '00:00:00'
        }
        // 加载督导状态
        hasRequestedSupervisor.value = localStorage.getItem(`hasRequestedSupervisor-${chatId}`) === 'true'
        // 加载本会话卡的tutorSessionId和tutorName
        loadTutorSessionInfo()
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
    if (SESSION_ID.value) {
        connectWebSocket()
    }
    // 先赋值tutorSessionId再加载消息
    nextTick(() => {
        loadTutorMessages()
    })
})

watch(messages, () => {
    scrollToBottom()
    saveStateToStorage()
}, { deep: true })
watch(tutorMessages, () => {
    scrollSupervisorToBottom()
    saveStateToStorage()
}, { deep: true })

// 切换会话卡时，重新加载tutorSessionId和tutorName，并加载自己的tutorMessages
watch(() => route.params.id, () => {
    stopTimer()
    saveStateToStorage()
    loadTutorSessionInfo()
    // 先赋值tutorSessionId再加载消息
    nextTick(() => {
        loadTutorMessages()
    })
    initChat()
})

onUnmounted(() => {
    if (stompClient.value) {
        stompClient.value.disconnect()
    }
    stopTimer()
    // 不再清除聊天和计时数据，只保存当前状态
    saveStateToStorage() // 只保存，不清除
    document.removeEventListener('mousedown', handleClickOutsideEmoji)
    document.removeEventListener('mousedown', handleClickOutsideSupervisorEmoji)
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
    })
    showEmojiPanel.value = false
}

const layoutRef = ref(null)
const selectedTutorId = ref('')
const tutorList = ref([])

const sendMessage = () => {
    if (!inputMessage.value.trim() || !SESSION_ID.value) return
    const msg = inputMessage.value
    // 先添加到本地消息列表
    messages.value.push({
        type: 'consultant',
        content: msg,
        isSelf: true
    })
    // 清空输入框
    inputMessage.value = ''
    // 滚动到底部
    scrollToBottom()
    // 保存状态
    saveStateToStorage()

    // 发送到服务器
    fetch(`${CHAT_BASE_URL}/api/messages/session/${SESSION_ID.value}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${TOKEN}`
        },
        body: JSON.stringify({
            content: msg,
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
            console.log('[中间栏发送消息]', msg)
        })
        .catch(error => {
            ElMessage.error('发送消息失败')
            // 发送失败时从消息列表中移除
            messages.value.pop()
        })
}

// 1. 添加文件选择ref
const fileInputRef = ref(null)

// 2. 文件选择后上传
const handleFileChange = async (e) => {
    const file = e.target.files[0]
    if (!file) return
    const formData = new FormData()
    formData.append('file', file)
    try {
        // 1. 上传文件
        const uploadRes = await axios.post(
            `${CHAT_BASE_URL}/api/files/upload`,
            formData,
            {
                headers: {
                    'Authorization': `Bearer ${TOKEN}`,
                    'Content-Type': 'multipart/form-data'
                }
            }
        )
        const data = uploadRes.data
        if (!data.id || !data.ossUrl) {
            ElMessage.error('上传返回数据格式错误')
            return
        }
        // 2. 组装文件消息内容
        const fileMsgContent = JSON.stringify({
            url: data.ossUrl,
            fileName: file.name,
            fileSize: formatFileSize(file.size)
        })
        // 3. 发送WebSocket消息
        await fetch(`${CHAT_BASE_URL}/api/messages/session/${SESSION_ID.value}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${TOKEN}`
            },
            body: JSON.stringify({
                content: fileMsgContent,
                type: 'FILE',
                fileId: data.id
            })
        })
        // 4. 本地显示
        messages.value.push({
            type: 'consultant',
            content: `[文件] ${file.name}`,
            fileUrl: data.ossUrl,
            fileType: getFileTypeFromUrl(data.ossUrl),
            isSelf: true
        })
        ElMessage.success('文件发送成功')
    } catch (e) {
        ElMessage.error('文件发送失败')
    }
}

// 判断是否为图片类型（兼容fileType和url后缀）
const isImage = (fileType, fileUrl) => {
    if (fileType && fileType.startsWith('image/')) return true
    if (fileUrl) {
        return /\.(png|jpe?g|gif|bmp|webp|svg)$/i.test(fileUrl)
    }
    return false
}

// 新增：判断是否为音频类型
const isAudio = (fileType, fileUrl) => {
    if (fileType && fileType.startsWith('audio/')) return true
    if (fileUrl) {
        return /\.(mp3|wav|ogg|aac)$/i.test(fileUrl)
    }
    return false
}

// 1. 添加右侧栏文件选择ref
const supervisorFileInputRef = ref(null)

// 2. 右侧栏文件选择后上传
const handleSupervisorFileChange = async (e) => {
    const file = e.target.files[0]
    if (!file) return
    const formData = new FormData()
    formData.append('file', file)
    try {
        // 1. 上传文件
        const uploadRes = await axios.post(
            `${CHAT_BASE_URL}/api/files/upload`,
            formData,
            {
                headers: {
                    'Authorization': `Bearer ${TOKEN}`,
                    'Content-Type': 'multipart/form-data'
                }
            }
        )
        const data = uploadRes.data
        if (!data.id || !data.ossUrl) {
            ElMessage.error('上传返回数据格式错误')
            return
        }
        // 2. 组装文件消息内容
        const fileMsgContent = JSON.stringify({
            url: data.ossUrl,
            fileName: file.name,
            fileSize: formatFileSize(file.size)
        })
        // 3. 发送WebSocket消息
        await fetch(`${CHAT_BASE_URL}/api/messages/session/${tutorSessionId.value}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${TOKEN}`
            },
            body: JSON.stringify({
                content: fileMsgContent,
                type: 'FILE',
                fileId: data.id
            })
        })
        // 4. 本地显示
        tutorMessages.value.push({
            type: 'consultant',
            content: `[文件] ${file.name}`,
            fileUrl: data.ossUrl,
            fileType: getFileTypeFromUrl(data.ossUrl),
            isSelf: true
        })
        ElMessage.success('文件发送成功')
    } catch (e) {
        ElMessage.error('文件发送失败')
    }
}

// 文件大小格式化
function formatFileSize(size) {
    if (size < 1024) return size + 'B'
    if (size < 1024 * 1024) return (size / 1024).toFixed(2) + 'KB'
    if (size < 1024 * 1024 * 1024) return (size / (1024 * 1024)).toFixed(2) + 'MB'
    return (size / (1024 * 1024 * 1024)).toFixed(2) + 'GB'
}

// 判断文件类型（图片/其他）
function getFileTypeFromUrl(url) {
    if (!url) return ''
    const ext = url.split('.').pop().toLowerCase()
    if (["png", "jpg", "jpeg", "gif", "bmp", "webp", "svg"].includes(ext)) return 'image/' + ext
    return ''
}

function isOssImageUrl(url) {
    return typeof url === 'string' && /\.(png|jpe?g|gif|bmp|webp|svg)$/i.test(url)
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