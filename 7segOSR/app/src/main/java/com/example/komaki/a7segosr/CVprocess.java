package com.example.komaki.a7segosr;


import android.graphics.Bitmap;

import org.opencv.android.*;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

import static org.opencv.android.Utils.*;

public class CVprocess {

    Bitmap myBitmap;
    int HEADER = 54;
    public CVprocess(Bitmap bitmap){
        myBitmap = bitmap;
    }

    public void run(){
        grayScale();
        binaly();
        byte[] bytes = getByteArray();
        for(byte elem :bytes) System.out.println(elem);
        //for(int i)
    }
    public Bitmap getMyBitmap(){
        return myBitmap;
    }

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
        Imgproc.threshold(origin,bin,60.0,255, Imgproc.THRESH_BINARY);
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

    }
}
