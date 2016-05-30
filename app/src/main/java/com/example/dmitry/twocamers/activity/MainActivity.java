package com.example.dmitry.twocamers.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.dmitry.twocamers.R;
import com.example.dmitry.twocamers.utils.CameraControler;
import com.example.dmitry.twocamers.utils.SDWorker;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainActivity extends Activity {
    private static final String TAG = "mActivityLogs";

    private SurfaceView sv;
    private SurfaceHolder holder;
    private HolderCallback holderCallback;
    private Camera camera;

    private File directory;

    private File backPhotoFile;
    private File frontPhotoFile;
    private File outputFile;

    final boolean FULL_SCREEN = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        directory = SDWorker.createDirectory();


        sv = (SurfaceView) findViewById(R.id.surfaceView);
        holder = sv.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        holderCallback = new HolderCallback();
        holder.addCallback(holderCallback);


    }

    @Override
    protected void onResume() {
        super.onResume();
        camera = CameraControler.getCameraInstance(0);
        setCameraDisplayOrientationAndParamsToCamera(0);
        //camera.setPreviewDisplay(holder);
        camera.startPreview();
        //updateFocus();
        setPreviewSize(FULL_SCREEN);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (camera != null)
            camera.release();
        camera = null;
    }

    public void onClickPicture(View view) {

        backPhotoFile = SDWorker.generateFileUri(directory, 0);
        frontPhotoFile = SDWorker.generateFileUri(directory, 1);
        takeAndSaveBackPhoto(backPhotoFile, frontPhotoFile);

    }

    private boolean takeAndSaveBackPhoto(final File file, final File file2) {

        List<String> supportedFocusModes = camera.getParameters().getSupportedFocusModes();
        boolean hasAutoFocus = supportedFocusModes != null && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO);
        if (hasAutoFocus) {
            camera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {

                    // если удалось сфокусироваться, делаем снимок
                    camera.takePicture(null, null, null, new Camera.PictureCallback() {
                        @Override
                        public void onPictureTaken(byte[] data, Camera camera) {
                            try {
                                SDWorker.writePhotoAndPutToGallery(file, data, MainActivity.this);
                                MainActivity.this.camera = CameraControler.flipCamera(camera, 1, holder);
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                Log.d(TAG, "flip");
                                setCameraDisplayOrientationAndParamsToCamera(1);
                                setPreviewSize(FULL_SCREEN);
                                takeAndSaveFrontPhotoAndDoPicture(file2);
                                Log.d(TAG, "take");
                            }

                        }
                    });

                }
            });
        } else {
            camera.takePicture(null, null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    try {
                        SDWorker.writePhotoAndPutToGallery(file, data, MainActivity.this);
                        MainActivity.this.camera = CameraControler.flipCamera(camera, 1, holder);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        Log.d(TAG, "flip");
                        setCameraDisplayOrientationAndParamsToCamera(1);
                        setPreviewSize(FULL_SCREEN);
                        takeAndSaveFrontPhotoAndDoPicture(file2);
                        Log.d(TAG, "take");
                    }
                }
            });
        }

        return true;
    }

    private boolean takeAndSaveFrontPhotoAndDoPicture(final File file) {

        //is auto focus
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
                    SDWorker.writePhotoAndPutToGallery(file, data, MainActivity.this);
                    //camera.startPreview();

                    Bitmap bBitmap = BitmapFactory.decodeFile(backPhotoFile.getAbsolutePath());
                    Log.d(TAG, bBitmap.getWidth() + "    " + bBitmap.getHeight());
                    Bitmap fBitmap = BitmapFactory.decodeFile(frontPhotoFile.getAbsolutePath());
                    Log.d(TAG, fBitmap.getWidth() + "    " + fBitmap.getHeight());


                    bBitmap = SDWorker.rotateImage(bBitmap, backPhotoFile);
                    fBitmap = SDWorker.rotateImage(fBitmap, frontPhotoFile);

                    SDWorker.writePhotoAndPutToGallery(backPhotoFile, bBitmap, MainActivity.this);
                    SDWorker.writePhotoAndPutToGallery(frontPhotoFile, fBitmap, MainActivity.this);

                    //start next activity

                    startEditor();


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
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

    private boolean setCameraDisplayOrientationAndParamsToCamera(int cameraId) {
        // получаем инфо по камере cameraId
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        // определяем насколько повернут экран от нормального положения
        int rotation = getWindowManager().getDefaultDisplay().getRotation();

        int cameraRotationOffset = info.orientation;

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

        int rotate;
        if (cameraId == 1) {
            rotate = (360 + cameraRotationOffset + degrees) % 360;
        } else {
            rotate = (360 + cameraRotationOffset - degrees) % 360;
        }

        Camera.Parameters parameters = camera.getParameters();
        parameters.setRotation(rotate);

        List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
        Camera.Size size = sizes.get(0);
        for (int i = 0; i < sizes.size(); i++) {
            if (sizes.get(i).width > size.width) {
                size = sizes.get(i);
                Log.d(TAG, sizes.get(i).width + " -----" + sizes.get(i).height);
            }
        }

        int w = size.width;
        int h = size.height;

        parameters.setPictureSize(w, h);

        parameters.set("jpeg-quality", 80);
        Log.d(TAG, rotate + " r");

        camera.setParameters(parameters);

        return true;
    }

    public void startEditor() {
        Intent intent = new Intent(this, EditPhotoActivity.class);
        intent.putExtra("orientation",getScreenOrientation());
        intent.putExtra("bFile", backPhotoFile.getAbsoluteFile().toString());
        intent.putExtra("fFile", frontPhotoFile.getAbsoluteFile().toString());

        startActivity(intent);
    }

    public boolean getScreenOrientation() {
        Display getOrient = getWindowManager().getDefaultDisplay();

        boolean orientation;
        if (getOrient.getWidth() < getOrient.getHeight()) {
            orientation = false;
        } else {
           orientation = true;
        }
        return orientation;
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
}
