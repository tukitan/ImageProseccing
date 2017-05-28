package com.example.komaki.a7segosr;

import org.opencv.core.Point;

/**
 * Created by tukitan on 17/05/29.
 */

public class Points {
    int maxX, maxY, minX, minY;
    public Points(){

    }
    public Points(int maxX,int maxY,int minX,int minY){
        this.maxX = maxX;
        this.maxY = maxY;
        this.minX = minX;
        this.minY = minY;
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

}