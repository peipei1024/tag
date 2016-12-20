package com.leancloud.im.tag.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.leancloud.im.tag.Chat_AVImClientManager;
import com.leancloud.im.tag.viewholder.Chat_AVCommonViewHolder;
import com.leancloud.im.tag.viewholder.Chat_LeftTextHolder;
import com.leancloud.im.tag.viewholder.Chat_RightTextHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wli on 15/8/13.
 * 聊天的 Adapter，此处还有可优化的地方，稍后考虑一下提取出公共的 adapter
 */
public class Chat_MultipleItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private final int ITEM_LEFT_TEXT = 0;
  private final int ITEM_RIGHT_TEXT = 1;

  // 时间间隔最小为十分钟
  private final long TIME_INTERVAL = 10 * 60 * 1000;

  private List<AVIMMessage> messageList = new ArrayList<AVIMMessage>();

  public Chat_MultipleItemAdapter() {
  }

  public void setMessageList(List<AVIMMessage> messages) {
    messageList.clear();
    if (null != messages) {
      messageList.addAll(messages);
    }
  }

  public void addMessageList(List<AVIMMessage> messages) {
    messageList.addAll(0, messages);
  }

  public void addMessage(AVIMMessage message) {
    messageList.addAll(Arrays.asList(message));
  }

  public AVIMMessage getFirstMessage() {
    if (null != messageList && messageList.size() > 0) {
      return messageList.get(0);
    } else {
      return null;
    }
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (viewType == ITEM_LEFT_TEXT) {
      return new Chat_LeftTextHolder(parent.getContext(), parent);
    } else if (viewType == ITEM_RIGHT_TEXT) {
      return new Chat_RightTextHolder(parent.getContext(), parent);
    } else {
      //TODO
      return null;
    }
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    ((Chat_AVCommonViewHolder)holder).bindData(messageList.get(position));
    if (holder instanceof Chat_LeftTextHolder) {
      ((Chat_LeftTextHolder)holder).showTimeView(shouldShowTime(position));
    } else if (holder instanceof Chat_RightTextHolder) {
      ((Chat_RightTextHolder)holder).showTimeView(shouldShowTime(position));
    }
  }

  @Override
  public int getItemViewType(int position) {
    AVIMMessage message = messageList.get(position);
    if (message.getFrom().equals(Chat_AVImClientManager.getInstance().getClientId())) {
      return ITEM_RIGHT_TEXT;
    } else {
      return ITEM_LEFT_TEXT;
    }
  }

  @Override
  public int getItemCount() {
    return messageList.size();
  }

  private boolean shouldShowTime(int position) {
    if (position == 0) {
      return true;
    }
    long lastTime = messageList.get(position - 1).getTimestamp();
    long curTime = messageList.get(position).getTimestamp();
    return curTime - lastTime > TIME_INTERVAL;
  }
}