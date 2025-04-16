////package com.counseling.platform.repositories;
//package com.example.demo.repositories;
//
////import com.counseling.platform.models.SessionParticipant;
////import com.counseling.platform.models.User;
//import com.example.demo.models.SessionParticipant;
//import com.example.demo.models.User;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Optional;
//
///**
// * 会话参与者存储库接口
// */
//@Repository
//public interface SessionParticipantRepository extends JpaRepository<SessionParticipant, Long> {
//
//    /**
//     * 根据会话ID查找参与者
//     */
//    List<SessionParticipant> findBySessionId(Long sessionId);
//
//    /**
//     * 根据用户ID查找参与的会话
//     */
//    List<SessionParticipant> findByUserId(Long userId);
//
//    /**
//     * 根据会话ID和用户ID查找参与者
//     */
//    Optional<SessionParticipant> findBySessionIdAndUserId(Long sessionId, Long userId);
//
//    /**
//     * 检查用户是否是会话的参与者
//     */
//    boolean existsBySessionIdAndUserId(Long sessionId, Long userId);
//
//    /**
//     * 根据角色查找参与者
//     */
//    List<SessionParticipant> findByRole(SessionParticipant.ParticipantRole role);
//
//    /**
//     * 根据会话ID和角色查找参与者
//     */
//    List<SessionParticipant> findBySessionIdAndRole(Long sessionId, SessionParticipant.ParticipantRole role);
//
//    /**
//     * 根据用户ID和角色查找参与的会话
//     */
//    List<SessionParticipant> findByUserIdAndRole(Long userId, SessionParticipant.ParticipantRole role);
//
//    /**
//     * 查找指定会话的用户
//     */
//    @Query("SELECT u FROM User u JOIN SessionParticipant sp ON u.id = sp.userId WHERE sp.sessionId = :sessionId")
//    List<User> findUsersBySessionId(@Param("sessionId") Long sessionId);
//
//    /**
//     * 根据会话ID和角色查找用户
//     */
//    @Query("SELECT u FROM User u JOIN SessionParticipant sp ON u.id = sp.userId WHERE sp.sessionId = :sessionId AND sp.role = :role")
//    List<User> findUsersBySessionIdAndRole(@Param("sessionId") Long sessionId, @Param("role") SessionParticipant.ParticipantRole role);
//
//    /**
//     * 删除会话参与者
//     */
//    void deleteBySessionIdAndUserId(Long sessionId, Long userId);
//
//    /**
//     * 删除会话的所有参与者
//     */
//    void deleteBySessionId(Long sessionId);
//}

package com.example.demo.repositories;

import com.example.demo.models.SessionParticipant;
import com.example.demo.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 会话参与者数据访问接口
 * 用于管理用户与 ChatSession 的关联关系
 */
@Repository
public interface SessionParticipantRepository extends JpaRepository<SessionParticipant, Integer> {

    /**
     * 根据会话 ID 查询该会话的所有参与者
     */
    List<SessionParticipant> findBySessionId(String sessionId);

    /**
     * 根据用户 ID 查询用户参与的所有会话
     */
    List<SessionParticipant> findByUserId(String userId);

    /**
     * 查询某个用户是否参与了指定会话
     */
    Optional<SessionParticipant> findBySessionIdAndUserId(String sessionId, String userId);

    /**
     * 判断用户是否是指定会话的参与者
     */
    boolean existsBySessionIdAndUserId(String sessionId, String userId);

    /**
     * 查询所有指定角色的参与者
     */
    List<SessionParticipant> findByRole(SessionParticipant.ParticipantRole role);

    /**
     * 查询指定会话中指定角色的参与者
     */
    List<SessionParticipant> findBySessionIdAndRole(String sessionId, SessionParticipant.ParticipantRole role);

    /**
     * 查询某用户担任指定角色的所有会话参与记录
     */
    List<SessionParticipant> findByUserIdAndRole(String userId, SessionParticipant.ParticipantRole role);

    /**
     * 查询指定会话中的所有参与用户（User 实体）
     */
    @Query("SELECT u FROM SessionParticipant sp JOIN sp.user u WHERE sp.session.id = :sessionId")
    List<User> findUsersBySessionId(@Param("sessionId") String sessionId);

    /**
     * 查询指定会话中具有指定角色的所有用户
     */
    @Query("SELECT u FROM SessionParticipant sp JOIN sp.user u WHERE sp.session.id = :sessionId AND sp.role = :role")
    List<User> findUsersBySessionIdAndRole(@Param("sessionId") String sessionId, @Param("role") SessionParticipant.ParticipantRole role);

    /**
     * 删除指定用户在指定会话中的参与记录
     */
    void deleteBySessionIdAndUserId(String sessionId, String userId);

    /**
     * 删除指定会话的所有参与者
     */
    void deleteBySessionId(String sessionId);
}
