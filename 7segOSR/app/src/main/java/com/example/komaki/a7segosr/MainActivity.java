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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

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
        /*
        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){

            }
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
        if(checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            if(shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)){

            }

            requestPermissions(new String[]{android.Manifest.permission.CAMERA},2);
        }*/

        Button startButton = (Button)findViewById(R.id.start);
        ImageButton configButton = (ImageButton)findViewById(R.id.config);
        ImageButton howtoButton = (ImageButton) findViewById(R.id.howto);
        Button cameraButton = (Button)findViewById(R.id.cameraTest);
        startButton.setOnClickListener(callCheckRecognize);
        configButton.setOnClickListener(callConfig);
        cameraButton.setOnClickListener(callCamera);
        howtoButton.setOnClickListener(callHowto);
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
            double tmp = configValues.get(0);
            CVprocess.KSIZE = (int)tmp;
            System.out.println(CVprocess.KSIZE);
            Intent intent = new Intent(MainActivity.this,CheckRecognize.class);
            startActivity(intent);
        }
    };
    private View.OnClickListener callConfig = new View.OnClickListener() {
        @Override
        public void onClick(View v){
            double tmp = configValues.get(0);

            ConfigActivity.tmpKSIZE = (int)tmp;

            Intent intent = new Intent(MainActivity.this,ConfigActivity2.class);
            startActivity(intent);

        }
    };
    private View.OnClickListener callCamera = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            double tmp = configValues.get(0);
            CVprocess.KSIZE = (int)tmp;
            System.out.println(CVprocess.KSIZE);
            Intent intent = new Intent(MainActivity.this,CameraActivity.class);
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

    /*
    private ArrayList<String> initConfig(){
        String filename = "initalize.txt";
        String str;
        FileInputStream in;
        ArrayList<String> consts = new ArrayList<>();
        try{
            in = openFileInput(filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(in,"UTF-8"));
            while((str = br.readLine()) != null){
                consts.add(str);
            }
            br.close();
        } catch (FileNotFoundException e) {
            try {
                (new File(filename)).createNewFile();
                OutputStream out = openFileOutput(filename, MODE_PRIVATE);
                PrintWriter pw = new PrintWriter(new OutputStreamWriter(out));
                pw.println("60.0");
                pw.println("31");
                consts.add("60.0");
                consts.add("31");

                pw.close();
            }catch (IOException e1){
                e1.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return consts;
    }
    */

    private ArrayList<Double> initConfig(){
        ArrayList<Double> res = new ArrayList<>();
        String filename = "initalize.txt";
        double[] datas = new double[]{9};
        File file = new File(filename);

        if(!file.exists()){
            try {
                FileOutputStream fos = openFileOutput(filename,MODE_PRIVATE);
                for(double elem: datas){
                    res.add(elem);
                    fos.write(String.valueOf(elem).getBytes());
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String tmp;
                while((tmp = br.readLine())!= null) res.add(Double.parseDouble(tmp));
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return res;
    }
}
