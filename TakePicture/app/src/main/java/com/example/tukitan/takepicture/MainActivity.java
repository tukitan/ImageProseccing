package com.example.tukitan.takepicture;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    Button startButton,recognizeButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        TextView myText = (TextView)findViewById(R.id.text);
        startButton = (Button)findViewById(R.id.start);
        startButton.setOnClickListener(startMethod);
        recognizeButton = (Button)findViewById(R.id.recognizePic);
        recognizeButton.setOnClickListener(recognizeFunc);

        myText.setText("Helloooooooo");
        initTraineddata();


    }
    private void initTraineddata(){
        try {
            String filePath = Environment.getExternalStorageDirectory().getPath() + "/tessdata/eng.traineddata";
            (new File(filePath)).getParentFile().mkdir();
            InputStream input = getResources().getAssets().open("eng.traineddata");
            FileOutputStream output = new FileOutputStream(filePath,false);
            byte[] buffer = new byte[1024];
            int length;
            while((length = input.read(buffer)) > 0) {
                output.write(buffer,0,length);
            }
            Toast.makeText(MainActivity.this,"traineddata.",Toast.LENGTH_SHORT);

            input.close();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private final View.OnClickListener startMethod = new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            Intent intentMain_Camera = new Intent(MainActivity.this,CameraActivity.class);
            Log.v("Intent","startActivity");
            startActivity(intentMain_Camera);
        }
    };

    private final View.OnClickListener recognizeFunc = new View.OnClickListener() {
        Bitmap bmp;

        @Override
        public void onClick(View v) {
            BufferedInputStream bis = null;
            try{
                bis = new BufferedInputStream(new FileInputStream("/"+Environment.getExternalStorageDirectory()+"/ScreenShots/RecognitionImage.png"));

                bmp = BitmapFactory.decodeStream(bis);

                RecognizeThread thread = new RecognizeThread(bmp);
                thread.start();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


        }
    };

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }
}
