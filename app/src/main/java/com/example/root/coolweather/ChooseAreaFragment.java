package com.example.root.coolweather;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.coolweather.db.City;
import com.example.root.coolweather.db.Province;
import com.example.root.coolweather.util.HttpUtil;
import com.example.root.coolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by root on 10/26/17.
 */

public class ChooseAreaFragment extends Fragment
{
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    ListView listView;
    ArrayAdapter<String> adapter;
    List<String> dataList = new ArrayList<>();

    //省列表
    List<Province> provinceList;
    //市列表
    List<City> cityList;

    //当前选中的级别
    int currentLevel;

    //选中的省份
    private Province selectedProvince;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.choose_area,container,false);

        listView   = view.findViewById(R.id.list_view);

        adapter = new ArrayAdapter<>
                (getContext(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        //第一步
        queryProvinces();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if (currentLevel == LEVEL_PROVINCE)
                {
                    selectedProvince = provinceList.get(position);
                    queryCities();
                }
                else if (currentLevel == LEVEL_CITY)
                {

                }
                else if (currentLevel == LEVEL_COUNTY)
                {
                    String weatherId="";
                    Intent intent = new Intent(getActivity(),WeatherActivity.class);
                    intent.putExtra("weather_id",weatherId);
                    startActivity(intent);
                    getActivity();
                }
            }
        });
    }

    //查询全国所有省份，优先从数据库查询，如果没有查询到再去服务器上查询
    private void queryProvinces()
    {
        provinceList = DataSupport.findAll(Province.class);
        //一开始没有数据，所以provinceList.size=0
        if (provinceList.size() > 0)
        {
            dataList.clear();

            for (Province province : provinceList)
            {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();

            listView.setSelection(0);
            //currentLevel = LEVEL_PROVINCE;
        }
        else
        {
            String address = "http://guolin.tech/api/china";
            queryFromServer(address,"province");
        }
    }

    //查询选中省内所有市，优先从数据库查询，如果没有，再到服务器查询
    private void queryCities()
    {
        //where()方法用于指定查询的约束条件，对应了SQL当中的where关键字
        cityList = DataSupport.where("provinceId = ?",
                String.valueOf(selectedProvince.getId())).find(City.class);
        if (cityList.size()>0)
        {
            dataList.clear();
            for (City city : cityList)
            {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        }
        else
        {
            int provinceCode = selectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromServer(address, "city");
        }
    }

    //根据传入的地址和类型从服务器上查询省市县数据
    private void queryFromServer(String address,final String type)
    {
        Callback callback = new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                //通过runOnUiThread()方法回到主线程处理逻辑
                Runnable runnable = new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //closeProgressDialog();
                        Toast.makeText(
                                getContext(),
                                "加载失败",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                };
                getActivity().runOnUiThread(runnable);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                String responseText = response.body().string();
                boolean result = false;
                if ("province".equals(type))
                {
                    result = Utility.handleProvinceResponse(responseText);
                }
                else if ("city".equals(type))
                {
                    result = Utility.handleCityResponse(responseText,selectedProvince.getId());
                }

                if (result)
                {
                    Runnable runnable = new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            //closeProgressDialog();
                            if ("province".equals(type))
                            {
                                queryProvinces();
                            }
                            else if ("city".equals(type))
                            {
                                queryCities();
                            }
                        }
                    };
                    getActivity().runOnUiThread(runnable);
                }
            }
        };

        HttpUtil.sendOkHttpRequest(address,callback);
    }
}
