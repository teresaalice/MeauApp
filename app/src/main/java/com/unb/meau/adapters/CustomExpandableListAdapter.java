package com.unb.meau.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.unb.meau.R;

import java.util.LinkedHashMap;
import java.util.List;

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> listTitle;
    private LinkedHashMap<String, List<String>> listItem;

    public CustomExpandableListAdapter(Context context, List<String> listTitle, LinkedHashMap<String, List<String>> listItem) {
        this.context = context;
        this.listTitle = listTitle;
        this.listItem = listItem;
    }

    @Override
    public int getGroupCount() {
        return listTitle.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listItem.get(listTitle.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listTitle.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listItem.get(listTitle.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.nav_list_group, parent, false);
        }

        String title = (String) getGroup(groupPosition);
        TextView text_title = convertView.findViewById(R.id.expandable_list_group);
        text_title.setText(title);

        ImageView group_indicator = convertView.findViewById(R.id.expandable_list_group_indicator);
        group_indicator.setSelected(isExpanded);

        ImageView icon = convertView.findViewById(R.id.expandable_list_group_icon);

        switch (groupPosition) {
            case 0:
                icon.setVisibility(View.GONE);
                float scale = context.getResources().getDisplayMetrics().density;
                text_title.setPadding((int) (16 * scale), (int) (16 * scale), (int) (40 * scale), (int) (20 * scale));
                text_title.setBackgroundColor(context.getResources().getColor(R.color.nav_primary));
                break;
            case 1:
                icon.setImageResource(R.drawable.ic_pets_black_24dp);
                text_title.setBackgroundColor(context.getResources().getColor(R.color.nav_atalhos));
                break;
            case 2:
                icon.setImageResource(R.drawable.ic_info_black_24dp);
                text_title.setBackgroundColor(context.getResources().getColor(R.color.nav_informacoes));
                break;
            case 3:
                icon.setImageResource(R.drawable.ic_settings_black_24dp);
                text_title.setBackgroundColor(context.getResources().getColor(R.color.nav_configuracoes));
                break;
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String title = (String) getChild(groupPosition, childPosition);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.nav_list_item, parent, false);
        }
        TextView text_item = convertView.findViewById(R.id.expandable_list_item);
        text_item.setText(title);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
