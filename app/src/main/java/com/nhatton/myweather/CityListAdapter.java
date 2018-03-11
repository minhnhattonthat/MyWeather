package com.nhatton.myweather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import static com.nhatton.myweather.Cities.CITIES;

/**
 * Created by nhatton on 11/3/18.
 */

public class CityListAdapter extends BaseAdapter {

    private Context mContext;

    public CityListAdapter(Context context){
        mContext = context;
    }

    @Override
    public int getCount() {
        return CITIES.length;
    }

    @Override
    public Object getItem(int position) {
        return CITIES[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.row_city, parent, false);
            holder = new ViewHolder();
            holder.cityText = convertView.findViewById(R.id.city_text);
            holder.checkBox = convertView.findViewById(R.id.city_checkbox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.cityText.setText((String) getItem(position));
        if (true) {
            holder.checkBox.setChecked(true);
        } else {
            holder.checkBox.setChecked(false);
        }
        holder.checkBox.setTag(position);
        return convertView;
    }

    private static class ViewHolder {
        TextView cityText;
        CheckBox checkBox;
    }
}
