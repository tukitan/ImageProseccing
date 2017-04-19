package com.example.tukitan.takepicture;

import android.graphics.Bitmap;
import android.os.Environment;

import com.googlecode.tesseract.android.TessBaseAPI;

/**
 * Created by komaki on 17/04/19.
 */

public class RecognizeThread extends Thread{

    Bitmap output;
    public RecognizeThread(Bitmap output){
        this.output = output;
    }

    @Override
    public void run(){
        System.out.println("Recognition");

        output = output.copy(Bitmap.Config.ARGB_8888,true);

        TessBaseAPI tessBaseAPI = new TessBaseAPI();
        tessBaseAPI.init(Environment.getExternalStorageDirectory().getPath(),"eng");

        tessBaseAPI.setImage(output);
        String recognized = tessBaseAPI.getUTF8Text();
        System.out.println("END Recognition");
        //Toast.makeText(getContext(),recognized,Toast.LENGTH_SHORT).show();

        System.out.println(recognized);
        tessBaseAPI.end();

    }


}




