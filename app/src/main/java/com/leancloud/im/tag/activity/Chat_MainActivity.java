package com.leancloud.im.tag.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.leancloud.im.tag.Chat_AVImClientManager;
import com.leancloud.im.tag.Chat_Constants;
import com.ngc123.tag.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
* Class name :Chat_MainActivity
*
* Version information :
*
* Describe ：
*
* Author ：裴徐泽
*
* Created by pei on 2016-5-19.
*
*/
public class Chat_MainActivity extends Chat_AVBaseActivity {
    @Bind(R.id.id_zhangsan)
    Button idZhangsan;
    @Bind(R.id.id_123)
    Button id123;
    private AVIMConversation squareConversation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity_main);
        ButterKnife.bind(this);
        String conversationId = getIntent().getStringExtra(Chat_Constants.CONVERSATION_ID);
        getSquare(conversationId);
    }

    @OnClick(R.id.id_zhangsan)
    public void onClick3() {
        Intent intent = new Intent(this, Chat_AVSingleChatActivity.class);
        intent.putExtra(Chat_Constants.MEMBER_ID, "张三");
        startActivity(intent);
    }

    /**
     * 根据 conversationId 查取本地缓存中的 conversation，如若没有缓存，则返回一个新建的 conversaiton
     */
    private void getSquare(String conversationId) {
        if (TextUtils.isEmpty(conversationId)) {
            throw new IllegalArgumentException("conversationId can not be null");
        }

        AVIMClient client = Chat_AVImClientManager.getInstance().getClient();
        if (null != client) {
            squareConversation = client.getConversation(conversationId);
        } else {
            finish();
            showToast("Please call AVIMClient.open first!");
        }
    }

    @OnClick(R.id.id_123)
    public void onClick() {
        Intent intent = new Intent(this, Chat_AVSingleChatActivity.class);
        intent.putExtra(Chat_Constants.MEMBER_ID, "123");
        startActivity(intent);
    }

}
