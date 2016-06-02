package com.example.dmitry.twocamera.model;

import android.graphics.Bitmap;


public class SmallPicture {
    private int x;
    private int y;

    private Bitmap picture;

    public SmallPicture(Bitmap picture, int x, int y) {
        this.picture = picture;
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }


    public void move(int x, int y) {
        this.x = x-picture.getWidth()/2;
        this.y = y-picture.getHeight()/2;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }
}
