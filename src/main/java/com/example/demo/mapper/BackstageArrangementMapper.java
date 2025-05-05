package com.example.demo.mapper;

import com.example.demo.dto.*;
import com.example.demo.entity.Ask_leave;
import com.example.demo.entity.Bind;
import com.example.demo.entity.Counselor;
import com.example.demo.entity.Duty_calendar;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

public interface BackstageArrangementMapper {

    @Select("SELECT * FROM duty_calendar where staff_id=#{id}")
    public List<Duty_calendar> searchWorkDays(String id);

    @Select("SELECT p.id, p.name, p.gender,p.age, p.id_picture_link, p.self_description, c.tag FROM person p JOIN duty_calendar dc JOIN counselor c ON  p.id = dc.staff_id AND p.id = c.id WHERE p.role = #{role} AND dc.duty_date=#{dutyDate} and dc.is_leave=0")
    public List<BackstageArrangementDTO> searchCounselorWorkDays(String role, String dutyDate);

    @Select("SELECT p.id, p.name, p.role FROM person p JOIN duty_calendar dc ON p.id = dc.staff_id WHERE dc.is_leave=0 and dc.duty_date=#{dutyDate} AND (p.role='COUNSELOR' OR p.role='TUTOR')")
    public List<BackstageArrangement1DTO> getDutyByDate(String dutyDate);

    @Insert("INSERT INTO duty_calendar(staff_id, duty_date) VALUES (#{staffId}, #{dutyDate})")
    public int insertDuty(Duty_calendar duty_calendar);

    @Delete("DELETE FROM duty_calendar WHERE staff_id=#{id} and duty_date=#{dutyDate}")
    public int deleteDuty(String id, String dutyDate);

    @Insert("INSERT INTO ask_leave(staff_id, duty_date, leave_reason) VALUES (#{staffId}, #{dutyDate}, #{leaveReason})")
    public int insertLeave1(String staffId, LocalDate dutyDate, String leaveReason);

    @Update("UPDATE duty_calendar SET is_leave=1 WHERE staff_id=#{staffId} and duty_date=#{dutyDate}")
    public int insertleave2(String staffId, LocalDate dutyDate);

    @Select("SELECT p.role, al.staff_id, al.duty_date, al.leave_reason, al.is_agree, al.leave_comment FROM ask_leave al JOIN person p on p.id=al.staff_id")
    public List<LeaveDTO> searchLeave();

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

    @Select("SELECT * from person p join counselor c ON p.id=c.id where p.name LIKE CONCAT('%', #{name}, '%')")
    public List<SearchCounselorByNameDTO> searchCounselorByName(String name);

    @Select("SELECT * FROM person p JOIN counselor c ON p.id = c.id WHERE c.tag LIKE CONCAT('%', #{tag}, '%')")
    public List<SearchCounselorByNameDTO> searchCounselorByTag(String tag);

    @Select("SELECT s2.user_id FROM session_participants s1 join session_participants s2 on s1.session_id=s2.session_id where s2.role='COUNSELOR' and s1.user_id=#{id} and s1.role='USER'")
    public List<SearchCounselorByIdDTO> searchCounselorById(String id);

    @Select("SELECT counselor_sametime FROM counselor WHERE id=#{id}")
    public Integer counselorState1(String id);

    @Select("SELECT COUNT(*) FROM chat_sessions cs join session_participants sp on cs.id=sp.session_id WHERE sp.user_id=#{id} and sp.role='COUNSELOR' and cs.ended=0")
    public int counselorState2(String id);

    @Update("UPDATE person SET state='BUSY' WHERE id=#{id}")
    public int updateState(String id);

    @Select("SELECT COUNT(*) FROM chat_sessions cs join session_participants sp on cs.id=sp.session_id WHERE sp.role='COUNSELOR' and cs.ended=0")
    public int conversationNow();

    /** 获取所有可调度的咨询师 ID 列表 */
    @Select("SELECT id FROM counselor")  // 假设有 counselor 表
    List<String> findAllCounselorIds();

    /** 获取指定咨询师的请假日期 */
    @Select("SELECT duty_date FROM duty_calendar " +
            "WHERE staff_id = #{counselorId} AND is_leave = TRUE " +
            "  AND duty_date BETWEEN #{start} AND #{end}")
    List<LocalDate> findLeaveDatesByCounselor(@Param("counselorId") String counselorId,
                                              @Param("start") LocalDate start,
                                              @Param("end") LocalDate end);

    /** 获取指定日期范围内已存在的排班日期（非请假） */
    @Select("SELECT duty_date FROM duty_calendar " +
            "WHERE is_leave = FALSE AND duty_date BETWEEN #{start} AND #{end}")
    List<LocalDate> findExistingDutyDates(@Param("start") LocalDate start,
                                          @Param("end") LocalDate end);

    /** 批量插入排班记录 */
    @Insert({
            "<script>",
            "INSERT INTO duty_calendar (staff_id, duty_date, is_leave, shift_type)",
            "VALUES",
            "<foreach collection='records' item='item' separator=','>",
            "(#{item.staffId}, #{item.dutyDate}, #{item.isLeave}, #{item.shiftType})",
            "</foreach>",
            "</script>"
    })
    int batchInsertDuties(@Param("records") List<Duty_calendar> records);

    /** ① 取出所有 “咨询师 → 督导” 绑定 */
    @Select("SELECT counselor_id, tutor_id FROM bind")
    @Results({
            @Result(column="counselor_id", property="counselorId"),
            @Result(column="tutor_id",     property="tutorId")
    })
    List<Bind> findAllBinds();
}
