package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// 聊天消息实体类
@Entity
@Table(name = "chat_messages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    // 消息类型枚举
    public enum MessageType {
        TEXT,   // 文本消息
        IMAGE,  // 图片消息
        VOICE,  // 语音消息
        VIDEO,  // 视频消息
        FILE    // 文件消息
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;  // 修改为 Integer 类型，以匹配数据库中的 INT 类型

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    @JsonIgnore
    private ChatSession session;  // session_id 引用 ChatSession 表中的 id 字段

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;  // sender_id 引用 User 表中的 id 字段

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false)
    private MessageType type;  // message_type 映射为 MessageType 枚举类型

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;  // content 字段为 TEXT 类型

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;  // sent_at 字段为 TIMESTAMP 类型

    // 用于文件类型消息，引用对应的文件记录
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    private File file;  // 通过 file_id 外键关联到 File 表中的 id 字段

    // 是否已读
    @Column(name = "read_status", nullable = false)
    private boolean read;  // read_status 字段映射为 boolean 类型，表示消息是否已读

    //getter和setter
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ChatSession getSession() {
        return session;
    }

    public void setSession(ChatSession session) {
        this.session = session;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    @PrePersist
    protected void onCreate() {
        sentAt = LocalDateTime.now();
        read = false;  // 默认消息为未读
    }
}
