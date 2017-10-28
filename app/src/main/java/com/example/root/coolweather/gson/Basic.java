package com.example.root.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 10/28/17.
 */

public class Basic
{
    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public class Update
    {
        @SerializedName("loc")
        public String updateTime;
    }

    public Update update;
}
