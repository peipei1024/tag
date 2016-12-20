package com.ngc123.tag.ui.person;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ngc123.tag.R;
import com.ngc123.tag.model.FeedItem;
import com.ngc123.tag.ui.comment.Comment1Activity;
import com.ngc123.tag.util.IntentUtils;
import com.ngc123.tag.util.LogUtils;
import com.ngc123.tag.util.ScreenUtils;
import com.squareup.picasso.Picasso;
import com.tencent.tauth.bean.Pic;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PersonPicHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.id_iv_img)
    ImageView idIvImg;

    private Context context;
    private FeedItem feedItem;
    public PersonPicHolder(LayoutInflater inflater, ViewGroup parent) {
        this(inflater.inflate(R.layout.view_person_img_item, parent, false));
    }

    public PersonPicHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }
    public void bindData(Context context, FeedItem feedItem){
        this.context = context;
        this.feedItem = feedItem;
        int width = ScreenUtils.getWidth(context);
        int a = width/3;
//        LogUtils.i("personpic", a + " ");
//        Picasso.with(context).load(R.mipmap.ic_launcher).into(idIvImg);
        if (!TextUtils.isEmpty(feedItem.getImgName())){
            StringBuffer sb = new StringBuffer("http://7xstnm.com2.z0.glb.clouddn.com/");
            sb.append(feedItem.getImgName());
            String r = sb.toString();
            Picasso.with(context).load(r).tag(feedItem.getImgName()).resize(a, a).centerCrop().placeholder(R.drawable.default_pic_load).error(R.drawable.default_pic_error).into(idIvImg);
        }else {
            Picasso.with(context).load(R.drawable.default_pic_error).into(idIvImg);
        }
//        IntentUtils.doIntentWithSerializable(context, Comment1Activity.class, "feeditem", feedItem);
    }
    @OnClick(R.id.id_iv_img)
    public void open(){
        IntentUtils.doIntentWithSerializable(context, Comment1Activity.class, "feeditem", feedItem);
    }
    public void resumeTag(){
        Picasso.with(context).resumeTag(feedItem.getImgName());
    }
    public void stop(){
        Picasso.with(context).pauseTag(feedItem.getImgName());
    }
    public ImageView getIdIvImg() {
        return idIvImg;
    }
}
