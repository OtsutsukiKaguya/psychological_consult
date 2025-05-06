package com.example.demo.dto;

public class InformationDTO {
    private String id;
    private String name;
    private String idPictureLink;
    private String state;
    private Double averageRating;
    private String tag;

    @Override
    public String toString() {
        return "InformationDto{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", idPictureLink='" + idPictureLink + '\'' +
                ", state='" + state + '\'' +
                ", averageRating=" + averageRating +
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

    public String getIdPictureLink() {
        return idPictureLink;
    }

    public void setIdPictureLink(String idPictureLink) {
        this.idPictureLink = idPictureLink;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
