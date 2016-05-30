package com.example.dmitry.twocamers.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.dmitry.twocamers.R;
import com.example.dmitry.twocamers.control.Controler;
import com.example.dmitry.twocamers.view.CanV;

import java.io.File;

public class EditPhotoActivity extends Activity implements View.OnClickListener {

    private Button btnSave;
    private Button btnPost;
    private CanV canV;
    ProgressBar progressBar;
    Controler controler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_edit_photo);

        Intent intent = getIntent();
        boolean b = false;
        boolean or = intent.getBooleanExtra("orientation",b);

        if(or)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        File back = new File(intent.getStringExtra("bFile"));
        File front = new File(intent.getStringExtra("fFile"));


        canV = (CanV) findViewById(R.id.canvas);
        btnSave = (Button) findViewById(R.id.button);
        btnPost = (Button) findViewById(R.id.button2);
        btnSave.setOnClickListener(this);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);


        controler = new Controler();
        canV.setControler(controler);
        canV.initBitmaps(back, front);
        canV.initSmallPicture(controler.getFrontBitmap());


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        canV.makeThePicture(progressBar);

    }


}
