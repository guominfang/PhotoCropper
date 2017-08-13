package com.kwok.photocropper.demo;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.kwok.takephoto.ITakePhotoListener;
import com.kwok.takephoto.TakeHelper;
import com.kwok.takephoto.TakeParam;

public class MainActivity extends AppCompatActivity implements ITakePhotoListener {

    private static final int RC_CAMERA_PERMISSIONS = 20;
    private static final int RC_ALBUM_PERMISSIONS = 21;

    private static String[] mCameraPermissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private ImageView mImageView;
    private CheckBox mCrop;
    private CheckBox mCompress;

    private TakeParam mParam;
    private PermissionsUtil mPermissionsUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCrop = (CheckBox) findViewById(R.id.crop);
        mCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCrop.setChecked(mCrop.isChecked());
            }
        });
        mCompress = (CheckBox) findViewById(R.id.compress);
        mCompress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCompress.setChecked(mCompress.isChecked());
            }
        });
        mImageView = (ImageView) findViewById(R.id.imageView);
        mPermissionsUtil = new PermissionsUtil(this);
    }

    public void onCamera(View view) {
        if (mPermissionsUtil.lacksPermissions(mCameraPermissions)) {
            ActivityCompat.requestPermissions(this, mCameraPermissions, RC_CAMERA_PERMISSIONS);
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        mParam = new TakeParam(this);
        mParam.isCrop = mCrop.isChecked();
        mParam.isCompress = mCompress.isChecked();
        startActivityForResult(TakeHelper.buildCameraIntent(mParam), mParam.REQUEST_CODE_CAMERA);
    }


    public void onAlbum(View view) {
        if (mPermissionsUtil.lacksPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, RC_ALBUM_PERMISSIONS);
        } else {
            openAlbum();
        }
    }

    private void openAlbum() {
        mParam = new TakeParam(this);
        mParam.isCrop = mCrop.isChecked();
        mParam.isCompress = mCompress.isChecked();
        startActivityForResult(TakeHelper.buildAlbumIntent(), mParam.REQUEST_CODE_ALBUM);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionsUtil.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mPermissionsUtil.isGranted) {
            handlePermissionsResult(requestCode);
        } else {
            Toast.makeText(this, "应用缺少权限，开启功能失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void handlePermissionsResult(int requestCode) {
        switch (requestCode) {
            case RC_ALBUM_PERMISSIONS: //相册
                openAlbum();
                break;
            case RC_CAMERA_PERMISSIONS: //相机
                openCamera();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        TakeHelper.handleResult(this, mParam, requestCode, resultCode, data);
    }

    @Override
    public void onComplete(Uri uri) {
        mImageView.setImageURI(uri);
//        TakeHelper.clearFile(this);
    }

    @Override
    public void onCancel() {

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
    protected void onDestroy() {
        TakeHelper.clearFile(this);
        super.onDestroy();
    }
}
