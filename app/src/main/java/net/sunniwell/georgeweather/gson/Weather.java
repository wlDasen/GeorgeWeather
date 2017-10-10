package net.sunniwell.georgeweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by admin on 2017/10/10.
 */

public class Weather {
    public AQI aqi;
    public Basic basic;
    @SerializedName("daily_forecast")
    public List<DailyForecast> dailyForecasts;
    @SerializedName("hourly_forecast")
    public List<HourForecast> hourForecasts;
    public Now now;
    public String status;
    public Suggestion suggestion;
}
