package com.kwok.takephoto;

import android.content.Context;

import java.io.File;

/**
 * @author gmf
 * @description 获取照片参数
 * @date 2017/8/11.
 */
public class TakeParam {

    protected Context mContext;

    private String mCropName;
    private String mCameraName;

    public File mCropFile;
    public File mCameraFile;

    // 是否需要剪切
    public boolean isCrop;

    /**
     * RequestCode 如果用冲突，可以修改
     */
    public int REQUEST_CODE_CAMERA = 7;
    public int REQUEST_CODE_ALBUM = 8;
    public int REQUEST_CODE_CROP = 9;

    public TakeParam(Context context) {
        mContext = context;

        long currentTime = System.currentTimeMillis();
        mCropName = currentTime + "_crop_image.jpg";
        mCameraName = currentTime + "_output_image.jpg";

        isCrop = true;
    }

    protected void createCameraFile() {
        mCameraFile = CacheFileUtil.getCacheFile(mContext, mCameraName);
    }

    protected void createCropFile() {
        mCropFile = CacheFileUtil.getCacheFile(mContext, mCropName);
    }

}
