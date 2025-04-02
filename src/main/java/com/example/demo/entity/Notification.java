package com.example.demo.entity;

import java.time.LocalDateTime;
import java.time.Duration;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

public class Notification {
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime notificationTime;

    private String notificationId;
    private String notificationTitle;
    private String senderId;
    private String notificationContent;
    private Boolean notificationIsDeleted;
    private String pictureLink;

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public LocalDateTime getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(LocalDateTime notificationTime) {
        this.notificationTime = notificationTime;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getNotificationContent() {
        return notificationContent;
    }

    public void setNotificationContent(String notificationContent) {
        this.notificationContent = notificationContent;
    }

    public Boolean getNotificationIsDeleted() {
        return notificationIsDeleted;
    }

    public void setNotificationIsDeleted(Boolean notificationIsDeleted) {
        this.notificationIsDeleted = notificationIsDeleted;
    }

    public String getPictureLink() {
        return pictureLink;
    }

    public void setPictureLink(String pictureLink) {
        this.pictureLink = pictureLink;
    }


    @Override
    public String toString() {
        return "Notification{" +
                "notificationId='" + notificationId + '\'' +
                ", notificationTime=" + notificationTime +
                ", notificationTitle='" + notificationTitle + '\'' +
                ", senderId='" + senderId + '\'' +
                ", notificationContent='" + notificationContent + '\'' +
                ", notificationIsDeleted=" + notificationIsDeleted +
                ", pictureLink='" + pictureLink + '\'' +
                '}';
    }


}
