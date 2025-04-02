package com.example.demo.service;

import com.example.demo.dto.AIDiagnosisRequestDTO;
import com.example.demo.dto.AIDiagnosisResponseDTO;
import com.example.demo.dto.DiagnosisResultDTO;
import com.example.demo.entity.Counselor;

import java.util.List;

public interface AIDiagnosisService {
    AIDiagnosisResponseDTO getDiagnosis(AIDiagnosisRequestDTO request);
    DiagnosisResultDTO finishAndRecommend(String userId);
}

