<template>
  <view class="container">
    <!-- 背景图 -->
    <image class="bg-image" src="/static/background.png" mode="aspectFill"></image>
    
    <!-- 顶部栏 -->
    <view class="header">
      <view class="left-section">
        <view class="back-btn" @click="handleBack">
          <image class="back-icon" src="/static/back.png" mode="aspectFit"></image>
        </view>
        <view class="consultant-info">
          <image class="consultant-avatar" :src="consultantAvatar" mode="aspectFit"></image>
          <view class="info-text">
            <text class="title">咨询师 {{consultantName}}</text>
            <view class="status">在线</view>
          </view>
        </view>
      </view>
      </view>
    
    <!-- 退出按钮 -->
    <view class="quit-btn" @click="handleQuit">
      <text>退出</text>
    </view>
    
    <!-- 内容区域 -->
    <scroll-view 
      class="content" 
      scroll-y="true"
      :scroll-top="scrollTop"
      @scroll="onScroll"
      :scroll-with-animation="true"
    >
      <view class="chat-messages">
        <view class="message" :class="message.type === 'consultant' ? 'consultant-message' : 'user-message'" 
              v-for="(message, index) in messages" :key="index">
          <template v-if="message.type === 'consultant'">
            <image class="avatar" :src="consultantAvatar" mode="aspectFit"></image>
            <view class="message-content">
              <image v-if="message.imageUrl" 
                     :src="message.imageUrl" 
                     mode="widthFix" 
                     class="message-image"
                     @tap="previewImage(message.imageUrl)"></image>
              <view v-else-if="message.fileInfo" class="file-message" @tap="openFile(message.fileInfo)">
                <image class="file-icon" :src="getFileIcon(message.fileInfo.fileName)" mode="aspectFit"></image>
                <view class="file-info">
                  <text class="file-name">{{message.fileInfo.fileName}}</text>
                  <text class="file-size">{{message.fileInfo.fileSize}}</text>
                </view>
              </view>
              <view v-else-if="message.voiceInfo" 
                    class="voice-message" 
                    :class="{ 'playing': currentPlayingMsg === message }"
                    @tap="playVoice(message)">
                <image class="voice-icon" src="/static/voice.png" mode="aspectFit"></image>
                <text class="duration">{{message.voiceInfo.duration}}″</text>
              </view>
              <text v-else>{{message.content}}</text>
            </view>
          </template>
          <template v-else>
            <view class="message-content">
              <image v-if="message.imageUrl" 
                     :src="message.imageUrl" 
                     mode="widthFix" 
                     class="message-image"
                     @tap="previewImage(message.imageUrl)"></image>
              <view v-else-if="message.fileInfo" class="file-message" @tap="openFile(message.fileInfo)">
                <image class="file-icon" :src="getFileIcon(message.fileInfo.fileName)" mode="aspectFit"></image>
                <view class="file-info">
                  <text class="file-name">{{message.fileInfo.fileName}}</text>
                  <text class="file-size">{{message.fileInfo.fileSize}}</text>
            </view>
        </view>
              <view v-else-if="message.voiceInfo" 
                    class="voice-message" 
                    :class="{ 'playing': currentPlayingMsg === message }"
                    @tap="playVoice(message)">
                <image class="voice-icon" src="/static/voice.png" mode="aspectFit"></image>
                <text class="duration">{{message.voiceInfo.duration}}″</text>
      </view>
              <text v-else>{{message.content}}</text>
    </view>
          </template>
        </view>
      </view>
    </scroll-view>

    <!-- 输入区域 -->
    <view class="input-area">
      <!-- 录音取消区域 - 移到输入框上方 -->
      <view class="record-cancel-area" v-if="isRecording" @tap="cancelRecording">
        <text>点击此处取消录音</text>
      </view>
      
      <view class="input-section">
        <image class="add-icon" src="/static/chat2.png" mode="aspectFit" @click="togglePopup"></image>
        <view class="voice-input" v-if="isRecording">
          <text class="recording-tip">正在录音 {{formatRecordTime(recordDuration)}}</text>
        </view>
        <input v-else class="text-input" type="text" placeholder="请输入内容..." v-model="inputText" @confirm="sendMessage"/>
        <image 
          class="voice-icon" 
          src="/static/voice.png"
          mode="aspectFit"
          @tap="toggleRecording"
        ></image>
        <view class="recording-status" v-if="isRecording"></view>
        <image class="emoji-icon" src="/static/chat3.png" mode="aspectFit" @click="toggleEmojiPanel"></image>
        <view class="send-btn" @click="sendMessage" v-if="inputText.trim()">发送</view>
      </view>
      
      <!-- 表情选择面板 -->
      <view class="emoji-panel" v-if="showEmojiPanel">
        <view class="emoji-grid">
          <view class="emoji-item" v-for="(emoji, index) in emojiList" :key="index" @click="selectEmoji(emoji)">
            {{emoji}}
          </view>
        </view>
      </view>
      
      <!-- 弹出按钮区域 -->
      <view class="popup-buttons" v-if="showPopup">
        <view class="popup-button" 
              v-for="(button, index) in popupButtonList" 
              :key="index"
              @click="handlePopupAction(button)">
          <text>{{button.text}}</text>
        </view>
      </view>
    </view>

    <!-- 评价弹窗 -->
    <view class="rating-popup" v-if="showRatingPopup">
      <view class="popup-mask" @click="cancelRating"></view>
      <view class="popup-content">
        <view class="popup-title">请您对本次咨询做出评价</view>
        
        <view class="rating-form">
          <view class="form-item">
            <text class="label">评分</text>
            <view class="stars">
              <view 
                class="star" 
                v-for="i in maxStars" 
                :key="i"
                @click="setRating(i)"
              >
                <view class="star-bg" :style="getStarStyle(i - 1, rating)"></view>
              </view>
            </view>
          </view>
          
          <view class="form-item">
            <text class="label">评价内容</text>
            <textarea 
              class="comment-input" 
              v-model="comment" 
              placeholder="请输入您的评价内容"
              maxlength="200"
            ></textarea>
          </view>
        </view>
        
        <view class="popup-buttons">
          <view class="btn cancel" @click="cancelRating">取消</view>
          <view class="btn confirm" @click="submitRating">确定</view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      inputText: '',
      showPopup: false,
      messages: [],
      socket: null,
      connected: false,
      sessionId: '',
      consultantName: '',
      consultantAvatar: '',
      consultantId: '',
      participants: {
        user: {
          id: '',
          name: '',
          avatar: ''
        },
        consultant: {
          id: '',
          name: '',
          avatar: ''
        }
      },
      apiURL: 'http://47.117.102.116:8081',
      popupButtonList: [
        { text: '发送图片', action: 'uploadImage' },
        { text: '发送文件', action: 'uploadFile' },
        { text: '发送咨询记录' },
        { text: '发送自测数据' }
      ],
      showRatingPopup: false,
      rating: 0,
      comment: '',
      activeColor: '#ffba00', // 激活的星星颜色
      inactiveColor: '#ddd', // 未激活的星星颜色
      maxStars: 5, // 最大星级数
      scrollTop: 0,
      showEmojiPanel: false,
      emojiList: ['😊','😄','😯','😢','😂','😅','😥','😪','😵',
                  '😳','😍','😠','😜','😝','😀','😗','😴','😷',
                  '😮','😃','😉','😆','😊','😢','😭','😤','😩',
                  '😫','😨','😰','😱','😳','😵','😡','😠'],
      isRecording: false, // 是否正在录音
      recordManager: null, // 录音管理器
      recordTimer: null, // 录音计时器
      recordDuration: 0, // 录音时长（秒）
      tempVoicePath: '', // 临时录音文件路径
      currentPlayingMsg: null, // 当前正在播放的消息
      isCancelled: false, // 添加取消状态标记
    }
  },
  async onLoad(options) {
    // 获取用户信息和token
    const userInfo = uni.getStorageSync('userInfo');
    const token = uni.getStorageSync('token');
    
    if (!userInfo || !userInfo.id || !token) {
      uni.showToast({
        title: '请先登录',
        icon: 'none'
      });
      setTimeout(() => {
        uni.navigateBack();
      }, 1500);
      return;
    }

    // 设置会话ID和咨询师信息
    if (options.sessionId) {
      this.sessionId = options.sessionId;
    
    // 获取咨询师信息
    if (options.name) {
        this.consultantName = decodeURIComponent(options.name);
    }
    if (options.avatar) {
        this.consultantAvatar = decodeURIComponent(options.avatar);
    }
    
      // 保存参与者信息
      this.participants = {
        user: {
          id: userInfo.id,
          name: userInfo.name,
          avatar: userInfo.avatar || '/static/default-avatar.png'
        },
        consultant: {
          id: decodeURIComponent(options.counselorId),
          name: decodeURIComponent(options.name),
          avatar: decodeURIComponent(options.avatar) || '/static/default-avatar.png'
        }
      };

      // 获取历史聊天记录
      try {
        const response = await uni.request({
          url: `${this.apiURL}/api/sessions/${this.sessionId}/records`,
          method: 'GET'
        });

        console.log('获取历史记录返回：', response);
    
        if (response.statusCode === 200) {
          // 处理历史消息
          const historyMessages = response.data.messages || [];
          this.messages = historyMessages.map(msg => {
            // 基础消息结构
            const message = {
              type: msg.senderId === this.participants.user.id ? 'user' : 'consultant'
            };

            // 根据消息类型处理
            if (msg.type === 'IMAGE') {
              message.imageUrl = msg.content;
            } else if (msg.type === 'FILE') {
              try {
                // 解析文件信息
                const fileInfo = JSON.parse(msg.content);
                message.fileInfo = {
                  url: fileInfo.url,
                  fileName: fileInfo.fileName || '未知文件',  // 如果没有文件名就显示"未知文件"
                  fileSize: fileInfo.fileSize || ''  // 如果没有文件大小就留空
                };
              } catch (error) {
                console.error('解析文件消息失败：', error);
                message.content = msg.content;
              }
            } else if (msg.type === 'VOICE') {
              try {
                const voiceInfo = JSON.parse(msg.content);
                message.voiceInfo = {
                  url: voiceInfo.url,
                  duration: voiceInfo.duration
                };
              } catch (error) {
                console.error('解析语音消息失败：', error);
                message.content = msg.content;
              }
            } else {
              message.content = msg.content;
            }

            return message;
          });

          this.$nextTick(() => {
            this.scrollToBottom();
          });
        }
      } catch (error) {
        console.error('获取历史记录失败：', error);
      }
    } else {
      uni.showToast({
        title: '会话创建失败',
        icon: 'none'
      });
      setTimeout(() => {
        uni.navigateBack();
      }, 1500);
      return;
    }

    // 连接WebSocket
    this.connectWebSocket();
  },
  onUnload() {
    this.disconnectWebSocket();
    if (this.recordTimer) {
      clearInterval(this.recordTimer);
    }
    if (this.recordManager) {
      this.recordManager.stop();
    }
  },
  methods: {
    handleBack() {
      uni.navigateBack()
    },
    handleQuit() {
      uni.showModal({
        title: '提示',
        content: '确定要结束本次咨询吗？',
        success: (res) => {
          if (res.confirm) {
            this.showRatingPopup = true;
          }
        }
      });
    },
    togglePopup() {
      this.showPopup = !this.showPopup;
    },
    toggleEmojiPanel() {
      this.showEmojiPanel = !this.showEmojiPanel;
      if(this.showEmojiPanel) {
        this.showPopup = false; // 如果表情面板打开，关闭其他弹出面板
      }
      // 滚动到底部，确保表情面板完全可见
      this.$nextTick(() => {
        this.scrollToBottom();
      });
    },
    connectWebSocket() {
      const token = uni.getStorageSync('token');
      if (!token) {
        uni.showToast({
          title: '请先登录',
          icon: 'none'
        });
        setTimeout(() => {
          uni.navigateBack();
        }, 1500);
        return;
      }

      // 如果已经有socket连接，先断开
      if (this.socket) {
        this.disconnectWebSocket();
      }

      console.log('开始创建WebSocket连接...');
      
      // 使用 wx API 创建 WebSocket 连接
      this.socket = wx.connectSocket({
        url: 'ws://47.117.102.116:8081/ws/websocket',
        header: {
          'Authorization': 'Bearer ' + token
        },
        success: () => {
          console.log('WebSocket连接创建成功');
        },
        fail: (error) => {
          console.error('WebSocket连接创建失败:', error);
          this.connected = false;
          // 连接失败后自动重试
          setTimeout(() => {
            if (!this.connected) {
              console.log('正在重试连接...');
              this.connectWebSocket();
            }
          }, 1000);
        }
      });

      wx.onSocketOpen(() => {
        console.log('WebSocket连接已打开');
        
        // 发送 STOMP CONNECT 帧
        const connectFrame = {
          command: 'CONNECT',
          headers: {
            'accept-version': '1.1,1.0',
            'heart-beat': '10000,10000',
            'Authorization': 'Bearer ' + token
          }
        };
        
        this.sendStompFrame(connectFrame);
      });

      wx.onSocketMessage((res) => {
        console.log('收到消息:', res);
        try {
          const frame = this.parseStompFrame(res.data);
          console.log('解析后的STOMP帧:', frame);
          
          switch (frame.command) {
            case 'CONNECTED':
              console.log('STOMP连接成功');
              this.connected = true;
              // 订阅消息
              this.subscribeToMessages();
              break;
              
            case 'MESSAGE':
              // 处理接收到的消息
              const messageContent = JSON.parse(frame.body);
              this.handleReceivedMessage(messageContent);
              break;
              
            case 'ERROR':
              console.error('STOMP错误:', frame);
              // STOMP错误时重试连接
              setTimeout(() => {
                if (!this.connected) {
                  this.connectWebSocket();
                }
              }, 1000);
              break;
              
            default:
              console.log('收到其他类型的STOMP帧:', frame);
          }
        } catch (error) {
          console.error('解析消息失败:', error);
        }
      });

      wx.onSocketError((error) => {
        console.error('WebSocket连接错误:', error);
        this.connected = false;
        uni.showToast({
          title: '连接错误，正在重试',
          icon: 'none'
        });
        // 连接错误时重试
        setTimeout(() => {
          if (!this.connected) {
            this.connectWebSocket();
          }
        }, 1000);
      });

      wx.onSocketClose(() => {
        console.log('WebSocket连接已关闭');
        this.connected = false;
        // 意外关闭时重试连接
        setTimeout(() => {
          if (!this.connected) {
            this.connectWebSocket();
          }
        }, 1000);
      });
    },
    
    // 发送 STOMP 帧
    sendStompFrame(frame) {
      const frameStr = this.buildStompFrame(frame);
      wx.sendSocketMessage({
        data: frameStr,
        success: () => {
          console.log('STOMP帧发送成功:', frame);
        },
        fail: (error) => {
          console.error('STOMP帧发送失败:', error);
        }
      });
    },
    
    // 构建 STOMP 帧
    buildStompFrame(frame) {
      let frameStr = frame.command + '\n';
      
      // 添加头部
      if (frame.headers) {
        for (const key in frame.headers) {
          frameStr += key + ':' + frame.headers[key] + '\n';
        }
      }
      
      frameStr += '\n'; // 头部结束
      
      // 添加消息体
      if (frame.body) {
        frameStr += frame.body;
      }
      
      frameStr += '\0'; // 帧结束符
      return frameStr;
    },
    
    // 解析 STOMP 帧
    parseStompFrame(data) {
      console.log('开始解析STOMP帧:', data);
      
      // 移除末尾的 null 字符
      data = data.replace(/\0$/, '');
      
      const lines = data.split('\n');
      console.log('分割后的行:', lines);
      
      const command = lines[0];
      const headers = {};
      let body = '';
      let i = 1;
      
      // 解析头部
      while (i < lines.length && lines[i] !== '') {
        const line = lines[i];
        const colonIndex = line.indexOf(':');
        if (colonIndex > 0) {
          const key = line.substring(0, colonIndex);
          const value = line.substring(colonIndex + 1);
          headers[key] = value;
        }
        i++;
      }
      
      // 解析消息体
      i++; // 跳过空行
      while (i < lines.length) {
        body += lines[i] + '\n';
        i++;
      }
      
      const frame = {
        command,
        headers,
        body: body.trim()
      };
      
      console.log('解析后的STOMP帧:', frame);
      return frame;
    },
    
    // 订阅消息
    subscribeToMessages() {
      console.log('开始订阅消息...');
      const subscribeFrame = {
        command: 'SUBSCRIBE',
        headers: {
          'id': 'sub-' + this.subscriptionId++,
          'destination': '/user/queue/messages'
        }
      };
      
      this.sendStompFrame(subscribeFrame);
    },
    
    disconnectWebSocket() {
      if (this.socket) {
        // 发送 DISCONNECT 帧
        const disconnectFrame = {
          command: 'DISCONNECT'
        };
        this.sendStompFrame(disconnectFrame);
        
        wx.closeSocket();
        this.socket = null;
      }
      this.connected = false;
      console.log("断开连接");
    },
    
    sendMessage() {
      console.log('当前连接状态:', this.connected);
      
      if (!this.connected) {
        uni.showToast({
          title: '连接中...',
          icon: 'loading',
          duration: 2000
        });
        
        // 重新连接
        this.connectWebSocket();
        
        // 等待连接建立后再发送消息
        setTimeout(() => {
          if (this.connected) {
            this.sendMessageContent();
          } else {
            uni.showToast({
              title: '连接失败，请重试',
          icon: 'none'
        });
          }
        }, 2000);
        return;
      }

      this.sendMessageContent();
    },
    
    // 抽取发送消息的具体逻辑为单独的方法
    sendMessageContent() {
      const content = this.inputText.trim();
      if (!content) return;

      const payload = {
        content: content,
        type: 'TEXT'
      };

      console.log('发送消息:', payload);
      
      try {
        // 先添加到消息列表
        const newMessage = {
          type: 'user',
          content: content
        };
        this.messages.push(newMessage);
        
        // 发送消息
        const sendFrame = {
          command: 'SEND',
          headers: {
            'destination': '/app/chat/' + this.sessionId
          },
          body: JSON.stringify(payload)
        };
        
        this.sendStompFrame(sendFrame);
        console.log('消息发送成功');
        this.inputText = '';
        this.scrollToBottom();
      } catch (error) {
        console.error('发送消息异常:', error);
        uni.showToast({
          title: '发送异常',
          icon: 'none'
        });
      }
    },
    
    // 修改接收消息的处理
    handleReceivedMessage(messageContent) {
      let newMessage = {
        type: 'consultant'
      };

      if (messageContent.type === 'FILE') {
        try {
          const fileInfo = JSON.parse(messageContent.content);
          newMessage.fileInfo = {
            url: fileInfo.url,
            fileName: fileInfo.fileName,
            fileSize: fileInfo.fileSize
          };
        } catch (error) {
          console.error('解析文件消息失败：', error);
          newMessage.content = messageContent.content;
        }
      } else if (messageContent.type === 'IMAGE') {
        newMessage.imageUrl = messageContent.content;
      } else if (messageContent.type === 'VOICE') {
        try {
          const voiceInfo = JSON.parse(messageContent.content);
          newMessage.voiceInfo = {
            url: voiceInfo.url,
            duration: voiceInfo.duration
          };
        } catch (error) {
          console.error('解析语音消息失败：', error);
          newMessage.content = messageContent.content;
        }
      } else {
        newMessage.content = messageContent.content;
      }

      this.messages.push(newMessage);
      this.scrollToBottom();
    },
    
    scrollToBottom() {
      this.$nextTick(() => {
        const query = uni.createSelectorQuery().in(this);
        query.select('.chat-messages').boundingClientRect(data => {
          if (data) {
            this.scrollTop = data.height;
          }
        }).exec();
      });
    },
    cancelRating() {
      // 直接关闭评价弹窗
      this.showRatingPopup = false;
    },
    setRating(index) {
      this.rating = index;
    },
    async submitRating() {
      if (this.rating === 0) {
        uni.showToast({
          title: '请选择评分',
          icon: 'none'
        });
        return;
      }
      
      if (!this.comment.trim()) {
        uni.showToast({
          title: '请填写评价内容',
          icon: 'none'
        });
        return;
      }

      try {
        const response = await uni.request({
          url: `${this.apiURL}/api/sessions/${this.sessionId}/end`,
          method: 'POST',
          data: {
            comment: this.comment,
            rating: this.rating
          }
        });

        console.log('结束会话响应:', response);

        if (response.statusCode === 200) {
          uni.showToast({
            title: '评价成功',
            icon: 'success'
          });
          // 断开WebSocket连接
          this.disconnectWebSocket();
          // 返回上一页
          setTimeout(() => {
            uni.navigateBack();
          }, 1500);
        } else {
          uni.showToast({
            title: '评价失败',
            icon: 'none'
          });
        }
      } catch (error) {
        console.error('提交评价失败:', error);
        uni.showToast({
          title: '评价失败，请重试',
          icon: 'none'
        });
      }
      this.showRatingPopup = false;
    },
    // 获取星星的样式
    getStarStyle(index, rating) {
      const percent = Math.min(Math.max(rating - index, 0), 1) * 100;
      return {
        'background': `linear-gradient(90deg, ${this.activeColor} ${percent}%, ${this.inactiveColor} ${percent}%)`
      };
    },
    // 获取星星的类名
    getStarClass(index, rating) {
      return {
        'star-full': rating >= index + 1,
        'star-half': rating > index && rating < index + 1,
        'star-empty': rating <= index
      };
    },
    onScroll(event) {
      this.scrollTop = event.detail.scrollTop;
    },
    selectEmoji(emoji) {
      this.inputText += emoji;
      // 可以选择是否在选择表情后关闭面板
      // this.showEmojiPanel = false; // 如果需要选择后自动关闭面板，取消注释这行
    },
    // 处理弹出按钮点击
    handlePopupAction(button) {
      switch(button.action) {
        case 'uploadImage':
          this.chooseAndUploadImage();
          break;
        case 'uploadFile':
          this.chooseAndUploadFile();
          break;
        default:
          console.log('未处理的操作:', button.text);
      }
      this.showPopup = false;
    },
    // 选择并上传图片
    async chooseAndUploadImage() {
      if (!this.connected) {
        uni.showToast({
          title: '正在连接中...',
          icon: 'loading',
          duration: 2000
        });
        
        // 重新连接
        this.connectWebSocket();
        
        // 等待连接建立后再发送
        setTimeout(() => {
          if (this.connected) {
            this.doChooseAndUploadImage();
          } else {
            uni.showToast({
              title: '连接失败，请重试',
              icon: 'none'
            });
          }
        }, 2000);
        return;
      }
      
      this.doChooseAndUploadImage();
    },
    
    // 实际的图片选择和上传逻辑
    async doChooseAndUploadImage() {
      try {
        const chooseRes = await uni.chooseImage({
          count: 1,
          sizeType: ['compressed'],
          sourceType: ['album', 'camera']
        });
        
        if (chooseRes.tempFilePaths && chooseRes.tempFilePaths.length > 0) {
          uni.showLoading({
            title: '上传中...'
          });
          
          const uploadRes = await uni.uploadFile({
            url: `${this.apiURL}/api/files/upload`,
            filePath: chooseRes.tempFilePaths[0],
            name: 'file',
            header: {
              'token': uni.getStorageSync('token')
            }
          });
          
          console.log('上传图片返回：', uploadRes);
          
          if (uploadRes.statusCode === 200) {
            let responseData;
            try {
              responseData = JSON.parse(uploadRes.data);
              console.log('解析后的响应数据：', responseData);
              
              if (responseData.id && responseData.ossUrl) {
                const imageMessage = {
                  content: responseData.ossUrl,
                  type: 'IMAGE'
                };
                
                // 添加到消息列表
                const newMessage = {
                  type: 'user',
                  content: '[图片]',
                  imageUrl: responseData.ossUrl
                };
                this.messages.push(newMessage);
                
                // 发送到WebSocket
                const sendFrame = {
                  command: 'SEND',
                  headers: {
                    'destination': '/app/chat/' + this.sessionId
                  },
                  body: JSON.stringify(imageMessage)
                };
                
                this.sendStompFrame(sendFrame);
                this.scrollToBottom();
                
                uni.hideLoading();
                uni.showToast({
                  title: '发送成功',
                  icon: 'success'
                });
              } else {
                throw new Error('上传返回数据格式错误');
              }
            } catch (parseError) {
              console.error('解析上传响应数据失败：', parseError);
              throw new Error('上传图片失败');
            }
          } else {
            throw new Error('上传图片失败');
          }
        }
      } catch (error) {
        console.error('图片上传或发送失败：', error);
        uni.hideLoading();
        uni.showToast({
          title: error.message || '操作失败',
          icon: 'none'
        });
      }
    },
    
    // 选择并上传文件
    async chooseAndUploadFile() {
      if (!this.connected) {
        uni.showToast({
          title: '正在连接中...',
          icon: 'loading',
          duration: 2000
        });
        
        // 重新连接
        this.connectWebSocket();
        
        // 等待连接建立后再发送
        setTimeout(() => {
          if (this.connected) {
            this.doChooseAndUploadFile();
          } else {
            uni.showToast({
              title: '连接失败，请重试',
              icon: 'none'
            });
          }
        }, 2000);
        return;
      }
      
      this.doChooseAndUploadFile();
    },
    
    // 实际的文件选择和上传逻辑
    async doChooseAndUploadFile() {
      try {
        let chooseResult;
        
        // #ifdef APP-PLUS
        chooseResult = await uni.chooseFile({
          count: 1,
          extension: ['.pdf', '.doc', '.docx', '.txt', '.xls', '.xlsx'],
        });
        // #endif
        
        // #ifdef H5
        chooseResult = await uni.chooseFile({
          count: 1,
          type: 'all'
        });
        // #endif
        
        // #ifdef MP-WEIXIN
        chooseResult = await uni.chooseMessageFile({
          count: 1,
          type: 'file',
          extension: ['pdf', 'doc', 'docx', 'txt', 'xls', 'xlsx']
        });
        // #endif
        
        if (chooseResult.tempFiles && chooseResult.tempFiles.length > 0) {
          const file = chooseResult.tempFiles[0];
          
          // 检查文件大小（限制为20MB）
          if (file.size > 20 * 1024 * 1024) {
            uni.showToast({
              title: '文件大小不能超过20MB',
              icon: 'none'
            });
            return;
          }
          
          uni.showLoading({
            title: '上传中...'
          });
          
          const uploadRes = await uni.uploadFile({
            url: `${this.apiURL}/api/files/upload`,
            filePath: file.path,
            name: 'file',
            header: {
              'token': uni.getStorageSync('token')
            }
          });
          
          console.log('上传文件返回：', uploadRes);
          
          if (uploadRes.statusCode === 200) {
            let responseData;
            try {
              responseData = JSON.parse(uploadRes.data);
              console.log('解析后的响应数据：', responseData);
              
              if (responseData.id && responseData.ossUrl) {
                // 构建文件消息
                const fileMessage = {
                  content: JSON.stringify({
                    url: responseData.ossUrl,
                    fileName: file.name,
                    fileSize: this.formatFileSize(file.size)
                  }),
                  type: 'FILE'
                };
                
                // 添加到消息列表
                const newMessage = {
                  type: 'user',
                  fileInfo: {
                    url: responseData.ossUrl,
                    fileName: file.name,
                    fileSize: this.formatFileSize(file.size)
                  }
                };
                this.messages.push(newMessage);
                
                // 发送到WebSocket
                const sendFrame = {
                  command: 'SEND',
                  headers: {
                    'destination': '/app/chat/' + this.sessionId
                  },
                  body: JSON.stringify(fileMessage)
                };
                
                this.sendStompFrame(sendFrame);
                this.scrollToBottom();
                
                uni.hideLoading();
                uni.showToast({
                  title: '发送成功',
                  icon: 'success'
                });
              } else {
                throw new Error('上传返回数据格式错误');
              }
            } catch (parseError) {
              console.error('解析上传响应数据失败：', parseError);
              throw new Error('上传文件失败');
            }
          } else {
            throw new Error('上传文件失败');
          }
        }
      } catch (error) {
        console.error('文件上传或发送失败：', error);
        uni.hideLoading();
        uni.showToast({
          title: error.message || '操作失败',
          icon: 'none'
        });
      }
    },
    // 格式化文件大小
    formatFileSize(size) {
      if (size < 1024) {
        return size + 'B';
      } else if (size < 1024 * 1024) {
        return (size / 1024).toFixed(2) + 'KB';
      } else if (size < 1024 * 1024 * 1024) {
        return (size / (1024 * 1024)).toFixed(2) + 'MB';
      } else {
        return (size / (1024 * 1024 * 1024)).toFixed(2) + 'GB';
      }
    },
    previewImage(imageUrl) {
      uni.previewImage({
        current: imageUrl,
        urls: [imageUrl]
      });
    },
    // 打开文件
    openFile(fileInfo) {
      // #ifdef H5
      window.open(fileInfo.url);
      // #endif
      
      // #ifdef MP-WEIXIN
      uni.downloadFile({
        url: fileInfo.url,
        success: (res) => {
          if (res.statusCode === 200) {
            uni.openDocument({
              filePath: res.tempFilePath,
              success: () => {
                console.log('打开文件成功');
              },
              fail: (error) => {
                console.error('打开文件失败：', error);
                uni.showToast({
                  title: '打开文件失败',
                  icon: 'none'
                });
              }
            });
          }
        },
        fail: () => {
          uni.showToast({
            title: '下载文件失败',
            icon: 'none'
          });
        }
      });
      // #endif
      
      // #ifdef APP-PLUS
      uni.downloadFile({
        url: fileInfo.url,
        success: (res) => {
          if (res.statusCode === 200) {
            uni.openDocument({
              filePath: res.tempFilePath,
              success: () => {
                console.log('打开文件成功');
              },
              fail: (error) => {
                console.error('打开文件失败：', error);
                uni.showToast({
                  title: '打开文件失败',
                  icon: 'none'
                });
              }
            });
          }
        },
        fail: () => {
          uni.showToast({
            title: '下载文件失败',
            icon: 'none'
          });
        }
      });
      // #endif
    },
    // 获取文件图标
    getFileIcon(fileName) {
      const extension = fileName.toLowerCase().split('.').pop();
      switch (extension) {
        case 'txt':
          return '/static/text.png';
        case 'pdf':
          return '/static/pdf.png';
        case 'doc':
        case 'docx':
          return '/static/word.png';
        case 'csv':
          return '/static/csv.png';
        default:
          return '/static/file.png'; // 默认文件图标
      }
    },
    // 切换录音状态
    toggleRecording() {
      console.log('切换录音状态，当前状态:', this.isRecording);
      if (this.isRecording) {
        this.stopRecording();
      } else {
        this.startRecording();
      }
    },
    startRecording() {
      // #ifdef MP-WEIXIN
      wx.getSetting({
        success: (res) => {
          if (!res.authSetting['scope.record']) {
            uni.authorize({
              scope: 'scope.record',
              success: () => {
                this.startRecord();
              },
              fail: () => {
                uni.showModal({
                  title: '提示',
                  content: '请授予录音权限',
                  success: (res) => {
                    if (res.confirm) {
                      uni.openSetting();
                    }
                  }
                });
              }
            });
          } else {
            this.startRecord();
          }
        }
      });
      // #endif
    },
    startRecord() {
      if (!this.recordManager) {
        console.error('录音管理器未初始化');
        uni.showToast({
          title: '录音功能不可用',
          icon: 'none'
        });
        return;
      }
      console.log('开始录音');
      this.isRecording = true;
      this.isCancelled = false;
      this.recordDuration = 0;
      this.recordManager.start({
        duration: 60000,
        format: 'mp3'
      });

      this.recordTimer = setInterval(() => {
        this.recordDuration += 1;
      }, 1000);

      uni.showToast({
        title: '开始录音',
        icon: 'none',
        duration: 1000
      });
    },
    stopRecording() {
      console.log('停止录音');
      if (!this.isRecording) {
        console.log('当前未在录音状态');
        return;
      }
      // #ifdef MP-WEIXIN
      if (this.recordManager) {
        console.log('调用录音停止');
        this.isRecording = false;
        this.isCancelled = false;
        this.recordManager.stop();
        clearInterval(this.recordTimer);
        uni.showToast({
          title: '录音完成',
          icon: 'none',
          duration: 1000
        });
      } else {
        console.error('录音管理器未初始化');
      }
      // #endif
    },
    cancelRecording() {
      console.log('取消录音');
      if (!this.isRecording) return;
      // #ifdef MP-WEIXIN
      this.isRecording = false;
      this.isCancelled = true;
      if (this.recordManager) {
        this.recordManager.stop();
      }
      this.recordDuration = 0;
      clearInterval(this.recordTimer);
      uni.showToast({
        title: '已取消录音',
        icon: 'none'
      });
      // #endif
    },
    async uploadVoice() {
      if (!this.tempVoicePath) {
        console.error('录音文件路径为空');
        return;
      }

      try {
        uni.showLoading({
          title: '上传中...'
        });

        const uploadRes = await uni.uploadFile({
          url: `${this.apiURL}/api/files/upload`,
          filePath: this.tempVoicePath,
          name: 'file',
          header: {
            'token': uni.getStorageSync('token')
          }
        });

        console.log('上传语音返回：', uploadRes);

        if (uploadRes.statusCode === 200) {
          const responseData = JSON.parse(uploadRes.data);
          if (responseData.id && responseData.ossUrl) {
            const voiceMessage = {
              content: JSON.stringify({
                url: responseData.ossUrl,
                duration: this.recordDuration
              }),
              type: 'VOICE'
            };

            const newMessage = {
              type: 'user',
              voiceInfo: {
                url: responseData.ossUrl,
                duration: this.recordDuration
              }
            };
            this.messages.push(newMessage);

            const sendFrame = {
              command: 'SEND',
              headers: {
                'destination': '/app/chat/' + this.sessionId
              },
              body: JSON.stringify(voiceMessage)
            };

            this.sendStompFrame(sendFrame);
            this.scrollToBottom();

            uni.hideLoading();
            uni.showToast({
              title: '语音发送成功',
              icon: 'success'
            });
          } else {
            throw new Error('上传返回数据格式错误');
          }
        } else {
          throw new Error('上传语音失败');
        }
      } catch (error) {
        console.error('语音上传失败：', error);
        uni.hideLoading();
        uni.showToast({
          title: '语音发送失败',
          icon: 'none'
        });
      }
    },
    formatRecordTime(duration) {
      const minutes = Math.floor(duration / 60);
      const seconds = duration % 60;
      return `${minutes}:${seconds < 10 ? '0' : ''}${seconds}`;
    },
    // 播放语音
    playVoice(message) {
      if (this.currentPlayingMsg === message) {
        // 如果点击的是当前正在播放的消息，则停止播放
        uni.stopBackgroundAudio();
        this.currentPlayingMsg = null;
        return;
      }
      
      // 停止当前正在播放的语音
      if (this.currentPlayingMsg) {
        uni.stopBackgroundAudio();
      }
      
      this.currentPlayingMsg = message;
      
      uni.playBackgroundAudio({
        dataUrl: message.voiceInfo.url,
        success: () => {
          console.log('开始播放语音');
        },
        fail: (error) => {
          console.error('播放语音失败：', error);
          uni.showToast({
            title: '播放失败',
            icon: 'none'
          });
          this.currentPlayingMsg = null;
        }
      });
      
      // 监听播放结束
      uni.onBackgroundAudioStop(() => {
        this.currentPlayingMsg = null;
      });
    },
    initRecordManager() {
      // #ifdef MP-WEIXIN
      this.recordManager = wx.getRecorderManager();
      if (!this.recordManager) {
        console.error('无法获取录音管理器');
        uni.showToast({
          title: '录音功能不可用',
          icon: 'none'
        });
        return;
      }

      this.recordManager.onStop((res) => {
        console.log('录音停止，临时文件路径:', res.tempFilePath);
        if (this.isCancelled) {
          console.log('录音已取消');
          return;
        }
        this.tempVoicePath = res.tempFilePath;
        this.recordDuration = Math.floor(res.duration / 1000);
        this.uploadVoice();
      });

      this.recordManager.onError((err) => {
        console.error('录音错误:', err);
        this.isRecording = false;
        this.recordDuration = 0;
        uni.showToast({
          title: '录音失败',
          icon: 'none'
        });
      });
      // #endif
    },
  },
  created() {
    this.initRecordManager();
  }
}
</script>

