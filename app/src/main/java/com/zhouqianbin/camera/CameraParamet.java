package com.zhouqianbin.camera;

import android.graphics.ImageFormat;
import android.view.SurfaceView;

public class CameraParamet {

    /**
     * 预览视图
     */
    private SurfaceView mSurfaceView;

    /**
     * 摄像头ID
     */
    private int mCameraId;

    /**
     * 预览的宽度
     */
    private int mPreviewWidth;

    /**
     * 预览的高度
     */
    private int mPreviewHeight;

    /**
     * 图片的宽度
     */
    private int mPictureWidth;

    /**
     * 图片的高度
     */
    private int mPictureHeight;

    /**
     * 图片的格式
     */
    private int mImageFormat = ImageFormat.JPEG;

    public SurfaceView getmSurfaceView() {
        return mSurfaceView;
    }

    public int getmCameraId() {
        return mCameraId;
    }

    public int getmPreviewWidth() {
        return mPreviewWidth;
    }

    public int getmPreviewHeight() {
        return mPreviewHeight;
    }

    public int getmImageFormat() {
        return mImageFormat;
    }

    public int getmPictureWidth() {
        return mPictureWidth;
    }

    public int getmPictureHeight() {
        return mPictureHeight;
    }

    @Override
    public String toString() {
        return "CameraParamet{" +
                "mSurfaceView=" + mSurfaceView +
                ", mCameraId=" + mCameraId +
                ", mPreviewWidth=" + mPreviewWidth +
                ", mPreviewHeight=" + mPreviewHeight +
                ", mImageFormat=" + mImageFormat +
                '}';
    }

    public static class Builder{

        private CameraParamet paramet;

        public Builder(){
            paramet = new CameraParamet();
        }

        public Builder setSurfaceView(SurfaceView surfaceView){
            paramet.mSurfaceView = surfaceView;
            return this;
        }

        public Builder setCameraId(int cameraId){
            paramet.mCameraId = cameraId;
            return this;
        }

        public Builder setPreviewSize(int width,int height){
            paramet.mPreviewWidth = width;
            paramet.mPreviewHeight = height;
            return this;
        }

        public Builder setPictureSize(int width,int height){
            paramet.mPictureWidth = width;
            paramet.mPictureHeight = height;
            return this;
        }

        public Builder setImageFormat(int imageFormat){
            paramet.mImageFormat = imageFormat;
            return this;
        }

        public CameraParamet Build(){
            return paramet;
        }
    }

}
