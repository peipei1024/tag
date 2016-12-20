package com.ngc123.tag.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.ngc123.tag.R;
import com.ngc123.tag.util.AppUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
* Class name :AboutActivity
*
* Version information :
*
* Describe ：
*
* Author ：裴徐泽
*
* Created by pei on 2016-8-5.
*
*/
public class AboutActivity extends Activity {
    @Bind(R.id.id_iv_back_arrow)
    ImageView idIvBackArrow;
    @Bind(R.id.id_tv_version)
    TextView idTvVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        idTvVersion.setText(" 版本号  " + AppUtils.getVersionName(this));
    }

    @OnClick(R.id.id_iv_back_arrow)
    public void onClick() {
        finish();
    }
}
