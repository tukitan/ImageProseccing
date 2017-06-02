package com.example.komaki.a7segosr;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){

            }
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);

        }
        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){

            }
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }

        Button startButton = (Button)findViewById(R.id.start);
        Button configButton = (Button)findViewById(R.id.config);
        startButton.setOnClickListener(callCheckRecognize);
        configButton.setOnClickListener(callConfig);
    }
    @Override
    protected void onResume(){
        super.onResume();
        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            super.onManagerConnected(status);
            switch(status) {
                case LoaderCallbackInterface.SUCCESS:
                    Log.i("TAG", "OpenCV loaded.");
                    break;
            }
        }
    };

    private View.OnClickListener callCheckRecognize = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this,CheckRecognize.class);
            startActivity(intent);
        }
    };
    private View.OnClickListener callConfig = new View.OnClickListener() {
        @Override
        public void onClick(View v){
            Intent intent = new Intent(MainActivity.this,ConfigActivity.class);
            startActivity(intent);

        }
    };
}
