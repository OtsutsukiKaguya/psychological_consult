//package com.counseling.platform.controllers;
package com.example.demo.controller;

//import com.counseling.platform.models.CallRecord;
//import com.counseling.platform.models.User;
//import com.counseling.platform.services.CallRecordService;
//import com.counseling.platform.services.ChatSessionService;
//import com.counseling.platform.services.UserService;
import com.example.demo.models.CallRecord;
import com.example.demo.models.User;
import com.example.demo.service.CallRecordService;
import com.example.demo.service.ChatSessionService;
import com.example.demo.service.UserService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * WebRTC信令控制器
 * 处理音视频通话的信令交换
 */
@Controller
@Slf4j
public class WebRTCSignalingController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private CallRecordService callRecordService;
    
    @Autowired
    private ChatSessionService chatSessionService;

    /**
     * 处理WebRTC信令
     */
    @MessageMapping("/webrtc/signal")
    public void handleSignal(@Payload SignalMessage message, Authentication authentication) {
        try {
            // 获取当前用户
            User sender = userService.findByUsername(authentication.getName());
            if (sender == null) {
                log.error("User not authenticated");
                return;
            }
            
            // 获取接收者
            User receiver = userService.findById(message.getReceiverUserId());
            if (receiver == null) {
                log.error("Receiver user not found: {}", message.getReceiverUserId());
                return;
            }
            
            // 发送信令消息
            Map<String, Object> signalData = new HashMap<>();
            signalData.put("type", message.getType());
            signalData.put("senderUserId", sender.getId());
            signalData.put("senderUsername", sender.getUsername());
            signalData.put("senderNickname", sender.getNickname());
            signalData.put("sessionId", message.getSessionId());
            signalData.put("callId", message.getCallId());
            signalData.put("callType", message.getCallType());
            signalData.put("sdp", message.getSdp());
            signalData.put("candidate", message.getCandidate());
            signalData.put("timestamp", LocalDateTime.now().toString());
            
            // 传递给接收者
            messagingTemplate.convertAndSendToUser(
                    receiver.getUsername(),
                    "/queue/webrtc/signal",
                    signalData
            );
            
            // 处理特定类型的信令
            handleSpecificSignalTypes(message, sender, receiver);
        } catch (Exception e) {
            log.error("Error processing WebRTC signal", e);
        }
    }

    /**
     * 处理特定类型的信令
     */
    private void handleSpecificSignalTypes(SignalMessage message, User sender, User receiver) {
        String type = message.getType();
        
        switch (type) {
            case "offer":
                // 创建通话记录
                createCallRecord(message, sender.getId(), receiver.getId());
                // 更新发起人状态为忙碌
                userService.updateUserStatus(sender.getId(), User.UserStatus.BUSY);
                break;
                
            case "answer":
                // 更新通话状态为已接听
                callRecordService.updateCallStatus(message.getCallId(), CallRecord.CallStatus.COMPLETED);
                // 更新接听人状态为忙碌
                userService.updateUserStatus(receiver.getId(), User.UserStatus.BUSY);
                break;
                
            case "reject":
                // 更新通话状态为已拒绝
                callRecordService.updateCallStatus(message.getCallId(), CallRecord.CallStatus.REJECTED);
                break;
                
            case "hangup":
                // 结束通话
                callRecordService.endCall(message.getCallId());
                // 更新用户状态为在线
                userService.updateUserStatus(sender.getId(), User.UserStatus.ONLINE);
                if (message.getReceiverUserId() != null) {
                    userService.updateUserStatus(receiver.getId(), User.UserStatus.ONLINE);
                }
                break;
                
            case "busy":
                // 更新通话状态为已拒绝（忙）
                callRecordService.updateCallStatus(message.getCallId(), CallRecord.CallStatus.REJECTED);
                break;
                
            default:
                // 其他信令类型不需要特殊处理
                break;
        }
    }

    /**
     * 创建通话记录
     */
    private void createCallRecord(SignalMessage message, Long callerId, Long calleeId) {
        try {
            CallRecord callRecord = CallRecord.builder()
                    .sessionId(message.getSessionId())
                    .callerId(callerId)
                    .calleeId(calleeId)
                    .type(message.getCallType() != null ? CallRecord.CallType.valueOf(message.getCallType()) : CallRecord.CallType.AUDIO)
                    .status(CallRecord.CallStatus.MISSED) // 默认为未接
                    .startTime(LocalDateTime.now())
                    .build();
            
            CallRecord savedRecord = callRecordService.createCallRecord(callRecord);
            
            // 更新消息中的通话ID
            message.setCallId(savedRecord.getId());
        } catch (Exception e) {
            log.error("Failed to create call record", e);
        }
    }

    /**
     * 发起通话（REST API方式）
     */
    @PostMapping("/api/calls/initiate")
    @ResponseBody
    public Map<String, Object> initiateCall(@RequestBody InitiateCallRequest request, Authentication authentication) {
        try {
            // 获取当前用户
            User caller = userService.findByUsername(authentication.getName());
            if (caller == null) {
                return Map.of("success", false, "message", "User not authenticated");
            }
            
            // 获取被呼叫者
            User callee = userService.findById(request.getCalleeId());
            if (callee == null) {
                return Map.of("success", false, "message", "Callee not found");
            }
            
            // 检查用户是否在线
            if (callee.getStatus() != User.UserStatus.ONLINE) {
                return Map.of("success", false, "message", "User is not online");
            }
            
            // 检查用户是否为该会话的参与者
            if (!chatSessionService.isSessionParticipant(request.getSessionId(), caller.getId()) ||
                !chatSessionService.isSessionParticipant(request.getSessionId(), callee.getId())) {
                return Map.of("success", false, "message", "User is not a participant of the session");
            }
            
            // 创建通话记录
            CallRecord callRecord = CallRecord.builder()
                    .sessionId(request.getSessionId())
                    .callerId(caller.getId())
                    .calleeId(callee.getId())
                    .type(CallRecord.CallType.valueOf(request.getCallType()))
                    .status(CallRecord.CallStatus.MISSED) // 默认为未接
                    .startTime(LocalDateTime.now())
                    .build();
            
            CallRecord savedRecord = callRecordService.createCallRecord(callRecord);
            
            // 发送呼叫信号
            Map<String, Object> callSignal = new HashMap<>();
            callSignal.put("type", "incoming_call");
            callSignal.put("callId", savedRecord.getId());
            callSignal.put("callerId", caller.getId());
            callSignal.put("callerName", caller.getUsername());
            callSignal.put("callerNickname", caller.getNickname());
            callSignal.put("sessionId", request.getSessionId());
            callSignal.put("callType", request.getCallType());
            callSignal.put("timestamp", LocalDateTime.now().toString());
            
            messagingTemplate.convertAndSendToUser(
                    callee.getUsername(),
                    "/queue/calls",
                    callSignal
            );
            
            // 更新发起人状态为忙碌
            userService.updateUserStatus(caller.getId(), User.UserStatus.BUSY);
            
            return Map.of(
                    "success", true,
                    "callId", savedRecord.getId(),
                    "calleeId", callee.getId(),
                    "calleeName", callee.getUsername()
            );
        } catch (Exception e) {
            log.error("Failed to initiate call", e);
            return Map.of("success", false, "message", "Failed to initiate call: " + e.getMessage());
        }
    }

    /**
     * 音视频通话信令消息
     */
    @Data
    public static class SignalMessage {
        private String type;
        private Long senderUserId;
        private Long receiverUserId;
        private Long sessionId;
        private Long callId;
        private String callType;
        private String sdp;
        private String candidate;
    }

    /**
     * 发起通话请求
     */
    @Data
    public static class InitiateCallRequest {
        private Long sessionId;
        private Long calleeId;
        private String callType;
    }
}