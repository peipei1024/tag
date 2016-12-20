package com.ngc123.tag.fragment.chat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leancloud.im.tag.Chat_Constants;
import com.leancloud.im.tag.activity.Chat_AVSingleChatActivity;
import com.ngc123.tag.R;
import com.ngc123.tag.bean.ChatHistory;
import com.ngc123.tag.util.LogUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ViewChatItemHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.id_iv_head)
    ImageView idIvHead;
    @Bind(R.id.id_tv_name)
    TextView idTvName;
    @Bind(R.id.id_tv_message)
    TextView idTvMessage;
    @Bind(R.id.id_view_chat_item)
    LinearLayout idViewChatItem;

    private Context context;
    private ChatHistory history;

    public ViewChatItemHolder(LayoutInflater inflater, ViewGroup parent) {
        this(inflater.inflate(R.layout.view_chat_item, parent, false));
    }

    public ViewChatItemHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public void bindData(Context context, ChatHistory history) {
        this.context = context;
        this.history = history;
        if (!TextUtils.isEmpty(history.getHead())) {
            //设置头像
        }
        if (!TextUtils.isEmpty(history.getName())) {
            idTvName.setText(history.getName());
        }
        if (!TextUtils.isEmpty(history.getLastMessage())) {
            idTvMessage.setText(history.getLastMessage());
        }
    }


    public TextView getIdTvName() {
        return idTvName;
    }

    public TextView getIdTvMessage() {
        return idTvMessage;
    }

    public ImageView getIdIvHead() {
        return idIvHead;
    }

    @OnClick(R.id.id_view_chat_item)
    public void onClick() {
        Intent intent = new Intent(context, Chat_AVSingleChatActivity.class);
        intent.putExtra(Chat_Constants.MEMBER_ID, history.getName());
        LogUtils.i("find", history.getName());
        context.startActivity(intent);
    }
}
