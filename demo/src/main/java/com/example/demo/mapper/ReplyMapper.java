package com.example.demo.mapper;

import com.example.demo.entity.Post;
import com.example.demo.entity.Reply;
import org.apache.ibatis.annotations.*;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ReplyMapper {
    //获取所有回复
    @Select("SELECT * FROM reply WHERE post_id=#{postId}")
    public List<Reply> find(String postId);

    //根据id查询
    @Select("SELECT * FROM reply WHERE post_id=#{postId} AND reply_id=#{replyId}")
    public List<Reply> findById(String postId, String replyId);

    //删除回复
    @Update("UPDATE reply SET delete_reason = #{deleteReason}, is_deleted = 1 WHERE post_id = #{postId} AND reply_id=#{replyId}")
    public int deleteReply(@Param("postId") String postId,
                          @Param("replyId") String replyId,
                          @Param("deleteReason") String deleteReason);

    //发布回复
    @Insert("INSERT INTO reply (reply_id,post_id,reply_time, reply_content, person_id, picture_link) " +
            "VALUES (#{replyId}, #{postId}, #{replyTime}, #{replyContent}, #{personId}, #{pictureLink})")
    public int createReply(Reply reply);

    //修改回复
    @Update("UPDATE reply SET reply_time=#{replyTime},reply_content=#{replyContent},picture_link=#{pictureLink} WHERE post_id = #{postId} AND reply_id=#{replyId} AND person_id=#{personId}")
    public int updateReply(@Param("replyId") String replyId,
                          @Param("postId") String postId,
                          @Param("personId") String personId,
                          @Param("replyTime") LocalDateTime replyTime,
                          @Param("replyContent") String replyContent,
                          @Param("pictureLink") String pictureLink);

}
