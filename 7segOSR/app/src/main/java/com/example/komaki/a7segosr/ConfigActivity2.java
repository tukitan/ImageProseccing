package com.example.komaki.a7segosr;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

public class ConfigActivity2 extends AppCompatActivity  implements View.OnClickListener,AdapterView.OnItemSelectedListener,SeekBar.OnSeekBarChangeListener{

    CheckBox smallBox,bigBox;
    Button kettei;
    Button autoGet;
    static boolean bigFlag,smallFlag;
    static String user = "山田太朗";

    String LANG="JAPANESE";
    String UNIT="ミリメートル";
    static String PERIOD;
    HashMap<String,String> lang_value;
    HashMap<String,String> unit_value;
    EditText period;
    SeekBar offset,sizeBar;
    EditText name;
    EditText collectNum;
    TextView sizeRect;


    int OFFSET = 12;
    int SIZE = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config2);
        kettei = (Button)findViewById(R.id.kettei);
        autoGet = (Button)findViewById(R.id.autoGetRotate);
        collectNum = (EditText)findViewById(R.id.collectNumber);
        Spinner langs = (Spinner)findViewById(R.id.lang);
        Spinner units = (Spinner)findViewById(R.id.unit);
        name = (EditText)findViewById(R.id.name);
        kettei.setOnClickListener(this);
        autoGet.setOnClickListener(this);
        sizeRect = (TextView)findViewById(R.id.sizeRect);
        bigFlag = false;
        smallFlag = true;

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

        offset = (SeekBar)findViewById(R.id.offset);
        offset.setOnSeekBarChangeListener(this);

        sizeBar = (SeekBar)findViewById(R.id.sizeBar);
        sizeBar.setOnSeekBarChangeListener(this);

        sizeRect.setHighlightColor(Color.RED);
        sizeRect.setWidth(SIZE * 2 * 4 * 2 * 2);
        sizeRect.setHeight((int) (SIZE * 2 * 4 * 2 *1.3));

        Intent intent = getIntent();
        if(intent.getStringExtra("mode").equals("autoGet")){
            String data = intent.getStringExtra("offset");
            OFFSET = Integer.parseInt(data);
            Toast.makeText(this,"offset = " + OFFSET,Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    public void onClick(View v) {
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
        if(v == autoGet){
            String collect = collectNum.getText().toString();

            CVprocess.KSIZE = (SIZE*2 -11 <13) ? 13 : ((int)SIZE * 2 - 11);
            CameraActivity.CHAR_SIZE = (int)SIZE*2;
            CameraActivity.LOCALE = Locale.JAPANESE;
            //System.out.println(CVprocess.KSIZE);
            CameraActivity.PERIOD = Double.parseDouble(PERIOD);
            CameraActivity.UNIT = Double.parseDouble(unit_value.get(UNIT));
            Charactor.OFFSET = -5;
            Charactor.RANGE = (int)SIZE/2;
            Intent intent = new Intent(ConfigActivity2.this,CameraActivity.class);
            intent.putExtra("mode","autoGet");
            intent.putExtra("collect",collect);
            startActivity(intent);
        }

    }

    private void writeConfigFile(){
        String filename = "initalize.txt";
        user = name.getText().toString();
        try {
            FileOutputStream fos = openFileOutput(filename,MODE_PRIVATE);
            fos.write(String.valueOf(SIZE).getBytes());
            fos.write("\n".getBytes());
            fos.write(PERIOD.getBytes());
            fos.write("\n".getBytes());
            fos.write(lang_value.get(LANG).getBytes());
            fos.write("\n".getBytes());
            fos.write(unit_value.get(UNIT).getBytes());
            fos.write("\n".getBytes());
            fos.write(String.valueOf(OFFSET -10).getBytes());
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

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(seekBar == offset) OFFSET = progress;
        if(seekBar == sizeBar) {
            SIZE = progress+10;
            //Log.d("ConfigActivity2","rect = " +SIZE*32 +"," + SIZE*20.8);
            sizeRect.setWidth(SIZE * 2 * 4 * 2 * 2);
            sizeRect.setHeight((int) (SIZE * 2 * 4 * 2 *1.3));
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
