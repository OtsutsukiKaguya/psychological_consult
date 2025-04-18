package com.example.demo.mapper;

import com.example.demo.entity.*;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface  InformationMapper {

    @Select("SELECT * FROM person WHERE id=#{id}")
    public List<Person> getPersonById(String id);

    @Select("SELECT * FROM user WHERE id=#{id}")
    public List<User> getUserById(String id);

    @Select("SELECT * FROM counselor WHERE id=#{id}")
    public List<Counselor> getCounselorById(String id);

    @Select("SELECT * FROM tutor WHERE id=#{id}")
    public List<Tutor> getTutorById(String id);

    @Update("UPDATE user SET user_isbanned=1 WHERE id=#{id}")
    public int bannedUser(String id);

    @Update("UPDATE person SET role = #{role}, name = #{name}, email = #{email}, gender = #{gender}, age = #{age}, idcard = #{idcard}, phone = #{phone}, self_description = #{selfDescription} WHERE id = #{id}")
    public int updatePerson(String id, String role, String name, String email, String gender, int age, String idcard, String phone, String selfDescription);

    @Insert("INSERT INTO person(id,role,name,email,gender,idcard,age,phone,password,state,self_description,id_picture_link) VALUES (#{id},#{role},#{name},#{email},#{gender},#{idcard},#{age},#{phone},#{password},#{state},#{selfDescription},#{idPictureLink})")
    public int insertPerson(Person person);

    @Insert("INSERT INTO user(id,urgent_name,urgent_phone,user_isbanned) VALUES (#{id},#{urgentName},#{urgentPhone},#{userIsBanned})")
    public int insertUser(User user);

    @Insert("INSERT INTO counselor(id,duty_arrangement,institute,job_title,counselor_sametime,tag) VALUES (#{id},#{dutyArrangement},#{institute},#{jobTitle},#{counselorSameTime},#{tag})")
    public int insertCounselor(Counselor counselor);

    @Insert("INSERT INTO tutor(id,duty_arrangement,institute,job_title,counselor_sametime,tag,is_qualified,qualified_number) VALUES (#{id},#{dutyArrangement},#{institute},#{jobTitle},#{counselorSameTime},#{tag},#{isQualified},#{qualifiedNumber})")
    public int insertTutor(Tutor tutor);

    @Update("UPDATE person SET password = #{password} WHERE id = #{id}")
    public int changePassword(String id, String password);

    @Select("SELECT last_login_time FROM person WHERE id=#{id}")
    public List<Person> getLastLoginTime(String id);

    @Update("UPDATE person SET id_picture_link = #{idPictureLink} WHERE id = #{id}")
    public int changeAvatar(String idPictureLink, String id);

    @Select("SELECT * FROM person where id=#{id} AND password=#{password}")
    public List<Person> Login(String id, String password);

}
