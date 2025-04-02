package com.example.demo.controller;

import com.example.demo.dto.DeleteReplyRequest;
import com.example.demo.dto.UpdateReplyRequest;
import com.example.demo.entity.Reply;
import com.example.demo.mapper.ReplyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ReplyController {
    @Autowired
    private ReplyMapper replyMapper;

    @GetMapping("/api/posts/{post_id}/replys")
    public List<Reply> findAllReply(@PathVariable("post_id")  String postId){
        List<Reply> list = replyMapper.find(postId);
        System.out.println(list);
        return list;
    }

    @GetMapping("/api/posts/{post_id}/replys/{reply_id}")
    public List<Reply> findByID(@PathVariable("post_id") String postId, @PathVariable("reply_id") String replyId){
        List<Reply> list = replyMapper.findById(postId,replyId);
        System.out.println(list);
        return list;
    }

    @DeleteMapping("/api/posts/{post_id}/replys/{reply_id}")
    public String deleteReply(@PathVariable("post_id") String postId,
                              @PathVariable("reply_id") String replyId,
                              @RequestBody DeleteReplyRequest request)
    {
        String adminId = request.getAdminId();
        String deleteReason = request.getDeleteReason();
        int result = replyMapper.deleteReply(postId, replyId, adminId, deleteReason);
        return result > 0 ? "删除成功" : "删除失败";
    }

    @PostMapping("/api/posts/{post_id}/replys")
    public String createReply(@PathVariable("post_id") String postId,@RequestBody Reply reply){
        //自动生成replyId
        String generatedId = "r" + System.currentTimeMillis();
        reply.setReplyId(generatedId);

        // 把 URL 中的 postId 设置进对象
        reply.setPostId(postId);

        int i = replyMapper.createReply(reply);
        return i > 0 ? "发布成功" : "发布失败";
    }

    @PutMapping("/api/posts/{post_id}/replys/{reply_id}")
    public String updateReply (@PathVariable("post_id") String postId,
                               @PathVariable("reply_id") String replyId,
                               @RequestBody UpdateReplyRequest request){
        //判断是否修改
        boolean hasUpdate = request.getReplyContent() != null ||
                request.getPictureLink() != null;

        if (!hasUpdate) {
            return "必须修改至少一个字段";
        }
//        int i = replyMapper.updateReply(replyId, postId, request.getPersonId(),request.getReplyTime(),request.getReplyContent(),request.getPictureLink());
        int i = replyMapper.updateReply(
                replyId,
                postId,
                request.getReplyTime(),
                request.getReplyContent(),
                request.getPersonId(),
                request.getPictureLink()
        );
        return i > 0 ? "修改成功" : "修改失败";
    }
}
