package com.example.tukitan.functiontestproject;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.print.PrintHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class PrintActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);
        Resources r = getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(r,R.drawable.shana01);
        printImage("testImage",bitmap);
    }

    private void printImage(String file,Bitmap image){
        if(PrintHelper.systemSupportsPrint()){
            PrintHelper printHelper = new PrintHelper(this);
            printHelper.setColorMode(PrintHelper.COLOR_MODE_MONOCHROME);
            printHelper.setScaleMode(PrintHelper.SCALE_MODE_FIT);
            printHelper.printBitmap(file,image);


        } else {
            Toast.makeText(this,"not available.",Toast.LENGTH_LONG).show();
        }
    }
}
