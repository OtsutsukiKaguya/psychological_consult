package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.dto.CreateNotificationRequestDTO;
import com.example.demo.entity.Notification;
import com.example.demo.mapper.NotificationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private NotificationMapper notificationMapper;

    // 1. 管理员群发通知
    @PostMapping
    public Result sendNotification(@RequestBody CreateNotificationRequestDTO request) {
        Notification notification = new Notification();
        String generatedId = "n" + System.currentTimeMillis();
        notification.setNotificationId(generatedId);
        notification.setNotificationTime(LocalDateTime.now());
        notification.setNotificationTitle(request.getNotificationTitle());
        notification.setNotificationContent(request.getNotificationContent());
        notification.setSenderId(request.getSenderId());
        notification.setNotificationIsDeleted(false);
        notification.setPictureLink(request.getPictureLink());

        boolean inserted = notificationMapper.insertNotification(notification) > 0;
        if (!inserted) return Result.error("通知发送失败");

        // 插入所有接收者
        for (String receiverId : request.getReceiverIds()) {
            notificationMapper.insertNotificationReceiver(generatedId, receiverId, false);
        }

        return Result.success(notification);
    }

    // 2. 获取我接收的通知
    @GetMapping("/receiver/{receiverId}")
    public Result getReceived(@PathVariable String receiverId) {
        return Result.success(notificationMapper.getNotificationsByReceiverId(receiverId));
    }

    // 3. 获取我发送的通知
    @GetMapping("/sender/{senderId}")
    public Result getSent(@PathVariable String senderId) {
        return Result.success(notificationMapper.getNotificationsBySenderId(senderId));
    }

    // 4. 获取通知详情
    @GetMapping("/{id}")
    public Result getNotificationDetail(@PathVariable String id) {
        return Result.success(notificationMapper.getNotificationById(id));
    }

    // 5. 删除通知（逻辑删除）
    @DeleteMapping("/{id}")
    public Result deleteNotification(@PathVariable String id) {
        boolean success = notificationMapper.deleteNotificationById(id) > 0;
        return success ? Result.success("通知已删除") : Result.error("删除失败，未找到通知");
    }

    // 6. 标记通知为已读
    @PostMapping("/read/{notificationId}/{receiverId}")
    public Result markAsRead(@PathVariable String notificationId, @PathVariable String receiverId) {
        int updated = notificationMapper.markNotificationAsRead(notificationId, receiverId);
        return updated > 0 ? Result.success("标记为已读") : Result.error("标记失败");
    }
}
