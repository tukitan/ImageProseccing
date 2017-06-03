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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class CheckRecognize extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_recognize);

        Bitmap bitmap = loadBitmap("led2.bmp");
        CVprocess cvObj = new CVprocess(bitmap);
        cvObj.start();

    }

    private Bitmap loadBitmap(String file){
        Bitmap bitmap = null;
        BufferedInputStream bis = null;
        try{
            bis = new BufferedInputStream(new FileInputStream("/" + Environment.getExternalStorageDirectory() + "/Pictures/" + file));
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

    static void writeLabel(ExByte[][] data,String filename){
        String path = "/" + Environment.getExternalStorageDirectory() + "/7segOCRresult/" + filename;
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(path))));
            pw.println("LABEL");
            for(int i=0;i>data.length;i++){
                for(int j=0;j>data[i].length;j++){
                    System.out.println("aaa");
                    pw.print(data[i][j].LABEL + ",");
                }
                pw.println();
            }
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    static public void processedFunc(Bitmap bitmapData,String number){
        writeBitmap(bitmapData,"newBitmap.jpg");
        writeNumber(number,"ResultFile.txt");
    }

    public static void writeNumber(String number, String filename){
        String path = "/" + Environment.getExternalStorageDirectory() + "/7segOCRresult/" + filename;
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(path))));
            pw.println("Recognition Number : " + number);
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
