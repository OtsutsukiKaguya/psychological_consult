package com.example.demo.dto;

import java.util.Set;

public class CreateNotificationRequestDTO {
    private String notificationTitle;
    private String senderId;
    private String notificationContent;
    private String pictureLink;
    private Set<String> receiverIds; // 支持群发

    public String getNotificationTitle() { return notificationTitle; }
    public void setNotificationTitle(String notificationTitle) { this.notificationTitle = notificationTitle; }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getNotificationContent() { return notificationContent; }
    public void setNotificationContent(String notificationContent) { this.notificationContent = notificationContent; }

    public String getPictureLink() { return pictureLink; }
    public void setPictureLink(String pictureLink) { this.pictureLink = pictureLink; }

    public Set<String> getReceiverIds() { return receiverIds; }
    public void setReceiverIds(Set<String> receiverIds) { this.receiverIds = receiverIds; }
}

