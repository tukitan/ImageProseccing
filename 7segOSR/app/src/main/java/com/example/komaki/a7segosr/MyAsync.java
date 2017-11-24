package com.example.komaki.a7segosr;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.WriteMode;

import java.io.BufferedInputStream;
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

public class MyAsync extends AsyncTask {
    int type;
    DbxClientV2 dbxClient;
    Context context;

    public MyAsync(int type, DbxClientV2 dbxClient, Context context) {
        this.type = type;
        this.dbxClient = dbxClient;
        this.context = context;

    }

    @Override
    protected Object doInBackground(Object[] params) {
        try {
            switch (type) {
                case 0:
                    //upload file
                    String filename = String.valueOf(params[0]);
                    File file = new File(filename);
                    InputStream in = new FileInputStream(file);
                    System.out.println("filepath :" + filename + ", fileSize :" + file.length());
                        dbxClient.files().uploadBuilder("/" + params[1])
                                .withMode(WriteMode.OVERWRITE)
                                .uploadAndFinish(in,1000);
                    Log.d("Upload File","Upload Success");
                    in.close();
                    break;

                case 1:
                    //download file
                    String downloadFilename = "initalize.txt";
                    File downloadFile = new File(context.getFilesDir().getPath() +"/"+
                            downloadFilename);
                    FileOutputStream os = new FileOutputStream(downloadFile);
                    dbxClient.files().download("/" + downloadFilename)
                            .download(os);
                    break;

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DbxException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(Object o){
        super.onPostExecute(o);
        Toast.makeText(context,"ACTION FINISHED.",Toast.LENGTH_SHORT).show();

    }
}
