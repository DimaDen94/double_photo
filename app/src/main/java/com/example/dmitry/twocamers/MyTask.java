package com.example.dmitry.twocamers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;

import com.example.dmitry.twocamers.model.Data;
import com.example.dmitry.twocamers.utils.SDWorker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Created by Dmitry on 29.05.2016.
 */
public class MyTask extends AsyncTask<Data, Void, Void> {
    ProgressBar progressBar;
    private static final String TAG = "log";
    File back;
    File front;
    Context c;

    @Override
    protected Void doInBackground(Data... params) {
        this.progressBar = params[0].getProgressBar();

        back = params[0].getBack();
        front = params[0].getFront();

        Bitmap bBitmap = createBitmap(back);
        Bitmap fBitmap = createBitmap(front);

        int largeW = bBitmap.getWidth() / params[0].getBackBitmapWidth();
        int largeH = bBitmap.getHeight() / params[0].getBackBitmapHeight();

        Bitmap concatedBitmap = Bitmap.createBitmap(bBitmap.getWidth(), bBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(concatedBitmap);

        canvas.drawBitmap(bBitmap, 0, 0, null);
        int x = params[0].getSmallPicture().getX() * largeW;
        int y = params[0].getSmallPicture().getY() * largeH;

        Bitmap resizeBitmap = getResizedBitmap(fBitmap, (int) (bBitmap.getWidth() / params[0].getZoom()), (int) (bBitmap.getHeight() / params[0].getZoom()));

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
        SDWorker.deleteOthers(back, front,c);
    }

    private Bitmap createBitmap(File photo) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photo.getAbsolutePath(), options);

        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(photo.getAbsolutePath(), options);

        Log.d(TAG, bitmap.getWidth() + "    " + bitmap.getHeight());
        return bitmap;
    }

    private Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();

        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();

        RectF fRect = new RectF(0, 0, width, height);
        RectF sRect = new RectF(0, 0, newWidth, newHeight);

        matrix.setRectToRect(fRect, sRect, Matrix.ScaleToFit.START);
        // RESIZE THE BIT MAP
        //matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }
}