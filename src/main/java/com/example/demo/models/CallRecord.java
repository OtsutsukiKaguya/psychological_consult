package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 通话记录实体类
 */
@Entity
@Table(name = "call_records")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CallRecord {

    // 通话类型枚举
    public enum CallType {
        AUDIO,  // 音频通话
        VIDEO   // 视频通话
    }

    // 通话状态枚举
    public enum CallStatus {
        MISSED,     // 未接听
        COMPLETED,  // 已完成
        REJECTED    // 已拒绝
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;  // 修改为 Integer 类型，匹配数据库中的 INT 类型

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    @JsonIgnore
    private ChatSession session;  // session_id 引用 ChatSession 表中的 id 字段

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caller_id", nullable = false)
    private User caller;  // caller_id 引用 User 表中的 id 字段

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "callee_id", nullable = false)
    private User callee;  // callee_id 引用 User 表中的 id 字段

    @Enumerated(EnumType.STRING)
    @Column(name = "call_type", nullable = false)
    private CallType callType;  // call_type 映射为 CallType 枚举类型

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CallStatus status;  // status 映射为 CallStatus 枚举类型

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;  // start_time 映射为 TIMESTAMP 类型

    @Column(name = "end_time")
    private LocalDateTime endTime;  // end_time 映射为 TIMESTAMP 类型

    @Column(name = "duration")
    private Integer durationSeconds;  // duration 映射为 INT 类型，使用 Integer 类型

    @PrePersist
    protected void onCreate() {
        // 如果没有设置开始时间，则设置为当前时间
        if (startTime == null) {
            startTime = LocalDateTime.now();
        }

        // 如果没有设置状态，则默认设置为未接听
        if (status == null) {
            status = CallStatus.MISSED;
        }

        // 如果没有设置通话时长，则默认为0
        if (durationSeconds == null) {
            durationSeconds = 0;
        }
    }
}
