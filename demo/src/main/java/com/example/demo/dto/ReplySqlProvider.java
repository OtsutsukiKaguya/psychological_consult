package com.example.demo.dto;

import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

public class ReplySqlProvider {
    public String buildUpdateReplySql(Map<String, Object> params) {
        SQL sql = new SQL();
        sql.UPDATE("reply");

        if (params.get("replyTime") != null) {
            sql.SET("reply_time = #{replyTime}");
        }
        if (params.get("replyContent") != null) {
            sql.SET("reply_content = #{replyContent}");
        }
        if (params.get("pictureLink") != null) {
            sql.SET("picture_link = #{pictureLink}");
        }

        sql.WHERE("reply_id = #{replyId}");
        sql.WHERE("post_id = #{postId}");
        sql.WHERE("person_id = #{personId}");

        return sql.toString();
    }
}
