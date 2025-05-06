package com.example.demo.service;

import com.example.demo.entity.Reservation;
import com.example.demo.repository.ReservationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock ReservationRepository reservationRepository;
    @InjectMocks ReservationService reservationService;

    @Test
    @DisplayName("创建预约——应保存并返回带 ID 的预约")
    void createReservation_savesAndReturns() {
        Reservation r = new Reservation();
        r.setUserId(1L);
        r.setCounselorId(2L);
        r.setStartTime(LocalDateTime.of(2025,5,8,14,0));
        r.setEndTime(LocalDateTime.of(2025,5,8,15,0));

        Reservation saved = new Reservation();
        saved.setId(100L);
        when(reservationRepository.save(any())).thenReturn(saved);

        Reservation result = reservationService.create(r);
        assertEquals(100L, result.getId());
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    @DisplayName("修改预约——越权时抛 403 异常")
    void updateReservation_forbidden() {
        when(reservationRepository.findById(1L))
                .thenReturn(java.util.Optional.of(new Reservation(){{
                    setUserId(9L);
                }}));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> reservationService.update(1L, 1L, "comment"));
        assertTrue(ex.getMessage().contains("Forbidden"));
    }
}
