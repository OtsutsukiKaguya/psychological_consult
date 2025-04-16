package com.example.demo.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 消息已读状态模型
 * 记录用户对消息的阅读状态
 */
@Entity
@Table(name = "message_read", indexes = {
        @Index(name = "idx_message_read_message_user", columnList = "message_id, user_id", unique = true)
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageRead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message_id", nullable = false)
    private Integer messageId;  // 修改为 Integer 类型，以匹配数据库中的 INT 类型

    @Column(name = "user_id", nullable = false)
    private String userId;  // 修改为 String 类型，以匹配数据库中的 VARCHAR(50) 类型

    @Column(name = "read_at", nullable = false)
    private LocalDateTime readAt;

    //getter和setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }
}
