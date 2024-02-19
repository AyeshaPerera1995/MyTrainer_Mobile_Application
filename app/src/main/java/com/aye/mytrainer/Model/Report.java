package com.aye.mytrainer.Model;

public class Report {
    private String date, burn_calories, get_calories;

    public Report(String date, String burn_calories, String get_calories) {
        this.date = date;
        this.burn_calories = burn_calories;
        this.get_calories = get_calories;
    }

    public Report() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBurn_calories() {
        return burn_calories;
    }

    public void setBurn_calories(String burn_calories) {
        this.burn_calories = burn_calories;
    }

    public String getGet_calories() {
        return get_calories;
    }

    public void setGet_calories(String get_calories) {
        this.get_calories = get_calories;
    }
}
