package com.zhouqianbin.camera;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @Copyright (C), 2018, 漳州科能电器有限公司
 * @FileName: CameraManager
 * @Author: 周千滨
 * @Date: 2018/12/10 14:33
 * @Description:  图像变形,自定义预览形状,TextureView
 * @Version: 1.0.0
 * @UpdateHistory: 修改历史
 * @修改人: 周千滨
 * @修改描述: 创建文件
 */

public class CameraManager implements Camera.PreviewCallback,SurfaceHolder.Callback{

    private static final String TAG = CameraManager.class.getSimpleName();

    private Camera mCamera;
    private int mCameraId;  //当前摄像头id
    private SurfaceHolder mSurfaceHolder;
    private int mPreviewWidth;
    private int mPreviewHeight;
    private int mPictureWidth;
    private int mPictureHeight;
    private int mCameraAngle;  //当前摄像头旋转的角度
    private Activity mActivity;
    private int mImageFormat;

    /**
     * 打开摄像头
     */
    public void openCamera(Activity activity,CameraParamet cameraParamet,
                           OnCameraInitState cameraInitCallBack){
        Log.d(TAG, "openCamera");
        int checkSelfPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        if(checkSelfPermission != PackageManager.PERMISSION_GRANTED){
            Log.d(TAG,"请打开摄像头权限");
            cameraInitCallBack.oppenError("请打开Camera权限");
            return;
        }
        this.mActivity = activity;
        mCameraId = cameraParamet.getmCameraId();
        mPreviewWidth = cameraParamet.getmPreviewWidth();
        mPreviewHeight = cameraParamet.getmPreviewHeight();
        mPictureWidth = cameraParamet.getmPictureWidth();
        mPictureHeight = cameraParamet.getmPictureHeight();
        mImageFormat = cameraParamet.getmImageFormat();
        mSurfaceHolder = cameraParamet.getmSurfaceView().getHolder();
        initCamera(cameraInitCallBack);
        setCameraConfig();
    }

    private void initCamera(OnCameraInitState cameraInitCallBack) {
        Log.d(TAG,"摄像头个数 " + Camera.getNumberOfCameras());
        if (null == mCamera) {
            try {
                mCamera = Camera.open(mCameraId);
                if (null != cameraInitCallBack) {
                    if (null != mCamera) {
                        Log.d(TAG, "onSuccess");
                        cameraInitCallBack.oppenSuccess();
                    } else {
                        Log.d(TAG, "onError");
                        cameraInitCallBack.oppenError("获取的Camera实例为null");
                    }
                }
            } catch (Exception e) {
                if (null != cameraInitCallBack) {
                    Log.d(TAG, "onError");
                    cameraInitCallBack.oppenError("摄像头正在使用或该设备" +
                            "没有摄像头");
                }
                //尝试打开另外一个摄像头
                Camera.open(mCameraId==0?1:0);
            }
        }
    }


    /**
     * 关闭摄像头
     */
    public void closeCamera(){
        Log.d(TAG, "closeCamera");
        if (null != mSurfaceHolder) {
            mSurfaceHolder.removeCallback(this);
        }
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        if (null != mActivity) {
            mActivity = null;
        }
    }


    /**
     * 设置摄像头参数
     */
    private void setCameraConfig(){
        Log.d(TAG, "setCameraConfig");
        mSurfaceHolder.addCallback(this);
        Camera.Parameters params = mCamera.getParameters();
        Camera.Size bestPreviewSize = calBestPreviewSize(
                mCamera.getParameters(),
                mPreviewWidth,
                mPreviewHeight);
        mPreviewWidth = bestPreviewSize.width;
        mPreviewHeight = bestPreviewSize.height;
        Log.d(TAG,"PreviewWidth " + mPreviewWidth + " mPreviewHeight" + mPreviewHeight);
        Camera.Size bestPictureSize = calBestPictureSize(
                mCamera.getParameters(),
                mPictureWidth,
                mPictureHeight);
        mPictureWidth = bestPictureSize.width;
        mPictureHeight = bestPictureSize.height;
        Log.d(TAG,"PictureWidth " + mPictureWidth + "  PictureHeight " + mPictureHeight);
        //预览的大小，也就是分辨率，越小越模糊. 设置的时候需要根据设备支持的分辨率设置
        params.setPreviewSize(mPreviewWidth, mPreviewHeight);
        params.setPictureFormat(mImageFormat);
        params.setPictureSize(mPictureWidth,mPictureHeight);
        mCamera.setParameters(params);
    }


    /**
     * 设置参数
     * @param parameters
     */
    public void setParameters(Camera.Parameters parameters){
        if(null == mCamera){
            return;
        }
        mCamera.setParameters(parameters);
    }


