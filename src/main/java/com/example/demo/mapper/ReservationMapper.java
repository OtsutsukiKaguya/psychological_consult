package com.example.demo.mapper;

import com.example.demo.entity.Reservation;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface ReservationMapper {

    @Insert("INSERT INTO reservation(user_id, reservation_time, counselor_id, reservation_description) " +
            "VALUES(#{userId}, #{reservationTime}, #{counselorId}, #{reservationDescription})")
    int insertReservation(Reservation reservation);

    @Select("SELECT * FROM reservation WHERE user_id = #{userId} AND counselor_id = #{counselorId} AND reservation_time = #{reservationTime}")
    Reservation getReservationDetail(@Param("userId") String userId,
                                     @Param("counselorId") String counselorId,
                                     @Param("reservationTime") String reservationTime);

    @Delete("DELETE FROM reservation WHERE user_id = #{userId} AND counselor_id = #{counselorId} AND reservation_time = #{reservationTime}")
    int cancelReservation(@Param("userId") String userId,
                          @Param("counselorId") String counselorId,
                          @Param("reservationTime") LocalDateTime reservationTime);

    @Select("SELECT * FROM reservation WHERE user_id = #{userId} ORDER BY reservation_time DESC")
    List<Reservation> getUserReservations(@Param("userId") String userId);

    @Select("SELECT * FROM reservation WHERE counselor_id = #{counselorId} ORDER BY reservation_time DESC")
    List<Reservation> getCounselorReservations(@Param("counselorId") String counselorId);

    @Select("SELECT COUNT(*) FROM reservation")
    int countAllReservations();

    @Select("SELECT COUNT(*) FROM reservation " +
            "WHERE counselor_id = #{counselorId} " +
            "AND (" +
            "   reservation_time < #{endTime} " +
            "   AND DATE_ADD(reservation_time, INTERVAL 1 HOUR) > #{startTime}" +
            ")")
    int countConflictingReservations(@Param("counselorId") String counselorId,
                                     @Param("startTime") String startTime,
                                     @Param("endTime") String endTime);

    @Select("SELECT counselor_sametime FROM counselor WHERE id = #{counselorId}")
    Integer getCounselorSameTimeById(@Param("counselorId") String counselorId);

    @Select("SELECT r.reservation_time, r.reservation_description, " +
            "c.id AS counselor_id, c.job_title, c.tag, p.name AS counselor_name, p.id_picture_link AS counselor_avatar " +
            "FROM reservation r " +
            "JOIN person p ON r.counselor_id = p.id " +
            "JOIN counselor c ON r.counselor_id = c.id " +
            "WHERE r.user_id = #{userId} AND DATE(r.reservation_time) = #{date}")
    List<Map<String, Object>> getReservationsByUserAndDate(@Param("userId") String userId, @Param("date") String date);


}
