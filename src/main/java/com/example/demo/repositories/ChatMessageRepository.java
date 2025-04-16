//package com.counseling.platform.repositories;
package com.example.demo.repositories;

//import com.counseling.platform.models.ChatMessage;
import com.example.demo.models.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 聊天消息存储库接口
 */
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    /**
     * 根据会话ID查找消息
     */
    List<ChatMessage> findBySessionIdOrderBySentAtDesc(Long sessionId);

    /**
     * 根据会话ID分页查询消息
     */
    List<ChatMessage> findBySessionIdOrderBySentAtDesc(Long sessionId, Pageable pageable);

    /**
     * 根据发送者ID查找消息
     */
    List<ChatMessage> findBySenderIdOrderBySentAtDesc(Long senderId);

    /**
     * 根据会话ID和发送者ID查找消息
     */
    List<ChatMessage> findBySessionIdAndSenderIdOrderBySentAtDesc(Long sessionId, Long senderId);

    /**
     * 根据消息类型查询消息
     */
    List<ChatMessage> findByTypeOrderBySentAtDesc(ChatMessage.MessageType type);

    /**
     * 根据会话ID和消息类型查询消息
     */
    List<ChatMessage> findBySessionIdAndTypeOrderBySentAtDesc(Long sessionId, ChatMessage.MessageType type);

    /**
     * 根据时间范围查询消息
     */
    List<ChatMessage> findBySentAtBetweenOrderBySentAtDesc(LocalDateTime start, LocalDateTime end);

    /**
     * 根据文件ID查询消息
     */
    List<ChatMessage> findByFileIdOrderBySentAtDesc(Long fileId);

    /**
     * 查询未读消息数
     */
    @Query("SELECT COUNT(cm) FROM ChatMessage cm WHERE cm.sessionId = :sessionId AND cm.senderId != :userId AND NOT EXISTS " +
           "(SELECT 1 FROM MessageRead mr WHERE mr.messageId = cm.id AND mr.userId = :userId)")
    int countUnreadMessages(@Param("sessionId") Long sessionId, @Param("userId") Long userId);

    /**
     * 标记消息为已读
     */
    @Modifying
    @Query(value = "INSERT INTO message_read (message_id, user_id, read_at) " +
                   "SELECT cm.id, :userId, CURRENT_TIMESTAMP FROM chat_message cm " +
                   "WHERE cm.session_id = :sessionId AND cm.sender_id != :userId " +
                   "AND NOT EXISTS (SELECT 1 FROM message_read mr WHERE mr.message_id = cm.id AND mr.user_id = :userId)",
           nativeQuery = true)
    void markMessagesAsRead(@Param("sessionId") Long sessionId, @Param("userId") Long userId);

    /**
     * 根据关键字搜索消息
     */
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.sessionId = :sessionId AND cm.content LIKE %:keyword%")
    List<ChatMessage> searchMessagesByKeyword(@Param("sessionId") Long sessionId, @Param("keyword") String keyword);

    /**
     * 获取会话最后一条消息
     */
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.sessionId = :sessionId ORDER BY cm.sentAt DESC")
    List<ChatMessage> findLastMessageBySessionId(@Param("sessionId") Long sessionId, Pageable pageable);
}