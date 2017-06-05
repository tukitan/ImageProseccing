package com.example.komaki.a7segosr;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.hardware.camera2.CameraAccessException;
import android.media.Image;
import android.os.Environment;
import android.util.Log;

import org.opencv.android.*;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static android.R.attr.bitmap;
import static org.opencv.android.Utils.*;

/*
    This class is Processing to Bitmap Image and Recognition thread
    Called By CheckRecognize.java
 */
public class CVprocess implements Runnable{


    // Primitive bitmap
    Bitmap myBitmap;

    // Expand byte data (Show ExByte.java)
    ExByte[][] exBytes;

    // Image Size of X and Y (Pixels)
    static int BITMAP_X_SIZE;
    static int BITMAP_Y_SIZE;

    // Threshold value. Used bynaly()
    static double THRESHOLD = 50.0;

    // Blur value. Used blurBitmap()
    static int KSIZE = 21;

    int COMMA_SIZE_MIN = 100;
    int COMMA_SIZE_MAX = 800;
    double COMMA_RANGE_MAX = 2.5;
    double COMMA_RANGE_MIN = 0.5;
    int SEG_SIZE_MIN = 4000;

    double NUMBER_RATIO = 0.9;

    // LED Segment Object. (Show Segment.java)
    Segment[] segments;

    // Mutable Bitmap Object
    Bitmap newBitmap;

    // into Labeling Num
    ArrayList<Integer> usedLabelNum;

    // Mapping to Label -> Index
    HashMap<Integer,Integer> labelMap;

    // Jugde two Segments to One Charactor
    int SEG_RANGE = 20;

    int COMMA_RATIO = 2;

    // Charactor List
    ArrayList<Charactor> numbers;

    public CVprocess(Bitmap bitmap){
        myBitmap = bitmap;
    }
    public CVprocess(Image image, Points points){
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.capacity()];
        buffer.get(bytes);
        Bitmap tmpBitmap  = BitmapFactory.decodeByteArray(bytes,0,bytes.length);

        // Rotate Input Picture
        int imageWidth = tmpBitmap.getWidth();
        int imageHeight = tmpBitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(90,imageWidth/2,imageHeight/2);

        Bitmap tmp2Bitmap = Bitmap.createBitmap(tmpBitmap,0,0,imageWidth,imageHeight,matrix,true);

        // Cut Rect Picture
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        tmp2Bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] tmpbytes = baos.toByteArray();

        try {
            BitmapRegionDecoder regionDecoder = BitmapRegionDecoder.newInstance(tmpbytes,0,tmpbytes.length,false);
            Rect rect = new Rect(points.minX,points.minY,points.maxX,points.maxY);
            myBitmap = regionDecoder.decodeRegion(rect,null);
            writeBitmap(myBitmap,"CuttedImage.bmp");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void run(){
        grayScale();
        binaly();
        //for(byte elem :bytes) System.out.println(elem);
        //for(int i)

        newBitmap = myBitmap.copy(Bitmap.Config.ARGB_8888,true);

        blurBitmap(KSIZE);
        labeling();
        makeSegment();
        makeCharactor();
        System.out.println("Charactor num :" + numbers.size());
        for(Charactor elem :numbers) {
            if(!elem.isComma) elem.recognition();
        }
        String result = makeString();
        System.out.println(result);

        writeBitmap(newBitmap,result);
        Log.d("CVprocess","Finish Processed.");
        CameraActivity.isProcessed = true;


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
        }

    }


    private void makeCharactor(){
        numbers = new ArrayList<>();

        int i,cnt=0;
        int dataSize;
        ArrayList<Integer> delete = new ArrayList<>();
        ArrayList<Segment> tmpSegArray = new ArrayList<>(Arrays.asList(segments));
        System.out.println(tmpSegArray.size());
        for(i=0;i<tmpSegArray.size();i++){
            dataSize = tmpSegArray.get(i).getSize();
            System.out.println("size : " + dataSize);
            if(dataSize < SEG_SIZE_MIN ){
                if(dataSize > COMMA_SIZE_MIN && dataSize < COMMA_SIZE_MAX){
                    if(COMMA_RANGE_MIN < Math.abs(tmpSegArray.get(i).points.getRatio()) && Math.abs(tmpSegArray.get(i).points.getRatio()) < COMMA_RANGE_MAX ) {
                        numbers.add(new Charactor(tmpSegArray.get(i),true));
                        System.out.println("comma");
                    }
                }
                delete.add(i);
            } else{
                if(NUMBER_RATIO > Math.abs(tmpSegArray.get(i).points.getRatio())){
                    System.out.println("noise");
                    delete.add(i);
                }
            }
        }
        for(Integer elem :delete) {
            tmpSegArray.remove((int)elem - cnt);
            cnt ++;
        }
        tmpSegArray.trimToSize();

        Segment[] newSegment = (Segment[]) tmpSegArray.toArray(new Segment[tmpSegArray.size()]);
        for(Segment elem :newSegment) System.out.println(elem.getSize());

        for(i=0;i<newSegment.length;i++){
            if(i == newSegment.length -1) {
                if(newSegment[i].signedFlag) continue;
                numbers.add(new Charactor(newSegment[i],false));
                continue;
            }
            for(int j=i+1;j<newSegment.length;j++){
                if(newSegment[i].signedFlag) continue;
                if(Math.abs(newSegment[i].maxX - newSegment[j].maxX) < SEG_RANGE) {
                    System.out.println("gattai");
                    numbers.add(new Charactor(newSegment[i], newSegment[j]));
                    newSegment[i].signedFlag = true;
                    newSegment[j].signedFlag = true;
                    continue;
                }
                if(j == newSegment.length -1 ) numbers.add(new Charactor(newSegment[i],false));
            }
        }
    }
    private String  makeString(){
        String res = new String();
        Charactor tmp;
        int current = 0;
        for (int i=0;i<numbers.size()-1;i++){
            for(int j=i+1;j<numbers.size();j++){
                if(numbers.get(i).minX > numbers.get(j).minX){
                    tmp = numbers.get(i);
                    numbers.set(i,numbers.get(j));
                    numbers.set(j,tmp);
                }
            }
        }
        for(Charactor elem :numbers) res += elem.value;
        return res;

    }
    static void writeBitmap(Bitmap bmp,String filename) {
        String path = "/" + Environment.getExternalStorageDirectory() + "/7segOCRresult/" + filename;
        File file = new File(path);
        file.getParentFile().mkdir();
        FileOutputStream out = null;
        try{
            out = new FileOutputStream(path);
            bmp.compress(Bitmap.CompressFormat.JPEG,100,out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
