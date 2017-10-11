package net.sunniwell.georgeweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by admin on 2017/10/10.
 */

public class AQI {
    public City city;
    public class City {
        public String aqi;
        public String pm25;
    }
}
