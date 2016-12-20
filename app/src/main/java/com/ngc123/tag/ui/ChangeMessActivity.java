package com.ngc123.tag.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.avos.avoscloud.okhttp.Request;
import com.ngc123.tag.R;
import com.ngc123.tag.api.ApiCallback;
import com.ngc123.tag.api.ApiClient;
import com.ngc123.tag.api.ApiClientResponse;
import com.ngc123.tag.bean.User;
import com.ngc123.tag.util.SharedPreferencesUtils;
import com.ngc123.tag.util.ToastUtils;
import com.ngc123.tag.util.UserHelp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
* Class name :ChangeMessActivity
*
* Version information :
*
* Describe ：
*
* Author ：裴徐泽
*
* Created by pei on 2016-8-5.
*
*/
public class ChangeMessActivity extends Activity {
    @Bind(R.id.id_iv_back_arrow)
    ImageView idIvBackArrow;
    @Bind(R.id.id_name_edit)
    EditText idNameEdit;
    @Bind(R.id.id_save_button)
    Button idSaveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_mess);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.id_iv_back_arrow, R.id.id_save_button})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_iv_back_arrow:
                setResult(1);
                finish();
                break;
            case R.id.id_save_button:
                String str = idNameEdit.getText().toString().trim();
                updateSummary(str);
                break;
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    ToastUtils.toast(ChangeMessActivity.this, "修改资料失败");
                    break;
                case 1:
                    ToastUtils.toast(ChangeMessActivity.this, "修改资料成功");
                    setResult(0);
                    finish();
                    break;
            }
        }
    };
    private void updateUser(ApiClient apiClient){
        apiClient.request(new ApiCallback() {
            @Override
            public void onFailure(Request request, IOException e) {
                handler.sendEmptyMessage(0);
            }

            @Override
            public void onResponse(ApiClientResponse apiClientResponse) throws IOException {
                if (apiClientResponse.getRet() == 200){
                    try {
                        JSONObject js = new JSONObject(apiClientResponse.getData());
                        String code = js.getString("code");
                        if (code.equals("0")){
                            String data = js.getString("userinfo");
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("user", data);
                            SharedPreferencesUtils.writeSharedPreferences(ChangeMessActivity.this, "user", map);
                            handler.sendEmptyMessage(1);
                        }else {
                            handler.sendEmptyMessage(0);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    handler.sendEmptyMessage(0);
                }
            }
        });
    }
    private void updateSummary(String lc){
        User user = UserHelp.getInstance().getCurrentUser(this);
        updateUser(ApiClient.create()
                .withService("User.AlterUserInfo")
                .addParams("uD", user.getUid())
                .addParams("3917e150bbaa953b", user.getSessionToken())
                .addParams("summary", lc));
    }

}
