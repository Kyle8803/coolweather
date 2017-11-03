package com.example.root.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by root on 10/30/17.
 */

public class County extends DataSupport
{
    /*
    [{"id":937,"name":"苏州","weather_id":"CN101190401"}]
    */
    //CREATE TABLE county (id integer primary key autoincrement,
    //cityid integer, countyname text, weatherid text);

    private int id;
    private String countyName;
    private String weatherId;
    private int cityId;

    public void setCityId(int cityId)
    {
        this.cityId = cityId;
    }
    public int getCityId()
    {
        return cityId;
    }

    public void setId(int id)
    {
        this.id = id;
    }
    public int getId()
    {
        return id;
    }

    public void setCountyName(String countyName)
    {
        this.countyName = countyName;
    }
    public String getCountyName()
    {
        return countyName;
    }

    public void setWeatherId(String weatherId)
    {
        this.weatherId = weatherId;
    }
    public String getWeatherId()
    {
        return weatherId;
    }
}
