package com.example.demo.dto;

import com.example.demo.entity.Notification;

public class NotificationDTO extends Notification {
    // 接收者 ID
    private String receiverId;
    // 已读状态：0 或 1
    private Integer isRead;

    public String getReceiverId() {
        return receiverId;
    }
    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }
    public Integer getIsRead() {
        return isRead;
    }
    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }
}
