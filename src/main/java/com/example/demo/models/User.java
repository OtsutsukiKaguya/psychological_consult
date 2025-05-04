package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;


// 用户实体类
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "person", uniqueConstraints = {
        @UniqueConstraint(columnNames = "id")  // 以 id 为唯一约束
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

}
