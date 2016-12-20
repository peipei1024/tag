package com.leancloud.im.tag;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.leancloud.im.tag.activity.Chat_AVLoginActivity;
import com.leancloud.im.tag.activity.Chat_AVSingleChatActivity;
import com.ngc123.tag.ui.Login1Activity;
import com.ngc123.tag.util.LogUtils;

/**
 * Created by wli on 15/9/8.
 * 因为 notification 点击时，控制权不在 app，此时如果 app 被 kill 或者上下文改变后，
 * 有可能对 notification 的响应会做相应的变化，所以此处将所有 notification 都发送至此类，
 * 然后由此类做分发。
 */
public class Chat_NotificationBroadcastReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    if (Chat_AVImClientManager.getInstance().getClient() == null) {
      gotoLoginActivity(context);
    } else {
      String conversationId = intent.getStringExtra(Chat_Constants.CONVERSATION_ID);
      if (!TextUtils.isEmpty(conversationId)) {
        if (Chat_Constants.SQUARE_CONVERSATION_ID.equals(conversationId)) {
          //gotoSquareActivity(context, intent);
        } else {
          gotoSingleChatActivity(context, intent);
        }
      }else {
        LogUtils.i("push", "push");
      }
    }
  }

  /**
   * 如果 app 上下文已经缺失，则跳转到登陆页面，走重新登陆的流程
   * @param context
   */
  private void gotoLoginActivity(Context context) {
    Intent startActivityIntent = new Intent(context, Login1Activity.class);
    startActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(startActivityIntent);
  }



  /**
   * 跳转至单聊页面
   * @param context
   * @param intent
   */
  private void gotoSingleChatActivity(Context context, Intent intent) {
    Intent startActivityIntent = new Intent(context, Chat_AVSingleChatActivity.class);
    startActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivityIntent.putExtra(Chat_Constants.MEMBER_ID, intent.getStringExtra(Chat_Constants.MEMBER_ID));
    context.startActivity(startActivityIntent);
  }
}
