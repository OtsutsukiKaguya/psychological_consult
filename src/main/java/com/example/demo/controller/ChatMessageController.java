//package com.counseling.platform.controllers;
package com.example.demo.controller;

import com.example.demo.models.ChatMessage;
import com.example.demo.models.ChatSession;
import com.example.demo.models.File;
import com.example.demo.models.User;
import com.example.demo.service.ChatMessageService;
import com.example.demo.service.ChatSessionService;
import com.example.demo.service.FileService;
import com.example.demo.service.UserService;
//import com.counseling.platform.models.ChatMessage;
//import com.counseling.platform.models.ChatSession;
//import com.counseling.platform.models.File;
//import com.counseling.platform.models.User;
//import com.counseling.platform.services.ChatMessageService;
//import com.counseling.platform.services.ChatSessionService;
//import com.counseling.platform.services.FileService;
//import com.counseling.platform.services.UserService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 聊天消息控制器
 * 处理消息的发送和接收
 */
@RestController
@RequestMapping("/api/messages")
@Slf4j
public class ChatMessageController {

    @Autowired
    private ChatMessageService chatMessageService;
    
    @Autowired
    private ChatSessionService chatSessionService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private FileService fileService;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * 获取会话的消息
     */
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<?> getSessionMessages(
            @PathVariable String sessionId,
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        try {
            // 获取当前用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.findByUsername(authentication.getName());
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }
            
            // 检查用户是否是会话的参与者
            if (!chatSessionService.isSessionParticipant(sessionId, currentUser.getId()) && 
                currentUser.getRole() != User.UserRole.ADMIN) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized to access this session");
            }
            
            // 获取会话消息
            List<ChatMessage> messages = chatMessageService.getSessionMessages(sessionId, limit, offset);
            
