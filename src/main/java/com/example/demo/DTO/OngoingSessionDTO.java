package com.example.demo.DTO;

import com.example.demo.models.ChatSession;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 正在进行的会话 DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OngoingSessionDTO {
    private String sessionId;
    private LocalDateTime createdAt;
    private ChatSession.SessionType type;
    private List<String> participantIds;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ChatSession.SessionType getType() {
        return type;
    }

    public void setType(ChatSession.SessionType type) {
        this.type = type;
    }

    public List<String> getParticipantIds() {
        return participantIds;
    }

    public void setParticipantIds(List<String> participantIds) {
        this.participantIds = participantIds;
    }
}
