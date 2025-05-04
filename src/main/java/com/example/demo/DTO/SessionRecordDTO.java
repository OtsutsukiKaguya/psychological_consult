package com.example.demo.DTO;

import com.example.demo.models.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionRecordDTO {

    private String visitorName;
    private SimpleUserDTO counselor;
    private SimpleUserDTO supervisor;
    private String duration;
    private String date;
    private Integer rating;
    private String userComment;
    private String counselorComment;
    private String tutorComment;


    public String getVisitorName() {
        return visitorName;
    }

    public void setVisitorName(String visitorName) {
        this.visitorName = visitorName;
    }

    public SimpleUserDTO getCounselor() {
        return counselor;
    }

    public void setCounselor(SimpleUserDTO counselor) {
        this.counselor = counselor;
    }

    public SimpleUserDTO getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(SimpleUserDTO supervisor) {
        this.supervisor = supervisor;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getUserComment() {
        return userComment;
    }

    public void setUserComment(String userComment) {
        this.userComment = userComment;
    }

    public String getCounselorComment() {
        return counselorComment;
    }

    public void setCounselorComment(String counselorComment) {
        this.counselorComment = counselorComment;
    }

    public String getTutorComment() {
        return tutorComment;
    }

    public void setTutorComment(String tutorComment) {
        this.tutorComment = tutorComment;
    }


}


