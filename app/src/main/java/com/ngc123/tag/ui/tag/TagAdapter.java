package com.ngc123.tag.ui.tag;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.ngc123.tag.model.TagItem;
import com.ngc123.tag.ui.askfriend.PhoneContact;
import com.ngc123.tag.ui.askfriend.PhoneContactHolder;

import java.util.List;

/*
* Class name :TagAdapter
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
public class TagAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    private List<TagItem> list;

    public TagAdapter(Context context, List<TagItem> list){
        this.context = context;
        this.list = list;
    }

    public void refresh(List<TagItem> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewMytagItemHolder(LayoutInflater.from(context), parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewMytagItemHolder){
            ((ViewMytagItemHolder) holder).bindData(list.get(position), context);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
