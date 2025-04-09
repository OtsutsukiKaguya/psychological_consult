package com.example.demo.dto;

import java.time.LocalDateTime;

public class UpdateReplyRequest {
    private String replyTime; //修改
    private String replyContent;
    private String pictureLink;
    private String personId;

    public String getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(String replyTime) {
        this.replyTime = replyTime;
    }

    public String getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }

    public String getPictureLink() {
        return pictureLink;
    }

    public void setPictureLink(String pictureLink) {
        this.pictureLink = pictureLink;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }


    @Override
    public String toString() {
        return "UpdateReplyRequest{" +
                "replyTime=" + replyTime +
                ", replyContent='" + replyContent + '\'' +
                ", pictureLink='" + pictureLink + '\'' +
                ", personId='" + personId + '\'' +
                '}';
    }


}
