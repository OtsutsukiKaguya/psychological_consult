package com.example.demo.controller;

import com.example.demo.entity.Notification;
import com.example.demo.mapper.NotificationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private NotificationMapper notificationMapper;

    // 1. 管理员发送通知
    @PostMapping
    public boolean sendNotification(@RequestBody Notification notification) {
        return notificationMapper.insertNotification(notification) > 0;
    }

    // 2. 显示我接收的通知（receiverId）
    @GetMapping("/receiver/{receiverId}")
    public List<Notification> getReceived(@PathVariable String receiverId) {
        return notificationMapper.getNotificationsByReceiverId(receiverId);
    }

    // 3. 获取我发送的通知（senderId）
    @GetMapping("/sender/{senderId}")
    public List<Notification> getSent(@PathVariable String senderId) {
        return notificationMapper.getNotificationsBySenderId(senderId);
    }

    // 4. 查看通知详情
    @GetMapping("/{id}")
    public Notification getNotificationDetail(@PathVariable String id) {
        return notificationMapper.getNotificationById(id);
    }
}
