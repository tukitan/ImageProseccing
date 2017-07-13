package com.example.komaki.a7segosr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ConfigActivity2 extends AppCompatActivity  implements View.OnClickListener{

    CheckBox smallBox,bigBox;
    Button kettei;
    static boolean bigFlag,smallFlag;
    static String BIGSIZE = "25";
    static String SMALLSIZE = "11";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config2);
        smallBox = (CheckBox)findViewById(R.id.small);
        bigBox = (CheckBox)findViewById(R.id.big);
        kettei = (Button)findViewById(R.id.kettei);
        smallBox.setOnClickListener(this);
        bigBox.setOnClickListener(this);
        kettei.setOnClickListener(this);
        bigFlag = false;
        smallFlag = true;
        smallBox.setChecked(smallFlag);
        bigBox.setChecked(bigFlag);

    }
    @Override
    public void onClick(View v) {
        if(v == smallBox) {
            if(smallBox.isChecked()){
                smallFlag = true;
                bigFlag = false;
                bigBox.setChecked(false);
            } else {
                smallFlag = false;
                bigFlag = true;
                bigBox.setChecked(true);
            }
        }
        if(v == bigBox){
            if(bigBox.isChecked()){
                bigFlag = true;
                smallFlag = false;
                smallBox.setChecked(false);
            }else{
                bigFlag = false;
                smallFlag = true;
                smallBox.setChecked(true);
            }
        }
        if(v == kettei){
            writeConfigFile();
            Intent intent = new Intent(ConfigActivity2.this,MainActivity.class);
            startActivity(intent);

        }
    }

    private void writeConfigFile(){
        String filename = "initalize.txt";
        try {
            FileOutputStream fos = openFileOutput(filename,MODE_PRIVATE);
            if(bigFlag) fos.write(BIGSIZE.getBytes());
            else if (smallFlag) fos.write(SMALLSIZE.getBytes());
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
