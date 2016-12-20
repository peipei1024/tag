package com.ngc123.tag.ui.mefollowers;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.avos.avoscloud.okhttp.Request;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ngc123.tag.R;
import com.ngc123.tag.api.ApiCallback;
import com.ngc123.tag.api.ApiClient;
import com.ngc123.tag.api.ApiClientResponse;
import com.ngc123.tag.bean.User;
import com.ngc123.tag.ui.mefollowees.MeFolloweAdapter;
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
public class MeFollowersActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener {


    @Bind(R.id.id_recycle)
    RecyclerView idRecycle;
    @Bind(R.id.id_swipe)
    SwipeRefreshLayout idSwipe;
    @Bind(R.id.id_iv_back_arrow)
    ImageView idIvBackArrow;

    private List<User> list = new ArrayList<>();
    private MeFolloweAdapter adapter;
    private final int pagesize = 10;
    private int page = 0;
    private int lastVisibleItem = 0;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    idSwipe.setRefreshing(false);
                    ToastUtils.toast(MeFollowersActivity.this, "获取数据失败");
                    break;
                case 2:
                    adapter.refresh(list);
                    idSwipe.setRefreshing(false);
                    break;
                case 4:
                    ToastUtils.toast(MeFollowersActivity.this, "你已经在别处登录，请重新登录");
                    idSwipe.setRefreshing(false);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.me_followers);
        ButterKnife.bind(this);
        idSwipe.setOnRefreshListener(this);
        adapter = new MeFolloweAdapter(this, list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        idRecycle.setLayoutManager(layoutManager);
        idRecycle.setAdapter(adapter);
        idRecycle.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == adapter.getItemCount()) {
                    if (list.size() > 10) {
                        page++;
                        getData();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            }
        });
        getData();
    }

    private void getData() {
        User user = UserHelp.getInstance().getCurrentUser(this);
        if (user == null) {
            ToastUtils.toast(this, "请先登录");
        } else {
            ApiClient.create()
                    .withService("User.GetFollower")
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
                            if (apiClientResponse.getRet() == 200) {
                                LogUtils.i("mefollowe", apiClientResponse.getData());
                                try {
                                    JSONObject jsonObject = new JSONObject(apiClientResponse.getData());
                                    String code = jsonObject.getString("code");
                                    if (code.equals("0")) {
                                        String followee = jsonObject.getString("follower");
                                        Gson gson = new Gson();
                                        List<User> l = gson.fromJson(followee, new TypeToken<List<User>>() {
                                        }.getType());
                                        for (User user1 : l) {
                                            list.add(user1);
                                        }
                                        handler.sendEmptyMessage(2);
                                    } else {
                                        handler.sendEmptyMessage(1);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else if (apiClientResponse.getRet() == 401) {
                                handler.sendEmptyMessage(3);
                            } else {
                                handler.sendEmptyMessage(1);
                            }
                        }
                    });

        }
    }





    @Override
    public void onRefresh() {
        page = 0;
        list.clear();
        getData();
    }

    @OnClick(R.id.id_iv_back_arrow)
    public void onClick() {
        finish();
    }
}
