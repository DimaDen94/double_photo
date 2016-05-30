package com.example.dmitry.twocamers.control;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.example.dmitry.twocamers.model.Data;
import com.example.dmitry.twocamers.MyTask;

import com.example.dmitry.twocamers.model.SmallPicture;
import com.example.dmitry.twocamers.utils.SDWorker;

import java.io.File;

/**
 * Created by Dmitry on 29.05.2016.
 */
public class Controler {
    private Bitmap backBitmap;
    private Bitmap frontBitmap;
    private int width;
    private int height;


    public double zoom = 4.5;
    private SmallPicture smallPicture;
    private File back;
    private File front;
    private Context c;

    public void initBitmaps(File backPhotoFile, File frontPhotoFile) {
        back = backPhotoFile;
        front = frontPhotoFile;

        backBitmap = SDWorker.createBitmap(width, backPhotoFile);
        frontBitmap = SDWorker.createBitmap((int) (width / zoom), frontPhotoFile);
    }

    public void initWidthAndHeight(Context context) {
        c = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        width = point.x;
        height = point.y;
    }

    public SmallPicture initSmallPicture(Bitmap frontBitmap) {
        return smallPicture = new SmallPicture(frontBitmap, width / 5, height / 6 * 4);
    }

    public void makeThePicture(ProgressBar progressBar) {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        MyTask task = new MyTask();
        Data data = new Data(progressBar, back, front, backBitmap.getWidth(), backBitmap.getHeight(), smallPicture, zoom, c);
        task.execute(data);
    }

    public Bitmap getBackBitmap() {
        return backBitmap;
    }

    public Bitmap getFrontBitmap() {
        return frontBitmap;
    }


}
