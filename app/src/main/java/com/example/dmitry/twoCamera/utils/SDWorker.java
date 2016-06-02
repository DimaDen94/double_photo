package com.example.dmitry.twocamera.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Dmitry on 18.05.2016.
 */
public class SDWorker {
    private static final String TAG = "mActivityLogs";

    public static File createDirectory() {
        File directory = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                "double_photo");
        if (!directory.exists())
            directory.mkdirs();
        return directory;
    }


    public static File generateFileUri(File directory, int pos) {
        if (pos == 0)
            return new File(directory.getPath() + "/" + "back_" + "photo_" + System.currentTimeMillis() + ".jpg");
        else if (pos == 1)
            return new File(directory.getPath() + "/" + "front_" + "photo_" + System.currentTimeMillis() + ".jpg");
        else
            return new File(directory.getPath() + "/" + "con_" + "photo_" + System.currentTimeMillis() + ".jpg");
    }


    public static boolean writePhotoAndPutToGallery(File file, byte[] data, Context c) throws IOException {
        writePhoto(file, data);
        galleryAddPic(file, c);
        return true;
    }

    private static boolean writePhoto(File file, byte[] data) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        Log.d(TAG, file.toString());
        fos.write(data);
        fos.close();
        return true;
    }


   public static boolean writePhotoAndPutToGallery(File file,Bitmap bitmap, Context c) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] data = bos.toByteArray();
        writePhotoAndPutToGallery(file,data,c);
        return true;
    }
    private static void galleryAddPic(File file, Context c) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        c.sendBroadcast(mediaScanIntent);
    }
    public static Bitmap rotateImage(Bitmap bitmapSrc, File imagePath) {
        Matrix matrix = new Matrix();
        ExifInterface exif;
        try {
            exif = new ExifInterface(imagePath.getAbsolutePath());
            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            System.out.println("yuri" + exifOrientation);

            switch (exifOrientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    break;
                case ExifInterface.ORIENTATION_NORMAL:
                    matrix.postRotate(0);
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap newBitmap = Bitmap.createBitmap(bitmapSrc, 0, 0, bitmapSrc.getWidth(), bitmapSrc.getHeight(), matrix, true);
        //bitmapSrc.recycle();
        return newBitmap;
    }

    public static Bitmap createBitmap(int width, File photo) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photo.getAbsolutePath(), options);
        int outWidth = options.outWidth;
        if (outWidth > width) {
            options.inSampleSize = Math.round((float) outWidth / (float) width);
        }
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(photo.getAbsolutePath(), options);
        Log.d(TAG, bitmap.getWidth() + "    " + bitmap.getHeight());
        return bitmap;
    }


    public static Bitmap createBitmap(File photo) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photo.getAbsolutePath(), options);

        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(photo.getAbsolutePath(), options);

        Log.d(TAG, bitmap.getWidth() + "    " + bitmap.getHeight());
        return bitmap;
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
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
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    public static void deleteOthers(File back, File front, Context c) {
        back.delete();
        c.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(back)));

        front.delete();
        c.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(front)));
    }
}
