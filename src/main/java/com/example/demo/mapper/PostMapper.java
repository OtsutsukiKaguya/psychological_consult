package com.example.demo.mapper;


import com.example.demo.dto.PostSqlProvider;
import com.example.demo.dto.PostWithUserDTO;
import com.example.demo.entity.Post;
import org.apache.ibatis.annotations.*;


import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface PostMapper {
//    //获取所有帖子
//    @Select("SELECT * FROM post WHERE post_isdeleted = 0 ORDER BY post_time DESC")
//    public List<Post> find();

    //获取所有帖子
    @Select("SELECT p.post_id, p.post_title, p.post_content, p.post_time, p.picture_link, " +
            "u.id AS user_id, u.name AS user_name, u.id_picture_link AS avatar_url " +
            "FROM post p " +
            "LEFT JOIN person u ON p.person_id = u.id "+
            "ORDER BY p.post_time DESC")
    @Results({
            @Result(property = "postId", column = "post_id"),
            @Result(property = "postTitle", column = "post_title"),
            @Result(property = "postContent", column = "post_content"),
            @Result(property = "pictureLink",column = "picture_link"),
            @Result(property = "postTime", column = "post_time"),
            @Result(property = "userInfo.id", column = "user_id"),
            @Result(property = "userInfo.name", column = "user_name"),
            @Result(property = "userInfo.avatarUrl", column = "avatar_url")
    })
    List<PostWithUserDTO> findAllWithUser();


    //根据id查询
    @Select("SELECT * FROM post WHERE post_id=#{postId} AND post_isdeleted = 0 ORDER BY post_time DESC")
    public List<Post> findById(String postId);

    //根据关键词查询
    @Select("SELECT * FROM post WHERE post_isdeleted = 0 AND (post_title LIKE CONCAT('%', #{query}, '%') OR post_content LIKE CONCAT('%', #{query}, '%')) ORDER BY post_time DESC")
    public List<Post> search(String query);

    //删除帖子
    @Update("UPDATE post SET delete_reason = #{deleteReason}, post_isdeleted = 1 WHERE post_id = #{postId} AND post_isdeleted = 0")
    public int deletePost(@Param("postId") String postId,
                          @Param("adminId") String adminId,
                          @Param("deleteReason") String deleteReason);

    //发布帖子
    @Insert("INSERT INTO post (post_id, post_time, post_title, post_content, person_id, picture_link, post_isdeleted) " +
            "VALUES (#{postId}, #{postTime}, #{postTitle}, #{postContent}, #{personId}, #{pictureLink}, 0)")
    public int createPost(Post post);

    //修改帖子
    @UpdateProvider(type = PostSqlProvider.class, method = "buildUpdatePostSql")
    int updatePost(@Param("postId") String postId,
                   @Param("personId") String personId,
                   @Param("postTime") LocalDateTime postTime,
                   @Param("postTitle") String postTitle,
                   @Param("postContent") String postContent,
                   @Param("pictureLink") String pictureLink);

}
