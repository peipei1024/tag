package com.ngc123.tag.ui.notificat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ngc123.tag.R;
import com.ngc123.tag.bean.NotificatBean;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

/*
* Class name :NotificatItemHolder
*
* Version information :
*
* Describe ：
*
* Author ：裴徐泽
*
* Created by pei on 2016-8-6.
*
*/
public class NotificatItemHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.id_iv_head)
    ImageView idIvHead;
    @Bind(R.id.id_tv_name)
    TextView idTvName;
    @Bind(R.id.id_tv_time)
    TextView idTvTime;
    @Bind(R.id.id_tv_content)
    TextView idTvContent;

    public NotificatItemHolder(LayoutInflater inflater, ViewGroup parent) {
        this(inflater.inflate(R.layout.view_notificat_item, parent, false));
    }

    public NotificatItemHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public void bindData(Context context, NotificatBean bean) {
        if (!TextUtils.isEmpty(bean.getAvatar())) {
            StringBuffer sb = new StringBuffer("http://7xstnm.com2.z0.glb.clouddn.com/");
            sb.append(bean.getAvatar());
            String r = sb.toString();
            Picasso.with(context).load(r).placeholder(R.drawable.default_avatar).error(R.drawable.default_avatar).into(idIvHead);
        }
        if (!TextUtils.isEmpty(bean.getUsername())) {
            idTvName.setText(bean.getUsername());
        } else {
            idTvName.setText("");
        }
        if (!TextUtils.isEmpty(bean.getTime())) {
            idTvTime.setText(bean.getTime());
        } else {
            idTvTime.setText("");
        }
        if (!TextUtils.isEmpty(bean.getContent())) {
            idTvContent.setText(bean.getContent());
        } else {
            idTvContent.setText("");
        }
    }

}
