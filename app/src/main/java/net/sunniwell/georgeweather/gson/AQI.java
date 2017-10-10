package net.sunniwell.georgeweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by admin on 2017/10/10.
 */

public class AQI {
    public AQICity aqiCity;
    public class AQICity {
        public String aqi;
        public String co;
        public String no2;
        public String o3;
        public String pm10;
        public String pm25;
        @SerializedName("qlty")
        public String quality;
        public String so2;
    }
}
