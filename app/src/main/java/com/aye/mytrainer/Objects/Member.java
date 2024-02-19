package com.aye.mytrainer.Objects;

public class Member {
    String memName;
    int memImage;
    String memMail;

    public Member(String memName, int memImage, String memMail) {
        this.memName = memName;
        this.memImage = memImage;
        this.memMail = memMail;
    }

    public int getMemImage() {
        return memImage;
    }

    public String getMemName() {
        return memName;
    }

    public String getMemMail() {
        return memMail;
    }
}
