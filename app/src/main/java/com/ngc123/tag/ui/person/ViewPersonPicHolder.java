package com.ngc123.tag.ui.person;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.ngc123.tag.R;
import com.ngc123.tag.model.FeedItem;
import com.ngc123.tag.util.LogUtils;
import com.ngc123.tag.view.MyGridView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ViewPersonPicHolder extends RecyclerView.ViewHolder {


    @Bind(R.id.id_grid)
    MyGridView idGrid;

    private List<FeedItem> list = new ArrayList<>();
    private Context context;
    private PersonImageAdapter adapter;

    public ViewPersonPicHolder(LayoutInflater inflater, ViewGroup parent) {
        this(inflater.inflate(R.layout.view_person_pic, parent, false));
    }

    public void bindData(Context context, List<FeedItem> list){
        this.list = list;
        this.context = context;
        idGrid.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == this.SCROLL_STATE_IDLE){
//                if (scrollState == this.SCROLL_STATE_IDLE  && view.getLastVisiblePosition() == (view.getCount() - 1)){
                    LogUtils.i("personholder", "到底了");
                    LogUtils.i("personholder", "view.getLastVisiblePosition()" + view.getLastVisiblePosition());
                    LogUtils.i("personholder", "view.getCount()" + view.getCount());
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        if (adapter == null){
            adapter = new PersonImageAdapter(context, list);
            idGrid.setAdapter(adapter);
        }else {
            adapter.refreshAdapter(list);
        }
    }
    public ViewPersonPicHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }


}
