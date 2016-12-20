package com.leancloud.im.tag.viewholder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.leancloud.im.tag.Chat_EmotionHelper;
import com.leancloud.im.tag.event.Chat_LeftChatItemClickEvent;
import com.ngc123.tag.R;

import java.text.SimpleDateFormat;

import butterknife.Bind;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by wli on 15/8/13.
 * 聊天时居左的文本 holder
 */

public class Chat_LeftTextHolder extends Chat_AVCommonViewHolder {

  @Bind(R.id.chat_left_text_tv_time)
  protected TextView timeView;

  @Bind(R.id.chat_left_text_tv_content)
  protected TextView contentView;

  @Bind(R.id.chat_left_text_tv_name)
  protected TextView nameView;

  @Bind(R.id.chat_left_image_head)
  protected ImageView headView;

  public Chat_LeftTextHolder(Context context, ViewGroup root) {
    super(context, root, R.layout.chat_left_text_view);
  }

  @OnClick({R.id.chat_left_text_tv_content, R.id.chat_left_text_tv_name})
  public void onNameClick(View view) {
    Chat_LeftChatItemClickEvent clickEvent = new Chat_LeftChatItemClickEvent();
    clickEvent.userId = nameView.getText().toString();
    EventBus.getDefault().post(clickEvent);
  }

  @Override
  public void bindData(Object o) {
    AVIMMessage message = (AVIMMessage)o;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
    String time = dateFormat.format(message.getTimestamp());

    String content =  getContext().getString(R.string.unspport_message_type);
    if (message instanceof AVIMTextMessage) {
      content = ((AVIMTextMessage)message).getText();
    }

    //contentView.setText(content);
    //根据用户id获取用户的昵称和头像url
    //Picasso.with(getContext()).load("").into(headView);
    contentView.setText(Chat_EmotionHelper.replace(getContext(), content));
    timeView.setText(time);
    nameView.setText(message.getFrom());
  }

  public void showTimeView(boolean isShow) {
    timeView.setVisibility(isShow ? View.VISIBLE : View.GONE);
  }
}