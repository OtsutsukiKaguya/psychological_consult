package com.example.demo.dto;

public class BackstageArrangementDTO {
    private String  id;
    private String  name;
    private String  gender;
    private int  age;
    private String  id_picture_link;
    private String  self_description;

    @Override
    public String toString() {
        return "BackstageArrangementDTO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", age='" + age + '\'' +
                ", id_picture_link='" + id_picture_link + '\'' +
                ", self_description='" + self_description + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public int getAge() {
        return age;
    }

    public String getId_picture_link() {
        return id_picture_link;
    }

    public String getSelf_description() {
        return self_description;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setId_picture_link(String id_picture_link) {
        this.id_picture_link = id_picture_link;
    }

    public void setSelf_description(String self_description) {
        this.self_description = self_description;
    }

}
