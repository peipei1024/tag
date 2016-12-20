package com.ngc123.tag.fragment.find;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.okhttp.Request;
import com.ngc123.tag.api.ApiCallback;
import com.ngc123.tag.api.ApiClient;
import com.ngc123.tag.api.ApiClientResponse;
import com.ngc123.tag.bean.User;
import com.ngc123.tag.R;
import com.ngc123.tag.model.FeedItem;
import com.ngc123.tag.ui.comment.Comment1Activity;
import com.ngc123.tag.ui.person.Person1Activity;
import com.ngc123.tag.ui.person.PersonActivity;
import com.ngc123.tag.util.IntentUtils;
import com.ngc123.tag.util.LogUtils;
import com.ngc123.tag.util.ToastUtils;
import com.ngc123.tag.util.UserHelp;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
* Class name :FindHolder
*
* Version information :
*
* Describe ：
*
* Author ：裴徐泽
*
* Created by pei on 2016-8-1.
*
*/
public class FindHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.id_iv_pic)
    ImageView idIvPic;
    @Bind(R.id.id_tv_content)
    TextView idTvContent;
    @Bind(R.id.id_iv_head)
    ImageView idIvHead;
    @Bind(R.id.id_tv_username)
    TextView idTvUsername;
    @Bind(R.id.id_iv_like)
    ImageView idIvLike;
    @Bind(R.id.id_tv_like)
    TextView idTvLike;
    @Bind(R.id.vBgLike)
    View mVBgLike;
    @Bind(R.id.ivLike)
    ImageView mIvLike;


    private FeedItem feedItem;
    private Context context;

    public FindHolder(LayoutInflater inflater, ViewGroup parent) {
        this(inflater.inflate(R.layout.view_find_item, parent, false));
    }

    public FindHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public void bindData(FeedItem feedItem, Context context) {
        this.feedItem = feedItem;
        this.context = context;
        changeUi();
    }

    @OnClick({R.id.id_iv_pic, R.id.id_tv_content, R.id.id_iv_head, R.id.id_tv_username, R.id.id_iv_like, R.id.id_tv_like})
    public void onClick(View view) {
        User user = null;
        switch (view.getId()) {
            case R.id.id_iv_pic:
                user = UserHelp.getInstance().getCurrentUser(context);
                if (user != null){
                    IntentUtils.doIntentWithSerializable(context, Comment1Activity.class, "feeditem", feedItem);
                }else {
                    ToastUtils.toast(context, "请先登录");
                }
                break;
            case R.id.id_tv_content:
                break;
            case R.id.id_iv_head:
                user = UserHelp.getInstance().getCurrentUser(context);
                if (user != null){
                    IntentUtils.doIntentWithString(context, Person1Activity.class, "id", feedItem.getUid());
                }else {
                    ToastUtils.toast(context, "请先登录");
                }
                break;
            case R.id.id_tv_username:
                user = UserHelp.getInstance().getCurrentUser(context);
                if (user != null){
                    IntentUtils.doIntentWithString(context, Person1Activity.class, "id", feedItem.getUid());
                }else {
                    ToastUtils.toast(context, "请先登录");
                }
                break;
            case R.id.id_iv_like:
                user = UserHelp.getInstance().getCurrentUser(context);
                if (user != null){
                    animatePhotoLike();
                    changeLike();
                }else {
                    ToastUtils.toast(context, "请先登录");
                }
                break;
            case R.id.id_tv_like:
                user = UserHelp.getInstance().getCurrentUser(context);
                if (user != null){
                    animatePhotoLike();
                    changeLike();
                }else {
                    ToastUtils.toast(context, "请先登录");
                }
                break;
        }
    }

    private void changeUi() {
        if (!TextUtils.isEmpty(feedItem.getAvatar())) {
            StringBuffer sb = new StringBuffer("http://7xstnm.com2.z0.glb.clouddn.com/");
            sb.append(feedItem.getAvatar());
            String r = sb.toString();
            Picasso.with(context).load(r).placeholder(R.drawable.default_avatar).error(R.drawable.default_avatar).into(idIvHead);
        }
        if (!TextUtils.isEmpty(feedItem.getUsername())) {
            idTvUsername.setText(feedItem.getUsername());
        }
        if (!TextUtils.isEmpty(feedItem.getImgName())) {
            StringBuffer sb = new StringBuffer("http://7xstnm.com2.z0.glb.clouddn.com/");
            sb.append(feedItem.getImgName());
            String r = sb.toString();
            Picasso.with(context).load(r).placeholder(R.drawable.default_pic_load).error(R.drawable.default_pic_error).into(idIvPic);
        }
        if (!TextUtils.isEmpty(feedItem.getTextContext())) {
            idTvContent.setText(feedItem.getTextContext());
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
        }else {
            ToastUtils.toast(context, "请先登录");
        }

        setLikeOnUi();
    }

    private void setLikeOnUi() {

        int currentLikesCount = feedItem.getLikesCount();
        idTvLike.setText(currentLikesCount + " ");

        if (feedItem.isClicked()) {
            idIvLike.setImageResource(R.drawable.ic_heart_red);
        } else {
            idIvLike.setImageResource(R.drawable.ic_heart_outline_grey);
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
