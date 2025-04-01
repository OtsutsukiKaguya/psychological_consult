package com.example.demo.entity;

import java.time.LocalDateTime;
import java.time.Duration;
import com.fasterxml.jackson.annotation.JsonFormat;

public class Conversation {
    private String conversationId;
    private String conversationState;
    private LocalDateTime conversationStartTime;
    private LocalDateTime conversationEndTime;
    private Duration conversationDuration;
    private String userId;
    private String counselorId;
    private Boolean isTutor;
    private String tutorId;
    private String tutorState;
    private String tutorEndTime;
    private Double rate;
    private Duration tutorDuration;
    private String tutorComment;
    private String userComment;
    private String counselorComment;


    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getConversationState() {
        return conversationState;
    }

    public void setConversationState(String conversationState) {
        this.conversationState = conversationState;
    }

    public LocalDateTime getConversationStartTime() {
        return conversationStartTime;
    }

    public void setConversationStartTime(LocalDateTime conversationStartTime) {
        this.conversationStartTime = conversationStartTime;
    }

    public LocalDateTime getConversationEndTime() {
        return conversationEndTime;
    }

    public void setConversationEndTime(LocalDateTime conversationEndTime) {
        this.conversationEndTime = conversationEndTime;
    }

    public Duration getConversationDuration() {
        return conversationDuration;
    }

    public void setConversationDuration(Duration conversationDuration) {
        this.conversationDuration = conversationDuration;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCounselorId() {
        return counselorId;
    }

    public void setCounselorId(String counselorId) {
        this.counselorId = counselorId;
    }

    public Boolean getTutor() {
        return isTutor;
    }

    public void setTutor(Boolean tutor) {
        isTutor = tutor;
    }

    public String getTutorId() {
        return tutorId;
    }

    public void setTutorId(String tutorId) {
        this.tutorId = tutorId;
    }

    public String getTutorState() {
        return tutorState;
    }

    public void setTutorState(String tutorState) {
        this.tutorState = tutorState;
    }

    public String getTutorEndTime() {
        return tutorEndTime;
    }

    public void setTutorEndTime(String tutorEndTime) {
        this.tutorEndTime = tutorEndTime;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Duration getTutorDuration() {
        return tutorDuration;
    }

    public void setTutorDuration(Duration tutorDuration) {
        this.tutorDuration = tutorDuration;
    }

    public String getTutorComment() {
        return tutorComment;
    }

    public void setTutorComment(String tutorComment) {
        this.tutorComment = tutorComment;
    }

    public String getUserComment() {
        return userComment;
    }

    public void setUserComment(String userComment) {
        this.userComment = userComment;
    }

    public String getCounselorComment() {
        return counselorComment;
    }

    public void setCounselorComment(String counselorComment) {
        this.counselorComment = counselorComment;
    }

    @Override
    public String toString() {
        return "Conversation{" +
                "conversationId='" + conversationId + '\'' +
                ", conversationState='" + conversationState + '\'' +
                ", conversationStartTime=" + conversationStartTime +
                ", conversationEndTime=" + conversationEndTime +
                ", conversationDuration=" + conversationDuration +
                ", userId='" + userId + '\'' +
                ", counselorId='" + counselorId + '\'' +
                ", isTutor=" + isTutor +
                ", tutorId='" + tutorId + '\'' +
                ", tutorState='" + tutorState + '\'' +
                ", tutorEndTime='" + tutorEndTime + '\'' +
                ", rate=" + rate +
                ", tutorDuration=" + tutorDuration +
                ", tutorComment='" + tutorComment + '\'' +
                ", userComment='" + userComment + '\'' +
                ", counselorComment='" + counselorComment + '\'' +
                '}';
    }

}
