package com.ngc123.tag.ui.comment;

import com.ngc123.tag.bean.CommentBean;
import com.ngc123.tag.model.FeedItem;

/*
* Class name :TComment
*
* Version information :
*
* Describe ：
*
* Author ：裴徐泽
*
* Created by pei on 2016-7-31.
*
*/
public class TComment {
    private FeedItem mFeedItem;
    private int type;
    private CommentBean mCommentBean;
    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public FeedItem getFeedItem() {
        return mFeedItem;
    }

    public void setFeedItem(FeedItem feedItem) {
        mFeedItem = feedItem;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public CommentBean getCommentBean() {
        return mCommentBean;
    }

    public void setCommentBean(CommentBean commentBean) {
        mCommentBean = commentBean;
    }
}
