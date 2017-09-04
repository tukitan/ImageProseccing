package com.example.tukitan.functiontestproject;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session;

public class CloudActivity extends AppCompatActivity {

    final static String APP_KEY = "oj0b2ppht009bos";
    final static String APP_SECRET = "xily7pexk630v3a";
    final static Session.AccessType ACCESS_TYPE = Session.AccessType.APP_FOLDER;
    DropboxAPI<AndroidAuthSession> mApi;

    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);

        AndroidAuthSession session = buildSession();
        mApi = new DropboxAPI<AndroidAuthSession>(session);
        mApi.getSession().startAuthentication(this);

    }

    @Override
    protected void onResume(){
        super.onResume();
        AndroidAuthSession session = mApi.getSession();
        if(session.authenticationSuccessful()){
            try{
                session.finishAuthentication();
            }catch (IllegalStateException e){
                e.printStackTrace();
            }
        }
    }

    private AndroidAuthSession buildSession(){
        AppKeyPair pair = new AppKeyPair(APP_KEY,APP_SECRET);
        AndroidAuthSession session;
        session = new AndroidAuthSession(pair,ACCESS_TYPE);
        return session;

    }

}
