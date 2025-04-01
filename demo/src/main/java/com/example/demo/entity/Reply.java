package com.example.demo.entity;

import java.time.LocalDateTime;

public class Reply {
    private String replyId;
    private String postId;
    private LocalDateTime replyTime;
    private String replyContent;
    private String person_id;
    private String pictureLink;
    private String idPictureLink;  //我觉得需要一个贴子发布者的头像
    private Boolean replyIsDeleted;
    private Integer thumbNum;

    public String getReplyId() {
        return replyId;
    }

    public void setReplyId(String replyId) {
        this.replyId = replyId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public LocalDateTime getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(LocalDateTime replyTime) {
        this.replyTime = replyTime;
    }

    public String getPerson_id() {
        return person_id;
    }

    public void setPerson_id(String person_id) {
        this.person_id = person_id;
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

    public String getIdPictureLink() {
        return idPictureLink;
    }

    public void setIdPictureLink(String idPictureLink) {
        this.idPictureLink = idPictureLink;
    }

    public Boolean getReplyIsDeleted() {
        return replyIsDeleted;
    }

    public void setReplyIsDeleted(Boolean replyIsDeleted) {
        this.replyIsDeleted = replyIsDeleted;
    }

    public Integer getThumbNum() {
        return thumbNum;
    }

    public void setThumbNum(Integer thumbNum) {
        this.thumbNum = thumbNum;
    }

    @Override
    public String toString() {
        return "Reply{" +
                "replyId='" + replyId + '\'' +
                ", postId='" + postId + '\'' +
                ", replyTime=" + replyTime +
                ", replyContent='" + replyContent + '\'' +
                ", person_id='" + person_id + '\'' +
                ", pictureLink='" + pictureLink + '\'' +
                ", idPictureLink='" + idPictureLink + '\'' +
                ", replyIsDeleted=" + replyIsDeleted +
                ", thumbNum=" + thumbNum +
                '}';
    }

}
