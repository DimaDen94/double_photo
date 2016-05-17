package com.example.dmitry.twocamers;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class MainActivity extends Activity {
    private static final String TAG = "mActivityLogs";
    SurfaceView sv;
    SurfaceHolder holder;
    HolderCallback holderCallback;
    Camera camera;
    File directory;
    File backPhotoFile;
    File frontPhotoFile;
    File outputFile;

    final boolean FULL_SCREEN = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        createDirectory();


        sv = (SurfaceView) findViewById(R.id.surfaceView);
        holder = sv.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        holderCallback = new HolderCallback();
        holder.addCallback(holderCallback);


    }


    @Override
    protected void onResume() {
        super.onResume();
        camera = getCameraInstance(0);

        setCameraDisplayOrientation(0);

        //setCameraDisplayOrientation(this,CAMERA_BACK_ID,camera);
        updateFocus();
        setPreviewSize(FULL_SCREEN);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (camera != null)
            camera.release();
        camera = null;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();


    }

    class HolderCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                camera.setPreviewDisplay(holder);
                camera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            camera.stopPreview();

            try {
                camera.setPreviewDisplay(holder);
                camera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }

    }

    public void onClickPicture(View view) {

        backPhotoFile = generateFileUri(0);
        frontPhotoFile = generateFileUri(1);
        takeAndSavePhoto(backPhotoFile, frontPhotoFile);

    }

    private boolean flipCamera(int index) {

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
        return true;
    }

    private boolean takeAndSavePhoto(final File file, final File file2) {

        List<String> supportedFocusModes = camera.getParameters().getSupportedFocusModes();
        boolean hasAutoFocus = supportedFocusModes != null && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO);
        if (hasAutoFocus) {
            camera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    if (success) {
                        // если удалось сфокусироваться, делаем снимок
                        camera.takePicture(null, null, null, new Camera.PictureCallback() {
                            @Override
                            public void onPictureTaken(byte[] data, Camera camera) {
                                try {
                                    writePhoto(file, data);

                                    flipCamera(1);
                                    setCameraDisplayOrientation(1);

                                    takeAndSavePhoto(file2);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                    }
                }
            });
        } else {
            camera.takePicture(null, null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    try {
                        writePhoto(file, data);

                        flipCamera(1);

                        takeAndSavePhoto(file2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        return true;
    }

    private boolean takeAndSavePhoto(final File file) {
        List<String> supportedFocusModes = camera.getParameters().getSupportedFocusModes();
        boolean hasAutoFocus = supportedFocusModes != null && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO);
        if (hasAutoFocus) {
            camera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                }
            });
        }

        camera.takePicture(null, null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                try {
                    writePhoto(file, data);
                    camera.startPreview();

                    BitmapFactory.Options o2 = new BitmapFactory.Options();


                    Bitmap bBitmap = BitmapFactory.decodeFile(backPhotoFile.getAbsolutePath());
                    Bitmap fBitmap = BitmapFactory.decodeFile(frontPhotoFile.getAbsolutePath());
                    int h = bBitmap.getWidth();
                    int w = bBitmap.getWidth();


                    //bBitmap = rotateImage(getOrientationFromExif(backPhotoFile,0), bBitmap);
                    doPicture(w, h, bBitmap, fBitmap);

                    flipCamera(0);

                    backPhotoFile.delete();
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(backPhotoFile)));
                    Log.d(TAG, backPhotoFile.toString() + " was deleted");

                    frontPhotoFile.delete();
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(frontPhotoFile)));
                    Log.d(TAG, frontPhotoFile.toString() + " was deleted");


                    Intent intent = new Intent(MainActivity.this, GalleryActivity.class);
                    intent.putExtra("photo", outputFile.getAbsoluteFile().toString());
                    startActivity(intent);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return true;
    }


    private boolean writePhoto(File file, byte[] data) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        Log.d(TAG, file.toString());
        fos.write(data);
        fos.close();
        galleryAddPic(file);

        return true;
    }


    void setPreviewSize(boolean fullScreen) {

        // получаем размеры экрана
        Display display = getWindowManager().getDefaultDisplay();
        boolean widthIsMax = display.getWidth() > display.getHeight();

        // определяем размеры превью камеры
        Size size = camera.getParameters().getPreviewSize();

        RectF rectDisplay = new RectF();
        RectF rectPreview = new RectF();

        // RectF экрана, соотвествует размерам экрана
        rectDisplay.set(0, 0, display.getWidth(), display.getHeight());

        // RectF первью
        if (widthIsMax) {
            // превью в горизонтальной ориентации
            rectPreview.set(0, 0, size.width, size.height);
        } else {
            // превью в вертикальной ориентации
            rectPreview.set(0, 0, size.height, size.width);
        }

        Matrix matrix = new Matrix();
        // подготовка матрицы преобразования
        if (!fullScreen) {
            // если превью будет "втиснут" в экран (второй вариант из урока)
            matrix.setRectToRect(rectPreview, rectDisplay, Matrix.ScaleToFit.START);
        } else {
            // если экран будет "втиснут" в превью (третий вариант из урока)
            matrix.setRectToRect(rectDisplay, rectPreview, Matrix.ScaleToFit.START);
            matrix.invert(matrix);
        }
        // преобразование
        matrix.mapRect(rectPreview);

        // установка размеров surface из получившегося преобразования
        sv.getLayoutParams().height = (int) (rectPreview.bottom);
        sv.getLayoutParams().width = (int) (rectPreview.right);
    }

    public Bitmap rotateImage(Bitmap bitmapSrc, File imagePath) {
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

        return Bitmap.createBitmap(bitmapSrc, 0, 0, bitmapSrc.getWidth(), bitmapSrc.getHeight(), matrix, true);
    }


    boolean setCameraDisplayOrientation(int cameraId) {
        // определяем насколько повернут экран от нормального положения
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
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
            case 0:
                if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    Log.d(TAG, "1");
                    r = ((360 - degrees) + info.orientation);
                } else {
                    Log.d(TAG, "2");
                    r = ((360 - degrees) - info.orientation) + 180;
                }

                r = r % 360;
                break;
            case 90:
                if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    Log.d(TAG, "3");
                    r = ((360 - degrees) + info.orientation);
                } else {
                    Log.d(TAG, "4");
                    r = ((360 - degrees) - info.orientation) + 180;
                }

                r = r % 360;
                break;
            case 180:
                if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    Log.d(TAG, "5");
                    r = ((360 - degrees) + info.orientation);
                } else {
                    Log.d(TAG, "6");
                    r = ((360 - degrees) - info.orientation) + 180;
                }

                r = r % 360;
                break;
            case 270:
                if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    Log.d(TAG, "7");
                    r = ((360 - degrees) + info.orientation);
                } else {
                    Log.d(TAG, "8");
                    r = ((360 - degrees) - info.orientation) + 180;
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
        return true;
    }

    private void createDirectory() {
        directory = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                "MyFolder");
        if (!directory.exists())
            directory.mkdirs();
    }

    private File generateFileUri(int pos) {
        if (pos == 0)
            return new File(directory.getPath() + "/" + "back_" + "photo_" + System.currentTimeMillis() + ".jpg");
        else if (pos == 1)
            return new File(directory.getPath() + "/" + "front_" + "photo_" + System.currentTimeMillis() + ".jpg");
        else
            return new File(directory.getPath() + "/" + "con_" + "photo_" + System.currentTimeMillis() + ".jpg");
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

    private void galleryAddPic(File file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }


    private void updateFocus() {
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    camera.autoFocus(new Camera.AutoFocusCallback() {
                        @Override
                        public void onAutoFocus(boolean success, Camera camera) {
                            if (success) {
                                camera.stopPreview();
                                camera.startPreview();
                            }
                        }
                    });
                    handler.postDelayed(this, 5000);
                } catch (Exception e) {

                }
            }
        };
        handler.postDelayed(runnable, 1000);
    }


    public boolean doPicture(int reqWidth, int reqHeight, Bitmap backBitmap, Bitmap frontBitmap) {
        Bitmap concatedBitmap = Bitmap.createBitmap(reqWidth, reqHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(concatedBitmap);
        Paint paint = null;


        //frontBitmap = rotateImage(frontBitmap, frontPhotoFile);

        canvas.drawBitmap(backBitmap, 0, 0, paint);
        double correctHeight = reqHeight / 1.5;
        canvas.drawBitmap(getResizedBitmap(frontBitmap, reqWidth / 5, reqHeight / 5), reqWidth / 8, (int) correctHeight, paint);


        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        concatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos);
        byte[] bitmapData = bos.toByteArray();

        outputFile = generateFileUri(2);

        try {
            writePhoto(outputFile, bitmapData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;

    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();

        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }
}