package com.ngc123.tag.ui;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.okhttp.Request;
import com.google.gson.Gson;
import com.ngc123.tag.ActivityCollector;
import com.ngc123.tag.api.ApiCallback;
import com.ngc123.tag.api.ApiClient;
import com.ngc123.tag.api.ApiClientResponse;
import com.ngc123.tag.bean.User;
import com.ngc123.tag.event.Avatar;
import com.ngc123.tag.fragment.chat.ChatFragment;
import com.ngc123.tag.fragment.HomeFragment;
import com.ngc123.tag.fragment.MeFragment;
import com.ngc123.tag.fragment.find.FindFragment;
import com.ngc123.tag.R;
import com.ngc123.tag.update.UpdateDialog;
import com.ngc123.tag.util.AppUtils;
import com.ngc123.tag.util.IntentUtils;
import com.ngc123.tag.util.LogUtils;
import com.ngc123.tag.util.SharedPreferencesUtils;
import com.ngc123.tag.util.UserHelp;

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

/**
 * Created by 5v on 2016/3/15.
 */
public class MainActivity extends FragmentActivity implements View.OnClickListener {

    public static final String ACTION_SHOW_LOADING_ITEM = "action_show_loading_item";
    @Bind(R.id.id_view_login)
    RelativeLayout idViewLogin;
    @Bind(R.id.id_view_register)
    RelativeLayout idViewRegister;
    @Bind(R.id.id_view_no_user)
    LinearLayout idViewNoUser;
    @Bind(R.id.menu_bottom)
    LinearLayout menuBottom;
    private ImageView[] bt_menu = new ImageView[4];
    private RelativeLayout[] bt_choose = new RelativeLayout[4];
    private ImageView cramera_Iv;
    private int[] bt_menu_id = {R.id.iv_home, R.id.iv_chat, R.id.iv_find, R.id.iv_me};
    private int[] bt_choose_id = {R.id.bt_home, R.id.bt_chat, R.id.bt_find, R.id.bt_me};

    private int[] select_on = {R.mipmap.home_press, R.mipmap.chat_press, R.mipmap.find_press, R.mipmap.me_press};
    private int[] select_off = {R.mipmap.home, R.mipmap.chat, R.mipmap.find, R.mipmap.me};

