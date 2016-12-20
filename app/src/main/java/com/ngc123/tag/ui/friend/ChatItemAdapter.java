package com.ngc123.tag.ui.friend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.ngc123.tag.bean.User;
import com.ngc123.tag.R;

import java.util.List;

/*
* Class name :ChatItemAdapter
*
* Version information :
*
* Describe ：
*
* Author ：裴徐泽
*
* Created by pei on 2016-7-14.
*
*/
public class ChatItemAdapter extends BaseAdapter {
    private Context context;
    private List<User> list;
    public ChatItemAdapter(Context context, List<User> l){
        this.context = context;
        this.list = l;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    public void change(List<User> list){
        this.list = list;
        notifyDataSetChanged();
    }
    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatItemHolder holder;
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.view_chat_item, null);
            holder = new ChatItemHolder();
            holder.textView = (TextView) convertView.findViewById(R.id.id_tv_name);
            convertView.setTag(holder);
        }else {
            holder = (ChatItemHolder) convertView.getTag();
        }
        User u = list.get(position);
        holder.textView.setText(u.getUsername());
        return convertView;
    }

    class ChatItemHolder{
      public TextView textView;
    }
}
