package com.ngc123.tag.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.okhttp.Request;
import com.google.gson.Gson;
import com.leancloud.im.tag.Chat_AVImClientManager;
import com.leancloud.im.tag.activity.Chat_AVBaseActivity;
import com.ngc123.tag.api.ApiCallback;
import com.ngc123.tag.api.ApiClient;
import com.ngc123.tag.api.ApiClientResponse;
import com.ngc123.tag.bean.User;
import com.ngc123.tag.R;
import com.ngc123.tag.util.CommonUtils;
import com.ngc123.tag.util.LogUtils;
import com.ngc123.tag.util.SharedPreferencesUtils;
import com.ngc123.tag.util.ToastUtils;
import com.ngc123.tag.view.FormView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**废弃
 * Created by Apple on 16/4/20.
 */
public class LoginActivity extends Chat_AVBaseActivity implements View.OnClickListener {
    private EditText usernameEditText;
    private EditText passwordEditText;

    public static final String VIDEO_NAME = "welcome_video.mp4";

    private VideoView mVideoView;

    private InputTypeLogin inputTypeLogin = InputTypeLogin.NONE;

    private Button buttonLeft, buttonRight;

    private FormView formView;

    private ViewGroup contianer;

    private TextView appName;

    private boolean progressShow;
    private String name = null;
    private String access_token = null;
    private String ßΩ = null;
    private String birthday = null;
    private String headurl = null;
    private String province = null;
    private String city = null;
    private String meida_type = null;
    private String meida_uid = null;
    private static String uid = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_u);

        findView();

        initView();

        File videoFile = getFileStreamPath(VIDEO_NAME);
        if (!videoFile.exists()) {
            videoFile = copyVideoFile();
        }

        playVideo(videoFile);

        playAnim();

        usernameEditText = (EditText) findViewById(R.id.username);
        usernameEditText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);//设置限制邮箱格式
        passwordEditText = (EditText) findViewById(R.id.password);

        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordEditText.setText(null);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    private void findView() {
        mVideoView = (VideoView) findViewById(R.id.videoView);
        buttonLeft = (Button) findViewById(R.id.buttonLeft);
        buttonRight = (Button) findViewById(R.id.buttonRight);
        contianer = (ViewGroup) findViewById(R.id.container);
        formView = (FormView) findViewById(R.id.formView);
        appName = (TextView) findViewById(R.id.appName);
        formView.post(new Runnable() {
            @Override
            public void run() {
                int delta = formView.getTop() + formView.getHeight();
                formView.setTranslationY(-1 * delta);
            }
        });
    }

    private void initView() {

        buttonRight.setOnClickListener(this);
        buttonLeft.setOnClickListener(this);
    }

    private void playVideo(File videoFile) {
        mVideoView.setVideoPath(videoFile.getPath());
        mVideoView.setLayoutParams(new RelativeLayout.LayoutParams(-1, -1));
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
            }
        });
    }

    private void playAnim() {
        ObjectAnimator anim = ObjectAnimator.ofFloat(appName, "alpha", 0,1);
        anim.setDuration(4000);
        anim.setRepeatCount(1);
        anim.setRepeatMode(ObjectAnimator.REVERSE);
        anim.start();
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                appName.setVisibility(View.INVISIBLE);
            }
        });
    }

    @NonNull
    private File copyVideoFile() {
        File videoFile;
        try {
            FileOutputStream fos = openFileOutput(VIDEO_NAME, MODE_PRIVATE);
            InputStream in = getResources().openRawResource(R.raw.welcome_video);
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = in.read(buff)) != -1) {
                fos.write(buff, 0, len);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        videoFile = getFileStreamPath(VIDEO_NAME);
        if (!videoFile.exists())
            throw new RuntimeException("video file has problem, are you sure you have welcome_video.mp4 in res/raw folder?");
        return videoFile;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoView.stopPlayback();
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 100){

            }else {
                ToastUtils.toast(LoginActivity.this, "登录失败");
            }
        }
    };
    public void login(){
        if (!CommonUtils.isNetWorkConnected(this)) {
            Toast.makeText(this, R.string.network_isnot_available, Toast.LENGTH_SHORT).show();
            return;
        }
        final String phone = usernameEditText.getText().toString();
        final String password = passwordEditText.getText().toString();

        if(!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(password)){
            progressShow = true;
            final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
            pd.setCanceledOnTouchOutside(false);
            pd.setOnCancelListener(new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    progressShow = false;
                }
            });
            pd.setMessage("正在登陆...");
            pd.show();
            Message message = new Message();
            message.what = 101;
            ApiClient.create()
                    .withService("User.Login")
                    .addParams("pN", phone)
                    .addParams("pW", password)
                    .request(new ApiCallback() {
                        @Override
                        public void onFailure(Request request, IOException e) {
                            Log.i("info", "info");
                        }

                        @Override
                        public void onResponse(ApiClientResponse apiClientResponse) throws IOException {
                            if (apiClientResponse.getRet() != 200){
                                Log.i("login", "failed");
                                handler.sendMessage(message);
                                return;
                            }
                            Log.i("login", "success" + apiClientResponse.getData());
                            try {
                                JSONObject js = new JSONObject(apiClientResponse.getData());
                                String data = js.getString("userinfo");
                                Log.i("login", "success" + data);
                                Gson gson = new Gson();
                                User user = gson.fromJson(data, User.class);
                                Log.i("login", "success" + user.getUsername());
                                if (!TextUtils.isEmpty(user.getUsername())){
                                    Chat_AVImClientManager.getInstance().open(user.getUsername(), new AVIMClientCallback() {
                                        @Override
                                        public void done(AVIMClient avimClient, AVIMException e) {
                                            if (e == null){
                                                Map<String, String> map = new HashMap<String, String>();
                                                map.put("user", data);
                                                SharedPreferencesUtils.writeSharedPreferences(LoginActivity.this, "user", map);
                                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                LogUtils.i("login", "login success and tcp success");
                                                finish();
                                            }else {
                                                handler.sendMessage(message);
                                            }
                                        }
                                    });
                                }else {
                                    handler.sendMessage(message);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
            pd.dismiss();
        }
    }

    protected boolean filterException(Exception e) {
        if (e != null) {
            e.printStackTrace();
            ToastUtils.toast(this, e.getMessage());
            return false;
        } else {
            return true;
        }
    }

    public void register(){
        startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
    }

    @Override
    public void onClick(View view) {
        int delta = formView.getTop()+formView.getHeight();
        switch (inputTypeLogin) {
            case NONE:

                formView.animate().translationY(0).alpha(1).setDuration(500).start();
                if (view == buttonLeft) {
                    inputTypeLogin = InputTypeLogin.LOGIN;
                    buttonLeft.setText(R.string.button_confirm_login);
                    buttonRight.setText(R.string.button_cancel_login);
                } else if (view == buttonRight) {
                    register();
                }

                break;
            case LOGIN:

                formView.animate().translationY(-1 * delta).alpha(0).setDuration(500).start();
                if (view == buttonLeft) {
                    login();

                } else if (view == buttonRight) {

                }
                inputTypeLogin = InputTypeLogin.NONE;
                buttonLeft.setText(R.string.button_login);
                buttonRight.setText(R.string.button_signup);
                break;
            case SIGN_UP:

                formView.animate().translationY(-1 * delta).alpha(0).setDuration(500).start();
                if (view == buttonLeft) {

                } else if (view == buttonRight) {

                }
                inputTypeLogin = InputTypeLogin.NONE;
                buttonLeft.setText(R.string.button_login);
                buttonRight.setText(R.string.button_signup);
                break;
        }
    }

    enum InputTypeLogin {
        NONE, LOGIN, SIGN_UP;
    }
}
