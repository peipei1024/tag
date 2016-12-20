package com.ngc123.tag.ui.person;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.ngc123.tag.fragment.Feed1Adapter.FeedHolder;
import com.ngc123.tag.ui.comment.CommentHolder;
import com.ngc123.tag.ui.comment.CommentcountHolder;
import com.ngc123.tag.ui.comment.CommentimgHolder;
import com.ngc123.tag.ui.comment.TComment;

import java.util.List;

/*
* Class name :PersonAdapter
*
* Version information :
*
* Describe ：
*
* Author ：裴徐泽
*
* Created by pei on 2016-8-3.
*
*/
public class PersonAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private final int user = 0;
    private final int item = 1;

    private Context context;

    public List<PersonBean> getmList() {
        return mList;
    }

    public void setmList(List<PersonBean> mList) {
        this.mList = mList;
    }

    private List<PersonBean> mList;

    public PersonAdapter(Context context, List<PersonBean> l) {
        this.context = context;
        this.mList = l;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        if (viewType == user){
            holder = new ViewPersionItemHolder(LayoutInflater.from(context), parent);
        }else if (viewType == item){
            holder = new PersonPicHolder(LayoutInflater.from(context), parent);
        }
        return holder;
    }

    public void refresh(List<PersonBean> comments){
        this.mList = comments;
        notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == 0){
            ((ViewPersionItemHolder) holder).bindData(context, mList.get(position).getUser());
        }else {
            ((PersonPicHolder) holder).bindData(context, mList.get(position).getFeedItem());
        }
//        if (holder instanceof ViewPersionItemHolder){
//            ((ViewPersionItemHolder) holder).bindData(context, mList.get(position).getUser());
//        }else if (holder instanceof ViewPersonPicHolder){
//            ((ViewPersonPicHolder) holder).bindData(context, mList.get(position).getList());
//        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        int reslut = 0;
        if (mList.get(position).getType() == user){
            reslut = user;
        }else if (mList.get(position).getType() == item){
            reslut = item;
        }
        return reslut;
    }

}
