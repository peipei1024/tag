package com.ngc123.tag.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.okhttp.Request;
import com.diycoder.citypick.widget.CityPickerPopWindow;
import com.google.gson.Gson;
import com.ngc123.tag.R;
import com.ngc123.tag.api.ApiCallback;
import com.ngc123.tag.api.ApiClient;
import com.ngc123.tag.api.ApiClientResponse;
import com.ngc123.tag.bean.User;
import com.ngc123.tag.event.Avatar;
import com.ngc123.tag.util.IntentUtils;
import com.ngc123.tag.util.LogUtils;
import com.ngc123.tag.util.SelectPhotoUtils;
import com.ngc123.tag.util.SharedPreferencesUtils;
import com.ngc123.tag.util.ToastUtils;
import com.ngc123.tag.util.UserHelp;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

/*
* Class name :PersonalMessActivity
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
public class PersonalMessActivity extends Activity implements CityPickerPopWindow.CityPickListener, DialogInterface.OnClickListener{
    @Bind(R.id.id_iv_back_arrow)
    ImageView idIvBackArrow;
    @Bind(R.id.id_iv_head)
    ImageView idIvHead;
    @Bind(R.id.id_view_head)
    RelativeLayout idViewHead;
    @Bind(R.id.id_tv_sex)
    TextView idTvSex;
    @Bind(R.id.id_view_sex)
    RelativeLayout idViewSex;
    @Bind(R.id.id_tv_location)
    TextView idTvLocation;
    @Bind(R.id.id_view_location)
    RelativeLayout idViewLocation;
    @Bind(R.id.id_tv_qianming)
    TextView idTvQianming;
    @Bind(R.id.id_view_qianming)
    RelativeLayout idViewQianming;
    @Bind(R.id.id_root_view)
    LinearLayout idRootView;
    String[] sex = {"男", "女"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_mess);
        ButterKnife.bind(this);
        setUi();
    }

    private Dialog dialog;
    int READ_EXTERNAL_STORAGE = 10000;
    @OnClick({R.id.id_iv_back_arrow, R.id.id_view_head, R.id.id_view_sex, R.id.id_view_location, R.id.id_view_qianming})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_iv_back_arrow:
                finish();
                break;
            case R.id.id_view_head:
//                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    //申请WRITE_EXTERNAL_STORAGE权限
//                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
//                            READ_EXTERNAL_STORAGE);
//                } else {
//                    SelectPhotoUtils.openAlbum(this, true, 1, MainActivity.photo_list, 100);
//                }
                break;
            case R.id.id_view_sex:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("选择性别");
                builder.setItems(sex, this);
                dialog = builder.create();
                dialog.show();
                break;
            case R.id.id_view_location:
                CityPickerPopWindow mPopWindow = new CityPickerPopWindow(this);
                mPopWindow.showPopupWindow(idRootView);
                mPopWindow.setCityPickListener(this);
                break;
            case R.id.id_view_qianming:
//                IntentUtils.doIntent(this, ChangeMessActivity.class);
                Intent intent = new Intent(this, ChangeMessActivity.class);
                startActivityForResult(intent, 300);
                break;
        }
    }

    @Override
    public void pickValue(String value) {
        updateLC(value);
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    ToastUtils.toast(PersonalMessActivity.this, "修改资料失败");
                    break;
                case 1:
                    ToastUtils.toast(PersonalMessActivity.this, "修改资料成功");
                    setUi();
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
                            SharedPreferencesUtils.writeSharedPreferences(PersonalMessActivity.this, "user", map);
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
    private void updateLC(String lc){
        User user = UserHelp.getInstance().getCurrentUser(this);
        updateUser(ApiClient.create()
                .withService("User.AlterUserInfo")
                .addParams("uD", user.getUid())
                .addParams("3917e150bbaa953b", user.getSessionToken())
                .addParams("lC", lc));
    }

    private void updateSex(int lc){
        User user = UserHelp.getInstance().getCurrentUser(this);
        updateUser(ApiClient.create()
                .withService("User.AlterUserInfo")
                .addParams("uD", user.getUid())
                .addParams("3917e150bbaa953b", user.getSessionToken())
                .addParams("sex", lc));
    }
    private void setUi(){
        User user = UserHelp.getInstance().getCurrentUser(this);
        if (!TextUtils.isEmpty(user.getAvatar())) {
            StringBuffer sb = new StringBuffer("http://7xstnm.com2.z0.glb.clouddn.com/");
            sb.append(user.getAvatar());
            String r = sb.toString();
            Picasso.with(this).load(r).placeholder(R.drawable.default_avatar).error(R.drawable.default_avatar).into(idIvHead);
        } else {
            Picasso.with(this).load(R.drawable.default_avatar).into(idIvHead);
        }
        if (user.getSex().equals("0")) {
            //男
            idTvSex.setText("男");
        } else if (user.getSex().equals("1")) {
            idTvSex.setText("女");
            //女
        } else if (user.getSex().equals("2")) {
            idTvSex.setText("未设置");
            //不详
        }
        if (!TextUtils.isEmpty(user.getLocation())) {
            idTvLocation.setText(user.getLocation());
        } else {
            idTvLocation.setText("火星");
        }
        if (!TextUtils.isEmpty(user.getSummary())) {
            idTvQianming.setText(user.getSummary());
        } else {
            idTvQianming.setText("这家伙很懒,什么都没写");
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        updateSex(which);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        doNext(requestCode,grantResults);
    }

    private void doNext(int requestCode, int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                SelectPhotoUtils.openAlbum(this, true, 1, MainActivity.photo_list, 100);
            } else {
                ToastUtils.toast(this, "获取权限失败");
                // Permission Denied
            }
        }
    }
    public static ArrayList<String> photo_list = new ArrayList<>();
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                photo_list = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                Log.i("me", "info" + photo_list.get(0));
                Intent intent = new Intent();
                Uri mUri = Uri.parse("file://" + photo_list.get(0));
                intent.setAction("com.android.camera.action.CROP");
                intent.setDataAndType(mUri, "image/*");
                intent.putExtra("crop", "true");
                intent.putExtra("aspectX", 1);// 裁剪框比例
                intent.putExtra("aspectY", 1);
                intent.putExtra("outputX", 150);// 输出图片大小
                intent.putExtra("outputY", 150);
                intent.putExtra("return-data", true);
                PersonalMessActivity.this.startActivityForResult(intent, 200);
                photo_list.clear();
            }
        } else if (requestCode == 200) {
            if (resultCode == RESULT_OK) {
                Bitmap bitmap = data.getParcelableExtra("data");
                File file = new File("/sdcard/tag");
                if (!file.exists()) {
                    file.mkdir();
                }
                File file1 = new File(file, "avatar.png");
                FileOutputStream foutput = null;
                try {
                    foutput = new FileOutputStream(file1);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, foutput);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    if (null != foutput) {
                        try {
                            foutput.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                uploadImage("/sdcard/tag/avatar.png");
            }
        }else if (requestCode == 300){
            if (resultCode == 0){
                setUi();
            }
        }
    }

    private void uploadImage(String path) {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("上传头像中....");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        User user = UserHelp.getInstance().getCurrentUser(this);
        File file = new File(path);
        ApiClient.create()
                .withService("User.AddAvatar")
                .addParams("uD", user.getUid())
                .addParams("3917e150bbaa953b", user.getSessionToken())
                .addImage("images", file)
                .request(new ApiCallback() {
                    @Override
                    public void onFailure(Request request, IOException e) {

                    }

                    @Override
                    public void onResponse(ApiClientResponse apiClientResponse) throws IOException {
                        dialog.dismiss();
                        if (apiClientResponse.getRet() != 200) {
                            LogUtils.i("me", "失败" + apiClientResponse.getRet());
                            handler.sendEmptyMessage(1);
                        }
                        JSONObject js = null;
                        try {
                            js = new JSONObject(apiClientResponse.getData());
                            String data = js.getString("info");
                            LogUtils.i("main", "成功" + data);
                            Gson gson = new Gson();
                            User user1 = gson.fromJson(data, User.class);
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("user", data);
                            SharedPreferencesUtils.writeSharedPreferences(PersonalMessActivity.this, "user", map);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });
    }
}
