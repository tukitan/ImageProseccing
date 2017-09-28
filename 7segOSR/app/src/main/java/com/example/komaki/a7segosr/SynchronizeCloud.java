package com.example.komaki.a7segosr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SynchronizeCloud extends AppCompatActivity implements View.OnClickListener{
    Button uploadConfig,downloadConfig,uploadData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_synchronize_cloud);

        uploadConfig = (Button)findViewById(R.id.uploadConfig);
        downloadConfig = (Button)findViewById(R.id.downloadConfig);
        uploadData = (Button)findViewById(R.id.uploadData);


    }

    @Override
    public void onClick(View v) {
        if(v == uploadConfig){

        }
    }
}
