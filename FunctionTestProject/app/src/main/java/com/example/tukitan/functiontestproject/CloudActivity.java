package com.example.tukitan.functiontestproject;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;

public class CloudActivity extends AppCompatActivity implements View.OnClickListener{

    final static String APP_KEY = "oj0b2ppht009bos";
    final static String APP_SECRET = "xily7pexk630v3a";
    final static Session.AccessType ACCESS_TYPE = Session.AccessType.APP_FOLDER;
    DropboxAPI<AndroidAuthSession> mApi;
    Button upload,download;


    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_cloud);

        AndroidAuthSession session = buildSession();
        mApi = new DropboxAPI<AndroidAuthSession>(session);
        mApi.getSession().startAuthentication(CloudActivity.this);

        upload = (Button)findViewById(R.id.upload);
        upload.setOnClickListener(this);
        download = (Button)findViewById(R.id.download);
        download.setOnClickListener(this);

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

    @Override
    public void onClick(View v) {
        if(v == upload){
            (new MyAsync(0,mApi)).execute();
            Toast.makeText(this,"UPLOAD Successfull",Toast.LENGTH_SHORT).show();
        }
        if(v == download){
            (new MyAsync(1,mApi)).execute();
            Toast.makeText(this,"DOWNLOAD Successfull",Toast.LENGTH_SHORT).show();
        }

    }

    class NetworkIOThread implements Runnable{
        int type;

        //type 1:UPLOAD  type 2:DOWNLOAD
        private NetworkIOThread(int type){
            this.type = type;
        }
        @Override
        public void run() {
            try {
                switch (type) {
                    case 0:
                        //upload file
                        Date now = new Date();
                        InputStream in = new ByteArrayInputStream(now.toString().getBytes());
                        String filename = "NowTime.txt";
                        DropboxAPI.Entry entry = mApi.putFile(filename, in, now.toString().getBytes().length, null, null);

                        break;
                }
            } catch (DropboxException e){
                e.printStackTrace();
            }
        }
    }
}
