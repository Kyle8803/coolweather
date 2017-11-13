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
import com.example.root.coolweather.db.County;
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

    private TextView titleText;
    private Button   backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    List<String> dataList = new ArrayList<>();

    //省列表
    private List<Province> provinceList;
    //市列表
    private List<City>     cityList;
    //县列表
    private List<County> countyList;
    //当前选中的级别
    private int currentLevel;

    //选中的省份
    private Province selectedProvince;
    //选中的城市
    private City selectedCity;

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view  = inflater.inflate(R.layout.choose_area,container,false);

        backButton = view.findViewById(R.id.back_button);
        titleText  = view.findViewById(R.id.title_text);
        listView   = view.findViewById(R.id.list_view);

        adapter    = new ArrayAdapter<>
                        (getContext(),
                         android.R.layout.simple_list_item_1,
                         dataList);
        listView.setAdapter(adapter);

        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        //第一步
        queryProvinces();

        backButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (currentLevel == LEVEL_COUNTY)
                {
                    queryCities();
                }
                else if (currentLevel == LEVEL_CITY)
                {
                    queryProvinces();
                }
            }
        });

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
                    selectedCity = cityList.get(position);
                    queryCounties();
                }
                else if (currentLevel == LEVEL_COUNTY)
                {
                    /*
                        [{"id":937,"name":"苏州","weather_id":"CN101190401"}]
                    */
                    //CREATE TABLE county (id integer primary key autoincrement,
                    //cityid integer, countyname text, weatherid text);
                    String weatherId = countyList.get(position).getWeatherId();

                    if (getActivity() instanceof MainActivity)
                    {
                        Intent intent = new Intent(getActivity(), WeatherActivity.class);
                        intent.putExtra("weather_id", weatherId);
                        startActivity(intent);
                        getActivity().finish();
                    }
                    else if (getActivity() instanceof WeatherActivity)
                    {
                        WeatherActivity activity = (WeatherActivity) getActivity();
                        activity.drawerLayout.closeDrawers();
                        activity.swipeRefreshLayout.setRefreshing(true);
                        activity.requestWeather(weatherId);
                    }
                }
            }
        });
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
                                Toast.LENGTH_SHORT).show();
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
                else if ("county".equals(type))
                {
                    result = Utility.handleCountyResponse(responseText,selectedCity.getId());
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
                            else if ("county".equals(type))
                            {
                                queryCounties();
                            }
                        }
                    };
                    getActivity().runOnUiThread(runnable);
                }
            }
        };

        HttpUtil.sendOkHttpRequest(address,callback);
    }

    //查询全国所有省份，优先从数据库查询，如果没有查询到再去服务器上查询
    private void queryProvinces()
    {
        //显示省份的时候，不能继续返回了
        backButton.setVisibility(View.GONE);
        titleText.setText("中国");

        ////CREATE TABLE province (id integer primary key autoincrement,
        // provincecode integer, provincename text);
        //select * from province;
        //findAll()方法的返回值是一个Province类型的List集合
        provinceList = DataSupport.findAll(Province.class);

        //从服务器读取到省份数据后，size才大于0
        if (provinceList.size() > 0)
        {
            dataList.clear();

            for (Province province : provinceList)
            {
                String province_name = province.getProvinceName();
                dataList.add(province_name);
            }
            adapter.notifyDataSetChanged();

            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        }
        //一开始没有数据，所以provinceList.size=0
        else
        {
            String address = "http://guolin.tech/api/china";
            queryFromServer(address,"province");
        }
    }

    //查询选中省内所有市，优先从数据库查询，如果没有，再到服务器查询
    private void queryCities()
    {
        backButton.setVisibility(View.VISIBLE);
        titleText.setText(selectedProvince.getProvinceName());

        //CREATE TABLE city (id integer primary key autoincrement,
        //citycode integer, cityname text, provinceid integer);
        //where()方法用于指定查询的约束条件，对应了SQL当中的where关键字
        //Select * from city where provinceId = ?

        String province_id = String.valueOf(selectedProvince.getId());
        cityList = DataSupport
                .where("provinceid = ?", province_id)
                .find(City.class);
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

    //查询选中市内所有的县，优先从数据库查询
    private  void queryCounties()
    {
        backButton.setVisibility(View.VISIBLE);
        titleText.setText(selectedCity.getCityName());

        //CREATE TABLE county (id integer primary key autoincrement,
        //cityid integer, countyname text, weatherid text);
        //select * from county where cityid = ?
        String city_id = String.valueOf(selectedCity.getId());
        countyList = DataSupport
                .where("cityid = ?", city_id)
                .find(County.class);
        if (countyList.size()>0)
        {
            dataList.clear();
            for (County county : countyList)
            {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        }
        else
        {
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode     = selectedCity.getCityCode();
            String address   = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            queryFromServer(address, "county");
        }
    }

}
