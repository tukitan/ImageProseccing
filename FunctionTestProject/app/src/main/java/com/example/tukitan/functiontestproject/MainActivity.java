package com.example.tukitan.functiontestproject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button page,share,print,cloud;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        page = (Button)findViewById(R.id.button);
        page.setOnClickListener(this);
        share = (Button)findViewById(R.id.button2);
        share.setOnClickListener(this);
        print = (Button)findViewById(R.id.printButton);
        print.setOnClickListener(this);
        cloud = (Button)findViewById(R.id.callCloud);
        cloud.setOnClickListener(this);

        context = getApplicationContext();
        //writeFile("document/testfile.txt");

    }

    @Override
    public void onClick(View v) {
        if(v == page){
            Intent intent = new Intent(MainActivity.this,PageTest.class);
            startActivity(intent);
        }
        if(v == share){
            /*
            ShareCompat.IntentBuilder builder = ShareCompat.IntentBuilder.from(MainActivity.this);
            builder.setChooserTitle("hoge");
            builder.startChooser();
            */
            File textPath = new File(getFilesDir(),"document");
            File file = new File(textPath, "testfile.txt");
            System.out.println(file.exists());
            Uri uri = FileProvider.getUriForFile(context,BuildConfig.APPLICATION_ID + ".fileprovider",file);
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setData(uri);
            shareIntent.setType("*/*");
            startActivity(shareIntent);
        }
        if(v == print){
            Intent intent = new Intent(MainActivity.this,PrintActivity.class);
            startActivity(intent);
        }
        if(v == cloud){
            Intent intent = new Intent(MainActivity.this,CloudActivity.class);
            startActivity(intent);
        }

    }

    private void writeFile(String file){
        File parent = new File(context.getFilesDir().getPath() + "/document");
        parent.mkdir();
        File textFile = new File(file);
        FileOutputStream fos;
        try{
            fos = new FileOutputStream(context.getFilesDir().getPath() + "/document/" + file);
            fos.write("this is test".getBytes());

            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
