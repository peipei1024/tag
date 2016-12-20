package com.ngc123.tag.ui;

import android.os.Bundle;

import com.ngc123.tag.camera.CameraManager;
import com.ngc123.tag.ui.base.BaseActivity;


public class CameraBaseActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CameraManager.getInst().addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CameraManager.getInst().removeActivity(this);
    }
}
