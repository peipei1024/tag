package com.ngc123.tag.ui.comment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ngc123.tag.bean.CommentBean;
import com.ngc123.tag.R;

import java.util.List;

/**
 * Created by JiaM on 2016/4/19.
 */
public class CommentItemAdapter extends BaseAdapter {

    private Context context;
    private List<CommentBean> commentBeen;

    public CommentItemAdapter(Context context, List<CommentBean> commentBeen) {
        this.context = context;
        this.commentBeen = commentBeen;
    }

    @Override
    public int getCount() {
        return commentBeen.size();
    }

    @Override
    public Object getItem(int position) {
        return commentBeen.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.view_tag_item, null);
            viewHolder.comment_iv = (ImageView) convertView.findViewById(R.id.comment_iv);
            viewHolder.comment_username = (TextView) convertView.findViewById(R.id.comment_user_name);
//            viewHolder.comment_month = (TextView) convertView.findViewById(R.id.comment_month);
            viewHolder.comment_time = (TextView) convertView.findViewById(R.id.comment_time);
            viewHolder.comment_content = (TextView) convertView.findViewById(R.id.comment_content);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.comment_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, commentBeen.get(position).getComment_username(), Toast.LENGTH_SHORT).show();
            }
        });
//        viewHolder.comment_username.setText(commentBeen.get(position).getComment_username());
//        viewHolder.comment_month.setText(commentBeen.get(position).getComment_month());
//        viewHolder.comment_time.setText(commentBeen.get(position).getComment_time());
//        viewHolder.comment_content.setText(commentBeen.get(position).getComment_content());
        return convertView;
    }

    public class ViewHolder {
        private ImageView comment_iv;
        private TextView comment_username;
        private TextView comment_month;
        private TextView comment_time;
        private TextView comment_content;
    }
}