<style lang="scss">
page {
  height: 100%;
  background-color: #f5f5f5;
}

.container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  position: relative;
  z-index: 1;
}

.bg-image {
  position: fixed;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  z-index: -1;
}

.header {
  flex-shrink: 0;
  padding: 80rpx 30rpx 20rpx;
  background-color: #A87B7B;
  position: relative;
  z-index: 2;
  display: flex;
  justify-content: space-between;
  align-items: center;

  .left-section {
    display: flex;
    align-items: center;

    .back-btn {
      width: 60rpx;
      height: 60rpx;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 20rpx;

      .back-icon {
        width: 40rpx;
        height: 40rpx;
      }
    }

    .consultant-info {
      display: flex;
      align-items: center;

      .consultant-avatar {
        width: 80rpx;
        height: 80rpx;
        border-radius: 40rpx;
        margin-right: 20rpx;
      }

      .info-text {
        display: flex;
        flex-direction: column;

        .title {
          color: #fff;
          font-size: 32rpx;
          margin-bottom: 8rpx;
        }

        .status {
          width: 70rpx;
          height: 40rpx;
          background-color: #8CD790;
          color: #fff;
          font-size: 24rpx;
          border-radius: 6rpx;
          display: flex;
          align-items: center;
          justify-content: center;
        }
      }
    }
  }
}

