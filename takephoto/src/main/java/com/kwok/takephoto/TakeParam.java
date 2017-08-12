package com.kwok.takephoto;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

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

    public String mCropPath;
    public String mCameraPath;

    public File mCropFile;
    public File mCameraFile;

    // 是否需要剪切
    public boolean isCrop;

    public int requestCamera = 7;
    public int requestAlbum = 8;
    public int requestCrop = 9;

    public TakeParam(Context context) {
        mContext = context;

        long currentTime = System.currentTimeMillis();
        mCropName = currentTime + "_crop_image.jpg";
        mCameraName = currentTime + "_output_image.jpg";

        mCropPath = CacheFileUtil.getCacheFilePath(context, mCropName);
        mCameraPath = CacheFileUtil.getCacheFilePath(context, mCameraName);

        isCrop = true;
    }

    protected void createCameraFile() {
        mCameraFile = CacheFileUtil.getCacheFile(mContext, mCameraName);
    }

    protected void createCropFile() {
        mCropFile = CacheFileUtil.getCacheFile(mContext, mCropName);
    }

    protected Uri getImageContentUri(File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Images.Media._ID},
                    MediaStore.Images.Media.DATA + "=? ",
                    new String[]{filePath}, null);

            if (cursor != null && cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
                Uri baseUri = Uri.parse("content://media/external/images/media");
                return Uri.withAppendedPath(baseUri, "" + id);
            } else {
                if (imageFile.exists()) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.DATA, filePath);
                    return mContext.getContentResolver().insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                } else {
                    return null;
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

}
