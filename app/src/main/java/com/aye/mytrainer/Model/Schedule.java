package com.aye.mytrainer.Model;

public class Schedule {

    private String date, note, created_date;

    public Schedule(String date, String note, String created_date) {
        this.date = date;
        this.note = note;
        this.created_date = created_date;
    }

    public Schedule() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }
}
