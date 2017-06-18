package com.example.tukitan.functiontestproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button page;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        page = (Button)findViewById(R.id.button);
        page.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v == page){
            Intent intent = new Intent(MainActivity.this,PageTest.class);
            startActivity(intent);
        }

    }
}