.content {
  flex: 1;
  position: relative;
  background-color: #FDF6F1;
  overflow: hidden;
  
  .chat-messages {
    height: 100%;
    overflow-y: auto;
  padding: 20rpx;
  box-sizing: border-box;
  -webkit-overflow-scrolling: touch;
  }
}

.input-area {
  flex-shrink: 0;
  position: relative;
  background-color: #fff;
  border-top: 1rpx solid #eee;
  z-index: 2;
  padding-bottom: env(safe-area-inset-bottom);
}

.input-section {
  height: 100rpx;
  display: flex;
  align-items: center;
  padding: 0 20rpx;
  box-shadow: 0 -2rpx 5rpx rgba(0,0,0,0.1);
  background-color: #fff;
  position: relative;
  z-index: 2;

  .add-icon {
    width: 50rpx;
    height: 50rpx;
    margin-right: 10rpx;
  }

  .voice-icon {
    width: 50rpx;
    height: 50rpx;
    margin: 0 10rpx;
    padding: 10rpx;
    position: relative;
  }

  .recording-status {
    position: absolute;
    width: 12rpx;
    height: 12rpx;
    background-color: #f56c6c;
    border-radius: 50%;
    right: 75rpx;
    top: 30rpx;
    animation: pulse 1s infinite;
  }

  .voice-input {
    flex: 1;
    height: 70rpx;
    background-color: #f5f5f5;
    border-radius: 35rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    
    .recording-tip {
      font-size: 28rpx;
      color: #333;
    }
  }

  .text-input {
    flex: 1;
    height: 70rpx;
    padding: 0 20rpx;
    border: 1rpx solid #ddd;
    border-radius: 35rpx;
    font-size: 28rpx;
    background-color: #f5f5f5;
  }

  .emoji-icon {
    width: 50rpx;
    height: 50rpx;
    margin-left: 10rpx;
  }
}

