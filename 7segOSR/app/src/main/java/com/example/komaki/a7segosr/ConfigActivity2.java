package com.example.komaki.a7segosr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class ConfigActivity2 extends AppCompatActivity  implements View.OnClickListener,AdapterView.OnItemSelectedListener{

    CheckBox smallBox,bigBox;
    Button kettei;
    static boolean bigFlag,smallFlag;
    static String BIGSIZE = "37";
    static String SMALLSIZE = "13";

    String LANG="JAPANESE";
    String UNIT="ミリメートル";
    static String PERIOD;
    HashMap<String,String> lang_value;
    HashMap<String,String> unit_value;
    EditText period;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config2);
        smallBox = (CheckBox)findViewById(R.id.small);
        bigBox = (CheckBox)findViewById(R.id.big);
        kettei = (Button)findViewById(R.id.kettei);
        Spinner langs = (Spinner)findViewById(R.id.lang);
        Spinner units = (Spinner)findViewById(R.id.unit);
        smallBox.setOnClickListener(this);
        bigBox.setOnClickListener(this);
        kettei.setOnClickListener(this);
        bigFlag = false;
        smallFlag = true;
        smallBox.setChecked(smallFlag);
        bigBox.setChecked(bigFlag);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.langList,android.R.layout.select_dialog_item);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,R.array.unitList,android.R.layout.select_dialog_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        langs.setAdapter(adapter);
        units.setAdapter(adapter2);

        langs.setOnItemSelectedListener(this);
        units.setOnItemSelectedListener(this);
        lang_value = new HashMap<>();
        lang_value.put("JAPANESE","0");
        lang_value.put("ENGLISH","1");

        unit_value = new HashMap<>();
        unit_value.put("ミリメートル","0");
        unit_value.put("センチメートル","1");
        unit_value.put("メートル","2");
        unit_value.put("℃","3");

        period =(EditText)findViewById(R.id.period);
        period.setText(PERIOD);
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
            String getPeriod = period.getText().toString();
            if(getPeriod.equals("")) {
                Toast.makeText(this,"周期を入力して下さい",Toast.LENGTH_SHORT).show();
                return;
            } else if(Double.parseDouble(getPeriod) < 4) {
                Toast.makeText(this,"4秒以上を設定して下さい",Toast.LENGTH_SHORT).show();
                return;
            } else PERIOD = getPeriod;
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
            fos.write("\n".getBytes());
            fos.write(PERIOD.getBytes());
            fos.write("\n".getBytes());
            fos.write(lang_value.get(LANG).getBytes());
            fos.write("\n".getBytes());
            fos.write(unit_value.get(UNIT).getBytes());
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        System.out.println("selected Item :" + parent.getItemAtPosition(position));
        if(parent.getItemAtPosition(position).equals("日本語")) LANG = "JAPANESE";
        else if(parent.getItemAtPosition(position).equals("English")) LANG = "ENGLISH";
        else UNIT = (String)parent.getItemAtPosition(position);
        System.out.println(unit_value.get(parent.getItemAtPosition(position)));
        System.out.println(LANG);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
