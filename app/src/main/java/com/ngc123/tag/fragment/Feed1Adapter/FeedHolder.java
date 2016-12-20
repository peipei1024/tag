package com.ngc123.tag.fragment.Feed1Adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.avos.avoscloud.okhttp.Request;
import com.ngc123.tag.R;
import com.ngc123.tag.api.ApiCallback;
import com.ngc123.tag.api.ApiClient;
import com.ngc123.tag.api.ApiClientResponse;
import com.ngc123.tag.bean.User;
import com.ngc123.tag.model.FeedItem;
import com.ngc123.tag.model.TagItem;
import com.ngc123.tag.ui.WebviewActivity;
import com.ngc123.tag.ui.comment.Comment1Activity;
import com.ngc123.tag.ui.person.Person1Activity;
import com.ngc123.tag.util.EffectUtil;
import com.ngc123.tag.util.IntentUtils;
import com.ngc123.tag.util.LogUtils;
import com.ngc123.tag.util.SharedPreferencesUtils;
import com.ngc123.tag.util.ToastUtils;
import com.ngc123.tag.util.UserHelp;
import com.ngc123.tag.view.LabelView;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;


/*
* Class name :FeedHolder
*
* Version information :
*
* Describe ：
*
* Author ：裴徐泽
*
* Created by pei on 2016-7-29.
*
*/
public class FeedHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.avatar_img)
    ImageView avatarImg;
    @Bind(R.id.name_friend)
    TextView nameFriend;
    @Bind(R.id.ivFeedCenter)
    ImageView ivFeedCenter;
    @Bind(R.id.pictureLayout)
    RelativeLayout pictureLayout;
    @Bind(R.id.vBgLike)
    View vBgLike;
    @Bind(R.id.ivLike)
    ImageView ivLike;
    @Bind(R.id.ivFeedBottom)
    TextView ivFeedBottom;
    @Bind(R.id.btnComments)
    ImageButton btnComments;
    @Bind(R.id.btnMore)
    ImageButton btnMore;
    @Bind(R.id.btnLike)
    ImageButton btnLike;
    @Bind(R.id.tsLikesCounter)
    TextSwitcher tsLikesCounter;
    @Bind(R.id.id_view_user)
    RelativeLayout idViewUser;


    private FeedItem feedItem;
    private Context context;


    public FeedHolder(LayoutInflater inflater, ViewGroup parent) {
        this(inflater.inflate(R.layout.item_feed, parent, false));
    }

    public FeedHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public void bindData(FeedItem feedItem, Context context) {
        ShareSDK.initSDK(context, "15aafdbd3bb44");
        this.feedItem = feedItem;
        this.context = context;
        changUi();
    }

    @OnClick({R.id.avatar_img, R.id.name_friend, R.id.ivFeedCenter, R.id.btnComments, R.id.btnMore, R.id.btnLike, R.id.tsLikesCounter})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.avatar_img:
                IntentUtils.doIntentWithString(context, Person1Activity.class, "id", feedItem.getUid());
                break;
            case R.id.name_friend:
                IntentUtils.doIntentWithString(context, Person1Activity.class, "id", feedItem.getUid());
                break;
            case R.id.ivFeedCenter:
                IntentUtils.doIntentWithSerializable(context, Comment1Activity.class, "feeditem", feedItem);
                break;
            case R.id.btnComments:
                IntentUtils.doIntentWithSerializable(context, Comment1Activity.class, "feeditem", feedItem);
                break;
            case R.id.btnMore:
                ShareSDK.initSDK(context);
                HashMap<String, String> map = (HashMap<String, String>) SharedPreferencesUtils.readShrePerface(context, "app");
                String link = map.get("link");
                OnekeyShare oks = new OnekeyShare();
                //关闭sso授权
                oks.disableSSOWhenAuthorize();

                // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间等使用
                oks.setTitle("TAG下载地址");
                // titleUrl是标题的网络链接，QQ和QQ空间等使用
                oks.setTitleUrl(link);
                // text是分享文本，所有平台都需要这个字段
                oks.setText("我标记的美好生活，你一定要看！" + "TAG下载地址>>"  + link);
                // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
                //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
                // url仅在微信（包括好友和朋友圈）中使用
                oks.setUrl(link);
                // comment是我对这条分享的评论，仅在人人网和QQ空间使用
                oks.setComment("我标记的美好生活，你一定要看！" + "TAG下载地址>>"  + link);
                // site是分享此内容的网站名称，仅在QQ空间使用
                oks.setSite(context.getString(R.string.app_name));
                // siteUrl是分享此内容的网站地址，仅在QQ空间使用
                oks.setSiteUrl(link);

                // 启动分享GUI
                oks.show(context);
                break;
            case R.id.btnLike:
                animatePhotoLike();
                changeLike();
                break;
            case R.id.tsLikesCounter:
                animatePhotoLike();
                changeLike();
                break;
        }
    }

    public void drawTag() {
        List<TagItem> list = feedItem.getTagList();
        LogUtils.i("feedadapter", list.size() + " " + feedItem.getImgId());
        for (TagItem feedImageTag : list) {
            LabelView tagView = new LabelView(context);
            tagView.init(feedImageTag);
            EffectUtil.myaddLabel(pictureLayout, tagView, feedImageTag.getX(), feedImageTag.getY());
            tagView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!feedImageTag.getClick().equals("unable")){
                        Bundle bundle = new Bundle();
                        bundle.putString("title", "标签");
                        bundle.putString("url", feedImageTag.getClick());
                        IntentUtils.doIntentWithBundle(context, WebviewActivity.class, "urlbundle", bundle);
                    }
//                    ToastUtils.toast(context, feedImageTag.getName());
                    LogUtils.i("feedadapter", feedImageTag.getName());
                }
            });

        }
    }

    public void clearTag() {
        pictureLayout.removeViews(1, pictureLayout.getChildCount() - 1);
    }

    private void changUi() {
        if (UserHelp.getInstance().getCurrentUser(context).getUid().equals(feedItem.getUid())){
            StringBuffer sb = new StringBuffer("http://7xstnm.com2.z0.glb.clouddn.com/");
            sb.append(UserHelp.getInstance().getCurrentUser(context).getAvatar());
            String r = sb.toString();
            Picasso.with(context).load(r).placeholder(R.drawable.default_avatar).into(avatarImg);
            nameFriend.setText(UserHelp.getInstance().getCurrentUser(context).getUsername());
        }else {
            if (!TextUtils.isEmpty(feedItem.getAvatar())) {
                StringBuffer sb = new StringBuffer("http://7xstnm.com2.z0.glb.clouddn.com/");
                sb.append(feedItem.getAvatar());
                String r = sb.toString();
                Picasso.with(context).load(r).placeholder(R.drawable.default_avatar).into(avatarImg);
            } else {
                Picasso.with(context).load(R.drawable.default_avatar).into(avatarImg);
            }
            if (!TextUtils.isEmpty(feedItem.getUsername())) {
                nameFriend.setText(feedItem.getUsername());
            } else {
                nameFriend.setText("");
            }
        }
        if (!TextUtils.isEmpty(feedItem.getImgName())) {
            StringBuffer sb = new StringBuffer("http://7xstnm.com2.z0.glb.clouddn.com/");
            sb.append(feedItem.getImgName());
            String r = sb.toString();
            Picasso.with(context).load(r).placeholder(R.drawable.default_pic_load).error(R.drawable.default_pic_error).into(ivFeedCenter);
        }
        if (feedItem.getLikesCount() > 0) {
            int currentLikesCount = feedItem.getLikesCount();
            String likesCountText = context.getResources().getQuantityString(
                    R.plurals.likes_count, currentLikesCount, currentLikesCount
            );
            tsLikesCounter.setCurrentText(likesCountText);
        }
        if (feedItem.isClicked()) {
            btnLike.setImageResource(R.drawable.ic_heart_red);
        } else {
            btnLike.setImageResource(R.drawable.ic_heart_outline_grey);
        }
        if (!TextUtils.isEmpty(feedItem.getTextContext())) {
            ivFeedBottom.setText(feedItem.getTextContext());
        } else {
            ivFeedBottom.setText("");
        }
        setLikeOnUi();
    }


    private void changeLike() {

        if (feedItem.isClicked()) {
            //取消赞
            int currentLikesCount = feedItem.getLikesCount() - 1;
            String likesCountText = context.getResources().getQuantityString(
                    R.plurals.likes_count, currentLikesCount, currentLikesCount
            );
            feedItem.setClicked(false);
            feedItem.setLikesCount(currentLikesCount);
        } else {
            int currentLikesCount = feedItem.getLikesCount() + 1;
            String likesCountText = context.getResources().getQuantityString(
                    R.plurals.likes_count, currentLikesCount, currentLikesCount
            );
            feedItem.setClicked(true);
            feedItem.setLikesCount(currentLikesCount);
        }
        User u = UserHelp.getInstance().getCurrentUser(context);
        if (u != null) {
            ApiClient.create()
                    .withService("Likes.UserClickLike")
                    .addParams("uid", u.getUid())
                    .addParams("imgId", feedItem.getImgId())
                    .addParams("3917e150bbaa953b", u.getSessionToken())
                    .request(new ApiCallback() {
                        @Override
                        public void onFailure(Request request, IOException e) {

                        }

                        @Override
                        public void onResponse(ApiClientResponse apiClientResponse) throws IOException {
                            if (apiClientResponse.getRet() != 200) {
                                return;
                            }
                            LogUtils.i("feed1adapter", "赞操作成功");
                        }
                    });
        }

        setLikeOnUi();
    }

    private void setLikeOnUi() {

        int currentLikesCount = feedItem.getLikesCount();
        String likesCountText = context.getResources().getQuantityString(
                R.plurals.likes_count, currentLikesCount, currentLikesCount
        );
        tsLikesCounter.setCurrentText(likesCountText);

        if (feedItem.isClicked()) {
            btnLike.setImageResource(R.drawable.ic_heart_red);
        } else {
            btnLike.setImageResource(R.drawable.ic_heart_outline_grey);
        }
    }


    /**
     * 图片上的点赞动画
     *
     * @param holder
     */
    private final Map<String, AnimatorSet> likeAnimations = new HashMap<>();
    private String animationsKey = "1";
    private static final DecelerateInterpolator DECCELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);

    private void animatePhotoLike() {
        if (!likeAnimations.containsKey(animationsKey)) {
            vBgLike.setVisibility(View.VISIBLE);
            ivLike.setVisibility(View.VISIBLE);

            vBgLike.setScaleY(0.1f);
            vBgLike.setScaleX(0.1f);
            vBgLike.setAlpha(1f);
            ivLike.setScaleY(0.1f);
            ivLike.setScaleX(0.1f);

            AnimatorSet animatorSet = new AnimatorSet();
            likeAnimations.put(animationsKey, animatorSet);

            ObjectAnimator bgScaleYAnim = ObjectAnimator.ofFloat(vBgLike, "scaleY", 0.1f, 1f);
            bgScaleYAnim.setDuration(200);
            bgScaleYAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
            ObjectAnimator bgScaleXAnim = ObjectAnimator.ofFloat(vBgLike, "scaleX", 0.1f, 1f);
            bgScaleXAnim.setDuration(200);
            bgScaleXAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
            ObjectAnimator bgAlphaAnim = ObjectAnimator.ofFloat(vBgLike, "alpha", 1f, 0f);
            bgAlphaAnim.setDuration(200);
            bgAlphaAnim.setStartDelay(150);
            bgAlphaAnim.setInterpolator(DECCELERATE_INTERPOLATOR);

            ObjectAnimator imgScaleUpYAnim = ObjectAnimator.ofFloat(ivLike, "scaleY", 0.1f, 1f);
            imgScaleUpYAnim.setDuration(300);
            imgScaleUpYAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
            ObjectAnimator imgScaleUpXAnim = ObjectAnimator.ofFloat(ivLike, "scaleX", 0.1f, 1f);
            imgScaleUpXAnim.setDuration(300);
            imgScaleUpXAnim.setInterpolator(DECCELERATE_INTERPOLATOR);

            ObjectAnimator imgScaleDownYAnim = ObjectAnimator.ofFloat(ivLike, "scaleY", 1f, 0f);
            imgScaleDownYAnim.setDuration(300);
            imgScaleDownYAnim.setInterpolator(ACCELERATE_INTERPOLATOR);
            ObjectAnimator imgScaleDownXAnim = ObjectAnimator.ofFloat(ivLike, "scaleX", 1f, 0f);
            imgScaleDownXAnim.setDuration(300);
            imgScaleDownXAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

            animatorSet.playTogether(bgScaleYAnim, bgScaleXAnim, bgAlphaAnim, imgScaleUpYAnim, imgScaleUpXAnim);
            animatorSet.play(imgScaleDownYAnim).with(imgScaleDownXAnim).after(imgScaleUpYAnim);

            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    likeAnimations.remove(animationsKey);
                    vBgLike.setVisibility(View.GONE);
                    ivLike.setVisibility(View.GONE);
                }
            });
            animatorSet.start();
        }
    }
}
