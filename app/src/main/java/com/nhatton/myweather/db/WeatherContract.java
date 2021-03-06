package com.nhatton.myweather.db;

import android.provider.BaseColumns;

/**
 * Created by nhatton on 12/3/18.
 */

public class WeatherContract {

    private WeatherContract(){

    }

    /* Inner class that defines the table contents */
    public static class WeatherEntry implements BaseColumns {
        public static final String TABLE_NAME = "weather";
        public static final String COLUMN_NAME_CITY = "city";
        public static final String COLUMN_NAME_ICON = "icon";
        public static final String COLUMN_NAME_TEMP = "temp";
        public static final String COLUMN_NAME_TEMP_MAX = "temp_max";
        public static final String COLUMN_NAME_TEMP_MIN = "temp_min";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
    }

}
