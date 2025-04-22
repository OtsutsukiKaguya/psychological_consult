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
            User currentUser = userService.findById(authentication.getName());
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
            User currentUser = userService.findById(authentication.getName());
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }
            
            // 检查用户是否是会话的参与者
            if (!chatSessionService.isSessionParticipant(sessionId, currentUser.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized to send messages to this session");
            }

            // ✅【新增】查出 ChatSession 实体
            ChatSession session = chatSessionService.findById(sessionId);
            if (session == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Session not found");
            }

            // ✅【新增】查出 File 实体（如果有）
            File file = null;
            if (request.getFileId() != null) {
                file = fileService.getFile(request.getFileId()); // 确保你实现了这个方法
            }

            // ✅【修改】创建消息：传入 ChatSession 和 File 实体，而不是 String ID
            ChatMessage message = ChatMessage.builder()
                    .session(session)                       // ✅ 修改：原来是 .session(sessionId)
                    .sender(currentUser)                    // ✅ 修改：原来是 .senderId(currentUser.getId())
                    .content(request.getContent())
                    .type(ChatMessage.MessageType.valueOf(request.getType()))
                    .file(file)                             // ✅ 修改：原来是 .fileId(request.getFileId())
                    .read(false)
                    .build();
            
//            // 创建消息
//            ChatMessage message = ChatMessage.builder()
//                    .session(sessionId)
//                    .senderId(currentUser.getId())
//                    .content(request.getContent())
//                    .type(ChatMessage.MessageType.valueOf(request.getType()))
//                    .fileId(request.getFileId())
//                    .build();
            
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
            User currentUser = userService.findById(authentication.getName());
            if (currentUser == null) {
                log.error("User not authenticated");
                return;
            }
            
            // 检查用户是否是会话的参与者
            if (!chatSessionService.isSessionParticipant(sessionId, currentUser.getId())) {
                log.error("User {} is not authorized to send messages to session {}", currentUser.getId(), sessionId);
                return;
            }

            // ✅【新增】查找 ChatSession 实体
            ChatSession session = chatSessionService.findById(sessionId);
            if (session == null) {
                log.error("Session not found: {}", sessionId);
                return;
            }

            // ✅【新增】查找 File 实体（如果传了）
            File file = null;
            if (payload.getFileId() != null) {
                file = fileService.getFile(payload.getFileId());
            }

            // ✅【修改】使用实体构建 ChatMessage
            ChatMessage message = ChatMessage.builder()
                    .session(session)                         // ✅ 原来是 .session(sessionId)
                    .sender(currentUser)                      // ✅ 原来是 .senderId(currentUser.getId())
                    .content(payload.getContent())
                    .type(ChatMessage.MessageType.valueOf(payload.getType()))
                    .file(file)                               // ✅ 原来是 .fileId(payload.getFileId())
                    .sentAt(LocalDateTime.now())
                    .read(false)
                    .build();
            
//            // 创建消息
//            ChatMessage message = ChatMessage.builder()
//                    .session(sessionId)
//                    .senderId(currentUser.getId())
//                    .content(payload.getContent())
//                    .type(ChatMessage.MessageType.valueOf(payload.getType()))
//                    .fileId(payload.getFileId())
//                    .sentAt(LocalDateTime.now())
//                    .build();
            
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
//    @PostMapping("/files")
//    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
//        try {
//            // 获取当前用户
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            User currentUser = userService.findByUsername(authentication.getName());
//            if (currentUser == null) {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
//            }
//
//            // 获取文件信息
//            String originalFilename = file.getOriginalFilename();
//            String contentType = file.getContentType();
//            long size = file.getSize();
//            byte[] bytes = file.getBytes(); //这是什么玩意啊
//
//            // 创建文件记录
//            File fileEntity = File.builder()
//                    .originalName(originalFilename)
//                    .fileType(contentType)
//                    .fileSize(size)  //那这边就要改File类里size的类型和数据库里的字段了？？？
//                    .uploader(currentUser.getId())
//                    .build();
//
//            // 保存文件
//            File savedFile = fileService.saveFile(fileEntity, bytes); //这里报类型不匹配的错
//
//            return ResponseEntity.ok(savedFile);
//        } catch (IOException e) {
//            log.error("Failed to upload file", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file: " + e.getMessage());
//        } catch (Exception e) {
//            log.error("Failed to process file upload", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process file upload: " + e.getMessage());
//        }
//    }

    @PostMapping("/files")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.findById(authentication.getName());
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }

            // 保存文件（使用已有方法）
            File savedFile = fileService.saveFile(file, currentUser.getId());

            return ResponseEntity.ok(savedFile);

        } catch (Exception e) {
            log.error("Failed to process file upload", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process file upload: " + e.getMessage());
        }
    }

    /**
     * 获取文件
     */
