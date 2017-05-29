package com.example.komaki.a7segosr;

import java.util.ArrayList;

/**
 * Created by tukitan on 17/05/28.
 */

public class Charactor {
    ArrayList<ExByte> data;
    Points points;
    int maxX,maxY,minX,minY;
    int midX,midY;
    String value;
    double ratio;

    double COMMA_RANGE = 1.5;
    double ONE_RANGE = 3.0;

    public Charactor(Segment seg1){
        data = seg1.element;
        points = seg1.points;
    }
    public Charactor(Segment seg1,Segment seg2){
        data = new ArrayList<>();
        for(ExByte elem :seg1.element) data.add(elem);
        for(ExByte elem :seg2.element) data.add(elem);

        maxX = (seg1.maxX > seg2.maxX) ? seg1.maxX : seg2.maxX;
        maxY = (seg1.maxY > seg2.maxY) ? seg1.maxY : seg2.maxY;
        minX = (seg1.minX < seg2.minX) ? seg1.minX : seg2.minX;
        minY = (seg1.minY < seg2.minY) ? seg1.minY : seg2.minY;
        points = new Points(maxX,maxY,minX,minY);
    }

    public void setRatio() { ratio = points.getRatio(); }

    public void setMid() {
        midX = (maxX + minX)/2;
        midY = (maxY + minY)/2;
    }

    public void recognition(){
        setRatio();
        setMid();
        if(Math.abs(ratio) < COMMA_RANGE ) {
            value = ".";
            System.out.println("value:.");
            points.display();
            return;
        }
        if(ratio > ONE_RANGE){
            value = "1";
            System.out.println("value:1");
            points.display();
            return;
        }



    }


}
