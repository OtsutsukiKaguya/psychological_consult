//package com.counseling.platform.repositories;
package com.example.demo.repositories;

//import com.counseling.platform.models.ChatSession;
import com.example.demo.models.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 聊天会话存储库接口
 */
@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {

    /**
     * 根据参与者ID查找会话
     */
    @Query("SELECT DISTINCT cs FROM ChatSession cs JOIN SessionParticipant sp ON cs.id = sp.sessionId WHERE sp.userId = :userId ORDER BY cs.lastActivityAt DESC")
    List<ChatSession> findByParticipantsUserId(@Param("userId") Long userId);

    /**
     * 根据类型查找会话
     */
    List<ChatSession> findByType(ChatSession.SessionType type);

    /**
     * 查找两个用户之间的一对一会话
     */
    @Query("SELECT cs FROM ChatSession cs " +
           "WHERE cs.type = 'ONE_TO_ONE' " +
           "AND EXISTS (SELECT 1 FROM SessionParticipant sp1 WHERE sp1.sessionId = cs.id AND sp1.userId = :userId1) " +
           "AND EXISTS (SELECT 1 FROM SessionParticipant sp2 WHERE sp2.sessionId = cs.id AND sp2.userId = :userId2) " +
           "AND (SELECT COUNT(sp) FROM SessionParticipant sp WHERE sp.sessionId = cs.id) = 2")
    ChatSession findOneToOneSession(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    /**
     * 根据创建时间范围查找会话
     */
    List<ChatSession> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    /**
     * 根据最后活动时间范围查找会话
     */
    List<ChatSession> findByLastActivityAtBetween(LocalDateTime start, LocalDateTime end);

    /**
     * 查找指定时间之后有活动的会话
     */
    List<ChatSession> findByLastActivityAtAfter(LocalDateTime time);

    /**
     * 根据会话ID列表查找最近的会话
     */
    @Query("SELECT cs FROM ChatSession cs WHERE cs.id IN :sessionIds ORDER BY cs.lastActivityAt DESC")
    List<ChatSession> findRecentSessionsByIds(@Param("sessionIds") List<Long> sessionIds);

    /**
     * 根据名称模糊查询会话
     */
    @Query("SELECT cs FROM ChatSession cs WHERE cs.name LIKE %:keyword% OR cs.description LIKE %:keyword%")
    List<ChatSession> findByKeyword(@Param("keyword") String keyword);

    /**
     * 获取用户参与的最近活跃的会话
     */
    @Query("SELECT cs FROM ChatSession cs JOIN SessionParticipant sp ON cs.id = sp.sessionId " +
           "WHERE sp.userId = :userId ORDER BY cs.lastActivityAt DESC")
    List<ChatSession> findRecentActiveSessionsByUserId(@Param("userId") Long userId, @Param("limit") int limit);
}