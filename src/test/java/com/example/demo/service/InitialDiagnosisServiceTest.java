package com.example.demo.service;

import com.example.demo.entity.DiagnosisResult;
import com.example.demo.repository.DiagnosisRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.util.List;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class InitialDiagnosisServiceTest {

    @Mock DiagnosisRepository repo;
    @InjectMocks InitialDiagnosisService service;

    @Test
    @DisplayName("AI 初诊——返回非空推荐列表")
    void diagnose_returnsRecommendations() {
        when(service.runModel(anyMap()))
                .thenReturn(List.of(1L, 2L, 3L));

        List<Long> recs = service.diagnose(1L, Map.of("q1","a"));
        assertFalse(recs.isEmpty());
        assertEquals(3, recs.size());
    }
}

