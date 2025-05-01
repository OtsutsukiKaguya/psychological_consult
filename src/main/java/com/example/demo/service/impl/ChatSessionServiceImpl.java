//package com.counseling.platform.services.impl;
package com.example.demo.service.impl;

import com.example.demo.models.ChatSession;
import com.example.demo.models.SessionParticipant;
import com.example.demo.models.User;
import com.example.demo.repositories.ChatMessageRepository;
import com.example.demo.repositories.ChatSessionRepository;
import com.example.demo.repositories.SessionParticipantRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.service.ChatSessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 聊天会话服务实现类
 */
@Service
@Slf4j
public class ChatSessionServiceImpl implements ChatSessionService {

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    @Autowired
    private SessionParticipantRepository sessionParticipantRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private UserRepository userRepository;

//    @Override
//    @Transactional
//    public ChatSession createSession(ChatSession session) {
//        // 设置创建时间和最后活动时间
//        if (session.getCreatedAt() == null) {
//            session.setCreatedAt(LocalDateTime.now());
//        }
////        session.setLastActivityAt(LocalDateTime.now());
//        session.setUpdatedAt(LocalDateTime.now());
//
//        return chatSessionRepository.save(session);
//    }

    @Override
    @Transactional
    public ChatSession createSession(ChatSession session) {
        if (session.getCreatedAt() == null) {
            session.setCreatedAt(LocalDateTime.now());
        }
        session.setUpdatedAt(LocalDateTime.now());

        ChatSession saved = chatSessionRepository.save(session);

        // ✅ 加入日志：确认成功插入会话记录
        log.info("✅ 新会话已成功保存到数据库，ID = {}, 类型 = {}, 创建时间 = {}",
                saved.getId(), saved.getType(), saved.getCreatedAt());

        return saved;
    }


    @Override
    @Transactional(readOnly = true)
    public ChatSession findById(String id) {
        return chatSessionRepository.findById(id).orElse(null);
    }

//    @Override
//    @Transactional(readOnly = true)
//    public ChatSession findByIdWithParticipants(String id) {
////        return chatSessionRepository.findByIdWithParticipantsAndUsers(id);
//        log.info("✅ ✅ 正在调用 fetch join 查询会话及参与者...");
//        return chatSessionRepository.findByIdWithParticipantsAndUsers(id).orElse(null);
//    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatSession> findAll() {
        return chatSessionRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatSession> findByUserId(String userId) {
        return chatSessionRepository.findByParticipantsUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public ChatSession getOneToOneSession(String userId1, String userId2) {
        return chatSessionRepository.findOneToOneSession(userId1, userId2);
    }

    @Override
    @Transactional
    public ChatSession updateSession(String id, ChatSession session) {
        ChatSession existingSession = chatSessionRepository.findById(id).orElse(null);
        if (existingSession == null) {
            return null;
        }
        
        /* 更新会话信息
        if (session.getName() != null) {
            existingSession.setName(session.getName());
        }
        
        if (session.getDescription() != null) {
            existingSession.setDescription(session.getDescription());
        }
        */

//        existingSession.setLastActivityAt(LocalDateTime.now());
        existingSession.setUpdatedAt(LocalDateTime.now());

        return chatSessionRepository.save(existingSession);
    }

    @Override
    @Transactional
    public ChatSession updateSession(ChatSession session) {
        // 直接保存传入的 session（适用于 Controller 手动设置好字段）
        return chatSessionRepository.save(session);
    }

    @Override
    @Transactional
    public void deleteSession(String id) {
        // 检查会话是否存在
        if (!chatSessionRepository.existsById(id)) {
            log.error("Session not found: {}", id);
            throw new IllegalArgumentException("Session not found");
        }

        // 删除会话（这将级联删除会话参与者和消息）
        chatSessionRepository.deleteById(id);
    }

    @Override
    @Transactional
    public SessionParticipant addParticipant(String sessionId, String userId, SessionParticipant.ParticipantRole role) {
        // 1.检查会话是否存在
        ChatSession session = chatSessionRepository.findById(sessionId).orElse(null);
        if (session == null) {
            log.error("Session not found: {}", sessionId);
            throw new IllegalArgumentException("Session not found");
        }

        // 2. 检查用户是否存在
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found: {}", userId);
                    return new IllegalArgumentException("User not found");
                });

        // 3.检查用户是否已经是会话参与者
        Optional<SessionParticipant> existingParticipant = sessionParticipantRepository.findBySessionIdAndUserId(sessionId, userId);
        if (existingParticipant.isPresent()) {
            log.debug("User {} is already a participant of session {}", userId, sessionId);
            return existingParticipant.get();
        }

        // 创建新参与者
//        SessionParticipant participant = SessionParticipant.builder()
////                .sessionId(sessionId)
//                .session()
//                .userId(userId)
//                .role(role)
//                .joinedAt(LocalDateTime.now())
//                .build();
        // 4. 创建新参与者
        SessionParticipant participant = SessionParticipant.builder()
                .session(session)   // ✅ 设置 ChatSession 实体
                .user(user)         // ✅ 设置 User 实体
                .role(role)
                .joinedAt(LocalDateTime.now())
                .build();

