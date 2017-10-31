package com.example.root.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 10/31/17.
 */

public class Forecast
{
    /*
    * "daily_forecast":
    * [
    *   {
    *       "date":"2016-08-08",
    *       "cond":
    *       {
    *           "txt_d":"阵雨"
    *       },
    *       "tmp":
    *       {
    *           "max":"34",
    *           "min":"27"
    *       }
    *   },
    *   {
    *       "date":"2016-08-09",
    *       "cond":
    *       {
    *           "txt_d":"阵雨"
    *       },
    *       "tmp":
    *       {
    *           "max":"34",
    *           "min":"27"
    *       }
    *   },
    *   ....
    * ]
    * */
    public String date;

    @SerializedName("cond")
    public More more;
    public class More
    {
        @SerializedName("txt_d")
        public String info;
    }

    @SerializedName("tmp")
    public Temperature temperature;
    public class Temperature
    {
        public String max;
        public String min;
    }
}
