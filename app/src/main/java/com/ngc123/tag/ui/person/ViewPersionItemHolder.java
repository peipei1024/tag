package com.ngc123.tag.ui.person;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.okhttp.Request;
import com.leancloud.im.tag.Chat_Constants;
import com.leancloud.im.tag.activity.Chat_AVSingleChatActivity;
import com.ngc123.tag.R;
import com.ngc123.tag.api.ApiCallback;
import com.ngc123.tag.api.ApiClient;
import com.ngc123.tag.api.ApiClientResponse;
import com.ngc123.tag.bean.User;
import com.ngc123.tag.event.PersonEvent;
import com.ngc123.tag.util.LogUtils;
import com.ngc123.tag.util.ToastUtils;
import com.ngc123.tag.util.UserHelp;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class ViewPersionItemHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.id_iv_head)
    ImageView idIvHead;
    @Bind(R.id.id_tv_username)
    TextView idTvUsername;
    @Bind(R.id.id_tv_message)
    TextView idTvMessage;
    @Bind(R.id.id_btn_follow)
    Button idBtnFollow;
    @Bind(R.id.id_iv_sex)
    ImageView idIvSex;
    @Bind(R.id.id_tv_location)
    TextView idTvLocation;
    @Bind(R.id.id_tv_descripe)
    TextView idTvDescripe;

    private Context context;
    private User mUser;
    public ViewPersionItemHolder(LayoutInflater inflater, ViewGroup parent) {
        this(inflater.inflate(R.layout.view_persion_item, parent, false));
    }

    public ViewPersionItemHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public void bindData(Context context, User user){
        this.context = context;
        this.mUser = user;
        changeUser(user);
    }
    @OnClick(R.id.id_btn_follow)
    public void onClick() {
        changeFollow();
    }

    private void changeFollow() {
        User user = UserHelp.getInstance().getCurrentUser(context);
        if (user != null) {
            ApiClient apiClient = null;
            if (TextUtils.isEmpty(mUser.getIsFollower())) {
                if (user.getUid().equals(Person1Activity.pid)) {
                    ToastUtils.toast(context, "自己不能关注自己哦");
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
//                        handler.sendEmptyMessage(6);
                        EventBus.getDefault().post(new PersonEvent(6));
                    } else {
                        EventBus.getDefault().post(new PersonEvent(5));
//                        handler.sendEmptyMessage(5);
                    }
                }
            });
        } else {
            ToastUtils.toast(context, "请先登录");
        }

    }

    private ApiClient unFollow(User user) {
        return ApiClient.create()
                .withService("User.UnFollow")
                .addParams("uD", user.getUid())
                .addParams("fD", Person1Activity.pid)
                .addParams("3917e150bbaa953b", user.getSessionToken());
    }

    private ApiClient follow(User user) {
        return ApiClient.create()
                .withService("User.Follow")
                .addParams("uD", user.getUid())
                .addParams("fD", Person1Activity.pid)
                .addParams("3917e150bbaa953b", user.getSessionToken());
    }

    private void changeUser(User user) {
        if (!TextUtils.isEmpty(user.getAvatar())) {
            StringBuffer sb = new StringBuffer("http://7xstnm.com2.z0.glb.clouddn.com/");
            sb.append(user.getAvatar());
            String r = sb.toString();
            Picasso.with(context).load(r).placeholder(R.drawable.default_avatar).error(R.drawable.default_avatar).into(idIvHead);
        } else {
            Picasso.with(context).load(R.drawable.default_avatar).into(idIvHead);
        }
        if (!TextUtils.isEmpty(user.getUsername())) {
            idTvUsername.setText(user.getUsername());
        } else {
            idTvUsername.setText("");
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
            Picasso.with(context).load(R.drawable.icon_nan).placeholder(R.drawable.icon_nannv).error(R.drawable.icon_nannv).into(idIvSex);
        } else if (user.getSex().equals("1")) {
            //女
            Picasso.with(context).load(R.drawable.icon_nv).placeholder(R.drawable.icon_nannv).error(R.drawable.icon_nannv).into(idIvSex);
        } else if (user.getSex().equals("2")) {
            //不详
            Picasso.with(context).load(R.drawable.icon_nannv).placeholder(R.drawable.icon_nannv).error(R.drawable.icon_nannv).into(idIvSex);
        }
        if (TextUtils.isEmpty(user.getIsFollower())) {
            if (UserHelp.getInstance().getCurrentUser(context).getUid().equals(Person1Activity.pid)) {
                idBtnFollow.setText("自己");
                idBtnFollow.setBackground(context.getResources().getDrawable(R.drawable.btn_follow_bg_n));
                idBtnFollow.setTextColor(context.getResources().getColor(R.color.colorMain));
//                idTvChat.setVisibility(View.GONE);
            } else {
                idBtnFollow.setBackground(context.getResources().getDrawable(R.drawable.btn_follow_bg_n));
                idBtnFollow.setTextColor(context.getResources().getColor(R.color.colorMain));
                idBtnFollow.setText("+ 关注");
            }
        } else if (user.getIsFollower().equals("Y")) {
            idBtnFollow.setBackground(context.getResources().getDrawable(R.drawable.btn_follow_bg_y));
            idBtnFollow.setTextColor(context.getResources().getColor(R.color.colorbtnfoolow));
            idBtnFollow.setText("已关注");
        } else if (user.getIsFollower().equals("N")) {
            idBtnFollow.setBackground(context.getResources().getDrawable(R.drawable.btn_follow_bg_n));
            idBtnFollow.setTextColor(context.getResources().getColor(R.color.white));
            idBtnFollow.setText("+ 关注");
        }
    }
    public TextView getIdTvUsername() {
        return idTvUsername;
    }

    public TextView getIdTvDescripe() {
        return idTvDescripe;
    }

    public ImageView getIdIvHead() {
        return idIvHead;
    }

    public TextView getIdTvMessage() {
        return idTvMessage;
    }

    public ImageView getIdIvSex() {
        return idIvSex;
    }

    public Button getIdBtnFollow() {
        return idBtnFollow;
    }

    public TextView getIdTvLocation() {
        return idTvLocation;
    }
}
