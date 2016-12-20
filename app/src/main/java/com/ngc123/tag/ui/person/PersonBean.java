package com.ngc123.tag.ui.person;

import com.ngc123.tag.bean.User;
import com.ngc123.tag.model.FeedItem;

import java.util.List;

/*
* Class name :PersonBean
*
* Version information :
*
* Describe ：
*
* Author ：裴徐泽
*
* Created by pei on 2016-8-3.
*
*/
public class PersonBean {
    private User user;
    private int type;
    private List<FeedItem> list;
    private FeedItem feedItem;

    public FeedItem getFeedItem() {
        return feedItem;
    }

    public void setFeedItem(FeedItem feedItem) {
        this.feedItem = feedItem;
    }

    public List<FeedItem> getList() {
        return list;
    }

    public void setList(List<FeedItem> list) {
        this.list = list;
    }



    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


}
