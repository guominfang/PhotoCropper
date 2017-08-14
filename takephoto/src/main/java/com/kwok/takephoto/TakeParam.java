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
    private static final int DEFAULT_REQUEST_CODE_CAMERA = 7;
    private static final int DEFAULT_REQUEST_CODE_ALBUM = 8;
    private static final int DEFAULT_REQUEST_CODE_CROP = 9;

    /**
     * RequestCode 如果用冲突，可以修改
     */
    public int REQUEST_CODE_CAMERA;
    public int REQUEST_CODE_ALBUM;
    public int REQUEST_CODE_CROP;

    Context mContext;

    private String mCropName;   //剪切文件名称
    private String mCameraName; //拍照文件名称
    private String mCompressName; //压缩文件名称

    File mCropFile;   //剪切文件
    File mCameraFile; //拍照文件
    File mCompressFile; //压缩文件

    Uri mCropUri;     //剪切文件Uri
    Uri mCameraUri;   //拍照文件Uri
    Uri mCompressUri; //压缩文件Uri

    String type;
    String outputFormat;  //输出格式，一般设为Bitmap格式：Bitmap.CompressFormat.JPEG.toString()
    String crop;          //发送裁剪信号

    boolean scale;    //是否保留比例
    boolean returnData;   //是否将数据保留在Bitmap中返回
    boolean noFaceDetection;//是否取消人脸识别功能

    int aspectX; //X方向上的比例
    int aspectY; //Y方向上的比例

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

    /**
     * 压缩宽度
     */
    public int compressWidth;
    /**
     * 压缩高度
     */
    public int compressHeight;
    /**
     * 压缩质量
     */
    public int compressQuality;


    public TakeParam(Context context) {
        mContext = context;

        long currentTime = System.currentTimeMillis();
        mCropName = currentTime + "_crop_image.jpg";
        mCameraName = currentTime + "_camera_image.jpg";
        mCompressName = currentTime + "_compress_image.jpg";

        REQUEST_CODE_CAMERA = DEFAULT_REQUEST_CODE_CAMERA;
        REQUEST_CODE_ALBUM = DEFAULT_REQUEST_CODE_ALBUM;
        REQUEST_CODE_CROP = DEFAULT_REQUEST_CODE_CROP;

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
        isCompress = true;

        compressQuality = DEFAULT_COMPRESS_QUALITY;
        compressWidth = DEFAULT_COMPRESS_WIDTH;
        compressHeight = DEFAULT_COMPRESS_HEIGHT;
    }

    void createCameraFile() {
        mCameraFile = CacheFileUtil.getCacheFile(mContext, mCameraName);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mCameraUri = Uri.fromFile(mCameraFile);
        } else {//android 7.0以上
            String authority = mContext.getPackageName() + AUTHORITY;
            mCameraUri = FileProvider.getUriForFile(mContext, authority, mCameraFile);
        }
    }

    void createCropFile() {
        mCropFile = CacheFileUtil.getCacheFile(mContext, mCropName);
        mCropUri = Uri.fromFile(mCropFile);
    }

    void createCompressFile() {
        mCompressFile = CacheFileUtil.getCacheFile(mContext, mCompressName);
        mCompressUri = Uri.fromFile(mCompressFile);
    }
}
