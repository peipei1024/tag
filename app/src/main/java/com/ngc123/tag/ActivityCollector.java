package com.ngc123.tag;/*
* Class name :ActivityCollector
*
* Version information :
*
* Describe ：
*
* Author ：裴徐泽
*
* Created by pei on 2016/3/17.
*
*/

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

public class ActivityCollector {
    public static List<Activity> list = new ArrayList<>();
    public static void addActivity(Activity activity){
        list.add(activity);
    }
    public static void removeActivity(Activity activity){
        list.remove(activity);
        activity.finish();
    }
    public static void finishAll(){
        for (Activity activity : list){
            if (!activity.isFinishing()){
                activity.finish();
            }
        }
    }
}
