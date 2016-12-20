package com.ngc123.tag.fragment.chat;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
import com.avos.avoscloud.im.v2.callback.AVIMSingleMessageQueryCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.leancloud.im.tag.Chat_AVImClientManager;
import com.ngc123.tag.R;
import com.ngc123.tag.bean.ChatHistory;
import com.ngc123.tag.bean.User;
import com.ngc123.tag.util.LogUtils;
import com.ngc123.tag.util.UserHelp;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by JiaM on 2016/3/15.
 */
public class ChatFragment extends Fragment {
    @Bind(R.id.id_recyle)
    RecyclerView idRecyle;
    private List<ChatHistory> mHistoryList = new ArrayList<ChatHistory>();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 200) {
                adapter.refresh(mHistoryList);
            }
        }
    };

    private ChatAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_main, null);
        ButterKnife.bind(this, view);
        adapter = new ChatAdapter(getActivity(), mHistoryList);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        idRecyle.setLayoutManager(manager);
        idRecyle.setAdapter(adapter);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mHistoryList.clear();
        ChatHistory nulBean = new ChatHistory();
        mHistoryList.add(nulBean);
        User user = UserHelp.getInstance().getCurrentUser(getActivity());
        if (user != null) {
            String conversationId = user.getUsername();
            getSquare(conversationId);
        } else {
            LogUtils.i("chat", "user is null");
        }
        getIMConversation();
    }

    /**
     * 获得历史对话
     */
    private void getIMConversation() {
        final AVIMClient client = Chat_AVImClientManager.getInstance().getClient();
        AVIMConversationQuery query = client.getQuery();
        query.findInBackground(new AVIMConversationQueryCallback() {
            @Override
            public void done(List<AVIMConversation> list, AVIMException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        LogUtils.i("chat", "对话.size = " + list.size());
                        for (int a = 0; a < list.size(); a++) {
                            final ChatHistory chatHistory = new ChatHistory();
                            LogUtils.i("conversation", "第" + a + "会话");
                            AVIMConversation conversation = list.get(a);
                            List<String> members = conversation.getMembers();
                            if (members.size() >= 2) {
                                for (int b = 0; b < members.size(); b++) {
                                    //过滤掉自己
                                    if (!members.get(b).equals(UserHelp.getInstance().getCurrentUser(getActivity()).getUsername())) {
                                        LogUtils.i("conversation", "过滤掉自己后的参与者" + members.get(b));
                                        chatHistory.setName(members.get(b));
                                    }
                                    LogUtils.i("conversation", "参与者" + members.get(b));
                                }
                                //对话最后一条消息的时间
//                            Date date = conversation.getLastMessageAt();
//                            LogUtils.i("conversation", "date" + date);
//                            记得添加******获取头像******
                                conversation.getLastMessage(new AVIMSingleMessageQueryCallback() {
                                    @Override
                                    public void done(AVIMMessage avimMessage, AVIMException e) {
                                        if (e == null) {
                                            if (avimMessage != null) {
                                                LogUtils.i("conversation", ((AVIMTextMessage) avimMessage).getText() + "最后一条消息");
                                                chatHistory.setLastMessage(((AVIMTextMessage) avimMessage).getText());
                                            }
                                        } else {
                                            LogUtils.i("conversation", "获取最后一条消息失败" + e.getMessage());
                                        }
                                    }
                                });
                                mHistoryList.add(chatHistory);
                            }
                        }
                        handler.sendEmptyMessage(200);
                    } else {
                        LogUtils.i("chat", "没有对话");
                        handler.sendEmptyMessage(200);
                    }
                } else {
                    LogUtils.i("chat", "获取对话失败" + e.getMessage() + e.getCode());
                    handler.sendEmptyMessage(200);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    private AVIMConversation squareConversation;

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
//            getActivity().finish();
            LogUtils.i("chat", "Please call AVIMClient.open first!");
            User user = UserHelp.getInstance().getCurrentUser(getActivity());
            if (user != null) {
                Chat_AVImClientManager.getInstance().open(user.getUsername(), new AVIMClientCallback() {
                    @Override
                    public void done(AVIMClient avimClient, AVIMException e) {
                        if (e == null) {
                            LogUtils.i("mefriend", user.getUsername());
                            LogUtils.i("mefriend", "create tcp success");
                            getSquare(user.getUsername());
                        }
                    }
                });
            } else {
                LogUtils.i("mefriend", "user is null");
            }
        }
    }
}

