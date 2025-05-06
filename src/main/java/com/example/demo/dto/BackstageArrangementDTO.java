package com.example.demo.dto;

public class BackstageArrangementDTO {
    private String id;               // 对应 p.id (人员ID)
    private String name;             // 对应 p.name (姓名)
    private String gender;           // 对应 p.gender (性别)
    private Integer age;             // 对应 p.age (年龄)
    private String idPictureLink;    // 对应 p.id_picture_link (证件照片链接)
    private String selfDescription;  // 对应 p.self_description (自我描述)
    private String tag;              // 对应 c.tag (咨询师标签)

    // 构造函数
    public BackstageArrangementDTO() {
    }

    // Getter 和 Setter 方法
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getIdPictureLink() {
        return idPictureLink;
    }

    public void setIdPictureLink(String idPictureLink) {
        this.idPictureLink = idPictureLink;
    }

    public String getSelfDescription() {
        return selfDescription;
    }

    public void setSelfDescription(String selfDescription) {
        this.selfDescription = selfDescription;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    // toString 方法，便于调试
    @Override
    public String toString() {
        return "BackstageArrangementDTO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", age=" + age +
                ", idPictureLink='" + idPictureLink + '\'' +
                ", selfDescription='" + selfDescription + '\'' +
                ", tag='" + tag + '\'' +
                '}';
    }

}
