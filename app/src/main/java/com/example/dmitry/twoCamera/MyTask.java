package com.example.dmitry.twocamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.dmitry.twocamera.model.Data;
import com.example.dmitry.twocamera.utils.SDWorker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Created by Dmitry on 29.05.2016.
 */
public class MyTask extends AsyncTask<Data, Void, Void> {
    ProgressBar progressBar;
    Button btnSave;
    private static final String TAG = "log";
    File back;
    File front;
    Context c;

    @Override
    protected Void doInBackground(Data... params) {

        progressBar = params[0].getProgressBar();
        btnSave = params[0].getBtnPost();
        back = params[0].getBack();
        front = params[0].getFront();

        Bitmap bBitmap = SDWorker.createBitmap(back);
        //Bitmap fBitmap = SDWorker.createBitmap(front);


        int largeW = bBitmap.getWidth() / params[0].getBackBitmapWidth();
        int largeH = bBitmap.getHeight() / params[0].getBackBitmapHeight();

        Bitmap concatedBitmap = Bitmap.createBitmap(bBitmap.getWidth(), bBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(concatedBitmap);

        canvas.drawBitmap(bBitmap, 0, 0, null);
        int x = params[0].getSmallPicture().getX() * largeW;
        int y = params[0].getSmallPicture().getY() * largeH;
        Bitmap resizeBitmap ;
        resizeBitmap = SDWorker.createBitmap(bBitmap.getWidth(), front);
        resizeBitmap = SDWorker.getResizedBitmap(resizeBitmap, (int) (bBitmap.getWidth() / params[0].getZoom()), (int) (bBitmap.getHeight() / params[0].getZoom()));

        canvas.drawBitmap(resizeBitmap, x, y, null);


        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        concatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] data = bos.toByteArray();

        File outputFile = SDWorker.generateFileUri(SDWorker.createDirectory(), 2);
        c = params[0].getC();
        try {
            SDWorker.writePhotoAndPutToGallery(outputFile, data, c);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        progressBar.setVisibility(ProgressBar.INVISIBLE);
        btnSave.setEnabled(true);
    }



}