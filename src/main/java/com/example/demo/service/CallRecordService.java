//package com.counseling.platform.services;
package com.example.demo.service;

//import com.counseling.platform.models.CallRecord;
import com.example.demo.models.CallRecord;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通话记录服务接口
 */
public interface CallRecordService {

    /**
     * 创建通话记录
     */
    CallRecord createCallRecord(CallRecord callRecord);

    /**
     * 根据ID查找通话记录
     */
    CallRecord findById(Long id);

    /**
     * 获取会话的通话记录
     */
    List<CallRecord> findBySessionId(Long sessionId);

    /**
     * 获取用户发起的通话记录
     */
    List<CallRecord> findByCallerId(Long callerId);

    /**
     * 获取用户接收的通话记录
     */
    List<CallRecord> findByCalleeId(Long calleeId);

    /**
     * 获取用户的所有通话记录（发起和接收）
     */
    List<CallRecord> findByUserId(Long userId);

    /**
     * 获取两个用户之间的通话记录
     */
    List<CallRecord> findBetweenUsers(Long user1Id, Long user2Id);

    /**
     * 更新通话状态
     */
    CallRecord updateCallStatus(Long id, CallRecord.CallStatus status);

    /**
     * 结束通话
     */
    CallRecord endCall(Long id);

    /**
     * 更新通话时长
     */
    CallRecord updateCallDuration(Long id, int durationSeconds);

    /**
     * 获取最近的通话记录
     */
    List<CallRecord> getRecentCalls(Long userId, int limit);

    /**
     * 获取未接来电
     */
    List<CallRecord> getMissedCalls(Long userId);

    /**
     * 删除通话记录
     */
    void deleteCallRecord(Long id);
}