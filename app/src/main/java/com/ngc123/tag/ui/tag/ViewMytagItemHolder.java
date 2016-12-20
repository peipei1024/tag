package com.ngc123.tag.ui.tag;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ngc123.tag.R;
import com.ngc123.tag.event.AddLabel;
import com.ngc123.tag.model.TagItem;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class ViewMytagItemHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.id_tv_label)
    TextView idTvLabel;
    private Context context;
    private TagItem tag;

    public ViewMytagItemHolder(LayoutInflater inflater, ViewGroup parent) {
        this(inflater.inflate(R.layout.view_mytag_item, parent, false));
    }

    public ViewMytagItemHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    @OnClick(R.id.id_tv_label)
    public void clink(){
        EventBus.getDefault().post(new AddLabel(tag));
    }

    public void bindData(TagItem tag, Context context){
        idTvLabel.setText(tag.getName());
        this.context = context;
        this.tag = tag;
    }

    public TextView getIdTvLabel() {
        return idTvLabel;
    }
}
