package com.example.dmitry.twocamera.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.dmitry.twocamera.utils.CanvasController;
import com.example.dmitry.twocamera.model.SmallPicture;

import java.io.File;


public class CanV extends View {

    private SmallPicture smallPicture;
    private CanvasController canvasController;
    private Context context;

    int touchState;
    final int IDLE = 0;
    final int TOUCH = 1;
    final int PINCH = 2;
    float distx, disty;
    float dist0 = 1, distCurrent = 1;


    public CanV(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

    }

    public void setCanvasController(CanvasController canvasController) {
        this.canvasController = canvasController;
        canvasController.initWidthAndHeight(context);
    }


    public void initBitmaps(File backPhotoFile, File frontPhotoFile) {
        canvasController.initBitmaps(backPhotoFile, frontPhotoFile);
        smallPicture = canvasController.initSmallPicture(canvasController.getFrontBitmap());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.drawBitmap(canvasController.getBackBitmap(), 0, 0, new Paint(Paint.FILTER_BITMAP_FLAG));
        canvas.drawBitmap(smallPicture.getPicture(), smallPicture.getX(), smallPicture.getY(), new Paint(Paint.FILTER_BITMAP_FLAG));
        canvas.restore();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                touchState = TOUCH;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                Log.d("log", "yes");
                touchState = PINCH;
                distx = event.getX(0) - event.getX(1);
                disty = event.getY(0) - event.getY(1);
                dist0 = (float) Math.sqrt(distx * distx + disty * disty);
                break;
            case MotionEvent.ACTION_MOVE:
                if (touchState == PINCH) {
                    //Get the current distance
                    distx = event.getX(0) - event.getX(1);
                    disty = event.getY(0) - event.getY(1);
                    distCurrent = (float) Math.sqrt(distx * distx + disty * disty);
                    float dif = dist0 - distCurrent;
                    Log.d("log", "" + distCurrent);
                    canvasController.setZoom(canvasController.getZoom() + dif / 5000);
                    canvasController.scalingBitmap();
                    Log.d("log", "zoom " + canvasController.getZoom());
                    smallPicture.move((int) event.getX(), (int) event.getY());
                } else
                    smallPicture.move((int) event.getX(), (int) event.getY());


                invalidate();
                break;
            case MotionEvent.ACTION_POINTER_UP:
                canvasController.scalingBitmapB();
                invalidate();
                touchState = TOUCH;
                break;
            case MotionEvent.ACTION_UP:

                touchState = IDLE;
                break;

        }
        return true;
    }

    public void makeThePicture(ProgressBar progressBar, Button btnSave) {
        canvasController.makeThePicture(progressBar, btnSave);
    }
}

