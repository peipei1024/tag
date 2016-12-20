package com.ngc123.tag.fragment.find;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.ngc123.tag.model.FeedItem;

import java.util.List;

/*
* Class name :FindAdapter
*
* Version information :
*
* Describe ：
*
* Author ：裴徐泽
*
* Created by pei on 2016-7-31.
*
*/
public class FindAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<FeedItem> list;
    private Context context;

    public FindAdapter(Context context, List<FeedItem> list){
        this.context = context;
        this.list = list;
    }

    public void refresh(List<FeedItem> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        holder = new FindHolder(LayoutInflater.from(context), parent);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof  FindHolder){
            ((FindHolder) holder).bindData(list.get(position), context);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
