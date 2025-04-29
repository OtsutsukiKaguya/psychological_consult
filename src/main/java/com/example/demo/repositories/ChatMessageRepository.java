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

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {  // 修改主键类型为 Integer

    // 根据会话ID查找消息
    List<ChatMessage> findBySessionIdOrderBySentAtDesc(String sessionId);  // 修改会话ID为 String 类型

    // 根据会话ID分页查询消息
    List<ChatMessage> findBySessionIdOrderBySentAtDesc(String sessionId, Pageable pageable);  // 修改会话ID为 String 类型

    // 根据发送者ID查找消息
    List<ChatMessage> findBySenderIdOrderBySentAtDesc(String senderId);  // 修改发送者ID为 String 类型

    // 根据会话ID和发送者ID查找消息
    List<ChatMessage> findBySessionIdAndSenderIdOrderBySentAtDesc(String sessionId, String senderId);  // 修改会话ID和发送者ID为 String 类型

    // 根据消息类型查询消息
    List<ChatMessage> findByTypeOrderBySentAtDesc(ChatMessage.MessageType type);  // 无需修改

    // 根据会话ID和消息类型查询消息
    List<ChatMessage> findBySessionIdAndTypeOrderBySentAtDesc(String sessionId, ChatMessage.MessageType type);  // 修改会话ID为 String 类型

    // 根据时间范围查询消息
    List<ChatMessage> findBySentAtBetweenOrderBySentAtDesc(LocalDateTime start, LocalDateTime end);  // 无需修改

    // 根据文件ID查询消息
    List<ChatMessage> findByFileIdOrderBySentAtDesc(Integer fileId);  // 修改文件ID为 Integer 类型

    // 查询未读消息数
    @Query("SELECT COUNT(cm) FROM ChatMessage cm WHERE cm.session.id = :sessionId AND cm.sender.id != :userId AND NOT EXISTS " +
            "(SELECT 1 FROM MessageRead mr WHERE mr.messageId = cm.id AND mr.userId = :userId)")
    int countUnreadMessages(@Param("sessionId") String sessionId, @Param("userId") String userId);  // 修改会话ID、发送者ID为 String 类型

    // 标记消息为已读
//    @Modifying
//    @Query(value = "INSERT INTO message_read (message_id, user_id, read_at) " +
//            "SELECT cm.id, :userId, CURRENT_TIMESTAMP FROM ChatMessage cm " +
//            "WHERE cm.session.id = :sessionId AND cm.sender.id != :userId " +
//            "AND NOT EXISTS (SELECT 1 FROM message_read mr WHERE mr.message_id = cm.id AND mr.user_id = :userId)",
//            nativeQuery = true)
//    void markMessagesAsRead(@Param("sessionId") String sessionId, @Param("userId") String userId);  // 修改会话ID、发送者ID为 String 类型
    @Modifying
    @Query(value = """
    INSERT INTO message_read (message_id, user_id, read_at)
    SELECT cm.id, :userId, CURRENT_TIMESTAMP
    FROM chat_messages cm
    WHERE cm.session_id = :sessionId
      AND cm.sender_id != :userId
      AND NOT EXISTS (
        SELECT 1 FROM message_read mr
        WHERE mr.message_id = cm.id
          AND mr.user_id = :userId
    )
    """, nativeQuery = true)
    void markMessagesAsRead(@Param("sessionId") String sessionId, @Param("userId") String userId);


    // 根据关键字搜索消息
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.session.id = :sessionId AND cm.content LIKE %:keyword%")
    List<ChatMessage> searchMessagesByKeyword(@Param("sessionId") String sessionId, @Param("keyword") String keyword);  // 修改会话ID为 String 类型

    // 获取会话最后一条消息
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.session.id = :sessionId ORDER BY cm.sentAt DESC")
    List<ChatMessage> findLastMessageBySessionId(@Param("sessionId") String sessionId, Pageable pageable);  // 修改会话ID为 String 类型

    // 根据会话 ID 及时间范围查询消息（按时间倒序）
    List<ChatMessage> findBySessionIdAndSentAtBetweenOrderBySentAtDesc(
            String sessionId,
            LocalDateTime start,
            LocalDateTime end);
}

