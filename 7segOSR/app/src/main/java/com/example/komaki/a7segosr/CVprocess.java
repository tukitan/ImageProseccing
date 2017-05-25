package com.example.komaki.a7segosr;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.opencv.android.*;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import static org.opencv.android.Utils.*;

public class CVprocess {

    Bitmap myBitmap;
    ExByte[][] exBytes;
    int HEADER = 54;
    int BITMAP_X_SIZE;
    int BITMAP_Y_SIZE;
    double THRESHOLD = 60.0;
    int BYTESIZE;
    int PIXEL;
    Bitmap newBitmap;
    public CVprocess(Bitmap bitmap){
        myBitmap = bitmap;
    }

    public void run(){
        grayScale();
        binaly();
        byte[] bytes = getByteArray();
        System.out.println("length:" + bytes.length);
        //for(byte elem :bytes) System.out.println(elem);
        //for(int i)
        labeling();

        Bitmap tmpBitmap = Bitmap.createBitmap(myBitmap);
        newBitmap = tmpBitmap.copy(Bitmap.Config.ARGB_8888,true);
        int[] pixels =returnPixels();
        newBitmap.setPixels(pixels,0,BITMAP_X_SIZE,0,0,BITMAP_X_SIZE,BITMAP_Y_SIZE);

    }
    public Bitmap getMyBitmap(){
        return myBitmap;
    }
    public Bitmap getNewBitmap(){ return newBitmap;}


        private void grayScale(){
        Mat gray = new Mat();
        Mat origin = new Mat();
        bitmapToMat(myBitmap,origin);
        Imgproc.cvtColor(origin,gray,Imgproc.COLOR_RGB2GRAY);
        matToBitmap(gray,myBitmap);
    }


    private void binaly(){
        Mat bin = new Mat();
        Mat origin = new Mat();
        bitmapToMat(myBitmap,origin);
        Imgproc.threshold(origin,bin,THRESHOLD,255, Imgproc.THRESH_BINARY);
        matToBitmap(bin,myBitmap);

    }

    private byte[] getByteArray(){
        int bytesize = myBitmap.getByteCount();
        ByteBuffer buffer = ByteBuffer.allocate(bytesize);
        myBitmap.copyPixelsToBuffer(buffer);
        return buffer.array();
    }

    private void labeling(){
        Mat src = new Mat();
        bitmapToMat(myBitmap,src);

        BITMAP_X_SIZE = myBitmap.getWidth();
        BITMAP_Y_SIZE = myBitmap.getHeight();
        System.out.println("x:"+BITMAP_X_SIZE+",y:"+BITMAP_Y_SIZE+",size:"+BITMAP_X_SIZE*BITMAP_Y_SIZE);

        exBytes = new ExByte[BITMAP_Y_SIZE][BITMAP_X_SIZE];

        for(int i=0;i<BITMAP_Y_SIZE;i++){
            for(int j=0;j<BITMAP_X_SIZE;j++){
                exBytes[i][j] = new ExByte(myBitmap.getPixel(j,i));
            }
        }

        //Labeling Process
        for(int i=1;i<BITMAP_Y_SIZE-1;i++){
            for(int j=1;j<BITMAP_X_SIZE-1;j++){

            }
        }
    }
    private int[] returnPixels(){
        int[] res = new int[BITMAP_Y_SIZE*BITMAP_X_SIZE];
        for(int i=0;i<BITMAP_Y_SIZE;i++){
            for(int j=0;j<BITMAP_X_SIZE;j++){
                res[i*BITMAP_Y_SIZE + j] = exBytes[i][j].intDada;
            }
        }
        return res;
    }
}
