//package com.counseling.platform.models;
package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//import javax.persistence.*;
import jakarta.persistence.*; // ← 新包名
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 聊天会话实体类
 */
@Entity
@Table(name = "chat_sessions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatSession {

    /**
     * 会话类型枚举
     */
    public enum SessionType {
        ONE_TO_ONE,  // 一对一会话
        GROUP        // 群组会话
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private SessionType type;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SessionType getType() {
        return type;
    }

    public void setType(SessionType type) {
        this.type = type;
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

    public Set<SessionParticipant> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<SessionParticipant> participants) {
        this.participants = participants;
    }

    public Set<ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(Set<ChatMessage> messages) {
        this.messages = messages;
    }

    public Set<CallRecord> getCallRecords() {
        return callRecords;
    }

    public void setCallRecords(Set<CallRecord> callRecords) {
        this.callRecords = callRecords;
    }


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

    /**
     * 添加参与者
     */
    public void addParticipant(User user, SessionParticipant.ParticipantRole role) {
        SessionParticipant participant = SessionParticipant.builder()
                .session(this)
                .user(user)
                .role(role)
                .joinedAt(LocalDateTime.now())
                .build();
        
        participants.add(participant);
        user.getSessions().add(participant);
    }

    /**
     * 移除参与者
     */
    public void removeParticipant(User user) {
        for (SessionParticipant participant : new HashSet<>(participants)) {
            if (participant.getUser().equals(user)) {
                participants.remove(participant);
                user.getSessions().remove(participant);
                participant.setSession(null);
                participant.setUser(null);
            }
        }
    }
}