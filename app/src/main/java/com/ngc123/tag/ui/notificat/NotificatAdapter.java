package com.ngc123.tag.ui.notificat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.ngc123.tag.bean.NotificatBean;

import java.util.List;

/*
* Class name :NotificatAdapter
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
public class NotificatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context context;
    private List<NotificatBean> list;

    public NotificatAdapter(Context context, List<NotificatBean> list){
        this.list = list;
        this.context = context;
    }

    public void refresh(List<NotificatBean> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NotificatItemHolder(LayoutInflater.from(context), parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NotificatItemHolder){
            ((NotificatItemHolder) holder).bindData(context, list.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
