package com.example.root.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by root on 10/28/17.
 */

public class City extends DataSupport
{
    //在数据库里的变量名都是小写的!
    //CREATE TABLE city (id integer primary key autoincrement,
    //citycode integer, cityname text, provinceid integer);

    private int id;
    private String cityName;
    private int cityCode;
    private int provinceId;

    public void setId(int id)
    {
        this.id = id;
    }
    public int getId()
    {
        return id;
    }


    public void setCityName(String cityName)
    {
        this.cityName = cityName;
    }
    public String getCityName()
    {
        return cityName;
    }


    public void setCityCode(int cityCode)
    {
        this.cityCode = cityCode;
    }
    public int getCityCode()
    {
        return cityCode;
    }


    public void setProvinceId(int provinceId)
    {
        this.provinceId = provinceId;
    }
    public int getProvinceId()
    {
        return provinceId;
    }
}
