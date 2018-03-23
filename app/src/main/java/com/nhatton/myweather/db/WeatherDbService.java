package com.nhatton.myweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.nhatton.myweather.db.WeatherContract.WeatherEntry;
import com.nhatton.myweather.db.model.WeatherDomain;

import java.util.ArrayList;

import static com.nhatton.myweather.db.WeatherContract.WeatherEntry.COLUMN_NAME_CITY;
import static com.nhatton.myweather.db.WeatherContract.WeatherEntry.COLUMN_NAME_ICON;
import static com.nhatton.myweather.db.WeatherContract.WeatherEntry.COLUMN_NAME_TEMP;
import static com.nhatton.myweather.db.WeatherContract.WeatherEntry.COLUMN_NAME_TEMP_MAX;
import static com.nhatton.myweather.db.WeatherContract.WeatherEntry.COLUMN_NAME_TEMP_MIN;
import static com.nhatton.myweather.db.WeatherContract.WeatherEntry.COLUMN_NAME_TIMESTAMP;
import static com.nhatton.myweather.db.WeatherContract.WeatherEntry.TABLE_NAME;

/**
 * Created by nhatton on 12/3/18.
 */

public class WeatherDbService {

    private WeatherDbHelper mDbHelper;
    private SQLiteDatabase mDb;
    private Context mContext;

    public WeatherDbService(Context context) {
        mContext = context;
    }

    public WeatherDbService open() throws SQLException {
        mDbHelper = new WeatherDbHelper(mContext);
        mDb = mDbHelper.getWritableDatabase();

        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public long insertWeather(WeatherDomain weather) {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();

        values.put(WeatherEntry.COLUMN_NAME_CITY, weather.getCity());
        values.put(WeatherEntry.COLUMN_NAME_ICON, weather.getIcon());
        values.put(WeatherEntry.COLUMN_NAME_TEMP, weather.getTemp());
        values.put(WeatherEntry.COLUMN_NAME_TEMP_MIN, weather.getTempMin());
        values.put(WeatherEntry.COLUMN_NAME_TEMP_MAX, weather.getTempMax());
        values.put(WeatherEntry.COLUMN_NAME_TIMESTAMP, weather.getTimeStamp());

        // Insert the new row, returning the primary key value of the new row
        return mDb.insert(TABLE_NAME, null, values);
    }

    public int updateWeather(WeatherDomain weather) {
        ContentValues values = new ContentValues();

        values.put(WeatherEntry.COLUMN_NAME_ICON, weather.getIcon());
        values.put(WeatherEntry.COLUMN_NAME_TEMP, weather.getTemp());
        values.put(WeatherEntry.COLUMN_NAME_TEMP_MIN, weather.getTempMin());
        values.put(WeatherEntry.COLUMN_NAME_TEMP_MAX, weather.getTempMax());
        values.put(WeatherEntry.COLUMN_NAME_TIMESTAMP, weather.getTimeStamp());

        return mDb.update(TABLE_NAME, values, COLUMN_NAME_CITY + " =?",
                new String[]{weather.getCity()});
    }

    public boolean deleteWeather(String cityName) {

        // Define 'where' part of query.
        String selection = WeatherEntry.COLUMN_NAME_CITY + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = {cityName};
        // Issue SQL statement.
        return mDb.delete(TABLE_NAME, selection, selectionArgs) > 0;
    }

    public ArrayList<WeatherDomain> getAllWeather() {
        ArrayList<WeatherDomain> result = new ArrayList<>();

        Cursor c = mDb.query(TABLE_NAME, null, null, null, null, null, null);

        while (c.moveToNext()) {
            WeatherDomain item = new WeatherDomain();

            item.setCity(c.getString(c.getColumnIndex(COLUMN_NAME_CITY)));
            item.setIcon(c.getString(c.getColumnIndex(COLUMN_NAME_ICON)));
            item.setTemp(c.getDouble(c.getColumnIndex(COLUMN_NAME_TEMP)));
            item.setTempMin(c.getDouble(c.getColumnIndex(COLUMN_NAME_TEMP_MIN)));
            item.setTempMax(c.getDouble(c.getColumnIndex(COLUMN_NAME_TEMP_MAX)));
            item.setTimeStamp(c.getLong(c.getColumnIndex(COLUMN_NAME_TIMESTAMP)));

            result.add(item);
        }
        c.close();

        return result;
    }

    public int getDataSize() {
        Cursor c = mDb.rawQuery("select COUNT(*) from " + TABLE_NAME, null);
        c.moveToNext();
        int count = c.getInt(0);
        c.close();

        return count;
    }

    public void dropTable() {
        mDb.delete(TABLE_NAME, null, null);
    }

    public ArrayList<String> getCityList() {
        Cursor c = mDb.rawQuery("select " + COLUMN_NAME_CITY + " from " + TABLE_NAME, null);
        ArrayList<String> cities = new ArrayList<>();
        while (c.moveToNext()){
            cities.add(c.getString(0));
        }
        return cities;
    }
}
