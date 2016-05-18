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
public class CameraControler {


    private static final String TAG = "mActivityLogs";

    public static Camera setCameraDisplayOrientation(Activity activity, int cameraId, Camera camera) {
        // определяем насколько повернут экран от нормального положения
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result = 0;

        // получаем инфо по камере cameraId
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);

        // задняя камера
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
            result = ((360 - degrees) + info.orientation);
        } else
            // передняя камера
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                result = ((360 - degrees) - info.orientation);
                result += 360;
            }
        result = result % 360;
        camera.setDisplayOrientation(result);

        int r=0;

        // задняя камера

        switch (rotation) {
            case Surface.ROTATION_0:
                if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    Log.d(TAG, "1");
                    r = ((360 - degrees) + info.orientation);
                } else {
                    Log.d(TAG, "2");
                    r = ((360 - degrees) - info.orientation) + 180;
                }

                r = r % 360;
                break;
            case Surface.ROTATION_90:
                if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    Log.d(TAG, "3");
                    r = ((360 - degrees) + info.orientation);
                } else {
                    Log.d(TAG, "4");
                    r = ((360 - degrees) - info.orientation);
                }

                r = r % 360;
                break;
            case Surface.ROTATION_180:
                if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    Log.d(TAG, "5");
                    r = ((360 - degrees) + info.orientation);
                } else {
                    Log.d(TAG, "6");
                    r = ((360 - degrees) - info.orientation) + 180;
                }

                r = r % 360;
                break;
            case Surface.ROTATION_270:
                if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    Log.d(TAG, "7");
                    r = ((360 - degrees) + info.orientation);
                } else {
                    Log.d(TAG, "8");
                    r = ((360 - degrees) - info.orientation);
                }

                r = r % 360;
                break;
        }




        Camera.Parameters parameters = camera.getParameters();
        List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
        Camera.Size size = sizes.get(0);
        for (int i = 0; i < sizes.size(); i++) {
            if (sizes.get(i).width > size.width)
                size = sizes.get(i);
        }
        parameters.setPictureSize(size.width, size.height);
        parameters.set("jpeg-quality", 10);
        parameters.setRotation(r);
       /* if (cameraId == 0)
            parameters.setRotation(result);
        else
            parameters.setRotation(result-90);*/
        camera.setParameters(parameters);
        return camera;
    }



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
            camera = null;

        }

        camera = Camera.open(index);
        //setCameraDisplayOrientation(this,index,camera);
        //setCameraDisplayOrientation(index);


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
