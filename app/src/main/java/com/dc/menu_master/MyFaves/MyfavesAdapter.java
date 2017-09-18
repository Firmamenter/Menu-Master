package com.dc.menu_master.MyFaves;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;
import com.dc.menu_master.Food.Food;
import com.dc.menu_master.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Adapter for MyFaves.
 */

public class MyfavesAdapter extends BaseAdapter {
    private ArrayList<Food> listData;
    private LayoutInflater layoutInflator;
    private Context context;

    public MyfavesAdapter(Context context, ArrayList<Food> listData) {
        this.listData = listData;
        this.context = context;
        layoutInflator = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflator.inflate(R.layout.myfaves_listview_layout, null);
            holder = new ViewHolder();
            holder.titleView = (TextView) convertView.findViewById(R.id.myfaves_title);
            holder.imageView = (ImageView) convertView.findViewById(R.id.myfaves_photo);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Food foodItem = (Food) listData.get(position);
        holder.titleView.setText(foodItem.getName());
        Typeface mTypeFace = Typeface.createFromAsset(context.getAssets(), "fonts/alegreya-regular.ttf");
        holder.titleView.setTypeface(mTypeFace);
        if (holder.imageView != null) {
            //new ImageDownloaderTask(holder.imageView).execute(foodItem.getImageUrl());
            Picasso.with(context)
                    .load(foodItem.getImageUrl())
                    .placeholder(R.drawable.ic_block_black)
                    .error(R.drawable.ic_error_outline)
                    .into(holder.imageView);
        }
        return convertView;
    }

    public static class ViewHolder {
        TextView titleView;
        ImageView imageView;
    }
}

