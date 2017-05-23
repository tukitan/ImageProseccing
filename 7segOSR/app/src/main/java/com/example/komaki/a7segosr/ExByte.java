package com.example.komaki.a7segosr;

import java.lang.Byte;
/**
 * Created by komaki on 17/05/19.
 */

public class ExByte {
    static boolean WHITE = true;
    static boolean BLACK = false;
    boolean color;
    int LABEL;
    int pointX,pointY;
    public ExByte(int x,int y,Byte data){
        color = (data == 0) ? BLACK : WHITE;
        pointX = x;
        pointY = y;
    }

}
