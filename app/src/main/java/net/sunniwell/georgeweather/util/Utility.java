package net.sunniwell.georgeweather.util;

import android.util.Log;

import net.sunniwell.georgeweather.db.City;
import net.sunniwell.georgeweather.db.County;
import net.sunniwell.georgeweather.db.Province;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by admin on 17/9/21.
 */

public class Utility {
    public static final String TAG = "jpd-Utility";
    
    /**
     * 将从服务器请求的省份数据保存到数据库
     * @param serverData 服务器数据
     */
    public static boolean handleProvinceData(String serverData) {
        Log.d(TAG, "handleProvinceData: ");
        try {
            JSONArray provinceArray = new JSONArray(serverData);
            for(int i = 0; i < provinceArray.length(); i++) {
                JSONObject provinceObject = provinceArray.getJSONObject(i);
                Province province = new Province();
                province.setPrinceCode(provinceObject.getInt("id"));
                province.setName(provinceObject.getString("name"));
                province.save();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 将从服务器请求的市数据保存到数据库
     * @param serverData 服务器数据
     * @param provinceId 对应上一级的省份ID
     */
    public static boolean handCityData(String serverData, int provinceId) {
        Log.d(TAG, "handCityData: ");
        try {
            JSONArray cityArray = new JSONArray(serverData);
            Log.d(TAG, "handCityData: length:" + cityArray.length());
            for(int i = 0; i < cityArray.length(); i++) {
                JSONObject cityObject = cityArray.getJSONObject(i);
                City city = new City();
                city.setCityCode(cityObject.getInt("id"));
                city.setName(cityObject.getString("name"));
                Log.d(TAG, "handCityData: id:" + cityObject.getInt("id") + ",name:" + cityObject.getString("name"));
                city.setProvinceId(provinceId);
                city.save();
                Log.d(TAG, "handCityData: id--:" + city.getCityCode() + ",name:" + city.getName());
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 将从服务器请求的县数据保存到数据库
     * @param serverData 服务器数据
     * @param cityId 对应上一级的城市ID
     */
    public static boolean handCountyData(String serverData, int cityId) {
        Log.d(TAG, "handCountyData: ");
        try {
            JSONArray cityArray = new JSONArray(serverData);
            for(int i = 0; i < cityArray.length(); i++) {
                JSONObject countyObject = cityArray.getJSONObject(i);
                County county = new County();
                county.setCountyCode(countyObject.getInt("id"));
                county.setName(countyObject.getString("name"));
                county.setWeather_id(countyObject.getString("weather_id"));
                county.setCityId(cityId);
                county.save();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
