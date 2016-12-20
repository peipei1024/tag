package com.ngc123.tag.fragment;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.ngc123.tag.R;
import com.ngc123.tag.bean.ChatHistory;

import java.util.List;

/*
* Class name :ChatHistoryAdapter
*
* Version information :
*
* Describe ：
*
* Author ：裴徐泽
*
* Created by pei on 2016-7-16.
*
*/
public class ChatHistoryAdapter extends BaseAdapter {
    private Context mContext;
    private List<ChatHistory> mList;

    public ChatHistoryAdapter(Context context, List<ChatHistory> list) {
        mContext = context;
        mList = list;
    }

    public void changeAdapter(List<ChatHistory> list){
        mList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatHistoryHolder holder;
        if (convertView == null) {
            holder = new ChatHistoryHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.view_chat_item, null);
            holder.idIvHead = (ImageView) convertView.findViewById(R.id.id_iv_head);
            holder.idTvName = (TextView) convertView.findViewById(R.id.id_tv_name);
            holder.idTvMessage = (TextView) convertView.findViewById(R.id.id_tv_message);
            convertView.setTag(holder);
        }else {
            holder = (ChatHistoryHolder) convertView.getTag();
        }
        ChatHistory history = mList.get(position);
        if (!TextUtils.isEmpty(history.getHead())){
            //设置头像
        }
        if (!TextUtils.isEmpty(history.getName())){
            holder.idTvName.setText(history.getName());
        }
        if (!TextUtils.isEmpty(history.getLastMessage())){
            holder.idTvMessage.setText(history.getLastMessage());
        }
        return convertView;
    }

    class ChatHistoryHolder {
        public ImageView idIvHead;
        public TextView idTvName;
        public TextView idTvMessage;
    }


}
