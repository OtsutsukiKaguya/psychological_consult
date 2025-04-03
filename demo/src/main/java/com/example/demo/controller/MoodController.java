package com.example.demo.controller;

import com.example.demo.Common.Result;
import com.example.demo.dto.MoodDeleteRequest;
import com.example.demo.dto.MoodQueryRequest;
import com.example.demo.entity.Mood;
import com.example.demo.mapper.MoodMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class MoodController {
    @Autowired
    private MoodMapper moodMapper;

    @PostMapping("/api/moods")
    public Result recordMood(@RequestBody Mood mood){

        int i = moodMapper.recordMood(mood);
        if (i > 0) {
            Map<String, Object> data = new HashMap<>();
            data.put("message", "心情记录成功");
            data.put("status", "recorded");
            return Result.success(data);
        } else {
            return Result.error("心情记录失败");
        }

    }

    @PostMapping("/api/moods/query")
    public Result queryMoodRecords(@RequestBody MoodQueryRequest request) {
        if (request.getUserId() == null || request.getUserId().isEmpty()) {
            throw new IllegalArgumentException("user_id 不能为空");
        }

        List<Mood> records = moodMapper.queryMoodRecords(
                request.getUserId(),
                request.getStartDate(),
                request.getEndDate(),
                request.getMoodType()
        );

        return Result.success(records);
    }

    @DeleteMapping("/api/moods")
    public Result deleteMoodRecord(@RequestBody MoodDeleteRequest request) {
        if (request.getUserId() == null || request.getMoodDate() == null) {
            throw new IllegalArgumentException("userId 和 date 不能为空");
        }

        int deleted = moodMapper.deleteByUserIdAndDate(request.getUserId(), request.getMoodDate());
        if (deleted > 0) {
            return Result.success("删除成功");
        } else {
            return Result.error("未找到符合条件的心情记录");
        }
    }

}
