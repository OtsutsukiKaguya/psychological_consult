//package com.counseling.platform.controllers;
package com.example.demo.controller;


import com.example.demo.models.CallRecord;
import com.example.demo.models.ChatSession;
import com.example.demo.models.User;
import com.example.demo.service.CallRecordService;
import com.example.demo.service.ChatSessionService;
import com.example.demo.service.UserService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/call")
@Slf4j
public class CallController {

    @Autowired
    private CallRecordService callRecordService;
    
    @Autowired
    private ChatSessionService chatSessionService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * 发起通话
     */
    @PostMapping("/start")
    public ResponseEntity<?> startCall(@Valid @RequestBody StartCallRequest request) {
        try {
            log.debug("Starting call: {}", request);
            
            // 获取当前用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User caller = userService.findById(authentication.getName());
            if (caller == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }
            
            // 获取被叫用户
            User callee = userService.findById(request.getCalleeId());
            if (callee == null) {
                return ResponseEntity.badRequest().body("Callee user not found");
            }
            
            // 检查会话是否存在
            ChatSession session = chatSessionService.findById(request.getSessionId());
            if (session == null) {
                return ResponseEntity.badRequest().body("Chat session not found");
            }
            
            // 创建通话记录
            CallRecord callRecord = CallRecord.builder()
                    .session(session)
                    .caller(caller)
                    .callee(callee)
                    .callType(CallRecord.CallType.valueOf(request.getCallType()))
                    .startTime(LocalDateTime.now())
                    .status(CallRecord.CallStatus.MISSED) // 初始状态为未接听
                    .durationSeconds(0)
                    .build();
            
            CallRecord savedCallRecord = callRecordService.save(callRecord); //service里没定义啊
            
            // 发送通话通知给被叫用户
            Map<String, Object> callNotification = new HashMap<>();
            callNotification.put("type", "INCOMING_CALL");
            callNotification.put("callId", savedCallRecord.getId());
            callNotification.put("sessionId", savedCallRecord.getSession().getId());
            callNotification.put("callerId", savedCallRecord.getCaller().getId());
            callNotification.put("callerName", savedCallRecord.getCaller().getName());
            callNotification.put("callType", savedCallRecord.getCallType().name());
            
            messagingTemplate.convertAndSendToUser(
                    callee.getName(),
                    "/queue/notifications",
                    callNotification
            );
            
            // 返回通话记录
            return ResponseEntity.ok(savedCallRecord);
        } catch (Exception e) {
            log.error("Failed to start call", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to start call: " + e.getMessage());
        }
    }

    /**
     * 接听通话
     */
    @PostMapping("/answer")
    public ResponseEntity<?> answerCall(@Valid @RequestBody AnswerCallRequest request) {
        try {
            log.debug("Answering call: {}", request);
            
            // 获取当前用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.findById(authentication.getName());
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }
            
            // 获取通话记录
            CallRecord callRecord = callRecordService.findById(request.getCallId());
            if (callRecord == null) {
                return ResponseEntity.badRequest().body("Call record not found");
            }
            
            // 检查当前用户是否是被叫用户
            if (!callRecord.getCallee().getId().equals(currentUser.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized to answer this call");
            }
            
            // 更新通话记录状态
            callRecord = callRecordService.updateCallStatus(request.getCallId(), CallRecord.CallStatus.COMPLETED);
            
            // 发送通话接听通知给主叫用户
            Map<String, Object> callNotification = new HashMap<>();
            callNotification.put("type", "CALL_ANSWERED");
            callNotification.put("callId", callRecord.getId());
            callNotification.put("sessionId", callRecord.getSession().getId());
            callNotification.put("calleeId", callRecord.getCallee().getId());
            callNotification.put("calleeName", callRecord.getCallee().getName());
            
            messagingTemplate.convertAndSendToUser(
                    callRecord.getCaller().getName(),
                    "/queue/notifications",
                    callNotification
            );
            
            // 返回通话记录
            return ResponseEntity.ok(callRecord);
        } catch (Exception e) {
            log.error("Failed to answer call", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to answer call: " + e.getMessage());
        }
    }

    /**
     * 拒绝通话
     */
    @PostMapping("/reject")
    public ResponseEntity<?> rejectCall(@Valid @RequestBody RejectCallRequest request) {
        try {
            log.debug("Rejecting call: {}", request);
            
            // 获取当前用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.findById(authentication.getName());
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }
            
            // 获取通话记录
            CallRecord callRecord = callRecordService.findById(request.getCallId());
            if (callRecord == null) {
                return ResponseEntity.badRequest().body("Call record not found");
            }
            
            // 检查当前用户是否是被叫用户
            if (!callRecord.getCallee().getId().equals(currentUser.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized to reject this call");
            }
            
            // 更新通话记录状态
            callRecord = callRecordService.updateCallStatus(request.getCallId(), CallRecord.CallStatus.REJECTED);
            
            // 发送通话拒绝通知给主叫用户
            Map<String, Object> callNotification = new HashMap<>();
            callNotification.put("type", "CALL_REJECTED");
            callNotification.put("callId", callRecord.getId());
            callNotification.put("sessionId", callRecord.getSession().getId());
            callNotification.put("calleeId", callRecord.getCallee().getId());
            callNotification.put("calleeName", callRecord.getCallee().getName());
            
            messagingTemplate.convertAndSendToUser(
                    callRecord.getCaller().getName(),
                    "/queue/notifications",
                    callNotification
            );
            
            // 返回通话记录
            return ResponseEntity.ok(callRecord);
        } catch (Exception e) {
            log.error("Failed to reject call", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to reject call: " + e.getMessage());
        }
    }

    /**
     * 挂断通话
     */
    @PostMapping("/hangup")
    public ResponseEntity<?> hangupCall(@Valid @RequestBody HangupCallRequest request) {
        try {
            log.debug("Hanging up call: {}", request);
            
            // 获取当前用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.findById(authentication.getName());
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }
            
            // 获取通话记录
            CallRecord callRecord = callRecordService.findById(request.getCallId());
            if (callRecord == null) {
                return ResponseEntity.badRequest().body("Call record not found");
            }
            
            // 检查当前用户是否是主叫或被叫用户
            if (!callRecord.getCaller().getId().equals(currentUser.getId()) && 
                !callRecord.getCallee().getId().equals(currentUser.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not authorized to hang up this call");
            }
            
            // 设置通话结束时间和时长
            callRecord.setEndTime(LocalDateTime.now());
            if (callRecord.getStartTime() != null) {
                callRecord.setDurationSeconds((int) java.time.Duration.between(callRecord.getStartTime(), callRecord.getEndTime()).getSeconds());
            }
            
            // 保存通话记录
            CallRecord savedCallRecord = callRecordService.save(callRecord);  //service没定义这个方法
            
            // 发送通话挂断通知给对方
            Map<String, Object> callNotification = new HashMap<>();
            callNotification.put("type", "CALL_HANGUP");
            callNotification.put("callId", savedCallRecord.getId());
            callNotification.put("sessionId", savedCallRecord.getSession().getId());
            callNotification.put("userId", currentUser.getId());
            callNotification.put("username", currentUser.getName());
            
            String targetUsername;
            if (callRecord.getCaller().getId().equals(currentUser.getId())) {
                targetUsername = callRecord.getCallee().getName();
            } else {
                targetUsername = callRecord.getCaller().getName();
            }
            
            messagingTemplate.convertAndSendToUser(
                    targetUsername,
                    "/queue/notifications",
                    callNotification
            );
            
            // 返回通话记录
            return ResponseEntity.ok(savedCallRecord);
        } catch (Exception e) {
            log.error("Failed to hang up call", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to hang up call: " + e.getMessage());
        }
    }

    /**
     * 获取会话的通话记录
     */
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<?> getSessionCallRecords(@PathVariable String sessionId) {
        try {
            // 获取当前用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.findById(authentication.getName());
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }
            
            // 检查会话是否存在
            ChatSession session = chatSessionService.findById(sessionId);
            if (session == null) {
                return ResponseEntity.badRequest().body("Chat session not found");
            }
            
            // 获取通话记录
            List<CallRecord> callRecords = callRecordService.findBySessionId(sessionId);
            
            return ResponseEntity.ok(callRecords);
        } catch (Exception e) {
            log.error("Failed to get call records", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to get call records: " + e.getMessage());
        }
    }

    /**
     * 获取用户的通话记录
     */
    @GetMapping("/user")
    public ResponseEntity<?> getUserCallRecords() {
        try {
            // 获取当前用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userService.findById(authentication.getName());
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }
            
            // 获取通话记录
            List<CallRecord> callRecords = callRecordService.findByUserId(currentUser.getId());
            
            return ResponseEntity.ok(callRecords);
        } catch (Exception e) {
            log.error("Failed to get call records", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to get call records: " + e.getMessage());
        }
    }

    /**
     * 开始通话请求
     */
    @Data
    public static class StartCallRequest {
        private String sessionId; //这里逻辑有问题啊，这里到底是用id还是用session_id
        private String calleeId;
        private String callType;
    }

    /**
     * 接听通话请求
     */
    @Data
    public static class AnswerCallRequest {
        private Integer callId;
    }

    /**
     * 拒绝通话请求
     */
    @Data
    public static class RejectCallRequest {
        private Integer callId;
    }

    /**
     * 挂断通话请求
     */
    @Data
    public static class HangupCallRequest {
        private Integer callId;
    }
}