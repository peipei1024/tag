package com.ngc123.tag.ui;

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
* Class name :RegisterComActivity
*
* Version information :
*
* Describe ：
*
* Author ：裴徐泽
*
* Created by pei on 2016-7-29.
*
*/
public class RegisterComActivity extends Activity {
    String PHONE = null;
    @Bind(R.id.username)
    EditText mUsername;
    @Bind(R.id.password)
    EditText mPassword;
    @Bind(R.id.id_bt_next)
    Button mIdBtNext;
    @Bind(R.id.id_iv_back_arrow)
    ImageView idIvBackArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        PHONE = intent.getStringExtra("phone");
        setContentView(R.layout.activity_register_com);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.id_bt_next)
    public void onClick() {
        String username = mUsername.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            register(username, password);
        } else {
            ToastUtils.toast(this, "信息不完整");
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 200) {
                ToastUtils.toast(RegisterComActivity.this, "注册成功");
                finish();
            } else if (msg.what == 1) {
                ToastUtils.toast(RegisterComActivity.this, "手机号已注册");
            } else {
                ToastUtils.toast(RegisterComActivity.this, "注册失败");
            }
        }
    };

    private void register(String username, String paswword) {
        ApiClient.create()
                .withService("User.Register")
                .addParams("pN", PHONE)
                .addParams("uN", username)
                .addParams("pW", paswword)
                .request(new ApiCallback() {
                    @Override
                    public void onFailure(Request request, IOException e) {

                    }

                    @Override
                    public void onResponse(ApiClientResponse apiClientResponse) throws IOException {
                        LogUtils.i("register", apiClientResponse.getMsg());
                        Message message = new Message();
                        if (apiClientResponse.getRet() == 200) {
                            try {
                                JSONObject jsonObject = new JSONObject(apiClientResponse.getData());
                                String code = jsonObject.getString("code");
                                if (code.equals("1")) {
                                    message.what = 1;
                                } else {
                                    message.what = apiClientResponse.getRet();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            message.what = apiClientResponse.getRet();
                        }
                        handler.sendMessage(message);
                    }
                });
    }

    public void back(View view) {
        finish();
    }

    @OnClick(R.id.id_iv_back_arrow)
    public void click() {
        finish();
    }
}
