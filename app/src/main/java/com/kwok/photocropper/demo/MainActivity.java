package com.kwok.photocropper.demo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.kwok.takephoto.ITakePhotoListener;
import com.kwok.takephoto.TakeHelper;
import com.kwok.takephoto.TakeParam;

import java.io.File;

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
        startActivityForResult(TakeHelper.buildCameraIntent(mParam), mParam.requestCamera);
    }


    public void onAlbum(View view) {
        mParam = new TakeParam(this);
        startActivityForResult(TakeHelper.buildAlbumIntent(), mParam.requestAlbum);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        TakeHelper.handleResult(this, requestCode, resultCode, data);
    }

    @Override
    public void onComplete(Uri uri) {
//        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
//        mImageView.setImageBitmap(bitmap);
        mImageView.setImageURI(uri);
    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onFailed() {

    }

    @Override
    public void startCropIntent(Intent intent) {
        startActivityForResult(intent, mParam.requestCrop);
    }

    @Override
    public TakeParam getTakeParam() {
        return mParam;
    }
}
