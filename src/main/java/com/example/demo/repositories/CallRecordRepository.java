//package com.counseling.platform.repositories;
package com.example.demo.repositories;

import com.example.demo.models.CallRecord;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

///**
// * 通话记录存储库接口
// */
//@Repository
//public interface CallRecordRepository extends JpaRepository<CallRecord, Long> {
//
//    /**
//     * 根据会话ID查询通话记录
//     */
//    List<CallRecord> findBySessionIdOrderByStartTimeDesc(Long sessionId);
//
//    /**
//     * 根据呼叫者ID查询通话记录
//     */
//    List<CallRecord> findByCallerIdOrderByStartTimeDesc(Long callerId);
//
//    /**
//     * 根据被呼叫者ID查询通话记录
//     */
//    List<CallRecord> findByCalleeIdOrderByStartTimeDesc(Long calleeId);
//
//    /**
//     * 根据呼叫者ID或被呼叫者ID查询通话记录
//     */
//    @Query("SELECT cr FROM call_records cr WHERE cr.caller_id = :userId OR cr.callee_id = :userId ORDER BY cr.start_time DESC")
//    List<CallRecord> findByCallerIdOrCalleeIdOrderByStartTimeDesc(@Param("userId") Long userId, @Param("userId") Long userId2);
//
//    /**
//     * 根据通话类型查询通话记录
//     */
//    List<CallRecord> findByTypeOrderByStartTimeDesc(CallRecord.CallType type);
//
//    /**
//     * 根据通话状态查询通话记录
//     */
//    List<CallRecord> findByStatusOrderByStartTimeDesc(CallRecord.CallStatus status);
//
//    /**
//     * 根据被呼叫者ID和通话状态查询通话记录
//     */
//    List<CallRecord> findByCalleeIdAndStatusOrderByStartTimeDesc(Long calleeId, CallRecord.CallStatus status);
//
//    /**
//     * 根据开始时间范围查询通话记录
//     */
//    List<CallRecord> findByStartTimeBetweenOrderByStartTimeDesc(LocalDateTime start, LocalDateTime end);
//
//    /**
//     * 查询两个用户之间的通话记录
//     */
//    @Query("SELECT cr FROM call_records cr WHERE " +
//           "(cr.caller_id = :user1Id AND cr.callee_id = :user2Id) OR " +
//           "(cr.caller_id = :user2Id AND cr.callee_id = :user1Id) " +
//           "ORDER BY cr.start_time DESC")
//    List<CallRecord> findBetweenUsers(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);
//
//    /**
//     * 根据用户ID查询最近的通话记录
//     */
//    @Query("SELECT cr FROM call_records cr WHERE cr.caller_id = :userId OR cr.callee_id = :userId ORDER BY cr.start_time DESC")
//    List<CallRecord> findRecentCallsByUserId(@Param("userId") Long userId, Pageable pageable);
//
//    /**
//     * 获取指定用户的未接来电数量
//     */
//    @Query("SELECT COUNT(cr) FROM call_records cr WHERE cr.callee_id = :userId AND cr.status = 'MISSED'")
//    int countMissedCallsByUserId(@Param("userId") Long userId);
//
//    /**
//     * 获取用户在指定时间范围内的通话总时长
//     */
//    @Query("SELECT SUM(cr.duration) FROM call_records cr WHERE " +
//           "(cr.caller_id = :userId OR cr.callee_id = :userId) AND " +
//           "cr.status = 'COMPLETED' AND " +
//           "cr.start_time BETWEEN :startDate AND :endDate")
//    Integer getTotalCallDuration(@Param("userId") Long userId,
//                                @Param("startDate") LocalDateTime startDate,
//                                @Param("endDate") LocalDateTime endDate);
//
//    /**
//     * 查找未结束的通话
//     */
//    @Query("SELECT cr FROM call_records cr WHERE " +
//           "(cr.caller_id = :userId OR cr.callee_id = :userId) AND " +
//           "cr.end_time IS NULL")
//    List<CallRecord> findOngoingCalls(@Param("userId") Long userId);
//}

/**
 * 通话记录数据访问接口，提供对 CallRecord 实体的 CRUD 和自定义查询操作
 */
