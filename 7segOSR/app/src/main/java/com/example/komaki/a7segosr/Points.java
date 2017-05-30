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

    public int getPoint(String value){
        switch (value){
            case "maxX":
                return maxX;
            case "maxY":
                return maxY;
            case "minX":
                return minY;
            case "minY":
                return minY;
        }
        return 0;
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