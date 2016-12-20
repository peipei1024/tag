package com.ngc123.tag.ui.askfriend;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

/*
* Class name :PhoneContactAdapter
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
public class PhoneContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context context;
    private List<PhoneContact> list;

    public PhoneContactAdapter(Context context, List<PhoneContact> list){
        this.context = context;
        this.list = list;
    }

    public void refresh(List<PhoneContact> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PhoneContactHolder(LayoutInflater.from(context), parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PhoneContactHolder){
            ((PhoneContactHolder) holder).bindData(context, list.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
