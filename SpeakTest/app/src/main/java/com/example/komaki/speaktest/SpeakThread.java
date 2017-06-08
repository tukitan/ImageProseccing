package com.example.komaki.speaktest;

import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

/**
 * Created by komaki on 17/06/08.
 */

public class SpeakThread implements Runnable{
    Handler handler;

    public SpeakThread(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        Log.d("SpeakThread","thread run");
        handler.post(new Runnable() {
            @Override
            public void run() {
                MainActivity.printMes("テスト");
            }
        });
    }

}
