package com.example.komaki.a7segosr;

import java.lang.Byte;
/**
 * Created by komaki on 17/05/19.
 */

public class ExByte {
    static boolean WHITE = true;
    static boolean BLACK = false;
    boolean color;
    int LABEL=0;
    public int intDada;
    int pointX,pointY;

    public ExByte(int data,int x,int y){
        intDada = data;
        color = (data == -1) ? WHITE : BLACK;
        //System.out.println(color);
        pointX = x;
        pointY = y;
    }


}
