package com.example.demo.entity;

public class Post {
    private String postId;
    private String postTime; //修改
    private String postTitle;
    private String postContent;
    private String personId;
    private String pictureLink;
    private String idPictureLink;  //我觉得需要一个贴子发布者的头像
    private Boolean postIsDeleted;
    private Integer thumbNum;


    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
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

    public String getIdPictureLink() {
        return idPictureLink;
    }

    public void setIdPictureLink(String idPictureLink) {
        this.idPictureLink = idPictureLink;
    }

    public Boolean getPostIsDeleted() {
        return postIsDeleted;
    }

    public void setPostIsDeleted(Boolean postIsDeleted) {
        this.postIsDeleted = postIsDeleted;
    }

    public Integer getThumbNum() {
        return thumbNum;
    }

    public void setThumbNum(Integer thumbNum) {
        this.thumbNum = thumbNum;
    }



    @Override
    public String toString() {
        return "Post{" +
                "postId='" + postId + '\'' +
                ", postTime='" + postTime + '\'' +
                ", postTitle='" + postTitle + '\'' +
                ", postContent='" + postContent + '\'' +
                ", personId='" + personId + '\'' +
                ", pictureLink='" + pictureLink + '\'' +
                ", idPictureLink='" + idPictureLink + '\'' +
                ", postIsDeleted=" + postIsDeleted +
                ", thumbNum=" + thumbNum +
                '}';
    }


}
