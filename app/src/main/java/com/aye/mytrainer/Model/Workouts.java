package com.aye.mytrainer.Model;

import com.aye.mytrainer.Login;

import java.util.HashMap;

public class Workouts {
    private String name,sets,reps,time_period,status,date,burn_calories;
    private Long timestamp;

    public Workouts() {
    }

    public Workouts(String date, String name, String sets, String reps, String time_period, String status,String burn_calories, Long timestamp) {
        this.date = date;
        this.name = name;
        this.sets = sets;
        this.reps = reps;
        this.timestamp = timestamp;
        this.time_period = time_period;
        this.burn_calories = burn_calories;
        this.status = status;
    }

    public String getBurn_calories() {
        return burn_calories;
    }

    public void setBurn_calories(String burn_calories) {
        this.burn_calories = burn_calories;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTime_period() {
        return time_period;
    }

    public void setTime_period(String time_period) {
        this.time_period = time_period;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSets() {
        return sets;
    }

    public void setSets(String sets) {
        this.sets = sets;
    }

    public String getReps() {
        return reps;
    }

    public void setReps(String reps) {
        this.reps = reps;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
