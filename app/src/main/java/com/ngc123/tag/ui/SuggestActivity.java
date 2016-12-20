package com.ngc123.tag.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.ngc123.tag.R;
import com.ngc123.tag.util.ToastUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
* Class name :SuggestActivity
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
public class SuggestActivity extends Activity {
    @Bind(R.id.id_iv_back_arrow)
    ImageView idIvBackArrow;
    @Bind(R.id.id_et_suggest)
    EditText idEtSuggest;
    @Bind(R.id.id_btn_suggest)
    Button idBtnSuggest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggest);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.id_iv_back_arrow, R.id.id_btn_suggest})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_iv_back_arrow:
                finish();
                break;
            case R.id.id_btn_suggest:
                idEtSuggest.setText("");
                ToastUtils.toast(this, "提交成功");
                break;
        }
    }
}
