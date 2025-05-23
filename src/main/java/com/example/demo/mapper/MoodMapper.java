package com.example.demo.mapper;

import com.example.demo.entity.Mood;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface MoodMapper {
    //用户记录心情
    @Insert("INSERT INTO mood(user_id, mood_date, mood_type, mood_content) " +
            "VALUES (#{userId}, #{moodDate}, #{moodType}, #{moodContent})")
    public int recordMood(Mood mood);

    //根据日期及心情类型查询记录
    @Select({
            "<script>",
            "SELECT * FROM mood",
            "WHERE user_id = #{userId}",
            "<if test='startDate != null and startDate != \"\"'>",
            "AND mood_date &gt;= #{startDate}",
            "</if>",
            "<if test='endDate != null and endDate != \"\"'>",
            "AND mood_date &lt;= #{endDate}",
            "</if>",
            "<if test='moodType != null and moodType != \"\"'>",
            "AND mood_type = #{moodType}",
            "</if>",
            "ORDER BY mood_date DESC",
            "</script>"
    })
    List<Mood> queryMoodRecords(@Param("userId") String userId,
                                @Param("startDate") String startDate,
                                @Param("endDate") String endDate,
                                @Param("moodType") String moodType);

    //根据用户ID及日期删除心情记录
    @Delete("DELETE FROM mood WHERE user_id = #{userId} AND mood_date = #{moodDate}")
    int deleteByUserIdAndDate(@Param("userId") String userId, @Param("moodDate") String moodDate);

    // 新增：根据 user_id + date 查询是否已有记录
    @Select("SELECT * FROM mood WHERE user_id = #{userId} AND mood_date = #{moodDate}")
    Mood selectByUserIdAndDate(@Param("userId") String userId, @Param("moodDate") LocalDate moodDate);

    // 新增：更新内容和标签
    @Update("UPDATE mood SET mood_type = #{moodType}, mood_content = #{moodContent} " +
            "WHERE user_id = #{userId} AND mood_date = #{moodDate}")
    int updateMoodContentAndType(@Param("userId") String userId,
                                 @Param("moodDate") LocalDate moodDate,
                                 @Param("moodType") String moodType,
                                 @Param("moodContent") String moodContent);

}