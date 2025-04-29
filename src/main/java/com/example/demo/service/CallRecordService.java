//package com.counseling.platform.services;
package com.example.demo.service;

//import com.counseling.platform.models.CallRecord;

import com.example.demo.models.CallRecord;

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
    CallRecord findById(Integer id);

    /**
     * 获取会话的通话记录
     */
    List<CallRecord> findBySessionId(String sessionId);

    /**
     * 获取用户发起的通话记录
     */
    List<CallRecord> findByCallerId(String callerId);

    /**
     * 获取用户接收的通话记录
     */
    List<CallRecord> findByCalleeId(String calleeId);

    /**
     * 获取用户的所有通话记录（发起和接收）
     */
    List<CallRecord> findByUserId(String userId);

    /**
     * 获取两个用户之间的通话记录
     */
    List<CallRecord> findBetweenUsers(String user1Id, String user2Id);

    /**
     * 更新通话状态
     */
    CallRecord updateCallStatus(Integer id, CallRecord.CallStatus status);

    /**
     * 结束通话
     */
    CallRecord endCall(Integer id);

    /**
     * 更新通话时长
     */
    CallRecord updateCallDuration(Integer id, int durationSeconds);

    /**
     * 获取最近的通话记录
     */
    List<CallRecord> getRecentCalls(String userId, int limit);

    /**
     * 获取未接来电
     */
    List<CallRecord> getMissedCalls(String userId);

    /**
     * 删除通话记录
     */
    void deleteCallRecord(Integer id);

    //这是我为了先解决报错随便创建的save方法，后面应该要改
    CallRecord save(CallRecord callRecord);
}