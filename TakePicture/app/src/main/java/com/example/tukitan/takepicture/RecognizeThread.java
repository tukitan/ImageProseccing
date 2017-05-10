package com.example.tukitan.takepicture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by komaki on 17/04/19.
 */

public class RecognizeThread extends Thread{

    Bitmap output;
    Handler handler;
    Context context;
    ByteArrayOutputStream baos;
    byte[] bytes;
    public RecognizeThread(Bitmap output,Handler handler,Context context){
        this.output = output;
        this.handler = handler;
        this.context = context;
        baos = new ByteArrayOutputStream();
    }

    @Override
    public void run(){
        System.out.println("Recognition");

        output = output.copy(Bitmap.Config.ARGB_8888,true);
        String whiteList = ".0123456789";
        CVprocessing mCVprocessing = new CVprocessing(context);
        output = mCVprocessing.grayScale(output);


        output = mCVprocessing.binaly(output);
        output.compress(Bitmap.CompressFormat.JPEG,100,baos);
        bytes = baos.toByteArray();
        saveImage(bytes);

        TessBaseAPI tessBaseAPI = new TessBaseAPI();
        tessBaseAPI.init(Environment.getExternalStorageDirectory().getPath(),"eng");
        tessBaseAPI.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST,whiteList);

        tessBaseAPI.setImage(output);

        final String recognized = tessBaseAPI.getUTF8Text();
        System.out.println("END Recognition");

        System.out.println(recognized);
        handler.post(new Runnable() {
            @Override
            public void run() {
                MainActivity.result.setText(recognized);
            }
        });

        tessBaseAPI.end();

    }

    private void saveImage(byte[] data){
        OutputStream out = null;
        try{
            out = new FileOutputStream(Environment.getExternalStorageDirectory().getPath()+"/RecognitionImage.jpg");
            out.write(data);
            out.close();
            System.out.println("Finished Save image");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}




