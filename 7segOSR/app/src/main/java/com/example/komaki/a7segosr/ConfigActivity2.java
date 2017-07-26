package com.example.komaki.a7segosr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ConfigActivity2 extends AppCompatActivity  implements View.OnClickListener,AdapterView.OnItemSelectedListener{

    CheckBox smallBox,bigBox;
    Button kettei;
    static boolean bigFlag,smallFlag;
    static String BIGSIZE = "37";
    static String SMALLSIZE = "13";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config2);
        smallBox = (CheckBox)findViewById(R.id.small);
        bigBox = (CheckBox)findViewById(R.id.big);
        kettei = (Button)findViewById(R.id.kettei);
        Spinner langs = (Spinner)findViewById(R.id.lang);
        smallBox.setOnClickListener(this);
        bigBox.setOnClickListener(this);
        kettei.setOnClickListener(this);
        bigFlag = false;
        smallFlag = true;
        smallBox.setChecked(smallFlag);
        bigBox.setChecked(bigFlag);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.langList,android.R.layout.select_dialog_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        langs.setAdapter(adapter);
        langs.setOnItemSelectedListener(this);
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        System.out.println("selected Item position:" + position);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
