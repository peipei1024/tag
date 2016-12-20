package com.ngc123.tag.fragment.find;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/*
* Class name :SpaceItemDecoration
*
* Version information :
*
* Describe ：
*
* Author ：裴徐泽---废弃
*
* Created by pei on 2016-8-1.
*
*/
public class SpaceItemDecoration extends RecyclerView.ItemDecoration{

    private int space;

    public SpaceItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        outRect.left = space;
        outRect.right = space;
        outRect.bottom = space;

        // Add top margin only for the first item to avoid double space between items
        outRect.top = space;

    }
}
