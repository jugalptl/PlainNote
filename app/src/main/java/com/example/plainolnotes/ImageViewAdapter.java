package com.example.plainolnotes;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by iGroup on 5/3/2017.
 */
public class ImageViewAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<Bitmap> mphoto;
    public ImageViewAdapter(Context editorActivity, ArrayList<Bitmap> photo) {
        this.mContext = editorActivity;
        this.mphoto = photo;
    }

    @Override
    public int getCount() {
        return mphoto.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null)
        {
            LayoutInflater layoutInflat = ((Activity)mContext).getLayoutInflater();
            holder = new ViewHolder();
            convertView = layoutInflat.inflate(R.layout.grid_item_layout,parent,false);
            holder.image = (ImageView)convertView.findViewById(R.id.grid_item_image);
            holder.geo_text = (TextView)convertView.findViewById(R.id.grid_item_title);
            convertView.setTag(holder);

        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        holder.geo_text.setText("Geo_Location");
        holder.image.setImageBitmap(mphoto.get(position));
        return convertView;
    }
    static class ViewHolder{
        ImageView image;
        TextView geo_text;
    }
}
