package com.nhatton.myweather.ui.citylist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.nhatton.myweather.R;

import java.util.ArrayList;

import static com.nhatton.myweather.Cities.CITIES;

/**
 * Created by nhatton on 11/3/18.
 */

public class CityListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> selectedCities;
    private ArrayList<String> newSelectedCities;

    public CityListAdapter(Context context, ArrayList<String> selectedCities) {
        mContext = context;
        this.selectedCities = selectedCities;
        newSelectedCities = new ArrayList<>();
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
            holder.root = convertView;
            holder.cityText = convertView.findViewById(R.id.city_text);
            holder.checkBox = convertView.findViewById(R.id.city_checkbox);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.checkBox.isChecked()) {
                    holder.checkBox.setChecked(false);
                    newSelectedCities.remove(getItem(position));
                } else {
                    holder.checkBox.setChecked(true);
                    newSelectedCities.add(getItem(position));
                }
            }
        });

        holder.cityText.setText(getItem(position));
        if (selectedCities.contains(getItem(position))) {
            holder.checkBox.setEnabled(false);
            holder.checkBox.setChecked(true);
            holder.root.setClickable(false);
            holder.root.setFocusable(false);
        } else if (newSelectedCities.contains(getItem(position))) {
            holder.checkBox.setChecked(true);
        } else {
            holder.checkBox.setEnabled(true);
            holder.checkBox.setChecked(false);
        }

        return convertView;
    }

    private static class ViewHolder {
        View root;
        TextView cityText;
        CheckBox checkBox;
    }

    public ArrayList<String> getNewCheckedList() {
        return newSelectedCities;
    }
}
