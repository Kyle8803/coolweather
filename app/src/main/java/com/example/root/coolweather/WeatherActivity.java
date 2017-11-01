package com.example.root.coolweather;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.root.coolweather.gson.Forecast;
import com.example.root.coolweather.gson.Weather;
import com.example.root.coolweather.util.HttpUtil;
import com.example.root.coolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity
{
    private ImageView bingPicImg;

    public SwipeRefreshLayout swipeRefreshLayout;
    private String mWeatherId;

    private ScrollView weatherLayout;
    private TextView   titleCity;
    private TextView   titleUpdateTime;

    private TextView degreeText;
    private TextView weatherInfoText;

    private LinearLayout forecastLayout;

    private TextView aqiText;
    private TextView pm25Text;

    public DrawerLayout drawerLayout;
    private Button navButton;

    //初始化控件
    private void initialize_control()
    {
        bingPicImg      = (ImageView) findViewById(R.id.bing_pic_img);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        weatherLayout   = (ScrollView) findViewById(R.id.weather_layout);
        titleCity       = (TextView)   findViewById(R.id.title_city);
        titleUpdateTime = (TextView)   findViewById(R.id.title_update_time);

        degreeText      = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);

        forecastLayout  = (LinearLayout) findViewById(R.id.forecast_layout);

        aqiText         = (TextView) findViewById(R.id.aqi_text);
        pm25Text        = (TextView) findViewById(R.id.pm25_text);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navButton = (Button) findViewById(R.id.nav_button);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        //初始化各种控件
        initialize_control();

        if (Build.VERSION.SDK_INT >= 21)
        {
            //设置活动的布局会显示再状态栏上面
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            //将状态栏设置成透明色
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        SharedPreferences sharedPreferences
                =PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = sharedPreferences.getString("weather",null);

        String bingPic = sharedPreferences.getString("bing_pic",null);
        if (bingPic != null)
        {
            Glide.with(this).load(bingPic).into(bingPicImg);
        }
        else
        {
            loadBingPic();
        }

        if (weatherString != null)
        {
            //有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            mWeatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        }
        else
        {
            //无缓存时去服务器查询天气
            //一开始是weatherId
            //String weatherId = getIntent().getStringExtra("weather_id");

            mWeatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(mWeatherId);
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                requestWeather(mWeatherId);
            }
        });

        navButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    //根据天气id请求天气信息
    public void requestWeather(final String weatherId)
    {
        loadBingPic();
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId
                + "&key=369ecd6913c641be9e741f177408fddf";
        Callback callback = new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                Runnable runnable = new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText(
                                WeatherActivity.this,
                                "获取天气信息失败",
                                Toast.LENGTH_SHORT
                        ).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                };
                runOnUiThread(runnable);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final String responseText = response.body().string();
                //将返回的JSON数据转换成Weather对象
                final Weather weather = Utility.handleWeatherResponse(responseText);
                Runnable runnable = new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (weather != null && "ok".equals(weather.status))
                        {
                            SharedPreferences.Editor editor =
                                    PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this)
                                            .edit();
                            editor.putString("weather",responseText);
                            editor.apply();

                            mWeatherId = weather.basic.weatherId;
                            showWeatherInfo(weather);
                        }
                        else
                        {
                            Toast.makeText(
                                    WeatherActivity.this,
                                    "获取天气信息失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                };
                //将当前线程切换到主线程
                runOnUiThread(runnable);
            }
        };
        HttpUtil.sendOkHttpRequest(weatherUrl,callback);
    }

    //加载必应每日一图
    private void loadBingPic()
    {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        Callback callback = new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor =
                        PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                Runnable runnable = new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                };
                runOnUiThread(runnable);
            }
        };
        HttpUtil.sendOkHttpRequest(requestBingPic,callback);
    }

    //处理并展示Weather实体类中的数据
    private void showWeatherInfo(Weather weather)
    {
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];

        String degree=weather.now.more.info;
        String weatherInfo = weather.now.temperature + "℃";

        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);

        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);

        forecastLayout.removeAllViews();
        for (Forecast forecast:weather.forecastList)
        {
            View view = LayoutInflater.from(this).inflate(
                    R.layout.forecast_item,
                    forecastLayout,
                    false
            );
            TextView dateText = view.findViewById(R.id.date_text);
            dateText.setText(forecast.date);

            TextView infoText = view.findViewById(R.id.info_text);
            infoText.setText(forecast.more.info);

            TextView maxText = view.findViewById(R.id.max_text);
            maxText.setText(forecast.temperature.max);

            TextView minText = view.findViewById(R.id.min_text);
            minText.setText(forecast.temperature.min);

            forecastLayout.addView(view);

            if (weather.aqi != null)
            {
                aqiText.setText(weather.aqi.city.aqi);
                pm25Text.setText(weather.aqi.city.pm25);
            }
        }

        weatherLayout.setVisibility(View.VISIBLE);
    }
}
