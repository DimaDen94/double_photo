package com.example.dmitry.twocamers.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

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
                "MyFolder");
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
}
