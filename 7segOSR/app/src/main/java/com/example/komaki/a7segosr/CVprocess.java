package com.example.komaki.a7segosr;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.opencv.android.*;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import static org.opencv.android.Utils.*;

/*
    This class is Processing to Bitmap Image and Recognition thread
    Called By CheckRecognize.java
 */
public class CVprocess extends Thread{


    // Primitive bitmap
    Bitmap myBitmap;

    // Expand byte data (Show ExByte.java)
    ExByte[][] exBytes;

    // Image Size of X and Y (Pixels)
    static int BITMAP_X_SIZE;
    static int BITMAP_Y_SIZE;

    // Threshold value. Used bynaly()
    double THRESHOLD = 63.0;

    // Blur value. Used blurBitmap()
    int KSIZE = 31;

    // LED Segment Object. (Show Segment.java)
    Segment[] segments;

    // Mutable Bitmap Object
    Bitmap newBitmap;

    // into Labeling Num
    ArrayList<Integer> usedLabelNum;

    // Mapping to Label -> Index
    HashMap<Integer,Integer> labelMap;

    // Jugde two Segments to One Charactor
    int SEG_RANGE = 30;

    // Charactor List
    ArrayList<Charactor> numbers;

    public CVprocess(Bitmap bitmap){
        myBitmap = bitmap;
    }

    @Override
    public void run(){
        grayScale();
        binaly();
        byte[] bytes = getByteArray();
        System.out.println("length:" + bytes.length);
        //for(byte elem :bytes) System.out.println(elem);
        //for(int i)

        Bitmap tmpBitmap = Bitmap.createBitmap(myBitmap);
        newBitmap = tmpBitmap.copy(Bitmap.Config.ARGB_8888,true);

        blurBitmap(KSIZE);
        labeling();
        makeSegment();
        makeCharactor();
        System.out.println("Charactor num :" + numbers.size());
        for(Charactor elem :numbers) {
            elem.recognition();

        }


        CheckRecognize.writeBitmap(newBitmap,"newBitmap2.jpg");
        Log.d("CVprocess","Finish Processed.");


        /*
        labeling();
        int[] pixels =returnPixels();
        newBitmap.setPixels(pixels,0,BITMAP_X_SIZE,0,0,BITMAP_X_SIZE,BITMAP_Y_SIZE);
        */

    }
    public Bitmap getMyBitmap(){
        return myBitmap;
    }
    public Bitmap getNewBitmap(){
        return newBitmap;
    }


    private void grayScale(){

        Mat gray = new Mat();
        Mat origin = new Mat();
        bitmapToMat(myBitmap,origin);
        Imgproc.cvtColor(origin,gray,Imgproc.COLOR_RGB2GRAY);
        matToBitmap(gray,myBitmap);
    }


    private void binaly(){
        Mat bin = new Mat();
        Mat origin = new Mat();
        bitmapToMat(myBitmap,origin);
        Imgproc.threshold(origin,bin,THRESHOLD,255, Imgproc.THRESH_BINARY);
        matToBitmap(bin,myBitmap);

    }

    private byte[] getByteArray(){
        int bytesize = myBitmap.getByteCount();
        ByteBuffer buffer = ByteBuffer.allocate(bytesize);
        myBitmap.copyPixelsToBuffer(buffer);
        return buffer.array();
    }

    private void labeling() {
        Mat src = new Mat();
        bitmapToMat(newBitmap, src);

        BITMAP_X_SIZE = newBitmap.getWidth();
        BITMAP_Y_SIZE = newBitmap.getHeight();
        System.out.println("x:" + BITMAP_X_SIZE + ",y:" + BITMAP_Y_SIZE + ",size:" + BITMAP_X_SIZE * BITMAP_Y_SIZE);

        exBytes = new ExByte[BITMAP_Y_SIZE][BITMAP_X_SIZE];

        for (int i = 0; i < BITMAP_Y_SIZE; i++) {
            for (int j = 0; j < BITMAP_X_SIZE; j++) {
                exBytes[i][j] = new ExByte(newBitmap.getPixel(j, i),j,i);
            }
        }
        setLabel();
    }

