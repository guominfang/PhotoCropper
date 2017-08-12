package com.kwok.takephoto;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;

/**
 * @author gmf
 * @description 获取图片工具类
 * @date 2017/8/11.
 */
public class TakeHelper {

    private static final String TAG = "TakeHelper";

    /**
     * @param params 参数
     * @return 打开相机Intent
     */
    public static Intent buildCameraIntent(TakeParam params) {
        params.createCameraFile();
        return new Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                .putExtra(MediaStore.EXTRA_OUTPUT, params.mCameraUri);
    }

    /**
     * @param params 参数
     * @return 7.0 打开相机Intent
     */
    public static Intent buildCameraIntent7(TakeParam params) {
        params.createCameraFile7();
        return new Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .putExtra(MediaStore.EXTRA_OUTPUT, params.mCameraUri);
    }

    /**
     * @return 打开相册Intent
     */
    public static Intent buildAlbumIntent() {
        return new Intent("android.intent.action.GET_CONTENT")
                .setType("image/*");
    }

    /**
     * @param file  剪切原文件
     * @param param 剪切参数
     * @return 剪切Intent
     */
    private static Intent buildCropIntent(File file, TakeParam param) {
        param.createCropFile();
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(FileUriUtil.getImageContentUri(param.mContext, file), param.type);
        intent.putExtra("crop", param.crop);
        intent.putExtra("aspectX", param.aspectX);
        intent.putExtra("aspectY", param.aspectY);
        intent.putExtra("outputX", param.outputX);
        intent.putExtra("outputY", param.outputY);
        intent.putExtra("scale", param.scale);
        intent.putExtra("return-data", param.returnData);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, param.mCropUri);//定义输出的File Uri
        intent.putExtra("outputFormat", param.outputFormat);
        intent.putExtra("noFaceDetection", param.noFaceDetection);
        return intent;
    }

    /**
     * @param handler     回调
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public static void handleResult(@NonNull ITakePhotoListener handler, int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_CANCELED) {
            handler.onCancel();
            return;
        }

        if (resultCode != Activity.RESULT_OK) {
            handler.onFailed();
            Log.e(TAG, "resultCode is not RESULT_OK");
            return;
        }

        handleResult(handler, requestCode, data);
    }

    private static void handleResult(ITakePhotoListener handler, int requestCode, Intent data) {
        TakeParam param = handler.getTakeParam();
        if (param == null) {
            Log.e(TAG, "ITakePhotoListener's TakeParam MUST NOT be null!");
            handler.onFailed();
            return;
        }
        if (requestCode == param.REQUEST_CODE_CAMERA) {//相机
            handleCameraResult(handler, param);
        } else if (requestCode == param.REQUEST_CODE_ALBUM) {//相册
            handleAlbumResult(handler, param, data);
        } else if (requestCode == param.REQUEST_CODE_CROP) {//剪切
            handleCropResult(handler, param);
        }
    }

    private static void handleCropResult(ITakePhotoListener handler, TakeParam param) {
        onTakePhoto(handler, param, param.mCropUri);
    }

    private static void handleCameraResult(ITakePhotoListener handler, TakeParam param) {
        if (param.isCrop) {
            handler.startCropIntent(buildCropIntent(param.mCameraFile, param));
        } else {
            onTakePhoto(handler, param, param.mCameraUri);
        }
    }

    private static void handleAlbumResult(ITakePhotoListener handler, TakeParam param, Intent data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 4.4及以上系统使用这个方法处理图片
            handleImageOnKitKat(handler, param, data);
        } else {
            // 4.4以下系统使用这个方法处理图片
            handleImageBeforeKitKat(handler, param, data);
        }
    }

    @TargetApi(19)
    private static void handleImageOnKitKat(ITakePhotoListener handler, TakeParam param, Intent data) {
        Uri uri = data.getData();
        Log.d(TAG, "handleImageOnKitKat: uri is " + uri);

        if (param.isCrop) {
            String imagePath = FileUriUtil.uriToPath(param.mContext, uri);
            Log.i(TAG, "file://" + imagePath + "选择图片的URI" + uri);
            handler.startCropIntent(buildCropIntent(new File(imagePath), param));
        } else {
            onTakePhoto(handler, param, uri);
        }
    }

    private static void handleImageBeforeKitKat(ITakePhotoListener handler, TakeParam param, Intent data) {
        Uri uri = data.getData();

        if (param.isCrop) {
            String imagePath = FileUriUtil.getImagePath(param.mContext, uri, null);
            Log.i(TAG, "file://" + imagePath + "选择图片的URI" + uri);
            handler.startCropIntent(buildCropIntent(new File(imagePath), param));
        } else {
            onTakePhoto(handler, param, uri);
        }
    }

    private static void onTakePhoto(ITakePhotoListener handler, TakeParam param, Uri uri) {
        if (param.isCompress) {
            param.createCompressFile();
            CompressImageUtil.compressImageFile(param, uri, param.mCompressUri);
            handler.onComplete(param.mCompressUri);
        } else {
            handler.onComplete(uri);
        }
    }

    public static void clearFile(Context context) {
        File fileDir = new File(CacheFileUtil.getDiskCacheDir(context));
        if (fileDir.exists()) {
            fileDir.delete();
        }
    }
}