.popup-buttons {
  position: relative;
  display: flex;
  justify-content: space-around;
  padding: 20rpx;
  background-color: #fff;
  border-top: 1rpx solid #eee;
}

.chat-messages {
  padding-bottom: 100rpx;
  
  .message {
    display: flex;
    margin-bottom: 30rpx;
    padding: 10rpx;
    
    &.user-message {
      flex-direction: row-reverse;
      
      .message-content {
        background-color: #fff;
        color: #333;
        margin-left: 0;
        margin-right: 20rpx;
      }
    }
    
    &.consultant-message {
      .message-content {
        background-color: #A87B7B;
        color: #fff;
        margin-left: 20rpx;
        margin-right: 0;
      }
    }
    
    .avatar {
      width: 80rpx;
      height: 80rpx;
      border-radius: 40rpx;
      flex-shrink: 0;
    }
    
    .message-content {
      max-width: 70%;
      padding: 20rpx 30rpx;
      border-radius: 30rpx;
      font-size: 28rpx;
      box-shadow: 0 2rpx 4rpx rgba(0,0,0,0.1);
      word-break: break-all;
    }
  }
}

.rating-popup {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 999;
  display: flex;
  justify-content: center;
  align-items: center;

  .popup-mask {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
  }

  .popup-content {
    position: relative;
    background-color: #fff;
    width: 600rpx;
    border-radius: 20rpx;
    padding: 40rpx;
    z-index: 1000;

    .popup-title {
      font-size: 32rpx;
      font-weight: bold;
      color: #333;
      text-align: center;
      margin-bottom: 40rpx;
    }

    .rating-form {
      .form-item {
        margin-bottom: 30rpx;

        .label {
          font-size: 28rpx;
          color: #666;
          margin-bottom: 20rpx;
          display: block;
        }

        .stars {
          display: flex;
          align-items: center;
          
          .star {
            width: 32rpx;
            height: 32rpx;
            margin-right: 8rpx;
            position: relative;
            cursor: pointer;
            
            .star-bg {
              width: 100%;
              height: 100%;
              -webkit-mask: url("data:image/svg+xml,%3Csvg t='1683902132324' class='icon' viewBox='0 0 1024 1024' version='1.1' xmlns='http://www.w3.org/2000/svg' p-id='2473'%3E%3Cpath d='M512 837.12L190.464 1024l61.44-372.736L0 384l370.688-56.32L512 0l141.312 327.68L1024 384 772.096 651.264 833.536 1024z' fill='%23333333' p-id='2474'%3E%3C/path%3E%3C/svg%3E") no-repeat;
              mask: url("data:image/svg+xml,%3Csvg t='1683902132324' class='icon' viewBox='0 0 1024 1024' version='1.1' xmlns='http://www.w3.org/2000/svg' p-id='2473'%3E%3Cpath d='M512 837.12L190.464 1024l61.44-372.736L0 384l370.688-56.32L512 0l141.312 327.68L1024 384 772.096 651.264 833.536 1024z' fill='%23333333' p-id='2474'%3E%3C/path%3E%3C/svg%3E") no-repeat;
              -webkit-mask-size: cover;
              mask-size: cover;
            }
            
            &:last-child {
              margin-right: 0;
            }
          }
        }

        .comment-input {
          width: 100%;
          height: 160rpx;
          padding: 20rpx;
          box-sizing: border-box;
          border: 1rpx solid #ddd;
          border-radius: 10rpx;
          font-size: 28rpx;
        }
      }
    }

    .popup-buttons {
      display: flex;
      justify-content: space-between;
      margin-top: 40rpx;

      .btn {
        width: 240rpx;
        height: 80rpx;
        border-radius: 40rpx;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 28rpx;

        &.cancel {
          background-color: #f5f5f5;
          color: #666;
        }

        &.confirm {
          background-color: #A87B7B;
          color: #fff;
        }
      }
    }
  }
}

