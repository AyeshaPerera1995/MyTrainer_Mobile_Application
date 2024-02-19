package com.aye.mytrainer.Model;

import java.util.HashMap;

public class Meals {
    private String last_update,calories,meal,time,title,status,com_time;
    private Long timestamp;

    public Meals() {
    }

    public Meals(String last_update, String calories, String meal, String time, String title, String status, String com_time,Long timestamp) {
        this.last_update = last_update;
        this.calories = calories;
        this.meal = meal;
        this.time = time;
        this.title = title;
        this.status = status;
        this.com_time = com_time;
        this.timestamp = timestamp;
    }

    public String getLast_update() {
        return last_update;
    }

    public void setLast_update(String last_update) {
        this.last_update = last_update;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public String getMeal() {
        return meal;
    }

    public void setMeal(String meal) {
        this.meal = meal;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCom_time() {
        return com_time;
    }

    public void setCom_time(String com_time) {
        this.com_time = com_time;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
