package com.example.demo.DTO;

import com.example.demo.models.ChatSession;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 咨询排行榜 DTO（通用于咨询量和好评榜）
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsultationRankingDTO {

    private String counselorName;
    private Integer consultationCount;

    public String getCounselorName() {
        return counselorName;
    }

    public void setCounselorName(String counselorName) {
        this.counselorName = counselorName;
    }

    public Integer getConsultationCount() {
        return consultationCount;
    }

    public void setConsultationCount(Integer consultationCount) {
        this.consultationCount = consultationCount;
    }
}
