package com.example.demo.entity;

public class Star {
    private String counselorId;
    private String userId;

    public String getCounselorId() {
        return counselorId;
    }

    public void setCounselorId(String counselorId) {
        this.counselorId = counselorId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Star{" +
                "counselorId='" + counselorId + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }


}
