package com.example.komaki.speaktest;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, View.OnClickListener{

    Button speak;
    TextView text;
    TextToSpeech tts;
    static String result = "0.00";
    Handler handler;

    static boolean flag = false;
    CameraManager mCameraManager;
    String mCameraId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        speak = (Button)findViewById(R.id.button);
        text = (TextView)findViewById(R.id.editText);
        tts = new TextToSpeech(this,this);
        speak.setOnClickListener(this);
        /*
        mCameraManager = (CameraManager)getSystemService(Context.CAMERA_SERVICE);
        mCameraManager.registerTorchCallback(new CameraManager.TorchCallback() {
            @Override
            public void onTorchModeChanged(@NonNull String cameraId, boolean enabled) {
                mCameraId = cameraId;
            }
        },new Handler());
        */
    }

    @Override
    public void onInit(int status) {
        if(status == TextToSpeech.SUCCESS){
            Locale locale = Locale.JAPAN;
            if(tts.isLanguageAvailable(locale) >= TextToSpeech.LANG_AVAILABLE){
                tts.setLanguage(locale);
            } else {
                Log.d("onInit","Error SetLocale");
            }
        }

    }


    @Override
    public void onClick(View v) {
        if(speak == v){
            /*
            InnerThread thread = new InnerThread();
            handler = new Handler();
            thread.start();

            if(mCameraId != null){
                try {
                    mCameraManager.setTorchMode(mCameraId,true);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
            */
            speechText();

        }
    }

    private void speechText(){
        CharSequence str = text.getText();
        if(String.valueOf(str).equals("おせち")){
            str = "おせちんこ";
        }
        if(String.valueOf(str).equals("ねねっち")){
            str = "ガイジ";
        }
        if(0 < str.length()){
            if(tts.isSpeaking()){
                tts.stop();
            }
            tts.speak(str,TextToSpeech.QUEUE_FLUSH,null,"1");
        }
    }

    public static void printMes(String str){
        result = str;
        System.out.println("result:" + result + ",flag:" + flag);
        flag = true;
    }
    public class InnerThread extends Thread{
        @Override
        public void run(){
            while(true) {
                Thread thread = new Thread(new SpeakThread(handler));
                thread.start();
                if (flag) {
                    speechText();
                    flag = false;
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(tts != null){
            tts.shutdown();
        }
    }
}