.emoji-panel {
  position: relative;
  background-color: #fff;
  padding: 20rpx;
  border-top: 1rpx solid #eee;
  box-shadow: 0 -2rpx 10rpx rgba(0,0,0,0.1);
  max-height: 400rpx;
}

.emoji-grid {
  display: grid;
  grid-template-columns: repeat(8, 1fr);
  gap: 16rpx;
  padding: 16rpx;
}

.emoji-item {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40rpx;
  height: 80rpx;
  background-color: #f8f8f8;
  border-radius: 8rpx;
  transition: all 0.2s;
  
  &:active {
    background-color: #eee;
    transform: scale(0.95);
  }
}

.message-image {
  max-width: 400rpx;
  border-radius: 12rpx;
  margin: 10rpx 0;
}

/* 添加退出按钮样式 */
.quit-btn {
  position: absolute;
  right: 30rpx;
  top: 200rpx;
  width: 100rpx;
  height: 100rpx;
  background-color: rgb(255,154,74);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4rpx 8rpx rgba(0,0,0,0.2);
  z-index: 100;
  
  text {
    color: #fff;
    font-size: 28rpx;
  }
  
  &:active {
    transform: scale(0.95);
  }
}

.file-message {
  display: flex;
  align-items: center;
  background-color: #fff;
  padding: 20rpx;
  border-radius: 12rpx;
  max-width: 400rpx;
  box-shadow: 0 2rpx 4rpx rgba(0,0,0,0.1);
  
  .file-icon {
    width: 48rpx;
    height: 48rpx;
    margin-right: 20rpx;
  }
  
  .file-info {
    flex: 1;
    overflow: hidden;
    
    .file-name {
      font-size: 28rpx;
      color: #333;
      margin-bottom: 8rpx;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      display: block;
    }
    
    .file-size {
      font-size: 24rpx;
      color: #999;
    }
  }
  
  &:active {
    opacity: 0.8;
  }
}

