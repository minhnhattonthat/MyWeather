package com.nhatton.myweather;

/**
 * Created by nhatton on 11/3/18.
 */

public class WeatherAPI {

    private static final String API_KEY = "a8e56c2943b3f8d56f4c4a4def6245f8";

    private static final String ROOT_URL = "http://api.openweathermap.org/data/2.5/weather";

    public static final String IMG_ROOT = "http://openweathermap.org/img/w/";

    public static String getWeatherByCity(String cityName) {
        String url = String.format("%s?q=%s&appid=%s&units=metric", ROOT_URL, cityName, API_KEY);
        RESTClient client = new RESTClient();
        return client.get(url);
    }

}
