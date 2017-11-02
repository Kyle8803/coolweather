package com.example.root.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.example.root.coolweather.gson.Weather;
import com.example.root.coolweather.util.HttpUtil;
import com.example.root.coolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service
{
    public AutoUpdateService()
    {

    }

    public int onStartCommand(Intent intent,int flags,int startId)
    {
        updateWeather();
        updateBingPic();

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        //这是8小时的毫秒数
        int anHour = 8 * 60 * 60 * 1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;

        Intent i = new Intent(this,AutoUpdateService.class);
        /*
        * flags可以有四个取值，分别是FLAG_ONE_SHOT, FLAG_NO_CREATE,
         * FLAG_CANCEL_CURRENT, FLAG_UPDATE_CURRENT。
         * 这四种取值每一种都有特定的意义。当然，也可以不使用上述四种常量中的任意一种，
         * 即不为PendingIntent指定flags，此时只需要传入一个数字0即可
         */
        PendingIntent pendingIntent = PendingIntent.getService(this,0,i,0);

        //让旧的PendingIntent先cancel()掉，这样得到的pendingIntent就是最新的了
        manager.cancel(pendingIntent);

        /*
        *闹钟类型
        这个闹钟类型就是前面setxxx()方法第一个参数int type.

        AlarmManager.ELAPSED_REALTIME：使用相对时间，可以通过SystemClock.elapsedRealtime()
        获取（从开机到现在的毫秒数，包括手机的睡眠时间），设备休眠时并不会唤醒设备。
        AlarmManager.ELAPSED_REALTIME_WAKEUP：与ELAPSED_REALTIME基本功能一样，
        只是会在设备休眠时唤醒设备。
        AlarmManager.RTC：使用绝对时间，可以通过 System.currentTimeMillis()获取，
        设备休眠时并不会唤醒设备。
        AlarmManager.RTC_WAKEUP: 与RTC基本功能一样，只是会在设备休眠时唤醒设备。
        */
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pendingIntent);

        return super.onStartCommand(intent,flags, startId);
    }

    //更新天气信息
    private void updateWeather()
    {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = sharedPreferences.getString("weather",null);
        if (weatherString != null)
        {
            //有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            String weatherId = weather.basic.weatherId;

            String weatherUrl = "http://guolin.tech/api/weather?cityid="
                    + weatherId + "&key=369ecd6913c641be9e741f177408fddf";

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
                    String responseText = response.body().string();
                    Weather weather = Utility.handleWeatherResponse(responseText);
                    if (weather != null && "ok".equals(weather.status))
                    {
                        SharedPreferences.Editor editor =
                                PreferenceManager.getDefaultSharedPreferences(
                                        AutoUpdateService.this).edit();
                        editor.putString("weather",responseText);
                        editor.apply();
                    }
                }
            };

            HttpUtil.sendOkHttpRequest(weatherUrl,callback);
        }
    }

    //更新必应每日一图
    private void updateBingPic()
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
                String bingPicture = response.body().string();
                SharedPreferences.Editor editor =
                        PreferenceManager.getDefaultSharedPreferences(
                                AutoUpdateService.this).edit();
                editor.putString("bing_pic",bingPicture);
                editor.apply();
            }
        };

        HttpUtil.sendOkHttpRequest(requestBingPic,callback);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
