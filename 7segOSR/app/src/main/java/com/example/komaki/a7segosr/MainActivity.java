package com.example.komaki.a7segosr;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("opencv_java3");
    }

    ArrayList<Double> configValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){

            }
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.CAMERA},0);

        }

        ImageButton debug = (ImageButton)findViewById(R.id.debugButton);
        ImageButton configButton = (ImageButton)findViewById(R.id.config);
        //ImageButton howtoButton = (ImageButton) findViewById(R.id.howto);
        ImageButton cameraButton = (ImageButton)findViewById(R.id.cameraTest);
        debug.setOnClickListener(callCheckRecognize);
        configButton.setOnClickListener(callConfig);
        cameraButton.setOnClickListener(callCamera);
        //howtoButton.setOnClickListener(callHowto);
        configValues = initConfig();
        for(Double elem :configValues) System.out.println(elem);
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
            /*
            double tmp = configValues.get(0);
            CVprocess.KSIZE = (int)tmp;
            double tmpOffset = configValues.get(4);
            Charactor.OFFSET = (int) tmpOffset;
            Intent intent = new Intent(MainActivity.this,CheckRecognize.class);
            startActivity(intent);
            */
            Intent intent = new Intent(MainActivity.this,ListPrintActivity.class);
            startActivity(intent);
        }
    };
    private View.OnClickListener callConfig = new View.OnClickListener() {
        @Override
        public void onClick(View v){
            ConfigActivity2.PERIOD = configValues.get(1).toString();

            Intent intent = new Intent(MainActivity.this,ConfigActivity2.class);
            startActivity(intent);

        }
    };
    private View.OnClickListener callCamera = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            double tmpKsize = configValues.get(0);
            double tmpPeriod = configValues.get(1);
            double tmpLang = configValues.get(2);
            double tmpUnit = configValues.get(3);
            double tmpOffset = configValues.get(4);

            CVprocess.KSIZE = (int)tmpKsize;
            CameraActivity.CHAR_SIZE = (int)tmpKsize;
            if(tmpLang == 0) CameraActivity.LOCALE = Locale.JAPANESE;
            else if(tmpLang == 1) CameraActivity.LOCALE = Locale.ENGLISH;
            //System.out.println(CVprocess.KSIZE);
            CameraActivity.PERIOD = tmpPeriod;
            CameraActivity.UNIT = tmpUnit;
            Charactor.OFFSET = (int) tmpOffset;
            Intent intent = new Intent(MainActivity.this,CameraActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    };
    private View.OnClickListener callHowto = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this,HowtoUse.class);
            startActivity(intent);
        }
    };

    private ArrayList<Double> initConfig(){
        ArrayList<Double> res = new ArrayList<>();
        String filename = "initalize.txt";
        double[] datas = new double[]{13,4,0,0,2};
        File file = new File(getFilesDir().getPath() +"/"+ filename);

        System.out.println("PATH:" + this.getFilesDir().getPath() + "/" + filename + " "+ file.exists());
        if(!file.exists()){
            try {
                FileOutputStream fos = openFileOutput(filename,MODE_PRIVATE);
                for(double elem: datas){
                    res.add(elem);
                    fos.write(String.valueOf(elem).getBytes());
                    fos.write("\n".getBytes());
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String tmp;
                while((tmp = br.readLine())!= null) {
                    res.add(Double.parseDouble(tmp));
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return res;
    }
}
