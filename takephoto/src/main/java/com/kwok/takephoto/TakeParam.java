package com.kwok.takephoto;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import java.io.File;

/**
 * @author gmf
 * @description 获取照片参数
 * @date 2017/8/11.
 */
public class TakeParam {

    private static final String AUTHORITY = ".fileprovider";
    private static final String CROP_TYPE = "image/*";
    private static final String OUTPUT_FORMAT = Bitmap.CompressFormat.JPEG.toString();
    private static final int DEFAULT_ASPECT = 1;
    private static final int DEFAULT_OUTPUT = 300;
    private static final int DEFAULT_COMPRESS_WIDTH = 640;
    private static final int DEFAULT_COMPRESS_HEIGHT = 854;
    private static final int DEFAULT_COMPRESS_QUALITY = 90;

    /**
     * RequestCode 如果用冲突，可以修改
     */
    public int REQUEST_CODE_CAMERA = 7;
    public int REQUEST_CODE_ALBUM = 8;
    public int REQUEST_CODE_CROP = 9;

    protected Context mContext;

    private String mCropName;   //剪切文件名称
    private String mCameraName; //拍照文件名称
    private String mCompressName; //压缩文件名称

    protected File mCropFile;   //剪切文件
    protected File mCameraFile; //拍照文件
    protected File mCompressFile; //拍照文件

    protected Uri mCropUri;     //剪切文件Uri
    protected Uri mCameraUri;   //拍照文件Uri
    protected Uri mCompressUri; //压缩文件Uri

    protected String type;
    protected String outputFormat;  //输出格式，一般设为Bitmap格式：Bitmap.CompressFormat.JPEG.toString()
    protected String crop;          //发送裁剪信号

    protected boolean scale;    //是否保留比例
    protected boolean returnData;   //是否将数据保留在Bitmap中返回
    protected boolean noFaceDetection;//是否取消人脸识别功能


    protected int aspectX; //X方向上的比例
    protected int aspectY; //Y方向上的比例


    public int outputX; //裁剪区的宽
    public int outputY; //裁剪区的高

    /**
     * 是否需要剪切 默认为：true
     */
    public boolean isCrop;

    /**
     * 是否需要压缩 默认为：false
     */
    public boolean isCompress;

    public int compressWidth;
    public int compressHeight;
    public int compressQuality;


    public TakeParam(Context context) {
        mContext = context;

        long currentTime = System.currentTimeMillis();
        mCropName = currentTime + "_crop_image.jpg";
        mCameraName = currentTime + "_camera_image.jpg";
        mCompressName = currentTime + "_compress_image.jpg";

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

        compressQuality = DEFAULT_COMPRESS_QUALITY;
        compressWidth = DEFAULT_COMPRESS_WIDTH;
        compressHeight = DEFAULT_COMPRESS_HEIGHT;
    }

    protected void createCameraFile() {
        mCameraFile = CacheFileUtil.getCacheFile(mContext, mCameraName);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mCameraUri = Uri.fromFile(mCameraFile);
        } else {//android 7.0以上
            String authority = mContext.getPackageName() + AUTHORITY;
            mCameraUri = FileProvider.getUriForFile(mContext, authority, mCameraFile);
        }
    }

    protected void createCropFile() {
        mCropFile = CacheFileUtil.getCacheFile(mContext, mCropName);
        mCropUri = Uri.fromFile(mCropFile);
    }

    protected void createCompressFile() {
        mCompressFile = CacheFileUtil.getCacheFile(mContext, mCompressName);
        mCompressUri = Uri.fromFile(mCompressFile);
    }
}
