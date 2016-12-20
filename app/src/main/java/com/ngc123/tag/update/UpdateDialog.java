package com.ngc123.tag.update;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

import com.ngc123.tag.App;

/**
 * Created by H_z on 2016/6/16.
 * 有新版本弹出该Dialog
 */
public class UpdateDialog {
    public static void showUpdateDialog(final Context context, final String AppName, final String url) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("检测到新版本");
        builder.setMessage("是否下载更新?");
        AlertDialog.Builder builder1 = builder.setPositiveButton("下载", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(App.getApp(), UpdateService.class);
                intent.putExtra("Key_App_Name", AppName);
                intent.putExtra("key_Down_URL", url);
                context.startService(intent);
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

}