//package com.counseling.platform.services.impl;
package com.example.demo.service.impl;

import com.example.demo.models.CallRecord;
import com.example.demo.repositories.CallRecordRepository;
import com.example.demo.service.CallRecordService;
//import com.counseling.platform.models.CallRecord;
//import com.counseling.platform.repositories.CallRecordRepository;
//import com.counseling.platform.services.CallRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    public CallRecord findById(Long id) {
        return callRecordRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CallRecord> findBySessionId(Long sessionId) {
        return callRecordRepository.findBySessionIdOrderByStartTimeDesc(sessionId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CallRecord> findByCallerId(Long callerId) {
        return callRecordRepository.findByCallerIdOrderByStartTimeDesc(callerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CallRecord> findByCalleeId(Long calleeId) {
        return callRecordRepository.findByCalleeIdOrderByStartTimeDesc(calleeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CallRecord> findByUserId(Long userId) {
        return callRecordRepository.findByCallerIdOrCalleeIdOrderByStartTimeDesc(userId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CallRecord> findBetweenUsers(Long user1Id, Long user2Id) {
        return callRecordRepository.findBetweenUsers(user1Id, user2Id);
    }

    @Override
    @Transactional
    public CallRecord updateCallStatus(Long id, CallRecord.CallStatus status) {
        CallRecord callRecord = callRecordRepository.findById(id).orElse(null);
        if (callRecord == null) {
            log.error("Call record not found: {}", id);
            throw new IllegalArgumentException("Call record not found");
        }
        
        callRecord.setStatus(status);
        
        // 如果接听了，更新接听时间
        if (status == CallRecord.CallStatus.COMPLETED && callRecord.getAnswerTime() == null) {
            callRecord.setAnswerTime(LocalDateTime.now());
        }
        
        return callRecordRepository.save(callRecord);
    }

    @Override
    @Transactional
    public CallRecord endCall(Long id) {
        CallRecord callRecord = callRecordRepository.findById(id).orElse(null);
        if (callRecord == null) {
            log.error("Call record not found: {}", id);
            throw new IllegalArgumentException("Call record not found");
        }
        
        // 设置结束时间
        LocalDateTime endTime = LocalDateTime.now();
        callRecord.setEndTime(endTime);
        
        // 计算通话时长（如果已接听）
        if (callRecord.getAnswerTime() != null) {
            Duration duration = Duration.between(callRecord.getAnswerTime(), endTime);
            callRecord.setDuration((int) duration.getSeconds());
            
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
    public CallRecord updateCallDuration(Long id, int durationSeconds) {
        CallRecord callRecord = callRecordRepository.findById(id).orElse(null);
        if (callRecord == null) {
            log.error("Call record not found: {}", id);
            throw new IllegalArgumentException("Call record not found");
        }
        
        callRecord.setDuration(durationSeconds);
        
        // 根据开始时间和持续时间计算结束时间
        if (callRecord.getAnswerTime() != null && durationSeconds > 0) {
            LocalDateTime endTime = callRecord.getAnswerTime().plusSeconds(durationSeconds);
            callRecord.setEndTime(endTime);
        }
        
        return callRecordRepository.save(callRecord);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CallRecord> getRecentCalls(Long userId, int limit) {
        return callRecordRepository.findRecentCallsByUserId(userId, limit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CallRecord> getMissedCalls(Long userId) {
        return callRecordRepository.findByCalleeIdAndStatusOrderByStartTimeDesc(userId, CallRecord.CallStatus.MISSED);
    }

    @Override
    @Transactional
    public void deleteCallRecord(Long id) {
        if (!callRecordRepository.existsById(id)) {
            log.error("Call record not found: {}", id);
            throw new IllegalArgumentException("Call record not found");
        }
        
        callRecordRepository.deleteById(id);
    }
}