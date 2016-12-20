package com.leancloud.im.tag.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.leancloud.im.tag.Chat_EmotionHelper;
import com.leancloud.im.tag.Chat_ViewHolder;
import com.ngc123.tag.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzw on 14-9-25.
 */
public class Chat_ChatEmotionGridAdapter extends BaseAdapter {
  private Context context;
  private List<String> datas = new ArrayList<String>();

  public Chat_ChatEmotionGridAdapter(Context ctx) {
    this.context = ctx;
  }

  public void setDatas(List<String> datas) {
    this.datas = datas;
  }

  @Override
  public int getCount() {
    return datas.size();
  }

  @Override
  public Object getItem(int i) {
    return datas.get(i);
  }

  @Override
  public long getItemId(int i) {
    return i;
  }

  @Override
  public View getView(int position, View conView, ViewGroup parent) {
    if (conView == null) {
      conView = View.inflate(context, R.layout.chat_emotion_item, null);
    }
    ImageView emotionImageView = Chat_ViewHolder.findViewById(conView, R.id.emotionImageView);
    String emotion = (String) getItem(position);
    emotion = emotion.substring(1, emotion.length() - 1);
    Bitmap bitmap = Chat_EmotionHelper.getEmojiDrawable(context, emotion);
    emotionImageView.setImageBitmap(bitmap);
    return conView;
  }
}
