<template>
    <div class="consultation-detail-content">
        <el-skeleton v-if="loading" rows="8" animated style="padding: 32px 0;" />
        <template v-else>
            <h1 class="main-title">咨询记录</h1>
            <div class="consultation-time">{{ date }}</div>

            <!-- 咨询师评价 -->
            <div class="evaluation-section">
                <div class="evaluation-title">咨询师评价：</div>
                <div class="evaluation-content">{{ counselorComment || '无' }}</div>
            </div>

            <!-- 咨询师详情聊天记录 -->
            <div class="section-title">咨询师详情</div>
            <div class="chat-box">
                <div v-for="(msg, idx) in counselorMessages" :key="idx">
                    <div class="chat-row">
                        <div class="sender-id">{{ msg.senderId }}：</div>
                        <div class="message">
                            <div class="message-content">
                                <template v-if="isImageUrl(msg.content)">
                                    <el-image :src="msg.content" style="max-width:120px;max-height:120px;"
                                        :preview-src-list="[msg.content]" fit="contain" />
                                </template>
                                <template v-else-if="isFileObject(msg.content)">
                                    <a :href="getFileUrl(msg.content)" target="_blank" rel="noopener noreferrer"
                                        download>
                                        [收到文件，请点击下载]{{ getFileName(msg.content) }}
                                    </a>
                                </template>
                                <template v-else>
                                    {{ msg.content }}
                                </template>
                            </div>
                        </div>
                    </div>
                    <div class="separator" v-if="idx !== counselorMessages.length - 1"></div>
                </div>
                <div v-if="!counselorMessages || counselorMessages.length === 0" class="empty-msg">暂无聊天记录</div>
            </div>

            <!-- 督导评价和详情（有督导时才显示） -->
            <template v-if="hasTutor">
                <div class="evaluation-section">
                    <div class="evaluation-title">督导评价：</div>
                    <div class="evaluation-content">{{ tutorComment || '无' }}</div>
                </div>
                <div class="section-title">督导详情</div>
                <div class="chat-box">
                    <div v-for="(msg, idx) in tutorMessages" :key="idx">
                        <div class="chat-row">
                            <div class="sender-id">{{ msg.senderId }}：</div>
                            <div class="message">
                                <div class="message-content">
                                    <template v-if="isImageUrl(msg.content)">
                                        <el-image :src="msg.content" style="max-width:120px;max-height:120px;"
                                            :preview-src-list="[msg.content]" fit="contain" />
                                    </template>
                                    <template v-else-if="isFileObject(msg.content)">
                                        <a :href="getFileUrl(msg.content)" target="_blank" rel="noopener noreferrer"
                                            download>
                                            [收到文件，请点击下载]{{ getFileName(msg.content) }}
                                        </a>
                                    </template>
                                    <template v-else>
                                        {{ msg.content }}
                                    </template>
                                </div>
                            </div>
                        </div>
                        <div class="separator" v-if="idx !== tutorMessages.length - 1"></div>
                    </div>
                    <div v-if="!tutorMessages || tutorMessages.length === 0" class="empty-msg">暂无聊天记录</div>
                </div>
            </template>
        </template>
    </div>
</template>

<script setup>
const props = defineProps({
    counselorComment: String,
    counselorMessages: Array,
    tutorComment: String,
    tutorMessages: Array,
    hasTutor: Boolean,
    date: String,
    loading: Boolean
})

// 判断是否为OSS图片链接
function isImageUrl(content) {
    if (!content) return false
    if (typeof content === 'string') {
        // 检查是否是图片链接
        return /https?:\/\/.*(oss|aliyuncs)\..*\.(png|jpe?g|gif|webp|bmp|svg)(\?.*)?$/i.test(content)
    }
    return false
}

// 判断是否为文件对象
function isFileObject(content) {
    if (!content) return false

    // 如果是对象
    if (typeof content === 'object' && content.url && content.fileName) {
        return true
    }

    // 如果是字符串化的JSON对象
    if (typeof content === 'string') {
        try {
            const obj = JSON.parse(content)
            return obj && obj.url && obj.fileName
        } catch (e) {
            return false
        }
    }

    return false
}

// 获取文件对象的文件名
function getFileName(content) {
    if (typeof content === 'object') {
        return content.fileName
    }

    if (typeof content === 'string') {
        try {
            const obj = JSON.parse(content)
            return obj.fileName
        } catch (e) {
            return ''
        }
    }

    return ''
}

// 获取文件对象的URL
function getFileUrl(content) {
    if (typeof content === 'object') {
        return content.url
    }

    if (typeof content === 'string') {
        try {
            const obj = JSON.parse(content)
            return obj.url
        } catch (e) {
            return ''
        }
    }

    return ''
}
</script>

<style scoped>
.consultation-detail-content {
    background: #fff;
    border-radius: 12px;
    padding: 32px 40px;
    max-width: 900px;
    margin: 0 auto;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.main-title {
    font-size: 24px;
    font-weight: bold;
    color: #333;
    margin-bottom: 8px;
}

.consultation-time {
    font-size: 14px;
    color: #888;
    margin-bottom: 24px;
}

.evaluation-section {
    margin-bottom: 24px;
}

.evaluation-title {
    font-size: 16px;
    font-weight: bold;
    color: #333;
    margin-bottom: 8px;
}

.evaluation-content {
    font-size: 14px;
    color: #333;
    line-height: 1.5;
}

.section-title {
    font-size: 16px;
    font-weight: normal;
    color: #888;
    margin: 24px 0 16px 0;
}

.chat-box {
    background: #fff;
    border-radius: 8px;
    border: 1px solid #eee;
    overflow-y: auto;
    height: 180px;
    margin-bottom: 32px;
}

.chat-row {
    display: flex;
    padding: 12px 16px;
    align-items: flex-start;
}

.sender-id {
    min-width: 80px;
    color: #888;
    font-size: 13px;
    font-weight: bold;
    margin-right: 8px;
    flex-shrink: 0;
}

.message {
    flex: 1;
}

.message-content {
    font-size: 13px;
    line-height: 1.4;
    color: #333;
    word-break: break-all;
    white-space: pre-line;
}

.separator {
    height: 1px;
    background-color: #eee;
    margin: 0;
}

.empty-msg {
    color: #bbb;
    font-size: 13px;
    text-align: center;
    margin: 20px 0;
}
</style>