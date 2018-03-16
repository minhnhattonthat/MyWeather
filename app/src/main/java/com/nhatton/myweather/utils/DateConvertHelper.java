package com.nhatton.myweather.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by nhatton on 16/3/18.
 */

public class DateConvertHelper {

    private static final SimpleDateFormat formatterForClient = new SimpleDateFormat("HH:mm", Locale.US);

    public static String convertToDate(long timeStamp){
        return formatterForClient.format(new Date(timeStamp));
    }
}