        // 更新会话最后活动时间
//        session.setLastActivityAt(LocalDateTime.now());
        session.setUpdatedAt(LocalDateTime.now());
        chatSessionRepository.save(session);

        return sessionParticipantRepository.save(participant);
    }

    @Override
    @Transactional
    public void removeParticipant(String sessionId, String userId) {
        // 检查会话是否存在
        ChatSession session = chatSessionRepository.findById(sessionId).orElse(null);
        if (session == null) {
            log.error("Session not found: {}", sessionId);
            throw new IllegalArgumentException("Session not found");
        }

        // 检查参与者是否存在
        Optional<SessionParticipant> participant = sessionParticipantRepository.findBySessionIdAndUserId(sessionId, userId);
        if (!participant.isPresent()) {
            log.error("Participant not found: session={}, user={}", sessionId, userId);
            throw new IllegalArgumentException("Participant not found");
        }

        // 删除参与者
        sessionParticipantRepository.delete(participant.get());

        // 更新会话最后活动时间
//        session.setLastActivityAt(LocalDateTime.now());
        session.setUpdatedAt(LocalDateTime.now());
        chatSessionRepository.save(session);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getSessionParticipants(String sessionId) {
        // 检查会话是否存在
        if (!chatSessionRepository.existsById(sessionId)) {
            log.error("Session not found: {}", sessionId);
            throw new IllegalArgumentException("Session not found");
        }

        // 获取会话参与者用户
        return sessionParticipantRepository.findUsersBySessionId(sessionId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SessionParticipant> getSessionParticipantsWithRoles(String sessionId) {
        // 检查会话是否存在
        if (!chatSessionRepository.existsById(sessionId)) {
            log.error("Session not found: {}", sessionId);
            throw new IllegalArgumentException("Session not found");
        }

        // 获取会话参与者
        return sessionParticipantRepository.findBySessionId(sessionId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isSessionParticipant(String sessionId, String userId) {
        return sessionParticipantRepository.existsBySessionIdAndUserId(sessionId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public SessionParticipant.ParticipantRole getUserRoleInSession(String sessionId, String userId) {
        Optional<SessionParticipant> participant = sessionParticipantRepository.findBySessionIdAndUserId(sessionId, userId);
        return participant.map(SessionParticipant::getRole).orElse(null);
    }

    @Override
    @Transactional
    public void markSessionAsRead(String sessionId, String userId) {
        // 检查用户是否是会话参与者
        if (!isSessionParticipant(sessionId, userId)) {
            log.error("User {} is not a participant of session {}", userId, sessionId);
            throw new IllegalArgumentException("User is not a participant of this session");
        }

        // 标记会话消息为已读
        chatMessageRepository.markMessagesAsRead(sessionId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatSession> getRecentSessions(String userId) {
        // 获取用户参与的所有会话ID
        List<String> sessionIds = sessionParticipantRepository.findByUserId(userId)
                .stream()
//                .map(SessionParticipant::getSessionId)
                .map(p -> p.getSession().getId())//我修改了getSessionId的方法
                .collect(Collectors.toList());

        if (sessionIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 按最后活动时间排序获取会话
        return chatSessionRepository.findRecentSessionsByIds(sessionIds);
    }

    @Override
    @Transactional(readOnly = true)
    public int getUnreadMessageCount(String sessionId, String userId) {
        // 检查用户是否是会话参与者
        if (!isSessionParticipant(sessionId, userId)) {
            log.error("User {} is not a participant of session {}", userId, sessionId);
            throw new IllegalArgumentException("User is not a participant of this session");
        }

        // 获取未读消息数
        return chatMessageRepository.countUnreadMessages(sessionId, userId);
    }

    @Override
    @Transactional
    public void updateLastActivity(String sessionId) {
        ChatSession session = chatSessionRepository.findById(sessionId).orElse(null);
        if (session == null) {
            log.error("Session not found: {}", sessionId);
            throw new IllegalArgumentException("Session not found");
        }

//        session.setLastActivityAt(LocalDateTime.now());
        session.setUpdatedAt(LocalDateTime.now());
        chatSessionRepository.save(session);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatSession> findAllByOrderByUpdatedAtDesc() {
        return chatSessionRepository.findAllByOrderByUpdatedAtDesc();
    }

    // 在 ChatSessionServiceImpl.java 中实现方法
    @Override
    @Transactional(readOnly = true)
    public boolean hasActiveSessionForUser(String userId) {
        return chatSessionRepository.countActiveSessionsForUser(userId) > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public int countActiveSessionsForCounselor(String userId) {
        List<SessionParticipant.ParticipantRole> roles = List.of(
                SessionParticipant.ParticipantRole.COUNSELOR,
                SessionParticipant.ParticipantRole.TUTOR
        );
        return chatSessionRepository.countActiveSessionsForRoles(userId, roles);
    }


}