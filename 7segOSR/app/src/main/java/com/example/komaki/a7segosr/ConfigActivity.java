package com.example.komaki.a7segosr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class ConfigActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener {

    SeekBar binalyBar,ksizeBar;
    double tmpBinalyValue = 60.0;
    int tmpKSIZE = 31;

    private CameraBridgeViewBase mCameraView;
    private Mat previewPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        Button save = (Button)findViewById(R.id.saveButton);
        Button back = (Button)findViewById(R.id.backButton);
        binalyBar = (SeekBar)findViewById(R.id.binalyVal);
        ksizeBar = (SeekBar)findViewById(R.id.bokasiVal);
        mCameraView = (CameraBridgeViewBase) findViewById(R.id.surfaceView);

        save.setOnClickListener(calledSaveButton);
        back.setOnClickListener(calledBackButton);

        binalyBar.setOnSeekBarChangeListener(calledBar);
        ksizeBar.setOnSeekBarChangeListener(calledBar);
        mCameraView.setCvCameraViewListener(this);
        mCameraView.enableView();
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

            Toast.makeText(ConfigActivity.this,"設定を保存しました",Toast.LENGTH_SHORT).show();

        }
    };
    View.OnClickListener calledBackButton = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ConfigActivity.this,MainActivity.class);
            startActivity(intent);

        }
    };


    SeekBar.OnSeekBarChangeListener calledBar = new SeekBar.OnSeekBarChangeListener(){
        @Override
        synchronized public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(seekBar.equals(binalyBar)) {
                tmpBinalyValue = progress;
                System.out.println("changed binaly");
            }
            if(seekBar.equals(ksizeBar)){
                progress = progress/2;
                progress = progress*2 + 1;
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

    @Override
    public void onCameraViewStarted(int width, int height) {
        previewPicture = new Mat(width,height, CvType.CV_8UC1);

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(Mat inputFrame) {

        Imgproc.cvtColor(inputFrame,previewPicture,Imgproc.COLOR_BGR2GRAY);
        Imgproc.threshold(previewPicture,previewPicture,tmpBinalyValue,255, Imgproc.THRESH_BINARY);
        Imgproc.medianBlur(previewPicture,previewPicture,tmpKSIZE);
        return previewPicture;
    }
}
