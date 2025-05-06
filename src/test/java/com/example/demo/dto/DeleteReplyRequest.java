package com.example.demo.dto;

public class DeleteReplyRequest {
    private String adminId;
    private String deleteReason;

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getDeleteReason() {
        return deleteReason;
    }

    public void setDeleteReason(String deleteReason) {
        this.deleteReason = deleteReason;
    }

    @Override
    public String toString() {
        return "DeleteReplyRequest{" +
                "adminId='" + adminId + '\'' +
                ", deleteReason='" + deleteReason + '\'' +
                '}';
    }


}