            // 标记消息为已读
            chatSessionService.markSessionAsRead(sessionId, currentUser.getId());
            
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            log.error("Failed to get session messages: {}", sessionId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to get session messages: " + e.getMessage());
        }
    }

    /**
     * 发送消息（HTTP 方式）
     */
    @PostMapping("/session/{sessionId}")
    public ResponseEntity<?> sendMessage(
            @PathVariable String sessionId,
            @Valid @RequestBody SendMessageRequest request) {
        try {
            // 获取当前用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.findByUsername(authentication.getName());
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }
            
            // 检查用户是否是会话的参与者
            if (!chatSessionService.isSessionParticipant(sessionId, currentUser.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized to send messages to this session");
            }
            
            // 创建消息
            ChatMessage message = ChatMessage.builder()
                    .sessionId(sessionId)
                    .senderId(currentUser.getId())
                    .content(request.getContent())
                    .type(ChatMessage.MessageType.valueOf(request.getType()))
                    .fileId(request.getFileId())
                    .build();
            
            // 保存消息
            ChatMessage savedMessage = chatMessageService.createMessage(message);
            
            // 更新会话最后活动时间
            chatSessionService.updateLastActivity(sessionId);
            
            // 广播消息给会话参与者
            broadcastMessage(savedMessage);
            
            return ResponseEntity.ok(savedMessage);
        } catch (Exception e) {
            log.error("Failed to send message to session: {}", sessionId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send message: " + e.getMessage());
        }
    }

    /**
     * 发送消息（WebSocket 方式）
     */
    @MessageMapping("/chat/{sessionId}")
    public void handleChatMessage(
            @DestinationVariable String sessionId,
            @Payload MessagePayload payload,
            Authentication authentication) {
        try {
            // 获取当前用户
            User currentUser = userService.findByUsername(authentication.getName());
            if (currentUser == null) {
                log.error("User not authenticated");
                return;
            }
            
            // 检查用户是否是会话的参与者
            if (!chatSessionService.isSessionParticipant(sessionId, currentUser.getId())) {
                log.error("User {} is not authorized to send messages to session {}", currentUser.getId(), sessionId);
                return;
            }
            
            // 创建消息
            ChatMessage message = ChatMessage.builder()
                    .sessionId(sessionId)
                    .senderId(currentUser.getId())
                    .content(payload.getContent())
                    .type(ChatMessage.MessageType.valueOf(payload.getType()))
                    .fileId(payload.getFileId())
                    .sentAt(LocalDateTime.now())
                    .build();
            
            // 保存消息
            ChatMessage savedMessage = chatMessageService.createMessage(message);
            
            // 更新会话最后活动时间
            chatSessionService.updateLastActivity(sessionId);
            
            // 广播消息给会话参与者
            broadcastMessage(savedMessage);
        } catch (Exception e) {
            log.error("Failed to handle chat message for session: {}", sessionId, e);
        }
    }

    /**
     * 上传文件
     */
    @PostMapping("/files")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // 获取当前用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.findByUsername(authentication.getName());
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }
            
            // 获取文件信息
            String originalFilename = file.getOriginalFilename();
            String contentType = file.getContentType();
            long size = file.getSize();
            byte[] bytes = file.getBytes();
            
            // 创建文件记录
            File fileEntity = File.builder()
                    .name(originalFilename)
                    .contentType(contentType)
                    .size(size)
                    .uploaderId(currentUser.getId())
                    .build();
            
            // 保存文件
            File savedFile = fileService.saveFile(fileEntity, bytes);
            
            return ResponseEntity.ok(savedFile);
        } catch (IOException e) {
            log.error("Failed to upload file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file: " + e.getMessage());
        } catch (Exception e) {
            log.error("Failed to process file upload", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process file upload: " + e.getMessage());
        }
    }

    /**
     * 获取文件
     */
    @GetMapping("/files/{fileId}")
    public ResponseEntity<?> getFile(@PathVariable Long fileId) {
        try {
            // 获取当前用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.findByUsername(authentication.getName());
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }
            
            // 获取文件
            File file = fileService.getFile(fileId);
            if (file == null) {
                return ResponseEntity.notFound().build();
            }
            
            // 获取文件内容
            byte[] fileData = fileService.getFileData(fileId);
            if (fileData == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File data not found");
            }
            
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"")
                    .header("Content-Type", file.getContentType())
                    .body(fileData);
        } catch (Exception e) {
            log.error("Failed to get file: {}", fileId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to get file: " + e.getMessage());
        }
    }

    /**
     * 获取文件信息
     */
    @GetMapping("/files/{fileId}/info")
    public ResponseEntity<?> getFileInfo(@PathVariable Long fileId) {
        try {
            // 获取当前用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.findByUsername(authentication.getName());
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }
            
            // 获取文件
            File file = fileService.getFile(fileId);
            if (file == null) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(file);
        } catch (Exception e) {
            log.error("Failed to get file info: {}", fileId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to get file info: " + e.getMessage());
        }
    }

    /**
     * 标记消息为已读
     */
    @PostMapping("/session/{sessionId}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long sessionId) {
        try {
            // 获取当前用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.findByUsername(authentication.getName());
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }
            
            // 检查用户是否是会话的参与者
            if (!chatSessionService.isSessionParticipant(sessionId, currentUser.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized to access this session");
            }
            
            // 标记会话为已读
            chatSessionService.markSessionAsRead(sessionId, currentUser.getId());
            
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to mark session as read: {}", sessionId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to mark session as read: " + e.getMessage());
        }
    }

    /**
     * 广播消息给会话参与者
     */
    private void broadcastMessage(ChatMessage message) {
        try {
            // 获取消息发送者
            User sender = userService.findById(message.getSenderId());
            if (sender == null) {
                log.error("Message sender not found: {}", message.getSenderId());
                return;
            }
            
            // 获取会话
            ChatSession session = chatSessionService.findById(message.getSessionId());
            if (session == null) {
                log.error("Chat session not found: {}", message.getSessionId());
                return;
            }
            
            // 获取会话参与者
            List<User> participants = chatSessionService.getSessionParticipants(message.getSessionId());
            
            // 构建消息对象
            Map<String, Object> messageData = new HashMap<>();
            messageData.put("id", message.getId());
            messageData.put("sessionId", message.getSessionId());
            messageData.put("senderId", message.getSenderId());
            messageData.put("senderName", sender.getUsername());
            messageData.put("senderNickname", sender.getNickname());
            messageData.put("content", message.getContent());
            messageData.put("type", message.getType().name());
            messageData.put("fileId", message.getFileId());
            messageData.put("sentAt", message.getSentAt().toString());
            
            // 广播给所有参与者
            for (User participant : participants) {
                // 不向发送者广播
                if (!participant.getId().equals(message.getSenderId())) {
                    messagingTemplate.convertAndSendToUser(
                            participant.getUsername(),
                            "/queue/messages",
                            messageData
                    );
                }
            }
        } catch (Exception e) {
            log.error("Failed to broadcast message", e);
        }
    }

    /**
     * 发送消息请求
     */
    @Data
    public static class SendMessageRequest {
        private String content;
        private String type;
        private Long fileId;
    }

    /**
     * WebSocket 消息载荷
     */
    @Data
    public static class MessagePayload {
        private String content;
        private String type;
        private Long fileId;
    }
}