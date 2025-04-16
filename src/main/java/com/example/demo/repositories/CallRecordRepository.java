//package com.counseling.platform.repositories;
package com.example.demo.repositories;

import com.example.demo.models.CallRecord;
//import com.counseling.platform.models.CallRecord;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通话记录存储库接口
 */
@Repository
public interface CallRecordRepository extends JpaRepository<CallRecord, Long> {

    /**
     * 根据会话ID查询通话记录
     */
    List<CallRecord> findBySessionIdOrderByStartTimeDesc(Long sessionId);

    /**
     * 根据呼叫者ID查询通话记录
     */
    List<CallRecord> findByCallerIdOrderByStartTimeDesc(Long callerId);

    /**
     * 根据被呼叫者ID查询通话记录
     */
    List<CallRecord> findByCalleeIdOrderByStartTimeDesc(Long calleeId);

    /**
     * 根据呼叫者ID或被呼叫者ID查询通话记录
     */
    @Query("SELECT cr FROM call_records cr WHERE cr.caller_id = :userId OR cr.callee_id = :userId ORDER BY cr.start_time DESC")
    List<CallRecord> findByCallerIdOrCalleeIdOrderByStartTimeDesc(@Param("userId") Long userId, @Param("userId") Long userId2);

    /**
     * 根据通话类型查询通话记录
     */
    List<CallRecord> findByTypeOrderByStartTimeDesc(CallRecord.CallType type);

    /**
     * 根据通话状态查询通话记录
     */
    List<CallRecord> findByStatusOrderByStartTimeDesc(CallRecord.CallStatus status);

    /**
     * 根据被呼叫者ID和通话状态查询通话记录
     */
    List<CallRecord> findByCalleeIdAndStatusOrderByStartTimeDesc(Long calleeId, CallRecord.CallStatus status);

    /**
     * 根据开始时间范围查询通话记录
     */
    List<CallRecord> findByStartTimeBetweenOrderByStartTimeDesc(LocalDateTime start, LocalDateTime end);

    /**
     * 查询两个用户之间的通话记录
     */
    @Query("SELECT cr FROM call_records cr WHERE " +
           "(cr.caller_id = :user1Id AND cr.callee_id = :user2Id) OR " +
           "(cr.caller_id = :user2Id AND cr.callee_id = :user1Id) " +
           "ORDER BY cr.start_time DESC")
    List<CallRecord> findBetweenUsers(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);

    /**
     * 根据用户ID查询最近的通话记录
     */
    @Query("SELECT cr FROM call_records cr WHERE cr.caller_id = :userId OR cr.callee_id = :userId ORDER BY cr.start_time DESC")
    List<CallRecord> findRecentCallsByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * 获取指定用户的未接来电数量
     */
    @Query("SELECT COUNT(cr) FROM call_records cr WHERE cr.callee_id = :userId AND cr.status = 'MISSED'")
    int countMissedCallsByUserId(@Param("userId") Long userId);

    /**
     * 获取用户在指定时间范围内的通话总时长
     */
    @Query("SELECT SUM(cr.duration) FROM call_records cr WHERE " +
           "(cr.caller_id = :userId OR cr.callee_id = :userId) AND " +
           "cr.status = 'COMPLETED' AND " +
           "cr.start_time BETWEEN :startDate AND :endDate")
    Integer getTotalCallDuration(@Param("userId") Long userId, 
                                @Param("startDate") LocalDateTime startDate,
                                @Param("endDate") LocalDateTime endDate);

    /**
     * 查找未结束的通话
     */
    @Query("SELECT cr FROM call_records cr WHERE " +
           "(cr.caller_id = :userId OR cr.callee_id = :userId) AND " +
           "cr.end_time IS NULL")
    List<CallRecord> findOngoingCalls(@Param("userId") Long userId);
}