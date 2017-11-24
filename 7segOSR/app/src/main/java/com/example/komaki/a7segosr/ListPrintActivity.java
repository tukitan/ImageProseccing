package com.example.komaki.a7segosr;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ListPrintActivity extends AppCompatActivity implements View.OnClickListener{

    String sdPath = Environment.getExternalStorageDirectory().getPath();

    File[] files;
    ListView lv;
    List<String> filelist;

    private DropboxAPI<AndroidAuthSession> mDBAPI;
    private DropboxUtils dropboxUtils;
    ArrayAdapter<String> adapter;

    Button uploadConf,downloadConf,delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_print);
        filelist = new ArrayList<String>();

        files = new File(sdPath + "/7segOCRresult/RESULT/").listFiles();
        if(files != null){
            for(int i=0;i<files.length;i++){
                filelist.add(files[i].getName());
                System.out.println(files[i].getName());
            }
        }else {//nene
            Log.i("ListPrintActivity","files is null");
        }

        lv = (ListView) findViewById(R.id.listview);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,filelist);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listview = (ListView) parent;
                String item = (String) listview.getItemAtPosition(position);
                String filepath = sdPath + "/7segOCRresult/RESULT/" + item;
                Log.d("ListPrintActivity","item = "+item+", filepath = "+filepath);

                (new MyAsync(0,DropboxUtils.getClient(DropboxUtils.TOKEN),ListPrintActivity.this)).execute(filepath,item);

            }

        });

        dropboxUtils = new DropboxUtils(this);

        if(!dropboxUtils.hasLoadAndroidAuthSession()){
            AppKeyPair pair = new AppKeyPair(DropboxUtils.APPKEY,DropboxUtils.APPSECRET);
            AndroidAuthSession session = new AndroidAuthSession(pair);
            mDBAPI = new DropboxAPI<>(session);
            mDBAPI.getSession().startOAuth2Authentication(this);
        } else{
            mDBAPI = new DropboxAPI<>(dropboxUtils.loadAndroidAuthSession());
        }

        downloadConf = (Button)findViewById(R.id.downloadConfig);
        downloadConf.setOnClickListener(this);
        uploadConf = (Button)findViewById(R.id.uploadConfig);
        uploadConf.setOnClickListener(this);
        delete = (Button)findViewById(R.id.deleteAll);
        delete.setOnClickListener(this);


    }

    public void showItem(String str){
        Toast.makeText(this,"file:" +str,Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onResume(){
        super.onResume();
        if(mDBAPI.getSession().authenticationSuccessful()){
            try{
                mDBAPI.getSession().finishAuthentication();
                dropboxUtils.storeOauth2AccessToken(mDBAPI.getSession().getOAuth2AccessToken());
            }catch (IllegalStateException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {

        if(v == uploadConf){
            String filename = "initalize.txt";
            String item = getFilesDir().getPath() +"/"+ filename;
            (new MyAsync(0,DropboxUtils.getClient(DropboxUtils.TOKEN),ListPrintActivity.this)).execute(item,filename);

        }
        if(v == downloadConf){
            (new MyAsync(1,DropboxUtils.getClient(DropboxUtils.TOKEN),ListPrintActivity.this)).execute();
            System.out.println("DOWNLOAD UNTI");
        }
        if(v == delete){
            new AlertDialog.Builder(this)
                    .setTitle("確認")
                    .setMessage("測定データを全削除します。よろしいですか？")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            for(File elem: files){
                                elem.delete();
                            }
                            Intent intent = new Intent(ListPrintActivity.this,MainActivity.class);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("NG",null).show();

        }

    }

}
