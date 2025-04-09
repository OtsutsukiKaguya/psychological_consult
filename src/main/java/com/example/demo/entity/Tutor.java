package com.example.demo.entity;

public class Tutor {
    private String id;
    private String dutyArrangement;
    private String institute;
    private String jobTitle;
    private Integer counselorSameTime;
    private String tag;
    private Boolean isQualified;
    private String qualifiedNumber;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDutyArrangement() {
        return dutyArrangement;
    }

    public void setDutyArrangement(String dutyArrangement) {
        this.dutyArrangement = dutyArrangement;
    }

    public String getInstitute() {
        return institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public Integer getCounselorSameTime() {
        return counselorSameTime;
    }

    public void setCounselorSameTime(Integer counselorSameTime) {
        this.counselorSameTime = counselorSameTime;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Boolean getQualified() {
        return isQualified;
    }

    public void setQualified(Boolean qualified) {
        isQualified = qualified;
    }

    public String getQualifiedNumber() {
        return qualifiedNumber;
    }

    public void setQualifiedNumber(String qualifiedNumber) {
        this.qualifiedNumber = qualifiedNumber;
    }


    @Override
    public String toString() {
        return "Tutor{" +
                "id='" + id + '\'' +
                ", dutyArrangement='" + dutyArrangement + '\'' +
                ", institute='" + institute + '\'' +
                ", jobTitle='" + jobTitle + '\'' +
                ", counselorSameTime=" + counselorSameTime +
                ", tag='" + tag + '\'' +
                ", isQualified=" + isQualified +
                ", qualifiedNumber='" + qualifiedNumber + '\'' +
                '}';
    }

}
