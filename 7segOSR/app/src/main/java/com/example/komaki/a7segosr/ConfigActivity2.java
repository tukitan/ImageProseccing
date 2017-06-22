package com.example.komaki.a7segosr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

public class ConfigActivity2 extends AppCompatActivity  implements View.OnClickListener{

    CheckBox smallBox,bigBox;
    static boolean bigFlag,smallFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config2);
        smallBox = (CheckBox)findViewById(R.id.small);
        bigBox = (CheckBox)findViewById(R.id.big);
        smallBox.setOnClickListener(this);
        bigBox.setOnClickListener(this);
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
            } else{
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
            }else {
                bigFlag = false;
                smallFlag = true;
                smallBox.setChecked(true);
            }
        }
    }

}
