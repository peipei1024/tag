package com.ngc123.tag.model;

import android.net.Uri;
import android.util.Log;

import com.avos.avoscloud.okhttp.Call;
import com.avos.avoscloud.okhttp.Callback;
import com.avos.avoscloud.okhttp.OkHttpClient;
import com.avos.avoscloud.okhttp.Request;
import com.avos.avoscloud.okhttp.Response;
import com.ngc123.tag.util.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/**
 * 图片Module
 * Created by sky on 15/7/18.
 */
public class FeedItem implements Serializable{

    //本地地址
    private String imgPath;
    private List<TagItem> tagList;
    private String textContext;
    private int imgId;
    private int likesCount;
    private boolean clicked;
    private String createTime;
    private String username;
    private String avatar;
    private String isFollowee;

    //图片名
    private String imgName;

    //增加用户访问控制
    private String uid;

    public FeedItem() {

    }

    public FeedItem(List<TagItem> tagList, String imgName) {
        this.imgName = imgName;
        this.tagList = tagList;
    }
    public FeedItem(List<TagItem> tagList, int imgid, String url) {
        this.imgId = imgid;
        this.tagList = tagList;
        this.imgName = url;
    }

    public boolean isClicked() {
        return clicked;
    }

    public String getIsFollowee() {
        return isFollowee;
    }

    public void setIsFollowee(String isFollowee) {
        this.isFollowee = isFollowee;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public List<TagItem> getTagList() {
        return tagList;
    }

    public void setTagList(List<TagItem> tagList) {
        this.tagList = tagList;
    }

    public Uri getImgUri(){
        Uri uri = Uri.parse("http://7xstnm.com2.z0.glb.clouddn.com/" + imgName);
        return uri;
    }

    public String getImgPath() {
        imgPath = FileUtils.getInst().getPhotoSavedPath() + "/" +imgName;
        Log.d("5v", imgPath);
        final File file = new File(FileUtils.getInst().getPhotoSavedPath() + "/",imgName);
        if(!file.exists()) {
            // 从网络上获取图片
            Log.d("5v", imgPath);
            String path = "http://7xstnm.com2.z0.glb.clouddn.com/" + imgName;
            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(path)
                    .build();
            Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {

                }

                @Override
                public void onResponse(Response response) throws IOException {
                    byte[] content = response.body().bytes();
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(file);
                        fos.write(content);
                        fos.close();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public void setTextContext(String textContext) {
        this.textContext = textContext;
    }

    public String getTextContext() {
        return textContext;
    }

    //增加访问控制
//    public void setUid(String uid) { this.uid = DemoApplication.getInstance().getUser(); }

    public void setUid(String uid){
        this.uid = uid;
    }

    public String getUid() {return this.uid;}

    public void setImgId(int imgId) { this.imgId = imgId; }
    public int getImgId() { return this.imgId; }

    public void setImgName(String imgName) {this.imgName = imgName; }
    public String getImgName() {return this.imgName;}

//    public int getLikesCount() {
//        ApiClient.create()
//                .withService("Likes.GetFeedLikes")
//                .addParams("uid",AVUser.getCurrentUser().getObjectId())
//                .addParams("imgId",String.valueOf(this.imgId))
//                .request(new ApiCallback() {
//                    @Override
//                    public void onFailure(Request request, IOException e) {
//
//                    }
//
//                    @Override
//                    public void onResponse(ApiClientResponse apiClientResponse) throws IOException {
//                        String data = apiClientResponse.getData();
//                        Map<String, Object> lm = JsonToMapList.getMap(data);
//                        if(lm.get("code") == null || !lm.get("code").toString().equals("0")){
//                            return;
//                        }
//                        likesCount = Integer.parseInt(lm.get("likesCount").toString());
//                        if(!lm.get("clicked").toString().equals("0")){
//                            clicked = true;
//                        }
//                    }
//                });
//        return this.likesCount;
//    }
//
//    public void setLikesCount(int likesCount) {
//        this.likesCount = likesCount;
//    }

    public boolean getClicked(){
        return this.clicked;
    }

    public void setClicked(boolean clicked){
        this.clicked = clicked;
//        ApiClient.create()
//                .withService("Likes.userClickLike")
//                .addParams("uid", AVUser.getCurrentUser().getObjectId())
//                .addParams("imgId", String.valueOf(this.imgId))
//                .request(new ApiCallback() {
//                    @Override
//                    public void onFailure(Request request, IOException e) {
//
//                    }
//
//                    @Override
//                    public void onResponse(ApiClientResponse apiClientResponse) throws IOException {
//
//                    }
//                });

    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
