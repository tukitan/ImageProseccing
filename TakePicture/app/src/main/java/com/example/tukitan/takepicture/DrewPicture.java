package com.example.tukitan.takepicture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.Image;
import android.view.View;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.nio.ByteBuffer;

/**
 * Created by tukitan on 17/04/14.
 */

public class DrewPicture extends View {
    byte[] bytes;
    private Paint mPaint = new Paint();
    public DrewPicture(Context context,Image image) {
        super(context);
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        bytes = new byte[buffer.capacity()];
        buffer.get(bytes);
    }

    @Override
    protected void onDraw(Canvas canvas){
        Bitmap output = null;
        if(bytes != null){
            output = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        }
        //canvas.drawBitmap(output,0,0,mPaint);
        output = output.copy(Bitmap.Config.ARGB_8888,true);
        TessBaseAPI tessBaseAPI = new TessBaseAPI();
        tessBaseAPI.init("/classes.jar/com.googlecode/","eng");

        tessBaseAPI.setImage(output);
        String recognized = tessBaseAPI.getUTF8Text();
        Toast.makeText(getContext(),recognized,Toast.LENGTH_SHORT).show();
    }

}
