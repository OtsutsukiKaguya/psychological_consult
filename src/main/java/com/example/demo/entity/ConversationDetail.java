package com.example.demo.entity;

import java.time.LocalDateTime;
import java.time.Duration;
import com.fasterxml.jackson.annotation.JsonFormat;

public class ConversationDetail {
    private String conversationId;
    private LocalDateTime sendTime;
    private String senderId;
    private String conversationContent;
    private String link;

    public LocalDateTime getSendTime() {
        return sendTime;
    }

    public void setSendTime(LocalDateTime sendTime) {
        this.sendTime = sendTime;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getConversationContent() {
        return conversationContent;
    }

    public void setConversationContent(String conversationContent) {
        this.conversationContent = conversationContent;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }


    @Override
    public String toString() {
        return "ConversationDetail{" +
                "conversationId='" + conversationId + '\'' +
                ", sendTime=" + sendTime +
                ", senderId='" + senderId + '\'' +
                ", conversationContent='" + conversationContent + '\'' +
                ", link='" + link + '\'' +
                '}';
    }


}
