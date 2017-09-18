package com.dc.menu_master.History;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.dc.menu_master.R;

import java.util.ArrayList;

import java.util.HashMap;

/**
 * Adapter for listview in history view.
 */

public class HistoryAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> list;
    private LayoutInflater inflater = null;

    public HistoryAdapter(Context context, ArrayList<String> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // Every time listview shows a line of data, this method will be executed once.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        com.dc.menu_master.History.HistoryAdapter.ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.history_listview_layout, null);
            holder = new ViewHolder();
            holder.tv = (TextView) convertView.findViewById(R.id.history_item);
            // Set tag for view.
            convertView.setTag(holder);
        } else {
            // Get holder.
            // The Object stored in this view as a tag.
            holder = (ViewHolder) convertView.getTag();
        }
        // Set up textView.
        holder.tv.setText(list.get(position));
        Typeface mTypeFace = Typeface.createFromAsset(context.getAssets(), "fonts/alegreya-regular.ttf");
        holder.tv.setTypeface(mTypeFace);
        return convertView;
    }

    public static class ViewHolder {
        TextView tv;
    }
}

