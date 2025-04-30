//package com.counseling.platform.controllers;
package com.example.demo.controller;

//import com.counseling.platform.models.ChatSession;
//import com.counseling.platform.models.SessionParticipant;
//import com.counseling.platform.models.User;
//import com.counseling.platform.services.ChatMessageService;
//import com.counseling.platform.services.ChatSessionService;
//import com.counseling.platform.services.UserService;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

import com.example.demo.DTO.ChatRecordDTO;
import com.example.demo.DTO.ChatSessionWithParticipantsDTO;
import com.example.demo.models.ChatMessage;
import com.example.demo.models.ChatSession;
import com.example.demo.models.SessionParticipant;
import com.example.demo.models.User;
import com.example.demo.repositories.ChatMessageRepository;
import com.example.demo.service.ChatExportService;
import com.example.demo.service.ChatMessageService;
import com.example.demo.service.ChatSessionService;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.stream.Collectors;

/**
 * 会话控制器
 * 处理聊天会话的创建和管理
 */
@RestController
@RequestMapping("/api/sessions")
@Slf4j
public class SessionController {

    @Autowired
    private ChatSessionService chatSessionService;

    @Autowired
    private UserService userService;

    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ChatExportService chatExportService;

    @Data
    public static class EndSessionRequest {
        /** 评论内容，所有人必填 */
        @NotBlank(message = "comment 不能为空")
        private String comment;

        /** 用户评分，仅 USER 填写 */
        private Integer rating;
    }

    /**
     * 任意参与者都可结束会话。
     * - USER: 提交 comment + rating
     * - COUNSELOR / TUTOR: 仅提交 comment
     */
    @PostMapping("/{sessionId}/end")
    public ResponseEntity<?> endSession(
            @PathVariable String sessionId,
            @Valid @RequestBody EndSessionRequest req) {

        // 1. 验证身份
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findById(auth.getName());
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户未认证");
        }

        // 2. 加载会话并验证参与权限
        ChatSession session = chatSessionService.findById(sessionId);
        if (session == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("会话不存在");
        }
        if (!chatSessionService.isSessionParticipant(sessionId, currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("无权操作该会话");
        }

        // 3. 更新会话结束标记和评论内容
        switch (currentUser.getRole()) {
            case USER -> {
                session.setUserComment(req.getComment());
                if (req.getRating() == null) {
                    return ResponseEntity.unprocessableEntity().body("USER 必须提交 rating");
                }
                session.setRating(req.getRating());
            }
            case COUNSELOR -> session.setCounselorComment(req.getComment());
            case TUTOR -> session.setTutorComment(req.getComment());
        }

        // 如果第一次调用结束，则标记为结束
        if (!Boolean.TRUE.equals(session.getEnded())) {
            session.setEnded(true);
            session.setEndedAt(LocalDateTime.now());
        }

        chatSessionService.updateSession(session);

        // ====== 广播“xxx 已退出会话”系统消息 ======
        ChatMessage systemMessage = ChatMessage.builder()
                .session(session)
                .sender(currentUser)
                .content(currentUser.getName() + " 已退出会话")
                .type(ChatMessage.MessageType.SYSTEM)  // 请确保你枚举中有 SYSTEM 类型
                .sentAt(LocalDateTime.now())
                .read(false)
                .build();

        chatMessageService.createMessage(systemMessage);
        chatSessionService.updateLastActivity(sessionId);  // 更新会话活跃时间
        broadcastMessage(systemMessage);

        return ResponseEntity.ok(Map.of("message", "已成功结束会话并提交评价"));
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
     * 查看会话详情
     */
    @GetMapping("/{sessionId}/records")
    public ResponseEntity<?> viewChatSessionDetails(@PathVariable String sessionId) {
        try {
            // 1. 校验
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.findById(auth.getName());
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户未认证");
            }
            ChatSession session = chatSessionService.findById(sessionId);
            if (session == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("会话不存在");
            }
            if (!chatSessionService.isSessionParticipant(sessionId, currentUser.getId())
                    && currentUser.getRole() != User.UserRole.ADMIN) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("无权查看该会话记录");
            }

            // 2. 查聊天记录（注意顺序是 sentAt 降序）
            List<ChatMessage> messages = chatMessageRepository.findBySessionIdOrderBySentAtAsc(sessionId);

            // 3. 转成简化版DTO
            List<ChatRecordDTO> messageList = messages.stream().map(msg -> new ChatRecordDTO(
                    msg.getSender() != null ? msg.getSender().getId() : "Unknown",
                    msg.getSentAt() != null ? msg.getSentAt().toString() : "",
                    msg.getType() != null ? msg.getType().name() : "",
                    msg.getContent() != null ? msg.getContent() : ""
            )).toList();

            // 4. 返回 session 的评分+评论+聊天记录
            Map<String, Object> result = new HashMap<>();
            result.put("sessionId", session.getId());
            result.put("rating", session.getRating());
            result.put("userComment", session.getUserComment());
            result.put("counselorComment", session.getCounselorComment());
            result.put("tutorComment", session.getTutorComment());
            result.put("messages", messageList);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("Failed to view session details: {}", sessionId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("查看失败：" + e.getMessage());
        }
    }




