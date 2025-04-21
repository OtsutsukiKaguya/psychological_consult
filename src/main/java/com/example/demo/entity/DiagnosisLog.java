package com.example.demo.entity;

import java.time.LocalDateTime;

public class DiagnosisLog {
    private String userId;
    private String fullConversation;
    private String diagnosisResult;
    private String keywordTag;
    private LocalDateTime createTime;

    // Getter and Setter

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullConversation() {
        return fullConversation;
    }

    public void setFullConversation(String fullConversation) {
        this.fullConversation = fullConversation;
    }

    public String getDiagnosisResult() {
        return diagnosisResult;
    }

    public void setDiagnosisResult(String diagnosisResult) {
        this.diagnosisResult = diagnosisResult;
    }

    public String getKeywordTag() {
        return keywordTag;
    }

    public void setKeywordTag(String keywordTag) {
        this.keywordTag = keywordTag;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
