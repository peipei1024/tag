package com.ngc123.tag.ui.mefollowees;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.okhttp.Request;
import com.ngc123.tag.R;
import com.ngc123.tag.api.ApiCallback;
import com.ngc123.tag.api.ApiClient;
import com.ngc123.tag.api.ApiClientResponse;
import com.ngc123.tag.bean.User;
import com.ngc123.tag.ui.person.Person1Activity;
import com.ngc123.tag.ui.person.PersonActivity;
import com.ngc123.tag.util.IntentUtils;
import com.ngc123.tag.util.LogUtils;
import com.ngc123.tag.util.ToastUtils;
import com.ngc123.tag.util.UserHelp;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MeFolloweeHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.id_iv_head)
    ImageView idIvHead;
    @Bind(R.id.id_tv_name)
    TextView idTvName;
    @Bind(R.id.id_tv_sign)
    TextView idTvSign;
    @Bind(R.id.id_btn_follow)
    Button idBtnFollow;
    @Bind(R.id.id_view_follower_item)
    LinearLayout idViewFollowerItem;

    private Context context;
    private User u;


    public MeFolloweeHolder(LayoutInflater inflater, ViewGroup parent) {
        this(inflater.inflate(R.layout.view_person_item, parent, false));
    }

    public MeFolloweeHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public void bindData(User user, Context context) {
        this.context = context;
        this.u = user;
        if (!TextUtils.isEmpty(user.getUsername())) {
            idTvName.setText(user.getUsername());
        } else {
            idTvName.setText("");
        }
        if (!TextUtils.isEmpty(user.getAvatar())) {
            StringBuffer sb = new StringBuffer("http://7xstnm.com2.z0.glb.clouddn.com/");
            sb.append(user.getAvatar());
            String r = sb.toString();
            Picasso.with(context).load(r).into(idIvHead);
        } else {
            Picasso.with(context).load(R.drawable.default_avatar).into(idIvHead);
        }
        if (!TextUtils.isEmpty(user.getDescribe())) {
            idTvSign.setText(user.getDescribe());
        } else {
            idTvSign.setText("");
        }
        if (TextUtils.isEmpty(user.getFollowStatus())) {
            idBtnFollow.setBackground(context.getResources().getDrawable(R.drawable.btn_follow_bg_y));
            idBtnFollow.setTextColor(context.getResources().getColor(R.color.colorbtnfoolow));
            idBtnFollow.setText("已关注");
        } else if (user.getFollowStatus().equals("Y")) {
            idBtnFollow.setBackground(context.getResources().getDrawable(R.drawable.btn_follow_bg_y));
            idBtnFollow.setTextColor(context.getResources().getColor(R.color.colorbtnfoolow));
            idBtnFollow.setText("已关注");
        } else if (user.getFollowStatus().equals("N")) {
            idBtnFollow.setBackground(context.getResources().getDrawable(R.drawable.btn_follow_bg_n));
            idBtnFollow.setTextColor(context.getResources().getColor(R.color.main_color));
            idBtnFollow.setText("+ 关注");
        }

    }

    @OnClick({R.id.id_btn_follow, R.id.id_tv_name, R.id.id_view_follower_item})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_btn_follow:
                changeFollow();
                break;
            case R.id.id_tv_name:
                if (!TextUtils.isEmpty(u.getFollower())) {
                    IntentUtils.doIntentWithString(context, PersonActivity.class, "id", u.getFollower());
                } else if (!TextUtils.isEmpty(u.getFollowee())) {
                    IntentUtils.doIntentWithString(context, Person1Activity.class, "id", u.getFollowee());
                }
                break;
            case R.id.id_view_follower_item:
                if (!TextUtils.isEmpty(u.getFollower())) {
                    IntentUtils.doIntentWithString(context, PersonActivity.class, "id", u.getFollower());
                } else if (!TextUtils.isEmpty(u.getFollowee())) {
                    IntentUtils.doIntentWithString(context, Person1Activity.class, "id", u.getFollowee());
                }
                break;

        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 2:
                    ToastUtils.toast(context, "操作失败");
                    break;
                case 1:
                    if (idBtnFollow.getText().equals("已关注")) {
                        //取消关注
                        idBtnFollow.setBackground(context.getResources().getDrawable(R.drawable.btn_follow_bg_n));
                        idBtnFollow.setTextColor(context.getResources().getColor(R.color.colorMain));
                        idBtnFollow.setText("+ 关注");
                    } else if (idBtnFollow.getText().equals("+ 关注")) {
                        idBtnFollow.setBackground(context.getResources().getDrawable(R.drawable.btn_follow_bg_y));
                        idBtnFollow.setTextColor(context.getResources().getColor(R.color.colorbtnfoolow));
                        idBtnFollow.setText("已关注");
                    }
                    ToastUtils.toast(context, "操作成功");
                    break;
            }
        }
    };

    private void changeFollow() {
        User myuser = UserHelp.getInstance().getCurrentUser(context);
        if (myuser != null) {
            ApiClient apiClient = null;
            if (idBtnFollow.getText().equals("已关注")) {
                //取消关注
                apiClient = unFollow(myuser);
            } else if (idBtnFollow.getText().equals("+ 关注")) {
                apiClient = follow(myuser);
            }
            apiClient.request(new ApiCallback() {
                @Override
                public void onFailure(Request request, IOException e) {

                }

                @Override
                public void onResponse(ApiClientResponse apiClientResponse) throws IOException {
                    if (apiClientResponse.getRet() == 200) {
                        LogUtils.i("commentholder", apiClientResponse.getData());
                        handler.sendEmptyMessage(1);
                    } else {
                        handler.sendEmptyMessage(2);
                    }
                }
            });
        } else {
            ToastUtils.toast(context, "请先登录");
        }

    }

    private ApiClient unFollow(User myuser) {
        ApiClient apiClient = null;
        if (!TextUtils.isEmpty(u.getFollowee())) {
            apiClient = unFollowee(myuser);
        } else if (!TextUtils.isEmpty(u.getFollower())) {
            apiClient = unFollower(myuser);
        }
        return apiClient;
    }

    private ApiClient unFollowee(User myuser) {
        return ApiClient.create()
                .withService("User.UnFollow")
                .addParams("uD", myuser.getUid())
                .addParams("fD", u.getFollowee())
                .addParams("3917e150bbaa953b", myuser.getSessionToken());
    }

    private ApiClient unFollower(User myuser) {
        return ApiClient.create()
                .withService("User.UnFollow")
                .addParams("uD", myuser.getUid())
                .addParams("fD", u.getFollower())
                .addParams("3917e150bbaa953b", myuser.getSessionToken());
    }

    private ApiClient follow(User myuser) {
        ApiClient apiClient = null;
        if (!TextUtils.isEmpty(u.getFollowee())) {
            apiClient = followee(myuser);
        } else if (!TextUtils.isEmpty(u.getFollower())) {
            apiClient = follower(myuser);
        }
        return apiClient;


    }

    private ApiClient followee(User myuser) {
        return ApiClient.create()
                .withService("User.Follow")
                .addParams("uD", myuser.getUid())
                .addParams("fD", u.getFollowee())
                .addParams("3917e150bbaa953b", myuser.getSessionToken());
    }

    private ApiClient follower(User myuser) {
        return ApiClient.create()
                .withService("User.Follow")
                .addParams("uD", myuser.getUid())
                .addParams("fD", u.getFollower())
                .addParams("3917e150bbaa953b", myuser.getSessionToken());
    }

    public Button getIdBtnFollow() {
        return idBtnFollow;
    }

    public TextView getIdTvSign() {
        return idTvSign;
    }

    public ImageView getIdIvHead() {
        return idIvHead;
    }

    public TextView getIdTvName() {
        return idTvName;
    }


}