    private void setLabel(){
        boolean updated = true;
        int tmpLab1,tmpLab2;
        int minimam;
        int label = 1;
        while(updated){
            updated = false;
            for(int i=1;i<BITMAP_Y_SIZE -1;i++){
                for (int j=1;j<BITMAP_X_SIZE -1;j++){
                    if(exBytes[i][j].color == ExByte.WHITE) continue;
                    tmpLab1 = 0;
                    tmpLab2 = 0;
                    //updated = true;
                    if(exBytes[i-1][j].color == ExByte.BLACK) tmpLab1 = exBytes[i-1][j].LABEL;
                    if(exBytes[i][j-1].color == ExByte.BLACK) tmpLab2 = exBytes[i][j-1].LABEL;

                    if(exBytes[i][j].LABEL == 0) {
                        if (tmpLab1 == 0 && tmpLab2 == 0) {
                            exBytes[i][j].LABEL = label;
                            label++;
                        } else if (tmpLab1 == 0) {
                            exBytes[i][j].LABEL = tmpLab2;
                        } else if (tmpLab2 == 0) {
                            exBytes[i][j].LABEL = tmpLab1;
                        } else {
                            exBytes[i][j].LABEL = (tmpLab1 < tmpLab2) ? tmpLab1 : tmpLab2;
                        }
                        updated = true;
                    } else {
                        if(tmpLab1 != 0 && tmpLab2 != 0) {
                            minimam = (tmpLab1 < tmpLab2) ? tmpLab1 : tmpLab2;
                            if (exBytes[i][j].LABEL > minimam) {
                                exBytes[i][j].LABEL = minimam;
                                updated = true;
                            }
                        } else if(tmpLab1 != 0) {
                            if(exBytes[i][j].LABEL > tmpLab1){
                                exBytes[i][j].LABEL = tmpLab1;
                                updated = true;
                            }
                        } else if(tmpLab2 != 0){
                            if(exBytes[i][j].LABEL > tmpLab2){
                                exBytes[i][j].LABEL = tmpLab2;
                                updated = true;
                            }
                        }
                    }
                    //System.out.println("LABELING");
                }
            }
            // Reverse Labeling direction
            for(int i=BITMAP_Y_SIZE -2 ;i>0 ;i--){
                for (int j=BITMAP_X_SIZE -2 ;j>0 ;j--){
                    if(exBytes[i][j].color == ExByte.WHITE) continue;
                    tmpLab1 = 0;
                    tmpLab2 = 0;
                    if(exBytes[i+1][j].color == ExByte.BLACK) tmpLab1 = exBytes[i+1][j].LABEL;
                    if(exBytes[i][j+1].color == ExByte.BLACK) tmpLab2 = exBytes[i][j+1].LABEL;
                    if(exBytes[i][j].LABEL == 0) {
                        if (tmpLab1 == 0 && tmpLab2 == 0) {
                            exBytes[i][j].LABEL = label;
                            label++;
                        } else if (tmpLab1 == 0) {
                            exBytes[i][j].LABEL = tmpLab2;
                        } else if (tmpLab2 == 0) {
                            exBytes[i][j].LABEL = tmpLab1;
                        } else {
                            exBytes[i][j].LABEL = (tmpLab1 < tmpLab2) ? tmpLab1 : tmpLab2;
                        }
                        updated = true;
                    }else {
                        if(tmpLab1 != 0 && tmpLab2 != 0) {
                            minimam = (tmpLab1 < tmpLab2) ? tmpLab1 : tmpLab2;
                            if (exBytes[i][j].LABEL > minimam) {
                                exBytes[i][j].LABEL = minimam;
                                updated = true;
                            }
                        } else if(tmpLab1 != 0) {
                            if(exBytes[i][j].LABEL > tmpLab1){
                                exBytes[i][j].LABEL = tmpLab1;
                                updated = true;
                            }
                        } else if(tmpLab2 != 0){
                            if(exBytes[i][j].LABEL > tmpLab2){
                                exBytes[i][j].LABEL = tmpLab2;
                                updated = true;
                            }
                        }
                    }
                    //System.out.println("LABELING");
                }
            }
        }
    }


    private int[] returnPixels(){
        int[] res = new int[BITMAP_Y_SIZE*BITMAP_X_SIZE];
        for(int i=0;i<BITMAP_Y_SIZE;i++){
            for(int j=0;j<BITMAP_X_SIZE;j++){
                res[i*BITMAP_Y_SIZE + j] = exBytes[i][j].intDada;
            }
        }
        return res;
    }
    private void blurBitmap(int ksize){
        Mat src = new Mat();
        Mat dst = new Mat();
        bitmapToMat(newBitmap,src);
        Imgproc.medianBlur(src,dst,ksize);
        matToBitmap(dst,newBitmap);
    }

    private void makeSegment(){
        int cnt = 0;
        usedLabelNum = new ArrayList<>();
        CheckRecognize.writeLabel(exBytes,"testLabel.txt");
        for (int i = 0; i < BITMAP_Y_SIZE; i++) {
            for (int j = 0; j < BITMAP_X_SIZE; j++) {
                if(usedLabelNum.indexOf(exBytes[i][j].LABEL) == -1) usedLabelNum.add(exBytes[i][j].LABEL);
            }
            System.out.println();
        }
        usedLabelNum.remove(0);
        segments = new Segment[usedLabelNum.size()];
        labelMap = new HashMap<>();

        for(int i=0;i<segments.length;i++) segments[i] = new Segment();
        for(int elem :usedLabelNum) {
            segments[cnt].setLabel(elem);
            labelMap.put(elem,cnt);
            cnt ++;
        }
        for (int i = 0; i < BITMAP_Y_SIZE; i++) {
            for (int j = 0; j < BITMAP_X_SIZE; j++) {
                if(exBytes[i][j].LABEL == 0) continue;
                segments[labelMap.get(exBytes[i][j].LABEL)].addData(exBytes[i][j]);
            }
        }
        for(Segment elem: segments) {
            elem.getExtremePoint();
            elem.setSize();
        }

    }


    private void makeCharactor(){
        numbers = new ArrayList<>();
        for(int i=0;i<segments.length;i++){
            if(i == segments.length -1) {
                if(segments[i].signedFlag) continue;
                numbers.add(new Charactor(segments[i]));
                continue;
            }
            for(int j=i+1;j<segments.length;j++){
                if(segments[i].signedFlag) continue;
                if(Math.abs(segments[i].maxX - segments[j].maxX) < SEG_RANGE) {
                    System.out.println("gattai");
                    numbers.add(new Charactor(segments[i], segments[j]));
                    segments[i].signedFlag = true;
                    segments[j].signedFlag = true;
                    continue;
                }
                if(j == segments.length -1 ) numbers.add(new Charactor(segments[i]));
            }
        }
    }


}
