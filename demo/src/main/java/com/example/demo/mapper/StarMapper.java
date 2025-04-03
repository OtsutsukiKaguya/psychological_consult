package com.example.demo.mapper;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StarMapper {
    //收藏咨询师
    @Insert("INSERT INTO star (counselor_id,user_id)"+
            "VALUES(#{counselorId},#{userId}) ")
    public int addStar(@Param("counselorId") String counselorId,
                       @Param("userId") String userId);

    //取消收藏咨询师
    @Delete("DELETE FROM star WHERE user_id = #{userId} AND counselor_id = #{counselorId}")
    int deleteStar(@Param("counselorId") String counselorId, @Param("userId") String userId);

    //获取已收藏的咨询师列表
    @Select("SELECT counselor_id FROM star WHERE user_id = #{userId}")
    List<String> getStarredCounselors(@Param("userId") String userId);
}
