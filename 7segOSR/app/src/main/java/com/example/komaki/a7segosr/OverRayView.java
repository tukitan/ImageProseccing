package com.example.komaki.a7segosr;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by tukitan on 17/10/05.
 */

public class OverRayView extends View {

    Points points;

    public OverRayView(Context context,Points points) {
        super(context);
        this.points = points;
    }

    @Override
    protected void onDraw(Canvas c){
        super.onDraw(c);

        Paint paint = new Paint();
        paint.setARGB(0,240,0,0);

    }
}
