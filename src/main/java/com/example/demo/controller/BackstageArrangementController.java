package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.dto.BackstageArrangement1DTO;
import com.example.demo.dto.BackstageArrangementDTO;
import com.example.demo.entity.Ask_leave;
import com.example.demo.entity.Bind;
import com.example.demo.entity.Duty_calendar;
import com.example.demo.mapper.BackstageArrangementMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BackstageArrangementController {

    private static final Logger logger = LoggerFactory.getLogger(BackstageArrangementController.class);

    @Autowired
    private BackstageArrangementMapper backstageArrangementMapper;

    // 查询指定 ID 的值班记录
    @GetMapping("/duty/getdutybyid/{id}")
    public Result searchWorkDays(@PathVariable("id") String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID cannot be empty");
        }
        List<Duty_calendar> list = backstageArrangementMapper.searchWorkDays(id);
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("Duty records not found for ID: " + id);
        }
        logger.info("Queried duty records for ID: {}", id);
        return Result.success(list);
    }

    // 查询指定日期和角色的值班记录
    @GetMapping("/duty/getdutybytodaycounselor/{role}/{dutyDate}")
    public Result searchCounselorWorkDays(@PathVariable("role") String role, @PathVariable("dutyDate") String dutyDate) {
        if (role == null || role.trim().isEmpty()) {
            throw new IllegalArgumentException("Role cannot be empty");
        }
        if (dutyDate == null || dutyDate.trim().isEmpty()) {
            throw new IllegalArgumentException("Duty date cannot be empty");
        }
        List<BackstageArrangementDTO> list = backstageArrangementMapper.searchCounselorWorkDays(role, dutyDate);
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("Duty records not found for role: " + role + " on date: " + dutyDate);
        }
        logger.info("Queried counselor duty records for role: {} on date: {}", role, dutyDate);
        return Result.success(list);
    }

    // 查询指定日期的值班记录
    @GetMapping("/duty/getdutybydate/{dutyDate}")
    public Result getDutyByDate(@PathVariable("dutyDate") String dutyDate) {
        if (dutyDate == null || dutyDate.trim().isEmpty()) {
            throw new IllegalArgumentException("Duty date cannot be empty");
        }
        List<BackstageArrangement1DTO> list = backstageArrangementMapper.getDutyByDate(dutyDate);
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("Duty records not found for date: " + dutyDate);
        }
        logger.info("Queried duty records for date: {}", dutyDate);
        return Result.success(list);
    }

    // 添加值班记录
    @PostMapping("/duty/addduty")
    public Result insertDuty(@RequestBody Duty_calendar dutyCalendar) {
        if (dutyCalendar == null || dutyCalendar.getStaffId() == null || dutyCalendar.getStaffId().trim().isEmpty()) {
            throw new IllegalArgumentException("Duty calendar staff ID cannot be empty");
        }
        if (dutyCalendar.getDutyDate() == null) {
            throw new IllegalArgumentException("Duty date cannot be null");
        }
        int i = backstageArrangementMapper.insertDuty(dutyCalendar);
        if (i <= 0) {
            throw new RuntimeException("Failed to insert duty record for staff ID: " + dutyCalendar.getStaffId());
        }
        logger.info("Inserted duty record with staff ID: {}", dutyCalendar.getStaffId());
        return Result.success("Insert successful");
    }

    // 删除值班记录
    @PostMapping("/duty/deleteduty")
    public Result deleteDuty(@RequestParam("id") String id, @RequestParam("dutyDate") String dutyDate) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID cannot be empty");
        }
        if (dutyDate == null || dutyDate.trim().isEmpty()) {
            throw new IllegalArgumentException("Duty date cannot be empty");
        }
        int i = backstageArrangementMapper.deleteDuty(id, dutyDate);
        if (i <= 0) {
            throw new IllegalArgumentException("Duty record not found for ID: " + id + " on date: " + dutyDate);
        }
        logger.info("Deleted duty record for ID: {} on date: {}", id, dutyDate);
        return Result.success("Delete successful");
    }

    // 添加请假记录
    @PostMapping("/leave/addleave")
    public Result insertLeave(@RequestBody Ask_leave askLeave) {
        if (askLeave == null || askLeave.getStaffId() == null || askLeave.getStaffId().trim().isEmpty()) {
            throw new IllegalArgumentException("Staff ID cannot be empty");
        }
        int i = backstageArrangementMapper.insertLeave(askLeave);
        if (i <= 0) {
            throw new RuntimeException("Failed to insert leave record for staff ID: " + askLeave.getStaffId());
        }
        logger.info("Inserted leave record for staff ID: {}", askLeave.getStaffId());
        return Result.success("Insert successful");
    }

    // 查询所有请假记录
    @GetMapping("/leave/showleave")
    public Result searchLeave() {
        List<Ask_leave> list = backstageArrangementMapper.searchLeave();
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("No leave records found");
        }
        logger.info("Queried all leave records");
        return Result.success(list);
    }

    // 查询指定员工的请假记录
    @GetMapping("/leave/showleavebyid/{staffId}")
    public Result searchLeaveById(@PathVariable("staffId") String staffId) {
        if (staffId == null || staffId.trim().isEmpty()) {
            throw new IllegalArgumentException("Staff ID cannot be empty");
        }
        List<Ask_leave> list = backstageArrangementMapper.searchLeaveById(staffId);
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("Leave records not found for staff ID: " + staffId);
        }
        logger.info("Queried leave records for staff ID: {}", staffId);
        return Result.success(list);
    }

    // 同意或拒绝请假
    @PostMapping("/leave/addleaveagree")
    public Result agreeLeave(@RequestParam("staffId") String staffId, @RequestParam("isAgree") boolean isAgree, @RequestParam("leaveDate") String leaveDate) {
        if (staffId == null || staffId.trim().isEmpty()) {
            throw new IllegalArgumentException("Staff ID cannot be empty");
        }
        if (leaveDate == null || leaveDate.trim().isEmpty()) {
            throw new IllegalArgumentException("Leave date cannot be empty");
        }
        int i = backstageArrangementMapper.agreeLeave(staffId, isAgree, leaveDate);
        if (i <= 0) {
            throw new IllegalArgumentException("Leave record not found for staff ID: " + staffId + " on date: " + leaveDate);
        }
        logger.info("Processed leave agreement for staff ID: {} on date: {}, isAgree: {}", staffId, leaveDate, isAgree);
        return Result.success("Leave agreement processed successfully");
    }

    // 添加绑定关系
    @PostMapping("/bind/addbind")
    public Result insertBind(@RequestBody Bind bind) {
        if (bind == null || bind.getCounselorId() == null || bind.getCounselorId().trim().isEmpty() || bind.getTutorId() == null || bind.getTutorId().trim().isEmpty()) {
            throw new IllegalArgumentException("Counselor ID and Tutor ID cannot be empty");
        }
        int i = backstageArrangementMapper.insertBind(bind);
        if (i <= 0) {
            throw new RuntimeException("Failed to insert bind record for Counselor ID: " + bind.getCounselorId() + " and Tutor ID: " + bind.getTutorId());
        }
        logger.info("Inserted bind record for Counselor ID: {} and Tutor ID: {}", bind.getCounselorId(), bind.getTutorId());
        return Result.success("Insert successful");
    }

    // 删除绑定关系
    @PostMapping("/bind/deletebind")
    public Result deleteBind(@RequestParam("id1") String counselorId, @RequestParam("id2") String tutorId) {
        if (counselorId == null || counselorId.trim().isEmpty() || tutorId == null || tutorId.trim().isEmpty()) {
            throw new IllegalArgumentException("Counselor ID and Tutor ID cannot be empty");
        }
        int i = backstageArrangementMapper.deleteBind(counselorId, tutorId);
        if (i <= 0) {
            throw new IllegalArgumentException("Bind record not found for Counselor ID: " + counselorId + " and Tutor ID: " + tutorId);
        }
        logger.info("Deleted bind record for Counselor ID: {} and Tutor ID: {}", counselorId, tutorId);
        return Result.success("Delete successful");
    }

    // 查询绑定关系
    @GetMapping("/bind/showbind/{id}")
    public Result searchBind(@PathVariable("id") String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID cannot be empty");
        }
        List<Bind> list = backstageArrangementMapper.searchBind(id);
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("Bind records not found for ID: " + id);
        }
        logger.info("Queried bind records for ID: {}", id);
        return Result.success(list);
    }
}