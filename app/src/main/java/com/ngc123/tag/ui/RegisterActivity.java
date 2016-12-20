/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ngc123.tag.ui;

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
import com.ngc123.tag.ui.base.BaseActivity;
import com.ngc123.tag.util.IntentUtils;
import com.ngc123.tag.util.TimeCountUtils;
import com.ngc123.tag.util.ToastUtils;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 注册页
 */
public class RegisterActivity extends BaseActivity {


    @Bind(R.id.phone)
    EditText mPhone;
    @Bind(R.id.verify)
    EditText mVerify;
    @Bind(R.id.button2)
    Button mButton2;
    @Bind(R.id.id_bt_next)
    Button mIdBtNext;
    @Bind(R.id.id_iv_back_arrow)
    ImageView idIvBackArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

    }

    /**
     * 注册
     *
     * @param view
     */
    public void register(View view) {


    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 200) {
                ToastUtils.toast(RegisterActivity.this, "验证码已发送");
            } else if (msg.what == 1000) {
                IntentUtils.doIntentWithString(RegisterActivity.this, RegisterComActivity.class, "phone", mPhone.getText().toString().trim());
                finish();
            } else if (msg.what == 1001) {
                ToastUtils.toast(RegisterActivity.this, "验证码错误");
            } else {
                ToastUtils.toast(RegisterActivity.this, "验证码请求失败");
            }
        }
    };

    public void sendVerify(String P) {
        ApiClient.create()
                .withService("Sms.RequestSmsCode")
                .addParams("phoneNum", P)
                .request(new ApiCallback() {
                    @Override
                    public void onFailure(Request request, IOException e) {

                    }

                    @Override
                    public void onResponse(ApiClientResponse apiClientResponse) throws IOException {
                        Message message = new Message();
                        message.what = apiClientResponse.getRet();
                        handler.sendMessage(message);
                    }
                });
    }

    public void verifySmsCode(String phone, String code) {
        ApiClient.create()
                .withService("Sms.VerifySmsCode")
                .addParams("phoneNum", phone)
                .addParams("verifyCode", code)
                .request(new ApiCallback() {
                    @Override
                    public void onFailure(Request request, IOException e) {

                    }

                    @Override
                    public void onResponse(ApiClientResponse apiClientResponse) throws IOException {
                        Message message = new Message();
                        if (apiClientResponse.getRet() == 200) {
                            message.what = 1000;
                        } else {
                            message.what = 1001;
                        }
                        handler.sendMessage(message);
                    }
                });
    }

    public void back(View view) {
        finish();
    }

    @OnClick({R.id.button2, R.id.id_bt_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button2:
                if (!TextUtils.isEmpty(mPhone.getText().toString().trim())) {
                    TimeCountUtils timeCountUtil = new TimeCountUtils(this, 60000, 1000, mButton2);
                    timeCountUtil.start();
                    sendVerify(mPhone.getText().toString().trim());
                } else {
                    ToastUtils.toast(this, "请输入手机号");
                }
                break;
            case R.id.id_bt_next:
                if (!TextUtils.isEmpty(mPhone.getText().toString().trim()) && !TextUtils.isEmpty(mVerify.getText().toString().trim())) {
                    verifySmsCode(mPhone.getText().toString().trim(), mVerify.getText().toString().trim());
                } else {
                    ToastUtils.toast(this, "请填写验证码");
                }
                break;
        }
    }

    @OnClick(R.id.id_iv_back_arrow)
    public void onClick() {
        finish();
    }
}
