package com.ngc123.tag.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.ngc123.tag.R;
import com.ngc123.tag.model.FeedItem;
import com.ngc123.tag.util.ToastUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
* Class name :PicActivity
*
* Version information :
*
* Describe ：
*
* Author ：裴徐泽
*
* Created by pei on 2016-8-1.
*
*/
public class PicActivity extends Activity {
    @Bind(R.id.id_iv_pic)
    ImageView idIvPic;
    @Bind(R.id.id_iv_back_arrow)
    ImageView idIvBackArrow;
    @Bind(R.id.id_iv_save)
    ImageView idIvSave;
    @Bind(R.id.id_iv_test)
    ImageView idIvTest;
    private FeedItem url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        url = (FeedItem) intent.getSerializableExtra("url");
        if (!TextUtils.isEmpty(url.getImgName())) {
            StringBuffer sb = new StringBuffer("http://7xstnm.com2.z0.glb.clouddn.com/");
            sb.append(url.getImgName());
            String r = sb.toString();
            Picasso.with(this).load(r).into(idIvPic);
        }
    }

    int READ_EXTERNAL_STORAGE = 10000;
    @OnClick({R.id.id_iv_back_arrow, R.id.id_iv_save})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_iv_back_arrow:
                finish();
                break;
            case R.id.id_iv_save:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    //申请WRITE_EXTERNAL_STORAGE权限
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            READ_EXTERNAL_STORAGE);
                }else {
                    idIvPic.setDrawingCacheEnabled(true);
                    Bitmap bitmap = Bitmap.createBitmap(idIvPic.getDrawingCache());
                    idIvPic.setDrawingCacheEnabled(false);
//                idIvTest.setImageBitmap(bitmap);
                    File file = new File("/sdcard/tag");
                    if (!file.exists()){
                        file.mkdir();
                    }
                    File file1 = new File(file, "tag_pic_" + url.getImgId() + ".png");
                    FileOutputStream foutput = null;
                    try {
                        foutput = new FileOutputStream(file1);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, foutput);
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtils.toast(this, "保存失败");
                    }finally {
                        if (null != foutput) {
                            try {
                                foutput.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        ToastUtils.toast(this, "保存成功");
                    }
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        doNext(requestCode,grantResults);
    }

    private void doNext(int requestCode, int[] grantResults) {
        if (requestCode == READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                idIvPic.setDrawingCacheEnabled(true);
                Bitmap bitmap = Bitmap.createBitmap(idIvPic.getDrawingCache());
                idIvPic.setDrawingCacheEnabled(false);
//                idIvTest.setImageBitmap(bitmap);
                File file = new File("/sdcard/tag");
                if (!file.exists()){
                    file.mkdir();
                }
                File file1 = new File(file, "tag_pic_" + url.getImgId() + ".png");
                FileOutputStream foutput = null;
                try {
                    foutput = new FileOutputStream(file1);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, foutput);
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtils.toast(this, "保存失败");
                }finally {
                    if (null != foutput) {
                        try {
                            foutput.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    ToastUtils.toast(this, "保存成功");
                }
            } else {
                ToastUtils.toast(this, "保存失败");
                // Permission Denied
            }
        }
    }
}
