package com.example.komaki.a7segosr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.TextureView;
import android.widget.Button;
import android.widget.SeekBar;

public class ConfigActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        Button save = (Button)findViewById(R.id.saveButton);
        Button test = (Button)findViewById(R.id.testButton);
        Button reset = (Button)findViewById(R.id.resetButton);
        SeekBar binalyBar = (SeekBar)findViewById(R.id.binalyVal);
        SeekBar ksizeBar = (SeekBar)findViewById(R.id.bokasiVal);
        SurfaceView sv = (SurfaceView)findViewById(R.id.surfaceView);

    }
}
