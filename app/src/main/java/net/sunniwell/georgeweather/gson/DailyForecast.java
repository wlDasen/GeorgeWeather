package net.sunniwell.georgeweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by admin on 2017/10/10.
 */

public class DailyForecast {
    public Astro astro;
    public Cond cond;
    public String date;
    public String hum;
    public String pcpn;
    public String pop;
    public String pres;
    @SerializedName("tmp")
    public Temperature temperature;
    public String uv;
    public String vis;
    public Wind wind;
    public class Wind {
        public String deg;
        public String dir;
        public String sc;
        public String spd;
    }
    public class Temperature {
        public String max;
        public String min;
    }
    public class Astro {
        public String mr;
        public String ms;
        public String sr;
        public String ss;
    }
    public class Cond {
        public String code_d;
        public String code_n;
        public String txt_d;
        public String txt_n;
    }
}