.record-cancel-area {
  position: absolute;
  bottom: 100%;
  left: 50%;
  transform: translateX(-50%);
  background-color: rgba(0,0,0,0.6);
  padding: 8rpx 24rpx;
  border-radius: 8rpx;
  margin-bottom: 10rpx;
  
  text {
    color: #fff;
    font-size: 24rpx;
    white-space: nowrap;
  }
  
  &:active {
    background-color: rgba(0,0,0,0.8);
  }
}

.voice-message {
  display: flex;
  align-items: center;
  background-color: #fff;
  padding: 20rpx 30rpx;
  border-radius: 12rpx;
  max-width: 400rpx;
  
  .voice-icon {
    width: 40rpx;
    height: 40rpx;
    margin-right: 20rpx;
  }
  
  .duration {
    font-size: 28rpx;
    color: #666;
  }
  
  &.playing {
    .voice-icon {
      animation: voiceWave 1s infinite;
    }
  }
}

@keyframes voiceWave {
  0% { opacity: 0.3; }
  50% { opacity: 1; }
  100% { opacity: 0.3; }
}

@keyframes pulse {
  0% {
    transform: scale(0.8);
    opacity: 0.5;
  }
  50% {
    transform: scale(1.2);
    opacity: 1;
  }
  100% {
    transform: scale(0.8);
    opacity: 0.5;
  }
}
</style>
