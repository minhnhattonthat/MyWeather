package com.nhatton.myweather;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
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

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(WeatherHolder holder, int position) {
        holder.mItem = mData.get(position);
        if (holder.mItem != null) {
            holder.mCityTextView.setText(holder.mItem.getName());
            loadIcon(holder.mItem.getWeather().get(0).getIcon(), holder.mWeatherIcon);
            holder.mTempTextView.setText(String.format("%dºC", holder.mItem.getMain().getRoundedTemp()));
            holder.mMinTempTextView.setText(String.format("%dºC", holder.mItem.getMain().getRoundedTempMin()));
            holder.mMaxTempTextView.setText(String.format("%dºC", holder.mItem.getMain().getRoundedTempMax()));
            holder.mUpdatedTimeTextView.setText(String.format("Updated at: %s", holder.mItem.getDtInString()));
        }
    }

    private void loadIcon(String iconName, ImageView imageView) {
        String path = Environment.getExternalStorageDirectory().toString();
        File file = new File(path, iconName + ".png");
        if(file.exists()) {
            Bitmap icon = BitmapFactory.decodeFile(file.getPath());
            imageView.setImageBitmap(icon);
        }
    }

    public void updateRow(int position, WeatherModel data) {
        mData.set(position, data);
        notifyItemChanged(position);
    }

    public void updateRowIcon(int position) {
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        return mCount;
    }

    class WeatherHolder extends RecyclerView.ViewHolder {
        final View mView;
        final ImageView mWeatherIcon;
        final TextView mCityTextView;
        final TextView mTempTextView;
        final TextView mMinTempTextView;
        final TextView mMaxTempTextView;
        final TextView mUpdatedTimeTextView;
        WeatherModel mItem;

        WeatherHolder(View view) {
            super(view);
            mView = view;
            mWeatherIcon = view.findViewById(R.id.weather_icon);
            mCityTextView = view.findViewById(R.id.city_text);
            mTempTextView = view.findViewById(R.id.temp_text);
            mMinTempTextView = view.findViewById(R.id.min_temp_text);
            mMaxTempTextView = view.findViewById(R.id.max_temp_text);
            mUpdatedTimeTextView = view.findViewById(R.id.updated_time_text);
        }
    }

}
