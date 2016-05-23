package com.example.dmitry.twocamers.activity;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.dmitry.twocamers.R;
import com.example.dmitry.twocamers.utils.PostToServer;

import java.io.File;

public class GalleryActivity extends AppCompatActivity {

    File pictureFile;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        String file = getIntent().getStringExtra("photo");
        pictureFile = new File(file) ;
        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageURI(Uri.fromFile(pictureFile));
    }


    public void clickToServer(View view){
        PostToServer.publicPhoto(this,pictureFile,"");
    }
}
