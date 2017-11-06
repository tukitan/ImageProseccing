package com.example.komaki.a7segosr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public class DropboxLauncher extends AppCompatActivity {

    Button upConfig,downConfig,updata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dropbox_launcher);
        upConfig = (Button) findViewById(R.id.upConfig);
        downConfig = (Button) findViewById(R.id.downConfig);
        updata = (Button) findViewById(R.id.upData);
    }
}
