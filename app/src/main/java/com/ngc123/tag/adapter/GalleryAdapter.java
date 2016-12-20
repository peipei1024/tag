package com.ngc123.tag.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import com.facebook.drawee.view.SimpleDraweeView;
import com.ngc123.tag.R;
import com.ngc123.tag.model.PhotoItem;
import com.ngc123.tag.util.DistanceUtil;
import com.ngc123.tag.util.ImageLoaderUtils;

import java.util.List;

/**
 * @author tongqian.ni
 *
 */
public class GalleryAdapter extends BaseAdapter {

    private Context mContext;
    private List<PhotoItem> values;
    public static GalleryHolder holder;

    /**
     * @param context
     * @param values
     */
    public GalleryAdapter(Context context, List<PhotoItem> values) {
        this.mContext = context;
        this.values = values;
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public Object getItem(int position) {
        return values.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final GalleryHolder holder;
        int width = DistanceUtil.getCameraAlbumWidth();
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.item_gallery, null);
            holder = new GalleryHolder();
            holder.sample = (SimpleDraweeView) convertView.findViewById(R.id.gallery_sample_image);
            holder.sample.setLayoutParams(new AbsListView.LayoutParams(width, width));
            convertView.setTag(holder);
        } else {
            holder = (GalleryHolder) convertView.getTag();
        }
        final PhotoItem gallery = (PhotoItem) getItem(position);

        ImageLoaderUtils.displayLocalThumb(gallery.getImageUri(), holder.sample,width,width);
        
        return convertView;
    }

    class GalleryHolder {
        SimpleDraweeView sample;
    }

}
