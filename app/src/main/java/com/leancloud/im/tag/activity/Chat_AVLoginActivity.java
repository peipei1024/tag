package com.leancloud.im.tag.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.leancloud.im.tag.Chat_AVImClientManager;
import com.leancloud.im.tag.Chat_Constants;
import com.ngc123.tag.R;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by wli on 15/8/13.
 * 登陆页面，暂时未做自动登陆，每次重新进入都要再登陆一次
 */
public class Chat_AVLoginActivity extends Chat_AVBaseActivity {

  /**
   * 此处 xml 里限制了长度为 30，汉字算一个
   */
  @Bind(R.id.activity_login_et_username)
  protected EditText userNameView;

  @Bind(R.id.activity_login_btn_login)
  protected Button loginButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.chat_activity_login);
  }

  @OnClick(R.id.activity_login_btn_login)
  public void onLoginClick(View view) {
    openClient(userNameView.getText().toString().trim());
  }

  private void openClient(String selfId) {
    if (TextUtils.isEmpty(selfId)) {
      showToast(R.string.login_null_name_tip);
      return;
    }

    loginButton.setEnabled(false);
    userNameView.setEnabled(false);
    Chat_AVImClientManager.getInstance().open(selfId, new AVIMClientCallback() {
      @Override
      public void done(AVIMClient avimClient, AVIMException e) {
        loginButton.setEnabled(true);
        userNameView.setEnabled(true);
        if (filterException(e)) {
          Intent intent = new Intent(Chat_AVLoginActivity.this, Chat_MainActivity.class);
          intent.putExtra(Chat_Constants.CONVERSATION_ID, Chat_Constants.SQUARE_CONVERSATION_ID);
          //intent.putExtra(Chat_Constants.ACTIVITY_TITLE, getString(R.string.square_name));
          startActivity(intent);
          finish();
        }
      }
    });
  }
}
