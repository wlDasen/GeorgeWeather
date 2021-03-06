package net.sunniwell.georgeweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import net.sunniwell.georgeweather.db.City;
import net.sunniwell.georgeweather.gson.Basic;
import net.sunniwell.georgeweather.gson.DailyForecast;
import net.sunniwell.georgeweather.gson.Weather;
import net.sunniwell.georgeweather.service.AutoUpdateService;
import net.sunniwell.georgeweather.util.HttpUtil;
import net.sunniwell.georgeweather.util.Utility;

import org.w3c.dom.Text;

import java.io.File;
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
    private TextView aqi;
    private TextView pm25;
    private TextView comf;
    private TextView car;
    private TextView sport;
    private ImageView bingPicImg;
    public SwipeRefreshLayout swipe;
    public DrawerLayout drawer;
    private Button navBtn;
    private String weatherId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        obtainWidgetInstance();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String bingPic = prefs.getString("bing_pic", null);
        if (bingPic != null) {
            Log.d(TAG, "onCreate: bingpic use cache.");
            Log.d(TAG, "onCreate: bingpic bingPic:" + bingPic);
            Glide.with(this).load(bingPic).into(bingPicImg);
        } else {
            Log.d(TAG, "onCreate: bingpic by internet.");
            loadBingPic();
        }
        String weatherString = prefs.getString("weather", null);
        if (weatherString != null) {
            Log.d(TAG, "onCreate: use cache.....");
            Weather weather = Utility.handleWeatherResponse(weatherString);
            weatherId = weather.basic.city;
            showWeatherInfo(weather);
        } else {
            Log.d(TAG, "onCreate: by internet...");
            Intent intent = getIntent();
            weatherId = intent.getStringExtra("weather_id");
            requestWeather(weatherId);
        }
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId);
            }
        });
        navBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });
    }

    private void loadBingPic() {
        Log.d(TAG, "loadBingPic: ");
        final String url = "http://cn.bing.com/az/hprichbg/rb/SoyuzReturn_ROW9920762589_1920x1080.jpg";
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
        editor.putString("bing_pic", url);
        editor.apply();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Glide.with(WeatherActivity.this).load(url).into(bingPicImg);
            }
        });
    }

    private void obtainWidgetInstance() {
        titleCity = (TextView)findViewById(R.id.title_city);
        titleTime = (TextView)findViewById(R.id.title_time);
        temperature = (TextView)findViewById(R.id.temperature);
        weatherInfo = (TextView)findViewById(R.id.weather);
        forecast = (LinearLayout)findViewById(R.id.forecast);
        aqi = (TextView)findViewById(R.id.aqi_text);
        pm25 = (TextView)findViewById(R.id.pm25_text);
        comf = (TextView)findViewById(R.id.comf_text);
        car = (TextView)findViewById(R.id.car_text);
        sport = (TextView)findViewById(R.id.sport_text);
        bingPicImg = (ImageView)findViewById(R.id.bing_pic);
        swipe = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        navBtn = (Button)findViewById(R.id.nav_button);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    public void requestWeather(final String weatherId) {
        String requestUrl = "https://free-api.heweather.com/v5/weather?city=" + weatherId + "&key=657f9365f0cd4daf85545a4a91c05aa8";
        HttpUtil.sendRequest(requestUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: ");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气失败", Toast.LENGTH_SHORT).show();
                        swipe.setRefreshing(false);
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
                            Log.d(TAG, "run: responseContent:" + responseContent);
                            editor.apply();
                            showWeatherInfo(weather);
                            Intent intent = new Intent(WeatherActivity.this, AutoUpdateService.class);
                            startService(intent);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气失败", Toast.LENGTH_SHORT).show();
                        }
                        swipe.setRefreshing(false);
                    }
                });
            }
        });
        loadBingPic();
    }

    private void showWeatherInfo(Weather weather) {
        Log.d(TAG, "showWeatherInfo: basic:" + weather.basic);
        String cityName = weather.basic.city;
        weatherId = cityName;
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
        if(weather.aqi != null ) {
            aqi.setText(weather.aqi.city.aqi);
            pm25.setText(weather.aqi.city.pm25);
        }
        if(weather.suggestion != null) {
            String comforStr = "舒适度：" + weather.suggestion.comf.txt;
            String carStr = "汽车指数：" + weather.suggestion.cw.txt;
            String sprotStr = "运动建议：" + weather.suggestion.sport.txt;
            comf.setText(comforStr);
            car.setText(carStr);
            sport.setText(sprotStr);
        }
    }
}