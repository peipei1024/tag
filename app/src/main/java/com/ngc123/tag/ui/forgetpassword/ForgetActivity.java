package com.ngc123.tag.ui.forgetpassword;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.avos.avoscloud.okhttp.Request;
import com.ngc123.tag.R;
import com.ngc123.tag.api.ApiCallback;
import com.ngc123.tag.api.ApiClient;
import com.ngc123.tag.api.ApiClientResponse;
import com.ngc123.tag.util.LogUtils;
import com.ngc123.tag.util.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
* Class name :ForgetActivity
*
* Version information :
*
* Describe ：
*
* Author ：裴徐泽
*
* Created by pei on 2016-8-6.
*
*/
public class ForgetActivity extends Activity{
    String PHONE = null;
    @Bind(R.id.username)
    EditText mUsername;
    @Bind(R.id.password)
    EditText mPassword;
    @Bind(R.id.id_bt_next)
    Button mIdBtNext;
    @Bind(R.id.id_iv_back_arrow)
    ImageView idIvBackArrow;
    String token = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("phone");
        PHONE = bundle.getString("phone");
        token = bundle.getString("token");
        setContentView(R.layout.activity_forget_pass);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.id_bt_next)
    public void onClick() {
        String username = mUsername.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        if (username.equals(password)) {
            register(username, password);
        } else {
            ToastUtils.toast(this, "二次输入密码不一样");
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 101) {
                ToastUtils.toast(ForgetActivity.this, "密码修改成功");
                finish();
            } else if (msg.what == 100) {
                ToastUtils.toast(ForgetActivity.this, "修改密码失败");
            }
        }
    };

    private void register(String username, String paswword) {
        ApiClient.create()
                .withService("User.ForgetPwd")
                .addParams("phoneNum", PHONE)
                .addParams("pD", username)
                .addParams("3917e150bbaa953b", token)
                .request(new ApiCallback() {
                    @Override
                    public void onFailure(Request request, IOException e) {

                    }

                    @Override
                    public void onResponse(ApiClientResponse apiClientResponse) throws IOException {
                        LogUtils.i("register", apiClientResponse.getMsg());
                        if (apiClientResponse.getRet() == 200) {
                            try {
                                JSONObject jsonObject = new JSONObject(apiClientResponse.getData());
                                String code = jsonObject.getString("code");
                                if (code.equals("0")) {
                                    handler.sendEmptyMessage(101);
                                } else {
                                    handler.sendEmptyMessage(100);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            handler.sendEmptyMessage(100);
                        }
                    }
                });
    }


    @OnClick(R.id.id_iv_back_arrow)
    public void click() {
        finish();
    }
}