    /**
     * 导出会话聊天记录
     */
    @GetMapping("/{sessionId}/export")
    public void exportChatMessages(
            @PathVariable String sessionId,
            @RequestParam("format") String format,
            @RequestParam(value = "start_date", required = false) String startDateStr,
            @RequestParam(value = "end_date", required = false) String endDateStr,
            HttpServletResponse response) {

        try {
            // 1. 校验导出格式
            List<String> allowedFormats = Arrays.asList("pdf", "excel", "csv", "txt");
            if (!allowedFormats.contains(format.toLowerCase())) {
                response.setStatus(422);
                response.getWriter().write("format 不在允许范围内（pdf / excel / csv / txt）");
                return;
            }

            // 2. 验证用户身份及权限
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.findById(auth.getName());
            if (currentUser == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("用户未认证");
                return;
            }
            ChatSession session = chatSessionService.findById(sessionId);
            if (session == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("会话不存在");
                return;
            }
            if (!chatSessionService.isSessionParticipant(sessionId, currentUser.getId())
                    && currentUser.getRole() != User.UserRole.ADMIN) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("无权导出该会话记录");
                return;
            }

            // 3. 解析日期并查询消息
            List<ChatMessage> messages;
            if (startDateStr != null && endDateStr != null) {
                LocalDateTime start, end;
                try {
                    LocalDate sd = LocalDate.parse(startDateStr);
                    LocalDate ed = LocalDate.parse(endDateStr);
                    start = sd.atStartOfDay();
                    end = ed.atTime(23, 59, 59);
                } catch (DateTimeParseException e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("日期格式应为 yyyy-MM-dd");
                    return;
                }
                messages = chatMessageRepository
                        .findBySessionIdAndSentAtBetweenOrderBySentAtDesc(sessionId, start, end);
            } else {
                messages = chatMessageRepository.findBySessionIdOrderBySentAtAsc(sessionId);
            }
            if (messages.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("导出失败：没有找到符合条件的聊天记录");
                return;
            }

            // 🔥 改这里！！
            byte[] fileBytes = chatExportService.generateFileContent(session, messages, format);  // 调用新版

            // 设置响应头
            response.setContentType(getContentType(format));
            String filename = "chat_messages_" + UUID.randomUUID().toString().substring(0, 6) + "." + format;
            String encodedFilename = URLEncoder.encode(filename, "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFilename);
            response.setContentLength(fileBytes.length);

            // 写入输出流
            OutputStream outputStream = response.getOutputStream();
            outputStream.write(fileBytes);
            outputStream.flush();

        } catch (Exception e) {
            log.error("Failed to export chat messages", e);
            try {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("导出失败：" + e.getMessage());
            } catch (IOException ioException) {
                log.error("Failed to write error response", ioException);
            }
        }
    }

