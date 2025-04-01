package com.example.demo.controller;

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
    public List<Reply> findAllReply(){
        List<Reply> list = replyMapper.find();
        System.out.println(list);
        return list;
    }

    @GetMapping("/api/posts/{post_id}/replys/{reply_id}")
    public List<Reply> findByID(@PathVariable String postId, @PathVariable String replyId){
        List<Reply> list = replyMapper.findById(postId,replyId);
        System.out.println(list);
        return list;
    }

    @PutMapping("/api/posts/{post_id}/replys/{reply_id}")
    public String deleteReply(@PathVariable("post_id") String postId,
                              @PathVariable("reply_id") String replyId,
                             @RequestBody String deleteReason)
    {
        int result = replyMapper.deleteReply(postId, replyId, deleteReason);
        return result > 0 ? "删除成功" : "删除失败";
    }

    @PostMapping("/api/posts/{post_id}/replys")
    public String createReply(@RequestBody Reply reply){
        int i = replyMapper.createReply(reply);
        return i > 0 ? "发布成功" : "发布失败";
    }

    @PutMapping("/api/posts/{post_id}/replys/{reply_id}")
    public String updateReply (@PathVariable("post_id") String postId,@PathVariable("reply_id") String replyId, @RequestBody UpdateReplyRequest request){
        int i = replyMapper.updateReply(postId, replyId, request.getPersonId(),request.getReplyTime(),request.getReplyContent(),request.getPictureLink());
        return i > 0 ? "修改成功" : "修改失败";
    }
}
