package com.example.demo.entity;

import java.time.LocalDateTime;

public class Person {
    private String id;
    private String role;
    private String name;
    private String email;
    private String gender;
    private String idcard;
    private Integer age;
    private String phone;
    private String password;
    private LocalDateTime lastLoginTime;
    private String state;
    private String selfDescription;
    private String idPictureLink;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(LocalDateTime lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getSelfDescription() {
        return selfDescription;
    }

    public void setSelfDescription(String selfDescription) {
        this.selfDescription = selfDescription;
    }

    public String getIdPictureLink() {
        return idPictureLink;
    }

    public void setIdPictureLink(String idPictureLink) {
        this.idPictureLink = idPictureLink;
    }


    @Override
    public String toString() {
        return "Person{" +
                "id='" + id + '\'' +
                ", role='" + role + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                ", idcard='" + idcard + '\'' +
                ", age=" + age +
                ", phone='" + phone + '\'' +
                ", password='" + password + '\'' +
                ", lastLoginTime=" + lastLoginTime +
                ", state='" + state + '\'' +
                ", selfDescription='" + selfDescription + '\'' +
                ", idPictureLink='" + idPictureLink + '\'' +
                '}';
    }


}
