package com.kwok.takephoto;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

/**
 * @author gmf
 * @description 缓存文件工具类
 * @date 2017/8/11.
 */
public class CacheFileUtil {

    private static final String TAKE_CACHE_FOLDER = "take_photo";

    /**
     * @param context  context
     * @param fileName 缓存文件名称
     * @return 获得缓存文件
     */
    protected static File getCacheFile(Context context, String fileName) {
        if (isExistsDir(context)) {
            return createCacheFile(getDiskCacheDir(context), fileName);
        }
        return createCacheFile(getAppDiskCacheDir(context), fileName);
    }

    /**
     * @param context context
     * @return 判断文件目录是否存在
     */
    private static boolean isExistsDir(Context context) {
        File fileDir = new File(getDiskCacheDir(context));
        if (!fileDir.exists()) {
            return fileDir.mkdirs();
        }
        return true;
    }

    /**
     * @param parent   缓存目录
     * @param fileName 缓存文件名称
     * @return 创建缓存文件
     */
    private static File createCacheFile(String parent, String fileName) {
        File file = new File(parent, fileName);

        if (file.exists()) {
            file.delete();
        }

        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }


    /**
     * @param context context
     * @return 返回自定义缓存目录
     */
    protected static String getDiskCacheDir(Context context) {
        return getAppDiskCacheDir(context) + File.separator + TAKE_CACHE_FOLDER;
    }

    /**
     * @param context context
     * @return 返回应用本身的缓存路径
     */
    private static String getAppDiskCacheDir(Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            if (context.getExternalCacheDir() != null) {
                //  .../Android/data/你的应用包名/cache/
                return context.getExternalCacheDir().getPath();
            }
        }
        //  .../data/data/你的应用包名/cache
        return context.getCacheDir().getPath();
    }


}
