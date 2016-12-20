package com.ngc123.tag.ui.comment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.ngc123.tag.R;

import java.util.List;

/*
* Class name :CommentAdapter
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
public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final int IMAGE = 0;
    private final int COMMENT = 1;
    private final int count = 2;

    private Context context;
    private List<TComment> mList;

    public CommentAdapter(Context context, List<TComment> commentBeen) {
        this.context = context;
        this.mList = commentBeen;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        if (viewType == IMAGE){
            holder = new CommentimgHolder(LayoutInflater.from(context), parent);
        }else if (viewType == COMMENT){
            holder = new CommentHolder(LayoutInflater.from(context), parent);
        }else if (viewType == count){
            holder = new CommentcountHolder(LayoutInflater.from(context), parent);
        }
        return holder;
    }

    public void refresh(List<TComment> comments){
        this.mList = comments;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CommentimgHolder){
            ((CommentimgHolder) holder).bindData(mList.get(position).getFeedItem(), context);
        }else if (holder instanceof CommentHolder){
            ((CommentHolder) holder).bindData(mList.get(position).getCommentBean(), context);
        }else if (holder instanceof CommentcountHolder){
            ((CommentcountHolder) holder).bindData(mList.get(position).getCount());
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        int reslut = 0;
        if (mList.get(position).getType() == IMAGE){
            reslut = IMAGE;
        }else if (mList.get(position).getType() == COMMENT){
            reslut = COMMENT;
        }else if (mList.get(position).getType() == count){
            reslut = count;
        }
        return reslut;
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        // 将标签移除,避免回收使用时标签重复
        if (holder instanceof CommentimgHolder){
            ((CommentimgHolder) holder).clearTag();
        }
        super.onViewRecycled(holder);
    }


    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (holder instanceof CommentimgHolder){
            ((CommentimgHolder) holder).drawTag();
        }
    }
}
