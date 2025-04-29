//package com.counseling.platform.controllers;
package com.example.demo.controller;

import com.example.demo.models.ChatMessage;
import com.example.demo.models.File;
import com.example.demo.models.User;
import com.example.demo.service.ChatMessageService;
import com.example.demo.service.ChatSessionService;
import com.example.demo.service.FileService;
import com.example.demo.service.UserService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@Slf4j
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private ChatSessionService chatSessionService;

    @Autowired
    private UserService userService;

    @Autowired
    private FileService fileService;

    /**
     * 处理发送消息
     */
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload MessageRequest messageRequest, Principal principal) {
        log.debug("Received message: {}", messageRequest);
        
        // 获取发送者信息
        User sender = userService.findById(principal.getName());
        if (sender == null) {
            log.error("Unable to find sender: {}", principal.getName());
            return;
        }

        // 获取 File 实体
        File file = fileService.getFileByUrl(messageRequest.getFileUrl());
        
        // 创建消息
        ChatMessage message = ChatMessage.builder()
                .session(chatSessionService.findById(messageRequest.getSessionId()))
                .sender(sender)
                .type(ChatMessage.MessageType.valueOf(messageRequest.getMessageType()))
                .content(messageRequest.getContent())
                .file(file) //File和String两个类型不匹配
                .read(false)
                .build();
        
        // 保存消息
        ChatMessage savedMessage = chatMessageService.save(message);
        
        // 发送给会话中的所有参与者
        chatSessionService.getSessionParticipants(messageRequest.getSessionId()).forEach(participant -> {
            if (!participant.getId().equals(sender.getId())) {
                messagingTemplate.convertAndSendToUser(
                        participant.getName(),
                        "/queue/messages",
                        MessageResponse.builder()
                                .id(savedMessage.getId())
                                .sessionId(savedMessage.getSession().getId())
                                .senderId(savedMessage.getSender().getId())
                                .senderUsername(savedMessage.getSender().getName())
//                                .senderNickname(savedMessage.getSender().getNickname())
                                //我们没设置昵称这个字段
                                .messageType(savedMessage.getType().name())
                                .content(savedMessage.getContent())
//                                .fileUrl(savedMessage.getFile())  //这里File类型和String类型不匹配
                                .fileUrl(savedMessage.getFile() != null ? savedMessage.getFile().getOssUrl() : null)
                                .sentAt(savedMessage.getSentAt())
                                .readStatus(savedMessage.isRead())
                                .build()
                );
            }
        });
    }

    /**
     * 处理输入状态通知
     */
    @MessageMapping("/chat.typing")
    public void typingNotification(@Payload TypingRequest typingRequest, Principal principal) {
        log.debug("Received typing notification: {}", typingRequest);
        
        // 获取发送者信息
        User sender = userService.findById(principal.getName());
        if (sender == null) {
            log.error("Unable to find sender: {}", principal.getName());
            return;
        }
        
        // 发送给会话中的所有参与者
        chatSessionService.getSessionParticipants(typingRequest.getSessionId()).forEach(participant -> {
            if (!participant.getId().equals(sender.getId())) {
                messagingTemplate.convertAndSendToUser(
                        participant.getName(),
                        "/queue/typing",
                        TypingResponse.builder()
                                .sessionId(typingRequest.getSessionId())
                                .senderId(sender.getId())
                                .senderUsername(sender.getName())
                                .typing(typingRequest.isTyping())
                                .build()
                );
            }
        });
    }

    /**
     * 处理标记消息已读
     */
    @MessageMapping("/chat.markAsRead")
    public void markAsRead(@Payload ReadStatusRequest readStatusRequest, Principal principal) {
        log.debug("Marking messages as read: {}", readStatusRequest);
        
        // 获取用户信息
        User user = userService.findById(principal.getName());
        if (user == null) {
            log.error("Unable to find user: {}", principal.getName());
            return;
        }
        
        // 标记消息为已读
        chatMessageService.markSessionMessagesAsRead(readStatusRequest.getSessionId(), user.getId());
        
        // 通知发送者消息已读
        String senderId = readStatusRequest.getSenderId();
        if (senderId != null) {
            User sender = userService.findById(senderId);
            if (sender != null) {
                messagingTemplate.convertAndSendToUser(
                        sender.getName(),
                        "/queue/read-status",
                        ReadStatusResponse.builder()
                                .sessionId(readStatusRequest.getSessionId())
                                .userId(user.getId())
                                .username(user.getName())
                                .messageIds(readStatusRequest.getMessageIds())
                                .build()
                );
            }
        }
    }

    /**
     * 消息请求
     */
    @Data
    public static class MessageRequest {
        private String sessionId;
        private String messageType;
        private String content;
        private String fileUrl;
    }

    /**
     * 消息响应
     */
    @Data
    public static class MessageResponse {
        private Integer id;
        private String sessionId;
        private String senderId;
        private String senderUsername;
        private String senderNickname;
        private String messageType;
        private String content;
        private String fileUrl;
        private LocalDateTime sentAt;
        private Boolean readStatus;
        
        private MessageResponse() {}
        
        public static Builder builder() {
            return new Builder();
        }
        
        public static class Builder {
            private final MessageResponse response = new MessageResponse();
            
            public Builder id(Integer id) {
                response.id = id;
                return this;
            }
            
            public Builder sessionId(String sessionId) {
                response.sessionId = sessionId;
                return this;
            }
            
            public Builder senderId(String senderId) {
                response.senderId = senderId;
                return this;
            }
            
            public Builder senderUsername(String senderUsername) {
                response.senderUsername = senderUsername;
                return this;
            }
            
            public Builder senderNickname(String senderNickname) {
                response.senderNickname = senderNickname;
                return this;
            }
            
            public Builder messageType(String messageType) {
                response.messageType = messageType;
                return this;
            }
            
            public Builder content(String content) {
                response.content = content;
                return this;
            }
            
            public Builder fileUrl(String fileUrl) {
                response.fileUrl = fileUrl;
                return this;
            }
            
            public Builder sentAt(LocalDateTime sentAt) {
                response.sentAt = sentAt;
                return this;
            }
            
            public Builder readStatus(Boolean readStatus) {
                response.readStatus = readStatus;
                return this;
            }
            
            public MessageResponse build() {
                return response;
            }
        }
    }

    /**
     * 输入状态请求
     */
    @Data
    public static class TypingRequest {
        private String sessionId;
        private boolean typing;
    }

    /**
     * 输入状态响应
     */
    @Data
    public static class TypingResponse {
        private String sessionId;
        private String senderId;
        private String senderUsername;
        private boolean typing;
        
        private TypingResponse() {}
        
        public static Builder builder() {
            return new Builder();
        }
        
        public static class Builder {
            private final TypingResponse response = new TypingResponse();
            
            public Builder sessionId(String sessionId) {
                response.sessionId = sessionId;
                return this;
            }
            
            public Builder senderId(String senderId) {
                response.senderId = senderId;
                return this;
            }
            
            public Builder senderUsername(String senderUsername) {
                response.senderUsername = senderUsername;
                return this;
            }
            
            public Builder typing(boolean typing) {
                response.typing = typing;
                return this;
            }
            
            public TypingResponse build() {
                return response;
            }
        }
    }

    /**
     * 已读状态请求
     */
    @Data
    public static class ReadStatusRequest {
        private String sessionId;
        private String senderId;
        private List<String> messageIds;
    }

    /**
     * 已读状态响应
     */
    @Data
    public static class ReadStatusResponse {
        private String sessionId;
        private String userId;
        private String username;
        private List<String> messageIds;
        
        private ReadStatusResponse() {}
        
        public static Builder builder() {
            return new Builder();
        }
        
        public static class Builder {
            private final ReadStatusResponse response = new ReadStatusResponse();
            
            public Builder sessionId(String sessionId) {
                response.sessionId = sessionId;
                return this;
            }
            
            public Builder userId(String userId) {
                response.userId = userId;
                return this;
            }
            
            public Builder username(String username) {
                response.username = username;
                return this;
            }
            
            public Builder messageIds(List<String> messageIds) {
                response.messageIds = messageIds;
                return this;
            }
            
            public ReadStatusResponse build() {
                return response;
            }
        }
    }
}