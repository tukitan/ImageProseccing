package com.example.komaki.a7segosr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.Image;
import android.os.Environment;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.opencv.android.Utils.bitmapToMat;
import static org.opencv.android.Utils.matToBitmap;

/**
 * Created by tukitan on 17/07/30.
 */

public class FirstRecognition implements Runnable{
    Bitmap target;

    public FirstRecognition(Image image){
        // Called by CameraActivity
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.capacity()];
        buffer.get(bytes);
        Bitmap tmpBitmap  = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        int imageWidth = tmpBitmap.getWidth();
        int imageHeight = tmpBitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(90,imageWidth/2,imageHeight/2);

        Bitmap myBitmap = Bitmap.createBitmap(tmpBitmap,0,0,imageWidth,imageHeight,matrix,true);
        target = myBitmap.copy(Bitmap.Config.ARGB_8888,true);
    }

    public FirstRecognition(Bitmap bitmap){
        target = bitmap.copy(Bitmap.Config.ARGB_8888,true);
    }

    @Override
    public void run() {
        binaly();
        blurBitmap(CVprocess.KSIZE);



        System.out.println("Finish FirstRecognition Thread process.");
        writeBitmap(target,"TestFirstRecog.bmp");
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void binaly(){
        Mat bin = new Mat();
        Mat gray = new Mat();
        Mat origin = new Mat();
        bitmapToMat(target,origin);
        Imgproc.cvtColor(origin,gray,Imgproc.COLOR_RGB2GRAY);
        Imgproc.threshold(gray,bin,0.0,255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
        matToBitmap(bin,target);
    }

    private void blurBitmap(int ksize){
        Mat src = new Mat();
        Mat dst = new Mat();
        bitmapToMat(target,src);
        Imgproc.medianBlur(src,dst,ksize);
        matToBitmap(dst,target);
    }
}
