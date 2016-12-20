package com.ngc123.tag.ui.person;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.okhttp.Request;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.leancloud.im.tag.Chat_AVImClientManager;
import com.leancloud.im.tag.Chat_Constants;
import com.leancloud.im.tag.activity.Chat_AVSingleChatActivity;
import com.ngc123.tag.api.ApiCallback;
import com.ngc123.tag.api.ApiClient;
import com.ngc123.tag.api.ApiClientResponse;
import com.ngc123.tag.bean.User;
import com.ngc123.tag.R;
import com.ngc123.tag.model.FeedItem;
import com.ngc123.tag.util.LogUtils;
import com.ngc123.tag.util.ToastUtils;
import com.ngc123.tag.util.UserHelp;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
* Class name :PersonActivity
*
* Version information :
*
* Describe ：
*
* Author ：裴徐泽
*
* Created by pei on 2016-8-1.
*
*/
public class PersonActivity extends Activity {
    @Bind(R.id.id_iv_back_arrow)
    ImageView idIvBackArrow;
    @Bind(R.id.id_iv_head)
    ImageView idIvHead;
    @Bind(R.id.id_btn_follow)
    Button idBtnFollow;
    @Bind(R.id.id_gridview)
    GridView idGridview;
    @Bind(R.id.id_sv)
    ScrollView idSv;
    @Bind(R.id.id_sv_layout)
    LinearLayout idSvLayout;
    @Bind(R.id.id_pbr)
    ProgressBar idPbr;
    @Bind(R.id.id_tv_username)
    TextView idTvUsername;
    @Bind(R.id.id_tv_message)
    TextView idTvMessage;
    @Bind(R.id.id_iv_sex)
    ImageView idIvSex;
    @Bind(R.id.id_tv_location)
    TextView idTvLocation;
    @Bind(R.id.id_tv_descripe)
    TextView idTvDescripe;
    @Bind(R.id.id_tv_title)
    TextView idTvTitle;
    @Bind(R.id.id_tv_chat)
    TextView idTvChat;

