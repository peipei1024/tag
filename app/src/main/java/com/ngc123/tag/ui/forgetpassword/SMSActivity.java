package com.ngc123.tag.ui.forgetpassword;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.okhttp.Request;
import com.ngc123.tag.R;
import com.ngc123.tag.api.ApiCallback;
import com.ngc123.tag.api.ApiClient;
import com.ngc123.tag.api.ApiClientResponse;
import com.ngc123.tag.ui.RegisterComActivity;
import com.ngc123.tag.util.IntentUtils;
import com.ngc123.tag.util.TimeCountUtils;
import com.ngc123.tag.util.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
* Class name :SMSActivity
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
public class SMSActivity extends Activity {
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
    @Bind(R.id.id_tv_title)
    TextView idTvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        idTvTitle.setText("找回密码");

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
                ToastUtils.toast(SMSActivity.this, "验证码已发送");
            } else if (msg.what == 1000) {
                Bundle bundle = new Bundle();
                bundle.putString("phone", mPhone.getText().toString().trim());
                bundle.putString("token", token);
                IntentUtils.doIntentWithBundle(SMSActivity.this, ForgetActivity.class, "phone", bundle);
                finish();
            } else if (msg.what == 1001) {
                ToastUtils.toast(SMSActivity.this, "验证码错误");
            } else {
                ToastUtils.toast(SMSActivity.this, "验证码请求失败");
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

    private String token;
    public void verifySmsCode(String phone, String code) {
        ApiClient.create()
                .withService("Sms.SmsReturnToken")
                .addParams("phoneNum", phone)
                .addParams("verifyCode", code)
                .request(new ApiCallback() {
                    @Override
                    public void onFailure(Request request, IOException e) {

                    }

                    @Override
                    public void onResponse(ApiClientResponse apiClientResponse) throws IOException {
                        if (apiClientResponse.getRet() == 200) {
                            try {
                                JSONObject js = new JSONObject(apiClientResponse.getData());
                                String code = js.getString("code");
                                if (code.equals("0")){
                                    token = js.getString("sessionToken");
                                    handler.sendEmptyMessage(1000);
                                }else {
                                    handler.sendEmptyMessage(1001);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            handler.sendEmptyMessage(1001);
                        }
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
