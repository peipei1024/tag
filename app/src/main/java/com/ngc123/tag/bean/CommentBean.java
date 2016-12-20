package com.ngc123.tag.bean;

/**
 * Created by JiaM on 2016/4/19.
 */
public class CommentBean {

    private String id;
    private String imgId;
    private String uid;
    private String comment;
    private String createTime;
    private String avatar;
    private String username;

    public CommentBean(String id, String imgId, String uid, String comment, String createTime, String avatar, String username){
        this.id = id;
        this.imgId = imgId;
        this.uid = uid;
        this.comment = comment;
        this.createTime = createTime;
        this.avatar = avatar;
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
