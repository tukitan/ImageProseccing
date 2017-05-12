package com.example.tukitan.takepicture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.Image;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;

import static java.security.AccessController.getContext;

/**
 * Created by tukitan on 17/04/14.
 */

public class Recognition extends View{
    byte[] bytes;
    private Paint mPaint = new Paint();
    double Yup,Ydown,Xright,Xleft;

    static double EXPAND_X = 2620.0/1020.0;
    static double EXPAND_Y = 4656.0/1940.0;
    static int range = 600;
    /*
    @param 2620,4656:画像の右端、下端の座標
    @param 1060,1940:タップしたときの右端、下端の座標
    */

    //Input image and recognize this
    public Recognition(Context context, Image image,double x,double y) {
        super(context);
        x = x * EXPAND_X;
        y = y * EXPAND_Y;
        Yup = (y-range>=0) ? y-range : 0;
        Ydown = (y+range<4656) ? y+range : 4656;
        Xright = (x+range<2620) ? x+range : 2620;
        Xleft = (x-range>=0) ? x-range : 0;
        System.out.println("left:"+Xleft+",up:"+Yup+",rigth:"+Xright+",down:"+Ydown);

        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        bytes = new byte[buffer.capacity()];
        buffer.get(bytes);
    }


    protected void recognize() throws Exception{
        Bitmap output = null;
        Bitmap rotatedBitmap = null;
        byte[] rotatedByte;
        CVprocessing mCVprocessing = new CVprocessing();
        if(bytes != null){
            output = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            rotatedByte = rotationBitmap(output);
            rotatedBitmap = rectCut(rotatedByte);
        }
        //canvas.drawBitmap(output,0,0,mPaint);
        System.out.println("Recognition");

        rotatedBitmap = rotatedBitmap.copy(Bitmap.Config.ARGB_8888,true);

        String whiteList = ".0123456789";

        rotatedBitmap = mCVprocessing.grayScale(rotatedBitmap);

        TessBaseAPI tessBaseAPI = new TessBaseAPI();
        tessBaseAPI.init(Environment.getExternalStorageDirectory().getPath(),"led");

        tessBaseAPI.setImage(rotatedBitmap);
        tessBaseAPI.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST,whiteList);
        String recognized = tessBaseAPI.getUTF8Text();
        writeFile(recognized);
        System.out.println("END Recognition");
        //Toast.makeText(getContext(),recognized,Toast.LENGTH_SHORT).show();


        System.out.println(recognized);
        tessBaseAPI.end();

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

    private Bitmap rectCut(byte[] bytes){
        Bitmap bitmap = null;
        try {
            BitmapRegionDecoder regionDecoder = BitmapRegionDecoder.newInstance(bytes,0,bytes.length,false);
            Rect rect = new Rect((int)Xleft,(int)Yup,(int)Xright,(int)Ydown);
            bitmap = regionDecoder.decodeRegion(rect,null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] savedBytes = baos.toByteArray();
        saveImage(savedBytes);

        return bitmap;

    }

    private byte[] rotationBitmap(Bitmap bitmap){
        int imageWidth = bitmap.getWidth();
        int imageHeight = bitmap.getHeight();
        Bitmap result;

        Matrix matrix = new Matrix();

        matrix.setRotate(90,imageWidth/2,imageHeight/2);

        result = Bitmap.createBitmap(bitmap,0,0,imageWidth,imageHeight,matrix,true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        result.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] bytes = baos.toByteArray();
        System.out.println("MAXwidth:" + result.getWidth() + ",MAXheight:" + result.getHeight());

        return bytes;
    }

    private void writeFile(String resultData){
        String filePath = Environment.getExternalStorageDirectory() + "/ocrResult/ocrByCamera.txt";
        File file = new File(filePath);
        file.getParentFile().mkdir();
        try{
            FileOutputStream fos = new FileOutputStream(filePath);
            OutputStreamWriter osw = new OutputStreamWriter(fos,"UTF-8");
            BufferedWriter bw = new BufferedWriter(osw);
            bw.write(resultData);
            bw.flush();
            bw.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