//    @GetMapping("/files/{fileId}")
//    public ResponseEntity<?> getFile(@PathVariable Integer fileId) {
//        try {
//            // 获取当前用户
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            User currentUser = userService.findByUsername(authentication.getName());
//            if (currentUser == null) {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
//            }
//
//            // 获取文件
//            File file = fileService.getFile(fileId);
//            if (file == null) {
//                return ResponseEntity.notFound().build();
//            }
//
//            byte[] downloadFile(String ossUrl);
//
//            // 获取文件内容
//            byte[] fileData = fileService.getFile(fileId);
//            if (fileData == null) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File data not found");
//            }
//
//            return ResponseEntity.ok()
//                    .header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"")
//                    .header("Content-Type", file.getContentType())
//                    .body(fileData);
//        } catch (Exception e) {
//            log.error("Failed to get file: {}", fileId, e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to get file: " + e.getMessage());
//        }
//    }

    @GetMapping("/files/{fileId}")
    public ResponseEntity<?> getFile(@PathVariable Integer fileId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.findById(authentication.getName());
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }

            File file = fileService.getFile(fileId);
            if (file == null) {
                return ResponseEntity.notFound().build();
            }

            byte[] fileData = fileService.downloadFile(file.getOssUrl());
            if (fileData == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File data not found");
            }

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + file.getOriginalName() + "\"")
                    .header("Content-Type", file.getFileType())
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
    public ResponseEntity<?> getFileInfo(@PathVariable Integer fileId) {
        try {
            // 获取当前用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.findById(authentication.getName());
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
    public ResponseEntity<?> markAsRead(@PathVariable String sessionId) {
        try {
            // 获取当前用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.findById(authentication.getName());
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
            User sender = userService.findById(message.getSender().getId());
            if (sender == null) {
                log.error("Message sender not found: {}", message.getSender());
                return;
            }
            
            // 获取会话
            ChatSession session = chatSessionService.findById(message.getSession().getId());
            if (session == null) {
                log.error("Chat session not found: {}", message.getSession());
                return;
            }
            
            // 获取会话参与者
            List<User> participants = chatSessionService.getSessionParticipants(message.getSession().getId());
            
            // 构建消息对象
            Map<String, Object> messageData = new HashMap<>();
            messageData.put("id", message.getId());
            messageData.put("sessionId", message.getSession().getId());
            messageData.put("senderId", message.getSender().getId());
            messageData.put("senderName", sender.getName());
//            messageData.put("senderNickname", sender.getNickname()); //没有昵称字段
            messageData.put("content", message.getContent());
            messageData.put("type", message.getType().name());
//            messageData.put("fileId", message.getFile().getId());  //文件为空会导致空指针报错，下面有一个判空逻辑
            messageData.put("sentAt", message.getSentAt().toString());

            // ✅ 文件为 null 时不加入 fileId 字段，避免空指针
            if (message.getFile() != null) {
                messageData.put("fileId", message.getFile().getId());
            }

            // 广播给所有参与者
            for (User participant : participants) {
                // 不向发送者广播
                if (!participant.getId().equals(message.getSender().getId())) {
                    messagingTemplate.convertAndSendToUser(
                            participant.getName(),
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
        private Integer fileId;
    }

    /**
     * WebSocket 消息载荷
     */
    @Data
    public static class MessagePayload {
        private String content;
        private String type;
        private Integer fileId;
    }
}