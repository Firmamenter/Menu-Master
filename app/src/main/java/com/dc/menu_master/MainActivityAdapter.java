package com.dc.menu_master;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;
import com.dc.menu_master.Food.Food;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Adapter for MainActivity.
 */

public class MainActivityAdapter extends BaseAdapter {
    private ArrayList<Food> listData;
    private LayoutInflater layoutInflator;
    private Context context;

    public MainActivityAdapter(Context context, ArrayList<Food> listData) {
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
            convertView = layoutInflator.inflate(R.layout.food_item, null);
            holder = new ViewHolder();
            holder.titleView = (TextView) convertView.findViewById(R.id.title);
            holder.imageView = (ImageView) convertView.findViewById(R.id.thumbImage);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Food foodItem = (Food) listData.get(position);
        holder.titleView.setText(foodItem.getName());
        Typeface mTypeFace = Typeface.createFromAsset(context.getAssets(), "fonts/alegreya-regular.ttf");
        holder.titleView.setTypeface(mTypeFace);
        if (holder.imageView != null) {
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
