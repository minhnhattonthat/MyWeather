package com.nhatton.myweather;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by nhatton on 16/3/18.
 */

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherHolder> {

    private int mCount;
    private ArrayList<WeatherModel> mData;

    public WeatherAdapter(int count) {
        mCount = count;
        mData = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            mData.add(null);
        }
    }

    @Override
    public WeatherHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_weather, parent, false);
        return new WeatherHolder(view);
    }

    @Override
    public void onBindViewHolder(WeatherHolder holder, int position) {
        holder.mItem = mData.get(position);
        if (holder.mItem != null) {
            holder.mCityTextView.setText(holder.mItem.getName());
        }
    }

    public void updateRow(int position, WeatherModel data) {
        mData.set(position, data);
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        return mCount;
    }

    class WeatherHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView mCityTextView;
        WeatherModel mItem;

        public WeatherHolder(View view) {
            super(view);
            mView = view;
            mCityTextView = view.findViewById(R.id.city_text);
        }
    }

}
