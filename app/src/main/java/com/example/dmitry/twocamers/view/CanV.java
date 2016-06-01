package com.example.dmitry.twocamers.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;

import com.example.dmitry.twocamers.utils.CanvasController;
import com.example.dmitry.twocamers.model.SmallPicture;

import java.io.File;

/**
 * Created by Dmitry on 26.05.2016.
 */
public class CanV extends View {

    private SmallPicture smallPicture;
    private CanvasController controler;
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

    public void setControler(CanvasController controler) {
        this.controler = controler;
        controler.initWidthAndHeight(context);
    }


    public void initBitmaps(File backPhotoFile, File frontPhotoFile) {
        controler.initBitmaps(backPhotoFile, frontPhotoFile);
        smallPicture = controler.initSmallPicture(controler.getFrontBitmap());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();


        canvas.drawBitmap(controler.getBackBitmap(), 0, 0, new Paint(Paint.FILTER_BITMAP_FLAG));
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
                    controler.setZoom(controler.getZoom() + dif / 5000);
                    controler.scalingBitmap();
                    Log.d("log", "zoom " + controler.getZoom());
                    smallPicture.move((int) event.getX(), (int) event.getY());

                } else
                    smallPicture.move((int) event.getX(), (int) event.getY());


                invalidate();
                break;
            case MotionEvent.ACTION_UP:

                touchState = IDLE;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                controler.scalingBitmapB();
                invalidate();
                touchState = TOUCH;
                break;
        }
        return true;
    }

    public void makeThePicture(ProgressBar progressBar) {
        controler.makeThePicture(progressBar);
    }

}

