package com.example.tukitan.takepicture;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;

import com.googlecode.tesseract.android.TessBaseAPI;

/**
 * Created by komaki on 17/04/19.
 */

public class RecognizeThread extends Thread{

    Bitmap output;
    Handler handler;
    Context context;
    public RecognizeThread(Bitmap output,Handler handler,Context context){
        this.output = output;
        this.handler = handler;
        this.context = context;
    }

    @Override
    public void run(){
        System.out.println("Recognition");

        output = output.copy(Bitmap.Config.ARGB_8888,true);
        String whiteList = ".0123456789";
        CVprocessing mCVprocessing = new CVprocessing(context);
        output = mCVprocessing.grayScale(output);

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


}




