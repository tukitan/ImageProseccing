package com.example.tukitan.takepicture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.media.Image;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import static java.security.AccessController.getContext;

/**
 * Created by tukitan on 17/04/14.
 */

public class Recognition extends View{
    byte[] bytes;
    private Paint mPaint = new Paint();
    //Input image and recognize this
    public Recognition(Context context, Image image) {
        super(context);
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        bytes = new byte[buffer.capacity()];
        buffer.get(bytes);
    }

    protected void recognize() throws Exception{
        Bitmap output = null;
        if(bytes != null){
            output = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            saveImage(bytes);
        }
        //canvas.drawBitmap(output,0,0,mPaint);
        System.out.println("Recognition");

        output = output.copy(Bitmap.Config.ARGB_8888,true);
        TessBaseAPI tessBaseAPI = new TessBaseAPI();
        tessBaseAPI.init(Environment.getExternalStorageDirectory().getPath(),"led");

        tessBaseAPI.setImage(output);
        String recognized = tessBaseAPI.getUTF8Text();
        System.out.println("END Recognition");
        //Toast.makeText(getContext(),recognized,Toast.LENGTH_SHORT).show();

        System.out.println(recognized);
        tessBaseAPI.end();

    }

    private void saveImage(byte[] data){
        OutputStream output = null;
        try{
            output = new FileOutputStream(Environment.getExternalStorageDirectory().getPath()+"/RecognitionImage.jpg");
            output.write(data);
            output.close();
            System.out.println("Finished Save image");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
