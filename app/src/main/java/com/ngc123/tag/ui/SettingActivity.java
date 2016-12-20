package com.ngc123.tag.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.okhttp.Request;
import com.ngc123.tag.ActivityCollector;
import com.ngc123.tag.R;
import com.ngc123.tag.api.ApiCallback;
import com.ngc123.tag.api.ApiClient;
import com.ngc123.tag.api.ApiClientResponse;
import com.ngc123.tag.update.UpdateDialog;
import com.ngc123.tag.util.AppUtils;
import com.ngc123.tag.util.DataCleanManager;
import com.ngc123.tag.util.IntentUtils;
import com.ngc123.tag.util.LogUtils;
import com.ngc123.tag.util.UserHelp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by JiaM on 2016/3/20.
 */
public class SettingActivity extends Activity {

    @Bind(R.id.id_iv_back_arrow)
    ImageView idIvBackArrow;
    @Bind(R.id.id_view_agreement)
    RelativeLayout idViewAgreement;
    @Bind(R.id.id_view_suggestion)
    RelativeLayout idViewSuggestion;
    @Bind(R.id.id_tv_phone)
    TextView idTvPhone;
    @Bind(R.id.id_view_custom_phone)
    RelativeLayout idViewCustomPhone;
    @Bind(R.id.id_tv_cache)
    TextView idTvCache;
    @Bind(R.id.id_view_cache)
    RelativeLayout idViewCache;
    @Bind(R.id.id_view_about)
    RelativeLayout idViewAbout;
    @Bind(R.id.id_view_update)
    RelativeLayout idViewUpdate;
    @Bind(R.id.id_tv_vsersion)
    TextView idTvVsersion;
    @Bind(R.id.quit)
    Button quit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.me_setting);
        ButterKnife.bind(this);
        ActivityCollector.addActivity(this);
        init();
    }



    @OnClick({R.id.id_iv_back_arrow, R.id.id_view_agreement, R.id.id_view_suggestion, R.id.id_view_custom_phone, R.id.id_view_cache, R.id.id_view_about, R.id.id_view_update, R.id.quit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_iv_back_arrow:
                ActivityCollector.removeActivity(this);
                break;
            case R.id.id_view_agreement:
                Bundle bundle = new Bundle();
                bundle.putString("title", "协议");
                bundle.putString("url", "http://www.baidu.com");
                IntentUtils.doIntentWithBundle(this, WebviewActivity.class, "urlbundle", bundle);
                break;
            case R.id.id_view_suggestion:
                IntentUtils.doIntent(this, SuggestActivity.class);
                break;
            case R.id.id_view_custom_phone:
                break;
            case R.id.id_view_cache:
                DataCleanManager.cleanInternalCache(this);
                File file = new File("/data/data/com.ngc123.tag/cache/picasso-cache");
                try {
                    String a = DataCleanManager.getCacheSize(file);
                    LogUtils.i("setting", a);
                    idTvCache.setText(a);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.id_view_about:
                IntentUtils.doIntent(this, AboutActivity.class);
                break;
            case R.id.id_view_update:
                updateAPK();
                break;
            case R.id.quit:
                UserHelp.getInstance().logoutUser(this);
                ActivityCollector.finishAll();
                Intent logoutIntent = new Intent(SettingActivity.this, Login1Activity.class);
                startActivity(logoutIntent);
                break;
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 100:
                    UpdateDialog.showUpdateDialog(SettingActivity.this, "tag_version" + newversion, link);
                    break;
            }
        }
    };
    private String link;
    private int newversion;
    private void updateAPK(){
        ApiClient.create()
                .withService("Config.CheckUpdate")
                .addParams("version", AppUtils.getVersionCode(this))
                .request(new ApiCallback() {
                    @Override
                    public void onFailure(Request request, IOException e) {

                    }

                    @Override
                    public void onResponse(ApiClientResponse apiClientResponse) throws IOException {
                        if (apiClientResponse.getRet() == 200){
                            try {
                                JSONObject js = new JSONObject(apiClientResponse.getData());
                                String version = js.getString("version");
                                link = js.getString("link");
                                newversion = Integer.valueOf(version);
                                if (newversion > AppUtils.getVersionCode(SettingActivity.this)){
                                    handler.sendEmptyMessage(100);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    private void init(){
        String code = AppUtils.getVersionName(this);
        idTvVsersion.setText(" " + code);
        File file = new File("/data/data/com.ngc123.tag/cache/picasso-cache");
        try {
            String a = DataCleanManager.getCacheSize(file);
            LogUtils.i("setting", a);
            idTvCache.setText(a);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
