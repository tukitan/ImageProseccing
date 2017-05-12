package com.example.tukitan.takepicture;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.opencv.android.Utils.bitmapToMat;
import static org.opencv.android.Utils.matToBitmap;

public class CVprocessing {

    public CVprocessing(){

    }


    Bitmap grayScale(Bitmap bitmap){
        if(!MainActivity.initFlag){
            return null;
        }
        Mat mat = new Mat();
        Mat grayed = new Mat();
        bitmapToMat(bitmap,mat);
        Imgproc.cvtColor(mat,grayed,Imgproc.COLOR_RGB2GRAY);
        matToBitmap(grayed,bitmap);

        return bitmap;
    }

    Bitmap binaly(Bitmap bitmap){
        if(!MainActivity.initFlag){
            return null;
        }
        Mat mat = new Mat();
        Mat binal = new Mat();
        bitmapToMat(bitmap,mat);
        Imgproc.threshold(mat, binal, 50.0, 255.0, Imgproc.THRESH_BINARY);
        matToBitmap(binal,bitmap);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
        saveImage(baos.toByteArray());

        return bitmap;
    }

    private void saveImage(byte[] data){
        OutputStream output = null;
        try{
            output = new FileOutputStream(Environment.getExternalStorageDirectory().getPath()+"/RecognitionImage.jpg");
            output.write(data);
            output.close();
            System.out.println("Finished Save image");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
