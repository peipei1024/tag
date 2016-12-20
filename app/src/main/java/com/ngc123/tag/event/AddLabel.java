package com.ngc123.tag.event;

import com.ngc123.tag.model.TagItem;
import com.ngc123.tag.ui.SettingActivity;

/*
* Class name :AddLabel
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
public class AddLabel {
    private TagItem tag;
    public AddLabel(TagItem tag){
        this.tag = tag;
    }

    public TagItem getTag() {
        return tag;
    }
}