    private int page = 0;
    private final int pagesize = 15;
    private String pid;
    private User mUser;
    private List<FeedItem> mList = new ArrayList<>();
    private PersonImageAdapter adapter;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    ToastUtils.toast(PersonActivity.this, "获取数据失败");
                    idPbr.setVisibility(View.GONE);
                    break;
                case 2:
//                    idPbr.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                    break;
                case 3:
                    adapter.notifyDataSetChanged();
                    changeUser(mUser);
                    if (mList.size() < 15) {
                        idPbr.setVisibility(View.GONE);
                    }
                    break;
                case 4:
                    ToastUtils.toast(PersonActivity.this, "自己不能关注自己哦");
                    break;
                case 5:
                    ToastUtils.toast(PersonActivity.this, "操作失败");
                    break;
                case 6:
                    ToastUtils.toast(PersonActivity.this, "操作成功");
                    String a = idBtnFollow.getText().toString();
                    if (a.equals("+ 关注")) {
                        mUser.setIsFollower("Y");
                    } else if (a.equals("已关注")) {
                        mUser.setIsFollower("N");
                    }
                    changeUser(mUser);
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        pid = intent.getStringExtra("id");
        LogUtils.i("person", "主页用户的id" + pid);
        init();
        preChat();
        getData(true);
    }

    private void init() {
        adapter = new PersonImageAdapter(this, mList);
        idGridview.setAdapter(adapter);
        idSv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        if (idSvLayout.getMeasuredHeight() <= idSv.getScrollY() + idSv.getHeight()) {
                            LogUtils.i("person", idSvLayout.getMeasuredHeight() + "  ");
                            LogUtils.i("person", idSv.getScrollY() + "  ");
                            LogUtils.i("person", idSv.getHeight() + "  ");
                            idPbr.setVisibility(View.VISIBLE);
                            page++;
                            getData(false);
                        }
                        break;
                }
                return false;
            }
        });
    }


    private void preChat(){
        User user = UserHelp.getInstance().getCurrentUser(this);
        LogUtils.i("chat-user", user.getUsername()+ "name");
        if (user != null){
            String conversationId = user.getUsername();
            getSquare(conversationId);
        }else {
            LogUtils.i("chat", "user is null");
        }
    }

    @OnClick({R.id.id_iv_back_arrow, R.id.id_btn_follow})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_iv_back_arrow:
                finish();
                break;
            case R.id.id_btn_follow:
                changeFollow();
                break;
        }
    }

    private void changeFollow() {
        User user = UserHelp.getInstance().getCurrentUser(this);
        if (user != null) {
            ApiClient apiClient = null;
            if (TextUtils.isEmpty(mUser.getIsFollower())) {
                if (user.getUid().equals(pid)) {
                    handler.sendEmptyMessage(4);
                    return;
                } else {
                    apiClient = follow(user);
                }
            } else if (mUser.getIsFollower().equals("Y")) {
                //取消关注
                apiClient = unFollow(user);
            } else if (mUser.getIsFollower().equals("N")) {
                apiClient = follow(user);
            }
            apiClient.request(new ApiCallback() {
                @Override
                public void onFailure(Request request, IOException e) {

                }

                @Override
                public void onResponse(ApiClientResponse apiClientResponse) throws IOException {
                    if (apiClientResponse.getRet() == 200) {
                        LogUtils.i("commentholder", apiClientResponse.getData());
                        handler.sendEmptyMessage(6);
                    } else {
                        handler.sendEmptyMessage(5);
                    }
                }
            });
        } else {
            ToastUtils.toast(this, "请先登录");
        }

    }

    private ApiClient unFollow(User user) {
        return ApiClient.create()
                .withService("User.UnFollow")
                .addParams("uD", user.getUid())
                .addParams("fD", pid)
                .addParams("3917e150bbaa953b", user.getSessionToken());
    }

    private ApiClient follow(User user) {
        return ApiClient.create()
                .withService("User.Follow")
                .addParams("uD", user.getUid())
                .addParams("fD", pid)
                .addParams("3917e150bbaa953b", user.getSessionToken());
    }


    private void changeUser(User user) {
        if (!TextUtils.isEmpty(user.getAvatar())) {
            StringBuffer sb = new StringBuffer("http://7xstnm.com2.z0.glb.clouddn.com/");
            sb.append(user.getAvatar());
            String r = sb.toString();
            Picasso.with(this).load(r).into(idIvHead);
        } else {
            Picasso.with(this).load(R.drawable.default_avatar).into(idIvHead);
        }
        if (!TextUtils.isEmpty(user.getUsername())) {
            idTvUsername.setText(user.getUsername());
            idTvTitle.setText(user.getUsername());
        } else {
            idTvUsername.setText("");
            idTvTitle.setText("");
        }
        if (!TextUtils.isEmpty(user.getSummary())) {
            idTvDescripe.setText(user.getSummary());
        } else {
            idTvDescripe.setText("这家伙很懒,什么都没写");
        }
        if (!TextUtils.isEmpty(user.getLocation())) {
            idTvLocation.setText(user.getLocation());
        } else {
            idTvLocation.setText("火星");
        }
        String wee;
        if (!TextUtils.isEmpty(user.getFollowee())) {
            wee = user.getFollowee();
        } else {
            wee = "0";
        }
        String wer;
        if (!TextUtils.isEmpty(user.getFollower())) {
            wer = user.getFollower();
        } else {
            wer = "0";
        }
        idTvMessage.setText(wee + " 关注的人,  " + wer + " 粉丝");
        if (user.getSex().equals("0")) {
            //男
        } else if (user.getSex().equals("1")) {
            //女
        } else if (user.getSex().equals("2")) {
            //不详
        }
        if (TextUtils.isEmpty(user.getIsFollower())) {
            if (UserHelp.getInstance().getCurrentUser(this).getUid().equals(pid)) {
                idBtnFollow.setText("自己");
                idBtnFollow.setBackground(getResources().getDrawable(R.drawable.btn_follow_bg_n));
                idBtnFollow.setTextColor(getResources().getColor(R.color.white));
                idTvChat.setVisibility(View.GONE);
            } else {
                idBtnFollow.setBackground(getResources().getDrawable(R.drawable.btn_follow_bg_n));
                idBtnFollow.setTextColor(getResources().getColor(R.color.white));
                idBtnFollow.setText("+ 关注");
            }
        } else if (user.getIsFollower().equals("Y")) {
            idBtnFollow.setBackground(getResources().getDrawable(R.drawable.btn_follow_bg_y));
            idBtnFollow.setTextColor(getResources().getColor(R.color.colorbtnfoolow));
            idBtnFollow.setText("已关注");
        } else if (user.getIsFollower().equals("N")) {
            idBtnFollow.setBackground(getResources().getDrawable(R.drawable.btn_follow_bg_n));
            idBtnFollow.setTextColor(getResources().getColor(R.color.white));
            idBtnFollow.setText("+ 关注");
        }
    }

    private void getData(boolean isfirst) {
        ApiClient apiClient = null;
        User user = UserHelp.getInstance().getCurrentUser(this);
        if (user != null) {
            if (isfirst) {
                apiClient = firstGetData(user);
            } else {
                apiClient = moreGetData(user);
            }
            apiClient.request(new ApiCallback() {
                @Override
                public void onFailure(Request request, IOException e) {

                }

                @Override
                public void onResponse(ApiClientResponse apiClientResponse) throws IOException {
                    if (apiClientResponse.getRet() == 200) {
                        try {
                            JSONObject jsonObject = new JSONObject(apiClientResponse.getData());
                            String code = jsonObject.getString("code");
                            if (code.equals("0")) {
                                Gson gson = new Gson();
                                LogUtils.i("person", apiClientResponse.getData());
                                String img = jsonObject.getString("img");
                                if (!TextUtils.isEmpty(img)) {
                                    List<FeedItem> l = gson.fromJson(img, new TypeToken<List<FeedItem>>() {
                                    }.getType());
                                    for (FeedItem feedItem : l) {
                                        mList.add(feedItem);
                                    }
                                    String header = jsonObject.getString("header");
                                    if (header.length() > 10) {
                                        LogUtils.i("person", header + " header");
                                        mUser = gson.fromJson(header, User.class);
                                        handler.sendEmptyMessage(3);
                                    } else {
                                        handler.sendEmptyMessage(2);
                                    }
                                }
                            } else {
                                handler.sendEmptyMessage(1);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        handler.sendEmptyMessage(1);
                    }
                }
            });
        } else {
            ToastUtils.toast(this, "请先登录");
        }
    }

    private ApiClient firstGetData(User user) {
        return ApiClient.create()
                .withService("User.Profile")
                .addParams("uD", user.getUid())
                .addParams("3917e150bbaa953b", user.getSessionToken())
                .addParams("pD", pid)
                .addParams("pG", page)
                .addParams("num", pagesize)
                .addParams("header", "true");
    }

    private ApiClient moreGetData(User user) {
        return ApiClient.create()
                .withService("User.Profile")
                .addParams("uD", user.getUid())
                .addParams("3917e150bbaa953b", user.getSessionToken())
                .addParams("pD", pid)
                .addParams("pG", page)
                .addParams("num", 9)
                .addParams("header", "false");
    }

    @OnClick(R.id.id_tv_chat)
    public void onClick() {
        Intent intent = new Intent(this, Chat_AVSingleChatActivity.class);
        intent.putExtra(Chat_Constants.MEMBER_ID, mUser.getUsername());
        LogUtils.i("chat", mUser.getUsername());
        startActivity(intent);
    }
    private AVIMConversation squareConversation;
    /**
     * 根据 conversationId 查取本地缓存中的 conversation，如若没有缓存，则返回一个新建的 conversaiton
     */
    private void getSquare(String conversationId) {
        if (TextUtils.isEmpty(conversationId)) {
            throw new IllegalArgumentException("conversationId can not be null");
        }

        AVIMClient client = Chat_AVImClientManager.getInstance().getClient();
        if (null != client) {
            squareConversation = client.getConversation(conversationId);
        } else {
            //finish();
            LogUtils.i("chat", "Please call AVIMClient.open first!");
            User user = UserHelp.getInstance().getCurrentUser(this);
            if (user != null){
                Chat_AVImClientManager.getInstance().open(user.getUsername(), new AVIMClientCallback() {
                    @Override
                    public void done(AVIMClient avimClient, AVIMException e) {
                        if (e == null){
                            LogUtils.i("mefriend", user.getUsername());
                            LogUtils.i("mefriend", "create tcp success");
                            getSquare(user.getUsername());
                        }
                    }
                });
            }else {
                LogUtils.i("mefriend", "user is null");
            }
        }
    }
}