    /**
     * 根据导出格式返回 MIME 类型
     */
    private String getContentType(String format) {
        switch (format.toLowerCase()) {
            case "csv":
                return "text/csv";
            case "txt":
                return "text/plain";
            case "pdf":
                return "application/pdf";
            case "excel":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            default:
                return "application/octet-stream";
        }
    }


    /**
     * 获取所有会话
     * 仅管理员可访问
     */
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllSessions() {
        try {
            List<ChatSession> sessions = chatSessionService.findAllByOrderByUpdatedAtDesc();;
            return ResponseEntity.ok(sessions);
        } catch (Exception e) {
            log.error("Failed to get all sessions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to get all sessions: " + e.getMessage());
        }
    }

    /**
     * 获取当前用户的会话
     */
    @GetMapping("/me")
    public ResponseEntity<?> getUserSessions() {
        try {
            // 获取当前用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.findById(authentication.getName());
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }

            // 获取用户参与的会话
            List<ChatSession> sessions = chatSessionService.findByUserId(currentUser.getId());

            return ResponseEntity.ok(sessions);
        } catch (Exception e) {
            log.error("Failed to get user sessions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to get user sessions: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取会话
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getSessionById(@PathVariable String id) {
        try {
            // 获取当前用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.findById(authentication.getName());
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }

            // 获取会话
            ChatSession session = chatSessionService.findById(id);
            if (session == null) {
                return ResponseEntity.notFound().build();
            }

            // 检查用户是否是会话的参与者
            if (!chatSessionService.isSessionParticipant(id, currentUser.getId()) &&
                    currentUser.getRole() != User.UserRole.ADMIN) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized to access this session");
            }

            // ✅ 获取参与者（通过已有方法）
            List<User> participants = chatSessionService.getSessionParticipants(id);

            // ✅ 手动构造返回 DTO
            ChatSessionWithParticipantsDTO result = new ChatSessionWithParticipantsDTO(
                    session.getId(),
                    session.getType().name(),
                    session.getCreatedAt(),
                    session.getUpdatedAt(),
                    participants
            );

            return ResponseEntity.ok(result);
//            return ResponseEntity.ok(session);
        } catch (Exception e) {
            log.error("Failed to get session by id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to get session: " + e.getMessage());
        }
    }

    /**
     * 创建新会话
     */
    @PostMapping
    public ResponseEntity<?> createSession(@Valid @RequestBody CreateSessionRequest request) {
        try {
            log.debug("Creating session: {}", request);

            // 获取当前用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.findById(authentication.getName());
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }

            // 如果是一对一会话，检查已存在的会话
            if (request.getType().equals("ONE_TO_ONE") && request.getParticipantIds().size() == 1) {
                String otherUserId = request.getParticipantIds().get(0);
                ChatSession existingSession = chatSessionService.getOneToOneSession(currentUser.getId(), otherUserId);

                if (existingSession != null) {
                    log.debug("Found existing one-to-one session: {}", existingSession.getId());
                    return ResponseEntity.ok(existingSession);
                }
            }

            // ✅ 手动生成 UUID 作为主键
            String generatedSessionId = UUID.randomUUID().toString();

            // 创建新会话（显式设置 ID）
            ChatSession session = ChatSession.builder()
                    .id(generatedSessionId)  // ✅ 显式设置 ID，避免 Hibernate 报错
                    .type(ChatSession.SessionType.valueOf(request.getType()))
                    .build();

//            // 创建新会话
//            ChatSession session = ChatSession.builder()
//                    //.name(request.getName())
//                    //.description(request.getDescription())
//                    .type(ChatSession.SessionType.valueOf(request.getType()))
//                    .build();

            ChatSession savedSession = chatSessionService.createSession(session);

            // 添加当前用户作为参与者
            chatSessionService.addParticipant(savedSession.getId(), currentUser.getId(),
                    determineRole(currentUser));

            // 添加其他参与者
            for (String userId : request.getParticipantIds()) {
                User participant = userService.findById(userId);
                if (participant != null) {
                    chatSessionService.addParticipant(savedSession.getId(), userId,
                            determineRole(participant));

                    // 通知被邀请的用户
                    sendSessionInvitation(participant, savedSession, currentUser);
                }
            }

            return ResponseEntity.ok(savedSession);
        } catch (Exception e) {
            log.error("Failed to create session", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create session: " + e.getMessage());
        }
    }

//    /**
//     * 更新会话信息
//     */
//    @PutMapping("/{id}")
//    public ResponseEntity<?> updateSession(@PathVariable String id, @Valid @RequestBody UpdateSessionRequest request) {
//        try {
//            // 获取当前用户
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            User currentUser = userService.findById(authentication.getName());
//            if (currentUser == null) {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
//            }
//
//            // 获取会话
//            ChatSession session = chatSessionService.findById(id);
//            if (session == null) {
//                return ResponseEntity.notFound().build();
//            }
//
//            // 检查用户是否是会话的参与者，或者是管理员
//            if (!chatSessionService.isSessionParticipant(id, currentUser.getId()) &&
//                    currentUser.getRole() != User.UserRole.ADMIN) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized to update this session");
//            }
//
//            // 更新会话信息
//            ChatSession updatedSession = ChatSession.builder()
//                    //.name(request.getName())
//                    //.description(request.getDescription())
//                    .build();
//
//            ChatSession result = chatSessionService.updateSession(id, updatedSession);
//
//            // 通知会话参与者
//            notifySessionUpdated(result);
//
//            return ResponseEntity.ok(result);
//        } catch (Exception e) {
//            log.error("Failed to update session: {}", id, e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update session: " + e.getMessage());
//        }
//    }

