package com.ngc123.tag.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ngc123.tag.R;

/**
 * Created by JiaM on 2016/3/20.
 */
public class MeMyPhotoActivity extends Activity implements View.OnClickListener {
    private ImageView title_back;
    private TextView title_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.me_myphoto);
        init();
        operate();
    }

    private void init() {
        title_back = (ImageView) findViewById(R.id.title_back);
        title_name = (TextView) findViewById(R.id.title_name);
    }

    private void operate() {
        title_back.setVisibility(View.VISIBLE);
        title_name.setText("我的相册");
        title_back.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
        }
    }
}
