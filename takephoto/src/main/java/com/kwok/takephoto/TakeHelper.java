package com.kwok.takephoto;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;

/**
 * @author gmf
 * @description
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
                .putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(params.mCameraFile));
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
        intent.setDataAndType(FileUrlUtil.getImageContentUri(param.mContext, file), "image/*");//自己使用Content Uri替换File Uri
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 180);
        intent.putExtra("outputY", 180);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(param.mCropFile));//定义输出的File Uri
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        return intent;
    }

    /**
     * @param handler     回调
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public static void handleResult(@NonNull ITakePhotoListener handler, int requestCode, int resultCode, Intent data) {
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
        handler.onComplete(Uri.fromFile(param.mCropFile));
    }

    private static void handleCameraResult(ITakePhotoListener handler, TakeParam param) {
        if (param.isCrop) {
            handler.startCropIntent(buildCropIntent(param.mCameraFile, param));
        } else {
            handler.onComplete(Uri.fromFile(param.mCameraFile));
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

    private static void handleImageOnKitKat(ITakePhotoListener handler, TakeParam param, Intent data) {
        Uri uri = data.getData();
        Log.d(TAG, "handleImageOnKitKat: uri is " + uri);

        if (param.isCrop) {
            String imagePath = uriToPath(param.mContext, uri);
            Log.i(TAG, "file://" + imagePath + "选择图片的URI" + uri);
            handler.startCropIntent(buildCropIntent(new File(imagePath), param));
        } else {
            handler.onComplete(uri);
        }
    }

    @TargetApi(19)
    private static String uriToPath(Context context, Uri uri) {
        String path = null;
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                path = getImagePath(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                path = getImagePath(context, contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            path = getImagePath(context, uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            path = uri.getPath();
        }
        return path;
    }

    private static void handleImageBeforeKitKat(ITakePhotoListener handler, TakeParam param, Intent data) {
        Uri uri = data.getData();

        if (param.isCrop) {
            String imagePath = getImagePath(param.mContext, uri, null);
            Log.i(TAG, "file://" + imagePath + "选择图片的URI" + uri);
            handler.startCropIntent(buildCropIntent(new File(imagePath), param));
        } else {
            handler.onComplete(uri);
        }
    }

    private static String getImagePath(Context context, Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = context.getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

}
