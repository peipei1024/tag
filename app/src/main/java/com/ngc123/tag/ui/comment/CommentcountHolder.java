package com.ngc123.tag.ui.comment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ngc123.tag.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/*
* Class name :CommentcountHolder
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
public class CommentcountHolder extends RecyclerView.ViewHolder {


    @Bind(R.id.id_count)
    TextView mIdCount;

    public CommentcountHolder(LayoutInflater inflater, ViewGroup parent) {
        this(inflater.inflate(R.layout.view_comment_count, parent, false));
    }

    public CommentcountHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }
    public void bindData(int count){
        mIdCount.setText("评论  " + count);
    }
}
