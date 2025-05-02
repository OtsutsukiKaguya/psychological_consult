package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.dto.*;
import com.example.demo.entity.Ask_leave;
import com.example.demo.entity.Bind;
import com.example.demo.entity.Duty_calendar;
import com.example.demo.mapper.BackstageArrangementMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
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
        try {
            if (id == null || id.trim().isEmpty()) {
                return Result.error("ID cannot be empty");
            }
            List<Duty_calendar> list = backstageArrangementMapper.searchWorkDays(id);
            if (list == null || list.isEmpty()) {
                return Result.error("Duty records not found for ID: " + id);
            }
            logger.info("Queried duty records for ID: {}", id);
            return Result.success(list);
        } catch (Exception e) {
            logger.error("Error querying duty records for ID: {}", id, e);
            return Result.error("An error occurred while querying duty records");
        }
    }

    // 查询指定日期和角色的值班记录
    @GetMapping("/duty/getdutybytodaycounselor/{role}/{dutyDate}")
    public Result searchCounselorWorkDays(@PathVariable("role") String role, @PathVariable("dutyDate") String dutyDate) {
        try {
            if (role == null || role.trim().isEmpty()) {
                return Result.error("Role cannot be empty");
            }
            if (dutyDate == null || dutyDate.trim().isEmpty()) {
                return Result.error("Duty date cannot be empty");
            }
            List<BackstageArrangementDTO> list = backstageArrangementMapper.searchCounselorWorkDays(role, dutyDate);
            if (list == null || list.isEmpty()) {
                return Result.error("Duty records not found for role: " + role + " on date: " + dutyDate);
            }
            logger.info("Queried counselor duty records for role: {} on date: {}", role, dutyDate);
            return Result.success(list);
        } catch (Exception e) {
            logger.error("Error querying counselor duty records for role: {} on date: {}", role, dutyDate, e);
            return Result.error("An error occurred while querying counselor duty records");
        }
    }

    // 查询指定日期的值班记录
    @GetMapping("/duty/getdutybydate/{dutyDate}")
    public Result getDutyByDate(@PathVariable("dutyDate") String dutyDate) {
        try {
            if (dutyDate == null || dutyDate.trim().isEmpty()) {
                return Result.error("Duty date cannot be empty");
            }
            List<BackstageArrangement1DTO> list = backstageArrangementMapper.getDutyByDate(dutyDate);
            if (list == null || list.isEmpty()) {
                return Result.error("Duty records not found for date: " + dutyDate);
            }
            logger.info("Queried duty records for date: {}", dutyDate);
            return Result.success(list);
        } catch (Exception e) {
            logger.error("Error querying duty records for date: {}", dutyDate, e);
            return Result.error("An error occurred while querying duty records");
        }
    }

    // 添加值班记录
    @PostMapping("/duty/addduty")
    public Result insertDuty(@RequestBody Duty_calendar dutyCalendar) {
        try {
            if (dutyCalendar == null || dutyCalendar.getStaffId() == null || dutyCalendar.getStaffId().trim().isEmpty()) {
                return Result.error("Duty calendar staff ID cannot be empty");
            }
            if (dutyCalendar.getDutyDate() == null) {
                return Result.error("Duty date cannot be null");
            }
            int i = backstageArrangementMapper.insertDuty(dutyCalendar);
            if (i <= 0) {
                return Result.error("Failed to insert duty record for staff ID: " + dutyCalendar.getStaffId());
            }
            logger.info("Inserted duty record with staff ID: {}", dutyCalendar.getStaffId());
            return Result.success("Insert successful");
        } catch (Exception e) {
            logger.error("Error inserting duty record for staff ID: {}", dutyCalendar != null ? dutyCalendar.getStaffId() : "null", e);
            return Result.error("An error occurred while inserting duty record");
        }
    }

    // 删除值班记录
    @PostMapping("/duty/deleteduty")
    public Result deleteDuty(@RequestParam("id") String id, @RequestParam("dutyDate") String dutyDate) {
        try {
            if (id == null || id.trim().isEmpty()) {
                return Result.error("ID cannot be empty");
            }
            if (dutyDate == null || dutyDate.trim().isEmpty()) {
                return Result.error("Duty date cannot be empty");
            }
            int i = backstageArrangementMapper.deleteDuty(id, dutyDate);
            if (i <= 0) {
                return Result.error("Duty record not found for ID: " + id + " on date: " + dutyDate);
            }
            logger.info("Deleted duty record for ID: {} on date: {}", id, dutyDate);
            return Result.success("Delete successful");
        } catch (Exception e) {
            logger.error("Error deleting duty record for ID: {} on date: {}", id, dutyDate, e);
            return Result.error("An error occurred while deleting duty record");
        }
    }

    // 添加请假记录
    @PostMapping("/leave/addleave")
    public Result insertLeave(@RequestBody Ask_leave askLeave) {
        try {
            // 验证输入
            if (askLeave == null || askLeave.getStaffId() == null || askLeave.getStaffId().trim().isEmpty()) {
                return Result.error("员工ID不能为空");
            }
            if (askLeave.getdutyDate() == null) {
                return Result.error("请假日期不能为空");
            }
            if (askLeave.getLeaveReason() == null || askLeave.getLeaveReason().trim().isEmpty()) {
                return Result.error("请假原因不能为空");
            }

            // 调用 insertLeave1 插入请假记录
            int leaveInsertResult = backstageArrangementMapper.insertLeave1(
                    askLeave.getStaffId(),
                    askLeave.getdutyDate(),
                    askLeave.getLeaveReason()
            );
            if (leaveInsertResult <= 0) {
                logger.error("插入请假记录失败，员工ID: {}", askLeave.getStaffId());
                return Result.error("插入请假记录失败，员工ID: " + askLeave.getStaffId());
            }

            // 调用 insertleave2 更新 duty_calendar 表
            int dutyUpdateResult = backstageArrangementMapper.insertleave2(
                    askLeave.getStaffId(),
                    askLeave.getdutyDate()
            );
            if (dutyUpdateResult <= 0) {
                logger.error("更新出勤日历失败，员工ID: {}, 日期: {}", askLeave.getStaffId(), askLeave.getdutyDate());
                throw new RuntimeException("更新出勤日历失败");
            }

            logger.info("成功插入请假记录并更新出勤日历，员工ID: {}", askLeave.getStaffId());
            return Result.success("请假记录插入成功");

        } catch (Exception e) {
            logger.error("插入请假记录或更新出勤日历时发生错误，员工ID: {}", askLeave != null ? askLeave.getStaffId() : "null", e);
            throw new RuntimeException("插入请假记录或更新出勤日历时发生错误", e);
        }
    }

    // 查询所有请假记录
    @GetMapping("/leave/showleave")
    public Result searchLeave() {
        try {
            List<LeaveDTO> leaves = backstageArrangementMapper.searchLeave();
            if (leaves == null || leaves.isEmpty()) {
                logger.warn("No leave records found");
                return Result.error("No leave records found");
            }
            logger.info("Successfully retrieved {} leave records", leaves.size());
            return Result.success(leaves);
        } catch (Exception e) {
            logger.error("Error retrieving leave records", e);
            return Result.error("An error occurred while retrieving leave records");
        }
    }

    // 查询指定员工的请假记录
    @GetMapping("/leave/showleavebyid/{staffId}")
    public Result searchLeaveById(@PathVariable("staffId") String staffId) {
        try {
            if (staffId == null || staffId.trim().isEmpty()) {
                return Result.error("Staff ID cannot be empty");
            }
            List<Ask_leave> list = backstageArrangementMapper.searchLeaveById(staffId);
            if (list == null || list.isEmpty()) {
                return Result.error("Leave records not found for staff ID: " + staffId);
            }
            logger.info("Queried leave records for staff ID: {}", staffId);
            return Result.success(list);
        } catch (Exception e) {
            logger.error("Error querying leave records for staff ID: {}", staffId, e);
            return Result.error("An error occurred while querying leave records");
        }
    }

    // 同意或拒绝请假
    @PostMapping("/leave/addleaveagree")
    public Result agreeLeave(@RequestParam("staffId") String staffId, @RequestParam("isAgree") boolean isAgree, @RequestParam("dutyDate") String dutyDate) {
        try {
            if (staffId == null || staffId.trim().isEmpty()) {
                return Result.error("Staff ID cannot be empty");
            }
            if (dutyDate == null || dutyDate.trim().isEmpty()) {
                return Result.error("Leave date cannot be empty");
            }
            int i = backstageArrangementMapper.agreeLeave(staffId, isAgree, dutyDate);
            if (i <= 0) {
                return Result.error("Leave record not found for staff ID: " + staffId + " on date: " + dutyDate);
            }
            logger.info("Processed leave agreement for staff ID: {} on date: {}, isAgree: {}", staffId, dutyDate, isAgree);
            return Result.success("Leave agreement processed successfully");
        } catch (Exception e) {
            logger.error("Error processing leave agreement for staff ID: {} on date: {}", staffId, dutyDate, e);
            return Result.error("An error occurred while processing leave agreement");
        }
    }

    // 添加绑定关系
    @PostMapping("/bind/addbind")
    public Result insertBind(@RequestBody Bind bind) {
        try {
            if (bind == null || bind.getCounselorId() == null || bind.getCounselorId().trim().isEmpty() || bind.getTutorId() == null || bind.getTutorId().trim().isEmpty()) {
                return Result.error("Counselor ID and Tutor ID cannot be empty");
            }
            int i = backstageArrangementMapper.insertBind(bind);
            if (i <= 0) {
                return Result.error("Failed to insert bind record for Counselor ID: " + bind.getCounselorId() + " and Tutor ID: " + bind.getTutorId());
            }
            logger.info("Inserted bind record for Counselor ID: {} and Tutor ID: {}", bind.getCounselorId(), bind.getTutorId());
            return Result.success("Insert successful");
        } catch (Exception e) {
            logger.error("Error inserting bind record for Counselor ID: {} and Tutor ID: {}", bind != null ? bind.getCounselorId() : "null", bind != null ? bind.getTutorId() : "null", e);
            return Result.error("An error occurred while inserting bind record");
        }
    }

    // 删除绑定关系
    @PostMapping("/bind/deletebind")
    public Result deleteBind(@RequestParam("id1") String counselorId, @RequestParam("id2") String tutorId) {
        try {
            if (counselorId == null || counselorId.trim().isEmpty() || tutorId == null || tutorId.trim().isEmpty()) {
                return Result.error("Counselor ID and Tutor ID cannot be empty");
            }
            int i = backstageArrangementMapper.deleteBind(counselorId, tutorId);
            if (i <= 0) {
                return Result.error("Bind record not found for Counselor ID: " + counselorId + " and Tutor ID: " + tutorId);
            }
            logger.info("Deleted bind record for Counselor ID: {} and Tutor ID: {}", counselorId, tutorId);
            return Result.success("Delete successful");
        } catch (Exception e) {
            logger.error("Error deleting bind record for Counselor ID: {} and Tutor ID: {}", counselorId, tutorId, e);
            return Result.error("An error occurred while deleting bind record");
        }
    }

    // 查询绑定关系
    @GetMapping("/bind/showbind/{id}")
    public Result searchBind(@PathVariable("id") String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                return Result.error("ID cannot be empty");
            }
            List<Bind> list = backstageArrangementMapper.searchBind(id);
            if (list == null || list.isEmpty()) {
                return Result.error("Bind records not found for ID: " + id);
            }
            logger.info("Queried bind records for ID: {}", id);
            return Result.success(list);
        } catch (Exception e) {
            logger.error("Error querying bind records for ID: {}", id, e);
            return Result.error("An error occurred while querying bind records");
        }
    }

    @GetMapping("/searchCounselor")
    public Result searchCounselor() {
        try {
            List<SearchCounselorDTO> counselors = backstageArrangementMapper.searchCounselor();
            if (counselors == null || counselors.isEmpty()) {
                return Result.error("No counselors found");
            }
            logger.info("Retrieved {} counselors with binding information", counselors.size());
            return Result.success(counselors);
        } catch (Exception e) {
            logger.error("Error retrieving counselors with binding information", e);
            return Result.error("An error occurred while retrieving counselors");
        }
    }

    @GetMapping("/tutor/search")
    public Result searchTutor() {
        try {
            List<SearchTutorDTO> tutors = backstageArrangementMapper.searchTutor();
            if (tutors == null || tutors.isEmpty()) {
                return Result.error("No tutors found");
            }
            logger.info("Retrieved {} tutors with binding information", tutors.size());
            return Result.success(tutors);
        } catch (Exception e) {
            logger.error("Error retrieving tutors with binding information", e);
            return Result.error("An error occurred while retrieving tutors");
        }
    }

    @GetMapping("/user/search")
    public Result searchUser() {
        try {
            List<SearchUserDTO> users = backstageArrangementMapper.searchUser();
            if (users == null || users.isEmpty()) {
                return Result.error("No users found");
            }
            logger.info("Retrieved {} users", users.size());
            return Result.success(users);
        } catch (Exception e) {
            logger.error("Error retrieving users", e);
            return Result.error("An error occurred while retrieving users");
        }
    }

    @GetMapping("/searchCounselorByName")
    public Result searchCounselorByName(@RequestParam String name) {
        try {
            List<SearchCounselorByNameDTO> counselorList = backstageArrangementMapper.searchCounselorByName(name);
            if (!counselorList.isEmpty()) {
                return Result.success(counselorList);
            } else {
                return Result.error("未找到匹配的咨询师信息");
            }
        } catch (Exception e) {
            return Result.error("服务器错误: " + e.getMessage());
        }
    }

    @GetMapping("/searchCounselorByTag")
    public Result searchCounselorByTag(@RequestParam String tag) {
        try {
            List<SearchCounselorByNameDTO> counselorList = backstageArrangementMapper.searchCounselorByTag(tag);
            if (!counselorList.isEmpty()) {
                return Result.success(counselorList);
            } else {
                return Result.error("未找到匹配的咨询师信息");
            }
        } catch (Exception e) {
            return Result.error("服务器错误: " + e.getMessage());
        }
    }

    @GetMapping("/searchCounselorById")
    public Result searchCounselorById(@RequestParam String id) {
        try {
            List<SearchCounselorByIdDTO> counselorList = backstageArrangementMapper.searchCounselorById(id);
            if (!counselorList.isEmpty()) {
                return Result.success(counselorList);
            } else {
                return Result.error("未找到匹配的咨询师信息");
            }
        } catch (Exception e) {
            return Result.error("服务器错误: " + e.getMessage());
        }
    }

    @PostMapping("/counselor/check-state")
    public Result checkCounselorState(@RequestParam("id") String id) {
        try {
            // 参数校验
            if (id == null || id.trim().isEmpty()) {
                return Result.error("咨询师 ID 不能为空");
            }

            // 查询最大同时会话数
            Integer maxSessions = backstageArrangementMapper.counselorState1(id);
            if (maxSessions == null) {
                logger.warn("No counselor found with ID: {}", id);
                return Result.error("未找到咨询师信息，ID: " + id);
            }

            // 查询当前正在进行的会话数
            int currentSessions = backstageArrangementMapper.counselorState2(id);

            // 检查是否达到最大会话数
            if (currentSessions >= maxSessions) {
                // 更新状态为 BUSY
                int updateResult = backstageArrangementMapper.updateState(id);
                if (updateResult <= 0) {
                    logger.warn("Failed to update state to BUSY for ID: {}", id);
                    return Result.error("更新状态失败，ID: " + id);
                }
                logger.info("Updated counselor state to BUSY for ID: {}, currentSessions: {}, maxSessions: {}",
                        id, currentSessions, maxSessions);
                return Result.success("咨询师状态已更新为 BUSY");
            } else {
                logger.info("Counselor state not updated for ID: {}, currentSessions: {}, maxSessions: {}",
                        id, currentSessions, maxSessions);
                return Result.success("咨询师未达到最大会话数，状态未更新");
            }

        } catch (Exception e) {
            logger.error("Error checking counselor state for ID: {}", id, e);
            return Result.error("检查咨询师状态时发生错误: " + e.getMessage());
        }
    }

    @GetMapping("/conversationNow")
    public Result conversationNow() {
        try {
            int count = backstageArrangementMapper.conversationNow();
            return Result.success(count);
        } catch (Exception e) {
            return Result.error("服务器错误: " + e.getMessage());
        }
    }
}