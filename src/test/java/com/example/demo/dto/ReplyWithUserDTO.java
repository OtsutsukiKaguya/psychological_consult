package com.example.demo.dto;

public class ReplyWithUserDTO {
    private String replyId;
    private String postId;
    private String replyTime;
    private String replyContent;
    private String pictureLink;
    private TreeHoleUserInfoDTO userInfo;


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

    public TreeHoleUserInfoDTO getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(TreeHoleUserInfoDTO userInfo) {
        this.userInfo = userInfo;
    }

}
