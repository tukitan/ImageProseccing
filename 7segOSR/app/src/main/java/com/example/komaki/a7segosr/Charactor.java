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
    int RANGE = 60;

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
            /*
            if(tmpX == (maxX -SEG_WIDTH) && tmpY == (minY +SEG_WIDTH)) rightup = true;
            else if(tmpX == (minX +SEG_WIDTH) && tmpY == (maxY -SEG_WIDTH)) leftdown = true;
            else if(tmpX == midX && tmpY == midY) mid = true;
            else if(tmpX == midX && tmpY == (minY +SEG_WIDTH)) midup = true;
            else if(tmpX == midX && tmpY == (maxY -SEG_WIDTH)) middown = true;
            else if(tmpX == (minX +SEG_WIDTH) && tmpY == mid_up_midY) left_up_mid = true;
            else if(tmpX == (minX +SEG_WIDTH) && tmpY == mid_down_midY) left_down_mid = true;
            */
            if(isEqual(tmpX,midX) && isEqual(tmpY,midY)) mid = true;
            else if(isEqual(tmpX,midX) && isEqual(tmpY,minY)) midup = true;
            else if(isEqual(tmpX,midX) && isEqual(tmpY,maxY)) middown = true;
            else if(isEqual(tmpX,minX) && isEqual(tmpY,mid_up_midY)) left_up_mid = true;
            else if(isEqual(tmpX,minX) && isEqual(tmpY,mid_down_midY)) left_down_mid = true;
            else if(isEqual(tmpX,maxX) && isEqual(tmpY,mid_up_midY)) right_up_mid = true;
        }

        /*

        if (!rightup){
            value = "6";
            System.out.println("value:6");
            points.display();
            return;
        }

        if(!leftdown){
            //Candidate  ; 4 , 7 , 9
            if(!mid) {
                value = "7";
                System.out.println("value:7");
                points.display();
                return;
            }
            if(!midup){
                value = "4";
                System.out.println("value:4");
                points.display();
                return;
            }
            if(!middown){
                value = "9";
                System.out.println("value:9");
                points.display();
                return;
            }
            System.out.println("Can't resolve Character");
            value = "-1";
            return;
        }

        // Candidate ; 0 , 2 , 3 , 5 , 8
        if(!mid){
            value = "0";
            System.out.println("value:0");
            points.display();
            return;
        }
        */

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

    private boolean isEqual(int src,int target){
        return(src < (target + RANGE) && src > (target - RANGE)) ? true : false;
    }


}
