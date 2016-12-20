package com.ngc123.tag.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.avos.avoscloud.okhttp.Request;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ngc123.tag.R;
import com.ngc123.tag.api.ApiCallback;
import com.ngc123.tag.api.ApiClient;
import com.ngc123.tag.api.ApiClientResponse;
import com.ngc123.tag.bean.User;
import com.ngc123.tag.fragment.Feed1Adapter.Feed1Adapter;
import com.ngc123.tag.model.FeedItem;
import com.ngc123.tag.util.LogUtils;
import com.ngc123.tag.util.ToastUtils;
import com.ngc123.tag.util.UserHelp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by JiaM on 2016/3/20.
 */
public class MeFeedActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener{

    @Bind(R.id.id_iv_back_arrow)
    ImageView idIvBackArrow;
    @Bind(R.id.rvFeed)
    RecyclerView rvFeed;
    @Bind(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;
    private int page = 0;

    private List<FeedItem> feedList = new ArrayList<>();
    private Feed1Adapter adapter;
    private int lastVisibleItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.me_feed);
        ButterKnife.bind(this);
        swipeRefresh.setOnRefreshListener(this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvFeed.setLayoutManager(manager);
        adapter = new Feed1Adapter(this);
        rvFeed.setAdapter(adapter);
        getFeeds();
        rvFeed.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == adapter.getItemCount()){
                    swipeRefresh.setRefreshing(true);
                    page++;
                    getFeeds();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem =manager.findLastVisibleItemPosition();
            }
        });
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    ToastUtils.toast(MeFeedActivity.this, "没有数据了");
                    swipeRefresh.setRefreshing(false);
                    break;
                case 2:
                    ToastUtils.toast(MeFeedActivity.this, "你已经在别处登录，请重新登录");
                    swipeRefresh.setRefreshing(false);
                    break;
                case 3:
                    adapter.refresh(feedList);
                    swipeRefresh.setRefreshing(false);
                    break;
                case 4:
                    ToastUtils.toast(MeFeedActivity.this, "获取数据失败");
                    swipeRefresh.setRefreshing(false);
                    break;
                case 5:
                    ToastUtils.toast(MeFeedActivity.this, "请检查网络");
                    swipeRefresh.setRefreshing(false);
                    break;
            }
        }
    };


    private void getFeeds(){
        User user = UserHelp.getInstance().getCurrentUser(this);
        LogUtils.i("home", user.getUid());
        ApiClient.create()
                .withService("Feeds.GetSelfImgList")
                .addParams("uid", user.getUid())
                .addParams("page",page)
                .addParams("3917e150bbaa953b", user.getSessionToken())
                .request(new ApiCallback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        LogUtils.i("mefeed", e.getMessage());
                        mHandler.sendEmptyMessage(5);
                    }

                    @Override
                    public void onResponse(ApiClientResponse apiClientResponse) throws IOException {
                        LogUtils.i("mefeed", apiClientResponse.getData());
                        if(apiClientResponse.getRet() == 200){
                            try {
                                JSONObject js = new JSONObject(apiClientResponse.getData());
                                int code = js.getInt("code");
                                if (code == 0){
                                    String list = js.getString("list");
                                    if (list.length() > 10){
                                        Gson gson = new Gson();
                                        List<FeedItem> list1 = gson.fromJson(list, new TypeToken<List<FeedItem>>(){}.getType());
                                        for (FeedItem feedItem: list1){
                                            feedList.add(feedItem);
                                        }
                                        mHandler.sendEmptyMessage(3);
                                    }else {
//                                        mHandler.sendEmptyMessage(1);
                                    }
                                }else {
                                    mHandler.sendEmptyMessage(4);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else if (apiClientResponse.getRet() == 401){
                            mHandler.sendEmptyMessage(2);
                        }else {
                            mHandler.sendEmptyMessage(4);
                        }
                    }
                });
    }



    @OnClick(R.id.id_iv_back_arrow)
    public void onClick() {
        finish();
    }

    @Override
    public void onRefresh() {
        feedList.clear();
        page = 0;
        getFeeds();
    }
}
