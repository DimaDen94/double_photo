package com.example.dmitry.twocamers.utils;

import android.content.Context;
import android.content.Intent;
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
        writePhoto(file,data);
        galleryAddPic(file,c);
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
}
