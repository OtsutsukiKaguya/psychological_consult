# 创建聊天页面
<template>
    <ConsultantBaseLayout>
        <div class="chat-layout">
            <!-- 左侧栏：咨询信息 -->
            <div class="left-panel">
                <div class="consult-info">
                    <img src="@/assets/avatar.png" alt="头像" class="avatar" />
                    <div class="info-text">
                        <h2>张先生</h2>
                        <p>12788903028</p>
                    </div>
                </div>
                <hr class="divider" />
                <div class="consult-status">
                    <p>正在咨询中</p>
                    <p>已咨询时间</p>
                    <h3>00:12:11</h3>
                </div>
                <div class="actions">
                    <button>请求督导</button>
                    <button>结束咨询</button>
                </div>
            </div>

            <!-- 中间栏：咨询师与用户聊天 -->
            <div class="center-panel">
                <div class="chat-box">
                    <!-- Consultant Message -->
                    <div class="message consultant">
                        <img src="@/assets/avatar.png" alt="咨询师头像" class="avatar" />
                        <div class="bubble consultant-bubble">
                            您好，请问您要咨询什么
                        </div>
                    </div>
                    <!-- User Message -->
                    <div class="message user">
                        <div class="bubble user-bubble">
                            您好，我是xxx，我想问
                        </div>
                        <img src="@/assets/avatar.png" alt="用户头像" class="avatar" />
                    </div>
                </div>
                <div class="input-area">
                    <div class="toolbar">
                        <img src="@/assets/chat/icon_microphone.png" alt="Mic" />
                        <img src="@/assets/chat/icon-photo.png" alt="Image" />
                        <img src="@/assets/chat/icon-emoji.png" alt="Emoji" />
                        <img src="@/assets/chat/Phone.png" alt="Phone" />
                    </div>
                    <textarea placeholder="输入消息..."></textarea>
                    <button class="send-button">发送</button>
                </div>
            </div>

            <!-- 右侧栏：咨询师与督导聊天 -->
            <div class="right-panel">
                <div class="supervisor-header">
                    <img src="@/assets/avatar.png" alt="督导头像" class="avatar" />
                    <h3>督导</h3>
                </div>
                <div class="chat-box supervisor-chat-box">
                    <!-- Supervisor Message -->
                    <div class="message supervisor">
                        <img src="@/assets/avatar.png" alt="督导头像" class="avatar" />
                        <div class="bubble supervisor-bubble">
                            您好，请问您要咨询什么
                        </div>
                    </div>
                    <!-- Consultant Message -->
                    <div class="message consultant">
                        <div class="bubble consultant-bubble-right">
                            您好，我是xxx，我想问
                        </div>
                        <img src="@/assets/avatar.png" alt="咨询师头像" class="avatar" />
                    </div>
                </div>
                <div class="input-area supervisor-input-area">
                    <div class="toolbar">
                        <img src="@/assets/chat/icon_microphone.png" alt="Mic" />
                        <img src="@/assets/chat/icon-photo.png" alt="Image" />
                        <img src="@/assets/chat/icon-emoji.png" alt="Emoji" />
                    </div>
                    <textarea placeholder="输入消息..."></textarea>
                    <button class="send-button">发送</button>
                </div>
            </div>
        </div>
    </ConsultantBaseLayout>
</template>

<script setup>
import ConsultantBaseLayout from '@/components/layout/ConsultantBaseLayout.vue'
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
    align-self: flex-start;
}

.message.user {
    align-self: flex-end;
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
    background-color: #fff;
    border: 1px solid #e0e0e0;
    color: #333;
}

.user-bubble {
    background-color: #19c490;
    color: #fff;
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