package com.example.root.coolweather.util;

import android.text.TextUtils;

import com.example.root.coolweather.db.City;
import com.example.root.coolweather.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by root on 10/26/17.
 */

public class Utility
{
    //解析和处理服务器返回的省级数据
    public static boolean handleProvinceResponse(String response)
    {
        //判断response是不是为空
        if (!TextUtils.isEmpty(response))
        {
            try
            {
                JSONArray allProvinces = new JSONArray(response);
                for (int i=0; i<allProvinces.length(); i++)
                {
                    JSONObject provinceObject = allProvinces.getJSONObject(i);

                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    //完成数据添加操作
                    province.save();
                    /*
                    id	  1
                    name	"北京"

                    id	      2
                    name	"上海"

                    id	      3
                    name	"天津"
                    */
                }
                return true;
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return false;
    }

    //解析和处理服务器返回的市级数据
    public static boolean handleCityResponse(String response,int provinceId)
    {
        if (!TextUtils.isEmpty(response))
        {
            try
            {
                JSONArray allCities = new JSONArray(response);

                for (int i=0; i<allCities.length();i++)
                {
                    JSONObject cityObject = allCities.getJSONObject(i);

                    City city = new City();

                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));

                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return false;
    }
}
