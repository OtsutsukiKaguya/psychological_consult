package com.example.demo.controller;

import com.example.demo.mapper.ConversationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import com.example.demo.entity.Conversation;

import java.util.List;

@RestController
public class ConversationController {

    @Autowired
    private ConversationMapper conversationMapper;

    @GetMapping("/api/conversation/initialConversation")

    public List query(){

        List<Conversation> list = conversationMapper.find();
        System.out.println(list);
        return list;
    }

    @GetMapping("/api/conversation/searchBySingleID/{id}")

    public List query1(@PathVariable("id") String id){

        List<Conversation> list = conversationMapper.findBySingleId(id);
        System.out.println(list);
        return list;
    }

    @GetMapping("/api/conversation/searchByDate/{date}")

    public List query2(@PathVariable("date") String date){

        List<Conversation> list = conversationMapper.findByDate(date);
        System.out.println(list);
        return list;
    }

    @GetMapping("/api/conversation/searchByIDAndDate/{id}/{date}")

    public List query3(@PathVariable("id") String id, @PathVariable("date") String date){

        List<Conversation> list = conversationMapper.findByIdAndDate(id, date);
        System.out.println(list);
        return list;
    }

    @GetMapping("/api/conversation/searchByDoubleID/{userid}/{counselorid}")

    public List query4(@PathVariable("userid") String userid, @PathVariable("counselorid") String counselorid){

        List<Conversation> list = conversationMapper.findByIdAndDate(userid, counselorid);
        System.out.println(list);
        return list;
    }


}
