package com.example.root.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by root on 10/26/17.
 */

public class Province extends DataSupport
{
    //CREATE TABLE province (id integer primary key autoincrement,
    // provincecode integer, provincename text);

    private int    id;
    private String provinceName;
    private int    provinceCode;

    public void setId(int id)
    {
        this.id = id;
    }
    public int getId()
    {
        return id;
    }

    public void setProvinceName(String provinceName)
    {
        this.provinceName = provinceName;
    }
    public String getProvinceName()
    {
        return provinceName;
    }


    public void setProvinceCode(int provinceCode)
    {
        this.provinceCode = provinceCode;
    }
    public int getProvinceCode()
    {
        return provinceCode;
    }
}
