package com.example.demo.dto;

import com.example.demo.entity.Counselor;
import java.util.List;

public class DiagnosisResultDTO {

    private String summary;
    private String keywords;
    private List<Counselor> recommendations;

    public DiagnosisResultDTO() {}

    public DiagnosisResultDTO(String summary, String keywords, List<Counselor> recommendations) {
        this.summary = summary;
        this.keywords = keywords;
        this.recommendations = recommendations;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public List<Counselor> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<Counselor> recommendations) {
        this.recommendations = recommendations;
    }
}
