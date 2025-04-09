package com.example.demo.controller;

import com.example.demo.dto.BackstageArrangement1DTO;
import com.example.demo.dto.BackstageArrangementDTO;
import com.example.demo.entity.Ask_leave;
import com.example.demo.entity.Bind;
import com.example.demo.entity.Duty_calendar;
import com.example.demo.mapper.BackstageArrangementMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BackstageArrangementController {

    @Autowired
    private BackstageArrangementMapper backstageArrangementMapper;

    @GetMapping("/api/duty/getdutybyid/{id}")

    public List searchWorkDays(@PathVariable("id") String id) {

        List<Duty_calendar> list = backstageArrangementMapper.searchWorkDays(id);
        System.out.println(list);
        return list;
    }

    @GetMapping("/api/duty/getdutybytodaycounselor/{role}/{dutyDate}")

    public List searchCounselorWorkDays(@PathVariable("role") String role, @PathVariable("dutyDate") String dutyDate) {

        List<BackstageArrangementDTO> list = backstageArrangementMapper.searchCounselorWorkDays(role, dutyDate);
        System.out.println(list);
        return list;
    }

    @GetMapping("/api/duty/getdutybydate/{dutyDate}")

    public List getdutybydate(@PathVariable("dutyDate") String dutyDate) {

        List<BackstageArrangement1DTO> list = backstageArrangementMapper.getDutyByDate(dutyDate);
        System.out.println(list);
        return list;
    }

    @PostMapping("/api/duty/addduty")
    public String insertDuty(@RequestBody Duty_calendar duty_calendar) {
        System.out.println("Received duty data: " + duty_calendar);
        int i = BackstageArrangementMapper.insertDuty(duty_calendar);
        if (i > 0) {
            return "success";
        } else {
            return "fail";
        }
    }


    @PostMapping("/api/duty/deleteduty")
    public String deleteDuty(@RequestParam("id") String id, @RequestParam("dutyDate") String dutyDate) {

        int i = BackstageArrangementMapper.deleteDuty(id, dutyDate);
        if (i > 0) {
            return "success";
        } else {
            return "fail";
        }
    }

    @PostMapping("/api/leave/addleave")
    public String insertLeave(@RequestBody Ask_leave ask_leave) {
        System.out.println("Received leave data: " + ask_leave);
        int i = BackstageArrangementMapper.insertLeave(ask_leave);
        if (i > 0) {
            return "success";
        } else {
            return "fail";
        }
    }

    @GetMapping("/api/leave/showleave")

    public List searchLeave() {

        List<Ask_leave> list = backstageArrangementMapper.searchLeave();
        System.out.println(list);
        return list;
    }

    @GetMapping("/api/leave/showleavebyid/{staffId}")

    public List searchLeaveById(@PathVariable("staffId") String staffId) {

        List<Ask_leave> list = backstageArrangementMapper.searchLeaveById(staffId);
        System.out.println(list);
        return list;
    }

    @PostMapping("/api/leave/addleaveagree")
    public String agreeLeave(@RequestParam("staffId") String staffId, @RequestParam("isAgree") boolean isAgree, @RequestParam("leaveDate") String leaveDate){

        int i=BackstageArrangementMapper.agreeLeave(staffId, isAgree, leaveDate);
        if(i > 0){
            return "success";
        }else{
            return "fail";
        }
    }

    @PostMapping("/api/bind/addbind")
    public String insertBind(@RequestBody Bind bind) {
        System.out.println("Received bind data: " + bind);
        int i = BackstageArrangementMapper.insertBind(bind);
        if (i > 0) {
            return "success";
        } else {
            return "fail";
        }
    }

    @PostMapping("/api/bind/deletebind")
    public String deleteBind(@RequestParam("id1") String id1, @RequestParam("id2") String id2) {

        int i = BackstageArrangementMapper.deleteBind(id1, id2);
        if (i > 0) {
            return "success";
        } else {
            return "fail";
        }
    }

    @GetMapping("/api/bind/showbind/{id}")

    public List searchBind(@PathVariable("id") String id) {

        List<Bind> list = backstageArrangementMapper.searchBind(id);
        System.out.println(list);
        return list;
    }

}