    /**、
     * 设置照相机旋转角度
     * @param cameraId
     * @param camera
     */
    private void setCameraDisplayOrientation(int cameraId, Camera camera) {
        Log.d(TAG, "startPreview setCameraDisplayOrientation");
        Camera.CameraInfo info =
                new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = mActivity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int mDegrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                mDegrees = 0;
                break;
            case Surface.ROTATION_90:
                mDegrees = 90;
                break;
            case Surface.ROTATION_180:
                mDegrees = 180;
                break;
            case Surface.ROTATION_270:
                mDegrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + mDegrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - mDegrees + 360) % 360;
        }
        mCameraAngle = result;
        Log.d(TAG, "Camera当前角度 " + mCameraAngle);
        camera.setDisplayOrientation(mCameraAngle);
    }


    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        //Log.d(TAG,"图像数据 "+data);
        if(null != mCameraPreviewFrame){
            mCameraPreviewFrame.onPreviewFrame(data);
        }
    }


    /**
     * 通过传入的宽高算出最接近于宽高值的相机大小
     */
    private Camera.Size calBestPreviewSize(Camera.Parameters camPara,
                                           final int width, final int height) {
        List<Camera.Size> allSupportedSize = camPara.getSupportedPreviewSizes();
        ArrayList<Camera.Size> widthLargerSize = new ArrayList<Camera.Size>();
        for (Camera.Size tmpSize : allSupportedSize) {
            Log.d("支持的预览大小 ", "width===" + tmpSize.width
                    + ", tmpSize.height===" + tmpSize.height);
            if (tmpSize.width > tmpSize.height) {
                widthLargerSize.add(tmpSize);
            }
        }

        Collections.sort(widthLargerSize, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size lhs, Camera.Size rhs) {
                int off_one = Math.abs(lhs.width * lhs.height - width * height);
                int off_two = Math.abs(rhs.width * rhs.height - width * height);
                return off_one - off_two;
            }
        });

        return widthLargerSize.get(0);
    }
    private Camera.Size calBestPictureSize(Camera.Parameters camPara,
                                           final int width, final int height) {

        List<Camera.Size> allSupportedSize = camPara.getSupportedPictureSizes();
        ArrayList<Camera.Size> widthLargerSize = new ArrayList<Camera.Size>();
        for (Camera.Size tmpSize : allSupportedSize) {
            Log.d("支持的图片大小 ", "width===" + tmpSize.width
                    + ", tmpSize.height===" + tmpSize.height);
            if (tmpSize.width > tmpSize.height) {
                widthLargerSize.add(tmpSize);
            }
        }

        Collections.sort(widthLargerSize, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size lhs, Camera.Size rhs) {
                int off_one = Math.abs(lhs.width * lhs.height - width * height);
                int off_two = Math.abs(rhs.width * rhs.height - width * height);
                return off_one - off_two;
            }
        });

        return widthLargerSize.get(0);
    }



    /************************************公共方法区************************************/

    /**
     * 获取默认的id 默认前摄像头
     * @return
     */
    public int getDefaultCameraId() {
        int defaultId = -1;
        int mNumberOfCameras = Camera.getNumberOfCameras();
        Log.d(TAG,"摄像头个数 " + mNumberOfCameras);
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < mNumberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                defaultId = i;
            }
        }
        if (-1 == defaultId) {
            if (mNumberOfCameras > 0) {
                defaultId = 0;
            } else {
            }
        }
        return defaultId;
    }

    /**
     * 判断该设备是否支持摄像头
     * @param context
     * @return
     */
    public boolean isSupportCamera(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 开始预览
     */
    public void startPreview(){
        if(null == mCamera){
            return;
        }
        mCamera.startPreview();
    }

    /**
     * 结束预览
     */
    public void stopPreview(){
        if(null == mCamera){
            return;
        }
        mCamera.stopPreview();
    }



    /**
     * 切换摄像头
     */
    public void switchCamera() {
        Log.d(TAG, "switchCamera");
        mCameraId = (mCameraId + 1) % Camera.getNumberOfCameras();
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        initCamera(null);
        createPreview(mSurfaceHolder);
    }


    /**
     * 获取预览的宽度
     * @return
     */
    public int getPreviewWidth(){
        return mPreviewWidth;
    }

    /**
     * 获取预览的高度
     * @return
     */
    public int getPreviewHeight(){
        return mPreviewHeight;
    }

    private OnCameraPreviewFrame mCameraPreviewFrame;
    public void setPreviewFrameCallBack(OnCameraPreviewFrame cameraPreviewFrame){
        this.mCameraPreviewFrame = cameraPreviewFrame;
    }

    /**
     * 拍照
     */
    public void takePicture(final boolean isRefresh, final OnTakePictureResult takePictureResult){
        if(null == mCamera){
            return;
        }
        //是否支持对焦
        Camera.Parameters parameters = mCamera.getParameters();
        List<String> focusModes = parameters.getSupportedFocusModes();
        if(focusModes.size() > 0) {
            hasFocusTakePicture(isRefresh,takePictureResult);
        }
        else {
            notFocusTakePicture(takePictureResult);
        }
    }


    /**
     * 带对焦拍照
     */
    private void hasFocusTakePicture(final boolean isRefresh, final OnTakePictureResult takePictureResult){
        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if(success){
                    mCamera.takePicture(null, null, new Camera.PictureCallback() {
                        @Override
                        public void onPictureTaken(byte[] data, Camera camera) {
                            if(null != takePictureResult){
                                takePictureResult.onTakePicture(data);
                            }
                            if(isRefresh){
                                camera.startPreview();
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 不带对焦拍照
     */
    private void notFocusTakePicture(final OnTakePictureResult takePictureResult){
        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                if(null != takePictureResult){
                    takePictureResult.onTakePicture(data);
                }
            }
        });
    }

    /**
     * 开启闪光灯(对应当前的摄像头)
     */
    public void turnOnFlash(){
        if(mCamera != null){
            try {
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                mCamera.setParameters(parameters);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭闪光灯
     */
    public void turnOffFlash(){
        if(mCamera != null){
            try {
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(parameters);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    /************************************生命周期**************************************/

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated");
        createPreview(holder);
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged");
        mCamera.stopPreview();
        createPreview(holder);
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed");
        closeCamera();
    }

    /**
     * 开始预览
     */
    private void createPreview(SurfaceHolder holder){
        if(null == mCamera){
            return;
        }
        Log.d(TAG, "startPreview");
        try {
            mCamera.setPreviewDisplay(holder);
            setCameraDisplayOrientation(mCameraId, mCamera);
            mCamera.setPreviewCallback(this);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
