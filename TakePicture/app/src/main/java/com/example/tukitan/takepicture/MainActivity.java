package com.example.tukitan.takepicture;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Manifest;

public class MainActivity extends AppCompatActivity {

    Button startButton,recognizeButton;
    static TextView result;
    static Handler handler;
    static boolean initFlag = false;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        TextView myText = (TextView)findViewById(R.id.text);

        if(checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE)){

            }

            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},0);

        }

    }
    private void mainMethod(){
        startButton = (Button)findViewById(R.id.start);
        startButton.setOnClickListener(startMethod);
        recognizeButton = (Button)findViewById(R.id.recognizePic);
        recognizeButton.setOnClickListener(recognizeFunc);
        result = (TextView)findViewById(R.id.result);

        initTraineddata();
    }

    private void initTraineddata(){
        try {
            String filePath = Environment.getExternalStorageDirectory().getPath() + "/tessdata/led.traineddata";
            (new File(filePath)).getParentFile().mkdir();
            InputStream input = getResources().getAssets().open("led.traineddata");
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
                bis = new BufferedInputStream(new FileInputStream("/"+Environment.getExternalStorageDirectory()+"/Pictures/led.png"));
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                bmp = BitmapFactory.decodeStream(bis);

                bmp.compress(Bitmap.CompressFormat.JPEG,100,baos);

                handler = new Handler();
                RecognizeThread thread = new RecognizeThread(bmp,handler,getApplicationContext());
                thread.start();


                /*
                Intent intent = new Intent(MainActivity.this,CVrecognition.class);
                intent.putExtra("BYTEARRAY",bytes);
                startActivity(intent);
                */
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onResume(){
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0,this,mLoaderCallback);
        //mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        super.onResume();


    }


    @Override
    protected void onPause(){
        super.onPause();
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status){
                case LoaderCallbackInterface.SUCCESS:
                    initFlag = true;
                    mainMethod();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };
}
