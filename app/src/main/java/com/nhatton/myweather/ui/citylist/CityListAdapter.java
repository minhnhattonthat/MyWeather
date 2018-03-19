package com.nhatton.myweather.ui.citylist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.nhatton.myweather.R;

import java.util.ArrayList;

import static com.nhatton.myweather.Cities.CITIES;

/**
 * Created by nhatton on 11/3/18.
 */

public class CityListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> mSelected;

    public CityListAdapter(Context context, ArrayList<String> selectedCities) {
        mContext = context;
        mSelected = selectedCities;
    }

    @Override
    public int getCount() {
        return CITIES.length;
    }

    @Override
    public String getItem(int position) {
        return CITIES[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.row_city, parent, false);
            holder = new ViewHolder();
            holder.cityText = convertView.findViewById(R.id.city_text);
            holder.checkBox = convertView.findViewById(R.id.city_checkbox);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.cityText.setText(getItem(position));
        if (mSelected.contains(getItem(position))) {
            holder.checkBox.setChecked(true);
        } else {
            holder.checkBox.setChecked(false);
        }
        holder.checkBox.setTag(position);

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(((int)buttonView.getTag()) == position) {
                    if (isChecked) {
                        if (!mSelected.contains(getItem((int) holder.checkBox.getTag())))
                            mSelected.add(getItem((int) holder.checkBox.getTag()));
                    } else {
                        mSelected.remove(getItem((int) holder.checkBox.getTag()));
                    }
                }

            }
        });
        return convertView;
    }

    private static class ViewHolder {
        TextView cityText;
        CheckBox checkBox;
    }

    public ArrayList<String> getCheckedList() {
        return mSelected;
    }
}
