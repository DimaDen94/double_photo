package com.example.dmitry.twocamers;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.File;

public class GalleryActivity extends AppCompatActivity {


    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        String file = getIntent().getStringExtra("photo");
        File pictureFile = new File(file) ;
        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageURI(Uri.fromFile(pictureFile));
    }
}
