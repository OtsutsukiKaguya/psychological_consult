package com.example.demo.controller;

import com.example.demo.dto.AIDiagnosisRequestDTO;
import com.example.demo.dto.AIDiagnosisResponseDTO;
import com.example.demo.dto.DiagnosisResultDTO;
import com.example.demo.entity.Counselor;
import com.example.demo.service.AIDiagnosisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai-diagnosis")
public class AIDiagnosisController {

    @Autowired
    private AIDiagnosisService aiDiagnosisService;

    @PostMapping("/ask")
    public AIDiagnosisResponseDTO askAiDiagnosis(@RequestBody AIDiagnosisRequestDTO request) {
        return aiDiagnosisService.getDiagnosis(request);
    }

    @PostMapping("/finish")
    public DiagnosisResultDTO finishAndRecommend(@RequestParam String userId) {
        return aiDiagnosisService.finishAndRecommend(userId);
    }
}
