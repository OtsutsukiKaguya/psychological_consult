package com.example.demo.mapper;

import com.example.demo.dto.BackstageArrangement1DTO;
import com.example.demo.dto.BackstageArrangementDTO;
import com.example.demo.entity.Ask_leave;
import com.example.demo.entity.Bind;
import com.example.demo.entity.Duty_calendar;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface BackstageArrangementMapper {

    @Select("SELECT * FROM duty_calendar where staff_id=#{id}")
    public List<Duty_calendar> searchWorkDays(String id);

    @Select("SELECT p.id, p.name, p.gender,p.age, p.id_picture_link, p.self_description FROM person p JOIN duty_calendar dc ON p.id = dc.staff_id WHERE p.role = #{role} AND dc.duty_date=#{dutyDate}")
    public List<BackstageArrangementDTO> searchCounselorWorkDays(String role, String dutyDate);

    @Select("SELECT p.id, p.name, p.role FROM person p JOIN duty_calendar dc ON p.id = dc.staff_id WHERE dc.duty_date=#{dutyDate}")
    public List<BackstageArrangement1DTO> getDutyByDate(String dutyDate);

    @Insert("INSERT INTO duty_calendar(staff_id, duty_date) VALUES (#{staffId}, #{dutyDate})")
    public static int insertDuty(Duty_calendar duty_calendar) {
        return 1;
    }

    @Delete("DELETE FROM duty_calendar WHERE id=#{staffId} and duty_date=#{dutyDate}")
    public static int deleteDuty(String id, String dutyDate) {
        return 1;
    }

    @Insert("INSERT INTO ask_leave(staff_id, duty_date, leave_reason) VALUES (#{staffId}, #{dutyTime}, #{leaveReason})")
    public static int insertLeave(Ask_leave ask_leave) {
        return 1;
    }

    @Select("SELECT * FROM ask_leave")
    public List<Ask_leave> searchLeave();

    @Select("SELECT * FROM ask_leave WHERE staff_id=#{staffId}")
    public List<Ask_leave> searchLeaveById(String staffId);

    @Update("UPDATE ask_leave SET (is_agree =#{isAgree} and leave_comment=#{leaveComment}) WHERE staff_id = #{staffId}")
    public static int agreeLeave(String staffId, boolean isAgree, String leaveComment) {
        return 1;
    }

    @Insert("INSERT INTO bind(Counselor_id, tutor_id) VALUES (#{counselorId}, #{tutorId})")
    public static int insertBind(Bind bind) {
        return 1;
    }

    @Delete("DELETE FROM bind WHERE counselor_id=#{cid} and tutor_id=#{tid}")
    public static int deleteBind(String cid, String tid) {
        return 1;
    }

    @Select("SELECT * FROM bind WHERE counselor_id=#{id}")
    public List<Bind> searchBind(String id);

}
