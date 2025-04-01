package com.example.demo.entity;

public class User {
    private String id;
    private String urgentName;
    private String urgentPhone;
    private Boolean userIsBanned;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrgentName() {
        return urgentName;
    }

    public void setUrgentName(String urgentName) {
        this.urgentName = urgentName;
    }

    public String getUrgentPhone() {
        return urgentPhone;
    }

    public void setUrgentPhone(String urgentPhone) {
        this.urgentPhone = urgentPhone;
    }

    public Boolean getUserIsBanned() {
        return userIsBanned;
    }

    public void setUserIsBanned(Boolean userIsBanned) {
        this.userIsBanned = userIsBanned;
    }


    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", urgentName='" + urgentName + '\'' +
                ", urgentPhone='" + urgentPhone + '\'' +
                ", userIsBanned=" + userIsBanned +
                '}';
    }


}
