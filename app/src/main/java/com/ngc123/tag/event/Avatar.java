package com.ngc123.tag.event;

/*
* Class name :Avatar
*
* Version information :
*
* Describe ：
*
* Author ：裴徐泽
*
* Created by pei on 2016-7-30.
*
*/
public class Avatar {
    private String url;
    private int code;

    public Avatar(String url, int code){
        this.url = url;
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public String getUrl() {
        return url;
    }
}
