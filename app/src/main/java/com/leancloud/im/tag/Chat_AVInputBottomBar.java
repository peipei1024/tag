package com.leancloud.im.tag;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.leancloud.im.tag.event.Chat_InputBottomBarEvent;
import com.leancloud.im.tag.event.Chat_InputBottomBarTextEvent;
import com.ngc123.tag.R;

import de.greenrobot.event.EventBus;


/**
 * Created by wli on 15/7/24.
 * 专门负责输入的底部操作栏，与 activity 解耦
 * 当点击相关按钮时发送 Chat_InputBottomBarEvent，需要的 View 可以自己去订阅相关消息
 */
public class Chat_AVInputBottomBar extends LinearLayout {

  /**
   * 最小间隔时间为 1 秒，避免多次点击
   */
  private final int MIN_INTERVAL_SEND_MESSAGE = 1000;

  /**
   * 发送文本的Button
   */
  private ImageButton sendTextBtn;

  private EditText contentView;

  public Chat_AVInputBottomBar(Context context) {
    super(context);
    initView(context);
  }

  public Chat_AVInputBottomBar(Context context, AttributeSet attrs) {
    super(context, attrs);
    initView(context);
  }

  private void initView(final Context context) {
    View.inflate(context, R.layout.chat_input_bottom_bar, this);

    sendTextBtn = (ImageButton) findViewById(R.id.input_bottom_bar_btn_send);
    contentView = (EditText) findViewById(R.id.input_bottom_bar_et_content);


    sendTextBtn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        String content = contentView.getText().toString();
        //输入内容为空
        if (TextUtils.isEmpty(content)) {
          Toast.makeText(getContext(), R.string.message_is_null, Toast.LENGTH_SHORT).show();
          return;
        }
        //输入内容不为空，清空输入框
        contentView.setText("");
        //一秒之内不可再点击发送
        new Handler().postDelayed(new Runnable() {
          @Override
          public void run() {
            sendTextBtn.setEnabled(true);
          }
        }, MIN_INTERVAL_SEND_MESSAGE);
        //发送消息
        EventBus.getDefault().post(new Chat_InputBottomBarTextEvent(Chat_InputBottomBarEvent.INPUTBOTTOMBAR_SEND_TEXT_ACTION, content, getTag()));
      }
    });
  }


}
