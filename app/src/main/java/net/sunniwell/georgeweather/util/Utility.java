package net.sunniwell.georgeweather.util;

import android.util.Log;

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
                province.setId(provinceObject.getInt("id"));
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
     */
    public static boolean handCityData(String serverData) {
        return true;
    }

    /**
     * 将从服务器请求的县数据保存到数据库
     * @param serverData 服务器数据
     */
    public static boolean handCountyData(String serverData) {
        return true;
    }
}
