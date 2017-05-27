package com.example.komaki.a7segosr;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CheckRecognize extends AppCompatActivity {

    static {
        System.loadLibrary("opencv_java3");
    }

    @Override
    protected synchronized void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_recognize);

        Bitmap bitmap = loadBitmap();
        CVprocess cvObj = new CVprocess(bitmap);
        cvObj.start();
        //writeBitmap(cvObj.getNewBitmap(),"newBitmap.jpg");

    }

    private Bitmap loadBitmap(){
        Bitmap bitmap = null;
        BufferedInputStream bis = null;
        try{
            bis = new BufferedInputStream(new FileInputStream("/" + Environment.getExternalStorageDirectory() + "/Pictures/led2.bmp"));
            //ByteArrayOutputStream baos = new ByteArrayOutputStream();

            bitmap = BitmapFactory.decodeStream(bis);
            //bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    static void writeBitmap(Bitmap bmp,String filename) {
        String path = "/" + Environment.getExternalStorageDirectory() + "/7segOCRresult/" + filename;
        File file = new File(path);
        file.getParentFile().mkdir();
        FileOutputStream out = null;
        try{
            out = new FileOutputStream(path);
            bmp.compress(Bitmap.CompressFormat.JPEG,100,out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



}
