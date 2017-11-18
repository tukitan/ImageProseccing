package com.example.tukitan.functiontestproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.net.Uri;
import android.os.Environment;
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

    Button page,share,print,cloud,makeBitmap;
    Context context;
    Bitmap bitmap;
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
        makeBitmap = (Button) findViewById(R.id.makeBitmap);
        makeBitmap.setOnClickListener(this);

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
        if(v == makeBitmap){
            int MAX_X = 10;
            int MAX_Y = 5;
            int[] tmpArray = new int[MAX_X*MAX_Y];
            System.out.println("MAX_X*MAX_Y = " + MAX_X*MAX_Y + ", ");
            for(int i=0;i<MAX_Y;i++){
                for(int j=0;j<MAX_X;j++){
                    tmpArray[i*MAX_X +j] = 0;
                    System.out.println("no." + (i*MAX_X +j) +" array = " + tmpArray[i*MAX_X+j]);
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(tmpArray,MAX_X,MAX_Y, Bitmap.Config.ARGB_4444);
            writeBitmap(bitmap,"makedBitmap.bmp");
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
    static void writeBitmap(Bitmap bmp,String filename) {
        String path = "/" + Environment.getExternalStorageDirectory() + "/FunctionTestResult/" + filename;
        File file = new File(path);
        file.getParentFile().mkdir();
        FileOutputStream out = null;
        try{
            out = new FileOutputStream(path);
            if(bmp == null) System.err.println("bitmap is null");
            else bmp.compress(Bitmap.CompressFormat.JPEG,100,out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
