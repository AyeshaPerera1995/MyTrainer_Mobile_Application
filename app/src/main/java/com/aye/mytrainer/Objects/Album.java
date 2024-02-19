package com.aye.mytrainer.Objects;

import java.util.ArrayList;

public class Album {

    String albumName;
    int albumImg;
    int imgCount;
    ArrayList<Integer> images;

    public Album(String albumName, int albumImg, int imgCount,ArrayList<Integer> images) {
        this.albumName = albumName;
        this.albumImg = albumImg;
        this.imgCount = imgCount;
        this.images = images;

    }

    public String getAlbumName() {
        return albumName;
    }

    public int getAlbumImg() {
        return albumImg;
    }

    public int getImgCount() {
        return imgCount;
    }

    public ArrayList<Integer> getImages() {
        return images;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public void setAlbumImg(int albumImg) {
        this.albumImg = albumImg;
    }

    public void setImgCount(int imgCount) {
        this.imgCount = imgCount;
    }

    public void setImages(ArrayList<Integer> images) {
        this.images = images;
    }
}
