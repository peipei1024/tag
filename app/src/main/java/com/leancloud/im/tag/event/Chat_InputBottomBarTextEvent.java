package com.leancloud.im.tag.event;

/**
 * Created by wli on 15/7/29.
 * InputBottomBar 发送文本事件
 */
public class Chat_InputBottomBarTextEvent extends Chat_InputBottomBarEvent {

  /**
   * 发送的文本内容
   */
  public String sendContent;

  public Chat_InputBottomBarTextEvent(int action, String content, Object tag) {
    super(action, tag);
    sendContent = content;
  }
}
