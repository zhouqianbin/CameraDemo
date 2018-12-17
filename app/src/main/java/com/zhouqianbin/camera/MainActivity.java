package com.zhouqianbin.camera;

import android.Manifest;
import android.graphics.ImageFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    SurfaceView mSurfaceView;
    CameraManager cameraManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSurfaceView = findViewById(R.id.surf_view);
        cameraManager = new CameraManager();

        findViewById(R.id.btn_take_picture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraManager.takePicture(true, new OnTakePictureResult() {
                    @Override
                    public void onTakePicture(byte[] bytes) {
                        Log.d(TAG,"图片数据 " + bytes);
                    }
                });
            }
        });

        findViewById(R.id.btn_change_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraManager.switchCamera();
            }
        });

        findViewById(R.id.btn_turn_on_light).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraManager.turnOnFlash();
            }
        });

        findViewById(R.id.btn_turn_off_light).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraManager.turnOffFlash();
            }
        });

        findViewById(R.id.btn_start_preview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraManager.startPreview();
            }
        });

        findViewById(R.id.btn_stop_preview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraManager.stopPreview();
            }
        });

        findViewById(R.id.btn_is_support_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,String.valueOf(cameraManager.isSupportCamera(MainActivity.this)));
            }
        });

        findViewById(R.id.btn_default_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"默认的摄像头 " + cameraManager.getDefaultCameraId());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        AndPermission.with(this)
                .runtime()
                .permission(Manifest.permission.CAMERA)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        Log.d(TAG,"权限获取成功");
                        openCamera();
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        Log.d(TAG,"权限获取失败");
                    }
                })
                .start();
    }


    private void openCamera() {
        CameraParamet cameraParamet = new CameraParamet.Builder()
                .setCameraId(1)
                .setImageFormat(ImageFormat.JPEG)
                .setPictureSize(1080,1920)
                .setPreviewSize(1080,1920)
                .setSurfaceView(mSurfaceView)
                .Build();
        cameraManager.openCamera(MainActivity.this, cameraParamet, new OnCameraInitState() {
            @Override
            public void oppenSuccess() {
                Log.d(TAG,"摄像头打开成功");
                cameraManager.setPreviewFrameCallBack(new OnCameraPreviewFrame() {
                    @Override
                    public void onPreviewFrame(byte[] bytes) {
                        Log.d(TAG,"实时预览数据 " + bytes);
                    }
                });
            }

            @Override
            public void oppenError(String errorMsg) {
                Log.d(TAG,"摄像头打开失败");
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraManager.closeCamera();
    }



}
