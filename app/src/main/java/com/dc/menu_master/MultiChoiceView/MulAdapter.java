package com.dc.menu_master.MultiChoiceView;

import android.content.Context;
import android.graphics.Color;
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
 * Adapter for MulActivity.
 */

public class MulAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> list;
    // Control status of checkboxes.
    private static HashMap<Integer, Boolean> isSelected;
    private LayoutInflater inflater = null;

    public MulAdapter(Context context, ArrayList<String> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
        isSelected = new HashMap<Integer, Boolean>();
        initData();
    }

    private void initData() {
        // Initialization.
        for (int i = 0; i < list.size(); i++) {
            getIsSelected().put(i, false);
        }
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
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listview_layout, null);
            holder = new ViewHolder();
            holder.tv = (TextView) convertView.findViewById(R.id.foodName);
            holder.cb = (CheckBox) convertView.findViewById(R.id.checkbox);
            // Set tag for view.
            convertView.setTag(holder);
        } else {
            // Get holder.
            // The Object stored in this view as a tag.
            holder = (ViewHolder) convertView.getTag();
        }
        // Set up textView.
        holder.tv.setTextColor(Color.BLACK);
        holder.tv.setText(list.get(position));
        // Set up checkboxes.
        holder.cb.setChecked(getIsSelected().get(position));
        return convertView;
    }

    public static class ViewHolder {
        TextView tv;
        CheckBox cb;
    }

    public static HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        MulAdapter.isSelected = isSelected;
    }
}
