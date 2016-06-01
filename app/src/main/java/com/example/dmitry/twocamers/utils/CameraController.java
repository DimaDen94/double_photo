package com.example.dmitry.twocamers.utils;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.List;

/**
 * Created by Dmitry on 18.05.2016.
 */
public class CameraController {


    private static final String TAG = "mActivityLogs";

    public static Camera getCameraInstance(int cameraId) {
        Camera c = null;
        try {
            c = Camera.open(cameraId); // attempt to get a Camera instance

        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
            Log.e(TAG, "Camera " + cameraId + " not available! " + e.toString());
        }
        return c; // returns null if camera is unavailable
    }

    public static Camera flipCamera(Camera camera,int index,SurfaceHolder holder) {

        if (camera != null) {
            camera.stopPreview();
            camera.release();
        }

        camera = Camera.open(index);

        if (camera != null) {
            try {
                camera.setPreviewDisplay(holder);
                camera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return camera;
    }
}
