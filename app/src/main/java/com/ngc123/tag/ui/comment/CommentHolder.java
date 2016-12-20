package com.ngc123.tag.ui.comment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ngc123.tag.bean.CommentBean;
import com.ngc123.tag.R;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CommentHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.comment_iv)
    ImageView commentIv;
    @Bind(R.id.comment_user_name)
    TextView commentUserName;
//    @Bind(R.id.comment_month)
//    TextView commentMonth;
    @Bind(R.id.comment_time)
    TextView commentTime;
    @Bind(R.id.comment_content)
    TextView commentContent;

    public CommentHolder(LayoutInflater inflater, ViewGroup parent) {
        this(inflater.inflate(R.layout.view_tag_item, parent, false));
    }

    public CommentHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public void bindData(CommentBean commentBean, Context context){
        if (!TextUtils.isEmpty(commentBean.getAvatar())){
            StringBuffer sb = new StringBuffer("http://7xstnm.com2.z0.glb.clouddn.com/");
            sb.append(commentBean.getAvatar());
            String r = sb.toString();
            Picasso.with(context).load(r).placeholder(R.drawable.default_avatar).error(R.drawable.default_avatar).into(commentIv);
        }else {
            Picasso.with(context).load(R.drawable.default_avatar).into(commentIv);
        }
        if (!TextUtils.isEmpty(commentBean.getUsername())){
            commentUserName.setText(commentBean.getUsername());
        }else {
            commentUserName.setText("");
        }
        if (!TextUtils.isEmpty(commentBean.getCreateTime())){
            commentTime.setText(commentBean.getCreateTime());
        }else {
            commentTime.setText("");
        }
        if (!TextUtils.isEmpty(commentBean.getComment())){
            commentContent.setText(commentBean.getComment());
        }else {
            commentContent.setText("");
        }
    }

    public ImageView getCommentIv() {
        return commentIv;
    }

    public TextView getCommentContent() {
        return commentContent;
    }

    public TextView getCommentTime() {
        return commentTime;
    }

    public TextView getCommentUserName() {
        return commentUserName;
    }

}
