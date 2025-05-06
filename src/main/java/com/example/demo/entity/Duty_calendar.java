package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableField;

import java.time.LocalDate;

public class Duty_calendar {
    private String staffId;
    private LocalDate dutyDate;
    private int isLeave;

//    /** 新增字段：标记请假(true)或排班(false) */
//    @TableField("is_leave")
//    private Boolean isLeave;

    /** 新增字段：班次类型（1=早班、2=晚班等，可扩展） */
    @TableField("shift_type")
    private Integer shiftType;

    public int getIsLeave() {
        return isLeave;
    }

    public void setIsLeave(int isLeave) {
        this.isLeave = isLeave;
    }

    public LocalDate getDutyDate() {
        return dutyDate;
    }

    public void setDutyDate(LocalDate dutyDate) {
        this.dutyDate = dutyDate;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public Integer getShiftType() {
        return shiftType;
    }

    public void setShiftType(Integer shiftType) {
        this.shiftType = shiftType;
    }

    @Override
    public String toString() {
        return "Duty_calendar{" +
                "staffId='" + staffId + '\'' +
                ", dutyDate=" + dutyDate +
                ", isLeave=" + isLeave +
                '}';
    }
}
