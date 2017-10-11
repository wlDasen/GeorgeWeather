package net.sunniwell.georgeweather;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.sunniwell.georgeweather.db.City;
import net.sunniwell.georgeweather.db.County;
import net.sunniwell.georgeweather.db.Province;
import net.sunniwell.georgeweather.util.HttpUtil;
import net.sunniwell.georgeweather.util.Utility;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by admin on 2017/9/21.
 */

public class ChooseAreaFragment extends Fragment {
    static final int PROVINCE_LEVEL = 1;
    static final int CITY_LEVEL = 2;
    static final int COUNTY_LEVEL = 3;
    public static final String TAG = "jpd-ChooseAreaFragment";
    private Button backBtn;
    private TextView barTitle;
    ProgressDialog progress;
    private ListView areaList;
    private ArrayAdapter<String> arrayAdapter;
    /**
     * 选中的城市
     */
    private City selectedCity;
    /**
     * 选中的省份
     */
    private Province selectedProvince;
    /**
     * 当前所处的List层级
     */
    private int currentLevel;
    /**
     * listView数据
     */
    private List<String> dataList = new ArrayList<>();
    /**
     * Province数据
     */
    private List<Province> provinceList;
    /**
     * City数据
     */
    private List<City> cityList;
    /**
     * County数据
     */
    private List<County> countyList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        View view = LayoutInflater.from(getContext()).inflate(R.layout.choose_area_frag, container, false);
        backBtn = (Button)view.findViewById(R.id.back_button);
        barTitle = (TextView)view.findViewById(R.id.bar_title);
        areaList = (ListView)view.findViewById(R.id.area_list);
        backBtn.setVisibility(View.INVISIBLE);
        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, dataList);
        areaList.setAdapter(arrayAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: ");
        areaList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: position:" + position + "id:" + id);
                if (currentLevel == PROVINCE_LEVEL) {
                    selectedProvince = provinceList.get(position);
//                    Log.d(TAG, "onItemClick: id:" + selectedProvince.getId() + ",code:" + selectedProvince.getPrinceCode());
                    queryCities();
                } else if(currentLevel == CITY_LEVEL) {
                    selectedCity = cityList.get(position);
//                    Log.d(TAG, "onItemClick: id:" + selectedCity.getCityCode() + ",name:" + selectedCity.getName());
                    queryCounties();
                } else if(currentLevel == COUNTY_LEVEL) {
                    String weatherId = countyList.get(position).getName();
                    if (getActivity() instanceof MainActivity) {
                        Intent intent = new Intent(getContext(), WeatherActivity.class);
                        intent.putExtra("weather_id", weatherId);
                        Log.d(TAG, "onItemClick: countyName:" + countyList.get(position).getName());
                        startActivity(intent);
                        getActivity().finish();
                    }
                    if (getActivity() instanceof WeatherActivity) {
                        WeatherActivity weatherActivity = (WeatherActivity)getActivity();
                        weatherActivity.drawer.closeDrawers();
                        weatherActivity.swipe.setRefreshing(true);
                        weatherActivity.requestWeather(weatherId);
                    }
                }
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onBackButtonClick: ");
                if (currentLevel == COUNTY_LEVEL) {
                    queryCities();
                } else if(currentLevel == CITY_LEVEL) {
                    queryProvinces();
                }
            }
        });
        queryProvinces();
    }

    /**
     * 查询全国所有的省份，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryProvinces() {
        Log.d(TAG, "queryProvinces: ");
        barTitle.setText("中国");
        backBtn.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0) {
            dataList.clear();
            for(Province province : provinceList) {
                dataList.add(province.getName());
//                Log.d(TAG, "queryProvinces: id:" + province.getId() + ",provinceCode:" + province.getPrinceCode()
//                    + ",name:" + province.getName());
            }
            arrayAdapter.notifyDataSetChanged();
            areaList.setSelection(0);
            currentLevel = PROVINCE_LEVEL;
        } else {
            String url = "http://guolin.tech/api/china";
            queryFromServer(url, "province");
        }
    }

    /**
     * 查询国家对应的所有城市，优先从数据查询，如果没有查询到再去服务器上查询
     */
    private void queryCities() {
        Log.d(TAG, "queryCities: ");
        backBtn.setVisibility(View.VISIBLE);
        barTitle.setText(selectedProvince.getName());
        cityList = DataSupport.where("provinceid = ?", String.valueOf(selectedProvince.getPrinceCode())).find(City.class);
        if (cityList.size() > 0) {
            dataList.clear();
            for(City city : cityList) {
                dataList.add(city.getName());
//                Log.d(TAG, "queryCities: id:" + city.getId() + ",cityCode:" + city.getCityCode()
//                    + ",name:" + city.getName());
            }
            arrayAdapter.notifyDataSetChanged();
            areaList.setSelection(0);
            currentLevel = CITY_LEVEL;
        } else {
            String url = "http://guolin.tech/api/china/" + selectedProvince.getPrinceCode();
            queryFromServer(url, "city");
        }
    }

    /**
     * 查询城市对应所有的县，优先从数据查询，如果没有查询到再去服务器上查询
     */
    private void queryCounties() {
        Log.d(TAG, "queryCounties: ");
        backBtn.setVisibility(View.VISIBLE);
        barTitle.setText(selectedCity.getName());
        countyList = DataSupport.where("cityid = ?", String.valueOf(selectedCity.getCityCode())).find(County.class);
        if (countyList.size() > 0) {
            dataList.clear();
            for(County county : countyList) {
                dataList.add(county.getName());
//                Log.d(TAG, "queryCounties: id:" + county.getId() + ",countyCode:" + county.getCountyCode()
//                    + ",name:" + county.getName() + ",weather_id:" + county.getWeather_id());
            }
            arrayAdapter.notifyDataSetChanged();
            areaList.setSelection(0);
            currentLevel = COUNTY_LEVEL;
        } else {
            String url = "http://guolin.tech/api/china/" + selectedProvince.getPrinceCode() + "/" + selectedCity.getCityCode();
            queryFromServer(url, "county");
        }
    }

    /**
     * 向服务器请求数据
     * @param url 服务器地址
     */
    private void queryFromServer(String url, final String type) {
        showProgressDialog();
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(), "加载数据失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String serverData = response.body().string();
                boolean result = false;
                if ("province".equalsIgnoreCase(type)) {
                    result = Utility.handleProvinceData(serverData);
                } else if ("city".equalsIgnoreCase(type)) {
                    result = Utility.handCityData(serverData, selectedProvince.getPrinceCode());
                } else if ("county".equalsIgnoreCase(type)) {
                    result = Utility.handCountyData(serverData, selectedCity.getCityCode());
                }
                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equalsIgnoreCase(type)) {
                                queryProvinces();
                            }
                            if("city".equalsIgnoreCase(type)) {
                                queryCities();
                            }
                            if("county".equalsIgnoreCase(type)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }
        };
        HttpUtil.sendRequest(url, callback);
    }

    /**
     * 展示请求数据的缓冲图标
     */
    private void showProgressDialog() {
        Log.d(TAG, "showProgressDialog: ");
        if (progress == null) {
            Log.d(TAG, "showProgressDialog: null");
            progress = new ProgressDialog(getContext());
            progress.setMessage("请求数据中....");
            progress.setCanceledOnTouchOutside(false);
            progress.show();
        }
    }

    /**
     * 关闭请求数据的缓冲图标
     */
    private void closeProgressDialog() {
        Log.d(TAG, "closeProgressDialog: ");
        if(progress != null) {
            Log.d(TAG, "closeProgressDialog: not null");
            progress.dismiss();
            progress = null;
        }
    }
}