package com.leancloud.im.tag.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
import com.leancloud.im.tag.Chat_AVImClientManager;
import com.leancloud.im.tag.Chat_Constants;
import com.leancloud.im.tag.fragment.Chat_ChatFragment;
import com.ngc123.tag.R;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wli on 15/8/14.
 * 一对一单聊的页面，需要传入 Chat_Constants.MEMBER_ID
 */
public class Chat_AVSingleChatActivity extends Chat_AVBaseActivity {

    @Bind(R.id.toolbar)
    protected Toolbar toolbar;

    protected Chat_ChatFragment chatFragment;
    private ImageView back;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity_square);
        back = (ImageView) findViewById(R.id.id_iv_back_arrow);
        textView = (TextView) findViewById(R.id.id_tv_title);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        chatFragment = (Chat_ChatFragment) getFragmentManager().findFragmentById(R.id.fragment_chat);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.chat_btn_navigation_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        String memberId = getIntent().getStringExtra(Chat_Constants.MEMBER_ID);
        Log.d("memberid", memberId);
        setTitle(memberId);
        textView.setText(memberId);
        getConversation(memberId);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = intent.getExtras();
        if (null != extras && extras.containsKey(Chat_Constants.MEMBER_ID)) {
            String memberId = extras.getString(Chat_Constants.MEMBER_ID);
            setTitle(memberId);
            getConversation(memberId);
        }
    }

    /**
     * 获取 conversation，为了避免重复的创建，此处先 query 是否已经存在只包含该 member 的 conversation
     * 如果存在，则直接赋值给 Chat_ChatFragment，否者创建后再赋值
     */
    private void getConversation(final String memberId) {
        final AVIMClient client = Chat_AVImClientManager.getInstance().getClient();
        AVIMConversationQuery conversationQuery = client.getQuery();
        conversationQuery.withMembers(Arrays.asList(memberId), true);
        conversationQuery.whereEqualTo("customConversationType", 1);
        conversationQuery.findInBackground(new AVIMConversationQueryCallback() {
            @Override
            public void done(List<AVIMConversation> list, AVIMException e) {
                if (filterException(e)) {
                    //注意：此处仍有漏洞，如果获取了多个 conversation，默认取第一个
                    if (null != list && list.size() > 0) {
                        chatFragment.setConversation(list.get(0));
                    } else {
                        HashMap<String, Object> attributes = new HashMap<String, Object>();
                        attributes.put("customConversationType", 1);
                        client.createConversation(Arrays.asList(memberId), null, attributes, false, new AVIMConversationCreatedCallback() {
                            @Override
                            public void done(AVIMConversation avimConversation, AVIMException e) {
                                chatFragment.setConversation(avimConversation);
                            }
                        });
                    }
                }
            }
        });
    }
}
