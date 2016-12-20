package com.ngc123.tag.ui.notificat;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ngc123.tag.R;
import com.ngc123.tag.bean.NotificatBean;
import com.ngc123.tag.db.DBOpenHelper;
import com.ngc123.tag.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
* Class name :LikeActivity
*
* Version information :
*
* Describe ：
*
* Author ：裴徐泽
*
* Created by pei on 2016-8-6.
*
*/
public class LikeActivity extends Activity{
    @Bind(R.id.id_tv_title)
    TextView idTvTitle;
    @Bind(R.id.id_iv_back_arrow)
    ImageView idIvBackArrow;
    @Bind(R.id.id_recycle)
    RecyclerView idRecycle;
    private NotificatAdapter adapter;
    private List<NotificatBean> list = new ArrayList<>();
    private DBOpenHelper helper;
    private SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);
        ButterKnife.bind(this);
        helper = new DBOpenHelper(this, "follow.db", null, 1);
        db = helper.getReadableDatabase();
        adapter = new NotificatAdapter(this, list);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        idRecycle.setLayoutManager(manager);
        idRecycle.setAdapter(adapter);
        getData();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    adapter.refresh(list);
                    break;
                case 2:
                    ToastUtils.toast(LikeActivity.this, "没有数据了");
                    break;
            }
        }
    };

    private void getData(){
        //参数依次是:表名，列名，where约束条件，where中占位符提供具体的值，指定group by的列，进一步约束
        //指定查询结果的排序方式
        Cursor cursor = db.query("like", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                int pid = cursor.getInt(cursor.getColumnIndex("id"));
                String name = cursor.getString(cursor.getColumnIndex("username"));
                String avatar = cursor.getString(cursor.getColumnIndex("avatar"));
                String time = cursor.getString(cursor.getColumnIndex("time"));
                String content = cursor.getString(cursor.getColumnIndex("content"));
                NotificatBean bean = new NotificatBean();
                bean.setUsername(name);
                bean.setAvatar(avatar);
                bean.setTime(time);
                bean.setContent(content);
                list.add(bean);
            } while (cursor.moveToNext());
        }
        cursor.close();
        if (list.size() == 0){
            handler.sendEmptyMessage(2);
        }else {
            handler.sendEmptyMessage(1);
        }
    }

    @OnClick(R.id.id_iv_back_arrow)
    public void onClick() {
        finish();
    }
}