@Repository
public interface CallRecordRepository extends JpaRepository<CallRecord, Integer> {

    /**
     * 根据会话 ID 查询该会话下的所有通话记录，按开始时间倒序排列
     */
    List<CallRecord> findBySessionIdOrderByStartTimeDesc(String sessionId);

    /**
     * 查询用户作为主叫方发起的所有通话记录，按开始时间倒序排列
     */
    List<CallRecord> findByCallerIdOrderByStartTimeDesc(String callerId);

    /**
     * 查询用户作为被叫方接收的所有通话记录，按开始时间倒序排列
     */
    List<CallRecord> findByCalleeIdOrderByStartTimeDesc(String calleeId);

    /**
     * 查询用户参与的所有通话记录（无论主叫还是被叫），按开始时间倒序排列
     */
    @Query("SELECT cr FROM CallRecord cr WHERE cr.caller.id = :userId OR cr.callee.id = :userId ORDER BY cr.startTime DESC")
    List<CallRecord> findByCallerIdOrCalleeIdOrderByStartTimeDesc(@Param("userId") String userId);

    /**
     * 查询所有指定类型的通话（音频或视频），按开始时间倒序排列
     */
    List<CallRecord> findByCallTypeOrderByStartTimeDesc(CallRecord.CallType callType);

    /**
     * 查询所有指定状态的通话（如 MISSED, COMPLETED, REJECTED），按开始时间倒序排列
     */
    List<CallRecord> findByStatusOrderByStartTimeDesc(CallRecord.CallStatus status);

    /**
     * 查询某用户的所有指定状态通话（如该用户所有未接来电），按开始时间倒序排列
     */
    List<CallRecord> findByCalleeIdAndStatusOrderByStartTimeDesc(String calleeId, CallRecord.CallStatus status);

    /**
     * 查询指定时间范围内的所有通话记录，按开始时间倒序排列
     */
    List<CallRecord> findByStartTimeBetweenOrderByStartTimeDesc(LocalDateTime start, LocalDateTime end);

    /**
     * 查询两个用户之间的通话记录（无论谁主叫），按开始时间倒序排列
     */
    @Query("SELECT cr FROM CallRecord cr WHERE " +
            "(cr.caller.id = :user1Id AND cr.callee.id = :user2Id) OR " +
            "(cr.caller.id = :user2Id AND cr.callee.id = :user1Id) " +
            "ORDER BY cr.startTime DESC")
    List<CallRecord> findBetweenUsers(@Param("user1Id") String user1Id, @Param("user2Id") String user2Id);

    /**
     * 查询指定用户最近的通话记录（作为主叫或被叫），按开始时间倒序排列，支持分页
     */
    @Query("SELECT cr FROM CallRecord cr WHERE cr.caller.id = :userId OR cr.callee.id = :userId ORDER BY cr.startTime DESC")
    List<CallRecord> findRecentCallsByUserId(@Param("userId") String userId, Pageable pageable); //要不就改这里数据库查询的语句

    /**
     * 统计某用户的未接来电数量（作为被叫，状态为 MISSED）
     */
    @Query("SELECT COUNT(cr) FROM CallRecord cr WHERE cr.callee.id = :userId AND cr.status = 'MISSED'")
    int countMissedCallsByUserId(@Param("userId") String userId);

    /**
     * 统计某用户在指定时间范围内的通话总时长（单位：秒，仅统计状态为 COMPLETED 的通话）
     */
    @Query("SELECT SUM(cr.durationSeconds) FROM CallRecord cr WHERE " +
            "(cr.caller.id = :userId OR cr.callee.id = :userId) AND " +
            "cr.status = 'COMPLETED' AND " +
            "cr.startTime BETWEEN :startDate AND :endDate")
    Integer getTotalCallDuration(@Param("userId") String userId,
                                 @Param("startDate") LocalDateTime startDate,
                                 @Param("endDate") LocalDateTime endDate);

    /**
     * 查询用户尚未结束的通话记录（endTime 为空）
     */
    @Query("SELECT cr FROM CallRecord cr WHERE " +
            "(cr.caller.id = :userId OR cr.callee.id = :userId) AND cr.endTime IS NULL")
    List<CallRecord> findOngoingCalls(@Param("userId") String userId);
}
