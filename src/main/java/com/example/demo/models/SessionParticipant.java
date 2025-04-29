package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 会话参与者实体类
 * 用于表示用户与聊天会话之间的关系
 */
@Entity
@Table(name = "session_participants")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionParticipant {

    // 参与者角色枚举
    public enum ParticipantRole {
        USER,        // 普通用户
        COUNSELOR,   // 咨询师
        TUTOR,  // 督导
        ADMIN        // 管理员
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 自增ID
    private Integer id;  // id 字段类型改为 Integer

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    @JsonIgnoreProperties({"participants", "messages", "callRecords"}) // 避免死循环
    private ChatSession session;  // session_id 映射

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"sessions", "supervisor", "supervisees", "password"}) // 避免嵌套太多或隐私字段
    private User user;  // user_id 映射

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private ParticipantRole role;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    //@Column(name = "last_read_message_id")
    //private Long lastReadMessageId;

    @PrePersist
    protected void onCreate() {
        if (joinedAt == null) {
            joinedAt = LocalDateTime.now();  // 如果 joinedAt 为 null，设置为当前时间
        }
    }
}
