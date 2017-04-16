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
        }
        //canvas.drawBitmap(output,0,0,mPaint);
        output = output.copy(Bitmap.Config.ARGB_8888,true);
        TessBaseAPI tessBaseAPI = new TessBaseAPI();
        tessBaseAPI.init(String.valueOf(Environment.getExternalStorageDirectory()),"eng");

        tessBaseAPI.setImage(output);
        String recognized = tessBaseAPI.getUTF8Text();
        Toast.makeText(getContext(),recognized,Toast.LENGTH_SHORT).show();
    }
}
