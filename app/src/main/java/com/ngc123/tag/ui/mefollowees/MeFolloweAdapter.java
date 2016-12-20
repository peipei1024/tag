package com.ngc123.tag.ui.mefollowees;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.ngc123.tag.bean.User;

import java.util.List;

/*
* Class name :MeFolloweAdapter
*
* Version information :
*
* Describe ：
*
* Author ：裴徐泽
*
* Created by pei on 2016-8-1.
*
*/
public class MeFolloweAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context context;
    private List<User> list;
    public MeFolloweAdapter(Context context, List<User> list){
        this.context = context;
        this.list = list;
    }
    public void refresh(List<User> l){
        this.list = l;
        notifyDataSetChanged();
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        holder = new MeFolloweeHolder(LayoutInflater.from(context), parent);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MeFolloweeHolder){
            ((MeFolloweeHolder) holder).bindData(list.get(position), context);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
