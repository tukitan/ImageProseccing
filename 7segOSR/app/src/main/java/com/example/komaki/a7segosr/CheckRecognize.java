package com.example.komaki.a7segosr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

public class CheckRecognize extends AppCompatActivity implements TextToSpeech.OnInitListener{
    Handler handler;
    static String number = "0.00";
    TextToSpeech tts;
    Bitmap bitmap;
    static boolean proceccFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_recognize);

        bitmap = loadBitmap("test2.bmp");
        handler = new Handler();
        tts = new TextToSpeech(this,this);
        (new CallCVprocess()).start();
    }

    private Bitmap loadBitmap(String file){
        Bitmap bitmap = null;
        BufferedInputStream bis = null;
        try{
            bis = new BufferedInputStream(new FileInputStream("/" + Environment.getExternalStorageDirectory() + "/Pictures/" + file));
            //ByteArrayOutputStream baos = new ByteArrayOutputStream();

            bitmap = BitmapFactory.decodeStream(bis);
            //bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return bitmap;
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    static public void processedFunc(Bitmap bitmapData,String number){
        writeBitmap(bitmapData,"newBitmap.jpg");
        writeNumber(number,"ResultFile.txt");
    }

    public static void writeNumber(String number, String filename){
        String path = "/" + Environment.getExternalStorageDirectory() + "/7segOCRresult/" + filename;
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(path))));
            pw.println("Recognition Number : " + number);
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onInit(int status) {
        if(TextToSpeech.SUCCESS == status){
            Locale locale = Locale.JAPANESE;
            if(tts.isLanguageAvailable(locale) >= TextToSpeech.LANG_AVAILABLE){
                tts.setLanguage(locale);
            } else{
                Log.d("CheckRecognize","Error SetLocale");
            }
        }
    }
    public void speechText(){
        if(0 < number.length()){
            if(tts.isSpeaking()){
                tts.stop();
            }
            tts.speak(number,TextToSpeech.QUEUE_FLUSH,null,"1");
        }
    }
    private class CallCVprocess extends Thread{
        @Override
        public void run(){
            while(true) {
                Thread thread = new Thread(new CVprocess(bitmap, true, handler));
                thread.start();
                if(proceccFlag){
                    proceccFlag = false;
                    speechText();
                }
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

    }

}
