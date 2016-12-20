package com.ngc123.tag.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.okhttp.Request;
import com.google.gson.Gson;
import com.leancloud.im.tag.Chat_AVImClientManager;
import com.ngc123.tag.ActivityCollector;
import com.ngc123.tag.R;
import com.ngc123.tag.api.ApiCallback;
import com.ngc123.tag.api.ApiClient;
import com.ngc123.tag.api.ApiClientResponse;
import com.ngc123.tag.bean.User;
import com.ngc123.tag.ui.forgetpassword.SMSActivity;
import com.ngc123.tag.util.CommonUtils;
import com.ngc123.tag.util.IntentUtils;
import com.ngc123.tag.util.LogUtils;
import com.ngc123.tag.util.SharedPreferencesUtils;
import com.ngc123.tag.util.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;

/*
* Class name :Login1Activity
*
* Version information :
*
* Describe ：
*
* Author ：裴徐泽
*
* Created by pei on 2016-8-2.
*
*/
public class Login1Activity extends Activity implements PlatformActionListener {
    @Bind(R.id.id_et_phone)
    EditText idEtPhone;
    @Bind(R.id.id_et_password)
    EditText idEtPassword;
    @Bind(R.id.id_btn_login)
    Button idBtnLogin;
    @Bind(R.id.id_tv_register)
    TextView idTvRegister;
    @Bind(R.id.id_tv_forget)
    TextView idTvForget;
    @Bind(R.id.id_tv_see)
    TextView idTvSee;
    @Bind(R.id.id_login_qq)
    ImageView idLoginQq;
    @Bind(R.id.id_login_sina)
    ImageView idLoginSina;
    @Bind(R.id.id_login_weichat)
    ImageView idLoginWeichat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login1);
        ShareSDK.initSDK(this, "15aafdbd3bb44");
        ButterKnife.bind(this);
        ActivityCollector.addActivity(this);
    }

    @OnClick({R.id.id_btn_login, R.id.id_tv_register, R.id.id_tv_forget, R.id.id_tv_see})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_btn_login:
                String phone = idEtPhone.getText().toString();
                String password = idEtPassword.getText().toString();
                if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(password)){
                    if (password.length() >= 6){
                        login();
                    }else {
                        ToastUtils.toast(this, "密码长度不能低于6位");
                    }
                }else {
                    ToastUtils.toast(this, "请将信息填写完整");
                }
                break;
            case R.id.id_tv_register:
                IntentUtils.doIntent(this, RegisterActivity.class);
                break;
            case R.id.id_tv_forget:
                IntentUtils.doIntent(this, SMSActivity.class);
                break;
            case R.id.id_tv_see:
                IntentUtils.doIntent(this, MainActivity.class);
                ActivityCollector.removeActivity(this);
                break;
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 100) {
                Intent intent = new Intent(Login1Activity.this, MainActivity.class);
                startActivity(intent);
                LogUtils.i("login", "login success and tcp success");
                ActivityCollector.removeActivity(Login1Activity.this);
            } else if (msg.what == 101) {
                pd.dismiss();
                ToastUtils.toast(Login1Activity.this, "登录失败");
            }else if (msg.what == 102) {
                pd.dismiss();
                ToastUtils.toast(Login1Activity.this, "密码或手机号错误");
            }
        }
    };

    ProgressDialog pd = null;

    public void login() {
        if (!CommonUtils.isNetWorkConnected(this)) {
            Toast.makeText(this, R.string.network_isnot_available, Toast.LENGTH_SHORT).show();
            return;
        }
        final String phone = idEtPhone.getText().toString();
        final String password = idEtPassword.getText().toString();

        if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(password)) {
            pd = new ProgressDialog(this);
            pd.setCanceledOnTouchOutside(false);
            pd.setMessage("正在登陆...");
            pd.show();

            ApiClient.create()
                    .withService("User.Login")
                    .addParams("pN", phone)
                    .addParams("pW", password)
                    .request(new ApiCallback() {
                        @Override
                        public void onFailure(Request request, IOException e) {
                        }

                        @Override
                        public void onResponse(ApiClientResponse apiClientResponse) throws IOException {
                            if (apiClientResponse.getRet() != 200) {
                                handler.sendEmptyMessage(101);
                                return;
                            }
                            Log.i("login", "success" + apiClientResponse.getData());
                            try {
                                JSONObject js = new JSONObject(apiClientResponse.getData());
                                String code = js.getString("code");
                                if (code.equals("0")) {
                                    String data = js.getString("userinfo");
                                    Log.i("login", "success" + data);
                                    Gson gson = new Gson();
                                    User user = gson.fromJson(data, User.class);
                                    if (!TextUtils.isEmpty(user.getUsername())) {
                                        Chat_AVImClientManager.getInstance().open(user.getUsername(), new AVIMClientCallback() {
                                            @Override
                                            public void done(AVIMClient avimClient, AVIMException e) {
                                                if (e == null) {
                                                    Map<String, String> map = new HashMap<String, String>();
                                                    map.put("user", data);
                                                    SharedPreferencesUtils.writeSharedPreferences(Login1Activity.this, "user", map);
                                                    handler.sendEmptyMessage(100);
                                                } else {
                                                    handler.sendEmptyMessage(101);
                                                }
                                            }
                                        });
                                    } else if (code.equals("1")) {
                                        handler.sendEmptyMessage(102);
                                    }else {
                                        handler.sendEmptyMessage(101);
                                    }
                                } else {
                                    handler.sendEmptyMessage(101);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
        }
    }


    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
        LogUtils.i("login", hashMap.toString());
        PlatformDb platDB = platform.getDb();//获取数平台数据DB
        //通过DB获取各种数据
        platDB.getToken();
        LogUtils.i("login", platDB.getToken() + "getToken");
        platDB.getUserGender();
        LogUtils.i("login", platDB.getUserGender() + "getUserGender");
        platDB.getUserIcon();
        LogUtils.i("login", platDB.getUserIcon() + "getUserIcon");
        platDB.getUserId();
        LogUtils.i("login", platDB.getUserId() + "id");
        platDB.getUserName();
        LogUtils.i("login", platDB.getUserName() + "getUserName");
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {

    }

    @Override
    public void onCancel(Platform platform, int i) {

    }

    @OnClick({R.id.id_login_qq, R.id.id_login_sina, R.id.id_login_weichat})
    public void otherlogin(View view) {
        switch (view.getId()) {
            case R.id.id_login_qq:
                ToastUtils.toast(this, "此功能正在开发");
//                Platform qq = ShareSDK.getPlatform(QQ.NAME);
//        qq.SSOSetting(false);
//        qq.setPlatformActionListener(this);
//        qq.authorize();
//                Platform qq = ShareSDK.getPlatform(this, QQ.NAME);
//                qq.setPlatformActionListener(this);
//                qq.showUser(null);
                break;
            case R.id.id_login_sina:
                ToastUtils.toast(this, "此功能正在开发");
                break;
            case R.id.id_login_weichat:
                ToastUtils.toast(this, "此功能正在开发");
                break;
        }
    }
}
