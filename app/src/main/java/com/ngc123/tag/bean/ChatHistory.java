package com.ngc123.tag.bean;

/*
* Class name :ChatHistory
*
* Version information :
*
* Describe ：
*
* Author ：裴徐泽
*
* Created by pei on 2016-7-16.
*
*/
public class ChatHistory {
    private String head;
    private String name;
    private String lastMessage;
    public ChatHistory(){
    }
    public ChatHistory(String h, String n, String m){
        head = h;
        name = n;
        lastMessage = m;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getHead() {
        return head;
    }

    public String getName() {
        return name;
    }

    public String getLastMessage() {
        return lastMessage;
    }
    public String toString(){
        return "ChatHistory::" + "head" + head + "name" + name + " lastMessage" + lastMessage;
    }
}
