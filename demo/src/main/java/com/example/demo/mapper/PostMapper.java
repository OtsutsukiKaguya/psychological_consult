package com.example.demo.mapper;


import com.example.demo.entity.Post;
import org.apache.ibatis.annotations.*;


import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface PostMapper {
    //获取所有帖子
    @Select("SELECT * FROM post")
    public List<Post> find();

    //根据id查询
    @Select("SELECT * FROM post WHERE post_id={postId}")
    public List<Post> findById(String postId);

    //根据关键词查询
    @Select("SELECT * FROM post WHERE post_title LIKE CONCAT('%', #{query}, '%') OR post_content LIKE CONCAT('%', #{query}, '%')")
    public List<Post> search(String query);

    //删除帖子
    @Update("UPDATE post SET admin_id = #{adminId}, delete_reason = #{deleteReason}, is_deleted = 1 WHERE post_id = #{postId}")
    public int deletePost(@Param("postId") String postId,
                          @Param("deleteReason") String deleteReason);

    //发布帖子
    @Insert("INSERT into post values (#{postId},#{postTime},#{postTitle},#{postContent},#{personId},#{pictureLink},#{postIsDeleted})")
    public int createPost(Post post);

    //修改帖子
    @Update("UPDATE post SET post_time=#{postTime},post_title=#{postTitle},post_content=#{postContent},picture_link=#{pictureLink} WHERE post_id = #{postId} AND person_id=#{personId}")
    public int updatePost(@Param("postId") String postId,
                          @Param("personId") String personId,
                      @Param("postTime") LocalDateTime postTime,
                      @Param("postTitle") String postTitle,
                      @Param("postContent") String postContent,
                      @Param("pictureLink") String pictureLink);

}
