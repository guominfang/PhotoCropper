package com.kwok.takephoto;

import android.content.Intent;
import android.net.Uri;

/**
 * @author gmf
 * @description 获取照片回调
 * @date 2017/8/11.
 */
public interface ITakePhotoListener {

    void onComplete(Uri uri);

    void onFailed();

    void startCropIntent(Intent intent);

    TakeParam getTakeParam();

}
