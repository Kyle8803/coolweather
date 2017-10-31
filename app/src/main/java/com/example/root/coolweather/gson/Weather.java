package com.example.root.coolweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by root on 10/28/17.
 */

public class Weather
{
    /*
    * {
    *   "HeWeather":
    *   [
    *       {
    *           "status":"ok",
    *           "basic",{},
    *       }
    *   ]
    * }
    * */
    public String status;
    public Basic basic;
    public Now   now;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
    public AQI   aqi;
}
