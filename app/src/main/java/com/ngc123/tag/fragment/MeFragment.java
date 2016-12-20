package com.ngc123.tag.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.okhttp.Request;
import com.ngc123.tag.R;
import com.ngc123.tag.api.ApiCallback;
import com.ngc123.tag.api.ApiClient;
import com.ngc123.tag.api.ApiClientResponse;
import com.ngc123.tag.bean.User;
import com.ngc123.tag.event.Avatar;
import com.ngc123.tag.ui.MainActivity;
import com.ngc123.tag.ui.MeFeedActivity;
import com.ngc123.tag.ui.PersonalMessActivity;
import com.ngc123.tag.ui.askfriend.MeMyCommentsActivity;
import com.ngc123.tag.ui.tag.MeMyFriendActivity;
import com.ngc123.tag.ui.SettingActivity;
import com.ngc123.tag.ui.WebviewActivity;
import com.ngc123.tag.ui.mefollowees.MeFolloweesActivity;
import com.ngc123.tag.ui.mefollowers.MeFollowersActivity;
import com.ngc123.tag.util.IntentUtils;
import com.ngc123.tag.util.SelectPhotoUtils;
import com.ngc123.tag.util.ToastUtils;
import com.ngc123.tag.util.UserHelp;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;


/**
 * Created by JiaM on 2016/3/15.
 */
public class MeFragment extends Fragment implements View.OnClickListener {
    @Bind(R.id.title_setting)
    TextView titleSetting;
    @Bind(R.id.id_iv_change_mess)
    ImageView idIvChangeMess;
    //    头像
    private ImageView me_avator;
    //    用户名
    private TextView me_username;
    //    动态点击
    private LinearLayout me_dongtai_bt;
    //    动态数目
    private TextView me_dongtai_num;
    //    关注点击
    private LinearLayout me_attention_bt;
    //    关注数目
    private TextView me_attention_num;
    //    粉丝点击
    private LinearLayout me_fans_bt;
    //    粉丝数目
    private TextView me_fans_num;
    //    我的好友
    private LinearLayout me_myfriend_bt;
    //    我的评论
    private LinearLayout me_mymention_bt;
    //    我的相册
    private LinearLayout me_myphoto_bt;