    private HomeFragment homeFragment;
    private ChatFragment chatFragment;
    private FindFragment findFragment;
    private MeFragment meFragment;

    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        ButterKnife.bind(this);
        LogUtils.i("main", AVInstallation.getCurrentInstallation().getInstallationId());
        updateInstallations();
        ActivityCollector.addActivity(this);
        initView();
        updateAPK();
        LogUtils.i("main", "main");
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 100:
                    UpdateDialog.showUpdateDialog(MainActivity.this, "tag_version" + newversion, link);
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
                                HashMap<String, String> map = new HashMap<String, String>();
                                map.put("link", link);
                                SharedPreferencesUtils.writeSharedPreferences(MainActivity.this, "app", map);
                                newversion = Integer.valueOf(version);
                                if (newversion > AppUtils.getVersionCode(MainActivity.this)){
                                    handler.sendEmptyMessage(100);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    private void updateInstallations(){
        User user = UserHelp.getInstance().getCurrentUser(this);
        String installations = AVInstallation.getCurrentInstallation().getInstallationId();
        if (user != null){
            if (installations.length() > 0){
                ApiClient.create()
                        .withService("Device.Installations")
                        .addParams("uD", user.getUid())
                        .addParams("iD", installations)
                        .addParams("3917e150bbaa953b", user.getSessionToken())
                        .request(new ApiCallback() {
                            @Override
                            public void onFailure(Request request, IOException e) {

                            }

                            @Override
                            public void onResponse(ApiClientResponse apiClientResponse) throws IOException {
                                LogUtils.i("main", " " + apiClientResponse.getData());
                            }
                        });
            }
        }

    }

    protected boolean filterException(Exception e) {
        if (e != null) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG);
            return false;
        } else {
            return true;
        }
    }

    private void initView() {
        if (UserHelp.getInstance().getCurrentUser(this) == null) {
            idViewNoUser.setVisibility(View.VISIBLE);
            menuBottom.setVisibility(View.GONE);
        } else {
            idViewNoUser.setVisibility(View.GONE);
            menuBottom.setVisibility(View.VISIBLE);
        }
        for (int i = 0; i < bt_menu.length; i++) {
            bt_menu[i] = (ImageView) findViewById(bt_menu_id[i]);
            bt_choose[i] = (RelativeLayout) findViewById(bt_choose_id[i]);
            bt_choose[i].setOnClickListener(this);
        }
        cramera_Iv = (ImageView) findViewById(R.id.iv_cramera);
        cramera_Iv.setOnClickListener(this);

        if (findFragment == null) {
            findFragment = new FindFragment();
            addFragment(findFragment);
            showFragment(findFragment);
        } else {
            showFragment(findFragment);
        }
        bt_menu[2].setImageResource(select_on[2]);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        bt_menu[index].setImageResource(select_on[index]);
    }


    private void showFragment(Fragment fragment) {
        FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
        // 设置Fragment的切换动画
        //ft.setCustomAnimations(R.anim.cu_push_right_in, R.anim.cu_push_left_out);

        // 判断页面是否已经创建，如果已经创建，那么就隐藏掉
        if (homeFragment != null) {
            ft.hide(homeFragment);
        }
        if (chatFragment != null) {
            ft.hide(chatFragment);
        }
        if (findFragment != null) {
            ft.hide(findFragment);
        }
        if (meFragment != null) {
            ft.hide(meFragment);
        }


        ft.show(fragment);
        ft.commitAllowingStateLoss();
    }

    //添加Fragment
    private void addFragment(Fragment fragment) {
        FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
        ft.add(R.id.show_layout, fragment);
        ft.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_home:
                index = 0;
                if (homeFragment == null) {
                    homeFragment = new HomeFragment();
                    addFragment(homeFragment);
                    showFragment(homeFragment);
                } else {
                    if (homeFragment.isHidden()) {
                        showFragment(homeFragment);
                    }
                }
                break;
            case R.id.bt_chat:
                index = 1;
                if (chatFragment == null) {
                    chatFragment = new ChatFragment();
                    addFragment(chatFragment);
                    showFragment(chatFragment);
                } else {
                    if (chatFragment.isHidden()) {
                        showFragment(chatFragment);
                    }
                }
                break;
            case R.id.iv_cramera:
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_find:
                index = 2;
                if (findFragment == null) {
                    findFragment = new FindFragment();
                    addFragment(findFragment);
                    showFragment(findFragment);
                } else {
                    if (findFragment.isHidden()) {
                        showFragment(findFragment);
                    }
                }
                break;
            case R.id.bt_me:
                index = 3;
                if (meFragment == null) {
                    meFragment = new MeFragment();
                    addFragment(meFragment);
                    showFragment(meFragment);
                } else {
                    if (meFragment.isHidden()) {
                        showFragment(meFragment);
                    }
                }
                break;
        }
        for (int i = 0; i < bt_menu.length; i++) {
            bt_menu[i].setImageResource(select_off[i]);
        }
        bt_menu[index].setImageResource(select_on[index]);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void back(View view) {
        finish();
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
                MainActivity.this.startActivityForResult(intent, 200);
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
                        }
                        JSONObject js = null;
                        try {
                            js = new JSONObject(apiClientResponse.getData());
                            String data = js.getString("info");
                            LogUtils.i("main", "成功" + data);
                            Gson gson = new Gson();
                            User user1 = gson.fromJson(data, User.class);
                            EventBus.getDefault().post(new Avatar(user1.getAvatar(), apiClientResponse.getRet()));
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("user", data);
                            SharedPreferencesUtils.writeSharedPreferences(MainActivity.this, "user", map);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });
    }

    @OnClick({R.id.id_view_login, R.id.id_view_register})
    public void noUser(View view) {
        switch (view.getId()) {
            case R.id.id_view_login:
                IntentUtils.doIntent(this, Login1Activity.class);
                ActivityCollector.removeActivity(this);
                break;
            case R.id.id_view_register:
                IntentUtils.doIntent(this, RegisterActivity.class);
                break;
        }
    }
}
