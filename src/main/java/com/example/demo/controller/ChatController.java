//package com.counseling.platform.controllers;
package com.example.demo.controller;

import com.example.demo.models.ChatMessage;
import com.example.demo.models.User;
import com.example.demo.service.ChatMessageService;
import com.example.demo.service.ChatSessionService;
import com.example.demo.service.UserService;
//import com.counseling.platform.models.ChatMessage;
//import com.counseling.platform.models.User;
//import com.counseling.platform.services.ChatMessageService;
//import com.counseling.platform.services.ChatSessionService;
//import com.counseling.platform.services.UserService;
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

    /**
     * 处理发送消息
     */
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload MessageRequest messageRequest, Principal principal) {
        log.debug("Received message: {}", messageRequest);
        
        // 获取发送者信息
        User sender = userService.findByUsername(principal.getName());
        if (sender == null) {
            log.error("Unable to find sender: {}", principal.getName());
            return;
        }
        
        // 创建消息
        ChatMessage message = ChatMessage.builder()
                .session(chatSessionService.findById(messageRequest.getSessionId()))
                .sender(sender)
                .messageType(ChatMessage.MessageType.valueOf(messageRequest.getMessageType()))
                .content(messageRequest.getContent())
                .fileUrl(messageRequest.getFileUrl())
                .readStatus(false)
                .build();
        
        // 保存消息
        ChatMessage savedMessage = chatMessageService.save(message);
        
        // 发送给会话中的所有参与者
        chatSessionService.getSessionParticipants(messageRequest.getSessionId()).forEach(participant -> {
            if (!participant.getUser().getId().equals(sender.getId())) {
                messagingTemplate.convertAndSendToUser(
                        participant.getUser().getUsername(),
                        "/queue/messages",
                        MessageResponse.builder()
                                .id(savedMessage.getId())
                                .sessionId(savedMessage.getSession().getId())
                                .senderId(savedMessage.getSender().getId())
                                .senderUsername(savedMessage.getSender().getUsername())
                                .senderNickname(savedMessage.getSender().getNickname())
                                .messageType(savedMessage.getMessageType().name())
                                .content(savedMessage.getContent())
                                .fileUrl(savedMessage.getFileUrl())
                                .sentAt(savedMessage.getSentAt())
                                .readStatus(savedMessage.getReadStatus())
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
        User sender = userService.findByUsername(principal.getName());
        if (sender == null) {
            log.error("Unable to find sender: {}", principal.getName());
            return;
        }
        
        // 发送给会话中的所有参与者
        chatSessionService.getParticipants(typingRequest.getSessionId()).forEach(participant -> {
            if (!participant.getUser().getId().equals(sender.getId())) {
                messagingTemplate.convertAndSendToUser(
                        participant.getUser().getUsername(),
                        "/queue/typing",
                        TypingResponse.builder()
                                .sessionId(typingRequest.getSessionId())
                                .senderId(sender.getId())
                                .senderUsername(sender.getUsername())
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
        User user = userService.findByUsername(principal.getName());
        if (user == null) {
            log.error("Unable to find user: {}", principal.getName());
            return;
        }
        
        // 标记消息为已读
        chatMessageService.markAsReadInSession(readStatusRequest.getSessionId(), user.getId());
        
        // 通知发送者消息已读
        Long senderId = readStatusRequest.getSenderId();
        if (senderId != null) {
            User sender = userService.findById(senderId);
            if (sender != null) {
                messagingTemplate.convertAndSendToUser(
                        sender.getUsername(),
                        "/queue/read-status",
                        ReadStatusResponse.builder()
                                .sessionId(readStatusRequest.getSessionId())
                                .userId(user.getId())
                                .username(user.getUsername())
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
            
            public Builder id(Long id) {
                response.id = id;
                return this;
            }
            
            public Builder sessionId(Long sessionId) {
                response.sessionId = sessionId;
                return this;
            }
            
            public Builder senderId(Long senderId) {
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
        private Long sessionId;
        private boolean typing;
    }

    /**
     * 输入状态响应
     */
    @Data
    public static class TypingResponse {
        private Long sessionId;
        private Long senderId;
        private String senderUsername;
        private boolean typing;
        
        private TypingResponse() {}
        
        public static Builder builder() {
            return new Builder();
        }
        
        public static class Builder {
            private final TypingResponse response = new TypingResponse();
            
            public Builder sessionId(Long sessionId) {
                response.sessionId = sessionId;
                return this;
            }
            
            public Builder senderId(Long senderId) {
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
        private Long sessionId;
        private Long senderId;
        private List<Long> messageIds;
    }

    /**
     * 已读状态响应
     */
    @Data
    public static class ReadStatusResponse {
        private Long sessionId;
        private Long userId;
        private String username;
        private List<Long> messageIds;
        
        private ReadStatusResponse() {}
        
        public static Builder builder() {
            return new Builder();
        }
        
        public static class Builder {
            private final ReadStatusResponse response = new ReadStatusResponse();
            
            public Builder sessionId(Long sessionId) {
                response.sessionId = sessionId;
                return this;
            }
            
            public Builder userId(Long userId) {
                response.userId = userId;
                return this;
            }
            
            public Builder username(String username) {
                response.username = username;
                return this;
            }
            
            public Builder messageIds(List<Long> messageIds) {
                response.messageIds = messageIds;
                return this;
            }
            
            public ReadStatusResponse build() {
                return response;
            }
        }
    }
}