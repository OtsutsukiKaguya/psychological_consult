package com.example.demo.controller;

import com.example.demo.dto.DeletePostRequest;
import com.example.demo.dto.UpdatePostRequest;
import com.example.demo.entity.Post;
import com.example.demo.mapper.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PostController {
    @Autowired
    private PostMapper postMapper;

    @GetMapping("/api/posts") //获取所有帖子列表
    public List findAll(){
        List<Post> list = postMapper.find();
        System.out.println(list);
        return list;
    }

    @GetMapping("/api/posts/{post_id}")  //获取单条帖子详情
    public List findByID(@PathVariable("post_id") String postId){
        List<Post> list = postMapper.findById(postId);
        System.out.println(list);
        return list;
    }

    @GetMapping("/api/posts/search")  //搜索帖子
    public List search(@RequestParam String query){
        System.out.println("query = " + query);
        List<Post> list = postMapper.search(query);
        System.out.println(list);
        return list;
    }

    @DeleteMapping("/api/posts/{post_id}") //删除帖子
    public String deletePost(@PathVariable("post_id") String postId,
                             @RequestBody DeletePostRequest request) {
        String adminId = request.getAdminId();
        String deleteReason = request.getDeleteReason();
        int result = postMapper.deletePost(postId, adminId, deleteReason);

        return result > 0 ? "删除成功" : "删除失败";
    }

    @PostMapping("/api/posts")  //发布帖子
    public String createPost(@RequestBody Post post){
        //自动生成postId
        String generatedId = "p" + System.currentTimeMillis();
        post.setPostId(generatedId);

        int i = postMapper.createPost(post);
        return i > 0 ? "发布成功" : "发布失败";
    }

    @PutMapping("/api/posts/{post_id}")  //修改帖子
    public String updatePost (@PathVariable("post_id") String postId, @RequestBody UpdatePostRequest request){
        //判断是否进行了修改
        boolean hasUpdate = request.getPostTitle() != null ||
                request.getPostContent() != null ||
                request.getPictureLink() != null;

        if (!hasUpdate) {
            return "必须修改至少一个字段";
        }

        int i = postMapper.updatePost(postId,
                request.getPersonId(),
                request.getPostTime(),
                request.getPostTitle(),
                request.getPostContent(),
                request.getPictureLink());
        return i > 0 ? "修改成功" : "修改失败";
    }
}
