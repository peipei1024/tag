package com.ngc123.tag.ui.tag;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.okhttp.Request;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ngc123.tag.R;
import com.ngc123.tag.api.ApiCallback;
import com.ngc123.tag.api.ApiClient;
import com.ngc123.tag.api.ApiClientResponse;
import com.ngc123.tag.bean.User;
import com.ngc123.tag.model.TagItem;
import com.ngc123.tag.util.LogUtils;
import com.ngc123.tag.util.ToastUtils;
import com.ngc123.tag.util.UserHelp;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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
public class MeMyFriendActivity extends Activity {

    @Bind(R.id.id_iv_back_arrow)
    ImageView idIvBackArrow;
    @Bind(R.id.id_recycle)
    RecyclerView idRecycle;

    private List<TagItem> tag_list = new ArrayList<>();
    private TagAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.me_myfriend);
        ButterKnife.bind(this);
        getData();

        adapter = new TagAdapter(this, tag_list);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        idRecycle.setLayoutManager(manager);
        idRecycle.setAdapter(adapter);

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 100:
                    ToastUtils.toast(MeMyFriendActivity.this, "获取数据失败");
                    break;
                case 101:
                    adapter.refresh(tag_list);
                    break;
            }
        }
    };

    private void getData() {
        User user = UserHelp.getInstance().getCurrentUser(this);
        ApiClient.create()
                .withService("Tag.GetTagLibrary")
                .addParams("uD", user.getUid())
                .addParams("3917e150bbaa953b", user.getSessionToken())
                .request(new ApiCallback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        handler.sendEmptyMessage(100);
                    }

                    @Override
                    public void onResponse(ApiClientResponse apiClientResponse) throws IOException {
                        if (apiClientResponse.getRet() == 200) {
                            try {
                                JSONObject js = new JSONObject(apiClientResponse.getData());
                                LogUtils.i("tag", apiClientResponse.getData());
                                String code = js.getString("code");
                                if (code.equals("0")) {
                                    String list = js.getString("list");
                                    Gson gson = new Gson();
                                    tag_list = gson.fromJson(list, new TypeToken<List<TagItem>>() {
                                    }.getType());
                                    handler.sendEmptyMessage(101);
                                } else {
                                    handler.sendEmptyMessage(100);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            handler.sendEmptyMessage(100);
                        }
                    }
                });
    }

    @OnClick(R.id.id_iv_back_arrow)
    public void onClick() {
        finish();
    }
}
