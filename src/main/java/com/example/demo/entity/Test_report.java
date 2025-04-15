package com.example.demo.entity;

import java.time.Duration;
import java.time.LocalDateTime;

public class Test_report {
    private String testId;
    private String testName;
    private String testContent;
    private String testResult;
    private Duration testDuration;
    private LocalDateTime testTime;
    private String userId;

    public String getTestResult() {
        return testResult;
    }

    public void setTestResult(String testResult) {
        this.testResult = testResult;
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getTestContent() {
        return testContent;
    }

    public void setTestContent(String testContent) {
        this.testContent = testContent;
    }

    public Duration getTestDuration() {
        return testDuration;
    }

    public void setTestDuration(Duration testDuration) {
        this.testDuration = testDuration;
    }

    public LocalDateTime getTestTime() {
        return testTime;
    }

    public void setTestTime(LocalDateTime testTime) {
        this.testTime = testTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    @Override
    public String toString() {
        return "Test_report{" +
                "testId='" + testId + '\'' +
                ", testName='" + testName + '\'' +
                ", testContent='" + testContent + '\'' +
                ", testResult='" + testResult + '\'' +
                ", testDuration=" + testDuration +
                ", testTime=" + testTime +
                ", userId='" + userId + '\'' +
                '}';
    }

}
