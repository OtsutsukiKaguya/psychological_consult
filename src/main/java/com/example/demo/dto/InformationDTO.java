package com.example.demo.dto;

public class InformationDTO {
    private String id;
    private String name;
    private String id_picture_link;
    private String state;
    private Double average_rating;
    private String tag;

    @Override
    public String toString() {
        return "InformationDto{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", id_picture_link='" + id_picture_link + '\'' +
                ", state='" + state + '\'' +
                ", average_rating=" + average_rating +
                ", tag='" + tag + '\'' +
                '}';
    }

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

    public String getId_picture_link() {
        return id_picture_link;
    }

    public void setId_picture_link(String id_picture_link) {
        this.id_picture_link = id_picture_link;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Double getAverage_rating() {
        return average_rating;
    }

    public void setAverage_rating(Double average_rating) {
        this.average_rating = average_rating;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
