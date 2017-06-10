package com.example.komaki.a7segosr;

import android.util.Log;

import org.opencv.core.Point;

/**
 * Created by tukitan on 17/05/29.
 */

public class Points {
    int maxX, maxY, minX, minY;
    int midX,midY;
    int mid_up_midY,mid_down_midY;

    static double EXPAND_X = 2620.0/1020.0;
    static double EXPAND_Y = 4656.0/1940.0;

    public Points(int maxX,int maxY,int minX,int minY,int midX,int midY,int mid_up_midY,int mid_down_midY){
        this.maxX = maxX;
        this.maxY = maxY;
        this.minX = minX;
        this.minY = minY;
        this.midX = midX;
        this.midY = midY;
        this.mid_up_midY = mid_up_midY;
        this.mid_down_midY = mid_down_midY;
    }

    public Points(int maxX,int maxY,int minX,int minY,boolean cameraToCV){
        if(cameraToCV) {
            this.maxX = (int)((double)maxX * EXPAND_X);
            this.maxY = (int)((double)maxY * EXPAND_Y);
            this.minX = (int)((double)minX * EXPAND_X);
            this.minY = (int)((double)minY * EXPAND_Y);
        } else{
            this.maxX = maxX;
            this.maxY = maxY;
            this.minX = minX;
            this.minY = minY;
        }

    }

    public Points() {

    }

    public double getRatio(){
        double Xsize = maxX - minX;
        double Ysize = maxY - minY;
        return Ysize/Xsize;
    }

    public void display(){
        Log.d("POINTS:","MIN("+minX+","+minY+"), MAX("+maxX+","+maxY+"), MID("+midX+","+midY+")");
    }

}