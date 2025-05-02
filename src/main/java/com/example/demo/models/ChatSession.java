package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

// 聊天会话实体类
@Entity
@Table(name = "chat_sessions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatSession {

    // 会话类型枚举
    public enum SessionType {
        ONE_TO_ONE,  // 一对一会话
        GROUP        // 群组会话
    }

    @Id
    @Column(name = "id", length = 100)  // 修改为 String 类型以匹配数据库中的 VARCHAR(100) 类型
    private String id;

    //@Column(name = "name")
    //private String name;

    //@Column(name = "description")
    //private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "session_type", nullable = false)
    private SessionType type;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "rating")
    private Integer rating;

    @Column(name = "user_comment", length = 255)
    private String userComment;

    @Column(name = "counselor_comment", columnDefinition = "TEXT")
    private String counselorComment;

    @Column(name = "tutor_comment", length = 255)
    private String tutorComment;

    @Column(name = "ended", nullable = false)
    private Boolean ended = false;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Column(name = "consult_id", length = 50)
    private String consultId;

    // 会话参与者
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SessionParticipant> participants = new HashSet<>();

    // 会话消息
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<ChatMessage> messages = new HashSet<>();

    // 通话记录
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<CallRecord> callRecords = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
