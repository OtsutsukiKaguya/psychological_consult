package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.dto.DeletePostRequest;
import com.example.demo.dto.PostWithUserDTO;
import com.example.demo.dto.UpdatePostRequest;
import com.example.demo.entity.Post;
import com.example.demo.mapper.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class PostController {
    @Autowired
    private PostMapper postMapper;

//    @GetMapping("/api/posts") // 获取所有帖子列表
//    public Result findAll() {
//        List<Post> list = postMapper.find();
//        return Result.success(list);
//    }

//    @GetMapping("/api/posts") // 获取所有帖子列表
//    public Result findAll() {
//        List<PostWithUserDTO> list = postMapper.findAllWithUser();
//        return Result.success(list);
//    }

    @GetMapping("/api/posts") // 获取所有帖子列表
    public Result findAll() {
        try {
            List<PostWithUserDTO> list = postMapper.findAllWithUser();

            // 数据为空的情况（但不是异常）
            if (list == null || list.isEmpty()) {
                return Result.error(-1, "暂无帖子");
            }

            return Result.success(list);
        } catch (Exception e) {
            // 捕获运行时异常，避免接口报500
            e.printStackTrace(); // 或使用日志框架 log.error("查询帖子失败", e);
            return Result.error(-99, "系统异常，获取帖子失败");
        }
    }

//    @GetMapping("/api/posts/{post_id}") //根据ID查询/获取帖子详情
//    public Result findByID(@PathVariable("post_id") String postId) {
//
//        if (postId == null || postId.trim().isEmpty()) {
//            return Result.error(-3, "post_id 不能为空");
//        }
//
//        if (!postId.matches("^[a-zA-Z0-9_-]+$")) {
//            return Result.error(-2, "post_id 参数非法");
//        }
//
//        List<Post> list = postMapper.findById(postId);
//
//        if (list == null || list.isEmpty()) {
//            return Result.error(-1, "帖子不存在");
//        }
//
//        return new Result(0, "ok", list);
//    }

    @GetMapping("/api/posts/{post_id}")  //根据ID查询/获取帖子详情
    public Result findByID(@PathVariable("post_id") String postId) {
        if (postId == null || postId.trim().isEmpty()) {
            return Result.error(-3, "post_id 不能为空");
        }
        if (!postId.matches("^[a-zA-Z0-9_-]+$")) {
            return Result.error(-2, "post_id 参数非法");
        }

        List<PostWithUserDTO> list = postMapper.findDTOById(postId);
        if (list == null || list.isEmpty()) {
            return Result.error(-1, "帖子不存在");
        }
        return Result.success(list.get(0));
    }

//    @GetMapping("/api/posts/search")  // 搜索帖子
//    public Result search(@RequestParam String query) {
//
//        // TC03: query 为空
//        if (query == null || query.trim().isEmpty()) {
//            return Result.error(-1, "query 参数不能为空");
//        }
//
//        // TC05: query 长度超出限制
//        if (query.length() > 1000) {
//            return Result.error(-2, "query 参数长度超出限制");
//        }
//
//        // TC04: query 含非法字符（只允许中英文、数字、空格）
//        if (!query.matches("^[\\u4e00-\\u9fa5a-zA-Z0-9\\s]+$")) {
//            return Result.error(-3, "query 参数包含非法字符");
//        }
//
//        // TC01 & TC02：执行搜索
//        List<Post> list = postMapper.search(query);
//        return new Result(0, "ok", list);
//    }

    @GetMapping("/api/posts/search") //搜索帖子
    public Result search(@RequestParam String query) {
        if (query == null || query.trim().isEmpty()) {
            return Result.error(-1, "query 参数不能为空");
        }
        if (query.length() > 1000) {
            return Result.error(-2, "query 参数长度超出限制");
        }
        if (!query.matches("^[\\u4e00-\\u9fa5a-zA-Z0-9\\s]+$")) {
            return Result.error(-3, "query 参数包含非法字符");
        }

        List<PostWithUserDTO> list = postMapper.search(query);
        return Result.success(list);
    }

    @DeleteMapping("/api/posts/{post_id}") // 删除帖子
    public Result deletePost(@PathVariable("post_id") String postId,
                             @RequestBody DeletePostRequest request) {

        String adminId = request.getAdminId();
        String deleteReason = request.getDeleteReason();

        // TC02: admin_id 为 null
        if (adminId == null) {
            return Result.error(-1, "admin_id 为必填参数");
        }

        // TC06: admin_id 为空字符串
        if (adminId.trim().isEmpty()) {
            return Result.error(-5, "admin_id 不能为空");
        }

        // TC03: reason 为空
        if (deleteReason == null || deleteReason.trim().isEmpty()) {
            return Result.error(-2, "reason 为必填参数");
        }

        // TC05: post_id 非法（只允许字母、数字、下划线、中划线）
        if (!postId.matches("^[a-zA-Z0-9_-]+$")) {
            return Result.error(-4, "post_id 参数非法");
        }

        // TC04: 帖子不存在（你可以先查找再判断）
        List<Post> postList = postMapper.findById(postId); // 确保 SQL 中已加 post_isdeleted = 0
        if (postList == null || postList.isEmpty()) {
            return Result.error(-3, "帖子不存在");
        }

        // TC01: 正常删除（软删除）
        int result = postMapper.deletePost(postId, adminId, deleteReason);
        if (result > 0) {
            Map<String, Object> data = new HashMap<>();
            data.put("message", "帖子已删除");
            data.put("post_id", postId);
            return new Result(0, "ok", data);
        } else {
            return Result.error("删除失败");
        }
    }

    @PostMapping("/api/posts") // 发布帖子
    public Result createPost(@RequestBody Post post) {
        // TC02: post_title 为空
        if (post.getPostTitle() == null || post.getPostTitle().trim().isEmpty()) {
            return Result.error(-1, "post_title 为必填字段");
        }

        // TC05: post_title 超长
        if (post.getPostTitle().length() > 1000) {
            return Result.error(-4, "post_title 超出长度限制");
        }

        // TC03: person_id 为空
        if (post.getPersonId() == null || post.getPersonId().trim().isEmpty()) {
            return Result.error(-2, "person_id 为必填字段");
        }

        // TC07: post_time 为空
        if (post.getPostTime() == null) {
            return Result.error(-6, "post_time 为必填字段");
        }

        // TC04: post_content 为空字符串
        boolean contentEmpty = post.getPostContent() != null && post.getPostContent().trim().isEmpty();

        // TC06: post_picture_link 为空字符串
        boolean pictureEmpty = post.getPictureLink() != null && post.getPictureLink().trim().isEmpty();
        if (pictureEmpty) {
            return Result.error(-5, "post_picture_link 不能为空");
        }

        // TC08: post_content 和 post_picture_link 同时为空
        if ((post.getPostContent() == null || post.getPostContent().trim().isEmpty())
                && (post.getPictureLink() == null || post.getPictureLink().trim().isEmpty())) {
            return Result.error(-7, "内容和图片不能同时为空");
        }

        // TC01: 正常发布
        String generatedId = "p" + System.currentTimeMillis();
        post.setPostId(generatedId);

        int i = postMapper.createPost(post);
        if (i > 0) {
            List<Post> savedPost = postMapper.findById(generatedId);
            return Result.success(savedPost);
        } else {
            return Result.error("发布失败");
        }
    }

    @PutMapping("/api/posts/{post_id}")  // 修改帖子
    public Result updatePost(@PathVariable("post_id") String postId,
                             @RequestBody UpdatePostRequest request) {

        // TC07: post_id 非法字符
        if (!postId.matches("^[a-zA-Z0-9_-]+$")) {
            return Result.error(-3, "post_id 参数非法");
        }

        // TC05: 缺少 post_time 字段
        if (request.getPostTime() == null) {
            return Result.error(-1, "post_time 为必填字段");
        }

        // TC08: post_title 长度超过限制（例如 1000 字）
        if (request.getPostTitle() != null && request.getPostTitle().length() > 1000) {
            return Result.error(-4, "post_title 超出长度限制");
        }

        // TC09/TC10: person_id 为必填字段且需合法
        if (request.getPersonId() == null || request.getPersonId().trim().isEmpty()) {
            return Result.error(-6, "person_id 为必填字段");
        }

        // TC06: post_id 不存在（查询是否能查到）
        List<Post> exist = postMapper.findById(postId);
        if (exist == null || exist.isEmpty()) {
            return Result.error(-2, "帖子不存在");
        }

        // TC01-TC04：判断是否有字段更新
        boolean hasUpdate = request.getPostTitle() != null ||
                request.getPostContent() != null ||
                request.getPictureLink() != null;

        if (!hasUpdate) {
            return Result.error(-5, "必须修改至少一个字段");
        }

        // 正常执行修改
        int i = postMapper.updatePost(
                postId,
                request.getPersonId(),
                request.getPostTime(),
                request.getPostTitle(),
                request.getPostContent(),
                request.getPictureLink());

        if (i > 0) {
            // 修改成功后，从数据库中查回最新帖子内容
            List<Post> updatedPost = postMapper.findById(postId);
            return Result.success(updatedPost);
        } else {
            return Result.error("修改失败");
        }
    }

}
