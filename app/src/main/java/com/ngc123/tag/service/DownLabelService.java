package com.ngc123.tag.service;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;

import com.avos.avoscloud.okhttp.Request;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ngc123.tag.api.ApiCallback;
import com.ngc123.tag.api.ApiClient;
import com.ngc123.tag.api.ApiClientResponse;
import com.ngc123.tag.model.TagItem;
import com.ngc123.tag.util.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/*
* Class name :DownLabelService
*
* Version information :
*
* Describe ：
*
* Author ：裴徐泽
*
* Created by pei on 2016-8-7.
*
*/
public class DownLabelService extends Service{

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getLael();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    List<TagItem> list1 = new ArrayList<>();
    private void getLael(){
        ApiClient.create()
                .withService("Tag.GetAllTagLibrary")
                .addParams("1", "1")
                .request(new ApiCallback() {
                    @Override
                    public void onFailure(Request request, IOException e) {

                    }

                    @Override
                    public void onResponse(ApiClientResponse apiClientResponse) throws IOException {
                        if (apiClientResponse.getRet() == 200){
                            try {
                                JSONObject js = new JSONObject(apiClientResponse.getData());
                                String code = js.getString("code");
                                if (code.equals("0")){
                                    String list = js.getString("list");
                                    Gson gson = new Gson();
                                    list1 = gson.fromJson(list, new TypeToken<List<TagItem>>(){}.getType());
                                    list(list1);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    private void list(List<TagItem> list){
        for (int a = 0; a < list.size(); a++){
            try {
                StringBuffer sb = new StringBuffer("http://7xstnm.com2.z0.glb.clouddn.com/tag/");
                sb.append(list.get(a).getStyle());
                sb.append("_left.png");
                String leftpic = sb.toString();
                LogUtils.i("label", leftpic );
                String filename = downloadFile(leftpic);

                StringBuffer sb1 = new StringBuffer("http://7xstnm.com2.z0.glb.clouddn.com/tag/");
                sb1.append(list.get(a).getStyle());
                sb1.append("_right.png");
                String rightpic = sb1.toString();
                String filename1 = downloadFile(rightpic);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String downloadFile(String urlString) throws Exception{
        InputStream inputStream;
        OutputStream outputStream;
        URL url = new URL(urlString);
        String filename=urlString.substring(urlString.lastIndexOf("/") + 1);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setConnectTimeout(10*1000);
        httpURLConnection.setReadTimeout(10*1000);

        int totalSize = httpURLConnection.getContentLength();
        LogUtils.i("label", httpURLConnection.getResponseCode() + " ");
        if (httpURLConnection.getResponseCode() == 404) {
            throw new Exception("fail!");
            //这个地方应该加一个下载失败的处理，但是，因为我们在外面加了一个try---catch，已经处理了Exception,
            //所以不用处理
        }

        File file0 = new File("/sdcard/tag");
        if (!file0.exists()){
            file0.mkdir();
        }

        File file = new File(Environment.getExternalStorageDirectory()+ "/tag/"+filename);

        inputStream = httpURLConnection.getInputStream();
        outputStream = new FileOutputStream(file, false);// 文件存在则覆盖掉

        byte buffer[] = new byte[1024];
        int readsize = 0;

        while ((readsize = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, readsize);
        }

        inputStream.close();
        outputStream.close();

        return file.toString();
    }


}
