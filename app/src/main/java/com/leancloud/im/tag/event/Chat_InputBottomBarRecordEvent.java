package com.leancloud.im.tag.event;

/**
 * Created by wli on 15/7/29.
 * InputBottomBar 录音事件，录音完成时触发
 */
public class Chat_InputBottomBarRecordEvent extends Chat_InputBottomBarEvent {

  /**
   * 录音本地路径
   */
  public String audioPath;

  /**
   * 录音长度
   */
  public int audioDuration;

  public Chat_InputBottomBarRecordEvent(int action, String path, int duration, Object tag) {
    super(action, tag);
    audioDuration = duration;
    audioPath = path;
  }
}
