package com.example.dmitry.twocamers.model;

import android.content.Context;
import android.widget.ProgressBar;

import java.io.File;

/**
 * Created by Dmitry on 29.05.2016.
 */
public class Data {
    private File back;
    private File front;
    private int backBitmapWidth;
    private int backBitmapHeight;
    private SmallPicture smallPicture;
    private double zoom;
    private Context c;
    private ProgressBar progressBar;

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public Data(ProgressBar progressBar, File back, File front, int backBitmapWidth, int backBitmapHeight, SmallPicture smallPicture, double zoom, Context c) {
        this.progressBar = progressBar;

        this.back = back;
        this.front = front;
        this.backBitmapWidth = backBitmapWidth;
        this.backBitmapHeight = backBitmapHeight;
        this.smallPicture = smallPicture;
        this.zoom = zoom;
        this.c = c;
    }

    public File getBack() {
        return back;
    }

    public File getFront() {
        return front;
    }

    public int getBackBitmapWidth() {
        return backBitmapWidth;
    }

    public int getBackBitmapHeight() {
        return backBitmapHeight;
    }

    public SmallPicture getSmallPicture() {
        return smallPicture;
    }

    public double getZoom() {
        return zoom;
    }

    public Context getC() {
        return c;
    }
}
