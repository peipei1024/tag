package com.ngc123.tag.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.okhttp.Request;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ngc123.tag.App;
import com.ngc123.tag.AppConstants;
import com.ngc123.tag.adapter.FilterAdapter;
import com.ngc123.tag.adapter.StickerToolAdapter;
import com.ngc123.tag.api.ApiCallback;
import com.ngc123.tag.api.ApiClient;
import com.ngc123.tag.api.ApiClientResponse;
import com.ngc123.tag.bean.User;
import com.ngc123.tag.camera.EffectService;
import com.ngc123.tag.camera.effect.FilterEffect;
import com.ngc123.tag.R;
import com.ngc123.tag.event.AddLabel;
import com.ngc123.tag.event.ShowLabel;
import com.ngc123.tag.model.Addon;
import com.ngc123.tag.model.FeedItem;
import com.ngc123.tag.model.TagItem;
import com.ngc123.tag.ui.base.CameraBaseActivity;
import com.ngc123.tag.ui.tag.TagAdapter;
import com.ngc123.tag.util.EffectUtil;
import com.ngc123.tag.util.FileUtils;
import com.ngc123.tag.util.GPUImageFilterTools;
import com.ngc123.tag.util.ImageUtils;
import com.ngc123.tag.util.IntentUtils;
import com.ngc123.tag.util.JsonToMapList;
import com.ngc123.tag.util.LogUtils;
import com.ngc123.tag.util.StringUtils;
import com.ngc123.tag.util.ToastUtils;
import com.ngc123.tag.util.UserHelp;
import com.ngc123.tag.view.LabelSelector;
import com.ngc123.tag.view.LabelView;
import com.ngc123.tag.view.MyHighlightView;
import com.ngc123.tag.view.MyImageViewDrawableOverlay;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import it.sephiroth.android.library.widget.HListView;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageView;

/**
 * 图片处理界面
 * Created by sky on 2015/7/8.
 * Weibo: http://weibo.com/2030683111
 * Email: 1132234509@qq.com
 */
public class PhotoProcessActivity extends CameraBaseActivity {

    //滤镜图片
    @Bind(R.id.gpuimage)
    GPUImageView mGPUImageView;
    //绘图区域
    @Bind(R.id.drawing_view_container)
    ViewGroup drawArea;
    //底部按钮
    @Bind(R.id.sticker_btn)
    TextView stickerBtn;
    @Bind(R.id.filter_btn)
    TextView filterBtn;
    @Bind(R.id.text_btn)
    TextView labelBtn;
    //工具区
    @Bind(R.id.list_tools)
    HListView bottomToolBar;
    @Bind(R.id.toolbar_area)
    ViewGroup toolArea;
    private MyImageViewDrawableOverlay mImageView;
    private LabelSelector labelSelector;

    //当前选择底部按钮
    private TextView currentBtn;
    //当前图片
    private Bitmap currentBitmap;
    //用于预览的小图片
    private Bitmap smallImageBackgroud;
    //小白点标签
    private LabelView emptyLabelView;

    private List<LabelView> labels = new ArrayList<LabelView>();

