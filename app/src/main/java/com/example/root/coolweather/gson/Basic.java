package com.example.root.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 10/28/17.
 */

public class Basic
{
    /*
    * "basic"
    * {
    *   "city":"苏州",
    *   "id":"CN101190401",
    *   "update":
    *   {
    *       "loc":"2016-08-08 21:58"
    *   }
    * }
    * */
    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public class Update
    {
        //loc表示天气的更新时间
        @SerializedName("loc")
        public String updateTime;
    }

    public Update update;
}
