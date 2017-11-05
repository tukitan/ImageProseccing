package com.example.komaki.a7segosr;

import org.opencv.core.Mat;

import java.util.ArrayList;

/**
 * Created by tukitan on 17/05/28.
 */

/*
    (minX,minY) ------ (midX,minY) ------ (maxX,minY)
         |                  |                  |
         |                  |                  |
         |                  |                  |
         |                  |                  |
(minX,mid_up_midY)          |           (maxX,mid_up_midY)
         |                  |                  |
         |                  |                  |
         |                  |                  |
         |                  |                  |
    (minX,midY) ------ (midX,midY) ------ (maxX,midY)
         |                  |                  |
         |                  |                  |
         |                  |                  |
         |                  |                  |
(minX,mid_down_midY)        |          (maxX,mid_down_midY)
         |                  |                  |
         |                  |                  |
         |                  |                  |
         |                  |                  |
    (minX,maxY) ------ (midX,maxY) ------ (maxX,maxY)
 */

public class Charactor {
    ArrayList<ExByte> data;
    Points points;
    int maxX,maxY,minX,minY;
    int midX,midY;
    int mid_up_midY,mid_down_midY;

    String value;
    double ratio;

    boolean isComma = false;

    double ONE_RANGE = 3.0;
    int SEG_WIDTH = 5;
    int RANGE = 7;
    int sizeX,sizeY;

    static int OFFSET;


    public Charactor(Segment seg1,boolean flag){
        data = seg1.element;
        maxX = seg1.maxX;
        maxY = seg1.maxY;
        minX = seg1.minX;
        minY = seg1.minY;
        if(flag){
            isComma = true;
            value = ".";
            System.out.println("value :.");
        }
    }
    public Charactor(Segment seg1,Segment seg2){
        data = new ArrayList<>();
        for(ExByte elem :seg1.element) data.add(elem);
        for(ExByte elem :seg2.element) data.add(elem);

        maxX = (seg1.maxX > seg2.maxX) ? seg1.maxX : seg2.maxX;
        maxY = (seg1.maxY > seg2.maxY) ? seg1.maxY : seg2.maxY;
        minX = (seg1.minX < seg2.minX) ? seg1.minX : seg2.minX;
        minY = (seg1.minY < seg2.minY) ? seg1.minY : seg2.minY;
    }

    public void setRatio() { ratio = points.getRatio(); }

    public void setMid() {
        midX = (maxX + minX)/2;
        midY = (maxY + minY)/2;
    }

    public void setBetweenMaxMid(){
        mid_up_midY = (minY + midY) /2;
        mid_down_midY = (maxY + midY) /2;
    }

    public void recognition(){
        setMid();
        setBetweenMaxMid();
        points = new Points(maxX,maxY,minX,minY,midX,midY,mid_up_midY,mid_down_midY);
        setRatio();

        sizeX = Math.abs(maxX-minX);
        sizeY = Math.abs(maxY-minY);

        // Use Recognition Flags
        boolean leftdown = false;
        boolean rightup = false;
        boolean mid = false;
        boolean midup = false;
        boolean middown = false;
        boolean left_up_mid = false;
        boolean left_down_mid = false;
        boolean right_up_mid = false;

        int tmpX,tmpY;

        if(ratio > ONE_RANGE){
            value = "1";
            System.out.println("value:1");
            points.display();
            return;
        }
        for(ExByte elem :data){
            tmpX = elem.pointX;
            tmpY = elem.pointY;

            if(isEqual(tmpX,midX,tmpY,midY,0)) mid = true;
            else if(isEqual(tmpX,midX,tmpY,minY,2)) midup = true;
            else if(isEqual(tmpX,midX,tmpY,maxY,-2)) middown = true;
            else if(isEqual(tmpX,minX,tmpY,mid_up_midY,1)) left_up_mid = true;
            else if(isEqual(tmpX,minX,tmpY,mid_down_midY,-1)) left_down_mid = true;
            else if(isEqual(tmpX,maxX,tmpY,mid_up_midY,1)) right_up_mid = true;
        }


        if(!mid){
            if(!middown){
                value = "7";
                System.out.println("value:7");
                points.display();
                return;
            }
            value = "0";
            System.out.println("value:0");
            points.display();
            return;
        }

        if(!midup){
            value = "4";
            System.out.println("value:4");
            points.display();
            return;
        }

        if(!right_up_mid){
            if(!left_down_mid) {
                value = "5";
                System.out.println("value:5");
                points.display();
                return;
            }
            value = "6";
            System.out.println("value:6");
            points.display();
            return;

        }

        if(!left_up_mid){
            // Candidate ; 2 , 3
            if(!left_down_mid) {
                value = "3";
                System.out.println("value:3");
                points.display();
                return;
            } else{
                value = "2";
                System.out.println("value:2");
                points.display();
                return;
            }
        }

        if(!left_down_mid) {
            value = "9";
            System.out.println("value:9");
            points.display();
            return;
        }

        value = "8";
        System.out.println("value:8");
        points.display();
        return;

    }

    private boolean isEqual(int srcX,int targetX,int srcY,int targetY,int position){
        double offsetLength=0;
        if(position == 2 || position == -2){
            offsetLength = OFFSET * 0.1 * sizeX * 0.5 * position*0.5;
        }
        if(position == 1 || position == -1){
            offsetLength = OFFSET * 0.05 * sizeX * 0.5 * position*0.5;
        }
        boolean xFrag = (srcX < (targetX + RANGE + (int)offsetLength) && srcX > (targetX - RANGE + (int)offsetLength)) ? true : false;
        boolean yFrag = (srcY < (targetY + RANGE) && srcY > (targetY - RANGE)) ? true : false;
        return xFrag && yFrag;
    }


}
