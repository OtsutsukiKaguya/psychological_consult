package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.dto.UpdateReservationDTO;
import com.example.demo.entity.Reservation;
import com.example.demo.mapper.ReservationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationMapper reservationMapper;

    // 创建预约（带整点校验 + 冲突检查）
    @PostMapping
    public Result createReservation(@RequestBody Reservation reservation) {
        LocalDateTime startTime = reservation.getReservationTime();
        // 校验是否为整点
        if (startTime.getMinute() != 0 || startTime.getSecond() != 0) {
            return Result.error("预约时间必须为整点（例如 10:00, 14:00）");
        }
        LocalDateTime endTime = startTime.plusHours(1);
        // 查询时间段内冲突的预约数量
        int conflictCount = reservationMapper.countConflictingReservations(
                reservation.getCounselorId(),
                startTime.toString(),
                endTime.toString()
        );
        // 查询咨询师支持的最大并发预约数
        Integer maxCount = reservationMapper.getCounselorSameTimeById(reservation.getCounselorId());
        if (maxCount == null) {
            return Result.error("未找到该咨询师信息");
        }
        if (conflictCount >= maxCount) {
            return Result.error("该时间段预约已满，请选择其他时间");
        }
        // 插入预约
        reservationMapper.insertReservation(reservation);
        Map<String, Object> data = new HashMap<>();
        data.put("message", "预约成功");
        data.put("user_id", reservation.getUserId());
        data.put("reservation_time", reservation.getReservationTime());
        data.put("counselor_id", reservation.getCounselorId());
        return Result.success(data);
    }


    // 2. 获取预约详情
    @GetMapping("/detail")
    public Result getReservationDetail(@RequestParam String userId,
                                       @RequestParam String counselorId,
                                       @RequestParam String reservationTime) {
        Reservation detail = reservationMapper.getReservationDetail(userId, counselorId, reservationTime);
        return Result.success(detail);
    }

    // 3. 用户取消预约
    @DeleteMapping
    public Result cancelReservation(@RequestParam String userId,
                                    @RequestParam String counselorId,
                                    @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime reservationTime) {
        reservationMapper.cancelReservation(userId, counselorId, reservationTime);
        return Result.success("预约已取消");
    }

    // 4. 查询用户的所有预约
    @GetMapping("/user/{userId}")
    public Result getUserReservations(@PathVariable String userId) {
        return Result.success(reservationMapper.getUserReservations(userId));
    }

    // 5. 查询咨询师的所有预约
    @GetMapping("/counselor/{counselorId}")
    public Result getCounselorReservations(@PathVariable String counselorId) {
        return Result.success(reservationMapper.getCounselorReservations(counselorId));
    }

    // 6. 修改预约时间或咨询师
    @PostMapping("/update")
    public Result updateReservation(@RequestBody UpdateReservationDTO dto) {
        reservationMapper.cancelReservation(dto.userId, dto.originalCounselorId, dto.originalTime);

        Reservation newReservation = new Reservation();
        newReservation.setUserId(dto.userId);
        newReservation.setCounselorId(dto.newCounselorId);
        newReservation.setReservationTime(dto.newTime);
        newReservation.setReservationDescription(dto.reservationDescription);

        return createReservation(newReservation);
    }


    // 7. 查询预约统计信息（如预约总数）
    @GetMapping("/statistics")
    public Result getReservationStatistics() {
        Map<String, Object> data = new HashMap<>();
        data.put("total", reservationMapper.countAllReservations());
        return Result.success(data);
    }

    // 8. 使用用户id和日期查询预约
    @GetMapping("/user/{userId}/date/{date}")
    public Result getUserReservationsByDate(@PathVariable String userId, @PathVariable String date) {
        // 调用ReservationMapper查询该用户在指定日期的所有预约
        List<Map<String, Object>> reservations = reservationMapper.getReservationsByUserAndDate(userId, date);

        if (reservations.isEmpty()) {
            return Result.error("没有找到该用户的预约记录");
        }

        return Result.success(reservations);
    }

}
