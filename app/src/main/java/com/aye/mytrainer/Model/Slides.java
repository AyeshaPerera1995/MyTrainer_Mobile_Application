package com.aye.mytrainer.Model;

public class Slides {
    private String s_id,s_image;

    public Slides() {
    }

    public Slides(String s_id, String s_image) {
        this.s_id = s_id;
        this.s_image = s_image;
    }

    public String getS_id() {
        return s_id;
    }

    public void setS_id(String s_id) {
        this.s_id = s_id;
    }

    public String getS_image() {
        return s_image;
    }

    public void setS_image(String s_image) {
        this.s_image = s_image;
    }

}
