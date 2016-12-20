package com.ngc123.tag.adapter;


/**
 * 废弃
 */
//public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.CellFeedViewHolder> implements View.OnClickListener {
//    private static final int VIEW_TYPE_DEFAULT = 1;
//    private static final int VIEW_TYPE_LOADER = 2;
//
//    private static final DecelerateInterpolator DECCELERATE_INTERPOLATOR = new DecelerateInterpolator();
//    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
//    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);
//
//    private static final int ANIMATED_ITEMS_COUNT = 2;
//
//    private Context context;
//    private int lastAnimatedPosition = -1;
//    private boolean animateItems = false;
//
//    int position;
//    private final Map<Integer, Integer> likesCount = new HashMap<>();
//    private final Map<RecyclerView.ViewHolder, AnimatorSet> likeAnimations = new HashMap<>();
//    private final ArrayList<Integer> likedPositions = new ArrayList<>();
//
//    private OnFeedItemClickListener onFeedItemClickListener;
//
//    private int loadingViewSize = App.getApp().dp2px(200);
//
//    private List<FeedItem> items = new ArrayList<FeedItem>();
//
//    public void setList(List<FeedItem> list) {
//        //MainActivity.count = MainActivity.count + 1;
//        if (items.size() > 0) {
//            items.clear();
//        }
//        items.addAll(list);
//    }
//
//    public void addList(List<FeedItem> list) {
//        items.addAll(list);
//    }
//
//    public FeedAdapter(Context context) {
//        this.context = context;
//    }
//
//    @Override
//    public CellFeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        final View view = LayoutInflater.from(context).inflate(R.layout.item_feed, parent, false);
//        final CellFeedViewHolder cellFeedViewHolder = new CellFeedViewHolder(view);
//        cellFeedViewHolder.btnComments.setOnClickListener(this);
//        cellFeedViewHolder.btnMore.setOnClickListener(this);
//        cellFeedViewHolder.draweeView.setOnClickListener(this);
//        cellFeedViewHolder.btnLike.setOnClickListener(this);
//        cellFeedViewHolder.ivUserProfile.setOnClickListener(this);
//        return cellFeedViewHolder;
//    }
//
//
//
//    @Override
//    public void onBindViewHolder(CellFeedViewHolder viewHolder, int position) {
//        runEnterAnimation(viewHolder.itemView, position);
//        FeedItem feedItem = items.get(position);
//        viewHolder.draweeView.setImageURI(feedItem.getImgUri());
//        viewHolder.ivFeedBottom.setText(feedItem.getTextContext());
//        viewHolder.setTagList(feedItem.getTagList());
//
//        initLikesCounter(viewHolder,false,position);
//        updateHeartButton(viewHolder, false, position);
//
//
//        if (likeAnimations.containsKey(viewHolder)) {
//            likeAnimations.get(viewHolder).cancel();
//        }
//        resetLikeAnimationState(viewHolder);
//
//    }
//
//
//
//    @Override
//    public int getItemViewType(int position) {
//        return VIEW_TYPE_DEFAULT;
//    }
//
//    @Override
//    public void onViewRecycled(CellFeedViewHolder holder) {
//
//        // 将标签移除,避免回收使用时标签重复
//        holder.pictureLayout.removeViews(1, holder.pictureLayout.getChildCount() - 1);
//        super.onViewRecycled(holder);
//    }
//
//
//    @Override
//    public void onViewAttachedToWindow(final CellFeedViewHolder holder) {
//        super.onViewAttachedToWindow(holder);
//        // 这里可能有问题 延迟200毫秒加载是为了等pictureLayout已经在屏幕上显示getWidth才为具体的值
//        holder.pictureLayout.getHandler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                for (TagItem feedImageTag : holder.getTagList()) {
//                    LabelView tagView = new LabelView(context);
//                    tagView.init(feedImageTag);
//                    tagView.draw(holder.pictureLayout,
//                            (int) (feedImageTag.getX() * ((double) holder.pictureLayout.getWidth() / (double) 1242)),
//                            (int) (feedImageTag.getY() * ((double) holder.pictureLayout.getWidth() / (double) 1242)),
//                            feedImageTag.isLeft());
//                    tagView.wave();
//                }
//            }
//        }, 200);
//    }
//
//    @Override
//    public int getItemCount() {
//        return items.size();
//    }
//
//    private void initLikesCounter(CellFeedViewHolder holder ,boolean animated,int position){
//        int currentLikesCount = items.get(position).getLikesCount();
//        String likesCountText = context.getResources().getQuantityString(
//                R.plurals.likes_count, currentLikesCount, currentLikesCount
//        );
//
//        if (animated) {
//            holder.tsLikesCounter.setText(likesCountText);
//        } else {
//            holder.tsLikesCounter.setCurrentText(likesCountText);
//        }
//
//        likesCount.put(holder.getLayoutPosition(), currentLikesCount);
//    }
//
//    private void updateLikesCounter(CellFeedViewHolder holder, boolean animated) {
//        int currentLikesCount;
//        Log.i("adapter", items.get(position).getLikesCount() + "点击之前   ");
//        if(items.get(position).getClicked()){
//            currentLikesCount = items.get(position).getLikesCount()+1;
//            Log.i("adapter", items.get(position).getLikesCount() + "关注 ");
//        }else{
//            currentLikesCount = items.get(position).getLikesCount()-1;
//        }
//        String likesCountText = context.getResources().getQuantityString(
//                R.plurals.likes_count, currentLikesCount, currentLikesCount
//        );
//
//        if (animated) {
//            holder.tsLikesCounter.setText(currentLikesCount + " likes");
//        } else {
//            holder.tsLikesCounter.setCurrentText(currentLikesCount + " likes");
//        }
//
////        likesCount.put(holder.getLayoutPosition(), currentLikesCount);
//    }
//
//    /**
//     * 右下角星形的动画
//     * @param holder
//     * @param animated
//     * @param position
//     */
//    private void updateHeartButton(final CellFeedViewHolder holder, boolean animated, int position) {
//        if (animated) {
//            if (!likeAnimations.containsKey(holder)) {
//                AnimatorSet animatorSet = new AnimatorSet();
//                likeAnimations.put(holder, animatorSet);
//
//                ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(holder.btnLike, "rotation", 0f, 360f);
//                rotationAnim.setDuration(300);
//                rotationAnim.setInterpolator(ACCELERATE_INTERPOLATOR);
//
//                ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(holder.btnLike, "scaleX", 0.2f, 1f);
//                bounceAnimX.setDuration(300);
//                bounceAnimX.setInterpolator(OVERSHOOT_INTERPOLATOR);
//
//                ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(holder.btnLike, "scaleY", 0.2f, 1f);
//                bounceAnimY.setDuration(300);
//                bounceAnimY.setInterpolator(OVERSHOOT_INTERPOLATOR);
//                bounceAnimY.addListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationStart(Animator animation) {
//                        if(items.get(position).getClicked()){
//                            holder.btnLike.setImageResource(R.drawable.ic_heart_red);
//                            items.get(position).setClicked(false);;
//                        }else{
//                            holder.btnLike.setImageResource(R.drawable.ic_heart_outline_grey);
//                            items.get(position).setClicked(true);
//                        }
//
//                    }
//                });
//
//                animatorSet.play(rotationAnim);
//                animatorSet.play(bounceAnimX).with(bounceAnimY).after(rotationAnim);
//
//                animatorSet.addListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        resetLikeAnimationState(holder);
//                    }
//                });
//
//                animatorSet.start();
//            }
//        } else {
//            if(items.get(position).getClicked()){
//                holder.btnLike.setImageResource(R.drawable.ic_heart_red);
//                items.get(position).setClicked(false);
//            }else{
//                holder.btnLike.setImageResource(R.drawable.ic_heart_outline_grey);
//                items.get(position).setClicked(true);
//            }
//        }
//    }
//
//    @Override
//    public void onClick(View view) {
//        final int viewId = view.getId();
//        if (viewId == R.id.btnComments) {
//            if (onFeedItemClickListener != null) {
//                onFeedItemClickListener.onCommentsClick(view, (Integer) view.getTag());
//            }
//        } else if (viewId == R.id.btnMore) {
//            if (onFeedItemClickListener != null) {
//                onFeedItemClickListener.onMoreClick(view, (Integer) view.getTag());
//            }
//        } else if (viewId == R.id.btnLike) {
//            CellFeedViewHolder holder = (CellFeedViewHolder) view.getTag();
//            updateLikesCounter(holder, true);
//            animatePhotoLike(holder);
//            updateHeartButton(holder, false, holder.getLayoutPosition());
//        } else if (viewId == R.id.ivFeedCenter) {
//            CellFeedViewHolder holder = (CellFeedViewHolder) view.getTag();
//            updateLikesCounter(holder, true);
//            animatePhotoLike(holder);
//            updateHeartButton(holder, false, holder.getLayoutPosition());
//        } else if (viewId == R.id.avatar_img) {
//            if (onFeedItemClickListener != null) {
//                onFeedItemClickListener.onProfileClick(view);
//            }
//        }
//    }
//
//    /**
//     * 图片上的点赞动画
//     * @param holder
//     */
//    private void animatePhotoLike(final CellFeedViewHolder holder) {
//        if (!likeAnimations.containsKey(holder)) {
//            holder.vBgLike.setVisibility(View.VISIBLE);
//            holder.ivLike.setVisibility(View.VISIBLE);
//
//            holder.vBgLike.setScaleY(0.1f);
//            holder.vBgLike.setScaleX(0.1f);
//            holder.vBgLike.setAlpha(1f);
//            holder.ivLike.setScaleY(0.1f);
//            holder.ivLike.setScaleX(0.1f);
//
//            AnimatorSet animatorSet = new AnimatorSet();
//            likeAnimations.put(holder, animatorSet);
//
//            ObjectAnimator bgScaleYAnim = ObjectAnimator.ofFloat(holder.vBgLike, "scaleY", 0.1f, 1f);
//            bgScaleYAnim.setDuration(200);
//            bgScaleYAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
//            ObjectAnimator bgScaleXAnim = ObjectAnimator.ofFloat(holder.vBgLike, "scaleX", 0.1f, 1f);
//            bgScaleXAnim.setDuration(200);
//            bgScaleXAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
//            ObjectAnimator bgAlphaAnim = ObjectAnimator.ofFloat(holder.vBgLike, "alpha", 1f, 0f);
//            bgAlphaAnim.setDuration(200);
//            bgAlphaAnim.setStartDelay(150);
//            bgAlphaAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
//
//            ObjectAnimator imgScaleUpYAnim = ObjectAnimator.ofFloat(holder.ivLike, "scaleY", 0.1f, 1f);
//            imgScaleUpYAnim.setDuration(300);
//            imgScaleUpYAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
//            ObjectAnimator imgScaleUpXAnim = ObjectAnimator.ofFloat(holder.ivLike, "scaleX", 0.1f, 1f);
//            imgScaleUpXAnim.setDuration(300);
//            imgScaleUpXAnim.setInterpolator(DECCELERATE_INTERPOLATOR);
//
//            ObjectAnimator imgScaleDownYAnim = ObjectAnimator.ofFloat(holder.ivLike, "scaleY", 1f, 0f);
//            imgScaleDownYAnim.setDuration(300);
//            imgScaleDownYAnim.setInterpolator(ACCELERATE_INTERPOLATOR);
//            ObjectAnimator imgScaleDownXAnim = ObjectAnimator.ofFloat(holder.ivLike, "scaleX", 1f, 0f);
//            imgScaleDownXAnim.setDuration(300);
//            imgScaleDownXAnim.setInterpolator(ACCELERATE_INTERPOLATOR);
//
//            animatorSet.playTogether(bgScaleYAnim, bgScaleXAnim, bgAlphaAnim, imgScaleUpYAnim, imgScaleUpXAnim);
//            animatorSet.play(imgScaleDownYAnim).with(imgScaleDownXAnim).after(imgScaleUpYAnim);
//
//            animatorSet.addListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    resetLikeAnimationState(holder);
//                }
//            });
//            animatorSet.start();
//        }
//    }
//
//    /**
//     *
//     * @param holder
//     */
//    private void resetLikeAnimationState(CellFeedViewHolder holder) {
//        likeAnimations.remove(holder);
//        holder.vBgLike.setVisibility(View.GONE);
//        holder.ivLike.setVisibility(View.GONE);
//    }
//
//
//
//
//    public void setOnFeedItemClickListener(OnFeedItemClickListener onFeedItemClickListener) {
//        this.onFeedItemClickListener = onFeedItemClickListener;
//    }
//
//
//
//    public static class CellFeedViewHolder extends RecyclerView.ViewHolder {
////        @Bind(R.id.ivFeedCenter)
////        ImageView ivFeedCenter;
//        SimpleDraweeView draweeView;
//        @Bind(R.id.ivFeedBottom)
//        TextView ivFeedBottom;
//        @Bind(R.id.btnComments)
//        ImageButton btnComments;
//        @Bind(R.id.btnLike)
//        ImageButton btnLike;
//        @Bind(R.id.btnMore)
//        ImageButton btnMore;
//        @Bind(R.id.vBgLike)
//        View vBgLike;
//        @Bind(R.id.ivLike)
//        ImageView ivLike;
//        @Bind(R.id.tsLikesCounter)
//        TextSwitcher tsLikesCounter;
//        @Bind(R.id.avatar_img)
//        ImageView ivUserProfile;
//        @Bind(R.id.vImageRoot)
//        FrameLayout vImageRoot;
//        @Bind(R.id.pictureLayout)
//        RelativeLayout pictureLayout;
//
//
//        SendingProgressView vSendingProgress;
//        View vProgressBg;
//
//        //new add
//        private List<TagItem> tagList = new ArrayList<>();
//
//        public List<TagItem> getTagList() {
//            return tagList;
//        }
//
//        public void setTagList(List<TagItem> tagList) {
//            if (this.tagList.size() > 0) {
//                this.tagList.clear();
//            }
//            if(tagList == null || tagList.size() <= 0){
//                return;
//            }
//            this.tagList.addAll(tagList);
//        }
//
//        public CellFeedViewHolder(View view) {
//            super(view);
//            draweeView = (SimpleDraweeView) view.findViewById(R.id.ivFeedCenter);
//            ButterKnife.bind(this,view);
//        }
//
//    }
//
//    public interface OnFeedItemClickListener {
//        public void onCommentsClick(View v, int position);
//
//        public void onMoreClick(View v, int position);
//
//        public void onProfileClick(View v);
//    }
//
//
//    /**
//     *加载数据项的动画
//     * @param view
//     * @param position
//     */
//    private void runEnterAnimation(View view, int position) {
//        if (!animateItems || position >= ANIMATED_ITEMS_COUNT - 1) {
//            return;
//        }
//
//        if (position > lastAnimatedPosition) {
//            lastAnimatedPosition = position;
//            view.setTranslationY(App.getApp().getScreenHeight());
//            view.animate()
//                    .translationY(0)
//                    .setInterpolator(new DecelerateInterpolator(3.f))
//                    .setDuration(700)
//                    .start();
//        }
//    }
//}
