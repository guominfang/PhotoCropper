package com.kwok.takephoto;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import java.io.File;

/**
 * @author gmf
 * @description 获取照片参数
 * @date 2017/8/11.
 */
public class TakeParam {

    public static final String CROP_TYPE = "image/*";
    public static final String OUTPUT_FORMAT = Bitmap.CompressFormat.JPEG.toString();

    private static final int DEFAULT_ASPECT = 1;
    private static final int DEFAULT_OUTPUT = 300;

    /**
     * RequestCode 如果用冲突，可以修改
     */
    public int REQUEST_CODE_CAMERA = 7;
    public int REQUEST_CODE_ALBUM = 8;
    public int REQUEST_CODE_CROP = 9;

    protected Context mContext;

    private String mCropName;
    private String mCameraName;

    protected File mCropFile;
    protected File mCameraFile;

    protected Uri mCropUri;
    protected Uri mCameraUri;

    protected String type;
    protected String outputFormat;
    protected String crop;

    protected boolean scale;
    protected boolean returnData;
    protected boolean noFaceDetection;

    protected int aspectX;
    protected int aspectY;

    public int outputX;
    public int outputY;

    // 是否需要剪切
    public boolean isCrop;

    // 是否压缩
    public boolean isCompress;

    public TakeParam(Context context) {
        mContext = context;

        long currentTime = System.currentTimeMillis();
        mCropName = currentTime + "_crop_image.jpg";
        mCameraName = currentTime + "_output_image.jpg";

        type = CROP_TYPE;
        outputFormat = OUTPUT_FORMAT;
        crop = "true";

        scale = true;
        returnData = false;
        noFaceDetection = true;
        aspectX = DEFAULT_ASPECT;
        aspectY = DEFAULT_ASPECT;
        outputX = DEFAULT_OUTPUT;
        outputY = DEFAULT_OUTPUT;

        isCrop = true;
        isCompress = false;
    }

    protected void createCameraFile() {
        mCameraFile = CacheFileUtil.getCacheFile(mContext, mCameraName);
        mCameraUri = Uri.fromFile(mCameraFile);
    }

    protected void createCropFile() {
        mCropFile = CacheFileUtil.getCacheFile(mContext, mCropName);
        mCropUri = Uri.fromFile(mCropFile);
    }

}
