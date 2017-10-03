package com.example.komaki.a7segosr;

import android.os.AsyncTask;
import android.os.Environment;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

/**
 * Created by tukitan on 17/09/08.
 */

public class MyAsync extends AsyncTask<String ,Void ,String> {
    int type;
    DropboxAPI mApi;
    public MyAsync(int type,DropboxAPI mApi) {
        this.type = type;
        this.mApi = mApi;
    }
    @Override
    protected String doInBackground(String... params) {
        try {
            switch (type) {
                case 0:
                    //upload file
                    String filename = params[0];
                    InputStream in = new FileInputStream(new File(filename));

                    DropboxAPI.Entry entry = mApi.putFile(filename, in, Long.parseLong(params[1]), null, null);
                    in.close();


                    break;
                case 1:
                    //download file
                    String downloadFilename = "NowTime.txt";
                    File file = new File(Environment.getExternalStorageDirectory().getPath() +"/"+
                            downloadFilename);
                    FileOutputStream os = new FileOutputStream(file);
                    DropboxAPI.DropboxFileInfo dfi = mApi.getFile("/" + downloadFilename,null,os,null);


                    break;
            }
        } catch (DropboxException e){
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
