package com.example.komaki.a7segosr;

import java.util.ArrayList;

/**
 * Created by tukitan on 17/05/28.
 */

public class Segment {
    ArrayList<ExByte> element;
    int SEG_LABEL;
    Points points;
    int maxX=0,maxY=0,minX=CVprocess.BITMAP_X_SIZE,minY=CVprocess.BITMAP_Y_SIZE;
    int size;

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
        points = new Points(maxX,maxY,minX,minY);

    }

    public void setSize(){ size = element.size(); }

}
