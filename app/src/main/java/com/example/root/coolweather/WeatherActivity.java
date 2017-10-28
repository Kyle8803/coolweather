package com.example.root.coolweather;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.root.coolweather.util.HttpUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        String weatherId = getIntent().getStringExtra("weather_id");
        requestWeather(weatherId);
    }

    //根据天气id请求天气信息
    public void requestWeather(final String weatherId)
    {
        String weatherUrl = "" + weatherId + "";
        Callback callback = new Callback() {
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
                
            }
        };
        HttpUtil.sendOkHttpRequest(weatherUrl,callback);
    }
}
