//package com.counseling.platform.repositories;
package com.example.demo.repositories;

//import com.counseling.platform.models.MessageRead;
import com.example.demo.models.MessageRead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 消息阅读状态存储库接口
 */
@Repository
public interface MessageReadRepository extends JpaRepository<MessageRead, Long> {

    /**
     * 根据消息ID和用户ID查询阅读状态
     */
    MessageRead findByMessageIdAndUserId(Long messageId, Long userId);

    /**
     * 判断消息是否已被用户阅读
     */
    boolean existsByMessageIdAndUserId(Long messageId, Long userId);

    /**
     * 根据用户ID查询已读消息ID列表
     */
    @Query("SELECT mr.messageId FROM MessageRead mr WHERE mr.userId = :userId")
    List<Long> findReadMessageIdsByUserId(@Param("userId") Long userId);

    /**
     * 根据会话ID和用户ID查询已读消息ID列表
     */
    @Query("SELECT mr.messageId FROM MessageRead mr JOIN ChatMessage cm ON mr.messageId = cm.id " +
           "WHERE cm.sessionId = :sessionId AND mr.userId = :userId")
    List<Long> findReadMessageIdsBySessionIdAndUserId(
            @Param("sessionId") Long sessionId, 
            @Param("userId") Long userId);

    /**
     * 删除指定用户的所有阅读状态记录
     */
    void deleteByUserId(Long userId);

    /**
     * 删除指定消息的所有阅读状态记录
     */
    void deleteByMessageId(Long messageId);

    /**
     * 删除指定会话的所有阅读状态记录
     */
    @Query("DELETE FROM MessageRead mr WHERE mr.messageId IN " +
           "(SELECT cm.id FROM ChatMessage cm WHERE cm.sessionId = :sessionId)")
    void deleteBySessionId(@Param("sessionId") Long sessionId);
}