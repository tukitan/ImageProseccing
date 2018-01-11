package com.example.komaki.a7segosr;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
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
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.WindowManager;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CameraActivity extends Activity implements TextToSpeech.OnInitListener{

    private TextureView mTextureView;
    private TextureView drawView;
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

    Thread process;

    private static ArrayList<String> recognitionNumbers;

    // TTS locale
    static Locale LOCALE;

    static double PERIOD;
    static double UNIT;

    int periodTime;

    HashMap LOCATE_MAP;
    HashMap CHAR_SIZE_MAP;

    static int CHAR_SIZE;

    static double WIDTH_RATIO;
    static double HEIGHT_RATIO;
    static int DISP_WIDTH;
    static int DISP_HEIGHT;



    @Override
    protected void onCreate(Bundle SavedInstance) {
        super.onCreate(SavedInstance);

        //full Screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera);

        mTextureView = (TextureView) findViewById(R.id.textureView);
        drawView = (TextureView) findViewById(R.id.drawView);
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
        recognitionNumbers = new ArrayList<>();
        periodTime = (int)PERIOD*1000;

        LOCATE_MAP = new HashMap();
        LOCATE_MAP.put(Locale.JAPANESE,0);
        LOCATE_MAP.put(Locale.ENGLISH,1);

        CHAR_SIZE_MAP = new HashMap();
        CHAR_SIZE_MAP.put(13,50);
        CHAR_SIZE_MAP.put(37,100);

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

        CameraManager manager = (CameraManager)getSystemService(Context.CAMERA_SERVICE);

        try {
            CameraCharacteristics character = manager.getCameraCharacteristics(mCameraDevice.getId());

            Size[] jpgSizes = null;
            int width = 640;
            int height = 480;
            if (character != null) {
                jpgSizes = character.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
                if (jpgSizes != null && 0 < jpgSizes.length) {
                    width = jpgSizes[0].getWidth();
                    height = jpgSizes[0].getHeight();
                }
            }
            WIDTH_RATIO = (double) height / (double) DISP_WIDTH;
            HEIGHT_RATIO = (double) width / (double) DISP_HEIGHT;
            Log.i("CameraActivity", "jpgSize = " + width + "," + height);

        } catch (CameraAccessException e) {
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
        System.out.println("called Thread");
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
                        if(isFrist) {
                            isFrist = false;
                            CHAR_POINTS = new Points();
                            process = new Thread(new CVprocess(image, points, handler,"0"));
                        } else{
                            if (CHAR_POINTS == null)System.out.println("NULL!");
                            process = new Thread(new CVprocess(image,CHAR_POINTS, handler,number));
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


    public void writeNumber(ArrayList<String> number, String filename){
        DateFormat df = new SimpleDateFormat("yyyyMMdd_HH_mm");
        Date date = new Date(System.currentTimeMillis());
        String path = "/" + Environment.getExternalStorageDirectory() + "/7segOCRresult/RESULT/" + df.format(date) + ConfigActivity2.user +".txt";
        File file = new File(path);
        if(!file.exists()){
            file.getParentFile().mkdir();
        }
        int count = 0;
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));

            pw.println((int)UNIT + "," + PERIOD + "," + LOCATE_MAP.get(LOCALE));
            for(String elem :number){
                if(count != 0 ) pw.println(count + "," + elem);
                count++;
            }
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public boolean onTouchEvent(MotionEvent event){
        if(!flag) {
            // Drag Rect type
            /*
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
            */

            // touch type
            if(event.getActionMasked() == MotionEvent.ACTION_UP) {
                int pointX = (int) event.getX();
                int pointY = (int) event.getY();
                int range = CHAR_SIZE * 4;
                //Log.d("CameraActivity","touch point = (" + (pointX + range * 2) + "," + (pointY + range) +"),("+(pointX- range*2)+ ","+(pointY-range)+")");
                // int range = (int) CHAR_SIZE_MAP.get(CHAR_SIZE);
                Points point = new Points(pointX + (range*2), (int) (pointY + (range*1.3)), pointX - (range*2), (int) (pointY - (range*1.3)), true);
                (new TakeThread(point)).start();
                flag = true;
            }


            //Log.i("CameraAcitivy","(X,Y):" +event.getX() + "," + event.getY());
        } else {
            ProgressDialog dialog = new ProgressDialog(this);
            dialog.setIndeterminate(true);
            dialog.setMessage("終了処理中です...");
            dialog.show();
            writeNumber(recognitionNumbers,"result.txt");
            threadFlag = false;
            isFrist = true;
            CHAR_POINTS = null;
            number = null;

        }


        return true;
    }

    private class TakeThread extends Thread{
        Points points;
        private TakeThread(Points points){
            this.points = points;
        }

        @Override
        public void run(){
            while (threadFlag){
                speechText();

                Log.d("CameraActivity","isProcessed " + isProcessed);
                if(isProcessed) {
                    isProcessed = false;
                    recognitionNumbers.add(number);
                    takePicture(points);
                }
                try {
                    Thread.sleep(periodTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            Intent intent = new Intent(CameraActivity.this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }

    }
    @Override
    public void onInit(int status) {
        if(TextToSpeech.SUCCESS == status){
            if(LOCALE == null) LOCALE = Locale.JAPANESE;
            Locale locale = LOCALE;
            if(tts.isLanguageAvailable(locale) >= TextToSpeech.LANG_AVAILABLE){
                tts.setLanguage(locale);
            } else{
                Log.d("CVprocess","Error SetLocale");
            }
        }
    }

    public void speechText(){
        Log.d("speechText()","number is" + number);
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

    @Override
    protected void onStop(){
        super.onStop();
        if (mCameraDevice != null){
            mCameraDevice.close();
            mCameraDevice =null;
        }
        flag = false;
    }

}

