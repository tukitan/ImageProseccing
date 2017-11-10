package com.example.komaki.a7segosr;

import android.graphics.Point;

import java.util.ArrayList;

/**
 * Created by tukitan on 17/05/28.
 */

public class Segment {
    ArrayList<ExByte> element;
    int SEG_LABEL;
    Points points;
    Point center;
    int maxX=0,maxY=0,minX=CVprocess.BITMAP_X_SIZE,minY=CVprocess.BITMAP_Y_SIZE;
    int size;

    boolean signedFlag = false;

    public Segment(){
        element = new ArrayList<>();
    }

    public void setLabel(int labelNum){
        SEG_LABEL = labelNum;
    }

    public void addData(ExByte data){
        element.add(data);
    }

    public void getExtremePoint(){
        int tmpX,tmpY;
        for(ExByte elem :element){
            tmpX = elem.pointX;
            tmpY = elem.pointY;


            maxX = (tmpX > maxX) ? tmpX : maxX;
            maxY = (tmpY > maxY) ? tmpY : maxY;
            minX = (tmpX < minX) ? tmpX : minX;
            minY = (tmpY < minY) ? tmpY : minY;
        }

        points = new Points(maxX,maxY,minX,minY,false);
        center = new Point();
        int center_x = minX + ((maxX-minX)/2);
        int center_y = minY + ((maxY-minY)/2);
        center.set(center_x,center_y);
    }

    public int getSize(){ return element.size(); }


}
