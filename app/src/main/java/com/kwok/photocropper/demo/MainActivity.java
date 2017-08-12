package com.kwok.photocropper.demo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.kwok.takephoto.ITakePhotoListener;
import com.kwok.takephoto.TakeHelper;
import com.kwok.takephoto.TakeParam;

public class MainActivity extends AppCompatActivity implements ITakePhotoListener {

    private TakeParam mParam;

    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageView = (ImageView) findViewById(R.id.imageView);
    }

    public void onCamera(View view) {
        mParam = new TakeParam(this);
        startActivityForResult(TakeHelper.buildCameraIntent(mParam), mParam.REQUEST_CODE_CAMERA);
    }

    public void onAlbum(View view) {
        mParam = new TakeParam(this);
        startActivityForResult(TakeHelper.buildAlbumIntent(), mParam.REQUEST_CODE_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        TakeHelper.handleResult(this, requestCode, resultCode, data);
    }

    @Override
    public void onComplete(Uri uri) {
        mImageView.setImageURI(uri);
    }

    @Override
    public void onFailed() {
        Toast.makeText(this, "获取图片失败", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void startCropIntent(Intent intent) {
        startActivityForResult(intent, mParam.REQUEST_CODE_CROP);
    }

    @Override
    public TakeParam getTakeParam() {
        return mParam;
    }
}
