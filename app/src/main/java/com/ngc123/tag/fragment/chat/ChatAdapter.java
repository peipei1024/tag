package com.ngc123.tag.fragment.chat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.ngc123.tag.bean.ChatHistory;

import java.util.List;

/*
* Class name :ChatAdapter
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
public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context context;
    private List<ChatHistory> list;
    public ChatAdapter(Context context, List<ChatHistory> list){
        this.context = context;
        this.list = list;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        if (viewType == 1){
            holder = new ViewMessageHolder(LayoutInflater.from(context), parent);
        }else if (viewType == 2){
            holder = new ViewChatItemHolder(LayoutInflater.from(context), parent);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof  ViewMessageHolder){
            ((ViewMessageHolder) holder).bindData(context);
        }else if (holder instanceof  ViewChatItemHolder){
            ((ViewChatItemHolder) holder).bindData(context, list.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public void refresh(List<ChatHistory> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return 1;
        }else {
            return 2;
        }
    }
}
