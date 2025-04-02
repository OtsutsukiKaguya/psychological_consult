package com.example.demo.dto;
import org.apache.ibatis.jdbc.SQL;
import java.util.Map;

public class PostSqlProvider {
    public String buildUpdatePostSql(Map<String, Object> params) {
        SQL sql = new SQL();
        sql.UPDATE("post");

        if (params.get("postTitle") != null) {
            sql.SET("post_title = #{postTitle}");
        }
        if (params.get("postContent") != null) {
            sql.SET("post_content = #{postContent}");
        }
        if (params.get("pictureLink") != null) {
            sql.SET("picture_link = #{pictureLink}");
        }
        if (params.get("postTime") != null) {
            sql.SET("post_time = #{postTime}");
        }

        sql.WHERE("post_id = #{postId}");
        sql.WHERE("person_id = #{personId}");

        return sql.toString();
    }
}