    /**
     * 获取会话参与者
     */
    @GetMapping("/{id}/participants")
    public ResponseEntity<?> getSessionParticipants(@PathVariable String id) {
        try {
            // 获取当前用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.findById(authentication.getName());
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }

            // 获取会话
            ChatSession session = chatSessionService.findById(id);
            if (session == null) {
                return ResponseEntity.notFound().build();
            }

            // 检查用户是否是会话的参与者，或者是管理员
            if (!chatSessionService.isSessionParticipant(id, currentUser.getId()) &&
                    currentUser.getRole() != User.UserRole.ADMIN) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized to access this session");
            }

            // 获取参与者
            List<User> participants = chatSessionService.getSessionParticipants(id);
            return ResponseEntity.ok(participants);
        } catch (Exception e) {
            log.error("Failed to get session participants: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to get session participants: " + e.getMessage());
        }
    }

    /**
     * 添加参与者到会话
     */
    @PostMapping("/{id}/participants")
    public ResponseEntity<?> addParticipant(@PathVariable String id, @Valid @RequestBody AddParticipantRequest request) {
        try {
            // 获取当前用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.findById(authentication.getName());
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }

            // 获取会话
            ChatSession session = chatSessionService.findById(id);
            if (session == null) {
                return ResponseEntity.notFound().build();
            }

            // 检查用户是否是会话的参与者，或者是管理员
            if (!chatSessionService.isSessionParticipant(id, currentUser.getId()) &&
                    currentUser.getRole() != User.UserRole.ADMIN) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized to add participants to this session");
            }

            // 如果是一对一会话，不允许添加更多参与者
            if (session.getType() == ChatSession.SessionType.ONE_TO_ONE) {
                return ResponseEntity.badRequest().body("Cannot add more participants to a one-to-one session");
            }

            // 添加参与者
            List<String> addedUserIds = new ArrayList<>();
            for (String userId : request.getUserIds()) {
                User user = userService.findById(userId);
                if (user != null) {
                    // 检查用户是否已经是会话的参与者
                    if (!chatSessionService.isSessionParticipant(id, userId)) {
                        SessionParticipant participant = chatSessionService.addParticipant(id, userId, determineRole(user));
                        if (participant != null) {
                            addedUserIds.add(userId);
                            // 通知被邀请的用户
                            sendSessionInvitation(user, session, currentUser);
                        }
                    }
                }
            }

            // 通知其他参与者有新成员加入
            if (!addedUserIds.isEmpty()) {
                notifyParticipantsAdded(session, addedUserIds, currentUser);
            }

            return ResponseEntity.ok(Map.of("addedUserIds", addedUserIds));
        } catch (Exception e) {
            log.error("Failed to add participants to session: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add participants: " + e.getMessage());
        }
    }

    /**
     * 从会话中移除参与者
     */
    @DeleteMapping("/{id}/participants/{userId}")
    public ResponseEntity<?> removeParticipant(@PathVariable String id, @PathVariable String userId) {
        try {
            // 获取当前用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.findById(authentication.getName());
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }

            // 获取会话
            ChatSession session = chatSessionService.findById(id);
            if (session == null) {
                return ResponseEntity.notFound().build();
            }

            // 检查被移除的用户是否存在
            User userToRemove = userService.findById(userId);
            if (userToRemove == null) {
                return ResponseEntity.badRequest().body("User not found");
            }

            // 检查权限
            boolean isAdmin = currentUser.getRole() == User.UserRole.ADMIN;
            boolean isSelf = currentUser.getId().equals(userId);
            boolean isSessionParticipant = chatSessionService.isSessionParticipant(id, currentUser.getId());

            // 如果是自己退出，或者是管理员，或者是会话参与者移除其他人，则允许
            if (isSelf || isAdmin || isSessionParticipant) {
                // 如果是一对一会话，不允许移除参与者（除非是自己退出）
                if (session.getType() == ChatSession.SessionType.ONE_TO_ONE && !isSelf) {
                    return ResponseEntity.badRequest().body("Cannot remove participants from a one-to-one session");
                }

                // 移除参与者
                chatSessionService.removeParticipant(id, userId);

                // 通知其他参与者有成员离开
                notifyParticipantRemoved(session, userId, currentUser.getId());

                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized to remove participants from this session");
            }
        } catch (Exception e) {
            log.error("Failed to remove participant from session: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to remove participant: " + e.getMessage());
        }
    }

    /**
     * 删除会话
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSession(@PathVariable String id) {
        try {
            // 获取当前用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.findById(authentication.getName());
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }

            // 获取会话
            ChatSession session = chatSessionService.findById(id);
            if (session == null) {
                return ResponseEntity.notFound().build();
            }

            // 检查权限（仅管理员可以删除会话）
            if (currentUser.getRole() != User.UserRole.ADMIN) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized to delete this session");
            }

            // 获取会话参与者，以便后续通知
            List<User> participants = chatSessionService.getSessionParticipants(id);

            // 删除会话
            chatSessionService.deleteSession(id);

            // 通知会话参与者
            notifySessionDeleted(session, participants);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to delete session: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete session: " + e.getMessage());
        }
    }

    /**
     * 获取最近的会话
     */
    @GetMapping("/recent")
    public ResponseEntity<?> getRecentSessions() {
        try {
            // 获取当前用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.findById(authentication.getName());
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }

            // 获取最近的会话
            List<ChatSession> sessions = chatSessionService.getRecentSessions(currentUser.getId());

            // 遍历每个会话，手动获取参与者并构建 DTO
            List<ChatSessionWithParticipantsDTO> resultList = sessions.stream()
                    .map(session -> {
                        List<User> participants = chatSessionService.getSessionParticipants(session.getId());
                        return new ChatSessionWithParticipantsDTO(
                                session.getId(),
                                session.getType().name(),
                                session.getCreatedAt(),
                                session.getUpdatedAt(),
                                participants
                        );
                    })
                    .toList();

            return ResponseEntity.ok(resultList);

//            return ResponseEntity.ok(sessions);
        } catch (Exception e) {
            log.error("Failed to get recent sessions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to get recent sessions: " + e.getMessage());
        }
    }

    /**
     * 根据用户角色确定会话参与者角色
     */
    private SessionParticipant.ParticipantRole determineRole(User user) {
        switch (user.getRole()) {
            case COUNSELOR:
                return SessionParticipant.ParticipantRole.COUNSELOR;
            case TUTOR:
                return SessionParticipant.ParticipantRole.TUTOR;
            case ADMIN:
                return SessionParticipant.ParticipantRole.ADMIN;
            default:
                return SessionParticipant.ParticipantRole.USER;
        }
    }

    /**
     * 发送会话邀请通知
     */
    private void sendSessionInvitation(User invitee, ChatSession session, User inviter) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "SESSION_INVITATION");
        notification.put("sessionId", session.getId());
        //notification.put("sessionName", session.getName());
        notification.put("sessionType", session.getType().name());
        notification.put("inviterId", inviter.getId());
        notification.put("inviterName", inviter.getId());

        messagingTemplate.convertAndSendToUser(
                invitee.getId(),
                "/queue/notifications",
                notification
        );
    }

    /**
     * 通知会话参与者会话信息已更新
     */
    private void notifySessionUpdated(ChatSession session) {
        List<User> participants = chatSessionService.getSessionParticipants(session.getId());

        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "SESSION_UPDATED");
        notification.put("sessionId", session.getId());
        //notification.put("sessionName", session.getName());
        //notification.put("sessionDescription", session.getDescription());

        for (User participant : participants) {
            messagingTemplate.convertAndSendToUser(
                    participant.getId(),
                    "/queue/notifications",
                    notification
            );
        }
    }

    /**
     * 通知会话参与者有新成员加入
     */
    private void notifyParticipantsAdded(ChatSession session, List<String> addedUserIds, User adder) {
        List<User> participants = chatSessionService.getSessionParticipants(session.getId());
        List<User> addedUsers = addedUserIds.stream()
                .map(id -> userService.findById(id))
                .collect(Collectors.toList());

        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "PARTICIPANTS_ADDED");
        notification.put("sessionId", session.getId());
        //notification.put("sessionName", session.getName());
        notification.put("adderId", adder.getId());
        notification.put("adderName", adder.getId());
        notification.put("addedUsers", addedUsers.stream()
                .filter(u -> u != null)
                .map(u -> Map.of(
                        "id", u.getId(),
                        "username", u.getName(),
                        "role", u.getRole().name()
                ))
                .collect(Collectors.toList()));

        for (User participant : participants) {
            // 不向新加入的成员发送通知
            if (!addedUserIds.contains(participant.getId())) {
                messagingTemplate.convertAndSendToUser(
                        participant.getId(),
                        "/queue/notifications",
                        notification
                );
            }
        }
    }

    /**
     * 通知会话参与者有成员离开
     */
    private void notifyParticipantRemoved(ChatSession session, String removedUserId, String removerUserId) {
        List<User> participants = chatSessionService.getSessionParticipants(session.getId());
        User removedUser = userService.findById(removedUserId);

        if (removedUser != null) {
            Map<String, Object> notification = new HashMap<>();
            notification.put("type", "PARTICIPANT_REMOVED");
            notification.put("sessionId", session.getId());
            //notification.put("sessionName", session.getName());
            notification.put("removedUserId", removedUserId);
            notification.put("removedUsername", removedUser.getId());
            notification.put("removerUserId", removerUserId);

            for (User participant : participants) {
                messagingTemplate.convertAndSendToUser(
                        participant.getId(),
                        "/queue/notifications",
                        notification
                );
            }
        }
    }

    /**
     * 通知会话参与者会话已删除
     */
    private void notifySessionDeleted(ChatSession session, List<User> participants) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "SESSION_DELETED");
        notification.put("sessionId", session.getId());
        //notification.put("sessionName", session.getName());

        for (User participant : participants) {
            messagingTemplate.convertAndSendToUser(
                    participant.getId(),
                    "/queue/notifications",
                    notification
            );
        }
    }

    /**
     * 创建会话请求
     */
    @Data
    public static class CreateSessionRequest {
        private String name;
        private String description;
        private String type;
        private List<String> participantIds;
    }

    /**
     * 更新会话请求
     */
    @Data
    public static class UpdateSessionRequest {
        private String name;
        private String description;
    }

    /**
     * 添加参与者请求
     */
    @Data
    public static class AddParticipantRequest {
        private List<String> userIds;
    }
}