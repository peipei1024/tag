package com.ngc123.tag.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.avos.avoscloud.okhttp.Request;
import com.ngc123.tag.R;
import com.ngc123.tag.api.ApiCallback;
import com.ngc123.tag.api.ApiClient;
import com.ngc123.tag.api.ApiClientResponse;
import com.ngc123.tag.bean.User;
import com.ngc123.tag.model.FeedItem;
import com.ngc123.tag.ui.base.BaseActivity;
import com.ngc123.tag.util.JsonToMapList;
import com.ngc123.tag.util.LogUtils;
import com.ngc123.tag.util.ToastUtils;
import com.ngc123.tag.util.UserHelp;
import com.ngc123.tag.util.Utils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by Miroslaw Stanek on 21.02.15.
 */
public class PublishActivity extends BaseActivity {
    public static final String ARG_TAKEN_PHOTO_URI = "arg_taken_photo_uri";


    @Bind(R.id.ivPhoto)
    ImageView ivPhoto;
    @Bind(R.id.etDescription)
    EditText etDescription;
    @Bind(R.id.id_iv_back_arrow)
    ImageView idIvBackArrow;
    @Bind(R.id.id_btn_publish)
    Button idBtnPublish;

    private boolean propagatingToggleState = false;
    private Uri photoUri;
    private int photoSize;
    static FeedItem feedItem;
    private String ivContent;

    /**
     * 上个页面在调用
     *
     * @param openingActivity
     * @param photoUri
     * @param feedItem
     */
    public static void openWithPhotoUri(Activity openingActivity, Uri photoUri, FeedItem feedItem) {
        Intent intent = new Intent(openingActivity, PublishActivity.class);
        intent.putExtra(ARG_TAKEN_PHOTO_URI, photoUri);
        PublishActivity.feedItem = feedItem;
        openingActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        ButterKnife.bind(this);
        photoSize = getResources().getDimensionPixelSize(R.dimen.publish_photo_thumbnail_size);
        Intent intent = getIntent();
        feedItem = (FeedItem) intent.getSerializableExtra("feeditem");
        Picasso.with(this).load(feedItem.getImgName()).placeholder(R.drawable.default_pic_load).error(R.drawable.default_pic_error).into(ivPhoto);

//        if (savedInstanceState == null) {
//            photoUri = getIntent().getParcelableExtra(ARG_TAKEN_PHOTO_URI);
//        } else {
//            photoUri = savedInstanceState.getParcelable(ARG_TAKEN_PHOTO_URI);
//        }
//        updateStatusBarColor();

//        ivPhoto.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//            @Override
//            public boolean onPreDraw() {
//                ivPhoto.getViewTreeObserver().removeOnPreDrawListener(this);
//                loadThumbnailPhoto();
//                return true;
//            }
//        });


    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void updateStatusBarColor() {
        if (Utils.isAndroid5()) {
            getWindow().setStatusBarColor(0xff888888);
        }
    }

    private void loadThumbnailPhoto() {
        ivPhoto.setScaleX(0);
        ivPhoto.setScaleY(0);
        Picasso.with(this)
                .load(photoUri)
                .centerCrop()
                .resize(photoSize, photoSize)
                .into(ivPhoto, new Callback() {
                    @Override
                    public void onSuccess() {
                        ivPhoto.animate()
                                .scaleX(1.f).scaleY(1.f)
                                .setInterpolator(new OvershootInterpolator())
                                .setDuration(400)
                                .setStartDelay(200)
                                .start();
                    }

                    @Override
                    public void onError() {
                    }
                });
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_publish, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.action_publish) {
//            ivContent = etDescription.getText().toString();
//            feedItem.setTextContext(ivContent);
//            if (TextUtils.isEmpty(ivContent)) {
//                ToastUtils.toast(this, "请填写图片描述");
//                return false;
//            }
//            LogUtils.i("publish", JSON.toJSONString(feedItem.getTagList()));
//            showProgressDialog("图片上传中");
//            User user = UserHelp.getInstance().getCurrentUser(this);
//            ApiClient.create()
//                    .withService("Feeds.setFeed")
//                    .addParams("imgId", feedItem.getImgId())
//                    .addParams("uid", user.getUid())
//                    .addParams("tL", JSON.toJSONString(feedItem.getTagList()))
//                    .addParams("tC", ivContent)
//                    .addParams("3917e150bbaa953b", user.getSessionToken())
//                    .request(new ApiCallback() {
//                        @Override
//                        public void onFailure(Request request, IOException e) {
//
//                        }
//
//                        @Override
//                        public void onResponse(ApiClientResponse apiClientResponse) throws IOException {
//                            dismissProgressDialog();
//                            if (apiClientResponse.getRet() != 200) {
//                                Log.d("5v !200", apiClientResponse.getMsg());
//                                return;
//                            }
//                            String data = apiClientResponse.getData();
//                            Log.d("5vv5", JSON.toJSONString(feedItem));
//                            Log.d("5v", data);
//                            Map<String, Object> lm = JsonToMapList.getMap(data);
//                            if (!lm.get("code").toString().equals("0")) {
//                                toast("上传失败", 3);
//                                Log.d("5v", apiClientResponse.getMsg());
//                            } else {
//                                EventBus.getDefault().post(feedItem);
//                                bringMainActivityToTop();
//                            }
//                        }
//                    });
//            return true;
//        } else {
//            return super.onOptionsItemSelected(item);
//        }
//    }

    private void bringMainActivityToTop() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setAction(MainActivity.ACTION_SHOW_LOADING_ITEM);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ARG_TAKEN_PHOTO_URI, photoUri);
    }


    @OnClick({R.id.id_iv_back_arrow, R.id.id_btn_publish})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_iv_back_arrow:
                finish();
                break;
            case R.id.id_btn_publish:
                ivContent = etDescription.getText().toString();
                feedItem.setTextContext(ivContent);
                if (TextUtils.isEmpty(ivContent)) {
                    ToastUtils.toast(this, "请填写图片描述");
                    return;
                }
                LogUtils.i("publish", JSON.toJSONString(feedItem.getTagList()));
                showProgressDialog("图片上传中");
                User user = UserHelp.getInstance().getCurrentUser(this);
                ApiClient.create()
                        .withService("Feeds.setFeed")
                        .addParams("imgId", feedItem.getImgId())
                        .addParams("uid", user.getUid())
                        .addParams("tL", JSON.toJSONString(feedItem.getTagList()))
                        .addParams("tC", ivContent)
                        .addParams("3917e150bbaa953b", user.getSessionToken())
                        .request(new ApiCallback() {
                            @Override
                            public void onFailure(Request request, IOException e) {

                            }

                            @Override
                            public void onResponse(ApiClientResponse apiClientResponse) throws IOException {
                                dismissProgressDialog();
                                if (apiClientResponse.getRet() != 200) {
                                    Log.d("5v !200", apiClientResponse.getMsg());
                                    return;
                                }
                                String data = apiClientResponse.getData();
                                Log.d("5vv5", JSON.toJSONString(feedItem));
                                Log.d("5v", data);
                                Map<String, Object> lm = JsonToMapList.getMap(data);
                                if (!lm.get("code").toString().equals("0")) {
                                    toast("上传失败", 3);
                                    Log.d("5v", apiClientResponse.getMsg());
                                } else {
                                    EventBus.getDefault().post(feedItem);
                                    bringMainActivityToTop();
                                }
                            }
                        });
                break;
        }
    }
}
