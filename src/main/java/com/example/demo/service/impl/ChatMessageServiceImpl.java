//package com.counseling.platform.services.impl;
package com.example.demo.service.impl;

//import com.counseling.platform.models.ChatMessage;
//import com.counseling.platform.models.MessageRead;
//import com.counseling.platform.repositories.ChatMessageRepository;
//import com.counseling.platform.repositories.MessageReadRepository;
//import com.counseling.platform.services.ChatMessageService;
//import com.counseling.platform.services.ChatSessionService;
import com.example.demo.models.ChatMessage;
import com.example.demo.models.MessageRead;
import com.example.demo.repositories.ChatMessageRepository;
import com.example.demo.repositories.MessageReadRepository;
import com.example.demo.service.ChatMessageService;
import com.example.demo.service.ChatSessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//import javax.persistence.EntityManager;
//import javax.persistence.PersistenceContext;
//import javax.persistence.Query;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 聊天消息服务实现类
 */
@Service
@Slf4j
public class ChatMessageServiceImpl implements ChatMessageService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;
    
    @Autowired
    private MessageReadRepository messageReadRepository;
    
    @Autowired
    private ChatSessionService chatSessionService;
    
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public ChatMessage createMessage(ChatMessage message) {
        // 设置发送时间
        if (message.getSentAt() == null) {
            message.setSentAt(LocalDateTime.now());
        }
        
        // 保存消息
        ChatMessage savedMessage = chatMessageRepository.save(message);
        
        // 更新会话最后活动时间
        chatSessionService.updateLastActivity(message.getSessionId());
        
        return savedMessage;
    }

    @Override
    @Transactional(readOnly = true)
    public ChatMessage findById(Long id) {
        return chatMessageRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessage> getSessionMessages(Long sessionId, int limit, int offset) {
        return chatMessageRepository.findBySessionIdOrderBySentAtDesc(
                sessionId, 
                PageRequest.of(offset / limit, limit));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessage> getSessionMessagesByType(Long sessionId, ChatMessage.MessageType type) {
        return chatMessageRepository.findBySessionIdAndTypeOrderBySentAtDesc(sessionId, type);
    }

    @Override
    @Transactional(readOnly = true)
    public ChatMessage getLastMessage(Long sessionId) {
        List<ChatMessage> messages = chatMessageRepository.findLastMessageBySessionId(
                sessionId, 
                PageRequest.of(0, 1));
        
        return messages.isEmpty() ? null : messages.get(0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessage> searchMessages(Long sessionId, String keyword) {
        return chatMessageRepository.searchMessagesByKeyword(sessionId, keyword);
    }

    @Override
    @Transactional
    public void markMessageAsRead(Long messageId, Long userId) {
        // 检查消息是否已读
        if (messageReadRepository.existsByMessageIdAndUserId(messageId, userId)) {
            return;
        }
        
        // 创建已读记录
        MessageRead messageRead = MessageRead.builder()
                .messageId(messageId)
                .userId(userId)
                .readAt(LocalDateTime.now())
                .build();
        
        messageReadRepository.save(messageRead);
    }

    @Override
    @Transactional
    public void markSessionMessagesAsRead(Long sessionId, Long userId) {
        // 使用原生SQL批量插入已读记录
        chatMessageRepository.markMessagesAsRead(sessionId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isMessageRead(Long messageId, Long userId) {
        return messageReadRepository.existsByMessageIdAndUserId(messageId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public int getUnreadCount(Long sessionId, Long userId) {
        return chatMessageRepository.countUnreadMessages(sessionId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessage> getAllUnreadMessages(Long userId) {
        // 使用自定义查询获取所有未读消息
        String jpql = "SELECT cm FROM ChatMessage cm WHERE cm.senderId != :userId " +
                      "AND NOT EXISTS (SELECT 1 FROM MessageRead mr WHERE mr.messageId = cm.id AND mr.userId = :userId) " +
                      "ORDER BY cm.sentAt DESC";
        
        return entityManager.createQuery(jpql, ChatMessage.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    @Override
    @Transactional
    public void deleteMessage(Long id) {
        // 删除消息的已读记录
        messageReadRepository.deleteByMessageId(id);
        
        // 删除消息
        chatMessageRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteSessionMessages(Long sessionId) {
        // 获取会话所有消息ID
        String jpql = "SELECT cm.id FROM ChatMessage cm WHERE cm.sessionId = :sessionId";
        List<Long> messageIds = entityManager.createQuery(jpql, Long.class)
                .setParameter("sessionId", sessionId)
                .getResultList();
        
        // 删除这些消息的已读记录
        if (!messageIds.isEmpty()) {
            String deleteMrJpql = "DELETE FROM MessageRead mr WHERE mr.messageId IN :messageIds";
            entityManager.createQuery(deleteMrJpql)
                    .setParameter("messageIds", messageIds)
                    .executeUpdate();
        }
        
        // 删除会话的所有消息
        String deleteCmJpql = "DELETE FROM ChatMessage cm WHERE cm.sessionId = :sessionId";
        entityManager.createQuery(deleteCmJpql)
                .setParameter("sessionId", sessionId)
                .executeUpdate();
    }
}