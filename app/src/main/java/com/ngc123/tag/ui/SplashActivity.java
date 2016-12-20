package com.ngc123.tag.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.ngc123.tag.App;
import com.ngc123.tag.R;
import com.ngc123.tag.service.DownLabelService;
import com.ngc123.tag.ui.base.BaseActivity;
import com.ngc123.tag.update.UpdateService;
import com.ngc123.tag.util.UserHelp;

/**
 * Created by Apple on 16/4/20.
 */
public class SplashActivity extends BaseActivity{
    private static final long DELAY_TIME = 500L;

    private static final int GO_MAIN_MSG = 1;
    private static final int GO_LOGIN_MSG = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Intent intent = new Intent(App.getApp(), DownLabelService.class);
        startService(intent);
        redirectByTime();

    }

    /**
     * 根据时间进行页面跳转
     */
    private void redirectByTime() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (UserHelp.getInstance().getCurrentUser(SplashActivity.this) != null) {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, Login1Activity.class));
                }
                finish();
            }
        }, DELAY_TIME);
    }
}
