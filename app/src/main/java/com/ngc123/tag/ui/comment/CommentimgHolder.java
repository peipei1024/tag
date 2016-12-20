package com.ngc123.tag.ui.comment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.avos.avoscloud.okhttp.Request;
import com.ngc123.tag.api.ApiCallback;
import com.ngc123.tag.api.ApiClient;
import com.ngc123.tag.api.ApiClientResponse;
import com.ngc123.tag.bean.User;
import com.ngc123.tag.event.Follow;
import com.ngc123.tag.R;
import com.ngc123.tag.model.FeedItem;
import com.ngc123.tag.model.TagItem;
import com.ngc123.tag.ui.PicActivity;
import com.ngc123.tag.ui.WebviewActivity;
import com.ngc123.tag.ui.person.Person1Activity;
import com.ngc123.tag.ui.person.PersonActivity;
import com.ngc123.tag.util.EffectUtil;
import com.ngc123.tag.util.IntentUtils;
import com.ngc123.tag.util.LogUtils;
import com.ngc123.tag.util.ToastUtils;
import com.ngc123.tag.util.UserHelp;
import com.ngc123.tag.view.LabelView;
import com.ngc123.tag.view.SquaredFrameLayout;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/*
* Class name :CommentimgHolder
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
public class CommentimgHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.id_iv_head)
    ImageView mIdIvHead;
    @Bind(R.id.id_tv_name)
    TextView mIdTvName;
    @Bind(R.id.id_btn_follow)
    Button mIdBtnFollow;
    @Bind(R.id.ivFeedCenter)
    ImageView mIvFeedCenter;
    @Bind(R.id.pictureLayout)
    RelativeLayout mPictureLayout;
    @Bind(R.id.vBgLike)
    View mVBgLike;
    @Bind(R.id.ivLike)
    ImageView mIvLike;
    @Bind(R.id.vImageRoot)
    SquaredFrameLayout mVImageRoot;
    @Bind(R.id.ivFeedBottom)
    TextView mIvFeedBottom;
    @Bind(R.id.id_tv_time)
    TextView mIdTvTime;
    @Bind(R.id.btnLike)
    ImageButton mBtnLike;
    @Bind(R.id.tsLikesCounter)
    TextSwitcher mTsLikesCounter;

    private FeedItem feedItem;
    private Context context;

    public CommentimgHolder(LayoutInflater inflater, ViewGroup parent) {
        this(inflater.inflate(R.layout.view_comment_image, parent, false));
    }

    public CommentimgHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public void bindData(FeedItem feedItem, Context context){
        this.feedItem = feedItem;
        this.context = context;
        changeUi();
    }

    public void clearTag(){
        mPictureLayout.removeViews(1, mPictureLayout.getChildCount() - 1);
    }
    @OnClick({R.id.id_iv_head, R.id.id_tv_name, R.id.id_btn_follow, R.id.ivFeedCenter, R.id.ivFeedBottom, R.id.btnLike, R.id.tsLikesCounter})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_iv_head:
                IntentUtils.doIntentWithString(context, Person1Activity.class, "id", feedItem.getUid());
                break;
            case R.id.id_tv_name:
                IntentUtils.doIntentWithString(context, Person1Activity.class, "id", feedItem.getUid());
                break;
            case R.id.id_btn_follow:
                changeFollow();
                break;
            case R.id.ivFeedCenter:
                IntentUtils.doIntentWithSerializable(context, PicActivity.class, "url", feedItem);
                break;
            case R.id.ivFeedBottom:
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

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 2:
                    ToastUtils.toast(context, "操作失败");
                    break;
                case 3:
                    ToastUtils.toast(context, "自己不能关注自己哦");
                    break;
            }
        }
    };
    private void changeFollow(){
        User user = UserHelp.getInstance().getCurrentUser(context);
        if (user != null){
            ApiClient apiClient = null;
            if (feedItem.getIsFollowee().equals("Y")){
                //取消关注
                apiClient = unFollow(user);
            }else if (feedItem.getIsFollowee().equals("N")){
                apiClient = follow(user);
            }else if (feedItem.getIsFollowee().equals("s")){
                handler.sendEmptyMessage(3);
                return;
            }
            apiClient.request(new ApiCallback() {
                @Override
                public void onFailure(Request request, IOException e) {

                }

                @Override
                public void onResponse(ApiClientResponse apiClientResponse) throws IOException {
                    if (apiClientResponse.getRet() == 200){
                        LogUtils.i("commentholder", apiClientResponse.getData());
                        EventBus.getDefault().post(new Follow());
                    }else {
                        handler.sendEmptyMessage(2);
                    }
                }
            });
        }else {
            ToastUtils.toast(context, "请先登录");
        }

    }

    private ApiClient unFollow(User user){
        return ApiClient.create()
                .withService("User.UnFollow")
                .addParams("uD", user.getUid())
                .addParams("fD", feedItem.getUid())
                .addParams("3917e150bbaa953b", user.getSessionToken());
    }
    private ApiClient follow(User user){
        return ApiClient.create()
                .withService("User.Follow")
                .addParams("uD", user.getUid())
                .addParams("fD", feedItem.getUid())
                .addParams("3917e150bbaa953b", user.getSessionToken());
    }

    public void drawTag(){
        List<TagItem> list = feedItem.getTagList();
        for (TagItem feedImageTag : list) {
            LabelView tagView = new LabelView(context);
            tagView.init(feedImageTag);
            EffectUtil.myaddLabel(mPictureLayout, tagView, feedImageTag.getX(), feedImageTag.getY());
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

    private void changeUi() {
        if (!TextUtils.isEmpty(feedItem.getAvatar())) {
            StringBuffer sb = new StringBuffer("http://7xstnm.com2.z0.glb.clouddn.com/");
            sb.append(feedItem.getAvatar());
            String r = sb.toString();
            Picasso.with(context).load(r).placeholder(R.drawable.default_avatar).error(R.drawable.default_pic_error).into(mIdIvHead);
        }else {
            Picasso.with(context).load(R.drawable.default_avatar).into(mIdIvHead);
        }
        if (!TextUtils.isEmpty(feedItem.getUsername())) {
            mIdTvName.setText(feedItem.getUsername());
        }else {
            mIdTvName.setText("");
        }
        if (!TextUtils.isEmpty(feedItem.getImgName())) {
            StringBuffer sb = new StringBuffer("http://7xstnm.com2.z0.glb.clouddn.com/");
            sb.append(feedItem.getImgName());
            String r = sb.toString();
            Picasso.with(context).load(r).placeholder(R.drawable.default_pic_load).error(R.drawable.default_pic_error).into(mIvFeedCenter);
        }
        if (!TextUtils.isEmpty(feedItem.getTextContext())) {
            mIvFeedBottom.setText(feedItem.getTextContext());
        }else {
            mIvFeedBottom.setText("");
        }
        setLikeOnUi();
        if (feedItem.getIsFollowee().equals("Y")){
            mIdBtnFollow.setBackground(context.getResources().getDrawable(R.drawable.btn_follow_bg_y));
            mIdBtnFollow.setTextColor(context.getResources().getColor(R.color.colorbtnfoolow));
            mIdBtnFollow.setText("已关注");
        }else if (feedItem.getIsFollowee().equals("N")){
            mIdBtnFollow.setBackground(context.getResources().getDrawable(R.drawable.btn_follow_bg_n));
            mIdBtnFollow.setTextColor(context.getResources().getColor(R.color.colorMain));
            mIdBtnFollow.setText("+ 关注");
        }else if (feedItem.getIsFollowee().equals("s")){
            mIdBtnFollow.setText("自己");
        }

    }

    private void changeLike(){

        if (feedItem.isClicked()){
            //取消赞
            int currentLikesCount = feedItem.getLikesCount() - 1;
            String likesCountText = context.getResources().getQuantityString(
                    R.plurals.likes_count, currentLikesCount, currentLikesCount
            );
            feedItem.setClicked(false);
            feedItem.setLikesCount(currentLikesCount);
        }else {
            int currentLikesCount = feedItem.getLikesCount() + 1;
            String likesCountText = context.getResources().getQuantityString(
                    R.plurals.likes_count, currentLikesCount, currentLikesCount
            );
            feedItem.setClicked(true);
            feedItem.setLikesCount(currentLikesCount);
        }
        User u = UserHelp.getInstance().getCurrentUser(context);
        if (u != null){
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
                            if (apiClientResponse.getRet() != 200){
                                return;
                            }
                            LogUtils.i("feed1adapter", "赞操作成功");
                        }
                    });
        }

        setLikeOnUi();
    }

    private void setLikeOnUi(){

        int currentLikesCount = feedItem.getLikesCount();
        String likesCountText = context.getResources().getQuantityString(
                R.plurals.likes_count, currentLikesCount, currentLikesCount
        );
        mTsLikesCounter.setCurrentText(likesCountText);

        if (feedItem.isClicked()){
            mBtnLike.setImageResource(R.drawable.ic_heart_red);
        }else {
            mBtnLike.setImageResource(R.drawable.ic_heart_outline_grey);
        }
    }



    /**
     * 图片上的点赞动画
     * @param holder
     */
    private final Map<String, AnimatorSet> likeAnimations = new HashMap<>();
    private String animationsKey = "1";
    private static final DecelerateInterpolator DECCELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);
    private void animatePhotoLike() {
        if (!likeAnimations.containsKey(animationsKey)) {
            mVBgLike.setVisibility(View.VISIBLE);
            mIvLike.setVisibility(View.VISIBLE);

            mVBgLike.setScaleY(0.1f);
            mVBgLike.setScaleX(0.1f);
            mVBgLike.setAlpha(1f);
            mIvLike.setScaleY(0.1f);
            mIvLike.setScaleX(0.1f);

            AnimatorSet animatorSet = new AnimatorSet();
            likeAnimations.put(animationsKey, animatorSet);

            ObjectAnimator bgScaleYAnim = ObjectAnimator.ofFloat(mVBgLike, "scaleY", 0.1f, 1f);
            bgScaleYAnim.setDuration(200);
            bgScaleYAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
            ObjectAnimator bgScaleXAnim = ObjectAnimator.ofFloat(mVBgLike, "scaleX", 0.1f, 1f);
            bgScaleXAnim.setDuration(200);
            bgScaleXAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
            ObjectAnimator bgAlphaAnim = ObjectAnimator.ofFloat(mVBgLike, "alpha", 1f, 0f);
            bgAlphaAnim.setDuration(200);
            bgAlphaAnim.setStartDelay(150);
            bgAlphaAnim.setInterpolator(DECCELERATE_INTERPOLATOR);

            ObjectAnimator imgScaleUpYAnim = ObjectAnimator.ofFloat(mIvLike, "scaleY", 0.1f, 1f);
            imgScaleUpYAnim.setDuration(300);
            imgScaleUpYAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
            ObjectAnimator imgScaleUpXAnim = ObjectAnimator.ofFloat(mIvLike, "scaleX", 0.1f, 1f);
            imgScaleUpXAnim.setDuration(300);
            imgScaleUpXAnim.setInterpolator(DECCELERATE_INTERPOLATOR);

            ObjectAnimator imgScaleDownYAnim = ObjectAnimator.ofFloat(mIvLike, "scaleY", 1f, 0f);
            imgScaleDownYAnim.setDuration(300);
            imgScaleDownYAnim.setInterpolator(ACCELERATE_INTERPOLATOR);
            ObjectAnimator imgScaleDownXAnim = ObjectAnimator.ofFloat(mIvLike, "scaleX", 1f, 0f);
            imgScaleDownXAnim.setDuration(300);
            imgScaleDownXAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

            animatorSet.playTogether(bgScaleYAnim, bgScaleXAnim, bgAlphaAnim, imgScaleUpYAnim, imgScaleUpXAnim);
            animatorSet.play(imgScaleDownYAnim).with(imgScaleDownXAnim).after(imgScaleUpYAnim);

            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    likeAnimations.remove(animationsKey);
                    mVBgLike.setVisibility(View.GONE);
                    mIvLike.setVisibility(View.GONE);
                }
            });
            animatorSet.start();
        }
    }
}
