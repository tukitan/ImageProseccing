package com.example.komaki.a7segosr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by komaki on 17/06/02.
 */

public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    Bitmap printBitmap;
    public MySurfaceView(Context context,Bitmap bitmap){
        super(context);
        printBitmap = bitmap;
        getHolder().addCallback(this);
    }
    private void mydraw(Canvas canvas){
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        canvas.drawBitmap(printBitmap,width,height,new Paint());
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        (new DrawThread()).start();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private class DrawThread extends Thread{
        public void run(){
            SurfaceHolder holder = getHolder();
            Canvas canvas = holder.lockCanvas();
            if(canvas != null){
                mydraw(canvas);
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }
}
