package com.example.root.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by root on 10/28/17.
 */

public class City extends DataSupport
{
    private int id;
    public void setId(int id)
    {
        this.id = id;
    }
    public int getId()
    {
        return id;
    }

    private String cityName;
    public void setCityName(String cityName)
    {
        this.cityName = cityName;
    }
    public String getCityName()
    {
        return cityName;
    }

    private int cityCode;
    public void setCityCode(int cityCode)
    {
        this.cityCode = cityCode;
    }
    public int getCityCode()
    {
        return cityCode;
    }

    private int provinceId;
    public void setProvinceId(int provinceId)
    {
        this.provinceId = provinceId;
    }
    public int getProvinceId()
    {
        return provinceId;
    }
}
