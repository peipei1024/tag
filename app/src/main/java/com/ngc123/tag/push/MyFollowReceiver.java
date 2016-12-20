package com.ngc123.tag.push;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;

import com.avos.avoscloud.AVPush;
import com.google.gson.Gson;
import com.ngc123.tag.R;
import com.ngc123.tag.bean.NotificatBean;
import com.ngc123.tag.db.DBOpenHelper;
import com.ngc123.tag.ui.Login1Activity;
import com.ngc123.tag.ui.SettingActivity;
import com.ngc123.tag.ui.notificat.FollowActivity;
import com.ngc123.tag.util.ImageU;
import com.ngc123.tag.util.ImageUtils;
import com.ngc123.tag.util.IntentUtils;
import com.ngc123.tag.util.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

/*
* Class name :MyFollowReceiver
*
* Version information :
*
* Describe ：
*
* Author ：裴徐泽
*
* Created by pei on 2016-8-4.
*
*/
public class MyFollowReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
//        String channel = intent.getExtras().getString("com.avos.avoscloud.Channel");
        String str = intent.getStringExtra("name");
        LogUtils.i("MyFollowReceiver", str +  action);
        if (action.equals("com.ngc.tag.follow")){
            Intent it = new Intent(context, FollowActivity.class);
            PendingIntent pit = PendingIntent.getActivity(context, 0, it, 0);
            Bitmap bitmap = ImageU.drawableToBitmap(context.getResources().getDrawable(R.mipmap.ic_launcher));
            String data = intent.getExtras().getString("com.avos.avoscloud.Data");
            try {
                JSONObject js = new JSONObject(data);
                String alert = js.getString("alert");
                String info = js.getString("info");
                Gson gson = new Gson();
                NotificatBean bean = gson.fromJson(info, NotificatBean.class);
                DBOpenHelper helper = new DBOpenHelper(context, "follow.db", null, 1);
                SQLiteDatabase db = helper.getWritableDatabase();
                ContentValues contentValues = new ContentValues();
                contentValues.put("username", bean.getUsername());
                contentValues.put("avatar", bean.getAvatar());
                contentValues.put("time", bean.getTime());
                contentValues.put("content", "关注了你");
                db.insert("follow", null, contentValues);
                //设置图片,通知标题,发送时间,提示方式等属性
                Notification.Builder mBuilder = new Notification.Builder(context);
                mBuilder.setContentTitle("TAG")                        //标题
                        .setContentText(alert)      //内容
                        .setSubText(bean.getTime())                    //内容下面的一小段文字
                        .setTicker("")             //收到信息后状态栏显示的文字信息
                        .setWhen(System.currentTimeMillis())           //设置通知时间
                    .setSmallIcon(R.mipmap.ic_launcher)            //设置小图标
                        .setLargeIcon(bitmap)                     //设置大图标
//                    .setSound(Uri.parse("android.resource://com.ngc123.tag"  + "/" + R.raw.danhuang))
                        .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)    //设置默认的三色灯与振动器
                        .setAutoCancel(true)                           //设置点击后取消Notification
                        .setContentIntent(pit);                        //设置PendingIntent
                Notification notify1 = mBuilder.build();
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                manager.notify(1, notify1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            LogUtils.i("mycustom", data + " ");

        }
        //获取消息内容
//        String str = intent.getExtras().getString("com.avos.avoscloud.Data");
//        Intent intent1 = new Intent(context, SettingActivity.class);
//        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(intent1);
//        LogUtils.i("MyFollowReceiver", str +  action);

    }
}
