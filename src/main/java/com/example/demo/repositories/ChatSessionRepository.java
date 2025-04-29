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
import java.util.Optional;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, String> {  // 修改主键类型为 String

    // 根据参与者ID查找会话
    @Query("SELECT DISTINCT cs FROM ChatSession cs JOIN SessionParticipant sp ON cs.id = sp.session.id WHERE sp.user.id = :userId ORDER BY cs.updatedAt DESC")
    List<ChatSession> findByParticipantsUserId(@Param("userId") String userId);  // 修改参与者和用户ID为 String 类型

//    // 根据会话 ID 查询并同时加载参与者和用户信息
//    @Query("""
//    SELECT cs FROM ChatSession cs
//    LEFT JOIN FETCH cs.participants p
//    LEFT JOIN FETCH p.user
//    WHERE cs.id = :id
//    """)
//    ChatSession findByIdWithParticipantsAndUsers(@Param("id") String id);

//    @Query("""
//    SELECT DISTINCT cs FROM ChatSession cs
//    LEFT JOIN FETCH cs.participants p
//    LEFT JOIN FETCH p.user
//    WHERE cs.id = :id
//""")
//    Optional<ChatSession> findByIdWithParticipantsAndUsers(@Param("id") String id);

    // 根据类型查找会话
    List<ChatSession> findByType(ChatSession.SessionType type);  // 无需修改

    // 查找两个用户之间的一对一会话
    @Query("SELECT cs FROM ChatSession cs " +
            "WHERE cs.type = 'ONE_TO_ONE' " +
            "AND EXISTS (SELECT 1 FROM SessionParticipant sp1 WHERE sp1.session.id = cs.id AND sp1.user.id = :userId1) " +
            "AND EXISTS (SELECT 1 FROM SessionParticipant sp2 WHERE sp2.session.id = cs.id AND sp2.user.id = :userId2) " +
            "AND (SELECT COUNT(sp) FROM SessionParticipant sp WHERE sp.session.id = cs.id) = 2")
    ChatSession findOneToOneSession(@Param("userId1") String userId1, @Param("userId2") String userId2);  // 修改用户ID为 String 类型

    // 根据创建时间范围查找会话
    List<ChatSession> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);  // 无需修改

    // 根据最后活动时间范围查找会话
    List<ChatSession> findByUpdatedAtBetween(LocalDateTime start, LocalDateTime end);  // 无需修改

    // 查找指定时间之后有活动的会话
    @Query("SELECT cs FROM ChatSession cs WHERE cs.updatedAt > :date")
    List<ChatSession> findByUpdatedAtAfterpdatedAtAfter(@Param("date") LocalDateTime date);

    // 根据会话ID列表查找最近的会话
    @Query("SELECT cs FROM ChatSession cs WHERE cs.id IN :sessionIds ORDER BY cs.updatedAt DESC")
    List<ChatSession> findRecentSessionsByIds(@Param("sessionIds") List<String> sessionIds);  // 修改会话ID为 String 类型

    // 根据名称模糊查询会话
    //@Query("SELECT cs FROM ChatSession cs WHERE cs.name LIKE %:keyword% OR cs.description LIKE %:keyword%")
    //List<ChatSession> findByKeyword(@Param("keyword") String keyword);  // 无需修改

    // 获取用户参与的最近活跃的会话
    @Query("SELECT cs FROM ChatSession cs JOIN SessionParticipant sp ON cs.id = sp.session.id " +
            "WHERE sp.user.id = :userId ORDER BY cs.updatedAt DESC")
    List<ChatSession> findRecentActiveSessionsByUserId(@Param("userId") String userId);  // 修改用户ID为 String 类型
}
