package net.sunniwell.georgeweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.sunniwell.georgeweather.db.City;
import net.sunniwell.georgeweather.gson.Basic;
import net.sunniwell.georgeweather.gson.DailyForecast;
import net.sunniwell.georgeweather.gson.Weather;
import net.sunniwell.georgeweather.util.HttpUtil;
import net.sunniwell.georgeweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    public static final String TAG = "jpd-WeatherActivity";
    private TextView titleCity;
    private TextView titleTime;
    private TextView temperature;
    private TextView weatherInfo;
    private LinearLayout forecast;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        obtainWidgetInstance();
        Intent intent = getIntent();
        String weatherId = intent.getStringExtra("weather_id");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
//        if (weatherString != null) {
//            Weather weather = Utility.handleWeatherResponse(weatherString);
//            showWeatherInfo(weather);
//        } else {
            requestWeather(weatherId);
//        }
    }

    private void obtainWidgetInstance() {
        titleCity = (TextView)findViewById(R.id.title_city);
        titleTime = (TextView)findViewById(R.id.title_time);
        temperature = (TextView)findViewById(R.id.temperature);
        weatherInfo = (TextView)findViewById(R.id.weather);
        forecast = (LinearLayout)findViewById(R.id.forecast);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    private void requestWeather(final String weatherId) {
        String requestUrl = "https://free-api.heweather.com/v5/weather?city=" + weatherId + "&key=657f9365f0cd4daf85545a4a91c05aa8";
        HttpUtil.sendRequest(requestUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: ");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "onResponse: ");
                final String responseContent = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseContent);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equalsIgnoreCase(weather.status)) {
                            Log.d(TAG, "run: ........");
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", responseContent);
                            editor.apply();
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void showWeatherInfo(Weather weather) {
        if(weather.basic == null) {
            Log.d(TAG, "showWeatherInfo: null");
        } else {
            Log.d(TAG, "showWeatherInfo: not null");
        }
        Log.d(TAG, "showWeatherInfo: basic:" + weather.basic);
        String cityName = weather.basic.city;
        String updateTime = weather.basic.update.loc.split(" ")[1];
        String degree = weather.now.tmp + "℃";
        String weatherText = weather.now.cond.txt;

        titleCity.setText(cityName);
        titleTime.setText(updateTime);
        temperature.setText(degree);
        weatherInfo.setText(weatherText);

        forecast.removeAllViews();
        for(DailyForecast df : weather.dailyForecasts) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecast, false);
            TextView date = (TextView)view.findViewById(R.id.forecast_date);
            TextView info = (TextView)view.findViewById(R.id.forecast_info);
            TextView max = (TextView)view.findViewById(R.id.forecast_max);
            TextView min = (TextView)view.findViewById(R.id.forecast_min);
            date.setText(df.date);
            info.setText(df.cond.txt_d);
            max.setText(df.temperature.max);
            min.setText(df.temperature.min);
            forecast.addView(view);
        }
    }
}