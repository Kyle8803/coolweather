package com.example.root.coolweather;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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
    private ScrollView weatherLayout;
    private TextView   titleCity;
    private TextView   titleUpdateTime;

    private TextView degreeText;
    private TextView weatherInfoText;

    private LinearLayout forecastLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        //初始化各种控件
        weatherLayout   =(ScrollView) findViewById(R.id.weather_layout);
        titleCity       =(TextView)   findViewById(R.id.title_city);
        titleUpdateTime =(TextView)   findViewById(R.id.title_update_time);

        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);

        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);

        SharedPreferences sharedPreferences
                =PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = sharedPreferences.getString("weather",null);
        if (weatherString != null)
        {
            //有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            showWeatherInfo(weather);
        }
        else
        {
            //无缓存时去服务器查询天气
            String weatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }
    }

    //根据天气id请求天气信息
    public void requestWeather(final String weatherId)
    {
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
                            /*SharedPreferences.Editor editor =
                                    PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this)
                                            .edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            */
                            showWeatherInfo(weather);
                        }
                        else
                        {
                            Toast.makeText(
                                    WeatherActivity.this,
                                    "获取天气信息失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                };
                //将当前线程切换到主线程
                runOnUiThread(runnable);
            }
        };
        HttpUtil.sendOkHttpRequest(weatherUrl,callback);
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
            forecastLayout.addView(view);
        }

        weatherLayout.setVisibility(View.VISIBLE);
    }
}
