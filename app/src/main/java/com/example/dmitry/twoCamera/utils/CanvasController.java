package com.example.dmitry.twocamera.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.dmitry.twocamera.MyTask;
import com.example.dmitry.twocamera.model.Data;
import com.example.dmitry.twocamera.model.SmallPicture;

import java.io.File;

/**
 * Created by Dmitry on 29.05.2016.
 */
public class CanvasController {
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
        //backBitmap = SDWorker.getResizedBitmap(backBitmap,width,height);
        frontBitmap = SDWorker.createBitmap(width, frontPhotoFile);
        frontBitmap = SDWorker.getResizedBitmap(frontBitmap, (int) (width / zoom), (int) (height / zoom));
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

    public void makeThePicture(ProgressBar progressBar, Button btnSave) {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        MyTask task = new MyTask();
        Data data = Data.getInstance();
        if (back != null)
            data.setData(btnSave, progressBar, back, front, backBitmap.getWidth(), backBitmap.getHeight(), smallPicture, zoom, c);
        task.execute(data);
    }

    public Bitmap getBackBitmap() {
        return backBitmap;
    }

    public Bitmap getFrontBitmap() {
        return frontBitmap;
    }

    public double getZoom() {
        return zoom;

    }

    public void setZoom(double zoom) {
        if (zoom > 10)
            zoom = 10;
        if (zoom < 1)
            zoom = 1;
        this.zoom = zoom;
    }

    public void scalingBitmap() {
        smallPicture.setPicture(SDWorker.getResizedBitmap(smallPicture.getPicture(), (int) (width / zoom), (int) (height / zoom)));
    }

    public void scalingBitmapB() {
        smallPicture.setPicture(SDWorker.createBitmap(width, front));
        smallPicture.setPicture(SDWorker.getResizedBitmap(smallPicture.getPicture(), (int) (width / zoom), (int) (height / zoom)));
    }

}
