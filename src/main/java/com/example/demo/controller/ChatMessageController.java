package com.example.demo.controller;

import com.example.demo.models.ChatMessage;
import com.example.demo.models.ChatSession;
import com.example.demo.models.File;
import com.example.demo.models.User;
import com.example.demo.service.ChatMessageService;
import com.example.demo.service.ChatSessionService;
import com.example.demo.service.FileService;
import com.example.demo.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import java.security.Principal;
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
            Principal principal) {
        try {
            if (principal == null) {
                log.error("WebSocket principal is null, authentication failed");
                return;
            }

            User currentUser = userService.findById(principal.getName());
            if (currentUser == null) {
                log.error("User not authenticated");
                return;
            }

            if (!chatSessionService.isSessionParticipant(sessionId, currentUser.getId())) {
                log.error("User {} is not authorized to send messages to session {}", currentUser.getId(), sessionId);
                return;
            }

            ChatSession session = chatSessionService.findById(sessionId);
            if (session == null) {
                log.error("Session not found: {}", sessionId);
                return;
            }

            File file = null;
            if (payload.getFileId() != null) {
                file = fileService.getFile(payload.getFileId());
            }

            ChatMessage message = ChatMessage.builder()
                    .session(session)
                    .sender(currentUser)
//                    .content(payload.getContent())
                    .content(payload.getContent() != null ? payload.getContent() : "") // 允许空content
                    .type(ChatMessage.MessageType.valueOf(payload.getType()))
                    .file(file)
                    .sentAt(LocalDateTime.now())
                    .read(false)
                    .build();

            ChatMessage savedMessage = chatMessageService.createMessage(message);
            chatSessionService.updateLastActivity(sessionId);
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
            User sender = userService.findById(message.getSender().getId());
            if (sender == null) {
                log.error("Message sender not found: {}", message.getSender());
                return;
            }

            ChatSession session = chatSessionService.findById(message.getSession().getId());
            if (session == null) {
                log.error("Chat session not found: {}", message.getSession());
                return;
            }

            List<User> participants = chatSessionService.getSessionParticipants(message.getSession().getId());

            Map<String, Object> messageData = new HashMap<>();
            messageData.put("id", message.getId());
            messageData.put("sessionId", message.getSession().getId());
            messageData.put("senderId", message.getSender().getId());
            messageData.put("senderName", sender.getName());
            messageData.put("content", message.getContent());
            messageData.put("type", message.getType().name());
            messageData.put("sentAt", message.getSentAt().toString());

            if (message.getFile() != null) {
                messageData.put("fileId", message.getFile().getId());
            }

            for (User participant : participants) {
                if (!participant.getId().equals(message.getSender().getId())) {
                    try {
                        messagingTemplate.convertAndSendToUser(
//                                participant.getName(),
                                participant.getId(),
                                "/queue/messages",
                                messageData
                        );
                        log.info("📤 已推送消息给用户: {}", participant.getId()); //getName改成getId
                    } catch (Exception e) {
                        log.error("❌ 推送消息给用户{}失败", participant.getId(), e); //getName改成getId
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to broadcast message", e);
        }
    }

    /**
     * 上传文件并发送文件消息（一步到位）
     */
    @ApiOperation(value = "上传文件并发送文件消息", notes = "上传一个文件，并立即以文件消息形式发送到指定会话")
    @PostMapping(
            value = "/session/{sessionId}/files/upload-and-send",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> uploadAndSendFile(
            @ApiParam(value = "会话ID", required = true)
            @PathVariable String sessionId,
            @ApiParam(value = "要上传的文件", required = true)
            @RequestPart("file") MultipartFile file) {
        try {
            // 1. 验证文件
            if (file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("没有文件上传");
            }

            // 2. 验证用户身份
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.findById(authentication.getName());
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户未认证");
            }

            // 3. 验证会话参与权限
            if (!chatSessionService.isSessionParticipant(sessionId, currentUser.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("无权访问该会话");
            }

            // 4. 查找会话实体
            ChatSession session = chatSessionService.findById(sessionId);
            if (session == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("会话未找到");
            }

            // 5. 上传文件（保存到OSS或本地）
            File savedFile = fileService.saveFile(file, currentUser.getId());

            // 6. 构建文件类型聊天消息
            ChatMessage message = ChatMessage.builder()
                    .session(session)
                    .sender(currentUser)
                    .content("")  // 这里content可以留空或者存一些描述
                    .type(ChatMessage.MessageType.FILE)
                    .file(savedFile)
                    .read(false)
                    .sentAt(LocalDateTime.now())
                    .build();

            // 7. 保存消息、更新会话
            ChatMessage savedMessage = chatMessageService.createMessage(message);
            chatSessionService.updateLastActivity(sessionId);

            // 8. 广播消息
            broadcastMessage(savedMessage);

            // 9. 返回消息
            return ResponseEntity.ok(savedMessage);

        } catch (Exception e) {
            log.error("上传并发送文件消息失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("上传并发送文件消息失败: " + e.getMessage());
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