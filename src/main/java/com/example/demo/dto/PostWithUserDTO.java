package com.example.demo.dto;

import java.time.LocalDateTime;

public class PostWithUserDTO {
    private String postId;
    private String postTitle;
    private String postContent;
    private String postTime;
    private String pictureLink;
    private TreeHoleUserInfoDTO userInfo;


    public String getPictureLink() {
        return pictureLink;
    }

    public void setPictureLink(String pictureLink) {
        this.pictureLink = pictureLink;
    }


    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
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

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }

    public TreeHoleUserInfoDTO getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(TreeHoleUserInfoDTO userInfo) {
        this.userInfo = userInfo;
    }
}
