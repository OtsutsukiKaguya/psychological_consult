package com.example.demo.mapper;

import com.example.demo.dto.*;
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

    @Select("SELECT p.id, p.name, p.gender,p.age, p.id_picture_link, p.self_description, c.tag FROM person p JOIN duty_calendar dc JOIN counselor c ON  p.id = dc.staff_id AND p.id = c.id WHERE p.role = #{role} AND dc.duty_date=#{dutyDate}")
    public List<BackstageArrangementDTO> searchCounselorWorkDays(String role, String dutyDate);

    @Select("SELECT p.id, p.name, p.role FROM person p JOIN duty_calendar dc ON p.id = dc.staff_id WHERE dc.duty_date=#{dutyDate} AND (p.role='COUNSELOR' OR p.role='TUTOR')")
    public List<BackstageArrangement1DTO> getDutyByDate(String dutyDate);

    @Insert("INSERT INTO duty_calendar(staff_id, duty_date) VALUES (#{staffId}, #{dutyDate})")
    public int insertDuty(Duty_calendar duty_calendar);

    @Delete("DELETE FROM duty_calendar WHERE staff_id=#{id} and duty_date=#{dutyTime}")
    public int deleteDuty(String id, String dutyTime);

    @Insert("INSERT INTO ask_leave(staff_id, duty_date, leave_reason) VALUES (#{staffId}, #{dutyDate}, #{leaveReason})")
    public int insertLeave(Ask_leave ask_leave);

    @Select("SELECT * FROM ask_leave")
    public List<Ask_leave> searchLeave();

    @Select("SELECT * FROM ask_leave WHERE staff_id=#{staffId}")
    public List<Ask_leave> searchLeaveById(String staffId);

    @Update("UPDATE ask_leave SET is_agree =#{isAgree} WHERE (staff_id = #{staffId} and duty_date=#{dutyDate})")
    public int agreeLeave(String staffId, boolean isAgree, String dutyDate);

    @Insert("INSERT INTO bind(Counselor_id, tutor_id) VALUES (#{counselorId}, #{tutorId})")
    public int insertBind(Bind bind);

    @Delete("DELETE FROM bind WHERE counselor_id=#{cid} and tutor_id=#{tid}")
    public int deleteBind(String cid, String tid);

    @Select("SELECT * FROM bind WHERE counselor_id=#{id}")
    public List<Bind> searchBind(String id);

    @Select("SELECT c.id, p.role, b.tutor_id, c.average_rating, c.total_sessions, c.duty_arrangement FROM person p join counselor c join bind b on p.id=c.id AND c.id=b.counselor_id where p.role='COUNSELOR' ORDER BY c.id")
    public List<SearchCounselorDTO> searchCounselor();

    @Select("SELECT t.id, p.role, b.counselor_id, t.total_sessions, t.duty_arrangement FROM person p join tutor t join bind b on p.id=t.id AND t.id=b.tutor_id WHERE p.role='TUTOR' ORDER BY t.id")
    public List<SearchTutorDTO> searchTutor();

    @Select("SELECT p.name, p.gender, p.id, p.phone, u.urgent_name, u.urgent_phone, p.role, p.last_login_time FROM person p join user u on p.id=u.id where p.role='USER' ORDER BY u.id")
    public List<SearchUserDTO> searchUser();


}
