package com.example.komaki.a7segosr;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by tukitan on 17/07/24.
 */

public class DrawRectView extends View {
    Paint paint;
    Points p;
    public DrawRectView(Context context,Points point) {
        super(context);
        paint = new Paint();
        p = point;
    }

    @Override
    protected void onDraw(Canvas canvas){
        paint.setColor(Color.argb(255,255,0,255));
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(p.minX,p.minY,p.maxX,p.maxY,paint);

    }
}
