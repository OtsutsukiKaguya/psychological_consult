package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;


// 用户实体类
@Entity
@Table(name = "person", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")  // 以 email 为唯一约束
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    // 用户角色枚举
    public enum UserRole {
        USER,        // 普通用户
        COUNSELOR,   // 咨询师
        TUTOR,       // 督导
        ADMIN        // 管理员
    }
    // 用户状态枚举
    public enum UserStatus {
        ONLINE,      // 在线
        OFFLINE,     // 离线
        BUSY         // 忙碌
    }

    @Id
    @Column(name = "id", length = 50)  // id 为 varchar(50)
    private String id;

    @Size(max = 50)
    @Column(name = "name", nullable = true)
    private String name;

    @Size(max = 50)
    @Column(name = "email", nullable = true)
    private String email;

    @Size(max = 10)
    @Column(name = "gender", nullable = true)
    private String gender;

    @Size(max = 50)
    @Column(name = "idcard", nullable = true)
    private String idCard;

    @Column(name = "age", nullable = true)
    private Integer age;

    @Size(max = 20)
    @Column(name = "phone", nullable = true)
    private String phone;

    @Size(max = 20)
    @Column(name = "password", nullable = true)
    private String password;

    @Column(name = "last_login_time", nullable = true)
    private LocalDateTime lastLoginTime;

    @Column(name = "self_description", length = 255, nullable = true)
    private String selfDescription;

    @Column(name = "id_picture_link", length = 255, nullable = true)
    private String idPictureLink;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, columnDefinition = "enum('ONLINE','OFFLINE','BUSY') default 'OFFLINE'")
    private UserStatus state;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, columnDefinition = "enum('USER','COUNSELOR','TUTOR','ADMIN') default 'USER'")
    private UserRole role;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 创建前的回调
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    // 更新前的回调
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
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

    public UserStatus getState() {
        return state;
    }

    public void setState(UserStatus state) {
        this.state = state;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
