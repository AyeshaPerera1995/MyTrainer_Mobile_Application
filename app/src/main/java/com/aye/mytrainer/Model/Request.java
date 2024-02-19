package com.aye.mytrainer.Model;

public class Request {

    private String request, created_date, status, approved_date,uid;

    public Request() {
    }

    public Request(String request, String created_date, String status, String approved_date, String uid) {
        this.request = request;
        this.created_date = created_date;
        this.status = status;
        this.approved_date = approved_date;
        this.uid = uid;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApproved_date() {
        return approved_date;
    }

    public void setApproved_date(String approved_date) {
        this.approved_date = approved_date;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
