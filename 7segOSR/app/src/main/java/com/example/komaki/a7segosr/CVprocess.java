package com.example.komaki.a7segosr;


import android.graphics.Bitmap;

import org.opencv.android.*;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import static org.opencv.android.Utils.*;

public class CVprocess {

    Bitmap myBitmap;
    public CVprocess(Bitmap bitmap){
        myBitmap = bitmap;
    }

    public void run(){
        grayScale();

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
}
