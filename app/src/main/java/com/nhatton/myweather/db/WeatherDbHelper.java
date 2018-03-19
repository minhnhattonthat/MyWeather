package com.nhatton.myweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.nhatton.myweather.db.WeatherContract.WeatherEntry;

/**
 * Created by nhatton on 12/3/18.
 */

public class WeatherDbHelper extends SQLiteOpenHelper {

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + WeatherEntry.TABLE_NAME + " (" +
                    WeatherEntry._ID + " INTEGER PRIMARY KEY, " +
                    WeatherEntry.COLUMN_NAME_CITY + " TEXT, " +
                    WeatherEntry.COLUMN_NAME_ICON + " TEXT, " +
                    WeatherEntry.COLUMN_NAME_TEMP + " REAL, " +
                    WeatherEntry.COLUMN_NAME_TEMP_MIN + " REAL, " +
                    WeatherEntry.COLUMN_NAME_TEMP_MAX + " REAL, " +
                    WeatherEntry.COLUMN_NAME_TIMESTAMP + " INTEGER)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + WeatherEntry.TABLE_NAME;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Weather.db";

    public WeatherDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
