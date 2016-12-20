package com.ngc123.tag.fragment.find;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avos.avoscloud.okhttp.Request;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ngc123.tag.api.ApiCallback;
import com.ngc123.tag.api.ApiClient;
import com.ngc123.tag.api.ApiClientResponse;
import com.ngc123.tag.bean.User;
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

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by JiaM on 2016/3/15.
 */
public class FindFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    @Bind(R.id.id_recycle)
    RecyclerView idRecycle;
    @Bind(R.id.id_swipe)
    SwipeRefreshLayout idSwipe;
    private TextView findName;
    private FindAdapter adapter;
    private List<FeedItem> list = new ArrayList<>();

    private final int pagesize = 10;
    private int page = 0;
    private int lastitem;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.find_main, container, false);
        init(view);
        ButterKnife.bind(this, view);
        adapter = new FindAdapter(getActivity(), list);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        StaggeredGridLayoutManager layoutManager1 = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        idRecycle.setLayoutManager(layoutManager1);
        idRecycle.setAdapter(adapter);
        idSwipe.setOnRefreshListener(this);
        idRecycle.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastitem + 1 == adapter.getItemCount()) {
                    LogUtils.i("find", "到底了");
                    idSwipe.setRefreshing(true);
                    page++;
                    getData();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int [] lastVisibleItem =layoutManager1.findLastVisibleItemPositions(null);
                lastitem = Math.max(lastVisibleItem[0],lastVisibleItem[1]);
            }
        });
        return view;
    }



    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 2:
                    adapter.refresh(list);
                    idSwipe.setRefreshing(false);
                    LogUtils.i("find", list.size() + " ");
                    break;
                case 3:
                    ToastUtils.toast(getActivity(), "请求数据失败");
                    idSwipe.setRefreshing(false);
                    break;
                case 4:
                    ToastUtils.toast(getActivity(), "你已经在别处登录，请重新登录");
                    idSwipe.setRefreshing(false);
                    break;
                case 5:
                    ToastUtils.toast(getActivity(), "请检查网络");
                    idSwipe.setRefreshing(false);
                    break;

            }
        }
    };
    private ApiClient noUser(){
        return ApiClient.create()
                .withService("Feeds.GetImgListByHot")
                .addParams("pG",page)
                .addParams("num", pagesize);
    }
    private ApiClient yesUser(User user){
        return ApiClient.create()
                .withService("Feeds.GetImgListByHot")
                .addParams("pG",page)
                .addParams("num", pagesize)
                .addParams("uid", user.getUid())
                .addParams("3917e150bbaa953b", user.getSessionToken());

    }
    private void getData(){
        User user = UserHelp.getInstance().getCurrentUser(getActivity());
        ApiClient apiClient;
        if (user == null){
            apiClient = noUser();
        }else {
            apiClient = yesUser(user);
        }
        apiClient.request(new ApiCallback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        handler.sendEmptyMessage(5);
                    }

                    @Override
                    public void onResponse(ApiClientResponse apiClientResponse) throws IOException {
                        if(apiClientResponse.getRet() == 200){
                            LogUtils.i("find", apiClientResponse.getRet() + " ");
                            try {
                                JSONObject jsonObject = new JSONObject(apiClientResponse.getData());
                                String code = jsonObject.getString("code");
                                if (code.equals("0")){
                                    String l = jsonObject.getString("list");
                                    LogUtils.i("find", l);
                                    Gson gson = new Gson();
                                    List<FeedItem> itemList = gson.fromJson(l, new TypeToken<List<FeedItem>>(){}.getType());
                                    if (itemList.size() == 0){
                                        //不存在这种情况
                                    }else {
                                        for (FeedItem feedItem: itemList){
                                            list.add(feedItem);
                                        }
                                        handler.sendEmptyMessage(2);
                                    }
                                }else {
                                    handler.sendEmptyMessage(3);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else if (apiClientResponse.getRet() == 401){
                            handler.sendEmptyMessage(4);
                        }else {
                            handler.sendEmptyMessage(3);
                        }
                    }
                });
    }

    private void init(View view) {
        findName = (TextView) view.findViewById(R.id.title_name);
        findName.setText("发现");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onRefresh() {
        page = 0;
        list.clear();
        getData();
    }
}
