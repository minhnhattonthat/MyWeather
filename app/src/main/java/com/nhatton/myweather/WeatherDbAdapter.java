package com.nhatton.myweather;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.nhatton.myweather.WeatherContract.WeatherEntry;

/**
 * Created by nhatton on 12/3/18.
 */

public class WeatherDbAdapter {

    private WeatherDbHelper mDbHelper;
    private SQLiteDatabase mDb;
    private Context mContext;

    public WeatherDbAdapter(Context context) {
        mContext = context;
    }

    WeatherDbAdapter open() throws SQLException {
        mDbHelper = new WeatherDbHelper(mContext);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    void close() {
        mDbHelper.close();
    }

    long createWeather(WeatherModel model) {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(WeatherEntry.COLUMN_NAME_CITY, model.getName());
        values.put(WeatherEntry.COLUMN_NAME_TIMESTAMP, model.getDt());

        // Insert the new row, returning the primary key value of the new row
        return mDb.insert(WeatherEntry.TABLE_NAME, null, values);
    }

    boolean deleteWeather(String cityName) {

        // Define 'where' part of query.
        String selection = WeatherEntry.COLUMN_NAME_CITY + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = {cityName};
        // Issue SQL statement.
        return mDb.delete(WeatherEntry.TABLE_NAME, selection, selectionArgs) > 0;
    }

}
