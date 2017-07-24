package com.example.komaki.a7segosr;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.Image;
import android.os.Environment;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
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
import java.util.Locale;

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
    static double THRESHOLD;

    // Blur value. Used blurBitmap()
    static int KSIZE;

    int COMMA_SIZE_MIN = 40;
    int COMMA_SIZE_MAX = 300;
    double COMMA_RANGE_MAX = 2.5;
    double COMMA_RANGE_MIN = 0.5;
    int SEG_SIZE_MIN = 500;

    double NUMBER_RATIO_MIN = 0.9;
    double NUMBER_RATIO_MAX = 5.5;

    // LED Segment Object. (Show Segment.java)
    Segment[] segments;

    // Mutable Bitmap Object
    Bitmap newBitmap;

    // into Labeling Num
    ArrayList<Integer> usedLabelNum;

    // Mapping to Label -> Index
    HashMap<Integer,Integer> labelMap;

    // Jugde two Segments to One Charactor
    int SEG_RANGE = 15;

    int COMMA_RATIO = 2;

    // Charactor List
    ArrayList<Charactor> numbers;
    ArrayList<Charactor> commaList;

    Handler handler;
    boolean isCalledByCheckRecognize = false;

    int TMP_MIN_X,TMP_MIN_Y,TMP_MAX_X,TMP_MAX_Y;

    public CVprocess(Bitmap bitmap,boolean calledCheckRec,Handler handler){
        myBitmap = bitmap;
        isCalledByCheckRecognize = true;
        this.handler = handler;
    }
    public CVprocess(Image image, Points points, Handler handler){
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.capacity()];
        buffer.get(bytes);
        Bitmap tmpBitmap  = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        buffer = null;
        bytes = null;

        // Rotate Input Picture
        int imageWidth = tmpBitmap.getWidth();
        int imageHeight = tmpBitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(90,imageWidth/2,imageHeight/2);

        Bitmap tmp2Bitmap = Bitmap.createBitmap(tmpBitmap,0,0,imageWidth,imageHeight,matrix,true);
        matrix = null;

        // Cut Rect Picture
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        tmp2Bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);

        try {
            BitmapRegionDecoder regionDecoder = BitmapRegionDecoder.newInstance(baos.toByteArray(),0,baos.toByteArray().length,false);
            System.out.println("x:" + points.minX + " y:" + points.minY + " x:" + points.maxX + " y:" + points.maxY);
            Rect rect = new Rect(points.minX,points.minY,points.maxX,points.maxY);
            myBitmap = regionDecoder.decodeRegion(rect,null);
            writeBitmap(myBitmap,"CuttedImage.bmp");
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.handler = handler;

        // use to the last function to invoke "new Point()"
        TMP_MIN_X = points.minX;
        TMP_MIN_Y = points.minY;
        TMP_MAX_X = points.maxX;
        TMP_MAX_Y = points.maxY;

    }
    @Override
    public void run(){
        binaly();
        //for(byte elem :bytes) System.out.println(elem);
        //for(int i)

        newBitmap = myBitmap.copy(Bitmap.Config.ARGB_8888,true);

        blurBitmap(KSIZE);
        writeBitmap(newBitmap,"result.bmp");
        labeling();
        makeSegment();
        makeCharactor();
        System.out.println("Charactor num :" + numbers.size());
        for(Charactor elem :numbers) {
            if(!elem.isComma) elem.recognition();
        }
        judgeComma();
        final String result = makeString();

        System.out.println("KSIZE:" + KSIZE);
        System.out.println(result);
        commaList = null;
        labelMap = null;
        exBytes = null;
        segments = null;

        Log.d("CVprocess","Finish Processed.");

        if(isCalledByCheckRecognize){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    CheckRecognize.number = result;
                }
            });
            CheckRecognize.proceccFlag = true;

        } else {
            handler.post(new Runnable() {
                int minx = TMP_MIN_X;
                int miny = TMP_MIN_Y + numbers.get(0).minY * 2;
                int maxx = TMP_MIN_X + (numbers.get(numbers.size() -1).maxX * 2);
                int maxy = TMP_MIN_Y + (numbers.get(numbers.size() -1).maxY * 2);
                @Override
                public void run() {
                    CameraActivity.number = result;
                    CameraActivity.CHAR_POINTS = new Points(maxx +5 ,maxy +5 , minx, miny -5,false);
                }
            });
            CameraActivity.isProcessed = true;
        }

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
        Mat gray = new Mat();
        Mat origin = new Mat();
        bitmapToMat(myBitmap,origin);
        Imgproc.cvtColor(origin,gray,Imgproc.COLOR_RGB2GRAY);
        //Imgproc.threshold(origin,bin,THRESHOLD,255, Imgproc.THRESH_BINARY);

        if(isCalledByCheckRecognize){
            //Imgproc.threshold(gray,bin,THRESHOLD,255, Imgproc.THRESH_BINARY);
            Imgproc.threshold(gray,bin,0.0,255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
        } else {
            // Auto deside THRESHOLD mode
            Imgproc.threshold(gray,bin,0.0,255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
        }

        matToBitmap(bin,myBitmap);

    }
    private void labeling() {
        Mat src = new Mat();
        bitmapToMat(newBitmap, src);
        int k=0,l=0;

        BITMAP_X_SIZE = newBitmap.getWidth();
        BITMAP_Y_SIZE = newBitmap.getHeight();
        System.out.println("x:" + BITMAP_X_SIZE/2 + ",y:" + BITMAP_Y_SIZE/2 + ",size:" + BITMAP_X_SIZE/2 * BITMAP_Y_SIZE/2);

        exBytes = new ExByte[BITMAP_Y_SIZE/2 + 1][BITMAP_X_SIZE/2 +1];

        for (int i = 0; i < BITMAP_Y_SIZE ; i+=2) {
            for (int j = 0; j < BITMAP_X_SIZE ;j+=2) {
                exBytes[k][l] = new ExByte(newBitmap.getPixel(j, i),l,k);
                l++;
            }
            l=0;
            k++;
        }
        BITMAP_X_SIZE /= 2;
        BITMAP_Y_SIZE /= 2;
        /*


        exBytes = new ExByte[BITMAP_Y_SIZE][BITMAP_X_SIZE];

        for (int i = 0; i < BITMAP_Y_SIZE; i+=2) {
            for (int j = 0; j < BITMAP_X_SIZE; j+=2) {
                exBytes[i][j] = new ExByte(newBitmap.getPixel(j, i),j,i);
            }
        }
         */
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
        commaList = new ArrayList<>();

        int i,cnt=0;
        int dataSize;
        ArrayList<Integer> delete = new ArrayList<>();
        ArrayList<Segment> tmpSegArray = new ArrayList<>(Arrays.asList(segments));
        System.out.println(tmpSegArray.size());
        for(i=0;i<tmpSegArray.size();i++){
            dataSize = tmpSegArray.get(i).getSize();
            System.out.println("size : " + dataSize);
            if(dataSize < SEG_SIZE_MIN ){
                commaList.add(new Charactor(tmpSegArray.get(i),false));
                delete.add(i);
            } else{
                double tmp = tmpSegArray.get(i).points.getRatio();
                if(NUMBER_RATIO_MIN > tmp || NUMBER_RATIO_MAX < tmp){
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

    private void judgeComma(){
        int maxX = 0;
        int cnt = 0;
        ArrayList<Integer> deleteList = new ArrayList<>();
        for (int i=0;i<numbers.size();i++){
            maxX = (maxX < numbers.get(i).minX) ? numbers.get(i).minX : maxX;
        }
        for(int i=0;i<commaList.size();i++){
            if(commaList.get(i).minX > maxX) {
                deleteList.add(i);
            }
        }

        for(Integer elem :deleteList) {
            commaList.remove((int)elem - cnt);
            cnt ++;
        }
        commaList.trimToSize();
        System.out.println("COMMALIST SIZE:" + commaList.size());


    }
    private String  makeString(){
        String res = new String();
        int minx,miny,maxx,maxy;
        Charactor tmp;
        int current = 0;
        if(commaList.size() != 0 ) {
            commaList.get(0).value = ".";
            numbers.add(commaList.get(0));
        }
        if(commaList.size() > 1) System.out.println("Too many Comma!");
        for (int i=0;i<numbers.size()-1;i++){
            for(int j=i+1;j<numbers.size();j++){
                if(numbers.get(i).minX > numbers.get(j).minX){
                    tmp = numbers.get(i);
                    numbers.set(i,numbers.get(j));
                    numbers.set(j,tmp);
                }
            }
        }
        for(Charactor elem :numbers) {
            res += elem.value;
        }

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
