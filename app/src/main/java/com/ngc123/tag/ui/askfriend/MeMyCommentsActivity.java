package com.ngc123.tag.ui.askfriend;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ngc123.tag.R;
import com.ngc123.tag.util.ToastUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by JiaM on 2016/3/20.
 */
public class MeMyCommentsActivity extends Activity {

    @Bind(R.id.id_iv_back_arrow)
    ImageView idIvBackArrow;
    @Bind(R.id.id_recycle)
    RecyclerView idRecycle;
    @Bind(R.id.id_tv_no_contact)
    TextView idTvNoContact;
    private List<PhoneContact> list = new ArrayList<>();
    private PhoneContactAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.me_comments);
        ButterKnife.bind(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 1000);

        }else {
            getContacts();
        }


        adapter = new PhoneContactAdapter(this, list);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        idRecycle.setLayoutManager(manager);
        idRecycle.setAdapter(adapter);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        doNext(requestCode,grantResults);
    }

    private void doNext(int requestCode, int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               getContacts();
            } else {
                ToastUtils.toast(this, "获取权限失败");
                // Permission Denied
            }
        }
    }

    @OnClick(R.id.id_iv_back_arrow)
    public void onClick() {
        finish();
    }


    private void getContacts(){
        //①查询raw_contacts表获得联系人的id
        ContentResolver resolver = getContentResolver();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        //查询联系人数据
        Cursor cursor = resolver.query(uri, null, null, null, null);
        PhoneContact phone = null;
        while(cursor.moveToNext())
        {
            //获取联系人姓名,手机号码
            String cName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String cNum = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            System.out.println("姓名:" + cName);
            System.out.println("号码:" + cNum);
            System.out.println("======================");
            phone = new PhoneContact();
            phone.setName(cName);
            phone.setPhone(cNum);
            list.add(phone);
        }
        cursor.close();
        handler.sendEmptyMessage(1);
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    if (list.size() == 0){
                        idTvNoContact.setVisibility(View.VISIBLE);
                    }else {
                        adapter.refresh(list);
                        idTvNoContact.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    };
}
