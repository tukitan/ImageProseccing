package com.example.tukitan.takepicture;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.*;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Size;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CameraActivity extends Activity {

    private TextureView mTextureView;
    private Button mButton;
    private Size mPreviewSize;
    private CameraDevice mCameraDevice;
    private CaptureRequest.Builder mPreviewBuilder;
    private CameraCaptureSession mPreviewSession;
    private double X,Y;
    boolean flag = false;

    @Override
    protected void onCreate(Bundle SavedInstance) {
        super.onCreate(SavedInstance);

        //full Screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera);

        mTextureView = (TextureView) findViewById(R.id.textureView);
        mTextureView.setSurfaceTextureListener(mCameraViewStatusChanged);

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
    protected void takePicture(){
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
            captureBuilder.set(CaptureRequest.CONTROL_AE_MODE,CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);

            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener(){

                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image image = null;
                    try{
                        image = reader.acquireLatestImage();

                        //System.out.println("KOKO");
                        Recognition obj = new Recognition(getApplication(),image,X,Y);
                        image.close();
                        obj.recognize();
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            HandlerThread thread = new HandlerThread("Camera");
            thread.start();
            final Handler backGround = new Handler(thread.getLooper());
            reader.setOnImageAvailableListener(readerListener,backGround);
            mPreviewSession.stopRepeating();
            //mPreviewSession.capture(captureBuilder.build(),null,backGround);

            final CameraCaptureSession.CaptureCallback mCaptureCallback = new CameraCaptureSession.CaptureCallback(){
                @Override
                public void onCaptureCompleted(CameraCaptureSession session,CaptureRequest request,TotalCaptureResult result){
                    super.onCaptureCompleted(session,request,result);
                    System.out.println("Taking picture is completed");


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
    public boolean onTouchEvent(MotionEvent event){
        X = (double)event.getX();
        Y = (double)event.getY();
        System.out.println("X:" + X + ",Y:" + Y);
        if(!flag){
            flag = true;
            takePicture();
        }
        return true;
    }

}

