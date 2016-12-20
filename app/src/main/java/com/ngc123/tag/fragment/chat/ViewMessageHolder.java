package com.ngc123.tag.fragment.chat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.ngc123.tag.R;
import com.ngc123.tag.ui.notificat.CommentNoActivity;
import com.ngc123.tag.ui.notificat.FollowActivity;
import com.ngc123.tag.ui.notificat.LikeActivity;
import com.ngc123.tag.util.IntentUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ViewMessageHolder extends RecyclerView.ViewHolder {


    @Bind(R.id.id_view_follow)
    RelativeLayout idViewFollow;
    @Bind(R.id.id_view_comment)
    RelativeLayout idViewComment;
    @Bind(R.id.id_view_tongzhi)
    RelativeLayout idViewTongzhi;
    @Bind(R.id.id_view_like)
    RelativeLayout idViewLike;
    private Context context;

    public ViewMessageHolder(LayoutInflater inflater, ViewGroup parent) {
        this(inflater.inflate(R.layout.view_message, parent, false));
    }

    public ViewMessageHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public void bindData(Context context) {
//        idTvComment.setText("pinglun");
        this.context = context;
    }

    public RelativeLayout getIdViewLike() {
        return idViewLike;
    }

    public RelativeLayout getIdViewFollow() {
        return idViewFollow;
    }

    public RelativeLayout getIdViewComment() {
        return idViewComment;
    }

    public RelativeLayout getIdViewTongzhi() {
        return idViewTongzhi;
    }


    @OnClick({R.id.id_view_follow, R.id.id_view_comment, R.id.id_view_tongzhi, R.id.id_view_like})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_view_follow:
                IntentUtils.doIntent(context, FollowActivity.class);
                break;
            case R.id.id_view_comment:
                IntentUtils.doIntent(context, CommentNoActivity.class);
                break;
            case R.id.id_view_tongzhi:
                break;
            case R.id.id_view_like:
                IntentUtils.doIntent(context, LikeActivity.class);
                break;
        }
    }
}
