//package com.counseling.platform.services.impl;
package com.example.demo.service.impl;

import com.example.demo.models.CallRecord;
import com.example.demo.repositories.CallRecordRepository;
import com.example.demo.service.CallRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 通话记录服务实现类
 */
@Service
@Slf4j
public class CallRecordServiceImpl implements CallRecordService {

    @Autowired
    private CallRecordRepository callRecordRepository;

    @Override
    @Transactional
    public CallRecord createCallRecord(CallRecord callRecord) {
        // 设置开始时间
        if (callRecord.getStartTime() == null) {
            callRecord.setStartTime(LocalDateTime.now());
        }
        
        return callRecordRepository.save(callRecord);
    }

    @Override
    @Transactional(readOnly = true)
    public CallRecord findById(Integer id) {
        return callRecordRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CallRecord> findBySessionId(String sessionId) {
        return callRecordRepository.findBySessionIdOrderByStartTimeDesc(sessionId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CallRecord> findByCallerId(String callerId) {
        return callRecordRepository.findByCallerIdOrderByStartTimeDesc(callerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CallRecord> findByCalleeId(String calleeId) {
        return callRecordRepository.findByCalleeIdOrderByStartTimeDesc(calleeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CallRecord> findByUserId(String userId) {
        return callRecordRepository.findByCallerIdOrCalleeIdOrderByStartTimeDesc(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CallRecord> findBetweenUsers(String user1Id, String user2Id) {
        return callRecordRepository.findBetweenUsers(user1Id, user2Id);
    }

    @Override
    @Transactional
    public CallRecord updateCallStatus(Integer id, CallRecord.CallStatus status) {
        CallRecord callRecord = callRecordRepository.findById(id).orElse(null);
        if (callRecord == null) {
            log.error("Call record not found: {}", id);
            throw new IllegalArgumentException("Call record not found");
        }
        
        callRecord.setStatus(status);
        
        // 如果接听了，更新接听时间
        if (status == CallRecord.CallStatus.COMPLETED && callRecord.getStartTime() == null) {
            callRecord.setStartTime(LocalDateTime.now());
        }
        
        return callRecordRepository.save(callRecord);
    }

    @Override
    @Transactional
    public CallRecord endCall(Integer id) {
        CallRecord callRecord = callRecordRepository.findById(id).orElse(null);
        if (callRecord == null) {
            log.error("Call record not found: {}", id);
            throw new IllegalArgumentException("Call record not found");
        }
        
        // 设置结束时间
        LocalDateTime endTime = LocalDateTime.now();
        callRecord.setEndTime(endTime);
        
        // 计算通话时长（如果已接听）
        if (callRecord.getStartTime() != null) {
            Duration duration = Duration.between(callRecord.getStartTime(), endTime);
            callRecord.setDurationSeconds((int) duration.getSeconds());
            
            // 确保通话状态为已完成
            callRecord.setStatus(CallRecord.CallStatus.COMPLETED);
        } else {
            // 如果未接听，则设置为未接
            callRecord.setStatus(CallRecord.CallStatus.MISSED);
        }
        
        return callRecordRepository.save(callRecord);
    }

    @Override
    @Transactional
    public CallRecord updateCallDuration(Integer id, int durationSeconds) {
        CallRecord callRecord = callRecordRepository.findById(id).orElse(null);
        if (callRecord == null) {
            log.error("Call record not found: {}", id);
            throw new IllegalArgumentException("Call record not found");
        }
        
        callRecord.setDurationSeconds(durationSeconds);
        
        // 根据开始时间和持续时间计算结束时间
        if (callRecord.getStartTime() != null && durationSeconds > 0) {
            LocalDateTime endTime = callRecord.getStartTime().plusSeconds(durationSeconds);
            callRecord.setEndTime(endTime);
        }
        
        return callRecordRepository.save(callRecord);
    }

//    @Override
//    @Transactional(readOnly = true)
//    public List<CallRecord> getRecentCalls(String userId, int limit) {
//        return callRecordRepository.findRecentCallsByUserId(userId, limit); //要不就改这里limit的类型，要不就改repository里的pageable
//    }

    //修改getRecentCalls
    @Override
    @Transactional(readOnly = true)
    public List<CallRecord> getRecentCalls(String userId, int limit) {
        Pageable pageable = PageRequest.of(0, limit); // 第0页，limit条记录
        return callRecordRepository.findRecentCallsByUserId(userId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CallRecord> getMissedCalls(String userId) {
        return callRecordRepository.findByCalleeIdAndStatusOrderByStartTimeDesc(userId, CallRecord.CallStatus.MISSED);
    }

    @Override
    @Transactional
    public void deleteCallRecord(Integer id) {
        if (!callRecordRepository.existsById(id)) {
            log.error("Call record not found: {}", id);
            throw new IllegalArgumentException("Call record not found");
        }
        
        callRecordRepository.deleteById(id);
    }

    //gjx加的save方法
    @Override
    public CallRecord save(CallRecord callRecord) {
        return callRecordRepository.save(callRecord);
    }
}