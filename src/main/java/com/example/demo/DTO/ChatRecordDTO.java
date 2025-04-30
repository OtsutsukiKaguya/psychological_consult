package com.example.demo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatRecordDTO {
    private String senderId;
    private String sentAt;
    private String type;
    private String content;
}
