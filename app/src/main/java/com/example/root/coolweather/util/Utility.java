package com.example.root.coolweather.util;

import android.text.TextUtils;

import com.example.root.coolweather.db.City;
import com.example.root.coolweather.db.County;
import com.example.root.coolweather.db.Province;
import com.example.root.coolweather.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by root on 10/26/17.
 */

public class Utility
{
    //解析和处理服务器返回的省级数据
    //解析成功返回true,解析失败返回false
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
                    id	      1
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
                {   /*
                     id	113
                     name	"南京"
                     */
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City city = new City();
                    //CREATE TABLE city (id integer primary key autoincrement,
                    //citycode integer, cityname text, provinceid integer);
                    city.setCityCode(cityObject.getInt("id"));
                    city.setCityName(cityObject.getString("name"));
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

    public static boolean handleCountyResponse(String response,int cityId)
    {
        if (!TextUtils.isEmpty(response))
        {
            try
            {
                JSONArray allCounties = new JSONArray(response);
                for (int i=0; i<allCounties.length(); i++)
                {
                    /*JSON data
                    * id	921
                    name	"南京"
                    weather_id	"CN101190101"
                    */
                    /*
                    * private int id;
                      private String countyName;
                      private String weatherId;
                      private int cityId;
                    * */
                    JSONObject countyObject = allCounties.getJSONObject(i);
                    County     county       = new County();

                    county.setCityId(cityId);
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.save();
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

    //将返回的JSON数据解析成Weather实体类
    public static Weather handleWeatherResponse(String response)
    {
        /*public class Weather
        {
    *       {
    *           "HeWeather":
    *           [
    *               {
    *                   "status":"ok",
    *                   "basic",{},
    *               }
    *           ]
    *       }
    *   }
    * */
        try
        {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray   = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            /*
            * {"name":"Tom","age":20}
              可以定一个一个类，并加入name 和age这两个字段，然后调用以下代码
               Gson gson = new Gson();
               Person person = gson.fromJson(jsonData,Person.class);
            */
            Gson gson = new Gson();
            return gson.fromJson(weatherContent,Weather.class);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
