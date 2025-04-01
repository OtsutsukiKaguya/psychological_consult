package com.example.demo.mapper;

import com.example.demo.entity.Counselor;
import com.example.demo.entity.Person;
import com.example.demo.entity.Tutor;
import com.example.demo.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface InformationMapper {

    @Select("SELECT * FROM person WHERE id=#{id}")
    public List<Person> getPersonById(String id);

    @Select("SELECT * FROM user WHERE id=#{id}")
    public List<User> getUserById(String id);

    @Select("SELECT * FROM counselor WHERE id=#{id}")
    public List<Counselor> getCounselorById(String id);

    @Select("SELECT * FROM tutor WHERE id=#{id}")
    public List<Tutor> getTutorById(String id);

    @Delete("DELETE FROM person WHERE id=#{id}")
    public int deletePersonById(String id);

    @Delete("DELETE FROM user WHERE id=#{id}")
    public int deleteUserById(String id);

    @Delete("DELETE FROM counselor WHERE id=#{id}")
    public int deleteCounselorById(String id);

    @Delete("DELETE FROM tutor WHERE id=#{id}")
    public int deleteTutorById(String id);

    @Insert("INSERT INTO person(id,role,name,email,sex,idcard,phone,password,state,self_description) VALUES (#{id},#{role},#{name},#{email},#{sex},#{idcard},#{phone},#{password},#{state},#{selfDescription})")
    public int insertPerson(Person person);

    @Insert("INSERT INTO user(id,urgent_name,urgent_phone,user_isbaned) VALUES (#{id},#{urgentName},#{urgentPhone},#{userIsbaned})")
    public int insertUser(User user);

    @Insert("INSERT INTO counselor(id,duty_arrangement,workplace,job_title,user_sametime,tag) VALUES (#{id},#{dutyArrangement},#{workplace},#{jobTitle},#{userSametime},#{tag})")
    public int insertCounselor(Counselor counselor);

    @Insert("INSERT INTO tutor(id,duty_arrangement,workplace,job_title,counselor_sametime,tag,is_qualified,qualified_no) VALUES (#{id},#{dutyArrangement},#{workplace},#{jobTitle},#{counselorSametime},#{tag},#{isQualified},#{qualifiedNo})")
    public int insertTutor(Tutor tutor);

    @Update("UPDATE person SET password = #{password} WHERE id = #{id}")
    public int changePassword(String id, String password);

    @Select("SELECT last_login_time FROM person WHERE id=#{id}")
    public List<Person> getLastLoginTime(String id);

    @Update("UPDATE person SET id_picture_link = #{idPictureLink} WHERE id = #{id}")
    public int changeAvatar(String idPictureLink, String id);
}
