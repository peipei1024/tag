package com.ngc123.tag.ui.askfriend;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ngc123.tag.R;
import com.ngc123.tag.util.SharedPreferencesUtils;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
* Class name :PhoneContactHolder
*
* Version information :
*
* Describe ：
*
* Author ：裴徐泽
*
* Created by pei on 2016-8-5.
*
*/
public class PhoneContactHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.id_iv_avatar)
    ImageView idIvAvatar;
    @Bind(R.id.id_tv_name)
    TextView idTvName;
    @Bind(R.id.id_btn_ask)
    Button idBtnAsk;

    private Context context;
    private PhoneContact contact;
    public PhoneContactHolder(LayoutInflater inflater, ViewGroup parent) {
        this(inflater.inflate(R.layout.view_ask_contact_item, parent, false));
    }

    public PhoneContactHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public void bindData(Context context, PhoneContact contact) {
        this.contact = contact;
        this.context = context;
        idTvName.setText(contact.getName() + " ");
    }

    @OnClick(R.id.id_btn_ask)
    public void onClick() {
        HashMap<String, String> map = (HashMap<String, String>) SharedPreferencesUtils.readShrePerface(context, "app");
        String link = map.get("link");
        Uri smsToUri = Uri.parse("smsto:" + contact.getPhone());
        Intent intent1 = new Intent(Intent.ACTION_SENDTO, smsToUri);
        intent1.putExtra("sms_body", "我标记的美好生活，你一定要看！" + "TAG下载地址>>"  + link);
        context.startActivity(intent1);
    }
}
