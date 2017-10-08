package com.example.komaki.a7segosr;

import android.content.Context;
import android.content.SharedPreferences;

import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

/**
 * Created by tukitan on 17/10/01.
 */

public class DropboxUtils {

    public final static String APPKEY = "y8w3i2f4ok163zw";
    public final static String APPSECRET = "41vn52lv1hir3gn";
    public final static String TOKEN = "w7IZzK4a54AAAAAAAAAAF0dYh1PMhUxF_G_v19QDPOBZyPckznxk1np6ylsnOJwk";
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

    public static DbxClientV2 getClient(String ACCESS_TOKEN){
        DbxRequestConfig config = new DbxRequestConfig("dropbox/example","en_JP");
        DbxClientV2 client = new DbxClientV2(config,ACCESS_TOKEN);
        return client;
    }
}