    //标签区域
    private View commonLabelArea;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_process);
        ButterKnife.bind(this);
        EffectUtil.clear();
        EventBus.getDefault().register(this);
        initView();
        initEvent();
        initStickerToolBar();

        ImageUtils.asyncLoadImage(this, getIntent().getData(), new ImageUtils.LoadImageCallback() {
            @Override
            public void callback(Bitmap result) {
                currentBitmap = result;
                mGPUImageView.setImage(currentBitmap);
            }
        });

        ImageUtils.asyncLoadSmallImage(this, getIntent().getData(), new ImageUtils.LoadImageCallback() {
            @Override
            public void callback(Bitmap result) {
                smallImageBackgroud = result;
            }
        });
    }

    private void initView() {
        //添加贴纸水印的画布
        View overlay = LayoutInflater.from(PhotoProcessActivity.this).inflate(
                R.layout.view_drawable_overlay, null);
        mImageView = (MyImageViewDrawableOverlay) overlay.findViewById(R.id.drawable_overlay);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(App.getApp().getScreenWidth(),
                App.getApp().getScreenWidth());
        mImageView.setLayoutParams(params);
        overlay.setLayoutParams(params);
        drawArea.addView(overlay);
        //添加标签选择器
        RelativeLayout.LayoutParams rparams = new RelativeLayout.LayoutParams(App.getApp().getScreenWidth(), App.getApp().getScreenWidth());
        labelSelector = new LabelSelector(this);
        labelSelector.setLayoutParams(rparams);
        drawArea.addView(labelSelector);
        labelSelector.hide();

        //初始化滤镜图片
        mGPUImageView.setLayoutParams(rparams);


        //初始化空白标签
        emptyLabelView = new LabelView(this);
        emptyLabelView.setEmpty();
        EffectUtil.addLabelEditable(mImageView, drawArea, emptyLabelView,
                mImageView.getWidth() / 2, mImageView.getWidth() / 2);
        emptyLabelView.setVisibility(View.INVISIBLE);

        intiLebel();
        //初始化推荐标签栏
        commonLabelArea = LayoutInflater.from(PhotoProcessActivity.this).inflate(
                R.layout.view_label_bottom, null);
        TagHolder holder = new TagHolder();
        holder.recyclerView = (RecyclerView) commonLabelArea.findViewById(R.id.id_recycle);
        GridLayoutManager manager = new GridLayoutManager(this, 3);
        holder.recyclerView.setLayoutManager(manager);
        adapter = new TagAdapter(this, tag_list);
        holder.recyclerView.setAdapter(adapter);

        commonLabelArea.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        toolArea.addView(commonLabelArea);
        commonLabelArea.setVisibility(View.GONE);
    }

    private TagAdapter adapter;
    private List<TagItem> tag_list = new ArrayList<>();
    public class TagHolder{
        public RecyclerView recyclerView;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 100:
                    break;
                case 101:
                    adapter.refresh(tag_list);
                    break;
            }
        }
    };

    private void intiLebel() {
        User user = UserHelp.getInstance().getCurrentUser(this);
        ApiClient.create()
                .withService("Tag.GetTagLibrary")
                .addParams("uD", user.getUid())
                .addParams("3917e150bbaa953b", user.getSessionToken())
                .request(new ApiCallback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        handler.sendEmptyMessage(100);
                    }

                    @Override
                    public void onResponse(ApiClientResponse apiClientResponse) throws IOException {
                        if (apiClientResponse.getRet() == 200) {
                            try {
                                JSONObject js = new JSONObject(apiClientResponse.getData());
                                LogUtils.i("tag", apiClientResponse.getData());
                                String code = js.getString("code");
                                if (code.equals("0")) {
                                    String list = js.getString("list");
                                    Gson gson = new Gson();
                                    tag_list = gson.fromJson(list, new TypeToken<List<TagItem>>() {
                                    }.getType());
                                    handler.sendEmptyMessage(101);
                                } else {
                                    handler.sendEmptyMessage(100);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            handler.sendEmptyMessage(100);
                        }
                    }
                });
    }

    private void initEvent() {
        stickerBtn.setOnClickListener(v -> {
            if (!setCurrentBtn(stickerBtn)) {
                return;
            }
            bottomToolBar.setVisibility(View.VISIBLE);
            labelSelector.hide();
            emptyLabelView.setVisibility(View.GONE);
            commonLabelArea.setVisibility(View.GONE);
            initStickerToolBar();
        });

        filterBtn.setOnClickListener(v -> {
            if (!setCurrentBtn(filterBtn)) {
                return;
            }
            bottomToolBar.setVisibility(View.VISIBLE);
            labelSelector.hide();
            emptyLabelView.setVisibility(View.INVISIBLE);
            commonLabelArea.setVisibility(View.GONE);
            initFilterToolBar();
        });
        labelBtn.setOnClickListener(v -> {
            if (!setCurrentBtn(labelBtn)) {
                return;
            }
            bottomToolBar.setVisibility(View.GONE);
            labelSelector.showToTop();
            commonLabelArea.setVisibility(View.VISIBLE);

        });
        labelSelector.setTxtClicked(v -> {
            EditTextActivity.openTextEdit(PhotoProcessActivity.this, "", 8, AppConstants.ACTION_EDIT_LABEL);
        });
        labelSelector.setAddrClicked(v -> {
            EditTextActivity.openTextEdit(PhotoProcessActivity.this, "", 8, AppConstants.ACTION_EDIT_LABEL_POI);

        });
        mImageView.setOnDrawableEventListener(wpEditListener);
        mImageView.setSingleTapListener(() -> {
            emptyLabelView.updateLocation((int) mImageView.getmLastMotionScrollX(),
                    (int) mImageView.getmLastMotionScrollY());
            emptyLabelView.setVisibility(View.VISIBLE);

            labelSelector.showToTop();
            drawArea.postInvalidate();
        });
        labelSelector.setOnClickListener(v -> {
            labelSelector.hide();
            emptyLabelView.updateLocation((int) labelSelector.getmLastTouchX(),
                    (int) labelSelector.getmLastTouchY());
            emptyLabelView.setVisibility(View.VISIBLE);
        });


        titleBar.setRightBtnOnclickListener(v -> {
            savePicture();
        });
    }

    //保存图片
    private void savePicture() {
        //加滤镜
        final Bitmap newBitmap = Bitmap.createBitmap(mImageView.getWidth(), mImageView.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas cv = new Canvas(newBitmap);
        RectF dst = new RectF(0, 0, mImageView.getWidth(), mImageView.getHeight());
        try {
            cv.drawBitmap(mGPUImageView.capture(), null, dst, null);
        } catch (InterruptedException e) {
            e.printStackTrace();
            cv.drawBitmap(currentBitmap, null, dst, null);
        }
        //加贴纸水印
        EffectUtil.applyOnSave(cv, mImageView);

        String fileName = "";
        File file = null;
        try {
            fileName = ImageUtils.saveToFile(FileUtils.getInst().getPhotoSavedPath() + "/tmp", false, newBitmap);
            file = new File(fileName);
        } catch (Exception e) {
            Toast.makeText(PhotoProcessActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
            return;
        }

        showProgressDialog("图片处理中...");
        User user = UserHelp.getInstance().getCurrentUser(this);
        ApiClient.create()
                .withService("Feeds.putImg")
                .addParams("uid",user.getUid())
                .addParams("3917e150bbaa953b", user.getSessionToken())
                .addImage(fileName,file)
                .request(new ApiCallback() {
                    @Override
                    public void onFailure(Request request, IOException e) {

                    }

                    @Override
                    public void onResponse(ApiClientResponse apiClientResponse) throws IOException {
                        dismissProgressDialog();
                        if(apiClientResponse.getRet() != 200){
                            Log.d("5v",apiClientResponse.getMsg());
                            toast("图片上传失败",3);
                            return;
                        }
                        String data = apiClientResponse.getData();
                        Map<String, Object> lm = JsonToMapList.getMap(data);
                        if(!lm.get("code").toString().equals("0")){
                            Log.d("5v",data);
                        }
                        LogUtils.i("photo", data);
                        String imageid = null;
                        String bUrl = null;
                        String url = null;
                        try {
                            JSONObject js = new JSONObject(data);
                            imageid = js.getString("imgId");
                            bUrl = js.getString("url");
                            StringBuffer sb = new StringBuffer("http://7xstnm.com2.z0.glb.clouddn.com/");
                            sb.append(bUrl);
                            url = sb.toString();
                            LogUtils.i("photo", "imgid " + imageid);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        List<TagItem> tagInfoList = new ArrayList<TagItem>();
                        for (LabelView label : labels) {
                            tagInfoList.add(label.getTagInfo());
                        }
                        LogUtils.i("photo", "tagInfoList.size " + tagInfoList.size());
                        FeedItem fi = new FeedItem(tagInfoList, Integer.valueOf(imageid), url);
                        IntentUtils.doIntentWithSerializable(PhotoProcessActivity.this, PublishActivity.class, "feeditem", fi);
                        //将图片信息通过EventBus发送到HomeFragment
//                        FeedItem feedItem = new FeedItem(tagInfoList, lm.get("url").toString());
//                        try {
//                            feedItem.setImgId(Integer.parseInt(lm.get("imgId").toString()));
//                        } catch (Exception e) {
//
//                        }

                        //EventBus.getDefault().post(feedItem);

                        // Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null, null));
//                        LogUtils.i("pre publish", JSON.toJSONString(feedItem));
//                        PublishActivity.openWithPhotoUri(PhotoProcessActivity.this, getIntent().getData(), feedItem);

                    }
                });



//        MediaType MEDIA_JPG = MediaType.parse("image/jpeg");
//
//        OkHttpClient okHttpClient = new OkHttpClient();
//        RequestBody formBody = new MultipartBuilder()
//                .addFormDataPart("uid", "1")
//                .addFormDataPart("image[]", fileName, RequestBody.create(MEDIA_JPG, file))
//                .build();
//        Request request = new Request.Builder()
//                .url("http://ngc123.sinaapp.com/index.php/Tag/uploadImg")
//                .post(formBody)
//                .build();
//        Call call = okHttpClient.newCall(request);
//        showProgressDialog("图片处理中...");
//        call.enqueue(new Callback() {
//
//            @Override
//            public void onFailure(Request request, IOException e) {
//
//            }
//
//            @Override
//            public void onResponse(Response response) throws IOException {
//                dismissProgressDialog();
//                String rawJsonResponse = response.toString();
//                Log.d("5v",rawJsonResponse);
//                if (rawJsonResponse == null || rawJsonResponse.equals("")) {
//                    Toast.makeText(PhotoProcessActivity.this, "您的网络不稳定,请检查网络！", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                Map<String, Object> lm = JsonToMapList.getMap(rawJsonResponse);
//                if (lm.get("status").toString() != null &&
//                        lm.get("status").toString().equals("ok")) {
//
//                    Map<String, Object> lmres = JsonToMapList.getMap(lm.get("result").toString());
//
//                    Log.d("5v", lm.get("result").toString());
//
//                    if (lmres.get("imgName") == null) {
//                        Toast.makeText(PhotoProcessActivity.this, "您的网络差，请稍后重试 ！", Toast.LENGTH_SHORT).show();
//                        dismissProgressDialog();
//                        return;
//                    }
////
//                    //将照片信息保存至sharedPreference
//                    //保存标签信息
//                    List<TagItem> tagInfoList = new ArrayList<TagItem>();
//                    for (LabelView label : labels) {
//                        tagInfoList.add(label.getTagInfo());
//                    }
//
//                    //将图片信息通过EventBus发送到MainActivity
//                    FeedItem feedItem = new FeedItem(tagInfoList, lmres.get("imgName").toString());
//                    try {
//                        feedItem.setImgId(Integer.parseInt(lmres.get("id").toString()));
//                    } catch (Exception e) {
//
//                    }
//
//                    //EventBus.getDefault().post(feedItem);
//
//                    // Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null, null));
//                    PublishActivity.openWithPhotoUri(
//                            PhotoProcessActivity.this,
//                            getIntent().getData(),
//                            feedItem
//                    );
//                }
//            }
//        });
    }

    public void tagClick(View v) {
        TextView textView = (TextView) v;
        TagItem tagItem = new TagItem(AppConstants.POST_TYPE_TAG, textView.getText().toString());
        String[] str = textView.getText().toString().split(":");
        tagItem.setClick(str[1]);
        addLabel(tagItem);
    }

    private MyImageViewDrawableOverlay.OnDrawableEventListener wpEditListener = new MyImageViewDrawableOverlay.OnDrawableEventListener() {
        @Override
        public void onMove(MyHighlightView view) {
        }

        @Override
        public void onFocusChange(MyHighlightView newFocus, MyHighlightView oldFocus) {
        }

        @Override
        public void onDown(MyHighlightView view) {

        }

        @Override
        public void onClick(MyHighlightView view) {
            labelSelector.hide();
        }

        @Override
        public void onClick(final LabelView label) {
            if (label.equals(emptyLabelView)) {
                return;
            }
            alert("温馨提示", "是否需要删除该标签！", "确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EffectUtil.removeLabelEditable(mImageView, drawArea, label);
                    labels.remove(label);
                }
            }, "取消", null);
        }
    };

    private boolean setCurrentBtn(TextView btn) {
        if (currentBtn == null) {
            currentBtn = btn;
        } else if (currentBtn.equals(btn)) {
            return false;
        } else {
            currentBtn.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
        Drawable myImage = getResources().getDrawable(R.drawable.select_icon);
        btn.setCompoundDrawablesWithIntrinsicBounds(null, null, null, myImage);
        currentBtn = btn;
        return true;
    }


    //初始化贴图
    private void initStickerToolBar() {

        bottomToolBar.setAdapter(new StickerToolAdapter(PhotoProcessActivity.this, EffectUtil.addonList));
        bottomToolBar.setOnItemClickListener(new it.sephiroth.android.library.widget.AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(it.sephiroth.android.library.widget.AdapterView<?> arg0,
                                    View arg1, int arg2, long arg3) {
                labelSelector.hide();
                Addon sticker = EffectUtil.addonList.get(arg2);
                EffectUtil.addStickerImage(mImageView, PhotoProcessActivity.this, sticker,
                        new EffectUtil.StickerCallback() {
                            @Override
                            public void onRemoveSticker(Addon sticker) {
                                labelSelector.hide();
                            }
                        });
            }
        });
        setCurrentBtn(stickerBtn);
    }


    //初始化滤镜
    private void initFilterToolBar() {
        final List<FilterEffect> filters = EffectService.getInst().getLocalFilters();
        final FilterAdapter adapter = new FilterAdapter(PhotoProcessActivity.this, filters, smallImageBackgroud);
        bottomToolBar.setAdapter(adapter);
        bottomToolBar.setOnItemClickListener(new it.sephiroth.android.library.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(it.sephiroth.android.library.widget.AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                labelSelector.hide();
                if (adapter.getSelectFilter() != arg2) {
                    adapter.setSelectFilter(arg2);
                    GPUImageFilter filter = GPUImageFilterTools.createFilterForType(
                            PhotoProcessActivity.this, filters.get(arg2).getType());
                    mGPUImageView.setFilter(filter);
                    GPUImageFilterTools.FilterAdjuster mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(filter);
                    //可调节颜色的滤镜
                    if (mFilterAdjuster.canAdjust()) {
                        //mFilterAdjuster.adjust(100); 给可调节的滤镜选一个合适的值
                    }
                }
            }
        });
    }
    private LabelView label;
    int left;
    int top;
    //添加标签
    private void addLabel(TagItem tagItem) {
        labelSelector.hide();
        emptyLabelView.setVisibility(View.INVISIBLE);
        if (labels.size() >= 5) {
            alert("温馨提示", "您只能添加5个标签！", "确定", null, null, null, true);
        } else {
            left = emptyLabelView.getLeft();
            top = emptyLabelView.getTop();
            if (labels.size() == 0 && left == 0 && top == 0) {
                left = mImageView.getWidth() / 2 - 10;
                top = mImageView.getWidth() / 2;
            }
            label = new LabelView(PhotoProcessActivity.this);
            label.init(tagItem);
            EffectUtil.addLabelEditable(mImageView, drawArea, label, left, top);
            labels.add(label);
        }
    }


    public void onEventMainThread(AddLabel label){
        addLabel(label.getTag());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        labelSelector.hide();
        super.onActivityResult(requestCode, resultCode, data);
        if (AppConstants.ACTION_EDIT_LABEL == requestCode && data != null) {
            String text = data.getStringExtra(AppConstants.PARAM_EDIT_TEXT);
            if (StringUtils.isNotEmpty(text)) {
                TagItem tagItem = new TagItem(AppConstants.POST_TYPE_TAG, text);
                tagItem.setStyle("normal");
                addLabel(tagItem);
            }
        } else if (AppConstants.ACTION_EDIT_LABEL_POI == requestCode && data != null) {
            String text = data.getStringExtra(AppConstants.PARAM_EDIT_TEXT);
            if (StringUtils.isNotEmpty(text)) {
                TagItem tagItem = new TagItem(AppConstants.POST_TYPE_POI, text);
                tagItem.setStyle("normal");
                addLabel(tagItem);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
