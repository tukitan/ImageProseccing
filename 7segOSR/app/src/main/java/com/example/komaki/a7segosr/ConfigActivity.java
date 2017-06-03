package com.example.komaki.a7segosr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class ConfigActivity extends AppCompatActivity {

    SeekBar binalyBar,ksizeBar;
    double tmpBinalyValue = 60.0;
    int tmpKSIZE = 31;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        Button save = (Button)findViewById(R.id.saveButton);
        Button test = (Button)findViewById(R.id.testButton);
        Button reset = (Button)findViewById(R.id.resetButton);
        binalyBar = (SeekBar)findViewById(R.id.binalyVal);
        ksizeBar = (SeekBar)findViewById(R.id.bokasiVal);
        SurfaceView sv = (SurfaceView)findViewById(R.id.surfaceView);

        save.setOnClickListener(calledSaveButton);
        binalyBar.setOnSeekBarChangeListener(calledBar);
        ksizeBar.setOnSeekBarChangeListener(calledBar);
    }

    View.OnClickListener calledSaveButton = new View.OnClickListener(){
        double binalyValue;
        int ksizeValue;

        @Override
        public void onClick(View v) {
            binalyValue = tmpBinalyValue;
            ksizeValue = tmpKSIZE;
            try {
                OutputStream out = openFileOutput("initalize.txt",MODE_PRIVATE);
                PrintWriter pw = new PrintWriter(new OutputStreamWriter(out));
                pw.println(binalyValue);
                pw.println(ksizeValue);
                pw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("Config","Saved Config.");


        }
    };
    SeekBar.OnSeekBarChangeListener calledBar = new SeekBar.OnSeekBarChangeListener(){
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(seekBar.equals(binalyBar)) {
                tmpBinalyValue = progress;
                System.out.println("changed binaly");
            }
            if(seekBar.equals(ksizeBar)){
                tmpKSIZE = progress;
                System.out.println("changed ksize");
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
}
