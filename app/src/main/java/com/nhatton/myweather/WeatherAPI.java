package com.nhatton.myweather;

/**
 * Created by nhatton on 11/3/18.
 */

public class WeatherAPI {

    private static final String API_KEY = "1e7f27cf98fac44b02286ba9e69c4173";

    private static final String ROOT_URL = "http://api.openweathermap.org/data/2.5/weather";

    private static final String IMG_ROOT = "http://openweathermap.org/img/w/";

    public static String getWeatherByCity(String cityName) {
        String url = String.format("%s?q=%s&appid=%s", ROOT_URL, cityName, API_KEY);
        RESTClient client = new RESTClient();
        return client.get(url);
    }

}
