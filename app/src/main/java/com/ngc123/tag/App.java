package com.ngc123.tag;

import android.app.Application;
import android.util.DisplayMetrics;

import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.leancloud.im.tag.Chat_MessageHandler;
import com.ngc123.tag.ui.MainActivity;
import com.ngc123.tag.ui.SplashActivity;

/**
 * Created by Apple on 16/3/20.
 */
public class App extends Application{
    protected static App       mInstance;
    private DisplayMetrics displayMetrics = null;

    public App(){
        mInstance = this;
    }

    public static App getApp() {
        if (mInstance != null && mInstance instanceof App) {
            return (App) mInstance;
        } else {
            mInstance = new App();
            mInstance.onCreate();
            return (App) mInstance;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initFresco();
        mInstance = this;
        AVOSCloud.initialize(this, "tCbmodbpAsdMYH8nAcKoKKN4-gzGzoHsz", "sb2SoCvdbO5AX9g0wtcD2hxO");
//        AVOSCloud.initialize(this, "Unbx3bnfTxJizCXIosNWDECp-gzGzoHsz", "M67PqvYisHIzhAK54j84XlJw");
        AVIMMessageManager.registerMessageHandler(AVIMTypedMessage.class, new Chat_MessageHandler(this));
        PushService.setDefaultPushCallback(this, SplashActivity.class);
    }


    private void initFresco() {
        Fresco.initialize(this);
    }


    public float getScreenDensity() {
        if (this.displayMetrics == null) {
            setDisplayMetrics(getResources().getDisplayMetrics());
        }
        return this.displayMetrics.density;
    }

    public int getScreenHeight() {
        if (this.displayMetrics == null) {
            setDisplayMetrics(getResources().getDisplayMetrics());
        }
        return this.displayMetrics.heightPixels;
    }

    public int getScreenWidth() {
        if (this.displayMetrics == null) {
            setDisplayMetrics(getResources().getDisplayMetrics());
        }
        return this.displayMetrics.widthPixels;
    }

    public void setDisplayMetrics(DisplayMetrics DisplayMetrics) {
        this.displayMetrics = DisplayMetrics;
    }

    public int dp2px(float f)
    {
        return (int)(0.5F + f * getScreenDensity());
    }

    public int px2dp(float pxValue) {
        return (int) (pxValue / getScreenDensity() + 0.5f);
    }

    //获取应用的data/data/....File目录
    public String getFilesDirPath() {
        return getFilesDir().getAbsolutePath();
    }

    //获取应用的data/data/....Cache目录
    public String getCacheDirPath() {
        return getCacheDir().getAbsolutePath();
    }
}
