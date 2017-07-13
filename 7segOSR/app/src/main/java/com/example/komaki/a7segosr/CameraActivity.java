package com.example.komaki.a7segosr;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.*;
import android.hardware.camera2.CameraManager.TorchCallback;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Size;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;


import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class CameraActivity extends Activity implements TextToSpeech.OnInitListener{

    private TextureView mTextureView;
    private Size mPreviewSize;
    private CameraDevice mCameraDevice;
    private CaptureRequest.Builder mPreviewBuilder;
    private CameraCaptureSession mPreviewSession;
    private int MinX, MinY;
    private int MaxX, MaxY;
    boolean flag = false;
    boolean threadFlag = true;
    static boolean isProcessed = true;

    String mCameraId = null;
    boolean isOn = false;

    TextToSpeech tts;
    static String number = null;

    Handler handler;

    static Points CHAR_POINTS;
    boolean isFrist = true;

    @Override
    protected void onCreate(Bundle SavedInstance) {
        super.onCreate(SavedInstance);

        //full Screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera);

        mTextureView = (TextureView) findViewById(R.id.textureView);
        mTextureView.setSurfaceTextureListener(mCameraViewStatusChanged);
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        manager.registerTorchCallback(new TorchCallback(){
            @Override
            public void onTorchModeChanged(String cameraId,boolean enabled){
                mCameraId = cameraId;
                isOn = enabled;
            }

        },new Handler());

        tts = new TextToSpeech(this,this);
        handler = new Handler();

    }

    private final TextureView.SurfaceTextureListener mCameraViewStatusChanged = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            // Camera Available
            prepareCameraView();

        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        }
    };

    private void prepareCameraView() {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            // Search BackCamera
            for (String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics charactor = manager.getCameraCharacteristics(cameraId);
                if (charactor.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue;
                }
                System.err.println("BackCamera");

                StreamConfigurationMap map = charactor.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                mPreviewSize = map.getOutputSizes(SurfaceTexture.class)[0];

                // Rotation and Resize Preview
                // this.configureTranceform();

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                manager.openCamera(cameraId, new CameraDevice.StateCallback() {

                    @Override
                    public void onOpened(@NonNull CameraDevice cameraDevice) {
                        mCameraDevice = cameraDevice;
                        createCameraPreviewSettion();

                    }

                    @Override
                    public void onDisconnected(@NonNull CameraDevice cameraDevice) {
                        cameraDevice.close();
                        mCameraDevice = null;
                    }

                    @Override
                    public void onError(@NonNull CameraDevice cameraDevice, int i) {
                        cameraDevice.close();
                        mCameraDevice = null;
                    }
                }, null);
            }

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    protected void createCameraPreviewSettion(){
        if(mCameraDevice == null || !mTextureView.isAvailable() || mPreviewSize == null){
            return;
        }
        SurfaceTexture surfaceTexture = mTextureView.getSurfaceTexture();
        if(surfaceTexture == null) return;
        surfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(),mPreviewSize.getHeight());
        Surface surface = new Surface(surfaceTexture);
        try{
            mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        mPreviewBuilder.addTarget(surface);

        try{
            mCameraDevice.createCaptureSession(Arrays.asList(surface),new CameraCaptureSession.StateCallback(){

                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    mPreviewSession = cameraCaptureSession;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(CameraActivity.this,"FAILED", Toast.LENGTH_SHORT).show();
                }
            },null);
        } catch (CameraAccessException e){
            e.printStackTrace();
        }
    }

    protected void updatePreview(){
        if(mCameraDevice == null) return;
        mPreviewBuilder.set(CaptureRequest.CONTROL_AF_MODE,CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
        //mPreviewBuilder.set(CaptureRequest.FLASH_MODE,CaptureRequest.FLASH_MODE_TORCH);
        mPreviewBuilder.set(CaptureRequest.CONTROL_AE_MODE,CaptureRequest.CONTROL_AE_MODE_ON);

        HandlerThread thread = new HandlerThread("CameraPreview");
        thread.start();
        Handler backgroundHandler = new Handler(thread.getLooper());
        try{
            //getCameraPreviews
            mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(),null,backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        //createCameraPreviewSettion();
    }

    protected void takePicture(final Points points){
        //Toast.makeText(CameraActivity.this,"それは無理＾＾；",Toast.LENGTH_LONG).show();
        //return;
        if(mCameraDevice == null) return;
        CameraManager manager = (CameraManager)getSystemService(Context.CAMERA_SERVICE);

        try{
            CameraCharacteristics character = manager.getCameraCharacteristics(mCameraDevice.getId());

            Size[] jpgSizes = null;
            int width =640;
            int height = 480;
            if(character != null){
                jpgSizes = character.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
                if(jpgSizes != null && 0 < jpgSizes.length){
                    width = jpgSizes[0].getWidth();
                    height = jpgSizes[0].getHeight();

                }
            }
            ImageReader reader = ImageReader.newInstance(width,height,ImageFormat.JPEG,2);

            List outputSurface = new ArrayList(2);
            outputSurface.add(reader.getSurface());
            outputSurface.add(new Surface(mTextureView.getSurfaceTexture()));
            final CaptureRequest.Builder captureBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE,CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            //captureBuilder.set(CaptureRequest.FLASH_MODE,CaptureRequest.FLASH_MODE_TORCH);
            captureBuilder.set(CaptureRequest.CONTROL_AE_MODE,CaptureRequest.CONTROL_AE_MODE_ON);

            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener(){

                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image image = null;
                    try{
                        image = reader.acquireLatestImage();
                        Thread process;
                        if(isFrist) {
                            isFrist = false;
                            CHAR_POINTS = new Points();
                            process = new Thread(new CVprocess(image, points, handler));
                        } else{
                            if (CHAR_POINTS == null)System.out.println("NULL!");
                            process = new Thread(new CVprocess(image,CHAR_POINTS, handler));
                        }
                        process.start();
                        image.close();

                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            HandlerThread thread = new HandlerThread("Camera");
            thread.start();
            final Handler backGround = new Handler(thread.getLooper());
            reader.setOnImageAvailableListener(readerListener,backGround);
            //mPreviewSession.stopRepeating();
            //mPreviewSession.capture(captureBuilder.build(),null,backGround);

            final CameraCaptureSession.CaptureCallback mCaptureCallback = new CameraCaptureSession.CaptureCallback(){
                @Override
                public void onCaptureCompleted(CameraCaptureSession session,CaptureRequest request,TotalCaptureResult result){
                    super.onCaptureCompleted(session,request,result);
                    System.out.println("Taking picture is completed");
                    createCameraPreviewSettion();

                }

            };

            mCameraDevice.createCaptureSession(outputSurface,new CameraCaptureSession.StateCallback(){

                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try{
                        session.capture(captureBuilder.build(),mCaptureCallback,backGround);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {

                }
            },backGround);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }
    private Bitmap loadBitmap(String file){
        Bitmap bitmap = null;
        BufferedInputStream bis = null;
        try{
            bis = new BufferedInputStream(new FileInputStream("/" + Environment.getExternalStorageDirectory() + "/Pictures/" + file));
            bitmap = BitmapFactory.decodeStream(bis);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return bitmap;
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

    static public void processedFunc(Bitmap bitmapData,String number){
        writeBitmap(bitmapData,"newBitmap.jpg");
        writeNumber(number,"ResultFile.txt");
    }

    public static void writeNumber(String number, String filename){
        String path = "/" + Environment.getExternalStorageDirectory() + "/7segOCRresult/" + filename;
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(path))));
            pw.println("Recognition Number : " + number);
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public boolean onTouchEvent(MotionEvent event){
        if(!flag) {
            if(event.getActionMasked() == MotionEvent.ACTION_DOWN){
                MinX = (int)event.getX();
                MinY = (int)event.getY();
            }
            if(event.getActionMasked() == MotionEvent.ACTION_UP){
                MaxX = (int)event.getX();
                MaxY = (int)event.getY();
                Points point = new Points(MaxX,MaxY,MinX,MinY,true);
                (new TakeThread(point)).start();
                flag = true;
            }
        } else {
            threadFlag = false;
            System.out.println("threadFlag : false");
        }

        return true;
    }
    public Bitmap getViewBitmap(View view){
        view.setDrawingCacheEnabled(true);
        Bitmap cache = view.getDrawingCache();
        if(cache == null){
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(cache);
        view.setDrawingCacheEnabled(false);
        System.out.println("Taked picture.");
        return bitmap;
    }
    public Bitmap getScreenBitmap(View view){
        return getViewBitmap(view.getRootView());
    }

    private class TakeThread extends Thread{
        Points points;
        private TakeThread(Points points){
            this.points = points;
        }

        @Override
        public void run(){
            while (threadFlag){

                Log.d("CameraActivity","isProcessed " + isProcessed);
                if(isProcessed) {
                    isProcessed = false;
                    writeNumber(number,"resultfile.txt");
                    takePicture(points);
                }
                speechText();
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            startActivity(new Intent(CameraActivity.this, MainActivity.class));
        }

    }
    @Override
    public void onInit(int status) {
        if(TextToSpeech.SUCCESS == status){
            Locale locale = Locale.JAPANESE;
            if(tts.isLanguageAvailable(locale) >= TextToSpeech.LANG_AVAILABLE){
                tts.setLanguage(locale);
            } else{
                Log.d("CVprocess","Error SetLocale");
            }
        }
    }

    public void speechText(){
        System.out.println(number);
        if(number != null){
            if(tts.isSpeaking()){
                tts.stop();
            }
            tts.speak(number,TextToSpeech.QUEUE_FLUSH,null,"1");
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(tts != null){
            tts.shutdown();
        }
    }

}

