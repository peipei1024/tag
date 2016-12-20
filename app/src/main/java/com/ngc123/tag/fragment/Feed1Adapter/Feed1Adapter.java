package com.ngc123.tag.fragment.Feed1Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;


import com.ngc123.tag.model.FeedItem;

import java.util.ArrayList;
import java.util.List;

/*
* Class name :Feed1Adapter
*
* Version information :
*
* Describe ：
*
* Author ：裴徐泽
*
* Created by pei on 2016-7-29.
*
*/
public class Feed1Adapter extends RecyclerView.Adapter<FeedHolder>{
    private List<FeedItem> items = new ArrayList<FeedItem>();
    private Context context;
    public void setList(List<FeedItem> list) {
        //MainActivity.count = MainActivity.count + 1;
        if (items.size() > 0) {
            items.clear();
        }
        items.addAll(list);
    }

    public void addList(List<FeedItem> list) {
        items.addAll(list);
    }

    public Feed1Adapter(Context context) {
        this.context = context;
    }


    @Override
    public FeedHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FeedHolder holder = new FeedHolder(LayoutInflater.from(context), parent);
        return holder;
    }

    @Override
    public void onBindViewHolder(FeedHolder holder, int position) {
        holder.bindData(items.get(position), context);
    }

    public void refresh(List<FeedItem> l){
        this.items = l;
        notifyDataSetChanged();
    }

    @Override
    public void onViewRecycled(FeedHolder holder) {
        // 将标签移除,避免回收使用时标签重复
        holder.clearTag();
        super.onViewRecycled(holder);
    }


    @Override
    public void onViewAttachedToWindow(FeedHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.drawTag();
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

//    ------------------------双击点赞 暂时废弃------------------------------------------------------
//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case 1:
//                    Toast.makeText(context,"这是单击事件",Toast.LENGTH_LONG).show();
//                    break;
//                case 2:
//                    Toast.makeText(context,"这是双击事件",Toast.LENGTH_LONG).show();
//                    break;
//            }
//        }
//    };
//     ------------------------双击点赞 暂时废弃------------------------------------------------------

    long mLastTime = 0;
    long mCurTime = 0;
//------------------------双击点赞 暂时废弃------------------------------------------------------
//                mLastTime=mCurTime;
//                mCurTime= System.currentTimeMillis();
//                if(mCurTime-mLastTime<300){//双击事件
//                    mCurTime =0;
//                    mLastTime = 0;
//                    handler.removeMessages(1);
//                    handler.sendEmptyMessage(2);
//                }else{//单击事件
//                    handler.sendEmptyMessageDelayed(1, 310);
//                }
// ------------------------双击点赞 暂时废弃------------------------------------------------------


}
