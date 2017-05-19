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
    public ExByte(Byte data){
        color = (data == 0) ? BLACK : WHITE;
    }
}
