package com.example.komaki.a7segosr;

import android.content.Context;
import android.content.SharedPreferences;

import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;

/**
 * Created by tukitan on 17/10/01.
 */

public class DropboxUtils {

    public final static String APPKEY = "b60d39boydv24cy";
    public final static String APPSECRET = "igghhnxnxej5tv4";
    private final static String TOKEN = "y3Mzqinj1oQAAAAAAAADegJMDlA4Z1-jZXPxvOae-g8BT_1qigWZAFMQwL5F6KnX";
    String PREF_NAME = "dropbox";

    private Context context;


    public DropboxUtils(Context context){
        this.context = context;
    }

    public void storeOauth2AccessToken(String secret){
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(TOKEN,secret);
        editor.commit();

    }

    public AndroidAuthSession loadAndroidAuthSession(){
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        String token = preferences.getString(TOKEN,null);
        if(token != null){
            AppKeyPair pair = new AppKeyPair(APPKEY,APPSECRET);
            return new AndroidAuthSession(pair,token);
        } else {
            return null;
        }
    }

    public boolean hasLoadAndroidAuthSession(){
        return loadAndroidAuthSession() != null;
    }
}
