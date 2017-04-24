package com.example.tukitan.takepicture;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

public class CVrecognition extends AppCompatActivity {

    ImageView view;
    Button reverse;
    Bitmap bitmap;
    boolean prepareFlag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cvrecognition);

        byte[] bytes = getIntent().getByteArrayExtra("BYTEARRAY");
        view = (ImageView)findViewById(R.id.imageView);
        reverse = (Button)findViewById(R.id.reverse);
        view.setImageBitmap(this.bitmap);
        reverse.setOnClickListener(listener);
        this.bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }

    private BaseLoaderCallback callback = new BaseLoaderCallback(this) {

        @Override
        public void onManagerConnected(int status) {
            switch (status){
                case LoaderCallbackInterface.SUCCESS:
                    prepareFlag = true;
                    System.out.println("prepareFlag = true.");
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };
    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Mat img0 = new Mat();
            Mat img1 = new Mat();
            Utils.bitmapToMat(bitmap,img0);
            Core.absdiff(img0,new Scalar(255,255,255),img1);
            Utils.matToBitmap(img1,bitmap);
            view.setImageBitmap(bitmap);

            img0.release();
            img1.release();

        }
    };
}
