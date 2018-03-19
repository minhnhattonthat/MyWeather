package com.nhatton.myweather.db.model;

import com.nhatton.myweather.network.model.WeatherModel;
import com.nhatton.myweather.utils.DateConvertHelper;

/**
 * Created by nhatton on 18/3/18.
 */

public class WeatherDomain {

    private String city;
    private String icon;
    private double temp;
    private double tempMin;
    private double tempMax;
    private long timeStamp;

    public WeatherDomain() {

    }

    public WeatherDomain(WeatherModel weatherModel) {
        city = weatherModel.getName();
        icon = weatherModel.getWeather().get(0).getIcon();
        temp = weatherModel.getMain().getTemp();
        tempMin = weatherModel.getMain().getTempMin();
        tempMax = weatherModel.getMain().getTempMax();
        timeStamp = weatherModel.getDt();
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public double getTempMin() {
        return tempMin;
    }

    public void setTempMin(double tempMin) {
        this.tempMin = tempMin;
    }

    public double getTempMax() {
        return tempMax;
    }

    public void setTempMax(double tempMax) {
        this.tempMax = tempMax;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getRoundedTemp() {
        return (int) Math.round(temp);
    }

    public int getRoundedTempMin() {
        return (int) Math.round(tempMin);
    }

    public int getRoundedTempMax() {
        return (int) Math.round(tempMax);
    }

    public String getTimeStampInString() {
        return DateConvertHelper.convertToDate(timeStamp);
    }
}
