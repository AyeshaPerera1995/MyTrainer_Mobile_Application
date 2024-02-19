package com.aye.mytrainer.Objects;

public class Exercise {

    int workoutImage;
    String wName;
    String wTime;
    int wSets;
    int wReps;
    String wStatus;
    int wBurnCal;
    String wDate;

    public Exercise(int workoutImage, String wName, String wTime, int wSets, int wReps) {
        this.workoutImage = workoutImage;
        this.wName = wName;
        this.wTime = wTime;
        this.wSets = wSets;
        this.wReps = wReps;
    }

    public int getWorkoutImage() {
        return workoutImage;
    }

    public String getwName() {
        return wName;
    }

    public String getwTime() {
        return wTime;
    }

    public int getwSets() {
        return wSets;
    }

    public int getwReps() {
        return wReps;
    }
}
