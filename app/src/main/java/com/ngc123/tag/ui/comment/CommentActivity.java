package com.ngc123.tag.ui.comment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.okhttp.Request;
import com.ngc123.tag.api.ApiCallback;
import com.ngc123.tag.api.ApiClient;
import com.ngc123.tag.api.ApiClientResponse;
import com.ngc123.tag.bean.CommentBean;
import com.ngc123.tag.bean.User;
import com.ngc123.tag.R;
import com.ngc123.tag.model.FeedItem;
import com.ngc123.tag.util.LogUtils;
import com.ngc123.tag.util.UserHelp;
import com.ngc123.tag.view.SquaredFrameLayout;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**废弃
 * Created by JiaM on 2016/4/19.
 */
public class CommentActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener{


    @Bind(R.id.id_iv_back_arrow)
    ImageView mIdIvBackArrow;
    @Bind(R.id.tag_title)
    LinearLayout mTagTitle;
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
    @Bind(R.id.scroll)
    ScrollView mScroll;
    @Bind(R.id.swipeLayout)
    SwipeRefreshLayout mSwipeLayout;
    @Bind(R.id.id_tv_time)
    TextView mIdTvTime;
    @Bind(R.id.btnLike)
    ImageButton mBtnLike;
    @Bind(R.id.tsLikesCounter)
    TextSwitcher mTsLikesCounter;
//    PullUpMoreListView mListView;

    private RecyclerView mRecyclerView;

    private FeedItem feedItem;



    private List<CommentBean> commentDate;
//    private CommentItemAdapter commentItemAdapter;
    private CommentAdapter mCommentAdapter;
    private CommentBean commentBean;
    private String[] commentContent = {"好像童话里的故事呦，太美，美在人的心房，让人不忍心去触碰，如身临其境给人一种感觉就是 好美哦",
            "晚会", "你的衣服很漂亮", "美", "直播嘛", "我？", "对", "美丽的长发", "中北三剑客", "友谊的小船说翻就翻,爱情的巨轮说沉就沉"};

    //handler
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    mSwipeLayout.setRefreshing(false);
                    //swipeRefreshLayout.setEnabled(false);
                    Toast.makeText(CommentActivity.this, "刷新完成...", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        ButterKnife.bind(this);

        mSwipeLayout.setOnRefreshListener(this);


        operoate();
        mRecyclerView = (RecyclerView) findViewById(R.id.recycleview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        mCommentAdapter = new CommentAdapter(this, commentDate);
        mRecyclerView.setAdapter(mCommentAdapter);

        Intent intent = getIntent();
        feedItem = (FeedItem) intent.getSerializableExtra("feeditem");
        mScroll.requestChildFocus(mIvFeedCenter, null);

        changeUi();
    }

    private void operoate() {
//        mSwipeLayout.setSize(SwipeRefreshLayout.DEFAULT);
//        swipeRefreshLayout.setProgressBackgroundColor(R.color.clickPress);


//        commentDate = new ArrayList<CommentBean>();
//        for (int i = 0; i < 10; i++) {
//            commentBean = new CommentBean();
//            commentBean.setComment_username("说好就算了" + i);
//            commentBean.setComment_month("4-" + i);
//            commentBean.setComment_time("16:0" + i);
//            commentBean.setComment_content(commentContent[i]);
//            commentDate.add(commentBean);
//        }
//        commentItemAdapter = new CommentItemAdapter(this, commentDate);
//        mListView = (PullUpMoreListView) findViewById(R.id.pulistview);
//        mListView.setAdapter(commentItemAdapter);
//        mListView.setLoadListener(new PullUpMoreListView.ILoadListener() {
//            @Override
//            public void onLoad() {
//
//            }
//        });

//        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(CommentActivity.this, "评论" + position, Toast.LENGTH_SHORT).show();
//            }
//        });
    }



    @Override
    public void onRefresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mHandler.sendEmptyMessage(1);
            }
        }).start();
    }


    @OnClick({R.id.id_iv_back_arrow, R.id.id_iv_head, R.id.id_tv_name, R.id.id_btn_follow, R.id.ivFeedCenter, R.id.btnLike, R.id.tsLikesCounter})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_iv_back_arrow:
                finish();
                break;
            case R.id.id_iv_head:
                break;
            case R.id.id_tv_name:
                break;
            case R.id.id_btn_follow:
                break;
            case R.id.ivFeedCenter:
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

    private void changeUi() {
        if (!TextUtils.isEmpty(feedItem.getAvatar())) {
            StringBuffer sb = new StringBuffer("http://7xstnm.com2.z0.glb.clouddn.com/");
            sb.append(feedItem.getAvatar());
            String r = sb.toString();
            Picasso.with(this).load(r).into(mIdIvHead);
        }
        if (!TextUtils.isEmpty(feedItem.getUsername())) {
            mIdTvName.setText(feedItem.getUsername());
        }
        if (!TextUtils.isEmpty(feedItem.getImgName())) {
            StringBuffer sb = new StringBuffer("http://7xstnm.com2.z0.glb.clouddn.com/");
            sb.append(feedItem.getImgName());
            String r = sb.toString();
            Picasso.with(this).load(r).into(mIvFeedCenter);
        }
        if (!TextUtils.isEmpty(feedItem.getTextContext())) {
            mIvFeedBottom.setText(feedItem.getTextContext());
        }
        setLikeOnUi();

    }


    private void changeLike(){

        if (feedItem.isClicked()){
            //取消赞
            int currentLikesCount = feedItem.getLikesCount() - 1;
            String likesCountText = getResources().getQuantityString(
                    R.plurals.likes_count, currentLikesCount, currentLikesCount
            );
            feedItem.setClicked(false);
            feedItem.setLikesCount(currentLikesCount);
        }else {
            int currentLikesCount = feedItem.getLikesCount() + 1;
            String likesCountText = getResources().getQuantityString(
                    R.plurals.likes_count, currentLikesCount, currentLikesCount
            );
            feedItem.setClicked(true);
            feedItem.setLikesCount(currentLikesCount);
        }
        User u = UserHelp.getInstance().getCurrentUser(this);
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
        String likesCountText = getResources().getQuantityString(
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
