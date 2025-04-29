package com.example.demo.DTO;

import com.example.demo.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ChatSessionWithParticipantsDTO {
    private String id;
    private String type;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<User> participants;
}
