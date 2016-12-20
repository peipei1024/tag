package com.ngc123.tag.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avos.avoscloud.okhttp.Request;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ngc123.tag.api.ApiCallback;
import com.ngc123.tag.api.ApiClient;
import com.ngc123.tag.api.ApiClientResponse;
import com.ngc123.tag.bean.User;
import com.ngc123.tag.fragment.Feed1Adapter.Feed1Adapter;
import com.ngc123.tag.R;
import com.ngc123.tag.model.FeedItem;
import com.ngc123.tag.util.LogUtils;
import com.ngc123.tag.util.ToastUtils;
import com.ngc123.tag.util.UserHelp;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by JiaM on 2016/3/15.
 */
public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private Feed1Adapter feedAdapter;
    private List<FeedItem> feedList = new ArrayList<>();
    private Context mContext;
    public static int count = 0;
    private boolean pendingIntroAnimation;
    private int lastVisibleItem;
    private int page = 0;//页数初始为0

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.i("测试token", UserHelp.getInstance().getCurrentUser(getActivity()).getSessionToken());
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_main, container, false);
        mContext = getActivity();
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setColorSchemeColors(R.color.main_color,R.color.yellow,R.color.white);
        mSwipeRefreshLayout.setOnRefreshListener(this);


        mRecyclerView = (RecyclerView) view.findViewById(R.id.rvFeed);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        setupFeed();

        //获得数据
        getFeeds();
        LogUtils.i("home", "home");
        return view;
    }


    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    ToastUtils.toast(getActivity(), "没有数据了");
                    mSwipeRefreshLayout.setRefreshing(false);
                    break;
                case 2:
                    ToastUtils.toast(getActivity(), "你已经在别处登录，请重新登录");
                    mSwipeRefreshLayout.setRefreshing(false);
                    break;
                case 3:
                    feedAdapter.refresh(feedList);
                    mSwipeRefreshLayout.setRefreshing(false);
                    break;
                case 4:
                    ToastUtils.toast(getActivity(), "获取数据失败");
                    mSwipeRefreshLayout.setRefreshing(false);
                    break;
                case 5:
                    ToastUtils.toast(getActivity(), "请检查网络");
                    mSwipeRefreshLayout.setRefreshing(false);
                    break;
            }
        }
    };


    private void setupFeed() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 300;
            }
        };
        mRecyclerView.setLayoutManager(linearLayoutManager);

        feedAdapter = new Feed1Adapter(mContext);
        mRecyclerView.setAdapter(feedAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == feedAdapter.getItemCount()){
                    mSwipeRefreshLayout.setRefreshing(true);
                    page++;
                    getFeeds();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem =linearLayoutManager.findLastVisibleItemPosition();
            }
        });
    }

    public void onEventMainThread(FeedItem feedItem){
        onRefresh();
    }



    @Override
    public void onRefresh() {
        feedList.clear();
        page = 0;
        getFeeds();
    }

    private void getFeeds(){
        User user = UserHelp.getInstance().getCurrentUser(getActivity());
        LogUtils.i("home", user.getUid());
        ApiClient.create()
                .withService("Feeds.GetFolloweeImgList")
                .addParams("uid", user.getUid())
                .addParams("page",page)
                .addParams("3917e150bbaa953b", user.getSessionToken())
                .request(new ApiCallback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        LogUtils.i("home", e.getMessage());
                        mHandler.sendEmptyMessage(5);
                    }

                    @Override
                    public void onResponse(ApiClientResponse apiClientResponse) throws IOException {
                        LogUtils.i("home", apiClientResponse.getData());
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
                                        mHandler.sendEmptyMessage(1);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }





}
