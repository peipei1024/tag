package com.ngc123.tag.ui.comment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.okhttp.Request;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ngc123.tag.api.ApiCallback;
import com.ngc123.tag.api.ApiClient;
import com.ngc123.tag.api.ApiClientResponse;
import com.ngc123.tag.bean.CommentBean;
import com.ngc123.tag.bean.User;
import com.ngc123.tag.event.Follow;
import com.ngc123.tag.R;
import com.ngc123.tag.model.FeedItem;
import com.ngc123.tag.util.LogUtils;
import com.ngc123.tag.util.SharedPreferencesUtils;
import com.ngc123.tag.util.ToastUtils;
import com.ngc123.tag.util.UserHelp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import de.greenrobot.event.EventBus;

/*
* Class name :Comment1Activity
*
* Version information :
*
* Describe ：
*
* Author ：裴徐泽
*
* Created by pei on 2016-7-31.
*
*/
public class Comment1Activity extends Activity implements SwipeRefreshLayout.OnRefreshListener {
    @Bind(R.id.id_iv_back_arrow)
    ImageView mIdIvBackArrow;
    @Bind(R.id.tag_title)
    LinearLayout mTagTitle;
    @Bind(R.id.operate_bottom)
    LinearLayout mOperateBottom;
    @Bind(R.id.recycleview)
    RecyclerView mRecycleview;
    @Bind(R.id.id_swipe)
    SwipeRefreshLayout mIdSwipe;
    @Bind(R.id.id_et_comment)
    EditText idEtComment;
    @Bind(R.id.id_btn_send)
    Button idBtnSend;
    @Bind(R.id.id_tv_share)
    ImageView idTvShare;
    private FeedItem feedItem;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    idEtComment.setText("");
                    InputMethodManager imm = (InputMethodManager) getSystemService(Comment1Activity.this.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(idEtComment.getWindowToken(), 0);
                    ToastUtils.toast(Comment1Activity.this, "评论成功");
                    onRefresh();
                    break;
                case 100:
                    mIdSwipe.setRefreshing(false);
                    mCommentAdapter.refresh(mList);
                    break;
                case 101:
                    mIdSwipe.setRefreshing(false);
                    ToastUtils.toast(Comment1Activity.this, "获取数据失败");
                    break;
                case 102:
                    ToastUtils.toast(Comment1Activity.this, "评论失败");
                    break;
                case 103:
//                    ToastUtils.toast(Comment1Activity.this, "没有评论了");
                    break;
            }
        }
    };

    private CommentBean commentBean;

    private List<TComment> mList = new ArrayList<>();
    private TComment mTComment;
    private CommentAdapter mCommentAdapter;
    private int lastVisibleItem;
    private final int pagesize = 5;
    private int page = 0;
    LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment1);
        ShareSDK.initSDK(this, "15aafdbd3bb44");
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        Intent intent = getIntent();
        feedItem = (FeedItem) intent.getSerializableExtra("feeditem");
        mCommentAdapter = new CommentAdapter(this, mList);
        getData();
        layoutManager = new LinearLayoutManager(this);
        mRecycleview.setLayoutManager(layoutManager);
        mRecycleview.setAdapter(mCommentAdapter);
        mRecycleview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == mCommentAdapter.getItemCount()) {
                    LogUtils.i("comment1", "到底了");
                    page++;
                    User user = UserHelp.getInstance().getCurrentUser(Comment1Activity.this);
                    if (user != null) {
                        ApiClient.create()
                                .withService("Comments.GetComments")
                                .addParams("imgId", feedItem.getImgId())
                                .addParams("uD", user.getUid())
                                .addParams("pG", page)
                                .addParams("num", pagesize)
                                .addParams("3917e150bbaa953b", user.getSessionToken())
                                .request(new ApiCallback() {
                                    @Override
                                    public void onFailure(Request request, IOException e) {

                                    }

                                    @Override
                                    public void onResponse(ApiClientResponse apiClientResponse) throws IOException {
                                        Message message = new Message();
                                        if (apiClientResponse.getRet() != 200) {
                                            message.what = 101;
                                            handler.sendMessage(message);
                                            return;
                                        }
                                        LogUtils.i("comment1", apiClientResponse.getRet() + " ");
                                        try {
                                            JSONObject jsonObject = new JSONObject(apiClientResponse.getData());
                                            String code = jsonObject.getString("code");
                                            if (code.equals("0")) {
                                                LogUtils.i("comment1", apiClientResponse.getData());

                                                String commentjs = jsonObject.getString("commentList");
                                                Gson gson = new Gson();
                                                List<CommentBean> list = gson.fromJson(commentjs, new TypeToken<List<CommentBean>>() {
                                                }.getType());
                                                if (list.size() == 0) {
                                                    message.what = 103;
                                                    handler.sendMessage(message);
                                                } else {
                                                    for (int a = 0; a < list.size(); a++) {
                                                        commentBean = new CommentBean(list.get(a).getId(),
                                                                list.get(a).getImgId(),
                                                                list.get(a).getUid(),
                                                                list.get(a).getComment(),
                                                                list.get(a).getCreateTime(),
                                                                list.get(a).getAvatar(),
                                                                list.get(a).getUsername());
                                                        mTComment = new TComment();
                                                        mTComment.setCommentBean(commentBean);
                                                        mTComment.setType(1);
                                                        mList.add(mTComment);

                                                    }
                                                    message.what = 100;
                                                    handler.sendMessage(message);
                                                }
                                            } else {
                                                message.what = 101;
                                                handler.sendMessage(message);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                    } else {
                        ToastUtils.toast(Comment1Activity.this, "请先登录");
                    }

                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            }
        });
        mIdSwipe.setOnRefreshListener(this);
    }

    @OnClick(R.id.id_iv_back_arrow)
    public void finsh() {
        finish();
    }


    public void onEventMainThread(Follow follow) {
        onRefresh();
        ToastUtils.toast(this, "操作成功");
    }

    private void getData() {
        User user = UserHelp.getInstance().getCurrentUser(this);
        if (user != null) {
            ApiClient.create()
                    .withService("Comments.GetComments")
                    .addParams("imgId", feedItem.getImgId())
                    .addParams("uD", user.getUid())
                    .addParams("pG", page)
                    .addParams("num", pagesize)
                    .addParams("3917e150bbaa953b", user.getSessionToken())
                    .request(new ApiCallback() {
                        @Override
                        public void onFailure(Request request, IOException e) {

                        }

                        @Override
                        public void onResponse(ApiClientResponse apiClientResponse) throws IOException {
                            Message message = new Message();
                            if (apiClientResponse.getRet() != 200) {
                                message.what = 101;
                                handler.sendMessage(message);
                                return;
                            }
                            LogUtils.i("comment1", apiClientResponse.getRet() + " ");
                            try {
                                JSONObject jsonObject = new JSONObject(apiClientResponse.getData());
                                String code = jsonObject.getString("code");
                                if (code.equals("0")) {
                                    LogUtils.i("comment1", apiClientResponse.getData());
                                    String img = jsonObject.getString("img");
                                    Gson gson = new Gson();
                                    FeedItem feed = gson.fromJson(img, FeedItem.class);
                                    String isfollowee = jsonObject.getString("isFollower");
                                    if (feed.getUid().equals(user.getUid())) {
                                        feed.setIsFollowee("s");
                                    } else {
                                        feed.setIsFollowee(isfollowee);
                                    }
                                    TComment mTComment1 = new TComment();
                                    mTComment1.setType(0);
                                    mTComment1.setFeedItem(feed);
                                    mList.add(mTComment1);


                                    String count = jsonObject.getString("len");
                                    TComment tComment = new TComment();
                                    tComment.setType(2);
                                    tComment.setCount(Integer.valueOf(count));
                                    mList.add(tComment);

                                    String commentjs = jsonObject.getString("commentList");
                                    Gson gson1 = new Gson();
                                    List<CommentBean> list = gson1.fromJson(commentjs, new TypeToken<List<CommentBean>>() {
                                    }.getType());
                                    if (list.size() == 0) {
//                                        message.what = 103;
//                                        handler.sendMessage(message);
                                    } else {
                                        for (int a = 0; a < list.size(); a++) {
                                            commentBean = new CommentBean(list.get(a).getId(),
                                                    list.get(a).getImgId(),
                                                    list.get(a).getUid(),
                                                    list.get(a).getComment(),
                                                    list.get(a).getCreateTime(),
                                                    list.get(a).getAvatar(),
                                                    list.get(a).getUsername());
                                            mTComment = new TComment();
                                            mTComment.setCommentBean(commentBean);
                                            mTComment.setType(1);
                                            mList.add(mTComment);

                                        }

                                    }
                                    message.what = 100;
                                    handler.sendMessage(message);
                                } else {
                                    message.what = 101;
                                    handler.sendMessage(message);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        } else {
            ToastUtils.toast(this, "请先登录");
        }


    }


    @OnClick(R.id.id_btn_send)
    public void onClick() {
        String comment = idEtComment.getText().toString().trim();
        if (!TextUtils.isEmpty(comment)) {
            User user = UserHelp.getInstance().getCurrentUser(this);
            if (user != null) {
                ApiClient.create()
                        .withService("Comments.SetComment")
                        .addParams("imgId", feedItem.getImgId())
                        .addParams("uD", user.getUid())
                        .addParams("comment", comment)
                        .addParams("3917e150bbaa953b", user.getSessionToken())
                        .request(new ApiCallback() {
                            @Override
                            public void onFailure(Request request, IOException e) {

                            }

                            @Override
                            public void onResponse(ApiClientResponse apiClientResponse) throws IOException {
                                Message message = new Message();
                                if (apiClientResponse.getRet() != 200) {
                                    message.what = 102;
                                    handler.sendMessage(message);
                                    return;
                                }
                                LogUtils.i("comment1", apiClientResponse.getRet() + " ");
                                try {
                                    JSONObject jsonObject = new JSONObject(apiClientResponse.getData());
                                    String code = jsonObject.getString("code");
                                    if (code.equals("0")) {
                                        LogUtils.i("comment1", "评论成功");
                                        message.what = 0;
                                        handler.sendMessage(message);
                                    } else {
                                        message.what = 102;
                                        handler.sendMessage(message);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
            } else {
                ToastUtils.toast(this, "请先登录");
            }
        } else {
            ToastUtils.toast(this, "请先输入内容");
        }
    }

    @Override
    public void onRefresh() {
        page = 0;
        mList.clear();
        getData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnClick(R.id.id_tv_share)
    public void share() {
        ShareSDK.initSDK(this);
        HashMap<String, String> map = (HashMap<String, String>) SharedPreferencesUtils.readShrePerface(this, "app");
        String link = map.get("link");
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间等使用
        oks.setTitle("TAG下载地址");
        // titleUrl是标题的网络链接，QQ和QQ空间等使用
        oks.setTitleUrl(link);
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我标记的美好生活，你一定要看！" + "TAG下载地址>>"  + link);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(link);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我标记的美好生活，你一定要看！" + "TAG下载地址>>"  + link);
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(link);

        // 启动分享GUI
        oks.show(this);
    }
}
