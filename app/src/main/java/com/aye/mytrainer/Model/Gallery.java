package com.aye.mytrainer.Model;

public class Gallery {

    private String image_id, image_url;

    public Gallery() {
    }

    public Gallery(String image_id, String image_url) {
        this.image_id = image_id;
        this.image_url = image_url;
    }

    public String getImage_id() {
        return image_id;
    }

    public void setImage_id(String image_id) {
        this.image_id = image_id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
