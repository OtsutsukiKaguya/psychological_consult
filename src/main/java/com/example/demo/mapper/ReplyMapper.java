package com.example.demo.mapper;

import com.example.demo.dto.PostSqlProvider;
import com.example.demo.dto.ReplySqlProvider;
import com.example.demo.dto.ReplyWithUserDTO;
import com.example.demo.entity.Post;
import com.example.demo.entity.Reply;
import org.apache.ibatis.annotations.*;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.List;


@Mapper
public interface ReplyMapper {
    //获取所有回复
//    @Select("SELECT * FROM reply WHERE post_id=#{postId} AND reply_isdeleted = 0 ORDER BY reply_time DESC")
//    public List<Reply> find(String postId);

    @Select("SELECT r.reply_id, r.post_id, r.reply_content, r.reply_time, r.picture_link, " +
            "p.id AS user_id, p.name AS user_name, p.id_picture_link AS avatar_url " +
            "FROM reply r " +
            "LEFT JOIN person p ON r.person_id = p.id " +
            "WHERE r.post_id = #{postId} AND r.reply_isdeleted = 0 " +
            "ORDER BY r.reply_time DESC")
    @Results({
            @Result(property = "replyId", column = "reply_id"),
            @Result(property = "postId", column = "post_id"),
            @Result(property = "replyContent", column = "reply_content"),
            @Result(property = "replyTime", column = "reply_time"),
            @Result(property = "pictureLink", column = "picture_link"),
            @Result(property = "userInfo.id", column = "user_id"),
            @Result(property = "userInfo.name", column = "user_name"),
            @Result(property = "userInfo.avatarUrl", column = "avatar_url")
    })
    List<ReplyWithUserDTO> findAll(@Param("postId") String postId);

    //根据id查询
    @Select("SELECT * FROM reply WHERE post_id=#{postId} AND reply_id=#{replyId} AND reply_isdeleted = 0")
    public List<Reply> findById(String postId, String replyId);

    //返回头像，根据ID查询
    @Select("SELECT r.reply_id, r.post_id, r.reply_content, r.reply_time, r.picture_link, " +
            "p.id AS user_id, p.name AS user_name, p.id_picture_link AS avatar_url " +
            "FROM reply r " +
            "LEFT JOIN person p ON r.person_id = p.id " +
            "WHERE r.post_id = #{postId} AND r.reply_id = #{replyId} AND r.reply_isdeleted = 0")
    @Results({
            @Result(property = "replyId", column = "reply_id"),
            @Result(property = "postId", column = "post_id"),
            @Result(property = "replyContent", column = "reply_content"),
            @Result(property = "replyTime", column = "reply_time"),
            @Result(property = "pictureLink", column = "picture_link"),
            @Result(property = "userInfo.id", column = "user_id"),
            @Result(property = "userInfo.name", column = "user_name"),
            @Result(property = "userInfo.avatarUrl", column = "avatar_url")
    })
    List<ReplyWithUserDTO> findDTOById(@Param("postId") String postId, @Param("replyId") String replyId);


    //删除回复
    @Update("UPDATE reply SET delete_reason = #{deleteReason}, reply_isdeleted = 1 WHERE post_id = #{postId} AND reply_id=#{replyId} AND reply_isdeleted = 0")
    public int deleteReply(@Param("postId") String postId,
                          @Param("replyId") String replyId,
                           @Param("adminId") String adminId,
                          @Param("deleteReason") String deleteReason);

    //发布回复
    @Insert("INSERT INTO reply (reply_id,post_id,reply_time, reply_content, person_id, picture_link, reply_isdeleted) " +
            "VALUES (#{replyId}, #{postId}, #{replyTime}, #{replyContent}, #{personId}, #{pictureLink},0)")
    public int createReply(Reply reply);

    //修改回复
    @UpdateProvider(type = ReplySqlProvider.class, method = "buildUpdateReplySql")
    int updateReply(@Param("replyId") String replyId,
                    @Param("postId") String postId,
                    @Param("replyTime") String replyTime,
                    @Param("replyContent") String replyContent,
                    @Param("personId") String personId,
                    @Param("pictureLink") String pictureLink);

}