    private String feeds  = "0";
    private String followee = "0";
    private String follower = "0";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.me_main, container, false);
        init(view);
        operate();
        getInfoCount();
        EventBus.getDefault().register(this);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        User user = UserHelp.getInstance().getCurrentUser(getActivity());
        if (user != null) {
            changerAvator(user);
        }
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    me_dongtai_num.setText(feeds);
                    me_attention_num.setText(followee);
                    me_fans_num.setText(follower);
                    break;
            }
        }
    };

    private void getInfoCount(){
        User user = UserHelp.getInstance().getCurrentUser(getActivity());
        if (user != null){
            ApiClient.create()
                    .withService("User.GetInfoCount")
                    .addParams("uD", user.getUid())
                    .addParams("3917e150bbaa953b", user.getSessionToken())
                    .request(new ApiCallback() {
                        @Override
                        public void onFailure(Request request, IOException e) {
                            handler.sendEmptyMessage(1);
                        }

                        @Override
                        public void onResponse(ApiClientResponse apiClientResponse) throws IOException {
                            if (apiClientResponse.getRet() == 200){
                                try {
                                    JSONObject js = new JSONObject(apiClientResponse.getData());
                                    String code = js.getString("code");
                                    if (code.equals("0")){
                                        String data = js.getString("data");
                                        JSONObject jsonObject = new JSONObject(data);
                                        followee = jsonObject.getString("followee");
                                        follower = jsonObject.getString("follower");
                                        feeds = jsonObject.getString("feeds");
                                        handler.sendEmptyMessage(1);
                                    }else {
                                        handler.sendEmptyMessage(1);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }else {
                                handler.sendEmptyMessage(1);
                            }
                        }
                    });
        }
    }

    private void changerAvator(User u) {
        if (!TextUtils.isEmpty(u.getUsername())) {
            me_username.setText(u.getUsername());
        }
        if (!TextUtils.isEmpty(u.getAvatar())) {
            StringBuffer sb = new StringBuffer("http://7xstnm.com2.z0.glb.clouddn.com/");
            sb.append(u.getAvatar());
            String r = sb.toString();
            Picasso.with(getActivity()).load(r).placeholder(R.drawable.default_avatar).error(R.drawable.default_avatar).into(me_avator);
        }
    }

    //初始化
    private void init(View view) {

        //个人信息
        me_avator = (ImageView) view.findViewById(R.id.me_avatar);
        me_username = (TextView) view.findViewById(R.id.me_username);
        /*动态*/
        me_dongtai_bt = (LinearLayout) view.findViewById(R.id.me_dongtai_bt);
        me_dongtai_num = (TextView) view.findViewById(R.id.me_dongtai_num);
        /*关注*/
        me_attention_bt = (LinearLayout) view.findViewById(R.id.me_attention_bt);
        me_attention_num = (TextView) view.findViewById(R.id.me_attention_num);
        /*粉丝*/
        me_fans_bt = (LinearLayout) view.findViewById(R.id.me_fans_bt);
        me_fans_num = (TextView) view.findViewById(R.id.me_fans_num);
        /*我的好友*/
        me_myfriend_bt = (LinearLayout) view.findViewById(R.id.me_myfriend_bt);
        /*我的评论*/
        me_mymention_bt = (LinearLayout) view.findViewById(R.id.me_mymention_bt);
        /*我的相册*/
        me_myphoto_bt = (LinearLayout) view.findViewById(R.id.me_myphoto_bt);

    }

    //操作
    private void operate() {
        me_avator.setOnClickListener(this);
        me_dongtai_bt.setOnClickListener(this);
        me_attention_bt.setOnClickListener(this);
        me_fans_bt.setOnClickListener(this);
        me_myfriend_bt.setOnClickListener(this);
        me_mymention_bt.setOnClickListener(this);
        me_myphoto_bt.setOnClickListener(this);
    }


    int READ_EXTERNAL_STORAGE = 10000;

    /*点击获取事件*/
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.me_avatar:
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    //申请WRITE_EXTERNAL_STORAGE权限
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            READ_EXTERNAL_STORAGE);
                } else {
                    SelectPhotoUtils.openAlbum(getActivity(), true, 1, MainActivity.photo_list, 100);
                }
                break;
            case R.id.me_dongtai_bt:
                //动态
                Intent me_dongtai_bt = new Intent(getActivity(), MeFeedActivity.class);
                startActivity(me_dongtai_bt);
                break;
            case R.id.me_attention_bt:
                //关注的人
                Intent me_attention_bt = new Intent(getActivity(), MeFolloweesActivity.class);
                startActivity(me_attention_bt);
                break;
            case R.id.me_fans_bt:
                //粉丝
                Intent me_fans_bt = new Intent(getActivity(), MeFollowersActivity.class);
                startActivity(me_fans_bt);
                break;
            case R.id.me_myfriend_bt:
                //我的标签
                Intent me_myfriend_bt = new Intent(getActivity(), MeMyFriendActivity.class);
                startActivity(me_myfriend_bt);
                break;
            case R.id.me_mymention_bt:
                //邀请朋友
                Intent me_mymention_bt = new Intent(getActivity(), MeMyCommentsActivity.class);
                startActivity(me_mymention_bt);
                break;
            case R.id.me_myphoto_bt:
                //新手指南
                Bundle bundle = new Bundle();
                bundle.putString("title", "新手指南");
                bundle.putString("url", "http://www.baidu.com");
                IntentUtils.doIntentWithBundle(getActivity(), WebviewActivity.class, "urlbundle", bundle);
                break;
        }

    }

    public void onEventMainThread(Avatar avatar) {
        if (avatar.getCode() == 200) {
            ToastUtils.toast(getActivity(), "头像上传成功");
            if (!TextUtils.isEmpty(avatar.getUrl())) {
                StringBuffer sb = new StringBuffer("http://7xstnm.com2.z0.glb.clouddn.com/");
                sb.append(avatar.getUrl());
                String r = sb.toString();
                Picasso.with(getActivity()).load(r).placeholder(R.drawable.default_avatar).error(R.drawable.default_avatar).into(me_avator);
            }
        } else {
            ToastUtils.toast(getActivity(), "头像上传失败");
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        doNext(requestCode, grantResults);
    }

    private void doNext(int requestCode, int[] grantResults) {
        if (requestCode == READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                SelectPhotoUtils.openAlbum(getActivity(), true, 1, MainActivity.photo_list, 100);
            } else {
                ToastUtils.toast(getActivity(), "获取内存卡权限失败");
                // Permission Denied
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.title_setting, R.id.id_iv_change_mess})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.title_setting:
                Intent title_setting = new Intent(getActivity(), SettingActivity.class);
                startActivity(title_setting);
                break;
            case R.id.id_iv_change_mess:
                IntentUtils.doIntent(getActivity(), PersonalMessActivity.class);
                break;
        }
    }
}
