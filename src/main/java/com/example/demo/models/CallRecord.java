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

    /**
     * 通话类型枚举
     */
    public enum CallType {
        AUDIO,  // 音频通话
        VIDEO   // 视频通话
    }
    
    /**
     * 通话状态枚举
     */
    public enum CallStatus {
        MISSED,     // 未接听
        COMPLETED,  // 已完成
        REJECTED    // 已拒绝
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    @JsonIgnore
    private ChatSession session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caller_id", nullable = false)
    private User caller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "callee_id", nullable = false)
    private User callee;

    @Enumerated(EnumType.STRING)
    @Column(name = "call_type", nullable = false)
    private CallType callType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CallStatus status;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    //getter和setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ChatSession getSession() {
        return session;
    }

    public void setSession(ChatSession session) {
        this.session = session;
    }

    public User getCaller() {
        return caller;
    }

    public void setCaller(User caller) {
        this.caller = caller;
    }

    public User getCallee() {
        return callee;
    }

    public void setCallee(User callee) {
        this.callee = callee;
    }

    public CallType getCallType() {
        return callType;
    }

    public void setCallType(CallType callType) {
        this.callType = callType;
    }

    public CallStatus getStatus() {
        return status;
    }

    public void setStatus(CallStatus status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Integer getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Integer durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    /**
     * 创建前的回调
     */
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