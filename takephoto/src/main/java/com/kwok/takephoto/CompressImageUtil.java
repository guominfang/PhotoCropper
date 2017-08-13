package com.kwok.takephoto;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author gmf
 * @description 压缩图片
 * @date 2017/8/12.
 */
class CompressImageUtil {
    private static final String TAG = "CompressImageUtil";

    static void compressImageFile(TakeParam takeParam, Uri originUri, Uri compressUri) {
        Bitmap bitmap = null;
        InputStream is = null;
        OutputStream out = null;
        try {
            is = takeParam.mContext.getContentResolver().openInputStream(originUri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, null, options);
            // Calculate inSampleSize
            int minSideLength = takeParam.compressWidth > takeParam.compressHeight
                    ? takeParam.compressHeight : takeParam.compressWidth;
            options.inSampleSize = computeSampleSize(options, minSideLength, takeParam.compressWidth * takeParam.compressHeight);
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            is = takeParam.mContext.getContentResolver().openInputStream(originUri);
            bitmap = BitmapFactory.decodeStream(is, null, options);
            File compressFile = new File(compressUri.getPath());
            if (!compressFile.exists()) {
                boolean result = compressFile.createNewFile();
                Log.d(TAG, "Target " + compressUri + " not exist, create a new one " + result);
            }
            out = new FileOutputStream(compressFile);
            boolean result = bitmap.compress(Bitmap.CompressFormat.JPEG, takeParam.compressQuality, out);
            Log.d(TAG, "Compress bitmap " + (result ? "succeed" : "failed"));
        } catch (Exception e) {
            Log.e(TAG, "compressInputStreamToOutputStream", e);
        } finally {
            if (bitmap != null)
                bitmap.recycle();
            try {
                if (is != null)
                    is.close();

                if (out != null)
                    out.close();
            } catch (IOException ignore) {
            }
        }
    }

    private static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }
}
