package com.example.demo.dto;

import java.time.LocalDateTime;

public class UpdatePostRequest {
    private String postTime; //修改
    private String postTitle;
    private String postContent;
    private String pictureLink;
    private String personId;

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostContent() {
        return postContent;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    public String getPictureLink() {
        return pictureLink;
    }

    public void setPictureLink(String pictureLink) {
        this.pictureLink = pictureLink;
    }


    @Override
    public String toString() {
        return "UpdatePostRequest{" +
                "postTime=" + postTime +
                ", postTitle='" + postTitle + '\'' +
                ", postContent='" + postContent + '\'' +
                ", pictureLink='" + pictureLink + '\'' +
                ", personId='" + personId + '\'' +
                '}';
    }



}
