//package com.counseling.platform.models;
package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//import javax.persistence.*;
import jakarta.persistence.*; // ← 新包名
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 用户实体类
 */
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * 用户角色枚举
     */
    public enum UserRole {
        USER,        // 普通用户
        COUNSELOR,   // 咨询师
        SUPERVISOR,  // 督导
        ADMIN        // 管理员
    }

    /**
     * 用户状态枚举
     */
    public enum UserStatus {
        ONLINE,      // 在线
        OFFLINE,     // 离线
        BUSY         // 忙碌
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank
    @Size(max = 128)
    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Size(max = 50)
    @Column(nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    //getter和setter

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotBlank @Size(max = 50) String getUsername() {
        return username;
    }

    public void setUsername(@NotBlank @Size(max = 50) String username) {
        this.username = username;
    }

    public @NotBlank @Size(max = 128) String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank @Size(max = 128) String password) {
        this.password = password;
    }

    public @Size(max = 50) String getNickname() {
        return nickname;
    }

    public void setNickname(@Size(max = 50) String nickname) {
        this.nickname = nickname;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
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

    public User getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(User supervisor) {
        this.supervisor = supervisor;
    }

    public Set<User> getSupervisees() {
        return supervisees;
    }

    public void setSupervisees(Set<User> supervisees) {
        this.supervisees = supervisees;
    }

    public Set<SessionParticipant> getSessions() {
        return sessions;
    }

    public void setSessions(Set<SessionParticipant> sessions) {
        this.sessions = sessions;
    }

    public Set<CallRecord> getInitiatedCalls() {
        return initiatedCalls;
    }

    public void setInitiatedCalls(Set<CallRecord> initiatedCalls) {
        this.initiatedCalls = initiatedCalls;
    }

    public Set<CallRecord> getReceivedCalls() {
        return receivedCalls;
    }

    public void setReceivedCalls(Set<CallRecord> receivedCalls) {
        this.receivedCalls = receivedCalls;
    }

    public Set<File> getUploadedFiles() {
        return uploadedFiles;
    }

    public void setUploadedFiles(Set<File> uploadedFiles) {
        this.uploadedFiles = uploadedFiles;
    }


    // 咨询师的督导
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id")
    @JsonIgnore
    private User supervisor;

    // 被督导的咨询师
    @OneToMany(mappedBy = "supervisor", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<User> supervisees = new HashSet<>();

    // 用户作为参与者的会话
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<SessionParticipant> sessions = new HashSet<>();

    // 用户发起的通话
    @OneToMany(mappedBy = "caller", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<CallRecord> initiatedCalls = new HashSet<>();

    // 用户接收的通话
    @OneToMany(mappedBy = "callee", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<CallRecord> receivedCalls = new HashSet<>();

    // 用户上传的文件
    @OneToMany(mappedBy = "uploader", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<File> uploadedFiles = new HashSet<>();

    /**
     * 创建前的回调
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * 更新前的回调
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}