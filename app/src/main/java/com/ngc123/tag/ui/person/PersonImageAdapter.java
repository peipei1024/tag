package com.ngc123.tag.ui.person;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.ngc123.tag.R;
import com.ngc123.tag.model.FeedItem;
import com.ngc123.tag.ui.comment.Comment1Activity;
import com.ngc123.tag.util.IntentUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

/*
* Class name :PersonImageAdapter
*
* Version information :
*
* Describe ：
*
* Author ：裴徐泽
*
* Created by pei on 2016-8-1.
*
*/
public class PersonImageAdapter extends BaseAdapter{
    private Context context;
    private List<FeedItem> feedItems;

    public PersonImageAdapter(Context c, List<FeedItem> l){
        this.context = c;
        this.feedItems = l;
    }
    public PersonImageAdapter(Context c){
        this.context = c;
    }

    public void refreshAdapter(List<FeedItem> l){
        this.feedItems = l;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return feedItems.size();
    }

    @Override
    public Object getItem(int position) {
        return feedItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImgHolder holder = null;
        if (convertView == null){
            holder = new ImgHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.view_person_img_item, null);
            holder.img = (ImageView) convertView.findViewById(R.id.id_iv_img);
            convertView.setTag(holder);
        }else {
            holder = (ImgHolder) convertView.getTag();
        }

        FeedItem feedItem = feedItems.get(position);
        if (!TextUtils.isEmpty(feedItem.getImgName())){
            StringBuffer sb = new StringBuffer("http://7xstnm.com2.z0.glb.clouddn.com/");
            sb.append(feedItem.getImgName());
            String r = sb.toString();
            Picasso.with(context).load(r).into(holder.img);
        }else {
            Picasso.with(context).load(R.mipmap.ic_launcher).into(holder.img);
        }
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtils.doIntentWithSerializable(context, Comment1Activity.class, "feeditem", feedItem);
            }
        });
        return convertView;
    }

    public class ImgHolder{
        public ImageView img;
    }
}
