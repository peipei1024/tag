package com.ngc123.tag.ui.person;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
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
import com.ngc123.tag.R;
import com.ngc123.tag.api.ApiCallback;
import com.ngc123.tag.api.ApiClient;
import com.ngc123.tag.api.ApiClientResponse;
import com.ngc123.tag.bean.User;
import com.ngc123.tag.event.PersonEvent;
import com.ngc123.tag.fragment.find.SpaceItemDecoration;
import com.ngc123.tag.model.FeedItem;
import com.ngc123.tag.util.LogUtils;
import com.ngc123.tag.util.ScreenUtils;
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
import de.greenrobot.event.EventBus;

/*
* Class name :Person1Activity
*
* Version information :
*
* Describe ：
*
* Author ：裴徐泽
*
* Created by pei on 2016-8-3.
*
*/
public class Person1Activity extends Activity {
    @Bind(R.id.id_tv_title)
    TextView idTvTitle;
    @Bind(R.id.id_tv_chat)
    TextView idTvChat;
    @Bind(R.id.id_iv_back_arrow)
    ImageView idIvBackArrow;
    @Bind(R.id.id_recycle)
    RecyclerView idRecycle;
    private int page = 0;
    private final int pagesize = 15;
    public static String pid;
    private User mUser;

    private PersonAdapter adapter;
    private List<PersonBean> mList = new ArrayList<>();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    ToastUtils.toast(Person1Activity.this, "获取数据失败");
                    break;
                case 2:
//                    idPbr.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                    break;
                case 3:
                    if (!TextUtils.isEmpty(mUser.getUsername())) {
                        idTvTitle.setText(mUser.getUsername());
                    } else {
                        idTvTitle.setText("");
                    }
                    adapter.refresh(mList);
                    break;
                case 4:
                    ToastUtils.toast(Person1Activity.this, "自己不能关注自己哦");
                    break;
                case 5:
                    ToastUtils.toast(Person1Activity.this, "操作失败");
                    break;
                case 6:
                    ToastUtils.toast(Person1Activity.this, "操作成功");
//                    String a = idBtnFollow.getText().toString();
//                    if (a.equals("+ 关注")) {
//                        mUser.setIsFollower("Y");
//                    } else if (a.equals("已关注")) {
//                        mUser.setIsFollower("N");
//                    }
//                    changeUser(mUser);
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person1);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        Intent intent = getIntent();
        pid = intent.getStringExtra("id");
        LogUtils.i("person", "主页用户的id" + pid);
        init();
        preChat();
        firstGetData();
        int a = ScreenUtils.getWidth(this);
        LogUtils.i("person", "主页用户的id" + a);
    }
    private int lastitem;
    private void init() {
        GridLayoutManager manager = new GridLayoutManager(this, 3);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position == 0 ? 3 : 1;
            }
        });
        idRecycle.addItemDecoration(new SpaceItemDecoration(5));
        idRecycle.setLayoutManager(manager);
        adapter = new PersonAdapter(Person1Activity.this, mList);
        idRecycle.setAdapter(adapter);

        if (UserHelp.getInstance().getCurrentUser(this).getUid().equals(Person1Activity.pid)) {
                idTvChat.setVisibility(View.GONE);
        }

        idRecycle.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastitem + 1 == adapter.getItemCount()) {
                    LogUtils.i("find", "到底了");
//                    Picasso.with(Person1Activity.this).resumeTag(tag);
                    page++;
                    moreGetData();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                int [] lastVisibleItem = manager.findLastVisibleItemPositions(null);
                lastitem = manager.findLastVisibleItemPosition();
//                lastitem = Math.max(lastVisibleItem[0],lastVisibleItem[1]);
            }
        });

    }
    public void onEventMainThread(PersonEvent p){
        if (p.getCode() == 5){
            ToastUtils.toast(Person1Activity.this, "操作失败");
        }else if (p.getCode() == 6){
            ToastUtils.toast(Person1Activity.this, "操作成功");
            mList.clear();
            firstGetData();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void preChat() {
        User user = UserHelp.getInstance().getCurrentUser(this);
        LogUtils.i("chat-user", user.getUsername() + "name");
        if (user != null) {
            String conversationId = user.getUsername();
            getSquare(conversationId);
        } else {
            LogUtils.i("chat", "user is null");
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





    private void firstGetData() {
        User user = UserHelp.getInstance().getCurrentUser(this);
        if (user != null) {
            ApiClient.create()
                    .withService("User.Profile")
                    .addParams("uD", user.getUid())
                    .addParams("3917e150bbaa953b", user.getSessionToken())
                    .addParams("pD", pid)
                    .addParams("pG", page)
                    .addParams("num", pagesize)
                    .addParams("header", "true")
                    .request(new ApiCallback() {
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
                                        String header = jsonObject.getString("header");
                                        LogUtils.i("person", header + " header");
                                        if (header.length() > 5) {
                                            mUser = gson.fromJson(header, User.class);
                                            PersonBean bean = new PersonBean();
                                            bean.setType(0);
                                            bean.setUser(mUser);
                                            mList.add(bean);
                                        }
                                        String img = jsonObject.getString("img");
                                        if (!TextUtils.isEmpty(img)) {
                                            List<FeedItem> l = gson.fromJson(img, new TypeToken<List<FeedItem>>() {}.getType());
                                            for (FeedItem feedItem: l){
                                                PersonBean bean = new PersonBean();
                                                bean.setType(1);
                                                bean.setFeedItem(feedItem);
                                                mList.add(bean);
                                            }
                                        }
                                        handler.sendEmptyMessage(3);
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

    private void moreGetData() {

        User user = UserHelp.getInstance().getCurrentUser(this);
        if (user != null) {
            ApiClient.create()
                    .withService("User.Profile")
                    .addParams("uD", user.getUid())
                    .addParams("3917e150bbaa953b", user.getSessionToken())
                    .addParams("pD", pid)
                    .addParams("pG", page)
                    .addParams("num", 9)
                    .addParams("header", "false")
                    .request(new ApiCallback() {
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
                                String img = jsonObject.getString("img");
                                if (!TextUtils.isEmpty(img)) {
                                    Gson gson = new Gson();
                                    List<FeedItem> l = gson.fromJson(img, new TypeToken<List<FeedItem>>() {}.getType());
                                    for (FeedItem feedItem: l){
                                        PersonBean bean = new PersonBean();
                                        bean.setType(1);
                                        bean.setFeedItem(feedItem);
                                        mList.add(bean);
                                    }

//                                    PersonBean bean = mList.get(1);
//                                    List<FeedItem> beanlist = bean.getList();
//                                    for (FeedItem feedItem: l) {
//                                        beanlist.add(feedItem);
//                                    }
//                                    bean.setType(1);
//                                    bean.setList(beanlist);
                                    handler.sendEmptyMessage(3);
                                }
                            }else {
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
            if (user != null) {
                Chat_AVImClientManager.getInstance().open(user.getUsername(), new AVIMClientCallback() {
                    @Override
                    public void done(AVIMClient avimClient, AVIMException e) {
                        if (e == null) {
                            LogUtils.i("mefriend", user.getUsername());
                            LogUtils.i("mefriend", "create tcp success");
                            getSquare(user.getUsername());
                        }
                    }
                });
            } else {
                LogUtils.i("mefriend", "user is null");
            }
        }
    }

    @OnClick({R.id.id_tv_chat, R.id.id_iv_back_arrow})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_tv_chat:
                Intent intent = new Intent(this, Chat_AVSingleChatActivity.class);
                intent.putExtra(Chat_Constants.MEMBER_ID, mUser.getUsername());
                LogUtils.i("chat", mUser.getUsername());
                startActivity(intent);
                break;
            case R.id.id_iv_back_arrow:
                finish();
                break;
        }
    }
}


