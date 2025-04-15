package com.example.demo.entity;

public class Bind {
    private String counselorId;
    private String tutorId;

    public String getCounselorId() {
        return counselorId;
    }

    public void setCounselorId(String counselorId) {
        this.counselorId = counselorId;
    }

    public String getTutorId() {
        return tutorId;
    }

    public void setTutorId(String tutorId) {
        this.tutorId = tutorId;
    }

    @Override
    public String toString() {
        return "Bind{" +
                "counselorId='" + counselorId + '\'' +
                ", tutorId='" + tutorId + '\'' +
                '}';
    }

}
