package com.example.demo.dto;

import java.time.LocalDateTime;

public class SearchUserDTO {
    private String name;
    private String gender;
    private String id;
    private String phone;
    private String urgentName;
    private String urgentPhone;
    private String role;
    private LocalDateTime lastLoginTime;

    @Override
    public String toString() {
        return "SearchUserDTO{" +
                "name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", id='" + id + '\'' +
                ", phone='" + phone + '\'' +
                ", urgentName='" + urgentName + '\'' +
                ", urgentPhone='" + urgentPhone + '\'' +
                ", role='" + role + '\'' +
                ", last_login_time=" + lastLoginTime +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUrgentName() {
        return urgentName;
    }

    public void setUrgentName(String urgentName) {
        this.urgentName = urgentName;
    }

    public String getUrgentPhone() {
        return urgentPhone;
    }

    public void setUrgentPhone(String urgentPhone) {
        this.urgentPhone = urgentPhone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(LocalDateTime lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }
}
