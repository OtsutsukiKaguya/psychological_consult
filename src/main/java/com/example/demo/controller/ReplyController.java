package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.dto.DeleteReplyRequest;
import com.example.demo.dto.ReplyWithUserDTO;
import com.example.demo.dto.UpdateReplyRequest;
import com.example.demo.entity.Post;
import com.example.demo.entity.Reply;
import com.example.demo.mapper.PostMapper;
import com.example.demo.mapper.ReplyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ReplyController {
    @Autowired
    private ReplyMapper replyMapper;

    @Autowired
    private PostMapper postMapper;

//    @GetMapping("/api/posts/{post_id}/replys")  //获取回复列表
//    public Result findAllReply(@PathVariable("post_id")  String postId){
//        List<Reply> list = replyMapper.find(postId);
//        return Result.success(list);
//    }

    @GetMapping("/api/posts/{post_id}/replys")
    public Result findAllReply(@PathVariable("post_id") String postId) {
        List<ReplyWithUserDTO> list = replyMapper.findAll(postId);
        return Result.success(list);
    }

    @GetMapping("/api/posts/{post_id}/replys/{reply_id}")  // 根据 ID 查看回复详情
    public Result findByID(@PathVariable("post_id") String postId,
                           @PathVariable("reply_id") String replyId) {

        // TC05: post_id 或 reply_id 为空
        if (postId == null || postId.trim().isEmpty() ||
                replyId == null || replyId.trim().isEmpty()) {
            return Result.error(-4, "post_id 和 reply_id 不能为空");
        }

        // TC04: post_id 或 reply_id 非法字符（只允许字母和数字）
        if (!postId.matches("^[a-zA-Z0-9]+$") || !replyId.matches("^[a-zA-Z0-9]+$")) {
            return Result.error(-3, "post_id 或 reply_id 参数非法");
        }

        // TC03: 帖子不存在
        List<Post> postList = postMapper.findById(postId);
        if (postList == null || postList.isEmpty()) {
            return Result.error(-2, "帖子不存在");
        }

        // TC02: 帖子存在但回复不存在
        List<Reply> replyList = replyMapper.findById(postId, replyId);
        if (replyList == null || replyList.isEmpty()) {
            return Result.error(-1, "回复不存在");
        }

        // TC01: 查询成功
        return Result.success(replyList);
    }

    @DeleteMapping("/api/posts/{post_id}/replys/{reply_id}")   // 删除回复
    public Result deleteReply(@PathVariable("post_id") String postId,
                              @PathVariable("reply_id") String replyId,
                              @RequestBody DeleteReplyRequest request) {

        String adminId = request.getAdminId();
        String deleteReason = request.getDeleteReason();

        // TC07: admin_id 为空
        if (adminId == null || adminId.trim().isEmpty()) {
            return Result.error(-6, "admin_id 不能为空");
        }

        // TC03: reason 缺失
        if (deleteReason == null || deleteReason.trim().isEmpty()) {
            return Result.error(-2, "reason 为必填参数");
        }

        // TC06: post_id 或 reply_id 非法（特殊字符）
        if (!postId.matches("^[a-zA-Z0-9_-]+$") || !replyId.matches("^[a-zA-Z0-9_-]+$")) {
            return Result.error(-5, "post_id 或 reply_id 参数非法");
        }

        // TC05: 帖子不存在
        List<Post> post = postMapper.findById(postId);
        if (post == null || post.isEmpty()) {
            return Result.error(-4, "帖子不存在");
        }

        // TC04: 回复不存在
        List<Reply> reply = replyMapper.findById(postId, replyId);
        if (reply == null || reply.isEmpty()) {
            return Result.error(-3, "回复不存在");
        }

        // TC01: 删除操作
        int result = replyMapper.deleteReply(postId, replyId, adminId, deleteReason);
        if (result > 0) {
            Map<String, Object> data = new HashMap<>();
            data.put("deletedReplyId", replyId);
            data.put("postId", postId);
            data.put("message", "删除成功");
            return Result.success(data);
        } else {
            return Result.error("删除失败");
        }
    }

    @PostMapping("/api/posts/{post_id}/replys")  // 发布回复
    public Result createReply(@PathVariable("post_id") String postId,
                              @RequestBody Reply reply) {

        // TC08: post_id 非法
        if (postId == null || !postId.matches("^[a-zA-Z0-9_-]+$")) {
            return Result.error(-5, "post_id 参数非法");
        }

        // TC07: post_id 不存在
        List<Post> posts = postMapper.findById(postId);
        if (posts == null || posts.isEmpty()) {
            return Result.error(-4, "帖子不存在");
        }

        // TC04: 回复内容为空（文字 + 图片都为空）
        boolean contentEmpty = (reply.getReplyContent() == null || reply.getReplyContent().trim().isEmpty());
        boolean imageEmpty = (reply.getPictureLink() == null || reply.getPictureLink().trim().isEmpty());
        if (contentEmpty && imageEmpty) {
            return Result.error(-1, "回复内容不能为空");
        }

        // TC05: 缺少 person_id
        if (reply.getPersonId() == null || reply.getPersonId().trim().isEmpty()) {
            return Result.error(-2, "person_id 为必填参数");
        }

        // TC06: reply_time 缺失或格式非法
        if (reply.getReplyTime() == null) {
            return Result.error(-3, "reply_time 不能为空");
        }

        // 自动生成 replyId 并绑定 postId
        String generatedId = "r" + System.currentTimeMillis();
        reply.setReplyId(generatedId);
        reply.setPostId(postId);

        // 插入数据库
        int i = replyMapper.createReply(reply);
        if (i > 0) {
            List<Reply> savedReply = replyMapper.findById(postId, generatedId);
            return Result.success(savedReply);
        } else {
            return Result.error("发布失败");
        }
    }

    @PutMapping("/api/posts/{post_id}/replys/{reply_id}")  // 修改回复
    public Result updateReply(@PathVariable("post_id") String postId,
                              @PathVariable("reply_id") String replyId,
                              @RequestBody UpdateReplyRequest request) {

        // TC04: 文本和图片都为空
        boolean hasUpdate = request.getReplyContent() != null && !request.getReplyContent().trim().isEmpty()
                || request.getPictureLink() != null && !request.getPictureLink().trim().isEmpty();
        if (!hasUpdate) {
            return Result.error(-1, "修改内容不能为空");
        }

        // TC09: 缺少 reply_time
        if (request.getReplyTime() == null) {
            return Result.error(-6, "reply_time 不能为空");
        }

        // TC07: 检查 post_id 和 reply_id 是否非法（仅允许字母、数字、-、_）
        if (!postId.matches("^[a-zA-Z0-9_-]+$") || !replyId.matches("^[a-zA-Z0-9_-]+$")) {
            return Result.error(-7, "post_id 或 reply_id 参数不合法");
        }

        // TC05 + TC06: 检查是否存在对应的帖子和回复
        List<Reply> existingReply = replyMapper.findById(postId, replyId);
        if (existingReply == null || existingReply.isEmpty()) {
            // TC05: 帖子不存在（需要你检查 postMapper 逻辑中是否存在 is_deleted=0）
            if (postMapper.findById(postId).isEmpty()) {
                return Result.error(-2, "帖子不存在");
            }
            // TC06: 回复不存在
            return Result.error(-3, "回复不存在");
        }

        // TC08: 修改他人回复（判断 person_id 不一致）
        if (!existingReply.get(0).getPersonId().equals(request.getPersonId())) {
            return Result.error(-5, "您无权修改本回复");
        }

        int i = replyMapper.updateReply(
                replyId,
                postId,
                request.getReplyTime(),
                request.getReplyContent(),
                request.getPersonId(),
                request.getPictureLink()
        );

        if (i > 0) {
            List<Reply> updatedReply = replyMapper.findById(postId, replyId);
            return Result.success(updatedReply);
        } else {
            return Result.error("修改失败");
        }
    }

}
