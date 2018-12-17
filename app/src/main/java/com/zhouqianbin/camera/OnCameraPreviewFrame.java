package com.zhouqianbin.camera;

/**
 * @Copyright (C), 2018, 漳州科能电器有限公司
 * @FileName: OnCameraPreviewFrame
 * @Author: 周千滨
 * @Date: 2018/12/10 15:50
 * @Description:
 * @Version: 1.0.0
 * @UpdateHistory: 修改历史
 * @修改人: 周千滨
 * @修改描述: 创建文件
 */

public interface OnCameraPreviewFrame {

    void onPreviewFrame(byte[] bytes);
}
