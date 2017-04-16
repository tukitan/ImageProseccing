package com.example.tukitan.takepicture;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    Button startButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        TextView myText = (TextView)findViewById(R.id.text);
        startButton = (Button)findViewById(R.id.start);
        startButton.setOnClickListener(startMethod);
        myText.setText("Helloooooooo");
        initTraineddata();


    }
    private void initTraineddata(){
        try {
            String filePath = Environment.getExternalStorageDirectory() + "/tessdata/eng.traineddata";
            (new File(filePath)).getParentFile().mkdir();
            InputStream input = getResources().getAssets().open("eng.traineddata");
            FileOutputStream output = new FileOutputStream(filePath,true);
            byte[] buffer = new byte[1024];
            int length;
            while((length = input.read(buffer)) > 0) {
                output.write(buffer,0,length);
            }

            input.close();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private final View.OnClickListener startMethod = new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            Intent intentMain_Camera = new Intent(MainActivity.this,CameraActivity.class);
            Log.v("Intent","startActivity");
            startActivity(intentMain_Camera);
        }
    };

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }
}
