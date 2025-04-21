package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.dto.CounselorInfoDTO;
import com.example.demo.entity.Star;
import com.example.demo.mapper.StarMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class StarController {
    @Autowired
    private StarMapper starMapper;

    //新增收藏
    @PostMapping("/api/stars")
    public Result addStar(@RequestBody Star star){
        String userId = star.getUserId();
        String counselorId = star.getCounselorId();

        if (userId == null || counselorId == null) {
            return Result.error("userId 和 counselorId 不能为空");
        }

        try {
            int result = starMapper.addStar(counselorId,userId);
            if (result > 0) {
                return Result.success("收藏成功");
            } else {
                return Result.error("收藏失败");
            }
        } catch (Exception e) {
            // 比如已存在、违反唯一约束等也可以返回提示
            return Result.error("收藏失败，可能已收藏或数据库异常");
        }
    }

    //取消收藏
    @DeleteMapping("/api/stars")
    public Result cancelStar(@RequestBody Star star){
        String userId = star.getUserId();
        String counselorId = star.getCounselorId();

        if (userId == null || counselorId == null) {
            return Result.error("userId 和 counselorId 不能为空");
        }

        int deleted = starMapper.deleteStar(counselorId, userId);
        if (deleted > 0) {
            return Result.success("取消收藏成功");
        } else {
            return Result.error("取消收藏失败，可能尚未收藏");
        }
    }

    //获取收藏的咨询师列表
    @GetMapping("/api/stars/{user_id}")
    public Result getStarredCounselors(@PathVariable("user_id") String userId) {
        if (userId == null || userId.isEmpty()) {
            return Result.error("user_id 不能为空");
        }

        List<CounselorInfoDTO> counselorInfos = starMapper.getStarredCounselors(userId);

        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("counselors", counselorInfos);

        return Result.success(data);
    }


}
