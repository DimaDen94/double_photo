package com.example.dmitry.twocamera.model;

import android.content.Context;
import android.widget.Button;
import android.widget.ProgressBar;

import java.io.File;

/**
 * Created by Dmitry on 29.05.2016.
 */
public class Data {

    private static Data ourInstance = new Data();

    private File back;
    private File front;
    private int backBitmapWidth;
    private int backBitmapHeight;
    private SmallPicture smallPicture;
    private double zoom;
    private Context c;
    private ProgressBar progressBar;
    private Button btnSave;

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public static Data getInstance() {
        return ourInstance;
    }


    private Data() {
    }
    public void setData(Button btnPost, ProgressBar progressBar, File back, File front, int backBitmapWidth, int backBitmapHeight, SmallPicture smallPicture, double zoom, Context c) {
        this.progressBar = progressBar;
        this.btnSave = btnPost;
        this.back = back;
        this.front = front;
        this.backBitmapWidth = backBitmapWidth;
        this.backBitmapHeight = backBitmapHeight;
        this.smallPicture = smallPicture;
        this.zoom = zoom;
        this.c = c;
    }

    public Button getBtnPost() {
        return btnSave;
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
