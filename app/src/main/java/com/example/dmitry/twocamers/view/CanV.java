package com.example.dmitry.twocamers.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;

import com.example.dmitry.twocamers.control.Controler;
import com.example.dmitry.twocamers.model.SmallPicture;

import java.io.File;

/**
 * Created by Dmitry on 26.05.2016.
 */
public class CanV extends View {

    private SmallPicture smallPicture;
    private Controler controler;
    private Context context;
    //private ScaleGestureDetector mScaleDetector;
    //private float mScaleFactor = 1.f;

    public CanV(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        //mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    public void setControler(Controler controler) {
        this.controler = controler;
        controler.initWidthAndHeight(context);
    }



    public void initBitmaps(File backPhotoFile, File frontPhotoFile) {
        controler.initBitmaps(backPhotoFile, frontPhotoFile);
    }

    public void initSmallPicture(Bitmap frontBitmap) {
        smallPicture = controler.initSmallPicture(frontBitmap);
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



        /*mScaleDetector.onTouchEvent(event);
        controler.setZoom(controler.getZoom()+mScaleFactor);*/

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                smallPicture.move((int) event.getX(), (int) event.getY());
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:

                smallPicture.move((int) event.getX(), (int) event.getY());


                invalidate();
                break;
        }
        return true;
    }

    public void makeThePicture(ProgressBar progressBar) {
        controler.makeThePicture(progressBar);
    }


   /* private class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

                    // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));
            controler.setZoom(mScaleFactor);
            invalidate();
            return true;
        }
    }*/



}

